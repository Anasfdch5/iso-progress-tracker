package com.iso_progress_tracker.config;

import com.iso_progress_tracker.entities.User;
import com.iso_progress_tracker.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Optional;

@Component
public class ForcePasswordChangeInterceptor implements HandlerInterceptor {

    private final UserService userService;

    public ForcePasswordChangeInterceptor(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            String username = auth.getName();
            Optional<User> userOpt = userService.findByUsername(username);

            if (userOpt.isPresent() && userOpt.get().isMustChangePassword()) {
                String requestURI = request.getRequestURI();

                // Allow access to the force-change page itself, processing endpoint, static assets, and logout
                if (!requestURI.startsWith("/force-change-password") &&
                        !requestURI.startsWith("/logout") &&
                        !requestURI.startsWith("/css/") &&
                        !requestURI.startsWith("/js/")) {

                    response.sendRedirect("/force-change-password");
                    return false;
                }
            }
        }
        return true;
    }
}