package com.example.nails_salon_mobile.data.remote.models;

import com.google.gson.annotations.SerializedName;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.math.BigDecimal;

public class AppointmentResponse {

    @SerializedName("appointmentId")
    private Long appointmentId;

    @SerializedName("clientId")
    private Long clientId;

    @SerializedName("clientName")
    private String clientName;

    @SerializedName("masterId")
    private Long masterId;

    @SerializedName("masterName")
    private String masterName;

    @SerializedName("serviceId")
    private Long serviceId;

    @SerializedName("serviceName")
    private String serviceName;

    @SerializedName("appointmentDatetime")
    private String appointmentDatetimeString; // String вместо LocalDateTime

    @SerializedName("endDatetime")
    private String endDatetimeString;

    @SerializedName("price")
    private BigDecimal price;

    @SerializedName("status")
    private String status;

    @SerializedName("notes")
    private String notes;

    @SerializedName("createdAt")
    private String createdAtString;

    // Конструкторы
    public AppointmentResponse() {}

    // Геттеры и сеттеры
    public Long getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public Long getMasterId() {
        return masterId;
    }

    public void setMasterId(Long masterId) {
        this.masterId = masterId;
    }

    public String getMasterName() {
        return masterName;
    }

    public void setMasterName(String masterName) {
        this.masterName = masterName;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getAppointmentDatetimeString() { return appointmentDatetimeString; }

    public void setAppointmentDatetimeString(String appointmentDatetimeString) {
        this.appointmentDatetimeString = appointmentDatetimeString;
    }

    public String getEndDatetimeString() { return endDatetimeString; }

    public void setEndDatetimeString(String endDatetimeString) {
        this.endDatetimeString = endDatetimeString;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getCreatedAtString() { return createdAtString; }

    public void setCreatedAtString(String createdAtString) {
        this.createdAtString = createdAtString;
    }

    // Геттеры для LocalDateTime (конвертация при необходимости)
    public LocalDateTime getAppointmentDatetime() {
        if (appointmentDatetimeString != null) {
            return LocalDateTime.parse(appointmentDatetimeString, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        }
        return null;
    }

    public LocalDateTime getEndDatetime() {
        if (endDatetimeString != null) {
            return LocalDateTime.parse(endDatetimeString, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        }
        return null;
    }

    public LocalDateTime getCreatedAt() {
        if (createdAtString != null) {
            return LocalDateTime.parse(createdAtString, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        }
        return null;
    }

    public void setAppointmentDatetime(LocalDateTime dateTime) {
        this.appointmentDatetimeString = dateTime != null ?
                dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null;
    }
}