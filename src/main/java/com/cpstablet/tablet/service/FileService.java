package com.cpstablet.tablet.service;

import com.cpstablet.tablet.entity.PNRSystem;
import com.cpstablet.tablet.entity.Photo;
import com.cpstablet.tablet.entity.SubObject;
import com.cpstablet.tablet.repository.PhotoRepo;
import com.cpstablet.tablet.repository.SubObjectRepo;
import com.cpstablet.tablet.repository.SystemRepo;
import lombok.AllArgsConstructor;
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
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.List;
import java.util.Map;
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

        List<String> strings = List.of("Поз. по ГП", "Объекты по ГП", "Системы", "Шифр РД", "Номер акта ИИ", "Номер акта КО" );

        List<String> strings2 = List.of(
                checkRow.getCell(0).getStringCellValue().replaceAll("[\\r\\n]", ""),
                checkRow.getCell(1).getStringCellValue().replaceAll("[\\r\\n]", ""),
                checkRow.getCell(2).getStringCellValue().replaceAll("[\\r\\n]", ""),
                checkRow.getCell(3).getStringCellValue().replaceAll("[\\r\\n]", ""),
                checkRow.getCell(4).getStringCellValue().replaceAll("[\\r\\n]", ""),
                checkRow.getCell(5).getStringCellValue().replaceAll("[\\r\\n]", ""));

        strings.stream().forEach(s-> System.out.println(s));
        strings2.stream().forEach(s-> System.out.println(s));

        if (!strings.equals(strings2)) {

            throw new RuntimeException("Наименования заголовков не соответствуют шаблону " + strings);

        } else if(systemRepo.getAllByCCSNumber(CCSCode).isEmpty()) {

            for (int i = 10; i <= sheet.getLastRowNum(); i++) {
                subObjectCreate(sheet.getRow(i), CCSCode);
            }

            for (int i = 10; i <= sheet.getLastRowNum(); i++) {
                systemCreate(sheet.getRow(i), CCSCode);
            }
        } else {

            throw  new RuntimeException("Структура данного объекта уже загружена");
        }
    }
    private void subObjectCreate(Row row, String CCSCode) {

        List<String> checkKONumber = subObjectRepo.findByCCSCode(CCSCode).stream().map(SubObject::getNumberKO).collect(Collectors.toList());

        DataFormatter df = new DataFormatter();

        if(!checkKONumber.contains(df.formatCellValue(row.getCell(5)))) {
            subObjectRepo.save(SubObject.builder().
                    subObjectName(df.formatCellValue(row.getCell(1))).
                    numberKO(df.formatCellValue(row.getCell(5))).
                    CCSCode(CCSCode).
                    status(" ").
                    build());
        }
    }
    private void systemCreate(Row row, String CCSCode) {

        DataFormatter df = new DataFormatter();

        if(row.getCell(0) != null) {
            systemRepo.save(PNRSystem.builder().
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
        }
    }
    public void uploadPhotos(MultipartFile file, Long id) throws IOException {
        System.out.println(file.getContentType() + "\n " + file.getOriginalFilename() + "\n " + file.getSize() );
        photoRepo.save(Photo.builder().
                fileName(file.getName()).
                contentType(file.getContentType()).
                size(file.getSize()).
                bytes(file.getBytes()).
                commentId(id).
                build());
    }
    public Photo getPhotosByCommentId(Long id) {

     return photoRepo.getPhotoByCommentId(id);

//        return  ResponseEntity.ok().
//                header("fileName").contentType(MediaType.IMAGE_JPEG).
//                contentLength(photo.getSize()).body(
//                        new InputStreamResource(new ByteArrayInputStream(photo.getBytes()))
//                );
    }
    public HttpStatus deletePhoto(Long id) {

            photoRepo.deleteById(id);

            return HttpStatus.OK;

    }
    private boolean checkDocument() {

        return false;
    }


    }