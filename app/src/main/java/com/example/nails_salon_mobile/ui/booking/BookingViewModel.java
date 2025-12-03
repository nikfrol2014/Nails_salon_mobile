package com.example.nails_salon_mobile.ui.booking;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.nails_salon_mobile.data.local.entities.AppointmentEntity;
import com.example.nails_salon_mobile.data.remote.models.MasterServiceResponse;
import com.example.nails_salon_mobile.data.remote.models.NailServiceDto;
import com.example.nails_salon_mobile.data.remote.models.ServiceCategory;
import com.example.nails_salon_mobile.data.remote.models.TimeSlotResponse;
import com.example.nails_salon_mobile.data.repository.AppointmentRepository;
import com.example.nails_salon_mobile.data.repository.CategoryRepository;
import com.example.nails_salon_mobile.data.repository.MasterRepository;
import com.example.nails_salon_mobile.data.repository.ServiceRepository;

import org.threeten.bp.LocalDate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class BookingViewModel extends AndroidViewModel {

    private final CategoryRepository categoryRepository;
    private final ServiceRepository serviceRepository;
    private final MasterRepository masterRepository;
    private final AppointmentRepository appointmentRepository;

    // LiveData для данных
    private final MutableLiveData<List<ServiceCategory>> _categories =
            new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<NailServiceDto>> _services =
            new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<MasterServiceResponse>> _masters =
            new MutableLiveData<>(new ArrayList<>());

    // LiveData для UI состояния
    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> _error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _bookingSuccess = new MutableLiveData<>(false);
    private final MutableLiveData<String> _bookingError = new MutableLiveData<>();

    // Выбранные значения
    private ServiceCategory selectedCategory;
    private NailServiceDto selectedService;
    private MasterServiceResponse selectedMaster;
    private LocalDate selectedDate;
    private Integer selectedTimeSlot;
    private String notes;

    public BookingViewModel(Application application) {
        super(application);
        categoryRepository = CategoryRepository.getInstance(application);
        serviceRepository = ServiceRepository.getInstance(application);
        masterRepository = MasterRepository.getInstance(application);
        appointmentRepository = AppointmentRepository.getInstance(application);
    }

    // LiveData геттеры
    public LiveData<List<ServiceCategory>> getCategories() { return _categories; }
    public LiveData<List<NailServiceDto>> getServices() { return _services; }
    public LiveData<List<MasterServiceResponse>> getMasters() { return _masters; }
    public LiveData<Boolean> getIsLoading() { return _isLoading; }
    public LiveData<String> getError() { return _error; }
    public LiveData<Boolean> getBookingSuccess() { return _bookingSuccess; }
    public LiveData<String> getBookingError() { return _bookingError; }

    // Загрузить категории
    public void loadCategories() {
        _isLoading.postValue(true);
        _error.postValue(null);

        categoryRepository.getAllCategories(new CategoryRepository.CategoriesCallback() {
            @Override
            public void onSuccess(List<com.example.nails_salon_mobile.data.local.entities.CategoryEntity> categories) {
                // Конвертируем CategoryEntity в ServiceCategory
                List<ServiceCategory> serviceCategories = new ArrayList<>();
                for (com.example.nails_salon_mobile.data.local.entities.CategoryEntity entity : categories) {
                    ServiceCategory dto = new ServiceCategory();
                    dto.setCategoryId(entity.getCategoryId());
                    dto.setName(entity.getName());
                    dto.setDescription(entity.getDescription());
                    dto.setSortOrder(entity.getSortOrder());
                    serviceCategories.add(dto);
                }

                _categories.postValue(serviceCategories);
                _isLoading.postValue(false);
            }

            @Override
            public void onError(String error) {
                _error.postValue(error);
                _isLoading.postValue(false);
            }
        });
    }

    // Загрузить услуги по категории
    public void loadServicesByCategory(Long categoryId) {
        if (categoryId == null) return;

        _isLoading.postValue(true);

        // Используем существующий метод из ServicesApi
        // Нужно добавить этот метод в ServicesRepository
        loadServicesFromApiByCategory(categoryId);
    }

    private void loadServicesFromApiByCategory(Long categoryId) {
        // Временная реализация - загружаем все услуги и фильтруем
        serviceRepository.getAllServices(new ServiceRepository.ServicesCallback() {
            @Override
            public void onSuccess(List<com.example.nails_salon_mobile.data.local.entities.ServiceEntity> services) {
                // Фильтруем по категории
                List<NailServiceDto> filteredServices = new ArrayList<>();
                for (com.example.nails_salon_mobile.data.local.entities.ServiceEntity entity : services) {
                    if (entity.getCategoryId() != null && entity.getCategoryId().equals(categoryId)) {
                        NailServiceDto dto = new NailServiceDto();
                        dto.setId(entity.getId());
                        dto.setName(entity.getName());
                        dto.setDescription(entity.getDescription());
                        dto.setPrice(entity.getPrice());
                        dto.setDurationMinutes(entity.getDurationMinutes());
                        dto.setCategoryId(entity.getCategoryId());
                        dto.setActive(entity.getActive());
                        filteredServices.add(dto);
                    }
                }

                _services.postValue(filteredServices);
                _isLoading.postValue(false);
            }

            @Override
            public void onError(String error) {
                _error.postValue(error);
                _isLoading.postValue(false);
            }
        });
    }

    // Загрузить мастеров для услуги
    public void loadMastersForService(Long serviceId) {
        if (serviceId == null) return;

        _isLoading.postValue(true);

        masterRepository.getServiceMasters(serviceId, new MasterRepository.MasterServicesCallback() {
            @Override
            public void onSuccess(List<MasterServiceResponse> masters) {
                _masters.postValue(masters);
                _isLoading.postValue(false);
            }

            @Override
            public void onError(String error) {
                _error.postValue(error);
                _isLoading.postValue(false);
            }
        });
    }

    // Создать запись
    public void createAppointment() {
        if (selectedMaster == null || selectedService == null ||
                selectedDate == null || selectedTimeSlot == null) {
            _bookingError.postValue("Заполните все поля");
            return;
        }

        _bookingSuccess.postValue(false);
        _bookingError.postValue(null);

        appointmentRepository.createAppointment(
                selectedMaster.getMasterId(), // ID мастера из MasterServiceResponse
                selectedService.getId(), // ID услуги
                selectedDate,
                selectedTimeSlot,
                notes != null ? notes : "",
                new AppointmentRepository.CreateAppointmentCallback() {
                    @Override
                    public void onSuccess(AppointmentEntity appointment) {
                        _bookingSuccess.postValue(true);
                        resetSelection();
                    }

                    @Override
                    public void onError(String error) {
                        _bookingError.postValue(error);
                    }
                }
        );
    }

    // Setters для выбранных значений
    public void setSelectedCategory(ServiceCategory category) {
        this.selectedCategory = category;
        if (category != null) {
            loadServicesByCategory(category.getCategoryId());
            // Сбрасываем выбранные услуги и мастеров
            _services.postValue(new ArrayList<>());/////////////////////////////////////////////////
            _masters.postValue(new ArrayList<>());//////////////////////////////////////////////
        }
    }

    public void setSelectedService(NailServiceDto service) {
        this.selectedService = service;
        if (service != null) {
            loadMastersForService(service.getId());
            // Сбрасываем выбранных мастеров
            _masters.postValue(new ArrayList<>());//////////////////////////////////////////////
        }
    }

    public void setSelectedMaster(MasterServiceResponse master) {
        this.selectedMaster = master;
    }

    public void setSelectedDate(LocalDate date) {
        this.selectedDate = date;
    }

    public void setSelectedTimeSlot(Integer timeSlot) {
        this.selectedTimeSlot = timeSlot;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    // Getters для выбранных значений
    public ServiceCategory getSelectedCategory() { return selectedCategory; }
    public NailServiceDto getSelectedService() { return selectedService; }
    public MasterServiceResponse getSelectedMaster() { return selectedMaster; }
    public LocalDate getSelectedDate() { return selectedDate; }
    public Integer getSelectedTimeSlot() { return selectedTimeSlot; }
    public String getNotes() { return notes; }

    // Получить выбранную цену (мастерская цена или базовая)
    public BigDecimal getSelectedPrice() {
        if (selectedMaster != null && selectedMaster.getMasterPrice() != null) {
            return selectedMaster.getMasterPrice();
        } else if (selectedService != null && selectedService.getPrice() != null) {
            return BigDecimal.valueOf(selectedService.getPrice());
        }
        return BigDecimal.ZERO;
    }

    // Сбросить выбор
    private void resetSelection() {
        selectedCategory = null;
        selectedService = null;
        selectedMaster = null;
        selectedDate = null;
        selectedTimeSlot = null;
        notes = null;
        _services.postValue(new ArrayList<>());
        _masters.postValue(new ArrayList<>());
    }
}