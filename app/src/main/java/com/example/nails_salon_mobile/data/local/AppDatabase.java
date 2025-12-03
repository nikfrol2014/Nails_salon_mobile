package com.example.nails_salon_mobile.data.local;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.nails_salon_mobile.data.local.converter.BigDecimalConverter;
import com.example.nails_salon_mobile.data.local.converter.LocalDateTimeConverter;
import com.example.nails_salon_mobile.data.local.converter.LocalTimeConverter;
import com.example.nails_salon_mobile.data.local.dao.AppointmentDao;
import com.example.nails_salon_mobile.data.local.dao.CategoryDao;
import com.example.nails_salon_mobile.data.local.dao.ServiceDao;
import com.example.nails_salon_mobile.data.local.entities.AppointmentEntity;
import com.example.nails_salon_mobile.data.local.entities.CategoryEntity;
import com.example.nails_salon_mobile.data.local.entities.ServiceEntity;

@Database(
        entities = {
                ServiceEntity.class,
                CategoryEntity.class,
                AppointmentEntity.class
        },
        version = 1,
        exportSchema = false
)
@TypeConverters({
        LocalDateTimeConverter.class,
        LocalTimeConverter.class,
        BigDecimalConverter.class
})
public abstract class AppDatabase extends RoomDatabase {

    // DAO интерфейсы
    public abstract ServiceDao serviceDao();
    public abstract CategoryDao categoryDao();
    public abstract AppointmentDao appointmentDao();

    // Имя базы данных
    public static final String DATABASE_NAME = "nails_salon_db";
}