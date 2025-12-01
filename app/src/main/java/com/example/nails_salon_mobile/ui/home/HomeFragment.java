package com.example.nails_salon_mobile.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import com.example.nails_salon_mobile.R;
import com.example.nails_salon_mobile.ui.home.PromoPagerAdapter;
import com.example.nails_salon_mobile.utils.SharedPrefsManager;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import java.util.Arrays;
import java.util.List;

public class HomeFragment extends Fragment {

    private TextView tvWelcome;
    private ViewPager2 viewPager;
    private TabLayout tabLayout;

    // Данные для ViewPager (промо-акции)
    private final List<String> promoTitles = Arrays.asList(
            "Скидка 20% на первый визит",
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

        return view;
    }

    private void initViews(View view) {
        tvWelcome = view.findViewById(R.id.tv_welcome);
        viewPager = view.findViewById(R.id.view_pager);
        tabLayout = view.findViewById(R.id.tab_layout);
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

    private void setupWelcomeMessage() {
        String userName = SharedPrefsManager.getInstance(requireContext()).getUserName();
        String userEmail = SharedPrefsManager.getInstance(requireContext()).getUserEmail();

        if (!userEmail.isEmpty()) {
            // Показываем email, так как имени нет в JWT
            tvWelcome.setText("Добро пожаловать, " + userEmail + "!");
        } else {
            tvWelcome.setText("Добро пожаловать в Nail Salon!");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewPager = null; // Очищаем ссылку
    }
}