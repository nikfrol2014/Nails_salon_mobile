package com.example.nails_salon_mobile.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.nails_salon_mobile.R;
import java.util.List;

public class PromoPagerAdapter extends RecyclerView.Adapter<PromoPagerAdapter.PromoViewHolder> {

    private final List<String> titles;
    private final List<String> descriptions;
    private final LayoutInflater inflater;

    public PromoPagerAdapter(android.content.Context context,
                             List<String> titles, List<String> descriptions) {
        this.titles = titles;
        this.descriptions = descriptions;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public PromoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_promo_slide, parent, false);
        return new PromoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PromoViewHolder holder, int position) {
        holder.bind(titles.get(position), descriptions.get(position));
    }

    @Override
    public int getItemCount() {
        return titles.size();
    }

    static class PromoViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvTitle;
        private final TextView tvDescription;

        public PromoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_promo_title);
            tvDescription = itemView.findViewById(R.id.tv_promo_description);
        }

        public void bind(String title, String description) {
            tvTitle.setText(title);
            tvDescription.setText(description);
        }
    }
}