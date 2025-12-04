package com.example.nails_salon_mobile.ui.profile;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.nails_salon_mobile.data.local.entities.AppointmentEntity;
import com.example.nails_salon_mobile.data.repository.AppointmentRepository;

import java.util.ArrayList;
import java.util.List;

public class ProfileViewModel extends AndroidViewModel {

    private final AppointmentRepository appointmentRepository;

    private final MutableLiveData<List<AppointmentEntity>> _appointments =
            new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> _error = new MutableLiveData<>();

    public ProfileViewModel(Application application) {
        super(application);
        appointmentRepository = AppointmentRepository.getInstance(application);
    }

    public LiveData<List<AppointmentEntity>> getAppointments() {
        return _appointments;
    }

    public LiveData<Boolean> getIsLoading() {
        return _isLoading;
    }

    public LiveData<String> getError() {
        return _error;
    }

    // Загрузить записи пользователя
    public void loadUserAppointments() {
        _isLoading.postValue(true);
        _error.postValue(null);

        appointmentRepository.getClientAppointments(new AppointmentRepository.AppointmentsCallback() {
            @Override
            public void onSuccess(List<AppointmentEntity> appointments) {
                _appointments.postValue(appointments);
                _isLoading.postValue(false);
            }

            @Override
            public void onError(String error) {
                _error.postValue(error);
                _isLoading.postValue(false);
            }
        });
    }

    // Отменить запись
    public void cancelAppointment(Long appointmentId, String reason) {
        appointmentRepository.cancelAppointment(appointmentId, reason, true);

        // Обновляем локальный список
        List<AppointmentEntity> currentAppointments = _appointments.getValue();
        if (currentAppointments != null) {
            for (AppointmentEntity appointment : currentAppointments) {
                if (appointment.getAppointmentId().equals(appointmentId)) {
                    appointment.setStatus("CANCELLED");
                    break;
                }
            }
            _appointments.postValue(currentAppointments);
        }
    }
}