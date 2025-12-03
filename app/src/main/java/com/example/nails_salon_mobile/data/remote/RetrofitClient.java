package com.example.nails_salon_mobile.data.remote;

import android.content.Context;
import android.util.Log;

import com.example.nails_salon_mobile.utils.SharedPrefsManager;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;

public class RetrofitClient {

    private static final String TAG = "RetrofitClient";
    private static final String BASE_URL = "http://87.242.87.228:3000/";
    private static Retrofit retrofit = null;
    private static Context appContext = null;

    // ЯВНАЯ ИНИЦИАЛИЗАЦИЯ (обязательно вызывать в Application или первой Activity)
    public static void init(Context context) {
        if (appContext == null) {
            appContext = context.getApplicationContext();
            Log.d(TAG, "RetrofitClient инициализирован с контекстом");
        }
    }

    // Проверка инициализации
    private static void checkInitialized() {
        if (appContext == null) {
            throw new IllegalStateException("RetrofitClient не инициализирован. " +
                    "Вызовите RetrofitClient.init(context) в Application или первой Activity");
        }
    }

    public static Retrofit getClient() {
        checkInitialized();

        if (retrofit == null) {
            synchronized (RetrofitClient.class) {
                if (retrofit == null) {
                    retrofit = createRetrofit();
                }
            }
        }
        return retrofit;
    }

    private static Retrofit createRetrofit() {
        Log.d(TAG, "Создание Retrofit клиента...");

        // Логирование запросов
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        // Интерцептор для добавления токена
        Interceptor authInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request originalRequest = chain.request();

                // Получаем токен из SharedPreferences
                String token = SharedPrefsManager.getInstance(appContext).getAccessToken();

                Log.d(TAG, "Токен для запроса " + originalRequest.url() + ": " +
                        (token != null ? "есть (" + token.length() + " chars)" : "null"));

                Request.Builder requestBuilder = originalRequest.newBuilder();

                // Добавляем заголовок Authorization если есть токен
                if (token != null && !token.isEmpty()) {
                    requestBuilder.header("Authorization", "Bearer " + token);
                    Log.d(TAG, "Добавлен Authorization header");
                } else {
                    Log.w(TAG, "Токен отсутствует, запрос без авторизации");
                }

                // Добавляем accept header (как в Swagger)
                requestBuilder.header("accept", "*/*");

                Request newRequest = requestBuilder.build();
                return chain.proceed(newRequest);
            }
        };

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .addInterceptor(authInterceptor)
                .build();

        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    // Метод для принудительного пересоздания (например, после смены токена)
    public static void reset() {
        synchronized (RetrofitClient.class) {
            retrofit = null;
            Log.d(TAG, "Retrofit клиент сброшен");
        }
    }
}