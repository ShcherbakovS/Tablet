package com.cpstablet.tablet.service;

import com.cpstablet.tablet.DTO.OrganisationDTO;
import com.cpstablet.tablet.entity.Organisation;
import com.cpstablet.tablet.entity.User;
import com.cpstablet.tablet.repository.OrganisationRepo;
import com.cpstablet.tablet.repository.UserRepo;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class OrganisationService {

    private final OrganisationRepo organisationRepo;
    private final UserRepo userRepo;


    public Organisation createOrganisation(OrganisationDTO organisationDTO) {

        return organisationRepo.save(Organisation.builder()
                        .organisationName(organisationDTO.getOrganisationName().toUpperCase())
                .build());
    }

    public Organisation updateOrganisation(OrganisationDTO organisationDTO) {

        return organisationRepo.save(Organisation.builder()
                .organisationName(organisationDTO.getOrganisationName().toUpperCase())
                .build());


    }
    public OrganisationDTO findOrganisation(Long id) {

        Organisation organisation = organisationRepo.findById(id).orElseThrow(()-> new RuntimeException("Организация не найдена"));

        return OrganisationDTO.builder()
                .id(organisation.getId())
                .organisationName(organisation.getOrganisationName())
                .build();
    }
    public List<OrganisationDTO> findAll() {

        return organisationRepo.findAll().stream().map(org -> OrganisationDTO.builder()
                .organisationName(org.getOrganisationName())
                .id(org.getId())
                .build()).sorted(Comparator.comparing(OrganisationDTO::getOrganisationName)).collect(Collectors.toList());
    }

        public HttpStatus deleteOrganisation(Long id) {

        Organisation organisation = organisationRepo.findById(id).orElseThrow(()-> new RuntimeException("Организация не найдена"));

        organisationRepo.delete(organisation);

        return HttpStatus.OK;
    }
    public HttpStatus addUserToOrganisation(Long organisationId, Long userId) {

        Organisation organisation =  organisationRepo.findById(organisationId).orElseThrow(()-> new RuntimeException("Организация не найдена"));

        User user = userRepo.findById(userId).orElseThrow(()-> new RuntimeException("Пользователь не найден"));

        organisation.getUsers().add(user);
        user.setOrganisation(organisation);

        organisationRepo.save(organisation);

        return HttpStatus.OK;
    }


}
