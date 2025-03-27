package com.cpstablet.tablet.config;

import com.cpstablet.tablet.filter.JwtFilter;
import com.cpstablet.tablet.handler.CustomAccessDeniedHandler;
import com.cpstablet.tablet.handler.CustomLogoutHandler;

import com.cpstablet.tablet.service.ServiceImpl.UserServiceImpl;
import com.cpstablet.tablet.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final UserService userService;
    private final CustomAccessDeniedHandler accessDeniedHandler;
    private final CustomLogoutHandler customLogoutHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception{

        httpSecurity.csrf(AbstractHttpConfigurer::disable);

        httpSecurity.authorizeHttpRequests( auth-> {

            auth.requestMatchers("/registration/**", "/login/**","/refresh_token/**", "/comments/**", "/capitals/**",
                    "/systems/**", "/commons/**","/files/**", "/subObjects/**").permitAll();
            auth.requestMatchers("/admin**","/authUser**").hasAnyAuthority("ADMIN");
            auth.requestMatchers("/authUser**").hasAnyAuthority("USER");
            auth.anyRequest().authenticated();

        })
                .userDetailsService(userService)
                .exceptionHandling( e-> {
                    e.accessDeniedHandler(accessDeniedHandler);
                    e.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));
                })
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .logout( logout -> {
                    logout.logoutUrl("/logout");
                    logout.addLogoutHandler(customLogoutHandler);
                    logout.logoutSuccessHandler(((request, response, authentication) ->
                            SecurityContextHolder.clearContext()));
                } );
        return httpSecurity.build();
    }
    @Bean
    public PasswordEncoder passwordEncoder()  {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception  {
        return config.getAuthenticationManager();
    }

}
