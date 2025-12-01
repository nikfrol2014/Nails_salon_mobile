package com.example.nails_salon_mobile.ui.services;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.nails_salon_mobile.R;
import com.example.nails_salon_mobile.data.remote.models.NailServiceDto;
import java.util.List;

public class ServicesAdapter extends RecyclerView.Adapter<ServicesAdapter.ServiceViewHolder> {

    private List<NailServiceDto> services;

    public ServicesAdapter(List<NailServiceDto> services) {
        this.services = services;
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_service, parent, false);
        return new ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        NailServiceDto service = services.get(position);
        holder.bind(service);
    }

    @Override
    public int getItemCount() {
        return services.size();
    }

    static class ServiceViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName, tvPrice, tvDuration;

        public ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_service_name);
            tvPrice = itemView.findViewById(R.id.tv_service_price);
            tvDuration = itemView.findViewById(R.id.tv_service_duration);
        }

        public void bind(NailServiceDto service) {
            tvName.setText(service.getName());
            tvPrice.setText(String.format("Цена: %.0f руб.", service.getPrice()));
            tvDuration.setText(String.format("Длительность: %d мин", service.getDurationMinutes()));
        }
    }
}