package com.sweetapps.kontamaboutique.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sweetapps.kontamaboutique.Adapters.CartAdapter;
import com.sweetapps.kontamaboutique.ConfirmOrderActivity;
import com.sweetapps.kontamaboutique.Models.CartItem;
import com.sweetapps.kontamaboutique.Prevalent.Prevalent;
import com.sweetapps.kontamaboutique.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CartFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CartFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CartFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CartFragment newInstance(String param1, String param2) {
        CartFragment fragment = new CartFragment();
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

    CartAdapter adapter;
    List<CartItem> cartItems = new ArrayList<>();

    public static TextView totalPrice;
    TextView nothingTxt;
    ImageView nothingImg;
    LinearLayout checkout_lay;
    RecyclerView cart_recycler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        cart_recycler = view.findViewById(R.id.cart_recycler);
        totalPrice = view.findViewById(R.id.totalPrice);
        nothingImg = view.findViewById(R.id.nothing_img);
        nothingTxt = view.findViewById(R.id.nothing_txt);
        checkout_lay = view.findViewById(R.id.checkout_lay);

        cart_recycler.setHasFixedSize(true);
        cart_recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new CartAdapter(cartItems);

        cart_recycler.setAdapter(adapter);

        totalPrice.setText("K" + String.valueOf(Prevalent.CURRENT_USER.getCartPrice()));

        checkout_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ConfirmOrderActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        Prevalent.CART_COLLECTION.document(FirebaseAuth.getInstance().getUid())
                .collection("Cart").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        cartItems.clear();
                        adapter.notifyDataSetChanged();

                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                                CartItem cartItem = snapshot.toObject(CartItem.class);
                                cartItem.setItemId(snapshot.getId());

                                cartItems.add(cartItem);
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }
                });

        if (Prevalent.CURRENT_USER.getCartPrice() <= 0) {
            nothingImg.setVisibility(View.VISIBLE);
            nothingTxt.setVisibility(View.VISIBLE);
            checkout_lay.setVisibility(View.GONE);
            cart_recycler.setVisibility(View.GONE);

        } else {
            nothingImg.setVisibility(View.GONE);
            nothingTxt.setVisibility(View.GONE);
            checkout_lay.setVisibility(View.VISIBLE);
            cart_recycler.setVisibility(View.VISIBLE);
        }
    }
}