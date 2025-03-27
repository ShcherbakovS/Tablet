package com.cpstablet.tablet.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticatedUserControllerTest {

    @GetMapping("/authUser")
    @PreAuthorize("hasRole('USER')")
    public String authUser(){
        return "Авторизованный пользователь/либо админ";
    }
}
