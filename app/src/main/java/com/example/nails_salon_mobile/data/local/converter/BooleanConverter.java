package com.example.nails_salon_mobile.data.local.converter;

import androidx.room.TypeConverter;

public class BooleanConverter {

    @TypeConverter
    public static Boolean toBoolean(Integer value) {
        return value == null ? null : value == 1;
    }

    @TypeConverter
    public static Integer fromBoolean(Boolean value) {
        return value == null ? null : (value ? 1 : 0);
    }
}