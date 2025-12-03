package com.example.nails_salon_mobile.data.repository;

import android.content.Context;
import android.util.Log;

import com.example.nails_salon_mobile.data.remote.RetrofitClient;
import com.example.nails_salon_mobile.data.remote.api.MastersApi;
import com.example.nails_salon_mobile.data.remote.models.MasterDto;
import com.example.nails_salon_mobile.data.remote.models.MasterServiceResponse;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MasterRepository {

    private static final String TAG = "MasterRepository";
    private static MasterRepository instance;

    private final MastersApi mastersApi;
    private final ExecutorService executorService;

    private MasterRepository(Context context) {
        mastersApi = RetrofitClient.getClient().create(MastersApi.class);
        executorService = Executors.newSingleThreadExecutor();
    }

    public static synchronized MasterRepository getInstance(Context context) {
        if (instance == null) {
            instance = new MasterRepository(context);
        }
        return instance;
    }

    // Получить активных мастеров
    public interface MastersCallback {
        void onSuccess(List<MasterDto> masters);
        void onError(String error);
    }

    public void getActiveMasters(MastersCallback callback) {
        executorService.execute(() -> {
            try {
                retrofit2.Response<List<MasterDto>> response =
                        mastersApi.getActiveMasters().execute();

                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Не удалось загрузить мастеров");
                }
            } catch (Exception e) {
                Log.e(TAG, "Error getting masters", e);
                callback.onError("Ошибка: " + e.getMessage());
            }
        });
    }

    // Получить услуги мастера
    public interface MasterServicesCallback {
        void onSuccess(List<MasterServiceResponse> services);
        void onError(String error);
    }

    public void getMasterServices(Long masterId, MasterServicesCallback callback) {
        executorService.execute(() -> {
            try {
                retrofit2.Response<List<MasterServiceResponse>> response =
                        mastersApi.getMasterServices(masterId).execute();

                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Не удалось загрузить услуги мастера");
                }
            } catch (Exception e) {
                Log.e(TAG, "Error getting master services", e);
                callback.onError("Ошибка: " + e.getMessage());
            }
        });
    }

    public void getServiceMasters(Long serviceId, MasterServicesCallback callback) {
        executorService.execute(() -> {
            try {
                retrofit2.Response<List<MasterServiceResponse>> response =
                        mastersApi.getServiceMasters(serviceId).execute();

                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Не удалось загрузить мастеров для услуги");
                }
            } catch (Exception e) {
                Log.e(TAG, "Error getting service masters", e);
                callback.onError("Ошибка: " + e.getMessage());
            }
        });
    }

    // Освобождение ресурсов
    public void shutdown() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}