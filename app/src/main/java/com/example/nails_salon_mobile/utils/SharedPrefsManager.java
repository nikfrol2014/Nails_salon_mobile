package com.example.nails_salon_mobile.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class SharedPrefsManager {
    private static final String PREFS_NAME = "NailSalonPrefs";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_REFRESH_TOKEN = "refresh_token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_ROLE = "user_role";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_FIRST_NAME = "user_first_name"; // НОВОЕ
    private static final String KEY_USER_LAST_NAME = "user_last_name";   // НОВОЕ
    private static final String KEY_USER_PHONE = "user_phone";           // НОВОЕ


    private SharedPreferences prefs;
    private static SharedPrefsManager instance;

    private SharedPrefsManager(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized SharedPrefsManager getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPrefsManager(context);
        }
        return instance;
    }

    // Метод 1: Сохраняем все данные
    public void saveAuthData(String accessToken, String refreshToken,
                             Long userId, String role, String email, String fullName) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_ACCESS_TOKEN, accessToken);
        editor.putString(KEY_REFRESH_TOKEN, refreshToken);
        editor.putLong(KEY_USER_ID, userId);
        editor.putString(KEY_USER_ROLE, role);
        editor.putString(KEY_USER_EMAIL, email);
        editor.putString(KEY_USER_NAME, fullName);
        editor.apply();
    }

    // Метод 2: Только токены и базовая информация
    public void saveAuthTokens(String accessToken, String refreshToken,
                               Long userId, String role, String email) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_ACCESS_TOKEN, accessToken);
        editor.putString(KEY_REFRESH_TOKEN, refreshToken);
        editor.putLong(KEY_USER_ID, userId);
        editor.putString(KEY_USER_ROLE, role);
        editor.putString(KEY_USER_EMAIL, email != null ? email : "");
        editor.putString(KEY_USER_NAME, ""); // Имя будет получено отдельно
        editor.apply();
    }

    // Метод 3: Только токены (минимальная информация)
    public void saveAuthTokens(String accessToken, String refreshToken,
                               Long userId, String role) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_ACCESS_TOKEN, accessToken);
        editor.putString(KEY_REFRESH_TOKEN, refreshToken);
        editor.putLong(KEY_USER_ID, userId);
        editor.putString(KEY_USER_ROLE, role);
        editor.apply();
    }

    // Метод для сохранения полных данных пользователя
    public void saveUserProfile(String firstName, String lastName, String phone) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_USER_FIRST_NAME, firstName != null ? firstName : "");
        editor.putString(KEY_USER_LAST_NAME, lastName != null ? lastName : "");
        editor.putString(KEY_USER_PHONE, phone != null ? phone : "");

        // Обновляем полное имя
        String fullName = (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "");
        editor.putString(KEY_USER_NAME, fullName.trim());

        editor.apply();
    }

    // Метод для сохранения всех данных пользователя
    public void saveUserData(String accessToken, String refreshToken,
                             Long userId, String role, String email,
                             String firstName, String lastName, String phone) {
        SharedPreferences.Editor editor = prefs.edit();

        // Сохраняем базовые данные
        editor.putString(KEY_ACCESS_TOKEN, accessToken);
        editor.putString(KEY_REFRESH_TOKEN, refreshToken);
        editor.putLong(KEY_USER_ID, userId);
        editor.putString(KEY_USER_ROLE, role);
        editor.putString(KEY_USER_EMAIL, email != null ? email : "");

        // Сохраняем профиль пользователя
        editor.putString(KEY_USER_FIRST_NAME, firstName != null ? firstName : "");
        editor.putString(KEY_USER_LAST_NAME, lastName != null ? lastName : "");
        editor.putString(KEY_USER_PHONE, phone != null ? phone : "");

        // Формируем полное имя
        String fullName = "";
        if (firstName != null && !firstName.isEmpty()) {
            fullName = firstName;
            if (lastName != null && !lastName.isEmpty()) {
                fullName += " " + lastName;
            }
        } else if (lastName != null && !lastName.isEmpty()) {
            fullName = lastName;
        }
        editor.putString(KEY_USER_NAME, fullName);

        editor.apply();

        Log.d("SharedPrefsManager", "Сохранено имя: " + fullName +
                ", email: " + email + ", firstName: " + firstName + ", lastName: " + lastName);
    }

    // Новые геттеры
    public String getUserFirstName() {
        return prefs.getString(KEY_USER_FIRST_NAME, "");
    }

    public String getUserLastName() {
        return prefs.getString(KEY_USER_LAST_NAME, "");
    }

    public String getUserPhone() {
        return prefs.getString(KEY_USER_PHONE, "");
    }

    // Геттеры
    public String getAccessToken() {
        String token = prefs.getString(KEY_ACCESS_TOKEN, null);
        Log.d("SharedPrefsManager", "Токен из SharedPrefs: " +
                (token != null ? token.substring(0, Math.min(20, token.length())) + "..." : "null"));
        return token;
    }

    public String getRefreshToken() {
        return prefs.getString(KEY_REFRESH_TOKEN, null);
    }

    public Long getUserId() {
        return prefs.getLong(KEY_USER_ID, -1);
    }

    public String getUserRole() {
        return prefs.getString(KEY_USER_ROLE, "CLIENT");
    }

    public String getUserEmail() {
        return prefs.getString(KEY_USER_EMAIL, "");
    }

    public String getUserName() {
        String storedName = prefs.getString(KEY_USER_NAME, "");
        if (storedName.isEmpty()) {
            // Если нет полного имени, соберем из компонентов
            String firstName = getUserFirstName();
            String lastName = getUserLastName();
            String fullName = (firstName + " " + lastName).trim();
            if (!fullName.isEmpty()) {
                return fullName;
            }
        }
        return storedName;
    }

    // Обновление отдельных полей
    public void updateUserName(String name) {
        prefs.edit().putString(KEY_USER_NAME, name).apply();
    }

    public void updateUserEmail(String email) {
        prefs.edit().putString(KEY_USER_EMAIL, email).apply();
    }

    public boolean isLoggedIn() {
        return getAccessToken() != null && !getAccessToken().isEmpty();
    }

    public void clearAuthData() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(KEY_ACCESS_TOKEN);
        editor.remove(KEY_REFRESH_TOKEN);
        editor.remove(KEY_USER_ID);
        editor.remove(KEY_USER_ROLE);
        editor.remove(KEY_USER_EMAIL);
        editor.remove(KEY_USER_NAME);
        editor.apply();
    }
}