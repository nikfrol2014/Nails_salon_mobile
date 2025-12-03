package com.example.nails_salon_mobile.data.remote.api;

import com.example.nails_salon_mobile.data.remote.models.AppointmentResponse;
import com.example.nails_salon_mobile.data.remote.models.CreateAppointmentRequest;
import com.example.nails_salon_mobile.data.remote.models.TimeSlotResponse;

import org.threeten.bp.LocalDate;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface BookingsApi {

    // Создать новую запись
    @POST("api/v1/appointments")
    Call<AppointmentResponse> createAppointment(@Body CreateAppointmentRequest request);

    // Получить записи клиента
    @GET("api/v1/appointments/client/{clientId}")
    Call<List<AppointmentResponse>> getClientAppointments(@Path("clientId") Long clientId);

    // Получить доступные слоты времени мастера
    @GET("api/v1/appointments/master/{masterId}/available-slots")
    Call<List<TimeSlotResponse>> getAvailableTimeSlots(
            @Path("masterId") Long masterId,
            @Query("date") LocalDate date
    );

    // Проверить доступность времени (опционально)
    @GET("api/v1/appointments/availability")
    Call<Boolean> checkTimeSlotAvailability(
            @Query("masterId") Long masterId,
            @Query("date") LocalDate date,
            @Query("timeSlot") Integer timeSlot
    );

    // Отменить запись
    @POST("api/v1/appointments/{appointmentId}/cancel")
    Call<AppointmentResponse> cancelAppointment(
            @Path("appointmentId") Long appointmentId,
            @Query("reason") String reason,
            @Query("byClient") boolean byClient
    );
}