package com.cpstablet.tablet.service;

import com.cpstablet.tablet.DTO.OrganisationDTO;
import com.cpstablet.tablet.entity.Organisation;
import com.cpstablet.tablet.repository.OrganisationRepo;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class OrganisationService {

    private final OrganisationRepo organisationRepo;


    public HttpStatus createOrganisation(OrganisationDTO organisationDTO) {

        organisationRepo.save(Organisation.builder()
                        .organisationName(organisationDTO.getOrganisationName())
                .build());

        return HttpStatus.CREATED;
    }

    public HttpStatus updateOrganisation(OrganisationDTO organisationDTO) {

        organisationRepo.save(Organisation.builder()
                .organisationName(organisationDTO.getOrganisationName())
                .build());

        return HttpStatus.OK;
    }
    public OrganisationDTO findOrganisation(Long id) {

        Organisation organisation = organisationRepo.findById(id).orElseThrow(()-> new RuntimeException("Организация не найдена"));

        return OrganisationDTO.builder()
                .id(organisation.getId())
                .organisationName(organisation.getOrganisationName())
                .build();
    }
    public List<OrganisationDTO> findAll() {

        return organisationRepo.findAll().stream().map(org -> OrganisationDTO.builder().build()).collect(Collectors.toList());
    }

        public HttpStatus deleteOrganisation(Long id) {

        Organisation organisation = organisationRepo.findById(id).orElseThrow(()-> new RuntimeException("Организация не найдена"));

        organisationRepo.delete(organisation);

        return HttpStatus.OK;
    }


}
