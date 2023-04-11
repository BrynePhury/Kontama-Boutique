package com.sweetapps.kontamaboutique.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.sweetapps.kontamaboutique.Models.Data;
import com.sweetapps.kontamaboutique.R;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ImageSliderViewHolder>{

    private List<Data> dataList;
    private Context mContext;

    public MyAdapter(List<Data> dataList) {
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public ImageSliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(mContext).inflate(R.layout.slider_item,parent,false);
        return new ImageSliderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageSliderViewHolder holder, int position) {
        holder.setSliderImage(dataList.get(position));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class ImageSliderViewHolder extends RecyclerView.ViewHolder {

        ImageView sliderImage;

        public ImageSliderViewHolder(@NonNull View itemView) {
            super(itemView);

            sliderImage = itemView.findViewById(R.id.slider_image);

        }

        public void setSliderImage(Data data) {
            RequestOptions options = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .centerCrop()
                    .priority(Priority.HIGH)
                    .format(DecodeFormat.PREFER_RGB_565);
            Glide.with(mContext)
                    .applyDefaultRequestOptions(options)
                    .load(data.getUrl())
                    .thumbnail(0.4f)
                    .into(sliderImage);
        }
    }
}
