package com.example.nails_salon_mobile.data.local.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.nails_salon_mobile.data.local.converter.BigDecimalConverter;
import com.example.nails_salon_mobile.data.local.converter.LocalDateTimeConverter;

import org.threeten.bp.LocalDateTime;

import java.math.BigDecimal;

@Entity(tableName = "appointments")
@TypeConverters({LocalDateTimeConverter.class, BigDecimalConverter.class})
public class AppointmentEntity {

    @PrimaryKey
    @ColumnInfo(name = "appointment_id")
    private Long appointmentId;

    @ColumnInfo(name = "client_id")
    private Long clientId;

    @ColumnInfo(name = "client_name")
    private String clientName;

    @ColumnInfo(name = "master_id")
    private Long masterId;

    @ColumnInfo(name = "master_name")
    private String masterName;

    @ColumnInfo(name = "service_id")
    private Long serviceId;

    @ColumnInfo(name = "service_name")
    private String serviceName;

    @ColumnInfo(name = "appointment_datetime")
    private LocalDateTime appointmentDatetime;

    @ColumnInfo(name = "end_datetime")
    private LocalDateTime endDatetime;

    @ColumnInfo(name = "price")
    private BigDecimal price;

    @ColumnInfo(name = "status")
    private String status;

    @ColumnInfo(name = "notes")
    private String notes;

    @ColumnInfo(name = "created_at")
    private LocalDateTime createdAt;

    @ColumnInfo(name = "is_synced")
    private boolean isSynced = true; // true если синхронизировано с сервером

    @ColumnInfo(name = "created_locally")
    private boolean createdLocally = false; // true если создана оффлайн

    // Конструкторы
    public AppointmentEntity() {}

    // Геттеры и сеттеры
    public Long getAppointmentId() { return appointmentId; }
    public void setAppointmentId(Long appointmentId) { this.appointmentId = appointmentId; }

    public Long getClientId() { return clientId; }
    public void setClientId(Long clientId) { this.clientId = clientId; }

    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }

    public Long getMasterId() { return masterId; }
    public void setMasterId(Long masterId) { this.masterId = masterId; }

    public String getMasterName() { return masterName; }
    public void setMasterName(String masterName) { this.masterName = masterName; }

    public Long getServiceId() { return serviceId; }
    public void setServiceId(Long serviceId) { this.serviceId = serviceId; }

    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }

    public LocalDateTime getAppointmentDatetime() { return appointmentDatetime; }
    public void setAppointmentDatetime(LocalDateTime appointmentDatetime) { this.appointmentDatetime = appointmentDatetime; }

    public LocalDateTime getEndDatetime() { return endDatetime; }
    public void setEndDatetime(LocalDateTime endDatetime) { this.endDatetime = endDatetime; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public boolean isSynced() { return isSynced; }
    public void setSynced(boolean synced) { isSynced = synced; }

    public boolean isCreatedLocally() { return createdLocally; }
    public void setCreatedLocally(boolean createdLocally) { this.createdLocally = createdLocally; }
}