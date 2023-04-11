package com.sweetapps.kontamaboutique;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sweetapps.kontamaboutique.Adapters.OrderAdapter;
import com.sweetapps.kontamaboutique.Models.OrderModel;
import com.sweetapps.kontamaboutique.Prevalent.Prevalent;

import java.util.ArrayList;
import java.util.List;

public class OrdersActivity extends AppCompatActivity {

    RecyclerView ordersRecycler;
    ImageView nothing_img;
    TextView nothing_txt;


    List<OrderModel> orders = new ArrayList<>();
    OrderAdapter adapter = new OrderAdapter(orders);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        ordersRecycler = findViewById(R.id.recyclerView);
        nothing_img = findViewById(R.id.nothing_img);
        nothing_txt = findViewById(R.id.nothing_txt);

        ordersRecycler.setHasFixedSize(true);
        ordersRecycler.setLayoutManager(new LinearLayoutManager(this));
        ordersRecycler.setAdapter(adapter);

    }

    @Override
    protected void onStart() {
        super.onStart();

        Prevalent.ORDER_COLLECTION.document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("Orders")
                .orderBy("timeStamp").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            orders.clear();
                            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                OrderModel orderModel = documentSnapshot.toObject(OrderModel.class);
                                orderModel.setOrderID(documentSnapshot.getId());

                                orders.add(orderModel);
                                adapter.notifyDataSetChanged();

                            }
                        } else{
                            nothing_txt.setVisibility(View.VISIBLE);
                            nothing_img.setVisibility(View.VISIBLE);
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(OrdersActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}