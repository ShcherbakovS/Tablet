package com.cpstablet.tablet.service;

import com.cpstablet.tablet.DTO.*;
import com.cpstablet.tablet.entity.Application;
import com.cpstablet.tablet.entity.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.sql.ClientInfoStatus;
import java.util.List;
import java.util.Optional;


public interface UserService extends UserDetailsService {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    ResponseEntity deleteByUserId(Long userId);

    ResponseEntity setUserRole(Long userId, String role);

    List<CapitalCSDTO> getAllowedObjects(Long id);

    void setObjectToAllowed(Long id, String ccsCode);

    List <UserDTO> getAllUsers();
    List<UserDTO> getUsersByCCS(String ccsCode);

    List<ApplicationResponseDTO> getApplications(Long id);

    HttpStatus updateUserInfo(UserInfoDTO userInfoDTO);

    HttpStatus createApplication(ApplicationRequestDTO applicationRequestDTO, Long id);

    UserDTO getUserById(Long id);

}
