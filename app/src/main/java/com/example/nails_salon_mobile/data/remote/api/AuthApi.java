package com.example.nails_salon_mobile.data.remote.api;

import com.example.nails_salon_mobile.data.remote.models.JwtResponse;
import com.example.nails_salon_mobile.data.remote.models.LoginRequest;
import com.example.nails_salon_mobile.data.remote.models.RefreshTokenRequest;
import com.example.nails_salon_mobile.data.remote.models.UserResponseDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Header;

public interface AuthApi {
    @POST("api/v1/auth/login")
    Call<JwtResponse> login(@Body LoginRequest loginRequest);

    // Другие методы аутентификации
    @POST("api/v1/auth/refresh-token")
    Call<JwtResponse> refreshToken(@Body RefreshTokenRequest request);

    @GET("api/v1/users/me")
    Call<UserResponseDto> getCurrentUser(@Header("Authorization") String token);
}