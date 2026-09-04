package com.iso_progress_tracker.config;

import com.iso_progress_tracker.services.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initUsers(UserService userService) {
        return args -> userService.seedDefaultUsers();
    }
}
