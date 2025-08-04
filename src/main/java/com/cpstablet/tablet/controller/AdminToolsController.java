package com.cpstablet.tablet.controller;

import com.cpstablet.tablet.DTO.ApplicationResponseDTO;
import com.cpstablet.tablet.DTO.UserDTO;
import com.cpstablet.tablet.DTO.sesurityDTO.RegistrationRequestDTO;
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

    // TODO: очереди RabbitMQ для всех эндпоинтов
    // TODO: кэширование данных, целесобразность (справочники, проверить списки)

    private final UserService userService;
    private final AdminService adminService;

    @GetMapping("/getUser/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public UserDTO getUser(@PathVariable("id") Long id) {
        return userService.getUserById(id);
    }

    @DeleteMapping("/delete_user")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity delete(@RequestParam("id") Long userId) {
        System.out.println(userId);
        return userService.deleteByUserId(userId);
    }
    @PostMapping("/set_user_role")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity setUserRole(@RequestParam("id") Long userId,
                                        @RequestParam("role") String role) {
        System.out.println(role);
        return userService.setUserRole(userId, role);
    }
    @PostMapping("/set_objects")
    @PreAuthorize("hasAuthority('ADMIN')")
    public HttpStatus setObjects(@RequestParam("id") Long id,
                                @RequestParam("objects") List <String> objects) {
        return adminService.setObjectsToUser(id, objects);
    }
    @GetMapping("/getRegistrationApps")
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<RegistrationRequestDTO> getRegistrationApps() {

        return null;
    }
    @GetMapping("/getUsers")
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
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

        return HttpStatus.OK;
    }
    @DeleteMapping("/deleteApplications")
    @PreAuthorize("hasAuthority('ADMIN')")
    public void deleteApps() {
        adminService.deleteApps();
    }
}
