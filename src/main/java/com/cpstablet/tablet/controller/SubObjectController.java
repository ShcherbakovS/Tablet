package com.cpstablet.tablet.controller;

import com.cpstablet.tablet.entity.SubObject;
import com.cpstablet.tablet.repository.SubObjectRepo;
import com.cpstablet.tablet.service.SystemService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/subobjects")
public class SubObjectController {

    private final SubObjectRepo subObjectRepo;
    private final SystemService systemService;

    @GetMapping("/getAll")
    public ResponseEntity<List<SubObject>> returnAll() {
        return new ResponseEntity<>(subObjectRepo.findAll(), HttpStatus.OK);
    }






}
