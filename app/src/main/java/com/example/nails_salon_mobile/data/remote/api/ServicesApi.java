package com.example.nails_salon_mobile.data.remote.api;

import com.example.nails_salon_mobile.data.remote.models.NailServiceDto;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ServicesApi {
    @GET("api/v1/services")
    Call<List<NailServiceDto>> getAllServices();

    @GET("api/v1/services/{serviceId}")
    Call<NailServiceDto> getServiceById(@Path("serviceId") Long serviceId);

    @GET("api/v1/services/category/{categoryId}")
    Call<List<NailServiceDto>> getServicesByCategory(@Path("categoryId") Long categoryId);

    @GET("api/v1/services/search")
    Call<List<NailServiceDto>> searchServices(@Query("query") String query);
}