package com.sweetapps.kontamaboutique.Adapters;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.sweetapps.kontamaboutique.Models.OrderModel;
import com.sweetapps.kontamaboutique.OrderProductsActivity;
import com.sweetapps.kontamaboutique.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    List<OrderModel> orderModels;
    Context context;

    public OrderAdapter(List<OrderModel> orderModels) {
        this.orderModels = orderModels;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_layout_order, parent, false);
        context = parent.getContext();
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        final OrderModel orderModel = orderModels.get(position);

        Picasso.get().load(orderModel.getImg()).into(holder.orderImg, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(Exception e) {
                Picasso.get().load(orderModel.getImg()).into(holder.orderImg);
            }
        });

        holder.order_id.setText(orderModel.getOrderID());
        holder.order_count.setText(orderModel.getItem_count() + " items");
        holder.order_price.setText("K" + orderModel.getTotalAmount());
        holder.order_status.setText(orderModel.getState());

        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(orderModel.getTimeStamp());

        SimpleDateFormat format = new SimpleDateFormat("dd MMM, yyyy");

        holder.order_date.setText(format.format(c.getTime()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, OrderProductsActivity.class);
                intent.putExtra("orderId",orderModel.getOrderID());
                context.startActivity(intent);
            }
        });

        holder.order_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("orderId",holder.order_id.getText());
                clipboardManager.setPrimaryClip(clip);

                Toast.makeText(context, "Copied", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return orderModels.size();
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder {
        ImageView orderImg;
        TextView order_id;
        TextView order_price;
        TextView order_count;
        TextView order_date;
        TextView order_status;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderImg = itemView.findViewById(R.id.order_product_image);
            order_id = itemView.findViewById(R.id.order_id);
            order_price = itemView.findViewById(R.id.order_price);
            order_count = itemView.findViewById(R.id.order_count);
            order_date = itemView.findViewById(R.id.order_date);
            order_status = itemView.findViewById(R.id.order_status);

        }
    }
}
