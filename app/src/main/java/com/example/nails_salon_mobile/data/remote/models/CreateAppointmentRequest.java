package com.example.nails_salon_mobile.data.remote.models;

import com.google.gson.annotations.SerializedName;

import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;

public class CreateAppointmentRequest {

    @SerializedName("clientId")
    private Long clientId;

    @SerializedName("masterId")
    private Long masterId;

    @SerializedName("serviceId")
    private Long serviceId;

    @SerializedName("appointmentDate")
    private String appointmentDate; // ИЗМЕНИЛИ: String вместо LocalDate

    @SerializedName("timeSlot")
    private Integer timeSlot;

    @SerializedName("notes")
    private String notes;

    // Конструкторы
    public CreateAppointmentRequest() {}

    // Новый конструктор с LocalDate
    public CreateAppointmentRequest(Long clientId, Long masterId, Long serviceId,
                                    LocalDate appointmentDate, Integer timeSlot, String notes) {
        this.clientId = clientId;
        this.masterId = masterId;
        this.serviceId = serviceId;
        setAppointmentDate(appointmentDate); // Используем сеттер
        this.timeSlot = timeSlot;
        this.notes = notes;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public Long getMasterId() {
        return masterId;
    }

    public void setMasterId(Long masterId) {
        this.masterId = masterId;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    // Геттер возвращает LocalDate (для удобства)
    public LocalDate getAppointmentDateAsLocalDate() {
        if (appointmentDate == null) return null;
        return LocalDate.parse(appointmentDate, DateTimeFormatter.ISO_LOCAL_DATE);
    }

    // Сеттер принимает LocalDate, сохраняет как строку
    public void setAppointmentDate(LocalDate date) {
        if (date != null) {
            this.appointmentDate = date.format(DateTimeFormatter.ISO_LOCAL_DATE);
        } else {
            this.appointmentDate = null;
        }
    }

    // Геттер/сеттер для строки (для GSON)
    public String getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(String appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public Integer getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(Integer timeSlot) {
        this.timeSlot = timeSlot;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}