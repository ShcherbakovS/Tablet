package com.cpstablet.tablet.controller;

import com.cpstablet.tablet.service.FileService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;


@RestController
@RequestMapping(("/files"))
@AllArgsConstructor
//TODO: переписать логику в контроллеры систем, уточнить корректность исполнения логики в данных контроллерах
public class FileController {

    private final FileService fileService;

    @PostMapping("/uploadStructure/{nameCSS}")
    public HttpStatus downloadStructure(@RequestParam("file") MultipartFile multipartFile, @PathVariable ("nameCSS") String nameCSS) {

        System.out.println(nameCSS);
            try {
                fileService.downloadStructure(multipartFile, nameCSS);
            } catch(IOException e) {
                return HttpStatus.BAD_REQUEST;
            }
            return HttpStatus.OK;
    }

    @PostMapping("/uploadPhotos/{commentId}")
    public HttpStatus downloadPhotos(@RequestPart("photo")MultipartFile multipartFile, @PathVariable("commentId")Long id ) {
        try {
            fileService.uploadPhotos(multipartFile, id);
        } catch (IOException e) {
            return HttpStatus.UNSUPPORTED_MEDIA_TYPE;
        }
        return HttpStatus.CREATED;
    }

    @GetMapping("/downloadPhoto/{id}")
    public ResponseEntity getPhotosByCommentId(@PathVariable("id") Long id) {

        return fileService.getPhotosByCommentId(id);

    }
    @DeleteMapping("/deletePhotoById/{id}")
    public HttpStatus deletePhotoById(@PathVariable("id") Long id) {

        return fileService.deletePhoto(id);
    }

}
