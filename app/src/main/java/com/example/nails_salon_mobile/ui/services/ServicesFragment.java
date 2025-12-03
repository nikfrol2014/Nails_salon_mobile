package com.example.nails_salon_mobile.ui.services;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.nails_salon_mobile.R;
import com.example.nails_salon_mobile.data.remote.models.NailServiceDto;
import java.util.ArrayList;
import java.util.List;
import com.example.nails_salon_mobile.data.local.entities.ServiceEntity;

// Важно: implements OnServiceClickListener
public class ServicesFragment extends Fragment implements ServicesAdapter.OnServiceClickListener {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private ServicesAdapter adapter;
    private List<NailServiceDto> servicesList = new ArrayList<>();

    private ServicesViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_services, container, false);

        recyclerView = view.findViewById(R.id.recycler_services);
        progressBar = view.findViewById(R.id.progress_bar);

        // Инициализируем ViewModel (AndroidViewModel версия)
        viewModel = new ViewModelProvider(requireActivity()).get(ServicesViewModel.class);

        setupRecyclerView();
        setupObservers();

        // Загружаем данные
        viewModel.loadServices();

        return view;
    }

    private void setupObservers() {
        // Наблюдаем за услугами
        viewModel.getServices().observe(getViewLifecycleOwner(), services -> {
            servicesList.clear();

            // Конвертируем ServiceEntity в NailServiceDto
            for (com.example.nails_salon_mobile.data.local.entities.ServiceEntity serviceEntity : services) {
                NailServiceDto dto = new NailServiceDto();
                dto.setId(serviceEntity.getId());
                dto.setName(serviceEntity.getName());
                dto.setDescription(serviceEntity.getDescription());
                dto.setPrice(serviceEntity.getPrice());
                dto.setDurationMinutes(serviceEntity.getDurationMinutes());
                dto.setCategoryId(serviceEntity.getCategoryId());
                dto.setActive(serviceEntity.getActive());

                servicesList.add(dto);
            }

            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        });

        // Наблюдаем за загрузкой
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        // Наблюдаем за ошибками
        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // this - потому что фрагмент реализует OnServiceClickListener
        adapter = new ServicesAdapter(servicesList, this);
        recyclerView.setAdapter(adapter);
    }

    // === РЕАЛИЗАЦИЯ МЕТОДОВ ИНТЕРФЕЙСА ===

    @Override
    public void onServiceClick(NailServiceDto service) {
        // Теперь service содержит данные из Entity
        Toast.makeText(getContext(),
                "Выбрано: " + service.getName() + "\nЦена: " + service.getPrice() + " руб.",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onServiceBook(NailServiceDto service) {
        // Можно открыть BookingFragment с выбранной услугой
        Toast.makeText(getContext(),
                "Запись на: " + service.getName(),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onServiceDetails(NailServiceDto service) {
        showServiceDetailsDialog(service);
    }

    private void showServiceDetailsDialog(NailServiceDto service) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(service.getName())
                .setMessage("Описание: " + service.getDescription() +
                        "\n\nЦена: " + service.getPrice() + " руб." +
                        "\nДлительность: " + service.getDurationMinutes() + " мин." +
                        "\nКатегория ID: " + service.getCategoryId())
                .setPositiveButton("OK", null)
                .setNeutralButton("Записаться", (dialog, which) -> {
                    onServiceBook(service); // Переход к записи
                })
                .show();
    }

    // === ЗАГРУЗКА ДАННЫХ ===

    private void loadServices() {
        // TODO: Реальная загрузка с API
        // Пока используем тестовые данные
        loadTestData();
    }

    private void loadTestData() {
        progressBar.setVisibility(View.VISIBLE);

        // Тестовые данные
        List<NailServiceDto> testServices = new ArrayList<>();

        NailServiceDto service1 = new NailServiceDto();
        service1.setId(1L);
        service1.setName("Маникюр классический");
        service1.setDescription("Классический маникюр с покрытием");
        service1.setPrice(1500.0);
        service1.setDurationMinutes(60);
        service1.setCategoryId(1L);
        testServices.add(service1);

        NailServiceDto service2 = new NailServiceDto();
        service2.setId(2L);
        service2.setName("Педикюр");
        service2.setDescription("Уход за стопами и ногтями на ногах");
        service2.setPrice(2000.0);
        service2.setDurationMinutes(90);
        service2.setCategoryId(1L);
        testServices.add(service2);

        NailServiceDto service3 = new NailServiceDto();
        service3.setId(3L);
        service3.setName("Наращивание ногтей");
        service3.setDescription("Наращивание гелевых ногтей");
        service3.setPrice(3000.0);
        service3.setDurationMinutes(120);
        service3.setCategoryId(2L);
        testServices.add(service3);

        NailServiceDto service4 = new NailServiceDto();
        service4.setId(4L);
        service4.setName("Дизайн ногтей");
        service4.setDescription("Художественный дизайн на ногтях");
        service4.setPrice(500.0);
        service4.setDurationMinutes(30);
        service4.setCategoryId(3L);
        testServices.add(service4);

        // Обновляем список
        servicesList.clear();
        servicesList.addAll(testServices);

        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }

        progressBar.setVisibility(View.GONE);
    }
}