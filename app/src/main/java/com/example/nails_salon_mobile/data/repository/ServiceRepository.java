package com.example.nails_salon_mobile.data.repository;

import android.content.Context;
import android.util.Log;

import com.example.nails_salon_mobile.data.local.AppDatabase;
import com.example.nails_salon_mobile.data.local.DatabaseClient;
import com.example.nails_salon_mobile.data.local.dao.ServiceDao;
import com.example.nails_salon_mobile.data.local.entities.ServiceEntity;
import com.example.nails_salon_mobile.data.remote.RetrofitClient;
import com.example.nails_salon_mobile.data.remote.api.ServicesApi;
import com.example.nails_salon_mobile.data.remote.models.NailServiceDto;
import com.google.gson.Gson;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class ServiceRepository {

    private static final String TAG = "ServiceRepository";
    private static ServiceRepository instance;

    private final ServicesApi servicesApi;
    private final ServiceDao serviceDao;
    private final ExecutorService executorService;

    private ServiceRepository(Context context) {
        servicesApi = RetrofitClient.getClient().create(ServicesApi.class);

        AppDatabase database = DatabaseClient.getInstance(context).getAppDatabase();
        serviceDao = database.serviceDao();

        executorService = Executors.newSingleThreadExecutor();
    }

    public static synchronized ServiceRepository getInstance(Context context) {
        if (instance == null) {
            instance = new ServiceRepository(context);
        }
        return instance;
    }

    // Получить все услуги (сначала из БД, потом обновить из API)
    public interface ServicesCallback {
        void onSuccess(List<ServiceEntity> services);
        void onError(String error);
    }

    public void getAllServices(ServicesCallback callback) {
        executorService.execute(() -> {
            try {
                // 1. Сначала показываем данные из БД
                List<ServiceEntity> cachedServices = serviceDao.getAll();
                if (!cachedServices.isEmpty()) {
                    callback.onSuccess(cachedServices);
                }

                // 2. Загружаем свежие данные с API
                retrofit2.Response<List<NailServiceDto>> response =
                        servicesApi.getAllServices().execute();

                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "API Response: " + new Gson().toJson(response.body()));
                    List<ServiceEntity> services = response.body().stream()
                            .map(this::convertToEntity)
                            .collect(Collectors.toList());

                    // Сохраняем в БД
                    serviceDao.deleteAll();
                    serviceDao.insertAll(services);

                    // Возвращаем обновленные данные
                    callback.onSuccess(services);
                } else {
                    if (cachedServices.isEmpty()) {
                        callback.onError("Не удалось загрузить услуги");
                    }
                }

            } catch (Exception e) {
                Log.e(TAG, "Error loading services", e);
                executorService.execute(() -> {
                    List<ServiceEntity> cachedServices = serviceDao.getAll();
                    if (!cachedServices.isEmpty()) {
                        callback.onSuccess(cachedServices);
                    } else {
                        callback.onError("Ошибка загрузки: " + e.getMessage());
                    }
                });
            }
        });
    }

    // Получить услуги по категории
    public void getServicesByCategory(Long categoryId, ServicesCallback callback) {
        executorService.execute(() -> {
            try {
                List<ServiceEntity> services = serviceDao.getByCategoryId(categoryId);
                callback.onSuccess(services);
            } catch (Exception e) {
                Log.e(TAG, "Error getting services by category", e);
                callback.onError("Ошибка: " + e.getMessage());
            }
        });
    }

    // Поиск услуг
    public void searchServices(String query, ServicesCallback callback) {
        executorService.execute(() -> {
            try {
                List<ServiceEntity> services = serviceDao.search(query);
                callback.onSuccess(services);
            } catch (Exception e) {
                Log.e(TAG, "Error searching services", e);
                callback.onError("Ошибка поиска: " + e.getMessage());
            }
        });
    }

    // Получить избранные услуги
    public void getFavoriteServices(ServicesCallback callback) {
        executorService.execute(() -> {
            try {
                List<ServiceEntity> services = serviceDao.getFavorites();
                callback.onSuccess(services);
            } catch (Exception e) {
                Log.e(TAG, "Error getting favorites", e);
                callback.onError("Ошибка: " + e.getMessage());
            }
        });
    }

    // Обновить статус избранного
    public void toggleFavorite(Long serviceId, boolean isFavorite) {
        executorService.execute(() -> {
            try {
                serviceDao.updateFavoriteStatus(serviceId, isFavorite);
            } catch (Exception e) {
                Log.e(TAG, "Error updating favorite", e);
            }
        });
    }

    // Получить услугу по ID
    public interface ServiceCallback {
        void onSuccess(ServiceEntity service);
        void onError(String error);
    }

    public void getServiceById(Long serviceId, ServiceCallback callback) {
        executorService.execute(() -> {
            try {
                // Сначала проверяем БД
                ServiceEntity service = serviceDao.getById(serviceId);
                if (service != null) {
                    callback.onSuccess(service);
                } else {
                    // Если нет в БД, грузим с API
                    retrofit2.Response<NailServiceDto> response =
                            servicesApi.getServiceById(serviceId).execute();

                    if (response.isSuccessful() && response.body() != null) {
                        Log.d(TAG, "API Response: " + new Gson().toJson(response.body()));
                        ServiceEntity newService = convertToEntity(response.body());
                        serviceDao.insert(newService);
                        callback.onSuccess(newService);
                    } else {
                        callback.onError("Услуга не найдена");
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error getting service by id", e);
                callback.onError("Ошибка: " + e.getMessage());
            }
        });
    }

    // Конвертер DTO -> Entity
    private ServiceEntity convertToEntity(NailServiceDto dto) {
        ServiceEntity entity = new ServiceEntity();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setPrice(dto.getPrice());
        entity.setDurationMinutes(dto.getDurationMinutes());
        entity.setCategoryId(dto.getCategoryId());
        entity.setCategoryName(dto.getCategoryName());

        // ИСПРАВЛЯЕМ: проверяем на null
        Boolean active = dto.getActive();
        entity.setActive(active != null ? active : true); // по умолчанию true

        entity.setLastUpdated(System.currentTimeMillis());
        return entity;
    }

    // Освобождение ресурсов
    public void shutdown() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}