package com.cpstablet.tablet.service;

import com.cpstablet.tablet.DTO.ApplicationResponseDTO;
import com.cpstablet.tablet.repository.ApplicationRepo;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AdminService {

    private final UserService userService;
    private final ApplicationService appService;
    private final ApplicationRepo appRepo;

    public HttpStatus setObjectsToUser(Long id, List<String> objects) {


        objects.stream().forEach(objectCode -> userService.setObjectToAllowed(id,objectCode));

        return HttpStatus.OK;
    }

    public List<ApplicationResponseDTO> getApplications() {

        return appRepo.findAll().stream().map(app-> appService.buildAppRespDTO(app)
        ).collect(Collectors.toList());

    }

    public ApplicationResponseDTO getApplication(Long id) {
        return appService.buildAppRespDTO(appRepo.findById(id).orElseThrow(()-> new RuntimeException("Заявка не найдена")));

    }
}
