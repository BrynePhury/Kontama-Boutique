package com.sweetapps.kontamaboutique;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.sweetapps.kontamaboutique.Fragments.CartFragment;
import com.sweetapps.kontamaboutique.Fragments.DiscoverFragment;
import com.sweetapps.kontamaboutique.Fragments.HomeFragment;
import com.sweetapps.kontamaboutique.Models.ProductModel;
import com.sweetapps.kontamaboutique.Prevalent.Prevalent;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity {

    //Global View Declarations
    BottomNavigationView chipNavigationBar;
    CircleImageView toolBar_img;

    HomeFragment homeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        chipNavigationBar = findViewById(R.id.chip_navigation);
        toolBar_img = findViewById(R.id.toolbar_img);

        Picasso.get().load(Prevalent.CURRENT_USER.getProfileImg())
                .placeholder(R.drawable.ic_blank_profile)
                .into(toolBar_img, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(Prevalent.CURRENT_USER.getProfileImg())
                                .placeholder(R.drawable.ic_blank_profile)
                                .into(toolBar_img);
                    }
                });

        homeFragment = new HomeFragment();
        DiscoverFragment discoverFragment = new DiscoverFragment();
        CartFragment cartFragment = new CartFragment();

        setFragment(homeFragment);

//        load();

        chipNavigationBar.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.bottom_nav_home:
                        //load summary fragment
                        setFragment(homeFragment);
                        break;

                    case R.id.bottom_nav_discover:
                        //load notes fragment
                        setFragment(discoverFragment);
                        break;

                    case R.id.bottom_nav_cart:
                        //load to do list fragment
                        setFragment(cartFragment);
                        break;
                    case R.id.bottom_nav_profile:
                        //Open profile activity
                        startActivity(new Intent(HomeActivity.this,
                                ProfileActivity.class));
                        finish();
                }

                return true;
            }
        });

        toolBar_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this,ProfileActivity.class));
            }
        });

    }

//    private void load(){
//        Prevalent.PRODUCTS_COLLECTION
//                .orderBy("price")
//                .orderBy("timeStamp")
//                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                    @Override
//                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                        Prevalent.productModelsFull.clear();
//
//                        for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
//                            ProductModel productModel = snapshot.toObject(ProductModel.class);
//                            productModel.setProductId(snapshot.getId());
//
//                            if (productModel.getStatus().equals("listed")) {
//                                Prevalent.productModelsFull.add(productModel);
//                            }
//                        }
//                    }
//                });
//    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
}