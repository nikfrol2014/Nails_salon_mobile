package com.example.nails_salon_mobile.ui.profile;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nails_salon_mobile.R;
import com.example.nails_salon_mobile.data.local.entities.AppointmentEntity;

import org.threeten.bp.format.DateTimeFormatter;

import java.util.List;
import java.util.Locale;

public class AppointmentsAdapter extends RecyclerView.Adapter<AppointmentsAdapter.AppointmentViewHolder> {

    private List<AppointmentEntity> appointments;
    private OnAppointmentClickListener listener;

    public interface OnAppointmentClickListener {
        void onAppointmentClick(AppointmentEntity appointment);
        void onCancelClick(AppointmentEntity appointment);
    }

    public AppointmentsAdapter(List<AppointmentEntity> appointments, OnAppointmentClickListener listener) {
        this.appointments = appointments;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_appointment, parent, false);
        return new AppointmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentViewHolder holder, int position) {
        AppointmentEntity appointment = appointments.get(position);
        holder.bind(appointment);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAppointmentClick(appointment);
            }
        });

        holder.btnCancel.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCancelClick(appointment);
            }
        });
    }

    @Override
    public int getItemCount() {
        return appointments.size();
    }

    static class AppointmentViewHolder extends RecyclerView.ViewHolder {
        private TextView tvServiceName, tvStatus, tvMasterName, tvDateTime, tvPrice;
        private Button btnCancel;

        public AppointmentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvServiceName = itemView.findViewById(R.id.tv_service_name);
            tvStatus = itemView.findViewById(R.id.tv_status);
            tvMasterName = itemView.findViewById(R.id.tv_master_name);
            tvDateTime = itemView.findViewById(R.id.tv_date_time);
            tvPrice = itemView.findViewById(R.id.tv_price);
            btnCancel = itemView.findViewById(R.id.btn_cancel);
        }

        public void bind(AppointmentEntity appointment) {
            // Услуга
            tvServiceName.setText(appointment.getServiceName() != null ?
                    appointment.getServiceName() : "Услуга");

            // Статус
            String status = appointment.getStatus();
            tvStatus.setText(getStatusDisplayName(status));
            tvStatus.setBackgroundResource(getStatusBackground(status));

            // Мастер
            String masterText = "Мастер: " +
                    (appointment.getMasterName() != null ? appointment.getMasterName() : "Не указан");
            tvMasterName.setText(masterText);

            // Дата и время
            if (appointment.getAppointmentDatetime() != null) {
                String dateTime = appointment.getAppointmentDatetime()
                        .format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm", Locale.getDefault()));
                tvDateTime.setText("Дата и время: " + dateTime);
            } else {
                tvDateTime.setText("Дата не указана");
            }

            // Цена
            if (appointment.getPrice() != null) {
                tvPrice.setText("Цена: " + appointment.getPrice() + " руб.");
            } else {
                tvPrice.setText("Цена не указана");
            }

            // Кнопка отмены (только для активных записей)
            boolean canCancel = "BOOKED".equals(status) || "CONFIRMED".equals(status);
            btnCancel.setVisibility(canCancel ? View.VISIBLE : View.GONE);
        }

        private String getStatusDisplayName(String status) {
            if (status == null) return "Неизвестно";

            switch (status.toUpperCase()) {
                case "BOOKED": return "Забронировано";
                case "CONFIRMED": return "Подтверждено";
                case "COMPLETED": return "Завершено";
                case "CANCELLED": return "Отменено";
                case "PENDING_SYNC": return "Ожидает синхронизации";
                default: return status;
            }
        }

        private int getStatusBackground(String status) {
            if (status == null) return R.drawable.status_background;

            switch (status.toUpperCase()) {
                case "BOOKED": return R.color.status_booked;
                case "CONFIRMED": return R.color.status_confirmed;
                case "COMPLETED": return R.color.status_completed;
                case "CANCELLED": return R.color.status_cancelled;
                case "PENDING_SYNC": return R.color.status_pending;
                default: return R.color.status_pending;
            }
        }
    }
}