package com.cpstablet.tablet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/organisations")

public class OrganisationController {

    @Qualifier("myMapper")
    private final ObjectMapper mapper;

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity createOrganisation(String organisationInfo) {

        return ResponseEntity.ok().build();

    }
    @PutMapping("/update")
    @PreAuthorize("hasAuthority('ADMIN')")

    public ResponseEntity updateOrganisationInfo(String organisationInfo) {

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAuthority('ADMIN')")
    public HttpStatus deleteOrganisation(Long id) {

        return HttpStatus.OK;
    }

}
