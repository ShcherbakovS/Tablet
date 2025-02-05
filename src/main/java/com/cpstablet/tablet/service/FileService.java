package com.cpstablet.tablet.service;

import com.cpstablet.tablet.entity.PNRSystem;
import com.cpstablet.tablet.entity.Photo;
import com.cpstablet.tablet.entity.SubObject;
import com.cpstablet.tablet.repository.PhotosRepo;
import com.cpstablet.tablet.repository.SubObjectRepo;
import com.cpstablet.tablet.repository.SystemRepo;
import lombok.AllArgsConstructor;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.time.LocalDate;
import java.util.Map;

@Service
@AllArgsConstructor
public class FileService {

    private final SubObjectRepo subObjectRepo;
    private final SystemRepo systemRepo;
    private final PhotosRepo photosRepo;

// загрузка структуры ОКС (подобъекты, системы)
    public void downloadStructure(MultipartFile file, String CCSCode) throws IOException {

        Map<Integer, String> headers = Map.of(
                0, "Поз. по ГП",
                1, "Объекты по ГП",
                2, "Системы",
                3, "Шифр РД",
                4, "Номер акта ИИ",
                5, "Номер акта КО"
        );


        ZipSecureFile.setMinInflateRatio(0);

        Workbook workbook = new XSSFWorkbook(file.getInputStream());

        Sheet sheet = workbook.getSheetAt(0);


        for (int i = 10; i <= sheet.getLastRowNum(); i++) {

            subObjectCreate(sheet.getRow(i), CCSCode);
        }

        for (int i = 10; i <= sheet.getLastRowNum(); i++) {

            systemCreate(sheet.getRow(i), CCSCode);
        }
    }
    private void subObjectCreate(Row row, String CCSCode) {
        DataFormatter df = new DataFormatter();

            subObjectRepo.save(SubObject.builder().
                    subObjectName(df.formatCellValue(row.getCell(1))).
                    numberKO(df.formatCellValue(row.getCell(5))).
                    CCSCode(CCSCode).
                    build());

    }
    public void systemCreate(Row row, String CCSCode) {


        DataFormatter df = new DataFormatter();
        systemRepo.save(PNRSystem.builder().
                PNRSystemName(df.formatCellValue(row.getCell(2))).
                PNRSystemRD(df.formatCellValue(row.getCell(3))).
                PNRSystemII(df.formatCellValue(row.getCell(4))).
                PNRSystemKO(df.formatCellValue(row.getCell(5))).
                CCSNumber(CCSCode).
                PNRSystemStatus("  ").
                PNRPlanDate(null).
                PNRFactDate(null).
                IIPlanDate(null).
                IIFactDate(null).
                KOPlanDate(null).
                KOFactDate(null).
                CIWExecutor("Не определён").
                CWExecutor("Не определён").
                build());
    }
    public void uploadPhotos(MultipartFile file, Long id) throws IOException {
        System.out.println(file.getContentType() + "\n " + file.getOriginalFilename() + "\n " + file.getSize() );
        photosRepo.save(Photo.builder().
                fileName(file.getName()).
                contentType(file.getContentType()).
                size(file.getSize()).
                bytes(file.getBytes()).
                commentId(id).
                build());
    }

    public ResponseEntity getPhotosByCommentId(Long id) {

        Photo photo = photosRepo.getPhotoByCommentId(id);

        return  ResponseEntity.ok().
                header("fileName").contentType(MediaType.valueOf(photo.getContentType())).
                contentLength(photo.getSize()).body(
                        new InputStreamResource(new ByteArrayInputStream(photo.getBytes()))
                );


    }

    public HttpStatus deletePhoto(Long id) {

        if (photosRepo.findById(id).isPresent()) {
            photosRepo.deleteById(id);

            return HttpStatus.OK;
        }

        return HttpStatus.NOT_FOUND;
    }

    }