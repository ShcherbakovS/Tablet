package com.cpstablet.tablet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TabletApplication {
    public static void main(String[] args) {
        SpringApplication.run(TabletApplication.class, args);
    }

}
