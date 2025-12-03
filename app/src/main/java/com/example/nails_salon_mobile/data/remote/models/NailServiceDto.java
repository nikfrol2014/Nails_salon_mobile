package com.example.nails_salon_mobile.data.remote.models;

import com.google.gson.annotations.SerializedName;

public class NailServiceDto {

    @SerializedName("serviceId")  // Было: "id"
    private Long id;

    @SerializedName("name")
    private String name;

    @SerializedName("description")
    private String description;

    @SerializedName("basePrice")  // Было: "price"
    private Double price;

    @SerializedName("baseDuration")  // Было: "durationMinutes"
    private Integer durationMinutes;

    @SerializedName("categoryId")
    private Long categoryId;

    @SerializedName("categoryName")
    private String categoryName;

    @SerializedName("active")  // Было: "isActive"
    private Boolean isActive;

    // Геттеры с защитой от null
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name != null ? name : ""; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description != null ? description : ""; }
    public void setDescription(String description) { this.description = description; }

    public Double getPrice() { return price != null ? price : 0.0; }
    public void setPrice(Double price) { this.price = price; }

    public Integer getDurationMinutes() { return durationMinutes != null ? durationMinutes : 0; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName != null ? categoryName : ""; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public Boolean getActive() { return isActive != null ? isActive : true; }
    public void setActive(Boolean active) { isActive = active; }
}