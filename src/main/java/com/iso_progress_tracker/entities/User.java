package com.iso_progress_tracker.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;

    private boolean enabled = false; // Account requires admin approval before login

    @Column(nullable = false)
    private boolean passwordResetRequested = false;
    // Flag for forgotten password requests

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean mustChangePassword = false;

    @Enumerated(EnumType.STRING)
    private Role role;

    public User() {}

    public User(String username, String password, String firstName, String lastName, String email, String phoneNumber, boolean enabled, Role role) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.enabled = enabled;
        this.role = role;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public boolean isPasswordResetRequested() { return passwordResetRequested; }
    public void setPasswordResetRequested(boolean passwordResetRequested) { this.passwordResetRequested = passwordResetRequested; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }


    public boolean isMustChangePassword() { return mustChangePassword; }
    public void setMustChangePassword(boolean mustChangePassword) {
        this.mustChangePassword = mustChangePassword;
    }
}