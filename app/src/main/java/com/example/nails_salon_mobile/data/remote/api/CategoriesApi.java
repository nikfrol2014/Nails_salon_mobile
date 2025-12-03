package com.example.nails_salon_mobile.data.remote.api;

import com.example.nails_salon_mobile.data.remote.models.ServiceCategory;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface CategoriesApi {

    // Получить все категории услуг
    @GET("api/v1/service-categories")
    Call<List<ServiceCategory>> getAllCategories();

    // Получить категорию по ID
    @GET("api/v1/service-categories/{categoryId}")
    Call<ServiceCategory> getCategoryById(@Path("categoryId") Long categoryId);
}