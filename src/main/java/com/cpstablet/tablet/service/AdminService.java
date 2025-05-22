package com.cpstablet.tablet.service;

import com.cpstablet.tablet.DTO.ApplicationResponseDTO;
import com.cpstablet.tablet.entity.Application;
import com.cpstablet.tablet.entity.User;
import com.cpstablet.tablet.repository.ApplicationRepo;
import com.cpstablet.tablet.repository.UserRepo;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor

public class AdminService {
    private final UserService userService;
    private final ApplicationService appService;
    private final ApplicationRepo appRepo;

    private final UserRepo userRepo;

    public HttpStatus setObjectsToUser(Long id, List<String> objects) {

        // TODO: необходимо забирать айди заявки по нему вытаскивать заявку -> пользователя, редактировать саму заявку
        // * Редактировать заявку.
        // * Редактировать список объектов

        Application userApp = appRepo.findById(id).orElseThrow(()-> new RuntimeException("Заявка не найдена"));

        User user = userApp.getUser();

        objects.forEach(objectCode -> userService.setObjectToAllowed(user.getId(), objectCode));

        user.getApplications().remove(userApp);

        userRepo.save(user);


        return HttpStatus.OK;
    }

    public List<ApplicationResponseDTO> getApplications() {

        return appRepo.findAll().stream().sorted(Comparator.comparing(Application::getCreationTime).reversed()).map(appService::buildAppRespDTO)
                .collect(Collectors.toList());

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
