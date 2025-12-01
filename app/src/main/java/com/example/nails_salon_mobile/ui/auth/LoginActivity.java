package com.example.nails_salon_mobile.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.nails_salon_mobile.MainActivity;
import com.example.nails_salon_mobile.R;
import com.example.nails_salon_mobile.data.remote.RetrofitClient;
import com.example.nails_salon_mobile.data.remote.api.AuthApi;
import com.example.nails_salon_mobile.data.remote.models.JwtResponse;
import com.example.nails_salon_mobile.data.remote.models.LoginRequest;
import com.example.nails_salon_mobile.utils.SharedPrefsManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private AuthApi authApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Проверяем, авторизован ли уже пользователь
        if (SharedPrefsManager.getInstance(this).isLoggedIn()) {
            startMainActivity();
            return;
        }

        initViews();
        setupRetrofit();
        setupListeners();
    }

    private void initViews() {
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
    }

    private void setupRetrofit() {
        // Инициализируем Retrofit
        RetrofitClient.init(getApplicationContext());
        authApi = RetrofitClient.getClient().create(AuthApi.class);
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });
    }

    private void attemptLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        // Показываем прогресс
        btnLogin.setText("Вход...");
        btnLogin.setEnabled(false);

        LoginRequest loginRequest = new LoginRequest(email, password);

        Call<JwtResponse> call = authApi.login(loginRequest);

        call.enqueue(new Callback<JwtResponse>() {
            @Override
            public void onResponse(Call<JwtResponse> call, Response<JwtResponse> response) {
                btnLogin.setText("Войти");
                btnLogin.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    JwtResponse jwtResponse = response.body();

                    // Используем метод с 4 параметрами (без email в методе)
                    SharedPrefsManager.getInstance(LoginActivity.this).saveAuthTokens(
                            jwtResponse.getAccessToken(),
                            jwtResponse.getRefreshToken(),
                            jwtResponse.getUserId(),
                            jwtResponse.getRole()
                    );

                    // Дополнительно сохраняем email отдельно
                    SharedPrefsManager.getInstance(LoginActivity.this).updateUserEmail(
                            jwtResponse.getEmail()
                    );

                    Toast.makeText(LoginActivity.this, "Успешный вход!", Toast.LENGTH_SHORT).show();
                    startMainActivity();
                } else {
                    // Покажем более информативную ошибку
                    String errorMessage = "Ошибка входа";
                    if (response.code() == 401) {
                        errorMessage = "Неверный email или пароль";
                    } else if (response.code() == 400) {
                        errorMessage = "Некорректные данные";
                    }
                    Toast.makeText(LoginActivity.this, errorMessage + " (код: " + response.code() + ")",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JwtResponse> call, Throwable t) {
                btnLogin.setText("Войти");
                btnLogin.setEnabled(true);
                Toast.makeText(LoginActivity.this, "Ошибка сети: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
                t.printStackTrace(); // Выведем стектрейс в Logcat
            }
        });
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}