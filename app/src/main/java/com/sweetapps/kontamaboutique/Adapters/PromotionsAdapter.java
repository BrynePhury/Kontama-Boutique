package com.sweetapps.kontamaboutique.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.sweetapps.kontamaboutique.Models.PromotionModel;
import com.sweetapps.kontamaboutique.R;

import java.util.List;

public class PromotionsAdapter extends RecyclerView.Adapter<PromotionsAdapter.PromotionsViewHolder> {

    List<PromotionModel> models;
    Context context;

    public PromotionsAdapter(List<PromotionModel> models) {
        this.models = models;
    }

    @NonNull
    @Override
    public PromotionsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_promo, parent, false);
        context = parent.getContext();
        return new PromotionsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PromotionsViewHolder holder, int position) {
        PromotionModel model = models.get(position);

        Picasso.get().load(model.getPromoImg())
                .into(holder.promo_img, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(model.getPromoImg())
                                .into(holder.promo_img);
                    }
                });

    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public class PromotionsViewHolder extends RecyclerView.ViewHolder {

        ImageView promo_img;

        public PromotionsViewHolder(@NonNull View itemView) {
            super(itemView);
            promo_img = itemView.findViewById(R.id.promo_img);
        }
    }
}
