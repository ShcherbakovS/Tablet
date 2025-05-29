package com.cpstablet.tablet.service;

import com.cpstablet.tablet.entity.PNRSystem;
import com.cpstablet.tablet.entity.Photo;
import com.cpstablet.tablet.entity.SubObject;
import com.cpstablet.tablet.repository.PhotoRepo;
import com.cpstablet.tablet.repository.SubObjectRepo;
import com.cpstablet.tablet.repository.SystemRepo;
import lombok.AllArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.poi.hpsf.Thumbnail;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FileService {
    private final SubObjectRepo subObjectRepo;
    private final SystemRepo systemRepo;
    private final PhotoRepo photoRepo;


    // загрузка структуры ОКС (подобъекты, системы)
    public void uploadStructure(MultipartFile file, String CCSCode) throws IOException {


        ZipSecureFile.setMinInflateRatio(0);

        Workbook workbook = new XSSFWorkbook(file.getInputStream());

        Sheet sheet = workbook.getSheetAt(0);

        Row checkRow = sheet.getRow(9);

        DataFormatter df = new DataFormatter();


        if (!checkDocument(checkRow)) {

            throw new RuntimeException("Наименования заголовков не соответствуют шаблону");

        } else {
            if(!systemRepo.getAllByCCSNumber(CCSCode).isEmpty()) {
                subObjectRepo.deleteAllByCCSCode(CCSCode);
                systemRepo.deleteAllByCCSCode(CCSCode);
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
                    systemCreate(sheet.getRow(i), CCSCode);
                }
            }


        }
    }

    private void subObjectCreate(Row row, String CCSCode) {

        //переписать проверку наименования подобьекта

        Map<String, String> checkKONumber = subObjectRepo.findByCCSCode(CCSCode).stream()
                .collect(Collectors.toMap(s-> s.getSubObjectName(), s-> s.getNumberKO()));

        DataFormatter df = new DataFormatter();

        if (!checkKONumber.containsKey(df.formatCellValue(row.getCell(1)))) {


            SubObject subObject = SubObject.builder().
                    subObjectName(df.formatCellValue(row.getCell(1))).
                    numberKO(df.formatCellValue(row.getCell(5))).
                    CCSCode(CCSCode).
                    status(" ").
                    PNRSystems(new ArrayList<>()).
                    build();

            subObjectRepo.save(subObject);
        }
    }

    private PNRSystem systemCreate(Row row, String CCSCode) {

        DataFormatter df = new DataFormatter();

        SubObject subObject = subObjectRepo.findBYCCSCodeAndSubObjectName(CCSCode,df.formatCellValue(row.getCell(1)));

        System.out.println(subObject.getSubObjectName() + " ИМЯ ПОДОБЪЕКТА");

        if (row.getCell(0) != null) {

            subObject.getPNRSystems().add(PNRSystem.builder().
                    PNRSystemName(df.formatCellValue(row.getCell(CellReference.convertColStringToIndex("C")))).
                    PNRSystemRD(df.formatCellValue(row.getCell(CellReference.convertColStringToIndex("D")))).
                    PNRSystemII(df.formatCellValue(row.getCell(CellReference.convertColStringToIndex("E")))).
                    PNRSystemKO(df.formatCellValue(row.getCell(CellReference.convertColStringToIndex("F")))).
                    CCSNumber(CCSCode).
                    PNRSystemStatus(" ").
                    PNRPlanDate(" ").
                    PNRFactDate(" ").
                    IIPlanDate(" ").
                    IIFactDate(" ").
                    KOPlanDate(" ").
                    KOFactDate(" ").
                    CIWExecutor("Не определён").
                    CWExecutor("Не определён").
                    build());
            subObject.getPNRSystems().stream().map(s-> s.getPNRSystemII()).forEach(System.out::println);

            subObjectRepo.save(subObject);
        }
        return null;
    }

    public void uploadPhotos(MultipartFile file, Long id) throws IOException {

        System.out.println("Размер исходного файла " + file.getSize());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

// Сжимаем полученный файл
        Thumbnails.of(file.getInputStream())
                .size(800, 600)  // TODO: проверить на фронте размерность и сжатие после загрузки необходимость корректировки
                .outputFormat("jpg")
                .outputQuality(0.7)
                .useExifOrientation(false)  // Игнорировать EXIF ориентацию
                .keepAspectRatio(true)
                .toOutputStream(baos);


        Photo toSave = photoRepo.save(Photo.builder().
                fileName(file.getName()).
                contentType(file.getContentType()).
                size((long)baos.size()).
                bytes(baos.toByteArray()).
                commentId(id).
                build());
        System.out.println("Размер сохраненного фото " + toSave.getSize());
    }

    public Photo getPhotosByCommentId(Long id) {

        Photo toSend = photoRepo.getPhotoByCommentId(id);

        System.out.println(toSend.getSize());

//        return  ResponseEntity.ok().
//                header("fileName").contentType(MediaType.IMAGE_JPEG).
//                contentLength(photo.getSize()).body(
//                        new InputStreamResource(new ByteArrayInputStream(photo.getBytes()))
//                );
        return toSend;
    }

    public HttpStatus deletePhoto(Long id) {

        photoRepo.deleteById(id);

        return HttpStatus.OK;

    }

    private boolean checkDocument(Row checkRow) {

        List<String> strings = List.of("Поз. по ГП", "Объекты по ГП", "Системы", "Шифр РД", "Номер акта ИИ", "Номер акта КО");

        List<String> strings2 = List.of(
                checkRow.getCell(0).getStringCellValue().replaceAll("[\\r\\n]", ""),
                checkRow.getCell(1).getStringCellValue().replaceAll("[\\r\\n]", ""),
                checkRow.getCell(2).getStringCellValue().replaceAll("[\\r\\n]", ""),
                checkRow.getCell(3).getStringCellValue().replaceAll("[\\r\\n]", ""),
                checkRow.getCell(4).getStringCellValue().replaceAll("[\\r\\n]", ""),
                checkRow.getCell(5).getStringCellValue().replaceAll("[\\r\\n]", ""));

        if (!strings.equals(strings2)) {

            return false;
        }

        return true;
    }
}



