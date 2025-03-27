package com.cpstablet.tablet.controller;



import com.cpstablet.tablet.DTO.PNRSystemDTO;
import com.cpstablet.tablet.entity.PNRSystem;
import com.cpstablet.tablet.service.SubObjectService;
import com.cpstablet.tablet.service.SystemService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/systems")
@AllArgsConstructor
public class SystemController {

    @Qualifier("myMapper")
    private final ObjectMapper myMapper;

    private final SystemService systemService;

    @GetMapping("/getSystems/{codeCCS}")
    public ResponseEntity<List<PNRSystemDTO>> getSystems(@PathVariable("codeCCS") String codeCCS) {

        return new ResponseEntity<>(systemService.getSystemsByCCSCode(codeCCS), HttpStatus.OK);
    }
    @PutMapping("/updateSystemInfo/{id}")
    public HttpStatus updatePNRSystemInfo(@RequestBody String JsonString, @PathVariable("id") Long id) {

        try {
            return systemService.updateSystemInfo(myMapper.readValue(JsonString, PNRSystemDTO.class), id);
        } catch (
                JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

}
