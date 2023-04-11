package com.sweetapps.kontamaboutique;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.sweetapps.kontamaboutique.Adapters.MyAdapter;
import com.sweetapps.kontamaboutique.Models.CartItem;
import com.sweetapps.kontamaboutique.Models.Data;
import com.sweetapps.kontamaboutique.Models.ProductModel;
import com.sweetapps.kontamaboutique.Prevalent.Prevalent;
import com.tbuonomo.viewpagerdotsindicator.SpringDotsIndicator;

import java.util.ArrayList;
import java.util.List;

public class ProductDetailsActivity extends AppCompatActivity {

    TextView product_name_det, product_price_det, product_desc_det;
    MaterialButton add_to_cart;
    CardView quick_buy;
    RadioGroup radioGroup;
    RadioGroup colorRadioGroup;
    ViewPager2 viewPager;
    SpringDotsIndicator dotsIndicator;

    MyAdapter adapter;
    String price = "0";
    ProductModel productModel;
    String productID;
    private List<Data> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        productID = getIntent().getStringExtra("productID");

        dataList = new ArrayList<>();

        viewPager = findViewById(R.id.view_pager);
        product_name_det = findViewById(R.id.product_name_details);
        product_price_det = findViewById(R.id.product_price_details);
        product_desc_det = findViewById(R.id.product_details_details);
        add_to_cart = findViewById(R.id.add_to_cart_det);
        dotsIndicator = findViewById(R.id.indicator);
        quick_buy = findViewById(R.id.quick_buy);

        //Setting up ViewPager
        viewPager.setClipToPadding(false);
        viewPager.setClipChildren(false);
        viewPager.setOffscreenPageLimit(3);

        viewPager.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));

        viewPager.setPageTransformer(compositePageTransformer);
        adapter = new MyAdapter(dataList);
        viewPager.setAdapter(adapter);
        dotsIndicator.attachTo(viewPager);

        getProductDetails(productID);

        add_to_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (productModel.getStatus().equals("listed")) {
                    addToCart();
                } else {
                    Toast.makeText(ProductDetailsActivity.this, "Not Available", Toast.LENGTH_SHORT).show();
                }
            }
        });

        quick_buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductDetailsActivity.this,ConfirmOrderActivity.class);
                intent.putExtra("isQuick",true);
                intent.putExtra("productID",productModel.getProductId());
                startActivity(intent);
            }
        });
    }

    private void addToCart() {

        CartItem cartItem = new CartItem();
        cartItem.setPid(productID);
        cartItem.setProductName(product_name_det.getText().toString());
        cartItem.setPrice(Integer.parseInt(productModel.getPrice()));
        cartItem.setTime(System.currentTimeMillis());
        cartItem.setDelStatus("Not Made");
        cartItem.setProductImg(productModel.getDefaultImage());
        cartItem.setUserID(FirebaseAuth.getInstance().getUid());

        Prevalent.CART_COLLECTION.document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("Cart")
                .document(productID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()){
                            Toast.makeText(ProductDetailsActivity.this, "Duplicate", Toast.LENGTH_SHORT).show();
                        } else {
                            Prevalent.CART_COLLECTION.document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .collection("Cart")
                                    .document(productID)
                                    .set(cartItem)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Prevalent.CURRENT_USER.setCartPrice(Prevalent.CURRENT_USER.getCartPrice()
                                                        + cartItem.getPrice());

                                                Prevalent.USERS_COLLECTION.document(Prevalent.CURRENT_USER.getUserID())
                                                        .set(Prevalent.CURRENT_USER)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    Toast.makeText(ProductDetailsActivity.this, "Added", Toast.LENGTH_SHORT).show();

                                                                } else {
                                                                    Toast.makeText(ProductDetailsActivity.this, task.getException().getMessage()
                                                                            , Toast.LENGTH_SHORT).show();

                                                                }
                                                            }
                                                        });


                                            } else {
                                                Toast.makeText(ProductDetailsActivity.this, task.getException().getMessage()
                                                        , Toast.LENGTH_SHORT).show();

                                            }
                                        }
                                    });
                        }
                    }
                });


    }

    private void getProductDetails(String productID) {


        Prevalent.PRODUCTS_COLLECTION.document(productID).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        productModel = documentSnapshot.toObject(ProductModel.class);
                        productModel.setProductId(documentSnapshot.getId());

                        dataList.add(new Data(productModel.getDefaultImage()));
                        dataList.add(new Data(productModel.getFrontImage()));
                        dataList.add(new Data(productModel.getBackImage()));
                        dataList.add(new Data(productModel.getExtraImage()));

                        adapter.notifyDataSetChanged();

                        product_name_det.setText(productModel.getProductName());
                        product_desc_det.setText(productModel.getDescription());
                        product_price_det.setText("K" + productModel.getPrice());

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProductDetailsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


    }

}