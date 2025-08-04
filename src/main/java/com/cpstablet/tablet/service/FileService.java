package com.cpstablet.tablet.service;

import com.cpstablet.tablet.entity.*;
import com.cpstablet.tablet.entity.Comment;
import com.cpstablet.tablet.repository.*;
import jakarta.persistence.EntityNotFoundException;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;

import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class FileService {
    private final SubObjectRepo subObjectRepo;
    private final SystemRepo systemRepo;
    private final CommentRepo commentRepo;
    private final DefectiveActRepo defectiveActRepo;
    private final SystemService systemService;
    private final SubObjectService subObjectService;

    public Photo addPhotoToComment(Long id, MultipartFile file) throws IOException {

        Comment comment = commentRepo.findCommentByCommentId(id);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        Thumbnails.of(file.getInputStream())
                .size(800, 600)
                .outputFormat("jpg")
                .outputQuality(0.7)
                .useExifOrientation(false)
                .keepAspectRatio(true)
                .toOutputStream(baos);

        Photo photo = Photo.builder().fileName(file.getName()).contentType(file.getContentType())
                .size((long) baos.size()).bytes(baos.toByteArray()).build();

        System.out.println(photo.getContentType() + " Расширение файла");

        System.out.println(photo.getSize() + " Размер фото");

        comment.setPhoto(photo);
        commentRepo.save(comment);
        return comment.getPhoto();
    }

    public void removePhotoFromComment(Long id) {

        Comment comment = commentRepo.findByCommentId(id).orElseThrow(() -> new EntityNotFoundException());

        comment.setPhoto(null);
        commentRepo.save(comment);
    }
    public byte[] getPhotoFromComment(Long id) {
        Comment comment = commentRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Замечание не найдено"));
        if(comment.getPhoto() != null){
            throw  new RuntimeException("В замечании нет фото");
        }
            return comment.getPhoto().getBytes();


    }

    public Photo addPhotoToDefectiveAct(Long id, MultipartFile photo) throws IOException {

        DefectiveAct defAct = defectiveActRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Дефектный акт не найден"));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        Thumbnails.of(photo.getInputStream())
                .size(800, 600)
                .outputFormat("jpg")
                .outputQuality(0.7)
                .useExifOrientation(false)
                .keepAspectRatio(true)
                .toOutputStream(baos);

        defAct.setPhoto(Photo.builder().fileName(photo.getName()).contentType(photo.getContentType())
                .size((long) baos.size()).bytes(baos.toByteArray()).build());
        defectiveActRepo.save(defAct);
        return defAct.getPhoto();
    }

    public byte[] getPhotoFromDefectiveAct(Long id) {
        DefectiveAct defAct = defectiveActRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Дефектный акт не найден"));

        return defAct.getPhoto().getBytes();
    }

    public void removePhotoFromDefectiveAct(Long id) {
        DefectiveAct defAct = defectiveActRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Дефектный акт не найден"));

        defAct.setPhoto(null);
        defectiveActRepo.save(defAct);
    }

    // загрузка структуры ОКС (подобъекты, системы)
    public void uploadStructure(MultipartFile file, String CCSCode) throws IOException {

        ZipSecureFile.setMinInflateRatio(0);

        Workbook workbook = new XSSFWorkbook(file.getInputStream());

        FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();

        Sheet sheet = workbook.getSheetAt(0);

        Row checkRow = sheet.getRow(9);

        DataFormatter df = new DataFormatter();

        if (!checkDocument(checkRow)) {
            workbook.close();
            throw new RuntimeException("Наименования заголовков не соответствуют шаблону");

        } else {
            if (!systemRepo.getAllByCCSNumber(CCSCode).isEmpty()) {

                List<SubObject> subObjects = subObjectRepo.findAllByCCSCode(CCSCode);

                // Удаляем SubObject, это автоматически удалит связанные PNRSystem
                // благодаря cascade = CascadeType.ALL и orphanRemoval = true
                subObjectRepo.deleteAll(subObjects);

            }
            System.out.println(sheet.getLastRowNum() + " индекс последней строки");

            for (int i = 10; i <= sheet.getLastRowNum(); i++) {
                if (sheet.getRow(i) != null && sheet.getRow(i).getCell(4) != null &&
                        !df.formatCellValue(sheet.getRow(i).getCell(4)).equals("")) {
                    subObjectCreate(sheet.getRow(i), CCSCode);
                }
            }
            for (int i = 10; i <= sheet.getLastRowNum(); i++) {
                if (sheet.getRow(i) != null && sheet.getRow(i).getCell(4) != null &&
                        !df.formatCellValue(sheet.getRow(i).getCell(4)).equals("")) {
                    systemCreate(sheet.getRow(i), CCSCode, evaluator);
                }
            }

        }
        workbook.close();
    }

    private void subObjectCreate(Row row, String CCSCode) {

        // переписать проверку наименования подобьекта

        Map<String, String> checkKONumber = subObjectRepo.findByCCSCode(CCSCode).stream()
                .collect(Collectors.toMap(s -> s.getSubObjectName(), s -> s.getNumberKO()));

        DataFormatter df = new DataFormatter();

        if (!checkKONumber.containsKey(df.formatCellValue(row.getCell(1)))) {

            SubObject subObject = SubObject.builder().subObjectName(df.formatCellValue(row.getCell(1)))
                    .numberKO(df.formatCellValue(row.getCell(5))).CCSCode(CCSCode).status(" ")
                    .PNRSystems(new ArrayList<>()).build();

            subObjectRepo.save(subObject);
        }
    }

    private PNRSystem systemCreate(Row row, String CCSCode, FormulaEvaluator evaluator) {
        DataFormatter df = new DataFormatter();

        SubObject subObjectTo = subObjectRepo.findBYCCSCodeAndSubObjectName(CCSCode,
                df.formatCellValue(row.getCell(1)));

        if (row.getCell(0) != null) {
            PNRSystem system = PNRSystem.builder()
                    .PNRSystemName(getCellValue(row.getCell(CellReference.convertColStringToIndex("C")), evaluator))
                    .PNRSystemRD(getCellValue(row.getCell(CellReference.convertColStringToIndex("D")), evaluator))
                    .PNRSystemII(getCellValue(row.getCell(CellReference.convertColStringToIndex("E")), evaluator))
                    .PNRSystemKO(getCellValue(row.getCell(CellReference.convertColStringToIndex("F")), evaluator))
                    .CCSNumber(CCSCode)
                    .PNRPlanDate(getCellValue(row.getCell(CellReference.convertColStringToIndex("G")), evaluator))
                    .PNRFactDate(getCellValue(row.getCell(CellReference.convertColStringToIndex("H")), evaluator))
                    .IIPlanDate(getCellValue(row.getCell(CellReference.convertColStringToIndex("I")), evaluator))
                    .IIFactDate(getCellValue(row.getCell(CellReference.convertColStringToIndex("J")), evaluator))
                    .KOPlanDate(getCellValue(row.getCell(CellReference.convertColStringToIndex("K")), evaluator))
                    .KOFactDate(getCellValue(row.getCell(CellReference.convertColStringToIndex("L")), evaluator))
                    .CIWExecutor(getCellValue(row.getCell(CellReference.convertColStringToIndex("M")), evaluator))
                    .CWExecutor(getCellValue(row.getCell(CellReference.convertColStringToIndex("N")), evaluator))
                    .subObject(subObjectTo)
                    .build();

            system.setPNRSystemStatus(systemService.getSystemStatus(system));

            PNRSystem savedSystem = systemRepo.save(system);

            subObjectTo.getPNRSystems().add(savedSystem);
            subObjectRepo.save(subObjectTo);

            if (savedSystem.getPNRSystemStatus().contains(" КО ")) {
                systemRepo.getAllByCCSNumber(savedSystem.getCCSNumber()).stream()
                        .filter(sub -> sub.getPNRSystemKO().equals(savedSystem.getPNRSystemKO()))
                        .forEach(s -> {
                            s.setKOPlanDate(savedSystem.getKOPlanDate());
                            s.setKOFactDate(savedSystem.getKOFactDate());
                            s.setPNRSystemStatus(savedSystem.getPNRSystemStatus());
                            systemRepo.save(s);
                        });
            }

            subObjectService.checkStatus(savedSystem.getPNRSystemId());

            return savedSystem;
        }
        return null;
    }

    private boolean checkDocument(Row checkRow) {

        List<String> strings = List.of("Поз. по ГП", "Объекты по ГП", "Системы", "Шифр РД", "Номер акта ИИ",
                "Номер акта КО", "План в ПНР", "Факт в ПНР", "План ИИ", "Факт ИИ", "План КО", "Факт КО",
                "Исполнитель СМР", "Исполнитель ПНР");

        List<String> strings2 = List.of(
                checkRow.getCell(0).getStringCellValue().replaceAll("[\\r\\n]", ""),
                checkRow.getCell(1).getStringCellValue().replaceAll("[\\r\\n]", ""),
                checkRow.getCell(2).getStringCellValue().replaceAll("[\\r\\n]", ""),
                checkRow.getCell(3).getStringCellValue().replaceAll("[\\r\\n]", ""),
                checkRow.getCell(4).getStringCellValue().replaceAll("[\\r\\n]", ""),
                checkRow.getCell(5).getStringCellValue().replaceAll("[\\r\\n]", ""),
                checkRow.getCell(6).getStringCellValue().replaceAll("[\\r\\n]", ""),
                checkRow.getCell(7).getStringCellValue().replaceAll("[\\r\\n]", ""),
                checkRow.getCell(8).getStringCellValue().replaceAll("[\\r\\n]", ""),
                checkRow.getCell(9).getStringCellValue().replaceAll("[\\r\\n]", ""),
                checkRow.getCell(10).getStringCellValue().replaceAll("[\\r\\n]", ""),
                checkRow.getCell(11).getStringCellValue().replaceAll("[\\r\\n]", ""),
                checkRow.getCell(12).getStringCellValue().replaceAll("[\\r\\n]", ""),
                checkRow.getCell(13).getStringCellValue().replaceAll("[\\r\\n]", ""));

        if (!strings.equals(strings2)) {

            return false;
        }
        return true;
    }

    private String getCellValue(Cell cell, FormulaEvaluator evaluator) {

        DataFormatter formatter = new DataFormatter();

        if (cell == null) {
            return " ";
        }

        if (cell.getCellType().equals(CellType.FORMULA)) {
            if (cell.getCellFormula() == null) {
                return " ";
            }
            String result;

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
            result = dateFormat.format(cell.getDateCellValue());
            return result;

        } else if (cell.getCellType().equals(CellType.STRING)) {
            if (cell.getStringCellValue() == null) {
                return " ";
            }
            System.out.println(cell.getStringCellValue() + " ЗАПИСЬ СТРОКА");

            return cell.getStringCellValue();
        } else if (cell.getCellType().equals(CellType.NUMERIC) && DateUtil.isCellDateFormatted(cell)) {

            if (cell.getNumericCellValue() == 0) {
                return " ";
            }

            java.util.Date date = cell.getDateCellValue();
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
            String dateStr = sdf.format(date);
            return dateStr;

        } else if (cell.getCellType().equals(CellType.NUMERIC)) {
            return formatter.formatCellValue(cell);
        }
        return " ";
    }


}
