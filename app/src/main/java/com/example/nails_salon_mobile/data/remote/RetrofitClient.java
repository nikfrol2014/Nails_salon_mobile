package com.example.nails_salon_mobile.data.remote;

import android.content.Context;
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
//    private static final String BASE_URL = "https://nails-salon-back.whysargis.ru/";
    private static final String BASE_URL = "http://87.242.87.228:3000/";
    private static Retrofit retrofit = null;
    private static Context context;

    public static void init(Context appContext) {
        context = appContext;
    }

    public static Retrofit getClient() {
        if (retrofit == null) {
            // Логирование запросов
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            // Интерцептор для добавления токена
            Interceptor authInterceptor = new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request originalRequest = chain.request();

                    // Получаем токен из SharedPreferences
                    String token = SharedPrefsManager.getInstance(context).getAccessToken();

                    Request newRequest;
                    if (token != null && !token.isEmpty()) {
                        // Добавляем заголовок Authorization
                        newRequest = originalRequest.newBuilder()
                                .header("Authorization", "Bearer " + token)
                                .build();
                    } else {
                        newRequest = originalRequest;
                    }

                    return chain.proceed(newRequest);
                }
            };

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .addInterceptor(authInterceptor)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}