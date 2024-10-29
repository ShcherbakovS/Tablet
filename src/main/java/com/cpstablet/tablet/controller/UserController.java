package com.cpstablet.tablet.controller;

import com.cpstablet.tablet.DTO.UserDTO;
import com.cpstablet.tablet.entity.User;
import com.cpstablet.tablet.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
public class UserController {

    private  final UserService userService;

    @PostMapping("/createUser")
    public ResponseEntity<?> create(@RequestBody UserDTO dto)  {

        return new ResponseEntity<>(userService.create(dto), HttpStatus.OK);
    }

    @GetMapping("/getAllUsers")
    public ResponseEntity<List<User>> findAll() {

        return new ResponseEntity<>(userService.findAll(), HttpStatus.OK);
    }

    @DeleteMapping("/deleteUser/{id}")
    public HttpStatus delete(@PathVariable Long id) {
        userService.deleteUserById(id);

        return  HttpStatus.OK;
    }
}
