package com.cpstablet.tablet.controller;

import com.cpstablet.tablet.entity.Photo;
import com.cpstablet.tablet.service.FileService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;


@RestController
@RequestMapping(("/files"))
@AllArgsConstructor
//TODO: переписать логику в контроллеры систем, уточнить корректность исполнения логики в данных контроллерах
public class FileController {

    private final FileService fileService;

    @PostMapping("/uploadStructure/{nameCSS}")
    public ResponseEntity downloadStructure(@RequestParam("file") MultipartFile multipartFile, @PathVariable ("nameCSS") String nameCSS) {

        System.out.println(nameCSS);
            try {
                fileService.uploadStructure(multipartFile, nameCSS);
            } catch(RuntimeException e) {
                e.getSuppressed();
                e.printStackTrace();
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            } catch (IOException e) {
                e.printStackTrace();
                return new ResponseEntity<>("Неверный формат файла", HttpStatus.BAD_REQUEST);
            }
        return new ResponseEntity<>("Структура загружена успешно", HttpStatus.OK);
    }

    @PostMapping( "/uploadPhotos/{commentId}")
    public HttpStatus downloadPhotos(@RequestParam("photo") MultipartFile multipartFile, @PathVariable("commentId")Long id ) {
        try {
            fileService.uploadPhotos(multipartFile, id);
        } catch (IOException e) {
            e.printStackTrace();
            return HttpStatus.UNSUPPORTED_MEDIA_TYPE;

        }
        return HttpStatus.CREATED;
    }
    @GetMapping("/downloadPhoto/{id}")
    public Photo getPhotosByCommentId(@PathVariable("id") Long id) {

        return fileService.getPhotosByCommentId(id);

    }
    @DeleteMapping("/deletePhotoById/{id}")
    public HttpStatus deletePhotoById(@PathVariable("id") Long id) {
        System.out.println();
        return fileService.deletePhoto(id);
    }

}
