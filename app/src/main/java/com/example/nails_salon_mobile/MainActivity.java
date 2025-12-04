package com.example.nails_salon_mobile;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.example.nails_salon_mobile.data.remote.RetrofitClient;
import com.example.nails_salon_mobile.data.remote.models.UserResponseDto;
import com.example.nails_salon_mobile.ui.home.HomeFragment;
import com.example.nails_salon_mobile.ui.services.ServicesFragment;
import com.example.nails_salon_mobile.ui.booking.BookingFragment;
import com.example.nails_salon_mobile.ui.profile.ProfileFragment;
import com.example.nails_salon_mobile.utils.SharedPrefsManager;
import com.example.nails_salon_mobile.utils.UserProfileManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ИНИЦИАЛИЗАЦИЯ RETROFIT
        RetrofitClient.init(getApplicationContext());

        initViews();
        setupToolbar();
        setupBottomNavigation();

        // Загружаем первый фрагмент
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }

        // ОБНОВЛЯЕМ ПРОФИЛЬ ПОЛЬЗОВАТЕЛЯ ПРИ ЗАПУСКЕ
        refreshUserProfileOnStart();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }
    }

    private void refreshUserProfileOnStart() {
        if (SharedPrefsManager.getInstance(this).isLoggedIn()) {
            // Фоновое обновление профиля
            UserProfileManager.getInstance(this).refreshProfileIfNeeded();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.action_search) {
            Toast.makeText(this, "Поиск", Toast.LENGTH_SHORT).show();
            return true;
        } else if (itemId == R.id.action_settings) {
            Toast.makeText(this, "Настройки", Toast.LENGTH_SHORT).show();
            return true;
        } else if (itemId == R.id.action_help) {
            Toast.makeText(this, "Бог в помощь", Toast.LENGTH_SHORT).show();
            return true;
        } else if (itemId == R.id.action_about) {
            Toast.makeText(this, "О приложении v1.0", Toast.LENGTH_SHORT).show();
            return true;
        } else if (itemId == R.id.action_refresh_profile) { // ДОБАВИМ ЭТОТ ПУНКТ
            refreshUserProfile();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Метод для ручного обновления профиля
    private void refreshUserProfile() {
        if (!SharedPrefsManager.getInstance(this).isLoggedIn()) {
            Toast.makeText(this, "Вы не авторизованы", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Обновление профиля...", Toast.LENGTH_SHORT).show();

        UserProfileManager.getInstance(this).refreshUserProfile(
                new UserProfileManager.ProfileRefreshCallback() {
                    @Override
                    public void onSuccess(UserResponseDto user) {
                        Toast.makeText(MainActivity.this,
                                "Профиль обновлен: " + user.getFirstName(),
                                Toast.LENGTH_SHORT).show();

                        // Обновляем текущий фрагмент, если это нужно
                        refreshCurrentFragment();
                    }

                    @Override
                    public void onFailure(String error) {
                        Toast.makeText(MainActivity.this,
                                "Не удалось обновить профиль: " + error,
                                Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    // Обновить текущий фрагмент (если это HomeFragment или ProfileFragment)
    private void refreshCurrentFragment() {
        Fragment currentFragment = getSupportFragmentManager()
                .findFragmentById(R.id.fragment_container);

        if (currentFragment instanceof HomeFragment) {
            ((HomeFragment) currentFragment).refreshWelcomeMessage();
        } else if (currentFragment instanceof ProfileFragment) {
            ((ProfileFragment) currentFragment).refreshUserData();
        }
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                selectedFragment = new HomeFragment();
                toolbar.setTitle("Главная");
            } else if (itemId == R.id.nav_services) {
                selectedFragment = new ServicesFragment();
                toolbar.setTitle("Услуги");
            } else if (itemId == R.id.nav_booking) {
                selectedFragment = new BookingFragment();
                toolbar.setTitle("Запись");
            } else if (itemId == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
                toolbar.setTitle("Профиль");
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
                return true;
            }

            return false;
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }
}