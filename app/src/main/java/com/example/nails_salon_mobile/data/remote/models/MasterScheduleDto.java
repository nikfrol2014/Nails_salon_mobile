package com.example.nails_salon_mobile.data.remote.models;

import com.google.gson.annotations.SerializedName;
import org.threeten.bp.LocalTime;

public class MasterScheduleDto {

    @SerializedName("scheduleId")
    private Long scheduleId;

    @SerializedName("masterId")
    private Long masterId;

    @SerializedName("dayOfWeek")
    private Short dayOfWeek; // 1-Понедельник, 7-Воскресенье

    @SerializedName("startTime")
    private LocalTime startTime;

    @SerializedName("endTime")
    private LocalTime endTime;

    // Конструкторы
    public MasterScheduleDto() {}

    public MasterScheduleDto(Long scheduleId, Long masterId, Short dayOfWeek,
                             LocalTime startTime, LocalTime endTime) {
        this.scheduleId = scheduleId;
        this.masterId = masterId;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // Геттеры и сеттеры
    public Long getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Long scheduleId) {
        this.scheduleId = scheduleId;
    }

    public Long getMasterId() {
        return masterId;
    }

    public void setMasterId(Long masterId) {
        this.masterId = masterId;
    }

    public Short getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(Short dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    // Вспомогательный метод для получения названия дня недели
    public String getDayOfWeekName() {
        switch (dayOfWeek) {
            case 1: return "Понедельник";
            case 2: return "Вторник";
            case 3: return "Среда";
            case 4: return "Четверг";
            case 5: return "Пятница";
            case 6: return "Суббота";
            case 7: return "Воскресенье";
            default: return "Неизвестный день";
        }
    }

    // Вспомогательный метод для получения времени работы в формате строки
    public String getWorkingHours() {
        return startTime.toString() + " - " + endTime.toString();
    }
}