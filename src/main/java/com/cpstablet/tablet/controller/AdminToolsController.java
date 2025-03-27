package com.cpstablet.tablet.controller;

import com.cpstablet.tablet.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@AllArgsConstructor
public class AdminToolsController {

    private final UserService userService;

    @GetMapping("/isAdminRole")
    @PreAuthorize("hasRole('ADMIN')")
    public String isAdminRole()  {
        return "Админ";
    }

    @PostMapping("/delete_user")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity delete(@RequestBody String username) {

        return userService.deleteByUserName(username);
    }
    @PostMapping("/set_user_role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity setUserRole(@RequestBody String username) {

        return userService.setUserRole(username);
    }
}
