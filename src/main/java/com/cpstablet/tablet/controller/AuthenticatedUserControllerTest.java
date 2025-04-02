package com.cpstablet.tablet.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticatedUserControllerTest {

    @GetMapping("/authUser")
    public String authUser(){
        return "Авторизованный пользователь/либо админ";
    }
}
