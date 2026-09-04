package com.iso_progress_tracker.services;

import com.iso_progress_tracker.entities.Role;
import com.iso_progress_tracker.entities.User;
import com.iso_progress_tracker.repositories.UserRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, @Lazy PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public List<User> getPendingUsers() {
        return userRepository.findByEnabled(false);
    }

    public List<User> getActiveUsers() {
        return userRepository.findByEnabled(true).stream()
                .filter(user -> !"admin".equalsIgnoreCase(user.getUsername()))
                .toList();
    }

    public User registerNewUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEnabled(false); // Awaits admin approval
        user.setRole(Role.ROLE_USER); // 🔒 Always default to ROLE_USER on public signup
        return userRepository.save(user);
    }

    public void approveUser(Long id) {
        userRepository.findById(id).ifPresent(user -> {
            user.setEnabled(true);
            userRepository.save(user);
        });
    }

    // 🛡️ PROTECTED DELETE METHOD
    public boolean deleteUser(Long targetUserId, String currentAdminUsername) {
        Optional<User> targetUserOpt = userRepository.findById(targetUserId);

        if (targetUserOpt.isEmpty()) {
            return false;
        }

        User targetUser = targetUserOpt.get();

        if ("admin".equalsIgnoreCase(targetUser.getUsername())) {
            throw new IllegalArgumentException("The primary system administrator account ('admin') cannot be deleted.");
        }

        if (targetUser.getUsername().equalsIgnoreCase(currentAdminUsername)) {
            throw new IllegalArgumentException("You cannot delete your own active administrator account.");
        }

        if (targetUser.getRole() == Role.ROLE_ADMIN && !"admin".equalsIgnoreCase(currentAdminUsername)) {
            throw new IllegalArgumentException("Only the primary root administrator can delete other admin accounts.");
        }

        userRepository.deleteById(targetUserId);
        return true;
    }

    // 🛡️ PROTECTED UPDATE METHOD WITH SELF-ROLE LOCKOUT PROTECTION
    public boolean updateUser(User updatedUser, String currentAdminUsername) {
        Optional<User> existingUserOpt = userRepository.findById(updatedUser.getId());

        if (existingUserOpt.isEmpty()) {
            return false;
        }

        User existingUser = existingUserOpt.get();

        if (existingUser.getUsername().equalsIgnoreCase(currentAdminUsername)) {
            if (updatedUser.getRole() != existingUser.getRole()) {
                throw new IllegalArgumentException("Security violation: You cannot alter or demote your own administrative role.");
            }
        }

        if ("admin".equalsIgnoreCase(existingUser.getUsername()) && updatedUser.getRole() != Role.ROLE_ADMIN) {
            throw new IllegalArgumentException("The primary root administrator role cannot be demoted.");
        }

        if (existingUser.getRole() == Role.ROLE_ADMIN
                && !"admin".equalsIgnoreCase(currentAdminUsername)
                && !existingUser.getUsername().equalsIgnoreCase(currentAdminUsername)) {
            throw new IllegalArgumentException("Only the primary root administrator ('admin') can modify another admin's account.");
        }

        Role finalRole = existingUser.getUsername().equalsIgnoreCase(currentAdminUsername)
                ? existingUser.getRole()
                : updatedUser.getRole();

        existingUser.setFirstName(updatedUser.getFirstName());
        existingUser.setLastName(updatedUser.getLastName());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setPhoneNumber(updatedUser.getPhoneNumber());
        existingUser.setRole(finalRole);

        userRepository.save(existingUser);
        return true;
    }

    public void updateProfileDetails(String username, User updatedDetails) {
        userRepository.findByUsername(username).ifPresent(user -> {
            user.setFirstName(updatedDetails.getFirstName());
            user.setLastName(updatedDetails.getLastName());
            user.setEmail(updatedDetails.getEmail());
            user.setPhoneNumber(updatedDetails.getPhoneNumber());
            userRepository.save(user);
        });
    }

    public boolean updatePassword(String username, String currentRawPassword, String newRawPassword) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            return false;
        }

        User user = userOpt.get();
        if (!passwordEncoder.matches(currentRawPassword.trim(), user.getPassword())) {
            return false;
        }

        user.setPassword(passwordEncoder.encode(newRawPassword.trim()));
        userRepository.save(user);
        return true;
    }

    // ==========================================
    // ADMIN PASSWORD RESET FLOW METHODS
    // ==========================================

    // 1. User submits request with matching username and email
    public boolean requestPasswordReset(String username, String email) {
        if (username == null || email == null) return false;
        Optional<User> userOpt = userRepository.findByUsername(username.trim());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.getEmail() != null && user.getEmail().equalsIgnoreCase(email.trim())) {
                user.setPasswordResetRequested(true);
                userRepository.save(user);
                return true;
            }
        }
        return false;
    }

    // 2. Admin fetches all active requests
    public List<User> getUsersRequestingPasswordReset() {
        return userRepository.findAll().stream()
                .filter(User::isPasswordResetRequested)
                .toList();
    }

    // 3. Admin updates the user's password -> Marks password change required on next login
    public boolean adminResetPassword(Long userId, String newRawPassword) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setPassword(passwordEncoder.encode(newRawPassword.trim()));
            user.setPasswordResetRequested(false);
            user.setMustChangePassword(true); // 👈 Requires first-login password update
            userRepository.save(user);
            return true;
        }
        return false;
    }

    // 4. User sets their new password upon forced change -> Clears flag
    public boolean forceChangePassword(String username, String newPassword) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setPassword(passwordEncoder.encode(newPassword.trim()));
            user.setMustChangePassword(false); // 👈 Clears flag after successfully updating
            userRepository.save(user);
            return true;
        }
        return false;
    }

    // 5. User validates credentials to view reset status
    public Optional<User> checkResetStatus(String username, String email) {
        if (username == null || email == null) return Optional.empty();
        Optional<User> userOpt = userRepository.findByUsername(username.trim());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.getEmail() != null && user.getEmail().equalsIgnoreCase(email.trim())) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    public void seedDefaultUsers() {
        if (userRepository.count() == 0) {
            User admin = new User("admin", passwordEncoder.encode("admin123"), "Admin", "System", "admin@iso.com", "0600000000", true, Role.ROLE_ADMIN);
            userRepository.saveAll(List.of(admin));
        }
    }
}