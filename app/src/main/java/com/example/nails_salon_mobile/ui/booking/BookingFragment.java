package com.example.nails_salon_mobile.ui.booking;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.nails_salon_mobile.R;
import com.example.nails_salon_mobile.data.remote.models.MasterServiceResponse;
import com.example.nails_salon_mobile.data.remote.models.NailServiceDto;
import com.example.nails_salon_mobile.data.remote.models.ServiceCategory;

import org.threeten.bp.LocalDate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class BookingFragment extends Fragment {

    // Spinner'ы для выбора (новая последовательность)
    private Spinner spinnerCategory;
    private Spinner spinnerService;
    private Spinner spinnerMaster;

    // Поля даты и времени
    private TextView tvDate, tvTime;
    private TextView tvSelectedPrice; // Для отображения цены
    private TextView tvSelectedMasterPrice; // Цена мастера

    // Кнопки
    private Button btnSelectDate, btnSelectTime, btnBook;

    // Прогресс
    private ProgressBar progressBar;

    // ViewModel
    private BookingViewModel viewModel;

    // Календарь
    private Calendar selectedDateTime = Calendar.getInstance();

    // Форматеры
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_booking, container, false);

        initViews(view);
        setupViewModel();
        setupListeners();

        // Устанавливаем текущую дату и время
        updateDateTimeDisplay();

        // Загружаем категории (первый шаг)
        viewModel.loadCategories();

        return view;
    }

    private void initViews(View view) {
        // Новые Spinner'ы
        spinnerCategory = view.findViewById(R.id.spinner_category);
        spinnerService = view.findViewById(R.id.spinner_service);
        spinnerMaster = view.findViewById(R.id.spinner_master);

        // Существующие поля
        tvDate = view.findViewById(R.id.tv_date);
        tvTime = view.findViewById(R.id.tv_time);
        tvSelectedPrice = view.findViewById(R.id.tv_selected_price);
        tvSelectedMasterPrice = view.findViewById(R.id.tv_master_price);
        btnSelectDate = view.findViewById(R.id.btn_select_date);
        btnSelectTime = view.findViewById(R.id.btn_select_time);
        btnBook = view.findViewById(R.id.btn_book);
        progressBar = view.findViewById(R.id.progress_bar);

        // Блокируем выбор услуги и мастера до выбора категории
        spinnerService.setEnabled(false);
        spinnerMaster.setEnabled(false);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(BookingViewModel.class);

        // Наблюдаем за категориями
        viewModel.getCategories().observe(getViewLifecycleOwner(), categories -> {
            if (categories != null && !categories.isEmpty()) {
                updateCategoriesSpinner(categories);
            }
        });

        // Наблюдаем за услугами
        viewModel.getServices().observe(getViewLifecycleOwner(), services -> {
            if (services != null) {
                updateServicesSpinner(services);
            }
        });

        // Наблюдаем за мастерами
        viewModel.getMasters().observe(getViewLifecycleOwner(), masters -> {
            if (masters != null) {
                updateMastersSpinner(masters);
            }
        });

        // Наблюдаем за загрузкой
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            btnBook.setEnabled(!isLoading);
            btnBook.setText(isLoading ? "Загрузка..." : "Записаться");
        });

        // Наблюдаем за ошибками
        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

        // Наблюдаем за успехом записи
        viewModel.getBookingSuccess().observe(getViewLifecycleOwner(), success -> {
            if (Boolean.TRUE.equals(success)) {
                Toast.makeText(getContext(),
                        "✅ Запись успешно создана!\nМы отправили вам подтверждение.",
                        Toast.LENGTH_LONG).show();
                resetForm();
            }
        });

        // Наблюдаем за ошибками записи
        viewModel.getBookingError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(getContext(), "❌ " + error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateCategoriesSpinner(List<ServiceCategory> categories) {
        List<String> categoryNames = new ArrayList<>();
        for (ServiceCategory category : categories) {
            categoryNames.add(category.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                categoryNames
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        // Обработчик выбора категории
        spinnerCategory.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && position < categories.size()) {
                    ServiceCategory selectedCategory = categories.get(position);
                    viewModel.setSelectedCategory(selectedCategory);

                    // Разблокируем выбор услуги
                    spinnerService.setEnabled(true);

                    // Сбрасываем выбранные услуги и мастеров
                    spinnerService.setAdapter(null);
                    spinnerMaster.setAdapter(null);
                    spinnerMaster.setEnabled(false);
                    tvSelectedPrice.setText("Выберите услугу");
                    tvSelectedMasterPrice.setText("");
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                // Ничего не выбрано
            }
        });
    }

    private void updateServicesSpinner(List<NailServiceDto> services) {
        if (services.isEmpty()) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    new String[]{"В категории нет доступных услуг"}
            );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerService.setAdapter(adapter);
            spinnerService.setEnabled(false);
            return;
        }

        List<String> serviceNames = new ArrayList<>();
        for (NailServiceDto service : services) {
            String price = service.getPrice() != null ?
                    String.format("%.0f", service.getPrice()) + " руб." : "цена не указана";
            String duration = service.getDurationMinutes() != null ?
                    service.getDurationMinutes() + " мин" : "";
            serviceNames.add(service.getName() + " - " + price + " (" + duration + ")");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                serviceNames
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerService.setAdapter(adapter);
        spinnerService.setEnabled(true);

        // Обработчик выбора услуги
        spinnerService.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && position < services.size()) {
                    NailServiceDto selectedService = services.get(position);
                    viewModel.setSelectedService(selectedService);

                    // Разблокируем выбор мастера
                    spinnerMaster.setEnabled(true);

                    // Показываем базовую цену услуги
                    if (selectedService.getPrice() != null) {
                        tvSelectedPrice.setText("Базовая цена: " +
                                String.format("%.0f", selectedService.getPrice()) + " руб.");
                    } else {
                        tvSelectedPrice.setText("Цена не указана");
                    }

                    tvSelectedMasterPrice.setText("Выберите мастера");
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                // Ничего не выбрано
            }
        });
    }

    private void updateMastersSpinner(List<MasterServiceResponse> masters) {
        if (masters.isEmpty()) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    new String[]{"Нет доступных мастеров для этой услуги"}
            );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerMaster.setAdapter(adapter);
            spinnerMaster.setEnabled(false);
            return;
        }

        List<String> masterNames = new ArrayList<>();
        for (MasterServiceResponse master : masters) {
            String price = master.getMasterPrice() != null ?
                    master.getMasterPrice().toString() + " руб." : "цена не указана";
            masterNames.add(master.getMasterName() + " - " + price);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                masterNames
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMaster.setAdapter(adapter);
        spinnerMaster.setEnabled(true);

        // Обработчик выбора мастера
        spinnerMaster.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && position < masters.size()) {
                    MasterServiceResponse selectedMaster = masters.get(position);
                    viewModel.setSelectedMaster(selectedMaster);

                    // Показываем цену мастера
                    if (selectedMaster.getMasterPrice() != null) {
                        tvSelectedMasterPrice.setText("Цена мастера: " +
                                selectedMaster.getMasterPrice() + " руб.");
                    } else {
                        tvSelectedMasterPrice.setText("Цена мастера не указана");
                    }
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                // Ничего не выбрано
            }
        });
    }

    private void setupListeners() {
        btnSelectDate.setOnClickListener(v -> showDatePicker());
        btnSelectTime.setOnClickListener(v -> showTimePicker());
        btnBook.setOnClickListener(v -> bookAppointment());
    }

    private void showDatePicker() {
        DatePickerDialog datePicker = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    selectedDateTime.set(Calendar.YEAR, year);
                    selectedDateTime.set(Calendar.MONTH, month);
                    selectedDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateDateTimeDisplay();
                },
                selectedDateTime.get(Calendar.YEAR),
                selectedDateTime.get(Calendar.MONTH),
                selectedDateTime.get(Calendar.DAY_OF_MONTH)
        );

        // Минимальная дата - сегодня
        datePicker.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

        // Максимальная дата - 3 месяца вперед
        Calendar maxDate = Calendar.getInstance();
        maxDate.add(Calendar.MONTH, 3);
        datePicker.getDatePicker().setMaxDate(maxDate.getTimeInMillis());

        datePicker.show();
    }

    private void showTimePicker() {
        TimePickerDialog timePicker = new TimePickerDialog(
                requireContext(),
                (view, hourOfDay, minute) -> {
                    selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    selectedDateTime.set(Calendar.MINUTE, minute);
                    updateDateTimeDisplay();
                },
                selectedDateTime.get(Calendar.HOUR_OF_DAY),
                selectedDateTime.get(Calendar.MINUTE),
                true
        );
        timePicker.show();
    }

    private void updateDateTimeDisplay() {
        tvDate.setText(dateFormat.format(selectedDateTime.getTime()));
        tvTime.setText(timeFormat.format(selectedDateTime.getTime()));
    }

    private void bookAppointment() {
        // Проверяем выбранную категорию
        int categoryPosition = spinnerCategory.getSelectedItemPosition();
        List<ServiceCategory> categories = viewModel.getCategories().getValue();

        if (categories == null || categories.isEmpty() || categoryPosition < 0) {
            Toast.makeText(getContext(), "Выберите категорию", Toast.LENGTH_SHORT).show();
            return;
        }

        // Проверяем выбранную услугу
        int servicePosition = spinnerService.getSelectedItemPosition();
        List<NailServiceDto> services = viewModel.getServices().getValue();

        if (services == null || services.isEmpty() || servicePosition < 0) {
            Toast.makeText(getContext(), "Выберите услугу", Toast.LENGTH_SHORT).show();
            return;
        }

        // Проверяем выбранного мастера
        int masterPosition = spinnerMaster.getSelectedItemPosition();
        List<MasterServiceResponse> masters = viewModel.getMasters().getValue();

        if (masters == null || masters.isEmpty() || masterPosition < 0) {
            Toast.makeText(getContext(), "Выберите мастера", Toast.LENGTH_SHORT).show();
            return;
        }

        // Получаем дату
        String dateStr = tvDate.getText().toString();
        String timeStr = tvTime.getText().toString();

        try {
            // Парсим дату из формата dd.MM.yyyy в LocalDate
            String[] dateParts = dateStr.split("\\.");
            int day = Integer.parseInt(dateParts[0]);
            int month = Integer.parseInt(dateParts[1]);
            int year = Integer.parseInt(dateParts[2]);

            LocalDate date = LocalDate.of(year, month, day);

            // Парсим время и конвертируем в слот
            String[] timeParts = timeStr.split(":");
            int hour = Integer.parseInt(timeParts[0]);
            int minute = Integer.parseInt(timeParts[1]);

            // Конвертируем время в слот (9:00-10:00 = слот 1, 10:00-11:00 = слот 2 и т.д.)
            int timeSlot = convertTimeToSlot(hour, minute);

            if (timeSlot < 1 || timeSlot > 10) {
                Toast.makeText(getContext(),
                        "Выберите время с 9:00 до 19:00 (только целые часы)",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            // Устанавливаем значения в ViewModel
            ServiceCategory selectedCategory = categories.get(categoryPosition);
            NailServiceDto selectedService = services.get(servicePosition);
            MasterServiceResponse selectedMaster = masters.get(masterPosition);

            viewModel.setSelectedCategory(selectedCategory);
            viewModel.setSelectedService(selectedService);
            viewModel.setSelectedMaster(selectedMaster);
            viewModel.setSelectedDate(date);
            viewModel.setSelectedTimeSlot(timeSlot);
            viewModel.setNotes("Запись через мобильное приложение");

            // Создаем запись
            viewModel.createAppointment();

        } catch (Exception e) {
            Toast.makeText(getContext(),
                    "Ошибка в формате даты/времени: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private int convertTimeToSlot(int hour, int minute) {
        // Слоты: 1=9:00, 2=10:00, ..., 10=18:00
        if (minute != 0) {
            return -1; // Только целые часы
        }

        if (hour >= 9 && hour <= 18) {
            return hour - 8; // 9->1, 10->2, ..., 18->10
        }

        return -1;
    }

    private void resetForm() {
        // Сбрасываем все спиннеры
        if (spinnerCategory.getAdapter() != null && spinnerCategory.getAdapter().getCount() > 0) {
            spinnerCategory.setSelection(0);
        }

        spinnerService.setAdapter(null);
        spinnerService.setEnabled(false);

        spinnerMaster.setAdapter(null);
        spinnerMaster.setEnabled(false);

        tvSelectedPrice.setText("Выберите услугу");
        tvSelectedMasterPrice.setText("");

        // Устанавливаем текущую дату и время
        selectedDateTime = Calendar.getInstance();
        updateDateTimeDisplay();
    }
}