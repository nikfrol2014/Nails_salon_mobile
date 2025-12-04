package com.example.nails_salon_mobile.data.remote.models;

import com.google.gson.annotations.SerializedName;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.DateTimeParseException;

public class UserResponseDto {
    @SerializedName("userId")
    private Long userId;

    @SerializedName("email")
    private String email;

    @SerializedName("firstName")
    private String firstName;

    @SerializedName("lastName")
    private String lastName;

    @SerializedName("phone")
    private String phone;

    @SerializedName("createdAt")
    private String createdAtString;

    @SerializedName("userType")
    private String userType; // "ADMIN", "MASTER", "CLIENT"

    @SerializedName("role")
    private String role;

    @SerializedName("isActive")
    private Boolean isActive;

    // Конструкторы
    public UserResponseDto() {}

    public UserResponseDto(Long userId, String email, String firstName, String lastName,
                           String phone, String createdAt, String userType,
                           String role, Boolean isActive) {
        this.userId = userId;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.createdAtString = createdAt;
        this.userType = userType;
        this.role = role;
        this.isActive = isActive;
    }
    public String getFullName() {
        return (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "");
    }

    // Геттер для LocalDateTime (конвертация при необходимости)
    public LocalDateTime getCreatedAt() {
        if (createdAtString == null || createdAtString.isEmpty()) {
            return null;
        }
        try {
            // Пробуем разные форматы
            if (createdAtString.contains("T")) {
                // ISO форматы: "2025-12-04T15:34:09" или "2025-12-04T15:34:09Z"
                return LocalDateTime.parse(
                        createdAtString.replace("Z", ""),
                        DateTimeFormatter.ISO_LOCAL_DATE_TIME
                );
            } else {
                // Другие форматы, если есть
                return LocalDateTime.parse(createdAtString, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            }
        } catch (DateTimeParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Сеттер для LocalDateTime
    public void setCreatedAt(LocalDateTime createdAt) {
        if (createdAt != null) {
            this.createdAtString = createdAt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } else {
            this.createdAtString = null;
        }
    }

    // Геттер для строки (для GSON)
    public String getCreatedAtString() {
        return createdAtString;
    }

    // Сеттер для строки (для GSON)
    public void setCreatedAtString(String createdAtString) {
        this.createdAtString = createdAtString;
    }

    // Геттеры и сеттеры для остальных полей
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Boolean getActive() { return isActive; }
    public void setActive(Boolean active) { isActive = active; }
}