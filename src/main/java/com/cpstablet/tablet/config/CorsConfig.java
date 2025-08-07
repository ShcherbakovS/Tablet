package com.cpstablet.tablet.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        config.addAllowedOrigin("https://tablet-pnr--pvk1lppoww.expo.app");
        config.addAllowedOrigin("https://xn----7sbpwlcifkq8d.xn--p1ai:8443");
        config.addAllowedOrigin("http://localhost:8081");

        config.addAllowedMethod("GET");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("DELETE");
//        config.addAllowedMethod("OPTIONS");

        config.addAllowedHeader("*");

        config.setAllowCredentials(true);

        config.setMaxAge(3600L);

        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
