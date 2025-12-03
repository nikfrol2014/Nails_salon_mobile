package com.example.nails_salon_mobile.ui.services;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.nails_salon_mobile.data.local.entities.ServiceEntity;
import com.example.nails_salon_mobile.data.repository.ServiceRepository;

import java.util.ArrayList;
import java.util.List;


public class ServicesViewModel extends AndroidViewModel {

    private ServiceRepository serviceRepository;
    private final MutableLiveData<List<ServiceEntity>> _services = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> _error = new MutableLiveData<>();

    public ServicesViewModel(Application application) {
        super(application);
        serviceRepository = ServiceRepository.getInstance(application);
    }

    public LiveData<List<ServiceEntity>> getServices() {
        return _services;
    }

    public LiveData<Boolean> getIsLoading() {
        return _isLoading;
    }

    public LiveData<String> getError() {
        return _error;
    }

    // Загрузить все услуги
    public void loadServices() {
        _isLoading.setValue(true);
        _error.setValue(null);

        serviceRepository.getAllServices(new ServiceRepository.ServicesCallback() {
            @Override
            public void onSuccess(List<ServiceEntity> services) {
                _services.postValue(services);
                _isLoading.postValue(false);
            }

            @Override
            public void onError(String error) {
                _error.postValue(error);
                _isLoading.postValue(false);
            }
        });
    }

    // Поиск услуг
    public void searchServices(android.content.Context context, String query) {
        if (query == null || query.trim().isEmpty()) {
            loadServices();
            return;
        }

        _isLoading.setValue(true);

        ServiceRepository.getInstance(context).searchServices(query, new ServiceRepository.ServicesCallback() {
            @Override
            public void onSuccess(List<ServiceEntity> services) {
                _services.postValue(services);
                _isLoading.postValue(false);
            }

            @Override
            public void onError(String error) {
                _error.postValue(error);
                _isLoading.postValue(false);
            }
        });
    }

    // Переключить избранное
    public void toggleFavorite(android.content.Context context, Long serviceId, boolean isFavorite) {
        ServiceRepository.getInstance(context).toggleFavorite(serviceId, isFavorite);

        // Обновляем локальный список
        List<ServiceEntity> currentServices = _services.getValue();
        if (currentServices != null) {
            for (ServiceEntity service : currentServices) {
                if (service.getId().equals(serviceId)) {
                    service.setFavorite(isFavorite);
                    break;
                }
            }
            _services.setValue(currentServices);
        }
    }
}