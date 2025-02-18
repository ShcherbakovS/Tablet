package com.cpstablet.tablet.controller;

import com.cpstablet.tablet.entity.SubObject;

import com.cpstablet.tablet.service.SubObjectService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/subObjects")
public class SubObjectController {

    final SubObjectService subObjectService;

    @GetMapping("/getAll/{CCSCode}")
    public ResponseEntity<List<SubObject>> returnAll(@PathVariable("CCSCode") String CCSCode) {

       return new ResponseEntity<>(subObjectService.findAllCCSCode(CCSCode), HttpStatus.OK);

    }






}
