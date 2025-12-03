package com.example.nails_salon_mobile.data.remote.models;

import com.google.gson.annotations.SerializedName;
import java.math.BigDecimal;

public class MasterServiceResponse {

    @SerializedName("masterId")
    private Long masterId;

    @SerializedName("masterName")
    private String masterName;

    @SerializedName("serviceId")
    private Long serviceId;

    @SerializedName("serviceName")
    private String serviceName;

    @SerializedName("masterPrice")
    private BigDecimal masterPrice;

    // Конструкторы
    public MasterServiceResponse() {}

    // Геттеры и сеттеры
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

    public BigDecimal getMasterPrice() {
        return masterPrice;
    }

    public void setMasterPrice(BigDecimal masterPrice) {
        this.masterPrice = masterPrice;
    }
}