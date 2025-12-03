package com.example.nails_salon_mobile.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.nails_salon_mobile.data.local.entities.ServiceEntity;

import java.util.List;

@Dao
public interface ServiceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ServiceEntity service);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<ServiceEntity> services);

    @Update
    void update(ServiceEntity service);

    // Получить все услуги
    @Query("SELECT * FROM services WHERE is_active = 1 ORDER BY name ASC")
    List<ServiceEntity> getAll();

    // Получить услугу по ID
    @Query("SELECT * FROM services WHERE service_id = :id")
    ServiceEntity getById(Long id);

    // Получить услуги по категории
    @Query("SELECT * FROM services WHERE category_id = :categoryId AND is_active = 1 ORDER BY name ASC")
    List<ServiceEntity> getByCategoryId(Long categoryId);

    // Получить избранные услуги
    @Query("SELECT * FROM services WHERE is_favorite = 1 AND is_active = 1 ORDER BY name ASC")
    List<ServiceEntity> getFavorites();

    // Поиск услуг
    @Query("SELECT * FROM services WHERE (name LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%') AND is_active = 1 ORDER BY name ASC")
    List<ServiceEntity> search(String query);

    // Обновить статус избранного
    @Query("UPDATE services SET is_favorite = :isFavorite WHERE service_id = :serviceId")
    void updateFavoriteStatus(Long serviceId, boolean isFavorite);

    // Получить все категории (уникальные)
    @Query("SELECT DISTINCT category_id, category_name FROM services WHERE category_name IS NOT NULL ORDER BY category_name ASC")
    List<CategoryInfo> getCategories();

    // Удалить все услуги
    @Query("DELETE FROM services")
    void deleteAll();

    // Проверить, устарели ли данные
    @Query("SELECT COUNT(*) FROM services WHERE last_updated < :timestamp")
    int countOutdated(Long timestamp);

    // Класс для получения информации о категориях
    static class CategoryInfo {
        public Long category_id;
        public String category_name;
    }
}