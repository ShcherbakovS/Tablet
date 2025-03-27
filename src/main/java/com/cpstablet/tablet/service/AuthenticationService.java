package com.cpstablet.tablet.service;

import com.cpstablet.tablet.DTO.sesurityDTO.AuthenticationResponseDTO;
import com.cpstablet.tablet.DTO.sesurityDTO.LoginRequestDTO;
import com.cpstablet.tablet.DTO.sesurityDTO.RegistrationRequestDTO;
import com.cpstablet.tablet.entity.Role;
import com.cpstablet.tablet.entity.Token;
import com.cpstablet.tablet.entity.User;
import com.cpstablet.tablet.repository.TokenRepo;
import com.cpstablet.tablet.repository.UserRepo;
import com.cpstablet.tablet.service.mail.MailService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class AuthenticationService {

    private final UserRepo userRepo;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;
    private final TokenRepo tokenRepo;
    private final MailService mailService;


    public void registration(RegistrationRequestDTO registration) {
        User user = new User();

        user.setUsername(registration.getUsername());
        user.setEmail(registration.getEmail());
        user.setPassword(passwordEncoder.encode(registration.getPassword()));
        user.setRole(Role.USER);


        userRepo.save(user);
        System.out.println(registration.getEmail());
//        mailService.sendEmail(registration);
    }

    private void revokeAllToken(User user) {

        List<Token> validTokens = tokenRepo.findAllAccessTokenByUser(user.getId());

        if(!validTokens.isEmpty()) {
            validTokens.forEach(t->{
                t.setLoggedOut(true);
            });
        }
        tokenRepo.saveAll(validTokens);
    }
    private void saveUserToken(String accessToken, String refreshToken, User user) {

        Token token = new Token();

        token.setAccessToken(accessToken);
        token.setRefreshToken(refreshToken);
        token.setLoggedOut(false);
        token.setUser(user);

        tokenRepo.save(token);
    }
    public AuthenticationResponseDTO authenticate(LoginRequestDTO request) {
        authManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()));

        User user = userRepo.findByUsername(request.getUsername()).orElseThrow();

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        revokeAllToken(user);

        saveUserToken(accessToken, refreshToken, user);

        return new AuthenticationResponseDTO(accessToken, refreshToken);
    }

    public ResponseEntity<AuthenticationResponseDTO> refreshToken(
            HttpServletRequest request,
            HttpServletResponse response) {

        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = authorizationHeader.substring(7);
        String username = jwtService.extractName(token);

        User user = userRepo.findByUsername(username).
                orElseThrow(()-> new UsernameNotFoundException("Пользователь " + username + " не найден"));

        if(jwtService.isValid(token, user)) {

            String accessToken = jwtService.generateAccessToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);

            revokeAllToken(user);

            saveUserToken(accessToken, refreshToken, user);

            return new ResponseEntity<>(new AuthenticationResponseDTO(accessToken, refreshToken), HttpStatus.OK);

        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
