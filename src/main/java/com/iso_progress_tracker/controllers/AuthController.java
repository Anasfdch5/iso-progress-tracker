package com.iso_progress_tracker.controllers;

import com.iso_progress_tracker.entities.User;
import com.iso_progress_tracker.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.Optional;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    // ==========================================
    // LOGIN & REGISTRATION
    // ==========================================

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        if (!model.containsAttribute("user")) {
            model.addAttribute("user", new User());
        }
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user, RedirectAttributes redirectAttributes) {
        if (userService.findByUsername(user.getUsername()).isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Username is already taken!");
            redirectAttributes.addFlashAttribute("user", user);
            return "redirect:/register";
        }

        userService.registerNewUser(user);
        return "redirect:/pending-approval";
    }

    @GetMapping("/pending-approval")
    public String pendingApprovalPage() {
        return "pending-approval";
    }

    // ==========================================
    // FORGOT PASSWORD REQUEST FLOW
    // ==========================================

    @GetMapping("/forgot-password")
    public String forgotPasswordPage() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("username") String username,
                                        @RequestParam("email") String email,
                                        RedirectAttributes redirectAttributes) {
        boolean requested = userService.requestPasswordReset(username, email);

        if (!requested) {
            redirectAttributes.addFlashAttribute("error", "Username and Gmail address do not match any active account.");
            return "redirect:/forgot-password";
        }

        return "redirect:/password-reset-pending";
    }

    @GetMapping("/password-reset-pending")
    public String passwordResetPendingPage() {
        return "password-reset-pending";
    }

    @GetMapping("/check-reset-status")
    public String checkResetStatusPage() {
        return "check-reset-status";
    }

    @PostMapping("/check-reset-status")
    public String processCheckResetStatus(@RequestParam("username") String username,
                                          @RequestParam("email") String email,
                                          Model model,
                                          RedirectAttributes redirectAttributes) {

        Optional<User> userOpt = userService.checkResetStatus(username, email);

        if (userOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Invalid username or Gmail address.");
            return "redirect:/check-reset-status";
        }

        User user = userOpt.get();

        if (user.isPasswordResetRequested()) {
            model.addAttribute("pending", true);
            model.addAttribute("username", user.getUsername());
        } else {
            model.addAttribute("fulfilled", true);
            model.addAttribute("username", user.getUsername());
        }

        return "check-reset-status";
    }

    // ==========================================
    // MANDATORY FIRST LOGIN PASSWORD CHANGE
    // ==========================================

    @GetMapping("/force-change-password")
    public String forceChangePasswordPage() {
        return "force-change-password";
    }

    @PostMapping("/force-change-password")
    public String processForceChangePassword(@RequestParam("password") String password,
                                             @RequestParam("confirmPassword") String confirmPassword,
                                             Principal principal,
                                             RedirectAttributes redirectAttributes) {

        if (!password.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Passwords do not match.");
            return "redirect:/force-change-password";
        }

        boolean success = userService.forceChangePassword(principal.getName(), password);

        if (success) {
            redirectAttributes.addFlashAttribute("success", "Password updated successfully! Welcome to your dashboard.");
            return "redirect:/processes";
        }

        redirectAttributes.addFlashAttribute("error", "An error occurred updating your password. Please try again.");
        return "redirect:/force-change-password";
    }
}