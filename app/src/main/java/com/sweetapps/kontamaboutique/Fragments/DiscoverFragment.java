package com.sweetapps.kontamaboutique.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sweetapps.kontamaboutique.Adapters.ProductsAdapter;
import com.sweetapps.kontamaboutique.Models.ProductModel;
import com.sweetapps.kontamaboutique.Prevalent.Prevalent;
import com.sweetapps.kontamaboutique.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DiscoverFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DiscoverFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DiscoverFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DiscoverFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DiscoverFragment newInstance(String param1, String param2) {
        DiscoverFragment fragment = new DiscoverFragment();
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
    public static List<ProductModel> discoProductModelsFull = new ArrayList<>();
    ProductsAdapter adapter = new ProductsAdapter(productModels,ProductsAdapter.DISCOVER);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_discover, container, false);

        RecyclerView discoverRecycler = view.findViewById(R.id.discover_recycler);
        RadioButton topsCheckBox = view.findViewById(R.id.tops);
        RadioButton bottomsCheck = view.findViewById(R.id.bottoms);
        RadioButton footwear = view.findViewById(R.id.footwear);
        RadioButton headwear = view.findViewById(R.id.headwear);
        RadioButton jewelry = view.findViewById(R.id.jewelry);
        RadioButton accessories = view.findViewById(R.id.accessories);
        RadioButton top_to_bottom = view.findViewById(R.id.top_to_bottom);
        RadioButton others = view.findViewById(R.id.others);

        RadioGroup radioGroup = view.findViewById(R.id.radioGroup);

        discoverRecycler.setHasFixedSize(true);
        discoverRecycler.setLayoutManager(new GridLayoutManager(getContext(), 2));
        discoverRecycler.setAdapter(adapter);

        loadList();

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.all:
                        adapter.getFilter().filter("");
                        break;
                    case R.id.tops:
                        adapter.getFilter().filter("Tops");
                        break;
                    case R.id.bottoms:
                        adapter.getFilter().filter("Bottoms");
                        break;
                    case R.id.footwear:
                        adapter.getFilter().filter("Footwear");
                        break;
                    case R.id.headwear:
                        adapter.getFilter().filter("Headwear");
                        break;
                    case R.id.jewelry:
                        adapter.getFilter().filter("Jewelry");
                        break;
                    case R.id.accessories:
                        adapter.getFilter().filter("Accessories");
                        break;
                    case R.id.top_to_bottom:
                        adapter.getFilter().filter("Top to Bottom");
                        break;
                    case R.id.others:
                        adapter.getFilter().filter("Others");
                        break;
                }
            }
        });

//        topsCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    adapter.getFilter().filter("Tops");
//                }
//            }
//        });
//
//        bottomsCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    for (ProductModel model : productModels) {
//                        if (model.getSub_category() != null &&
//                                model.getSub_category().equals("Bottoms")) {
//                            filteredList.add(model);
//                            adapter.setProductModels(filteredList);
//                            adapter.notifyDataSetChanged();
//                        }
//                    }
//                } else {
//                    for (ProductModel model : filteredList) {
//                        if (model.getSub_category() != null &&
//                                model.getSub_category().equals("Bottoms")) {
//                            filteredList.remove(model);
//                            adapter.setProductModels(filteredList);
//                            adapter.notifyDataSetChanged();
//                        }
//                    }
//                }
//            }
//        });
//
//        footwear.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    for (ProductModel model : productModels) {
//                        if (model.getSub_category() != null &&
//                                model.getSub_category().equals("Footwear")) {
//                            filteredList.add(model);
//                            adapter.setProductModels(filteredList);
//                            adapter.notifyDataSetChanged();
//                        }
//                    }
//                } else {
//                    for (ProductModel model : filteredList) {
//                        if (model.getSub_category() != null &&
//                                model.getSub_category().equals("Footwear")) {
//                            filteredList.remove(model);
//                            adapter.setProductModels(filteredList);
//                            adapter.notifyDataSetChanged();
//                        }
//                    }
//                }
//            }
//        });
//
//        headwear.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    for (ProductModel model : productModels) {
//                        if (model.getSub_category() != null &&
//                                model.getSub_category().equals("Headwear")) {
//                            filteredList.add(model);
//                            adapter.setProductModels(filteredList);
//                            adapter.notifyDataSetChanged();
//                        }
//                    }
//                } else {
//                    for (ProductModel model : filteredList) {
//                        if (model.getSub_category() != null &&
//                                model.getSub_category().equals("Headwear")) {
//                            filteredList.remove(model);
//                            adapter.setProductModels(filteredList);
//                            adapter.notifyDataSetChanged();
//                        }
//                    }
//                }
//            }
//        });
//
//        jewelry.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    for (ProductModel model : productModels) {
//                        if (model.getSub_category() != null &&
//                                model.getSub_category().equals("Jewelry")) {
//                            filteredList.add(model);
//                            adapter.setProductModels(filteredList);
//                            adapter.notifyDataSetChanged();
//                        }
//                    }
//                } else {
//                    for (ProductModel model : filteredList) {
//                        if (model.getSub_category() != null &&
//                                model.getSub_category().equals("Jewelry")) {
//                            filteredList.remove(model);
//                            adapter.setProductModels(filteredList);
//                            adapter.notifyDataSetChanged();
//                        }
//                    }
//                }
//            }
//        });
//
//        accessories.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    for (ProductModel model : productModels) {
//                        if (model.getSub_category() != null &&
//                                model.getSub_category().equals("Accessories")) {
//                            filteredList.add(model);
//                            adapter.setProductModels(filteredList);
//                            adapter.notifyDataSetChanged();
//                        }
//                    }
//                } else {
//                    for (ProductModel model : filteredList) {
//                        if (model.getSub_category() != null &&
//                                model.getSub_category().equals("Accessories")) {
//                            filteredList.remove(model);
//                            adapter.setProductModels(filteredList);
//                            adapter.notifyDataSetChanged();
//                        }
//                    }
//                }
//            }
//        });
//
//        top_to_bottom.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    for (ProductModel model : productModels) {
//                        if (model.getSub_category() != null &&
//                                model.getSub_category().equals("Top to Bottom")) {
//                            filteredList.add(model);
//                            adapter.setProductModels(filteredList);
//                            adapter.notifyDataSetChanged();
//                        }
//                    }
//                } else {
//                    for (ProductModel model : filteredList) {
//                        if (model.getSub_category() != null &&
//                                model.getSub_category().equals("Top to Bottom")) {
//                            filteredList.remove(model);
//                            adapter.setProductModels(filteredList);
//                            adapter.notifyDataSetChanged();
//                        }
//                    }
//                }
//            }
//        });
//
//        others.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    for (ProductModel model : productModels) {
//                        if (model.getSub_category() != null &&
//                                model.getSub_category().equals("Others")) {
//                            filteredList.add(model);
//                            adapter.setProductModels(filteredList);
//                            adapter.notifyDataSetChanged();
//                        }
//                    }
//                } else {
//                    for (ProductModel model : filteredList) {
//                        if (model.getSub_category() != null &&
//                                model.getSub_category().equals("Others")) {
//                            filteredList.remove(model);
//                            adapter.setProductModels(filteredList);
//                            adapter.notifyDataSetChanged();
//                        }
//                    }
//                }
//            }
//        });

        return view;
    }


    private void loadList() {
        Prevalent.PRODUCTS_COLLECTION
                .orderBy("price")
                .orderBy("timeStamp")
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        productModels.clear();

                        for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                            ProductModel productModel = snapshot.toObject(ProductModel.class);
                            productModel.setProductId(snapshot.getId());

                            if (productModel.getStatus().equals("listed")) {
                                productModels.add(productModel);
                                discoProductModelsFull.add(productModel);
                                adapter.notifyDataSetChanged();
                            }
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