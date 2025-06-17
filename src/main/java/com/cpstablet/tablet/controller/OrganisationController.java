package com.cpstablet.tablet.controller;

import com.cpstablet.tablet.DTO.OrganisationDTO;
import com.cpstablet.tablet.entity.Organisation;
import com.cpstablet.tablet.repository.OrganisationRepo;
import com.cpstablet.tablet.service.OrganisationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/organisations")

public class OrganisationController {

    OrganisationRepo organisationRepo;
    OrganisationService organisationService;

    @Qualifier("myMapper")
    private final ObjectMapper mapper;

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity createOrganisation(@RequestBody OrganisationDTO organisationInfo) {

           return new ResponseEntity<>(organisationService.createOrganisation(organisationInfo), HttpStatus.OK) ;


    }
    @PutMapping("/update")
    @PreAuthorize("hasAuthority('ADMIN')")

    public ResponseEntity updateOrganisationInfo(@RequestBody OrganisationDTO organisationInfo) {
            organisationService.updateOrganisation(organisationInfo);


        return ResponseEntity.ok().build();
    }
    @GetMapping("/getAll")
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    public List<OrganisationDTO> getAll() {

        return organisationService.findAll();
    }
    @DeleteMapping("/delete")
    @PreAuthorize("hasAuthority('ADMIN')")
    public HttpStatus deleteOrganisation(Long id) {
        organisationService.deleteOrganisation(id);

        return HttpStatus.OK;
    }

}
