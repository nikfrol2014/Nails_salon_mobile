package com.example.nails_salon_mobile.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import com.example.nails_salon_mobile.MainActivity;
import com.example.nails_salon_mobile.R;
import com.example.nails_salon_mobile.ui.auth.LoginActivity;
import com.example.nails_salon_mobile.utils.SharedPrefsManager;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 2000; // 2 секунды

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Скрываем ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        com.example.nails_salon_mobile.data.remote.RetrofitClient.init(getApplicationContext());

        // Анимация логотипа
        ImageView logo = findViewById(R.id.imageView);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.logo_animation);
        logo.startAnimation(animation);

        // Запускаем переход после задержки
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            checkAuthAndNavigate();
        }, SPLASH_DELAY);
    }

    private void checkAuthAndNavigate() {
        SharedPrefsManager prefs = SharedPrefsManager.getInstance(this);

        if (prefs.isLoggedIn()) {
            // Пользователь авторизован - идем в MainActivity
            startActivity(new Intent(this, MainActivity.class));
        } else {
            // Пользователь не авторизован - идем в LoginActivity
            startActivity(new Intent(this, LoginActivity.class));
        }

        finish(); // Закрываем SplashActivity
    }
}