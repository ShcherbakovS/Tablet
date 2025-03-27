package com.cpstablet.tablet.service;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;


public interface UserService extends UserDetailsService {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    ResponseEntity deleteByUserName(String username);

    ResponseEntity setUserRole(String username);
}
