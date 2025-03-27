package com.cpstablet.tablet.handler;

import com.cpstablet.tablet.entity.Token;
import com.cpstablet.tablet.repository.TokenRepo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CustomLogoutHandler implements LogoutHandler {

    private final TokenRepo tokenRepo;

    @Override
    public void logout(HttpServletRequest request,
                       HttpServletResponse response,
                       Authentication authentication) {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer")) {
            return;
        }
        String token = authHeader.substring(7);

        Token tokenEntity = tokenRepo.findByAccessToken(token).orElse(null);

        if(tokenEntity != null) {
            tokenEntity.setLoggedOut(true);
            tokenRepo.save(tokenEntity);
        }

    }
}
