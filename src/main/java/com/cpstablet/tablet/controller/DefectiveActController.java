package com.cpstablet.tablet.controller;

import com.cpstablet.tablet.DTO.DefectiveActDTO;
import com.cpstablet.tablet.service.DefectiveActService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/defectiveActs")
public class DefectiveActController {

    @Qualifier("myMapper")
    public ObjectMapper myMapper;

    DefectiveActService defActService;

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

}
