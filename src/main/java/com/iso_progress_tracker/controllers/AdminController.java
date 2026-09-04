package com.iso_progress_tracker.controllers;

import com.iso_progress_tracker.entities.Role;
import com.iso_progress_tracker.entities.User;
import com.iso_progress_tracker.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public String listUsers(Model model) {
        model.addAttribute("pendingUsers", userService.getPendingUsers());
        model.addAttribute("activeUsers", userService.getActiveUsers());
        model.addAttribute("resetRequests", userService.getUsersRequestingPasswordReset()); // Pass reset requests list
        return "admin-users";
    }

    @PostMapping("/users/approve/{id}")
    public String approveUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        userService.approveUser(id);
        redirectAttributes.addFlashAttribute("success", "User account approved successfully!");
        return "redirect:/admin/users";
    }

    @PostMapping("/users/reject/{id}")
    public String rejectUser(@PathVariable Long id, RedirectAttributes redirectAttributes, Principal principal) {
        try {
            userService.deleteUser(id, principal.getName());
            redirectAttributes.addFlashAttribute("info", "User registration request rejected.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes, Principal principal) {
        try {
            userService.deleteUser(id, principal.getName());
            redirectAttributes.addFlashAttribute("info", "User account deleted successfully.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/users/edit/{id}")
    public String editUserForm(@PathVariable Long id, Model model) {
        userService.findById(id).ifPresent(user -> {
            model.addAttribute("user", user);
            model.addAttribute("roles", Role.values());
        });
        return "admin-edit-user";
    }

    @PostMapping("/users/update")
    public String updateUser(@ModelAttribute("user") User user, RedirectAttributes redirectAttributes, Principal principal) {
        try {
            userService.updateUser(user, principal.getName());
            redirectAttributes.addFlashAttribute("success", "User details updated successfully!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/users/edit/" + user.getId();
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/reset-password")
    public String adminResetPassword(@RequestParam("userId") Long userId,
                                     @RequestParam("newPassword") String newPassword,
                                     RedirectAttributes redirectAttributes) {
        boolean success = userService.adminResetPassword(userId, newPassword);

        if (success) {
            redirectAttributes.addFlashAttribute("success", "Password reset successfully for the user!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Failed to reset password.");
        }

        return "redirect:/admin/users";
    }
}