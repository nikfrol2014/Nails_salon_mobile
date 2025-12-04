package com.example.nails_salon_mobile.ui.home;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.nails_salon_mobile.R;
import com.example.nails_salon_mobile.utils.SharedPrefsManager;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Arrays;
import java.util.List;

public class HomeFragment extends Fragment {

    private TextView tvWelcome;
    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private Button btnQuickBook, btnMyAppointments, btnMoreOptions;

    // Данные для ViewPager (промо-акции)
    private final List<String> promoTitles = Arrays.asList(
            "Скидка 15% на первый визит",
            "Комплексный уход за 2500 руб",
            "Бесплатный дизайн при наращивании",
            "Акция на педикюр + маникюр"
    );

    private final List<String> promoDescriptions = Arrays.asList(
            "Для новых клиентов специальное предложение",
            "Полный комплекс услуг по специальной цене",
            "При наращивании ногтей дизайн в подарок",
            "Комбо-предложение со скидкой 15%"
    );

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        initViews(view);
        setupViewPager();
        setupWelcomeMessage();
        setupListeners();

        return view;
    }

    private void initViews(View view) {
        tvWelcome = view.findViewById(R.id.tv_welcome);
        viewPager = view.findViewById(R.id.view_pager);
        tabLayout = view.findViewById(R.id.tab_layout);
        btnQuickBook = view.findViewById(R.id.btn_quick_book);
        btnMyAppointments = view.findViewById(R.id.btn_my_appointments);
        btnMoreOptions = view.findViewById(R.id.btn_more_options);
    }

    private void setupViewPager() {
        // Создаем адаптер для ViewPager2
        PromoPagerAdapter adapter = new PromoPagerAdapter(
                requireActivity(),
                promoTitles,
                promoDescriptions
        );

        viewPager.setAdapter(adapter);

        // Связываем ViewPager2 с TabLayout
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText("Акция " + (position + 1))
        ).attach();

        // Автопрокрутка каждые 3 секунды
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                // Сбрасываем таймер при ручном перелистывании
            }
        });

        startAutoScroll();
    }

    private void setupListeners() {
        // 1. Кнопка "Записаться" - переход в BookingFragment
        btnQuickBook.setOnClickListener(v -> {
            navigateToBooking();
        });

        // 2. Кнопка "Мои записи" - переход в ProfileFragment
        btnMyAppointments.setOnClickListener(v -> {
            navigateToMyAppointments();
        });

        // 3. Кнопка "Еще" - popup меню
        btnMoreOptions.setOnClickListener(v -> {
            showMoreOptionsPopup(v);
        });
    }

    private void navigateToBooking() {
        // Проверяем авторизацию
        if (!SharedPrefsManager.getInstance(requireContext()).isLoggedIn()) {
            Toast.makeText(getContext(),
                    "Для записи необходимо войти в систему",
                    Toast.LENGTH_SHORT).show();

            // Можно предложить переход на экран логина
            // navigateToLogin();
            return;
        }

        // Переход в BookingFragment через BottomNavigationView
        // В MainActivity у нас есть BottomNavigationView с ID nav_booking
        if (getActivity() != null) {
            // Получаем BottomNavigationView из MainActivity
            com.google.android.material.bottomnavigation.BottomNavigationView bottomNav =
                    getActivity().findViewById(R.id.bottom_navigation);

            if (bottomNav != null) {
                // Выбираем пункт "Запись" (ID: nav_booking)
                bottomNav.setSelectedItemId(R.id.nav_booking);
            } else {
                Toast.makeText(getContext(),
                        "Навигация временно недоступна",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void navigateToMyAppointments() {
        // Проверяем авторизацию
        if (!SharedPrefsManager.getInstance(requireContext()).isLoggedIn()) {
            Toast.makeText(getContext(),
                    "Для просмотра записей необходимо войти в систему",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Переход в ProfileFragment (там уже есть список записей)
        if (getActivity() != null) {
            com.google.android.material.bottomnavigation.BottomNavigationView bottomNav =
                    getActivity().findViewById(R.id.bottom_navigation);

            if (bottomNav != null) {
                // Выбираем пункт "Профиль" (ID: nav_profile)
                bottomNav.setSelectedItemId(R.id.nav_profile);

                // Можно добавить scroll к списку записей
                // Для этого нужно передать параметр в ProfileFragment
            } else {
                Toast.makeText(getContext(),
                        "Навигация временно недоступна",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showMoreOptionsPopup(View anchor) {
        PopupMenu popupMenu = new PopupMenu(requireContext(), anchor);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.popup_menu_home, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.popup_contact) {
                showContactInfo();
                return true;
            } else if (itemId == R.id.popup_location) {
                showLocationInfo();
                return true;
            } else if (itemId == R.id.popup_schedule) {
                showScheduleInfo();
                return true;
            } else if (itemId == R.id.popup_feedback) {
                showFeedbackDialog();
                return true;
            }
            return false;
        });

        popupMenu.show();
    }

    private void showContactInfo() {
        // Показываем контактную информацию
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(requireContext());
        builder.setTitle("Контакты")
                .setMessage("Nail Salon\n\n" +
                        "📞 Телефон разработчика: +7 (929) 049-82-37 - Фролов Никита Максимович\n" +
                        "📧 Email: nikitka_frolov_2014@inbox.ru\n" +
                        "🌐 Сайт: https://nails-salon.whysargis.ru")
                .setPositiveButton("OK", null)
                .setNeutralButton("Позвонить", (dialog, which) -> {
                    // Открыть набор номера
                    try {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:+79290498237"));
                        startActivity(intent);
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Не удалось открыть звонок", Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }

    private void showLocationInfo() {
        // Показываем информацию о местоположении
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(requireContext());
        builder.setTitle("Как добраться")
                .setMessage("Адрес: г. Н.Новгород, ул. Лескова, д. 2\n\n" +
                        "🚇 Метро: «Парк Культуры»\n" +
                        "🚗 Парковка: есть бесплатная придомовая парковка")
                .setPositiveButton("OK", null)
                .setNeutralButton("Открыть карту", (dialog, which) -> {
                    // Открыть карту
                    try {
                        Uri gmmIntentUri = Uri.parse("geo:56.23806,43.86656?q=салон+красоты");
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        startActivity(mapIntent);
                    } catch (Exception e) {
                        Toast.makeText(getContext(),
                                "Установите Google Карты",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }

    private void showScheduleInfo() {
        // Показываем график работы
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(requireContext());
        builder.setTitle("График работы")
                .setMessage("🕘 Часы работы:\n\n" +
                        "Понедельник - Пятница: 9:00 - 21:00\n" +
                        "Суббота: 9:00 - 21:00\n" +
                        "Воскресенье: 9:00 - 21:00\n\n" +
                        "📅 Последняя запись за 2 часа до закрытия")
                .setPositiveButton("OK", null)
                .show();
    }

    private void showFeedbackDialog() {
        // Диалог для отзыва
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(requireContext());

        // Создаем View для диалога
        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_feedback, null);

        builder.setView(dialogView)
                .setTitle("Оставить отзыв")
                .setPositiveButton("Отправить", (dialog, which) -> {
                    // Здесь будет отправка отзыва
                    Toast.makeText(getContext(),
                            "Спасибо за ваш отзыв!",
                            Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Отмена", null)
                .show();
    }

    private void startAutoScroll() {
        viewPager.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (viewPager != null) {
                    int currentItem = viewPager.getCurrentItem();
                    int nextItem = (currentItem + 1) % promoTitles.size();
                    viewPager.setCurrentItem(nextItem, true);
                    viewPager.postDelayed(this, 3000); // 3 секунды
                }
            }
        }, 3000);
    }

    public void refreshWelcomeMessage() {
        if (isAdded() && getView() != null) {
            setupWelcomeMessage();
        }
    }

    private void setupWelcomeMessage() {
        SharedPrefsManager prefs = SharedPrefsManager.getInstance(requireContext());

        // Пробуем получить полное имя
        String fullName = prefs.getUserName();
        Log.d("HomeFragment", "Полное имя из prefs: '" + fullName + "'");

        if (fullName != null && !fullName.trim().isEmpty() && !fullName.equals(" ")) {
            // Показываем имя
            tvWelcome.setText("Добро пожаловать, " + fullName.trim() + "!");
            Log.d("HomeFragment", "Показываем имя: " + fullName);
        } else {
            // Пробуем собрать из компонентов
            String firstName = prefs.getUserFirstName();
            String lastName = prefs.getUserLastName();
            String name = (firstName + " " + lastName).trim();

            Log.d("HomeFragment", "Компоненты: firstName='" + firstName +
                    "', lastName='" + lastName + "', name='" + name + "'");

            if (!name.isEmpty() && !name.equals(" ")) {
                tvWelcome.setText("Добро пожаловать, " + name + "!");
                Log.d("HomeFragment", "Показываем собранное имя: " + name);
            } else {
                // Показываем email
                String email = prefs.getUserEmail();
                if (!email.isEmpty()) {
                    tvWelcome.setText("Добро пожаловать, " + email + "!");
                    Log.d("HomeFragment", "Показываем email: " + email);
                } else {
                    tvWelcome.setText("Добро пожаловать в Nail Salon!");
                    Log.d("HomeFragment", "Показываем общее приветствие");
                }
            }
        }

        // Добавим логирование всех данных для отладки
        Log.d("HomeFragment", "Все данные из SharedPrefs:");
        Log.d("HomeFragment", "Email: " + prefs.getUserEmail());
        Log.d("HomeFragment", "FirstName: " + prefs.getUserFirstName());
        Log.d("HomeFragment", "LastName: " + prefs.getUserLastName());
        Log.d("HomeFragment", "Phone: " + prefs.getUserPhone());
        Log.d("HomeFragment", "UserName: " + prefs.getUserName());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewPager = null; // Очищаем ссылку
    }
}