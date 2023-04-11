package com.sweetapps.kontamaboutique.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sweetapps.kontamaboutique.Adapters.ProductsAdapter;
import com.sweetapps.kontamaboutique.Adapters.PromotionsAdapter;
import com.sweetapps.kontamaboutique.Models.ProductModel;
import com.sweetapps.kontamaboutique.Models.PromotionModel;
import com.sweetapps.kontamaboutique.Prevalent.Prevalent;
import com.sweetapps.kontamaboutique.R;
import com.sweetapps.kontamaboutique.SearchActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    List<ProductModel> productModels = new ArrayList<>();
    List<PromotionModel> promotionModels = new ArrayList<>();

    ProductsAdapter productsAdapter;
    PromotionsAdapter promotionsAdapter;

    RecyclerView promotions_recycler;
    CardView menu_fab;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Create the view
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //Find the Views
        promotions_recycler = view.findViewById(R.id.promo_recycler);
        RecyclerView new_arrivals_recycler = view.findViewById(R.id.new_arrivals_recycler);
        menu_fab = view.findViewById(R.id.nav_menu);
        CardView search_box = view.findViewById(R.id.search_box);
        TextView search_edit = view.findViewById(R.id.search_edit);

        //Setting up recyclers
        promotions_recycler.setHasFixedSize(true);
        promotions_recycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));

        new_arrivals_recycler.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        new_arrivals_recycler.setLayoutManager(layoutManager);

        productsAdapter = new ProductsAdapter(productModels,ProductsAdapter.NONE);
        new_arrivals_recycler.setAdapter(productsAdapter);

        promotionsAdapter = new PromotionsAdapter(promotionModels);
        promotions_recycler.setAdapter(promotionsAdapter);

        search_box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), SearchActivity.class));
            }
        });

        search_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), SearchActivity.class));
            }
        });

        //  return the view for this fragment
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Prevalent.PRODUCTS_COLLECTION
                .orderBy("price")
                .orderBy("timeStamp")
                .limit(100)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        productModels.clear();

                        for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                            ProductModel productModel = snapshot.toObject(ProductModel.class);
                            productModel.setProductId(snapshot.getId());

                            if (productModel.getStatus().equals("listed")) {
                                productModels.add(productModel);
                                productsAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        Prevalent.PROMOTIONS_COLLECTION.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                promotionModels.clear();
                if (!queryDocumentSnapshots.isEmpty()) {
                    promotions_recycler.setVisibility(View.VISIBLE);
                    for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                        PromotionModel promotionModel = snapshot.toObject(PromotionModel.class);
                        promotionModel.setPromoId(snapshot.getId());

                        promotionModels.add(promotionModel);
                        promotionsAdapter.notifyDataSetChanged();
                    }
                } else {
                    promotions_recycler.setVisibility(View.GONE);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}