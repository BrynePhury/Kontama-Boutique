package com.sweetapps.kontamaboutique.Adapters;


import static com.sweetapps.kontamaboutique.Fragments.DiscoverFragment.discoProductModelsFull;
import static com.sweetapps.kontamaboutique.SearchActivity.productModelsFull;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.sweetapps.kontamaboutique.Models.ProductModel;
import com.sweetapps.kontamaboutique.Prevalent.Prevalent;
import com.sweetapps.kontamaboutique.ProductDetailsActivity;
import com.sweetapps.kontamaboutique.R;

import java.util.ArrayList;
import java.util.List;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ProductsViewHolder> implements Filterable {

    List<ProductModel> productModels;
    Context context;
    int activity;

    public static final int DISCOVER = 890;
    public static final int SEARCH = 190;
    public static final int NONE = 190;


    public ProductsAdapter(List<ProductModel> productModels, int activity) {
        this.productModels = productModels;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ProductsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        context = parent.getContext();
        return new ProductsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductsViewHolder holder, int position) {
        ProductModel model = productModels.get(position);

        Picasso.get().load(model.getDefaultImage())
                .into(holder.product_img, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(model.getDefaultImage())
                                .into(holder.product_img);
                    }
                });

        holder.product_name.setText(model.getProductName());
        holder.product_price.setText("K" + model.getPrice());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, ProductDetailsActivity.class)
                        .putExtra("productID", model.getProductId()));
            }
        });

    }

    @Override
    public int getItemCount() {
        return productModels.size();
    }

    public class ProductsViewHolder extends RecyclerView.ViewHolder {
        ImageView product_img;
        TextView product_name;
        TextView product_price;

        public ProductsViewHolder(@NonNull View itemView) {
            super(itemView);
            product_img = itemView.findViewById(R.id.item_product_img);
            product_name = itemView.findViewById(R.id.item_product_name);
            product_price = itemView.findViewById(R.id.item_price);
        }
    }

    @Override
    public Filter getFilter() {
        return productFilter;
    }

    public boolean containsID(List<ProductModel> list, String ID) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return list.stream().map(ProductModel::getProductId).filter(ID::equals).findFirst().isPresent();
        } else {
            return false;
        }
    }

    private Filter productFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<ProductModel> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                if (activity == SEARCH) {
                    filteredList.addAll(productModelsFull);
                } else if (activity == DISCOVER){
                    filteredList.addAll(discoProductModelsFull);
                }
            } else if (activity == SEARCH){
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (ProductModel productModel : productModelsFull) {
                    if (productModel.getProductName().toLowerCase().contains(filterPattern)) {
                        if (!containsID(filteredList,productModel.getProductId())) {
                            filteredList.add(productModel);
                        }

                    }
                }
                for (ProductModel productModel :productModelsFull) {
                    if (productModel.getDescription().toLowerCase().contains(filterPattern)) {
                        if (!containsID(filteredList,productModel.getProductId())) {
                            filteredList.add(productModel);
                        }
                    }
                }

            } else if (activity == DISCOVER){
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (ProductModel productModel : discoProductModelsFull) {
                    if (productModel.getSub_category() != null &&
                            productModel.getSub_category().toLowerCase().contains(filterPattern)) {
                        if (!containsID(filteredList,productModel.getProductId())) {
                            filteredList.add(productModel);
                        }
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.values != null) {

                List<ProductModel> list = (List<ProductModel>) results.values;
                productModels.clear();
                productModels.addAll(list);

                notifyDataSetChanged();
            }
        }
    };
}
