package com.cpstablet.tablet.controller;

import com.cpstablet.tablet.DTO.ApplicationResponseDTO;
import com.cpstablet.tablet.DTO.UserDTO;
import com.cpstablet.tablet.entity.User;
import com.cpstablet.tablet.service.AdminService;
import com.cpstablet.tablet.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@AllArgsConstructor
public class AdminToolsController {

    private final UserService userService;
    private final AdminService adminService;

    @DeleteMapping("/delete_user")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity delete(@RequestPart("id") Long userId) {
        System.out.println(userId);
        return userService.deleteByUserId(userId);
    }
    //TODO заявка на присвоение роли
    @PostMapping("/set_user_role")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity setUserRole(@RequestParam("id") Long userId) {

        return userService.setUserRole(userId);
    }
    //TODO заявка на доступ к объекту
    @PostMapping("/set_objects")
    @PreAuthorize("hasAuthority('ADMIN')")
    public HttpStatus setObjects(@RequestParam("id") Long id,
                                @RequestParam("objects") List <String> objects) {
        return adminService.setObjectsToUser(id, objects);
    }
    @GetMapping("/getUsers")
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<UserDTO> getUsers() {
        return userService.getAllUsers();
    }
    @GetMapping("/getApplications")
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<ApplicationResponseDTO> getApplications() {
        return adminService.getApplications();
    }
    @GetMapping("/getApplication/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ApplicationResponseDTO getApplication(@PathVariable("id") Long id){
        return adminService.getApplication(id);
    }
    @PostMapping("/acceptUser")
    @PreAuthorize("hasAuthority('ADMIN')")
    public HttpStatus acceptUser(@RequestParam("id") Long id) {
        userService.setUserStatus(id);
        return HttpStatus.OK;
    }
}
