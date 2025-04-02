package com.cpstablet.tablet.controller;


import com.cpstablet.tablet.DTO.ApplicationRequestDTO;
import com.cpstablet.tablet.DTO.ApplicationResponseDTO;
import com.cpstablet.tablet.DTO.CapitalCSDTO;
import com.cpstablet.tablet.DTO.UserInfoDTO;
import com.cpstablet.tablet.service.ApplicationService;
import com.cpstablet.tablet.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final ApplicationService appService;
    private final ObjectMapper myMapper;

    public UserController(UserService userService, ApplicationService appService, ObjectMapper myMapper1) {
        this.userService = userService;
        this.appService = appService;
        this.myMapper = myMapper1;

    }

    @GetMapping("/getString")
    @PreAuthorize("hasRole('USER')")
    public String string() {
        return "Вот тут";
    }


    @GetMapping("/getAllowedObjects/{id}")
    @PreAuthorize("hasRole('USER')")
    public List<CapitalCSDTO> getAllowedObjects(@PathVariable("id") Long id) {
        return userService.getAllowedObjects(id);
    }

    @PutMapping("/updateUserInfo")
    @PreAuthorize("hasRole('USER')")
    public HttpStatus updateUserInfo(@RequestBody String userInfo) throws JsonProcessingException {

        return userService.updateUserInfo(myMapper.readValue(userInfo, UserInfoDTO.class));
    }
    @PostMapping("/createApplication/{id}")
    @PreAuthorize("hasRole('USER')")
    public HttpStatus createApplication(@RequestBody String userApplication,
                                        @PathVariable("id") Long id) throws JsonProcessingException {
        System.out.println(userApplication);
        return userService.createApplication(myMapper.readValue(userApplication, ApplicationRequestDTO.class), id);
    }
    @GetMapping("/getUserApplications/{id}")
    @PreAuthorize("hasRole('USER')")
    public List<ApplicationResponseDTO> getApplications(@PathVariable("id") Long id) {
        return userService.getApplications(id);
    }
    @GetMapping("/getApplication/{id}")
    @PreAuthorize("hasRole('USER')")
    public ApplicationResponseDTO getApplication(@PathVariable("id") Long id) {
        return appService.findById(id);
    }
}
