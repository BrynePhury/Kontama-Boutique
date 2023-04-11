package com.sweetapps.kontamaboutique;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sweetapps.kontamaboutique.Adapters.ProductsAdapter;
import com.sweetapps.kontamaboutique.Models.ProductModel;
import com.sweetapps.kontamaboutique.Prevalent.Prevalent;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    EditText search_edit;
    RecyclerView search_recycler;

    ProductsAdapter adapter;
    List<ProductModel> productModels;
    public static List<ProductModel> productModelsFull = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        search_edit = findViewById(R.id.search_edit_text);
        search_recycler = findViewById(R.id.search_recycler);

        search_recycler.setHasFixedSize(true);
        search_recycler.setLayoutManager(new GridLayoutManager(this, 2));

        productModels = new ArrayList<>();

        adapter = new ProductsAdapter(productModels,ProductsAdapter.SEARCH);
        search_recycler.setAdapter(adapter);

        loadAllProducts();

        search_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                if (TextUtils.isEmpty(text.trim())){
                    loadAllProducts();
                }
            }
        });

    }

    private void loadAllProducts(){
        productModels.clear();
        Prevalent.PRODUCTS_COLLECTION
                .orderBy("price")
                .orderBy("timeStamp")
                .limit(500)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            ProductModel productModel = documentSnapshot.toObject(ProductModel.class);
                            productModel.setProductId(documentSnapshot.getId());

                            if (productModel.getStatus().equals("listed")) {
                                productModels.add(productModel);
                                productModelsFull.add(productModel);
                            }


                        }


                    }
                });
    }
}