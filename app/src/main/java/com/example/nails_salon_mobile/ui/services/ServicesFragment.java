package com.example.nails_salon_mobile.ui.services;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.nails_salon_mobile.R;
import com.example.nails_salon_mobile.data.remote.RetrofitClient;
import com.example.nails_salon_mobile.data.remote.api.ServicesApi;
import com.example.nails_salon_mobile.data.remote.models.NailServiceDto;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServicesFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private ServicesAdapter adapter;
    private List<NailServiceDto> servicesList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_services, container, false);

        recyclerView = view.findViewById(R.id.recycler_services);
        progressBar = view.findViewById(R.id.progress_bar);

        setupRecyclerView();
        loadServices();

        return view;
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ServicesAdapter(servicesList);
        recyclerView.setAdapter(adapter);
    }

    private void loadServices() {
        progressBar.setVisibility(View.VISIBLE);

        ServicesApi servicesApi = RetrofitClient.getClient().create(ServicesApi.class);
        Call<List<NailServiceDto>> call = servicesApi.getAllServices();

        call.enqueue(new Callback<List<NailServiceDto>>() {
            @Override
            public void onResponse(Call<List<NailServiceDto>> call, Response<List<NailServiceDto>> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    servicesList.clear();
                    servicesList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Ошибка загрузки услуг", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<NailServiceDto>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}