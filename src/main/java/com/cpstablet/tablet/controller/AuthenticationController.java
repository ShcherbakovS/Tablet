package com.cpstablet.tablet.controller;

import com.cpstablet.tablet.DTO.sesurityDTO.AuthenticationResponseDTO;
import com.cpstablet.tablet.DTO.sesurityDTO.LoginRequestDTO;
import com.cpstablet.tablet.DTO.sesurityDTO.RegistrationRequestDTO;
import com.cpstablet.tablet.service.AuthenticationService;
import com.cpstablet.tablet.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authService;
    private final UserService userService;

    @Qualifier("myMapper")
    private final ObjectMapper myMapper;



    @PostMapping("/registration")
    public ResponseEntity<String> registration(@RequestBody String request) throws JsonProcessingException {

        RegistrationRequestDTO dto = myMapper.readValue(request,RegistrationRequestDTO.class);

        if(userService.existsByUsername(dto.getUsername())) {
            return ResponseEntity.badRequest().body("Имя пользователя занято");
        }
        if (userService.existsByEmail(dto.getEmail())) {
            return ResponseEntity.badRequest().body("Пользователь с таким Email уже сущетствует");
        }

        authService.registration(dto);


        return ResponseEntity.ok().body("Регистрация прошла успешно");
    }
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponseDTO> authenticate(@RequestBody String requestDTO) throws JsonProcessingException {
        System.out.println("залогиниться");
        return ResponseEntity.ok().body(authService.authenticate(myMapper.readValue(requestDTO, LoginRequestDTO.class)));
    }
    @PostMapping("/refresh_token")
    public ResponseEntity<AuthenticationResponseDTO> refreshToken(
            HttpServletRequest request,
            HttpServletResponse response) {

        return authService.refreshToken(request, response);
    }
}
