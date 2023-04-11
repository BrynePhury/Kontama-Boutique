package com.sweetapps.kontamaboutique;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.sweetapps.kontamaboutique.Adapters.ProductsAdapter;
import com.sweetapps.kontamaboutique.Models.OrderModel;
import com.sweetapps.kontamaboutique.Models.ProductModel;
import com.sweetapps.kontamaboutique.Prevalent.Prevalent;

import java.util.ArrayList;
import java.util.List;

public class OrderProductsActivity extends AppCompatActivity {

    RecyclerView productsRecycler;

    ProductsAdapter adapter;

    List<ProductModel> models;

    String orderID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_products);

        models = new ArrayList<>();
        adapter = new ProductsAdapter(models,ProductsAdapter.NONE);

        productsRecycler = findViewById(R.id.recyclerView);
        productsRecycler.setLayoutManager(new GridLayoutManager(this,2));
        productsRecycler.setHasFixedSize(true);
        productsRecycler.setAdapter(adapter);

        orderID = getIntent().getStringExtra("orderId");
    }

    @Override
    protected void onStart() {
        super.onStart();

        Prevalent.ORDER_COLLECTION.document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("Orders")
                .document(orderID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        models.clear();
                        OrderModel orderModel = documentSnapshot.toObject(OrderModel.class);
                        orderModel.setOrderID(documentSnapshot.getId());

                        for (String productId: orderModel.getProducts()){
                            Prevalent.PRODUCTS_COLLECTION
                                    .document(productId)
                                    .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            ProductModel productModel = documentSnapshot.toObject(ProductModel.class);
                                            productModel.setProductId(documentSnapshot.getId());

                                            models.add(productModel);
                                            adapter.notifyDataSetChanged();
                                        }
                                    });
                        }
                    }
                });
    }
}