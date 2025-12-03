package com.example.nails_salon_mobile.data.remote.models;

import com.google.gson.annotations.SerializedName;

public class ServiceCategory {

    @SerializedName("categoryId")
    private Long categoryId;

    @SerializedName("name")
    private String name;

    @SerializedName("description")
    private String description;

    @SerializedName("sortOrder")
    private Integer sortOrder;

    // Конструкторы
    public ServiceCategory() {}

    public ServiceCategory(Long categoryId, String name, String description, Integer sortOrder) {
        this.categoryId = categoryId;
        this.name = name;
        this.description = description;
        this.sortOrder = sortOrder;
    }

    // Геттеры и сеттеры
    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }
}