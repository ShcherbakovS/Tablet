package com.cpstablet.tablet.controller;

import com.cpstablet.tablet.service.DirectoryService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@Controller
@AllArgsConstructor
@RequestMapping("/directories")
public class DirectoryController {

    private final DirectoryService directoryService;

    @GetMapping("/getRegions")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public ResponseEntity<Map<String, String>> getRegionsDirectory() {

        return new ResponseEntity<>(directoryService.getRegions(), HttpStatus.OK);

    }
    @GetMapping("/getObjectTypes")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public ResponseEntity< List<String>> getObjectTypesDirectory() {

        return new ResponseEntity<>(directoryService.getObjectTypes(), HttpStatus.OK);

    }
}
