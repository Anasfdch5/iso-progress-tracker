package com.iso_progress_tracker.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final ForcePasswordChangeInterceptor forcePasswordChangeInterceptor;

    public WebMvcConfig(ForcePasswordChangeInterceptor forcePasswordChangeInterceptor) {
        this.forcePasswordChangeInterceptor = forcePasswordChangeInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(forcePasswordChangeInterceptor);
    }
}