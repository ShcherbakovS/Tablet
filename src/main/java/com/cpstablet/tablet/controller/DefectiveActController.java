package com.cpstablet.tablet.controller;

import com.cpstablet.tablet.DTO.DefectiveActDTO;
import com.cpstablet.tablet.entity.Photo;
import com.cpstablet.tablet.service.DefectiveActService;
import com.cpstablet.tablet.service.FileService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/defectiveActs")
public class DefectiveActController {

    @Qualifier("myMapper")
    public ObjectMapper myMapper;

    private final DefectiveActService defActService;
    private final FileService fileService;


    @PostMapping("/createDefAct")
    public ResponseEntity<String> createDefectiveAct (@RequestBody String jsonString) throws JsonProcessingException {

        Long defActId = defActService.create(myMapper.readValue(jsonString, DefectiveActDTO.class)).getId();

        return new ResponseEntity<>(defActId.toString(), HttpStatus.OK);
    }

    @GetMapping("/getAllDefActs/{codeCCS}")
    public ResponseEntity<List<DefectiveActDTO>> getAllDefectiveActs(@PathVariable("codeCCS") String codeCCS) {
        return new ResponseEntity<>(defActService.getDefectiveActs(codeCCS), HttpStatus.OK);
    }
    @GetMapping("/getDefActById/{id}")
    public ResponseEntity<DefectiveActDTO> getCommentById(@PathVariable("id") Long id) {
        return new ResponseEntity<>(defActService.findCommentByCommentId(id), HttpStatus.OK);
    }
    @PutMapping("/updateDefAct/{id}")
    public HttpStatus updateComment(@RequestBody String jsonString, @PathVariable("id") Long id) {

        try {
            return defActService.update(myMapper.readValue(jsonString, DefectiveActDTO.class), id);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);

        }
    }
    @DeleteMapping("/deleteDefAct/{id}")
    public HttpStatus delete(@PathVariable("id") Long id) {

        return defActService.deleteDefectiveActById(id);

    }
    @PostMapping("/addPhoto/{defActId}")
    public ResponseEntity<Photo> addPhotoToDefAct(@PathVariable("defActId") Long defActId, @RequestParam("photo") MultipartFile photo) throws IOException {
        Photo result = fileService.addPhotoToDefectiveAct(defActId, photo);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/getPhoto/{defActId}")
    public ResponseEntity<byte[]> getDefActPhoto(@PathVariable("defActId") Long defActId) {
        byte[] imageData = fileService.getPhotoFromDefectiveAct(defActId);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(imageData);
    }

    @DeleteMapping("/removePhoto/{defActId}")
    public ResponseEntity<Void> removeCommentPhoto(@PathVariable("defActId")  Long defActId) {
        fileService.removePhotoFromDefectiveAct(defActId);
        return ResponseEntity.noContent().build();
    }

}
