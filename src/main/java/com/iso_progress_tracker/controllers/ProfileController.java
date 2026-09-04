package com.iso_progress_tracker.controllers;

import com.iso_progress_tracker.dto.PasswordChangeForm;
import com.iso_progress_tracker.entities.User;
import com.iso_progress_tracker.services.UserService;
import jakarta.validation.Valid;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String showProfilePage(Model model, Principal principal) {
        String username = principal.getName();
        User currentUser = userService.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

        model.addAttribute("user", currentUser);
        if (!model.containsAttribute("passwordForm")) {
            model.addAttribute("passwordForm", new PasswordChangeForm());
        }
        return "profile";
    }

    @PostMapping("/update-details")
    public String updateProfileDetails(@ModelAttribute("user") User updatedUser,
                                       Principal principal,
                                       RedirectAttributes redirectAttributes) {
        try {
            String currentUsername = principal.getName();
            userService.updateProfileDetails(currentUsername, updatedUser);

            // If username changed, update Spring Security session context cleanly
            if (!currentUsername.equalsIgnoreCase(updatedUser.getUsername())) {
                User freshUser = userService.findByUsername(updatedUser.getUsername())
                        .orElseThrow(() -> new IllegalArgumentException("User not found after update"));

                UsernamePasswordAuthenticationToken newAuth =
                        new UsernamePasswordAuthenticationToken(
                                freshUser.getUsername(),
                                freshUser.getPassword(),
                                SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                        );
                SecurityContextHolder.getContext().setAuthentication(newAuth);
            }

            redirectAttributes.addFlashAttribute("success", "Profile details updated successfully!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("passwordError", e.getMessage());
        }
        return "redirect:/profile";
    }

    @PostMapping("/update-password")
    public String updatePassword(
            @Valid @ModelAttribute("passwordForm") PasswordChangeForm passwordForm,
            BindingResult bindingResult,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("passwordError", "Password must be at least 6 characters.");
            return "redirect:/profile";
        }

        if (!passwordForm.getNewPassword().equals(passwordForm.getConfirmPassword())) {
            redirectAttributes.addFlashAttribute("passwordError", "New password and confirmation password do not match.");
            return "redirect:/profile";
        }

        boolean success = userService.updatePassword(
                principal.getName(),
                passwordForm.getCurrentPassword(),
                passwordForm.getNewPassword()
        );

        if (!success) {
            //  Flash error message when old password doesn't match
            redirectAttributes.addFlashAttribute("passwordError", "⚠️ Incorrect password! Your current password was not recognized.");
            return "redirect:/profile";
        }

        redirectAttributes.addFlashAttribute("success", "Password changed successfully!");
        return "redirect:/profile";
    }
}