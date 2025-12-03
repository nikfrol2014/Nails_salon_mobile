package com.example.nails_salon_mobile.data.remote.models;

import com.google.gson.annotations.SerializedName;
import org.threeten.bp.LocalDateTime;

public class TimeSlotResponse {

    @SerializedName("startTime")
    private LocalDateTime startTime;

    @SerializedName("endTime")
    private LocalDateTime endTime;

    @SerializedName("available")
    private boolean available;

    @SerializedName("slotNumber")
    private Integer slotNumber;

    // Конструкторы
    public TimeSlotResponse() {}

    public TimeSlotResponse(LocalDateTime startTime, LocalDateTime endTime, boolean available, Integer slotNumber) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.available = available;
        this.slotNumber = slotNumber;
    }

    // Геттеры и сеттеры
    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public Integer getSlotNumber() {
        return slotNumber;
    }

    public void setSlotNumber(Integer slotNumber) {
        this.slotNumber = slotNumber;
    }

    // Вспомогательный метод для получения времени в формате строки
    public String getTimeRange() {
        if (slotNumber != null) {
            // Маппинг слотов на время (как в TimeSlotEnum)
            switch (slotNumber) {
                case 1: return "09:00-10:00";
                case 2: return "10:00-11:00";
                case 3: return "11:00-12:00";
                case 4: return "12:00-13:00";
                case 5: return "13:00-14:00";
                case 6: return "14:00-15:00";
                case 7: return "15:00-16:00";
                case 8: return "16:00-17:00";
                case 9: return "17:00-18:00";
                case 10: return "18:00-19:00";
                default: return "Неизвестное время";
            }
        }
        return "Время не указано";
    }
}