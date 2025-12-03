package com.example.nails_salon_mobile.data.repository;

import android.content.Context;
import android.util.Log;

import com.example.nails_salon_mobile.data.local.AppDatabase;
import com.example.nails_salon_mobile.data.local.DatabaseClient;
import com.example.nails_salon_mobile.data.local.dao.CategoryDao;
import com.example.nails_salon_mobile.data.local.entities.CategoryEntity;
import com.example.nails_salon_mobile.data.remote.RetrofitClient;
import com.example.nails_salon_mobile.data.remote.api.CategoriesApi;
import com.example.nails_salon_mobile.data.remote.models.ServiceCategory;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class CategoryRepository {

    private static final String TAG = "CategoryRepository";
    private static CategoryRepository instance;

    private final CategoriesApi categoriesApi;
    private final CategoryDao categoryDao;
    private final ExecutorService executorService;

    private CategoryRepository(Context context) {
        categoriesApi = RetrofitClient.getClient().create(CategoriesApi.class);

        AppDatabase database = DatabaseClient.getInstance(context).getAppDatabase();
        categoryDao = database.categoryDao();

        executorService = Executors.newSingleThreadExecutor();
    }

    public static synchronized CategoryRepository getInstance(Context context) {
        if (instance == null) {
            instance = new CategoryRepository(context);
        }
        return instance;
    }

    // Получить все категории
    public interface CategoriesCallback {
        void onSuccess(List<CategoryEntity> categories);
        void onError(String error);
    }

    public void getAllCategories(CategoriesCallback callback) {
        executorService.execute(() -> {
            try {
                // Сначала из БД
                List<CategoryEntity> cachedCategories = categoryDao.getAll();
                if (!cachedCategories.isEmpty()) {
                    callback.onSuccess(cachedCategories);
                }

                // Затем из API
                retrofit2.Response<List<ServiceCategory>> response =
                        categoriesApi.getAllCategories().execute();

                if (response.isSuccessful() && response.body() != null) {
                    List<CategoryEntity> categories = response.body().stream()
                            .map(this::convertToEntity)
                            .collect(Collectors.toList());

                    // Обновляем БД
                    categoryDao.deleteAll();
                    categoryDao.insertAll(categories);

                    callback.onSuccess(categories);

                } else if (cachedCategories.isEmpty()) {
                    callback.onError("Не удалось загрузить категории");
                }

            } catch (Exception e) {
                Log.e(TAG, "Error loading categories", e);
                List<CategoryEntity> cachedCategories = categoryDao.getAll();
                if (!cachedCategories.isEmpty()) {
                    callback.onSuccess(cachedCategories);
                } else {
                    callback.onError("Ошибка: " + e.getMessage());
                }
            }
        });
    }

    // Получить категорию по ID
    public interface CategoryCallback {
        void onSuccess(CategoryEntity category);
        void onError(String error);
    }

    public void getCategoryById(Long categoryId, CategoryCallback callback) {
        executorService.execute(() -> {
            try {
                CategoryEntity category = categoryDao.getById(categoryId);
                if (category != null) {
                    callback.onSuccess(category);
                } else {
                    // Если нет в БД, грузим с API
                    retrofit2.Response<ServiceCategory> response =
                            categoriesApi.getCategoryById(categoryId).execute();

                    if (response.isSuccessful() && response.body() != null) {
                        CategoryEntity newCategory = convertToEntity(response.body());
                        categoryDao.insert(newCategory);
                        callback.onSuccess(newCategory);
                    } else {
                        callback.onError("Категория не найдена");
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error getting category by id", e);
                callback.onError("Ошибка: " + e.getMessage());
            }
        });
    }

    // Конвертер
    private CategoryEntity convertToEntity(ServiceCategory dto) {
        CategoryEntity entity = new CategoryEntity();
        entity.setCategoryId(dto.getCategoryId());
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setSortOrder(dto.getSortOrder());
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