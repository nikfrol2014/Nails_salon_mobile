package com.example.nails_salon_mobile.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.nails_salon_mobile.data.local.entities.CategoryEntity;

import java.util.List;

@Dao
public interface CategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(CategoryEntity category);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<CategoryEntity> categories);

    // Получить все категории
    @Query("SELECT * FROM categories ORDER BY sort_order ASC, name ASC")
    List<CategoryEntity> getAll();

    // Получить категорию по ID
    @Query("SELECT * FROM categories WHERE category_id = :id")
    CategoryEntity getById(Long id);

    // Удалить все категории
    @Query("DELETE FROM categories")
    void deleteAll();

    // Получить количество категорий
    @Query("SELECT COUNT(*) FROM categories")
    int count();
}