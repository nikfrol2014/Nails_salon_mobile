package com.example.nails_salon_mobile.data.remote.models;

import com.google.gson.annotations.SerializedName;

public class JwtResponse {
    @SerializedName("accessToken")
    private String accessToken;

    @SerializedName("refreshToken")
    private String refreshToken;

    @SerializedName("userId")
    private Long userId;

    @SerializedName("email")
    private String email;

    @SerializedName("role")
    private String role;

    // Конструкторы
    public JwtResponse() {}

    public JwtResponse(String accessToken, String refreshToken, Long userId,
                       String email, String role) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.userId = userId;
        this.email = email;
        this.role = role;
    }

    // Геттеры
    public String getAccessToken() { return accessToken; }
    public String getRefreshToken() { return refreshToken; }
    public Long getUserId() { return userId; }
    public String getEmail() { return email; }
    public String getRole() { return role; }

    // Сеттеры
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setEmail(String email) { this.email = email; }
    public void setRole(String role) { this.role = role; }
}