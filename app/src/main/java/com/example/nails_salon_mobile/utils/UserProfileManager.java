package com.example.nails_salon_mobile.utils;

import android.content.Context;
import android.util.Log;
import com.example.nails_salon_mobile.data.remote.RetrofitClient;
import com.example.nails_salon_mobile.data.remote.api.AuthApi;
import com.example.nails_salon_mobile.data.remote.models.UserResponseDto;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserProfileManager {

    private static final String TAG = "UserProfileManager";
    private static UserProfileManager instance;
    private final SharedPrefsManager prefsManager;

    private UserProfileManager(Context context) {
        prefsManager = SharedPrefsManager.getInstance(context);
    }

    public static synchronized UserProfileManager getInstance(Context context) {
        if (instance == null) {
            instance = new UserProfileManager(context);
        }
        return instance;
    }

    // Принудительное обновление профиля пользователя
    public void refreshUserProfile(ProfileRefreshCallback callback) {
        String accessToken = prefsManager.getAccessToken();

        if (accessToken == null || accessToken.isEmpty()) {
            Log.w(TAG, "Не могу обновить профиль: токен отсутствует");
            if (callback != null) {
                callback.onFailure("Пользователь не авторизован");
            }
            return;
        }

        AuthApi authApi = RetrofitClient.getClient().create(AuthApi.class);
        Call<UserResponseDto> call = authApi.getCurrentUser("Bearer " + accessToken);

        call.enqueue(new Callback<UserResponseDto>() {
            @Override
            public void onResponse(Call<UserResponseDto> call, Response<UserResponseDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserResponseDto user = response.body();

                    // Сохраняем все данные пользователя
                    prefsManager.saveUserData(
                            accessToken,
                            prefsManager.getRefreshToken(),
                            user.getUserId(),
                            user.getRole(),
                            user.getEmail(),
                            user.getFirstName(),
                            user.getLastName(),
                            user.getPhone()
                    );

                    Log.i(TAG, "Профиль пользователя обновлен: " + user.getFirstName() + " " + user.getLastName());

                    if (callback != null) {
                        callback.onSuccess(user);
                    }

                } else {
                    Log.e(TAG, "Ошибка при обновлении профиля. Код: " + response.code());
                    if (callback != null) {
                        callback.onFailure("Ошибка сервера: " + response.code());
                    }
                }
            }

            @Override
            public void onFailure(Call<UserResponseDto> call, Throwable t) {
                Log.e(TAG, "Ошибка сети при обновлении профиля: " + t.getMessage());
                if (callback != null) {
                    callback.onFailure("Ошибка сети: " + t.getMessage());
                }
            }
        });
    }

    // Проверить, нужно ли обновить профиль (например, если прошло больше суток)
    public boolean shouldRefreshProfile() {
        // Здесь можно добавить логику, например:
        // - Обновлять раз в день
        // - Обновлять при каждом запуске приложения
        // - Хранить timestamp последнего обновления

        // Пока всегда возвращаем true для тестирования
        return true;
    }

    // Обновить профиль, если нужно
    public void refreshProfileIfNeeded() {
        if (shouldRefreshProfile()) {
            refreshUserProfile(null); // Без колбэка
        }
    }

    // Интерфейс для колбэков
    public interface ProfileRefreshCallback {
        void onSuccess(UserResponseDto user);
        void onFailure(String error);
    }
}