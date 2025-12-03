package com.example.nails_salon_mobile.data.remote.models;

import com.google.gson.annotations.SerializedName;

public class MasterDto {

    @SerializedName("userId")
    private Long userId;

    @SerializedName("firstName")
    private String firstName;

    @SerializedName("lastName")
    private String lastName;

    @SerializedName("specialization")
    private String specialization;

    @SerializedName("photoUrl")
    private String photoUrl;

    @SerializedName("isActive")
    private Boolean isActive;

    // Геттеры с защитой от null
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getFirstName() { return firstName != null ? firstName : ""; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName != null ? lastName : ""; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getFullName() {
        return (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "");
    }

    public String getSpecialization() { return specialization != null ? specialization : ""; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public String getPhotoUrl() { return photoUrl != null ? photoUrl : ""; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    public Boolean getActive() { return isActive != null ? isActive : true; }
    public void setActive(Boolean active) { isActive = active; }
}