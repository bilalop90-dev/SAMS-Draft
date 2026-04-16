package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ChangePasswordRequest {

    @NotBlank(message = "Username cannot be empty")
    private String username;

    @NotBlank(message = "Current password cannot be empty")
    private String currentPassword;

    @NotBlank(message = "New password cannot be empty")
    @Size(min = 6, message = "New password must be at least 6 characters")
    private String newPassword;

    public ChangePasswordRequest() {}

    public String getUsername()        { return username; }
    public String getCurrentPassword() { return currentPassword; }
    public String getNewPassword()     { return newPassword; }

    public void setUsername(String username)               { this.username = username; }
    public void setCurrentPassword(String currentPassword) { this.currentPassword = currentPassword; }
    public void setNewPassword(String newPassword)         { this.newPassword = newPassword; }
}