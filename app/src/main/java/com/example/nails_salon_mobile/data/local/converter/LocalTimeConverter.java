package com.example.nails_salon_mobile.data.local.converter;

import androidx.room.TypeConverter;
import org.threeten.bp.LocalTime;
import org.threeten.bp.format.DateTimeFormatter;

public class LocalTimeConverter {

    private static final DateTimeFormatter formatter =
            DateTimeFormatter.ISO_LOCAL_TIME;

    @TypeConverter
    public static LocalTime toLocalTime(String value) {
        return value == null ? null : LocalTime.parse(value, formatter);
    }

    @TypeConverter
    public static String fromLocalTime(LocalTime time) {
        return time == null ? null : time.format(formatter);
    }
}