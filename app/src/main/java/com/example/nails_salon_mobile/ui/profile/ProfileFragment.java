package com.example.nails_salon_mobile.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.nails_salon_mobile.R;
import com.example.nails_salon_mobile.data.local.entities.AppointmentEntity;
import com.example.nails_salon_mobile.data.remote.models.UserResponseDto;
import com.example.nails_salon_mobile.ui.auth.LoginActivity;
import com.example.nails_salon_mobile.utils.SharedPrefsManager;
import com.example.nails_salon_mobile.utils.UserProfileManager;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment implements AppointmentsAdapter.OnAppointmentClickListener {

    private TextView tvUserName, tvUserEmail, tvUserRole;
    private Button btnLogout;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView tvEmpty;

    private ProfileViewModel viewModel;
    private AppointmentsAdapter adapter;
    private List<AppointmentEntity> appointmentsList = new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        initViews(view);
        setupViewModel();
        setupRecyclerView();
        setupSwipeRefresh();
        loadUserData();

        // Загружаем записи пользователя
        viewModel.loadUserAppointments();

        // ОБНОВЛЯЕМ ПРОФИЛЬ ПРИ ОТКРЫТИИ
        refreshProfileIfNeeded();

        return view;
    }

    private void refreshProfileIfNeeded() {
        if (SharedPrefsManager.getInstance(requireContext()).isLoggedIn()) {
            UserProfileManager.getInstance(requireContext())
                    .refreshProfileIfNeeded();
        }
    }

    private void initViews(View view) {
        tvUserName = view.findViewById(R.id.tv_user_name);
        tvUserEmail = view.findViewById(R.id.tv_user_email);
        tvUserRole = view.findViewById(R.id.tv_user_role);
        btnLogout = view.findViewById(R.id.btn_logout);
        recyclerView = view.findViewById(R.id.recycler_appointments);
        progressBar = view.findViewById(R.id.progress_bar);
        tvEmpty = view.findViewById(R.id.tv_empty);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);

        btnLogout.setOnClickListener(v -> logout());
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);

        // Наблюдаем за записями
        viewModel.getAppointments().observe(getViewLifecycleOwner(), appointments -> {
            appointmentsList.clear();
            appointmentsList.addAll(appointments);

            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }

            // Показываем/скрываем сообщение "нет записей"
            tvEmpty.setVisibility(appointments.isEmpty() ? View.VISIBLE : View.GONE);
            recyclerView.setVisibility(appointments.isEmpty() ? View.GONE : View.VISIBLE);
        });

        // Наблюдаем за загрузкой
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        // Наблюдаем за ошибками
        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            // Обновляем профиль
            UserProfileManager.getInstance(requireContext())
                    .refreshUserProfile(new UserProfileManager.ProfileRefreshCallback() {
                        @Override
                        public void onSuccess(UserResponseDto user) {
                            swipeRefreshLayout.setRefreshing(false);
                            loadUserData();
                            viewModel.loadUserAppointments();
                            Toast.makeText(getContext(), "Профиль обновлен", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(String error) {
                            swipeRefreshLayout.setRefreshing(false);
                            Toast.makeText(getContext(), "Ошибка: " + error, Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AppointmentsAdapter(appointmentsList, this);
        recyclerView.setAdapter(adapter);
    }

    public void refreshUserData() {
        if (isAdded() && getView() != null) {
            loadUserData();
        }
    }

    private void loadUserData() {
        SharedPrefsManager prefs = SharedPrefsManager.getInstance(requireContext());

        Log.d("ProfileFragment", "Загрузка данных пользователя:");
        Log.d("ProfileFragment", "Email: " + prefs.getUserEmail());
        Log.d("ProfileFragment", "FirstName: " + prefs.getUserFirstName());
        Log.d("ProfileFragment", "LastName: " + prefs.getUserLastName());
        Log.d("ProfileFragment", "UserName: " + prefs.getUserName());

        // Полное имя
        String fullName = prefs.getUserName();
        if (fullName != null && !fullName.trim().isEmpty() && !fullName.equals(" ")) {
            tvUserName.setText(fullName.trim());
            Log.d("ProfileFragment", "Установлено имя: " + fullName);
        } else {
            // Пробуем собрать из компонентов
            String firstName = prefs.getUserFirstName();
            String lastName = prefs.getUserLastName();
            String name = (firstName + " " + lastName).trim();

            if (!name.isEmpty() && !name.equals(" ")) {
                tvUserName.setText(name);
                Log.d("ProfileFragment", "Установлено собранное имя: " + name);
            } else {
                // Показываем email
                String email = prefs.getUserEmail();
                if (!email.isEmpty()) {
                    tvUserName.setText(email);
                    Log.d("ProfileFragment", "Установлен email: " + email);
                } else {
                    tvUserName.setText("Пользователь");
                    Log.d("ProfileFragment", "Установлено 'Пользователь'");
                }
            }
        }

        // Email
        String email = prefs.getUserEmail();
        if (!email.isEmpty()) {
            tvUserEmail.setText(email);
        } else {
            tvUserEmail.setText("Email не указан");
        }

        // Роль
        String role = prefs.getUserRole();
        if (!role.isEmpty()) {
            String roleText = "Роль: " + getRoleDisplayName(role);
            tvUserRole.setText(roleText);
        }

        // Можно добавить телефон, если нужно
        String phone = prefs.getUserPhone();
        if (!phone.isEmpty()) {
            // Можно добавить TextView для телефона
            // tvUserPhone.setText("Телефон: " + phone);
        }
    }
    private String getRoleDisplayName(String role) {
        switch (role) {
            case "CLIENT": return "Клиент";
            case "MASTER": return "Мастер";
            case "ADMIN": return "Администратор";
            default: return role;
        }
    }

    // Реализация интерфейса адаптера
    @Override
    public void onAppointmentClick(AppointmentEntity appointment) {
        // Показать детали записи
        showAppointmentDetails(appointment);
    }

    @Override
    public void onCancelClick(AppointmentEntity appointment) {
        // Отменить запись
        cancelAppointment(appointment);
    }

    private void showAppointmentDetails(AppointmentEntity appointment) {
        // TODO: Показать диалог с деталями записи
        Toast.makeText(getContext(),
                "Детали записи: " + appointment.getServiceName(),
                Toast.LENGTH_SHORT).show();
    }

    private void cancelAppointment(AppointmentEntity appointment) {
        // TODO: Показать диалог подтверждения
        viewModel.cancelAppointment(appointment.getAppointmentId(), "Отменено клиентом");
        Toast.makeText(getContext(), "Запись отменена", Toast.LENGTH_SHORT).show();
    }

    private void logout() {
        SharedPrefsManager.getInstance(requireContext()).clearAuthData();
        Toast.makeText(getContext(), "Вы вышли из системы", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        if (getActivity() != null) {
            getActivity().finish();
        }
    }
}