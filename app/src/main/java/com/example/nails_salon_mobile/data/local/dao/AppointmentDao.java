package com.example.nails_salon_mobile.data.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.nails_salon_mobile.data.local.entities.AppointmentEntity;

import java.util.List;

@Dao
public interface AppointmentDao {

    // Вставить или заменить запись
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(AppointmentEntity appointment);

    // Вставить несколько записей
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<AppointmentEntity> appointments);

    // Обновить запись
    @Update
    void update(AppointmentEntity appointment);

    // Удалить запись
    @Delete
    void delete(AppointmentEntity appointment);

    // Получить все записи
    @Query("SELECT * FROM appointments ORDER BY appointment_datetime DESC")
    List<AppointmentEntity> getAll();

    // Получить запись по ID
    @Query("SELECT * FROM appointments WHERE appointment_id = :id")
    AppointmentEntity getById(Long id);

    // Получить записи клиента
    @Query("SELECT * FROM appointments WHERE client_id = :clientId ORDER BY appointment_datetime DESC")
    List<AppointmentEntity> getByClientId(Long clientId);

    // Получить неподтвержденные (оффлайн) записи
    @Query("SELECT * FROM appointments WHERE is_synced = 0 ORDER BY created_at DESC")
    List<AppointmentEntity> getUnsyncedAppointments();

    // Получить записи по статусу
    @Query("SELECT * FROM appointments WHERE status = :status ORDER BY appointment_datetime DESC")
    List<AppointmentEntity> getByStatus(String status);

    // Получить будущие записи
    @Query("SELECT * FROM appointments WHERE appointment_datetime > datetime('now') ORDER BY appointment_datetime ASC")
    List<AppointmentEntity> getUpcomingAppointments();

    // Удалить все записи
    @Query("DELETE FROM appointments")
    void deleteAll();

    // Получить количество записей
    @Query("SELECT COUNT(*) FROM appointments")
    int count();

    // Проверить, существует ли запись
    @Query("SELECT COUNT(*) FROM appointments WHERE appointment_id = :id")
    int exists(Long id);
}