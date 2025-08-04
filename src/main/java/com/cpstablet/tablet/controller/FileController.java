package com.cpstablet.tablet.controller;

import com.cpstablet.tablet.entity.Photo;
import com.cpstablet.tablet.service.FileService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@RestController
@RequestMapping("/files")
@AllArgsConstructor
// TODO: переписать логику в контроллеры систем, уточнить корректность
public class FileController {

    private final FileService fileService;

    @PostMapping("/uploadStructure/{nameCSS}")
    public ResponseEntity uploadStructure(@RequestParam("file") MultipartFile multipartFile,
            @PathVariable("nameCSS") String nameCSS) {

        System.out.println(nameCSS);
        try {
            fileService.uploadStructure(multipartFile, nameCSS);
        } catch (RuntimeException e) {
            e.getSuppressed();
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Неверный формат файла", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Структура загружена успешно", HttpStatus.OK);
    }


}
