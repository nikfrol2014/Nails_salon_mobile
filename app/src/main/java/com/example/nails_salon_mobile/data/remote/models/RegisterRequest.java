package com.example.nails_salon_mobile.data.remote.models;

import com.google.gson.annotations.SerializedName;
import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;

public class RegisterRequest {

    @SerializedName("email")
    private String email;

    @SerializedName("password")
    private String password;

    @SerializedName("firstName")
    private String firstName;

    @SerializedName("lastName")
    private String lastName;

    @SerializedName("phone")
    private String phone;

    @SerializedName("role")
    private String role = "CLIENT"; // Всегда CLIENT для регистрации

    @SerializedName("birthdate")
    private String birthdateString; // Формат "1990-05-15"

    // Конструкторы
    public RegisterRequest() {}

    public RegisterRequest(String email, String password, String firstName,
                           String lastName, String phone, LocalDate birthdate) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.role = "CLIENT";
        setBirthdate(birthdate);
    }

    // Геттеры и сеттеры
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    // Работа с датой
    public LocalDate getBirthdate() {
        if (birthdateString != null && !birthdateString.isEmpty()) {
            return LocalDate.parse(birthdateString, DateTimeFormatter.ISO_LOCAL_DATE);
        }
        return null;
    }

    public void setBirthdate(LocalDate birthdate) {
        if (birthdate != null) {
            this.birthdateString = birthdate.format(DateTimeFormatter.ISO_LOCAL_DATE);
        } else {
            this.birthdateString = null;
        }
    }

    public String getBirthdateString() { return birthdateString; }
    public void setBirthdateString(String birthdateString) {
        this.birthdateString = birthdateString;
    }

    // Вспомогательный метод для получения полного имени
    public String getFullName() {
        return (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "");
    }
}