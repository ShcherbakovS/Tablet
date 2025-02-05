package com.cpstablet.tablet.controller;


import com.cpstablet.tablet.DTO.CommentDTO;
import com.cpstablet.tablet.DTO.PNRSystemDTO;
import com.cpstablet.tablet.entity.PNRSystem;
import com.cpstablet.tablet.repository.SystemRepo;
import com.cpstablet.tablet.service.SystemService;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/systems")
@AllArgsConstructor
public class SystemController {
    private final SystemRepo systemRepo;

    private final SystemService systemService;

    @GetMapping("/getSystems/{codeCCS}")
    public ResponseEntity<List<PNRSystem>> getSystems(@PathVariable("codeCCS") String codeCCS) {

        // TODO: не корректо отдать через Сервичный класс
        return new ResponseEntity<>(systemRepo.getAllByCCSNumber(codeCCS), HttpStatus.OK);
    }

    @PutMapping("/updateSystemInfo/{id}")
    public HttpStatus updatePNRSystemInfo(@RequestBody String JsonString, @PathVariable("id") Long id) {

        try {
            return systemService.updateSystemInfo(new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL).
                    enable(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT).readValue(JsonString, PNRSystemDTO.class), id);
        } catch (
                JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }


}
