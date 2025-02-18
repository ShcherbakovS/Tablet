package com.cpstablet.tablet.controller;

import com.cpstablet.tablet.DTO.CapitalCSDTO;
import com.cpstablet.tablet.entity.CapitalCS;
import com.cpstablet.tablet.service.CapitalCSService;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(("/capitals"))
@AllArgsConstructor
public class CapitalCSController {

    private final CapitalCSService capitalService;

    @Qualifier("myMapper")
    private final ObjectMapper myMapper;


    @PostMapping("/createObject")
    public HttpStatus createNewObject(@RequestBody String jsonString) {

        try {
            capitalService.create(myMapper.readValue(jsonString, CapitalCSDTO.class));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return HttpStatus.CREATED;
    }
    @PutMapping("/updateCapitalCS/{id}")
    public HttpStatus updateCCSInfo(@RequestBody String jsonString, @PathVariable("id") Long id) {
        try {
            capitalService.update(myMapper.readValue(jsonString, CapitalCSDTO.class), id);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return HttpStatus.CREATED;
    }

    @GetMapping("/findByCodeCCS/{codeCCS}")
    public ResponseEntity<CapitalCS> getByCodeCCS(@PathVariable("codeCCS") String codeCCS) {
            return new ResponseEntity(capitalService.findCCS(codeCCS), HttpStatus.OK);
    }

    @GetMapping("/getAll")
    public  ResponseEntity<List<CapitalCS>> getAll() {
        return new ResponseEntity<>(capitalService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/getApprovedFacilities/{userId}")
    public ResponseEntity<List<CapitalCSDTO>> getUserApprovedCapitalCCS(@PathVariable("userId") String userId) {

        return null;
    }

    @DeleteMapping("/deleteCapitalCS/{id}")
    public HttpStatus deleteCapitalCS(@PathVariable("id") Long id) {
        return capitalService.deleteCapitalCS(id);
    }
}
