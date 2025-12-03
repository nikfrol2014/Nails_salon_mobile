package com.example.nails_salon_mobile.data.local;

import android.content.Context;

import androidx.room.Room;

public class DatabaseClient {

    private static DatabaseClient instance;
    private AppDatabase appDatabase;

    private DatabaseClient(Context context) {
        // Создаем базу данных
        appDatabase = Room.databaseBuilder(
                        context.getApplicationContext(),
                        AppDatabase.class,
                        AppDatabase.DATABASE_NAME
                )
                .fallbackToDestructiveMigration() // При изменении версии - пересоздать БД
                .build();
    }

    public static synchronized DatabaseClient getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseClient(context);
        }
        return instance;
    }

    public AppDatabase getAppDatabase() {
        return appDatabase;
    }

    // Закрыть соединение (вызывать в onDestroy)
    public void close() {
        if (appDatabase != null && appDatabase.isOpen()) {
            appDatabase.close();
        }
        instance = null;
    }
}