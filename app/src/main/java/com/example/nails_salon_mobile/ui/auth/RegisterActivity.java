package com.example.nails_salon_mobile.ui.auth;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.nails_salon_mobile.MainActivity;
import com.example.nails_salon_mobile.R;
import com.example.nails_salon_mobile.data.remote.RetrofitClient;
import com.example.nails_salon_mobile.data.remote.api.AuthApi;
import com.example.nails_salon_mobile.data.remote.models.JwtResponse;
import com.example.nails_salon_mobile.data.remote.models.LoginRequest;
import com.example.nails_salon_mobile.data.remote.models.RegisterRequest;
import com.example.nails_salon_mobile.data.remote.models.UserResponseDto;
import com.example.nails_salon_mobile.utils.SharedPrefsManager;
import org.threeten.bp.LocalDate;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.Calendar;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity {

    private EditText etEmail, etPassword, etFirstName, etLastName, etPhone, etBirthdate;
    private TextView btnRegister, btnLoginRedirect;
    private Button btnPickDate;
    private ImageButton btnTogglePassword;
    private AuthApi authApi;

    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();
        setupRetrofit();
        setupListeners();
    }

    private void initViews() {
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etPhone = findViewById(R.id.etPhone);
        etBirthdate = findViewById(R.id.etBirthdate);
        btnRegister = findViewById(R.id.btnRegister);
        btnLoginRedirect = findViewById(R.id.btnLoginRedirect);
        btnPickDate = findViewById(R.id.btnPickDate);
        btnTogglePassword = findViewById(R.id.btnTogglePassword);
    }

    private void setupRetrofit() {
        RetrofitClient.init(getApplicationContext());
        authApi = RetrofitClient.getClient().create(AuthApi.class);
    }

    private void setupListeners() {
        btnRegister.setOnClickListener(v -> attemptRegister());
        btnLoginRedirect.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
        btnPickDate.setOnClickListener(v -> showDatePicker());
        etBirthdate.setOnClickListener(v -> showDatePicker());
        btnTogglePassword.setOnClickListener(v -> togglePasswordVisibility());
    }

    private void togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible;

        if (isPasswordVisible) {
            // Показать пароль
            etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            btnTogglePassword.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
        } else {
            // Скрыть пароль
            etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            btnTogglePassword.setImageResource(android.R.drawable.ic_menu_view);
        }

        // Переместить курсор в конец
        etPassword.setSelection(etPassword.getText().length());
    }

    private void showDatePicker() {
        // По умолчанию - 18 лет назад
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -18);

        DatePickerDialog datePicker = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    // Форматируем как ГГГГ-ММ-ДД
                    String formattedDate = String.format(Locale.getDefault(),
                            "%04d-%02d-%02d", year, month + 1, dayOfMonth);
                    etBirthdate.setText(formattedDate);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        // Максимальная дата - сегодня
        datePicker.getDatePicker().setMaxDate(System.currentTimeMillis());

        // Минимальная дата - 100 лет назад
        Calendar minDate = Calendar.getInstance();
        minDate.add(Calendar.YEAR, -100);
        datePicker.getDatePicker().setMinDate(minDate.getTimeInMillis());

        datePicker.show();
    }

    private void attemptRegister() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String birthdateStr = etBirthdate.getText().toString().trim();

        // Валидация обязательных полей
        if (email.isEmpty() || password.isEmpty() || firstName.isEmpty() ||
                lastName.isEmpty() || phone.isEmpty() || birthdateStr.isEmpty()) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        // Валидация email
        if (!isValidEmail(email)) {
            Toast.makeText(this, "Введите корректный email", Toast.LENGTH_SHORT).show();
            etEmail.requestFocus();
            return;
        }

        // Валидация пароля
        String passwordError = validatePassword(password);
        if (passwordError != null) {
            Toast.makeText(this, passwordError, Toast.LENGTH_SHORT).show();
            etPassword.requestFocus();
            return;
        }

        // Валидация телефона
        if (!isValidPhone(phone)) {
            Toast.makeText(this, "Введите телефон в формате +7XXXXXXXXXX", Toast.LENGTH_SHORT).show();
            etPhone.requestFocus();
            return;
        }

        // Парсим дату
        LocalDate birthdate;
        try {
            birthdate = LocalDate.parse(birthdateStr); // Формат: "1990-05-15"
        } catch (Exception e) {
            Toast.makeText(this, "Неверный формат даты. Используйте: ГГГГ-ММ-ДД", Toast.LENGTH_SHORT).show();
            etBirthdate.requestFocus();
            return;
        }

        // Проверка возраста (минимум 16 лет)
        if (calculateAge(birthdate) < 16) {
            Toast.makeText(this, "Вам должно быть не менее 16 лет", Toast.LENGTH_SHORT).show();
            etBirthdate.requestFocus();
            return;
        }

        // Показываем прогресс
        btnRegister.setEnabled(false);
        btnLoginRedirect.setEnabled(false);
        btnRegister.setText("Регистрация...");

        // Создаем запрос
        RegisterRequest registerRequest = new RegisterRequest(
                email, password, firstName, lastName, phone, birthdate
        );

        Call<UserResponseDto> call = authApi.register(registerRequest);

        call.enqueue(new Callback<UserResponseDto>() {
            @Override
            public void onResponse(Call<UserResponseDto> call, Response<UserResponseDto> response) {
                btnRegister.setEnabled(true);
                btnLoginRedirect.setEnabled(true);
                btnRegister.setText("Зарегистрироваться");

                if (response.isSuccessful() && response.body() != null) {
                    UserResponseDto user = response.body();

                    Toast.makeText(RegisterActivity.this,
                            "Регистрация успешна! Добро пожаловать, " + user.getFirstName() + "!",
                            Toast.LENGTH_SHORT).show();

                    // АВТОМАТИЧЕСКИ ЛОГИНИМСЯ ПОСЛЕ РЕГИСТРАЦИИ
                    autoLoginAfterRegister(email, password);

                } else {
                    String errorMessage = "Ошибка регистрации";
                    if (response.code() == 400) {
                        errorMessage = "Пользователь с таким email уже существует";
                    } else if (response.code() == 422) {
                        errorMessage = "Некорректные данные";
                    }
                    Toast.makeText(RegisterActivity.this,
                            errorMessage + " (код: " + response.code() + ")",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserResponseDto> call, Throwable t) {
                btnRegister.setEnabled(true);
                btnLoginRedirect.setEnabled(true);
                btnRegister.setText("Зарегистрироваться");
                Toast.makeText(RegisterActivity.this,
                        "Ошибка сети: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Методы валидации
    private boolean isValidEmail(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}";
        return email.matches(emailPattern);
    }

    private String validatePassword(String password) {
        if (password.length() < 8) {
            return "Пароль должен содержать минимум 8 символов";
        }
        if (!password.matches(".*[0-9].*")) {
            return "Пароль должен содержать хотя бы одну цифру";
        }
        if (!password.matches(".*[a-zA-Z].*")) {
            return "Пароль должен содержать хотя бы одну букву";
        }
        return null;
    }

    private boolean isValidPhone(String phone) {
        // Российский формат: +7XXXXXXXXXX (11 цифр после +7)
        return phone.matches("^\\+7\\d{10}$");
    }

    private int calculateAge(LocalDate birthdate) {
        LocalDate now = LocalDate.now();
        return now.getYear() - birthdate.getYear() -
                (now.getDayOfYear() < birthdate.getDayOfYear() ? 1 : 0);
    }

    // Автологин после регистрации
    private void autoLoginAfterRegister(String email, String password) {
        LoginRequest loginRequest = new LoginRequest(email, password);

        Call<JwtResponse> call = authApi.login(loginRequest);

        call.enqueue(new Callback<JwtResponse>() {
            @Override
            public void onResponse(Call<JwtResponse> call, Response<JwtResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JwtResponse jwtResponse = response.body();

                    // Сохраняем токены
                    SharedPrefsManager prefs = SharedPrefsManager.getInstance(RegisterActivity.this);
                    prefs.saveAuthTokens(
                            jwtResponse.getAccessToken(),
                            jwtResponse.getRefreshToken(),
                            jwtResponse.getUserId(),
                            jwtResponse.getRole(),
                            jwtResponse.getEmail()
                    );

                    // Получаем полные данные пользователя
                    fetchUserProfile(jwtResponse.getAccessToken());

                } else {
                    // Если автологин не удался, идем на экран логина
                    Toast.makeText(RegisterActivity.this,
                            "Регистрация успешна! Теперь войдите в систему",
                            Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    intent.putExtra("email", email);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<JwtResponse> call, Throwable t) {
                // При ошибке сети идем на логин
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                intent.putExtra("email", email);
                startActivity(intent);
                finish();
            }
        });
    }

    // Получение профиля
    private void fetchUserProfile(String accessToken) {
        Call<UserResponseDto> call = authApi.getCurrentUser("Bearer " + accessToken);

        call.enqueue(new Callback<UserResponseDto>() {
            @Override
            public void onResponse(Call<UserResponseDto> call, Response<UserResponseDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserResponseDto user = response.body();

                    // Сохраняем ВСЕ данные пользователя
                    SharedPrefsManager prefs = SharedPrefsManager.getInstance(RegisterActivity.this);
                    prefs.saveUserData(
                            accessToken,
                            prefs.getRefreshToken(),
                            user.getUserId(),
                            user.getRole(),
                            user.getEmail(),
                            user.getFirstName(),
                            user.getLastName(),
                            user.getPhone()
                    );
                }
                // Переходим в главное приложение
                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                finish();
            }

            @Override
            public void onFailure(Call<UserResponseDto> call, Throwable t) {
                // Все равно переходим
                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                finish();
            }
        });
    }
}