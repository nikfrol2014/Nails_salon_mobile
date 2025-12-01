package com.example.nails_salon_mobile.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.example.nails_salon_mobile.R;
import com.example.nails_salon_mobile.ui.auth.LoginActivity;
import com.example.nails_salon_mobile.utils.SharedPrefsManager;

public class ProfileFragment extends Fragment {

    private TextView tvUserName, tvUserEmail, tvUserRole;
    private Button btnLogout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        initViews(view);
        loadUserData();
        setupListeners();

        return view;
    }

    private void initViews(View view) {
        tvUserName = view.findViewById(R.id.tv_user_name);
        tvUserEmail = view.findViewById(R.id.tv_user_email);
        tvUserRole = view.findViewById(R.id.tv_user_role);
        btnLogout = view.findViewById(R.id.btn_logout);
    }

    private void loadUserData() {
        SharedPrefsManager prefs = SharedPrefsManager.getInstance(requireContext());

        String name = prefs.getUserName();
        String email = prefs.getUserEmail();
        String role = prefs.getUserRole();

        if (!name.isEmpty()) {
            tvUserName.setText(name);
        }
        if (!email.isEmpty()) {
            tvUserEmail.setText(email);
        }
        if (!role.isEmpty()) {
            String roleText = "Роль: " + getRoleDisplayName(role);
            tvUserRole.setText(roleText);
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

    private void setupListeners() {
        btnLogout.setOnClickListener(v -> logout());
    }

    private void logout() {
        SharedPrefsManager.getInstance(requireContext()).clearAuthData();

        Toast.makeText(getContext(), "Вы вышли из системы", Toast.LENGTH_SHORT).show();

        // Возвращаемся на экран логина
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        if (getActivity() != null) {
            getActivity().finish();
        }
    }
}