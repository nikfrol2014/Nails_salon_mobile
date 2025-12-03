package com.example.nails_salon_mobile.data.repository;

import android.content.Context;
import android.util.Log;

import com.example.nails_salon_mobile.data.local.AppDatabase;
import com.example.nails_salon_mobile.data.local.DatabaseClient;
import com.example.nails_salon_mobile.data.local.dao.AppointmentDao;
import com.example.nails_salon_mobile.data.local.entities.AppointmentEntity;
import com.example.nails_salon_mobile.data.remote.RetrofitClient;
import com.example.nails_salon_mobile.data.remote.api.BookingsApi;
import com.example.nails_salon_mobile.data.remote.models.AppointmentResponse;
import com.example.nails_salon_mobile.data.remote.models.CreateAppointmentRequest;
import com.example.nails_salon_mobile.utils.SharedPrefsManager;

import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AppointmentRepository {

    private static final String TAG = "AppointmentRepository";
    private static AppointmentRepository instance;

    private final BookingsApi bookingsApi;
    private final AppointmentDao appointmentDao;
    private final ExecutorService executorService;
    private final Context context;

    private AppointmentRepository(Context context) {
        this.context = context.getApplicationContext();
        bookingsApi = RetrofitClient.getClient().create(BookingsApi.class);

        AppDatabase database = DatabaseClient.getInstance(context).getAppDatabase();
        appointmentDao = database.appointmentDao();

        executorService = Executors.newSingleThreadExecutor();
    }

    public static synchronized AppointmentRepository getInstance(Context context) {
        if (instance == null) {
            instance = new AppointmentRepository(context);
        }
        return instance;
    }

    // Создать запись
    public interface CreateAppointmentCallback {
        void onSuccess(AppointmentEntity appointment);
        void onError(String error);
    }

    public void createAppointment(Long masterId, Long serviceId,
                                  LocalDate date, Integer timeSlot,
                                  String notes, CreateAppointmentCallback callback) {
        executorService.execute(() -> {
            try {
                // Получаем clientId из SharedPreferences
                Long clientId = SharedPrefsManager.getInstance(context).getUserId();
                if (clientId == null || clientId == -1) {
                    callback.onError("Пользователь не авторизован");
                    return;
                }

                // Создаем запрос
                CreateAppointmentRequest request = new CreateAppointmentRequest();
                request.setClientId(clientId);
                request.setMasterId(masterId);
                request.setServiceId(serviceId);
                request.setAppointmentDate(date);
                request.setTimeSlot(timeSlot);
                request.setNotes(notes);

                // Отправляем на сервер
                retrofit2.Response<AppointmentResponse> response =
                        bookingsApi.createAppointment(request).execute();

                if (response.isSuccessful() && response.body() != null) {
                    AppointmentResponse appointmentResponse = response.body();

                    // Конвертируем в Entity и сохраняем в БД
                    AppointmentEntity appointment = convertToEntity(appointmentResponse);
                    appointment.setSynced(true);
                    appointment.setCreatedLocally(false);

                    appointmentDao.insert(appointment);
                    callback.onSuccess(appointment);

                } else {
                    String error = "Ошибка создания записи";
                    if (response.errorBody() != null) {
                        error = response.errorBody().string();
                    }
                    callback.onError(error);

                    // Создаем оффлайн запись
                    createOfflineAppointment(clientId, masterId, serviceId,
                            date, timeSlot, notes, callback);
                }

            } catch (Exception e) {
                Log.e(TAG, "Error creating appointment", e);
                callback.onError("Ошибка сети: " + e.getMessage());
            }
        });
    }

    // Создать оффлайн запись (когда нет интернета)
    private void createOfflineAppointment(Long clientId, Long masterId, Long serviceId,
                                          LocalDate date, Integer timeSlot,
                                          String notes, CreateAppointmentCallback callback) {
        try {
            AppointmentEntity offlineAppointment = new AppointmentEntity();
            offlineAppointment.setClientId(clientId);
            offlineAppointment.setMasterId(masterId);
            offlineAppointment.setServiceId(serviceId);
            offlineAppointment.setAppointmentDatetime(
                    LocalDateTime.of(date.getYear(), date.getMonthValue(), date.getDayOfMonth(),
                            getHourFromTimeSlot(timeSlot), 0)
            );
            offlineAppointment.setStatus("PENDING_SYNC");
            offlineAppointment.setNotes(notes);
            offlineAppointment.setSynced(false);
            offlineAppointment.setCreatedLocally(true);
            offlineAppointment.setCreatedAt(LocalDateTime.now());

            // Генерируем временный отрицательный ID
            offlineAppointment.setAppointmentId(-System.currentTimeMillis());

            appointmentDao.insert(offlineAppointment);
            callback.onSuccess(offlineAppointment);

            Log.i(TAG, "Created offline appointment, will sync later");

        } catch (Exception e) {
            callback.onError("Не удалось создать запись даже оффлайн");
        }
    }

    // Получить записи клиента
    public interface AppointmentsCallback {
        void onSuccess(List<AppointmentEntity> appointments);
        void onError(String error);
    }

    public void getClientAppointments(AppointmentsCallback callback) {
        executorService.execute(() -> {
            try {
                // Получаем clientId
                Long clientId = SharedPrefsManager.getInstance(context).getUserId();
                if (clientId == null || clientId == -1) {
                    callback.onError("Пользователь не авторизован");
                    return;
                }

                // Сначала показываем из БД
                List<AppointmentEntity> cachedAppointments =
                        appointmentDao.getByClientId(clientId);
                if (!cachedAppointments.isEmpty()) {
                    callback.onSuccess(cachedAppointments);
                }

                // Загружаем с сервера
                retrofit2.Response<List<AppointmentResponse>> response =
                        bookingsApi.getClientAppointments(clientId).execute();

                if (response.isSuccessful() && response.body() != null) {
                    List<AppointmentEntity> appointments = response.body().stream()
                            .map(this::convertToEntity)
                            .collect(java.util.stream.Collectors.toList());

                    // Обновляем БД
                    appointmentDao.insertAll(appointments);
                    callback.onSuccess(appointments);

                    // Синхронизируем оффлайн записи
                    syncOfflineAppointments();

                } else if (cachedAppointments.isEmpty()) {
                    callback.onError("Не удалось загрузить записи");
                }

            } catch (Exception e) {
                Log.e(TAG, "Error getting appointments", e);
                List<AppointmentEntity> cachedAppointments =
                        appointmentDao.getByClientId(
                                SharedPrefsManager.getInstance(context).getUserId()
                        );
                if (!cachedAppointments.isEmpty()) {
                    callback.onSuccess(cachedAppointments);
                } else {
                    callback.onError("Ошибка: " + e.getMessage());
                }
            }
        });
    }

    // Синхронизировать оффлайн записи
    private void syncOfflineAppointments() {
        try {
            List<AppointmentEntity> unsynced = appointmentDao.getUnsyncedAppointments();
            for (AppointmentEntity appointment : unsynced) {
                if (appointment.isCreatedLocally()) {
                    // Пытаемся отправить на сервер
                    // Здесь нужно реализовать логику повторной отправки
                    Log.i(TAG, "Syncing offline appointment: " + appointment.getAppointmentId());
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error syncing offline appointments", e);
        }
    }

    // Получить доступные слоты времени
    public interface TimeSlotsCallback {
        void onSuccess(List<com.example.nails_salon_mobile.data.remote.models.TimeSlotResponse> timeSlots);
        void onError(String error);
    }

    public void getAvailableTimeSlots(Long masterId, LocalDate date, TimeSlotsCallback callback) {
        executorService.execute(() -> {
            try {
                retrofit2.Response<List<com.example.nails_salon_mobile.data.remote.models.TimeSlotResponse>> response =
                        bookingsApi.getAvailableTimeSlots(masterId, date).execute();

                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Не удалось получить доступное время");
                }
            } catch (Exception e) {
                Log.e(TAG, "Error getting time slots", e);
                callback.onError("Ошибка: " + e.getMessage());
            }
        });
    }

    // Отменить запись
    public void cancelAppointment(Long appointmentId, String reason, boolean byClient) {
        executorService.execute(() -> {
            try {
                retrofit2.Response<AppointmentResponse> response =
                        bookingsApi.cancelAppointment(appointmentId, reason, byClient).execute();

                if (response.isSuccessful()) {
                    // Обновляем статус в БД
                    AppointmentEntity appointment = appointmentDao.getById(appointmentId);
                    if (appointment != null) {
                        appointment.setStatus("CANCELLED");
                        appointmentDao.update(appointment);
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error cancelling appointment", e);
            }
        });
    }

    // Вспомогательные методы
    private AppointmentEntity convertToEntity(AppointmentResponse dto) {
        AppointmentEntity entity = new AppointmentEntity();
        entity.setAppointmentId(dto.getAppointmentId());
        entity.setClientId(dto.getClientId());
        entity.setClientName(dto.getClientName());
        entity.setMasterId(dto.getMasterId());
        entity.setMasterName(dto.getMasterName());
        entity.setServiceId(dto.getServiceId());
        entity.setServiceName(dto.getServiceName());
        if (dto.getAppointmentDatetime() != null) {
            try {
                LocalDateTime dateTime = LocalDateTime.parse(
                        dto.getAppointmentDatetimeString(),
                        DateTimeFormatter.ISO_LOCAL_DATE_TIME
                );
                entity.setAppointmentDatetime(dateTime);
            } catch (Exception e) {
                Log.e(TAG, "Error parsing appointmentDatetime: " + dto.getAppointmentDatetime(), e);
            }
        }
        if (dto.getEndDatetime() != null) {
            try {
                LocalDateTime dateTime = LocalDateTime.parse(
                        dto.getEndDatetimeString(),
                        DateTimeFormatter.ISO_LOCAL_DATE_TIME
                );
                entity.setEndDatetime(dateTime);
            } catch (Exception e) {
                Log.e(TAG, "Error parsing appointmentDatetime: " + dto.getEndDatetime(), e);
            }
        }
        entity.setPrice(dto.getPrice());
        entity.setStatus(dto.getStatus());
        entity.setNotes(dto.getNotes());
        if (dto.getCreatedAt() != null) {
            try {
                LocalDateTime dateTime = LocalDateTime.parse(
                        dto.getCreatedAtString(),
                        DateTimeFormatter.ISO_LOCAL_DATE_TIME
                );
                entity.setCreatedAt(dateTime);
            } catch (Exception e) {
                Log.e(TAG, "Error parsing appointmentDatetime: " + dto.getCreatedAt(), e);
            }
        }
        entity.setSynced(true);
        entity.setCreatedLocally(false);
        return entity;
    }

    private int getHourFromTimeSlot(Integer timeSlot) {
        switch (timeSlot) {
            case 1: return 9;
            case 2: return 10;
            case 3: return 11;
            case 4: return 12;
            case 5: return 13;
            case 6: return 14;
            case 7: return 15;
            case 8: return 16;
            case 9: return 17;
            case 10: return 18;
            default: return 9;
        }
    }

    // Освобождение ресурсов
    public void shutdown() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}