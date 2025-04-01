package com.cpstablet.tablet.service;

import com.cpstablet.tablet.DTO.ApplicationResponseDTO;
import com.cpstablet.tablet.DTO.CapitalCSDTO;
import com.cpstablet.tablet.entity.Application;
import com.cpstablet.tablet.repository.ApplicationRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ApplicationService {

    private final ApplicationRepo appRepo;

    public ApplicationResponseDTO findById(Long id) {

        Application application = appRepo.findById(id).orElseThrow(()-> new RuntimeException("Заявка не найдена"));

        return buildAppRespDTO(application);
    }
    public ApplicationResponseDTO buildAppRespDTO(Application app) {

        return ApplicationResponseDTO.builder()
                .id(app.getId())
                .userId(app.getUser().getId())
                .username(app.getUser().getUsername())
                .description(app.getDescription())
                .objectsToAdd(app.getObjectsToAdd().stream().map(obj->
                        CapitalCSDTO.builder()
                                .capitalCSName(obj.getCapitalCSName())
                                .codeCCS(obj.getCodeCCS())
                                .locationRegion(obj.getLocationRegion())
                                .objectType(obj.getObjectType())
                                .customer(obj.getCustomer())
                                .CIWExecutor(obj.getCIWExecutor())
                                .CWExecutor(obj.getCWExecutor())
                                .customerSupervisor(obj.getCustomerSupervisor())
                                .CWSupervisor(obj.getCWSupervisor())
                                .CIWSupervisor(obj.getCIWSupervisor())
                                .build() ).collect(Collectors.toList()))
                .description(app.getDescription())
                .build();

    }
}
