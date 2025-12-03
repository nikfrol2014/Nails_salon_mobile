package com.example.nails_salon_mobile.data.remote.api;

import com.example.nails_salon_mobile.data.remote.models.MasterDto;
import com.example.nails_salon_mobile.data.remote.models.MasterServiceResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface MastersApi {

    // Получить всех активных мастеров
    @GET("api/v1/masters/active")
    Call<List<MasterDto>> getActiveMasters();

    // Получить мастеров для услуги (из MasterServiceController)
    @GET("api/v1/master-services/service/{serviceId}")
    Call<List<MasterServiceResponse>> getServiceMasters(@Path("serviceId") Long serviceId);

    // Получить услуги мастера
    @GET("api/v1/master-services/master/{masterId}")
    Call<List<MasterServiceResponse>> getMasterServices(@Path("masterId") Long masterId);

    // Получить мастера по ID (если есть такой эндпоинт)
    @GET("api/v1/masters/{masterId}")
    Call<MasterDto> getMasterById(@Path("masterId") Long masterId);
}