package com.cpstablet.tablet.service;

import com.cpstablet.tablet.DTO.ApplicationResponseDTO;
import com.cpstablet.tablet.entity.Application;
import com.cpstablet.tablet.entity.User;
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

        // TODO: необходимо забирать айди заявки по нему вытаскивать заявку -> пользователя, редактировать саму заявку
        // * Редактировать заявку.
        // * Редактировать список объуктов

        Application userApp = appRepo.findById(id).orElseThrow(()-> new RuntimeException("Заявка не найдена"));

        User user = userApp.getUser();

        objects.stream().forEach(objectCode -> userService.setObjectToAllowed(user.getId(), objectCode));



        return HttpStatus.OK;
    }

    public List<ApplicationResponseDTO> getApplications() {

        return appRepo.findAll().stream().map(app-> appService.buildAppRespDTO(app)
        ).collect(Collectors.toList());

    }
// TODO: не передаю организацию при необходимости добавить
    public ApplicationResponseDTO getApplication(Long id) {
        System.out.println("Айди заявки пользователя " + id );
        return appService.buildAppRespDTO(appRepo.findById(id).orElseThrow(()-> new RuntimeException("Заявка не найдена")));

    }
    public void deleteApps() {
        appRepo.deleteAll();
    }
}
