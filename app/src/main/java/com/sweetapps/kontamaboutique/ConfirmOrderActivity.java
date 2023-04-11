package com.sweetapps.kontamaboutique;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.sweetapps.kontamaboutique.Models.CartItem;
import com.sweetapps.kontamaboutique.Models.LocationModel;
import com.sweetapps.kontamaboutique.Models.OrderModel;
import com.sweetapps.kontamaboutique.Models.ProductModel;
import com.sweetapps.kontamaboutique.Prevalent.Prevalent;

import java.util.ArrayList;
import java.util.List;

public class ConfirmOrderActivity extends AppCompatActivity {

    EditText deliveryName;
    EditText deliveryPhone;
    TextView totalPriceTV;
    MaterialButton confirm_order;
    CheckBox useCurrentLocation;

    final int DE_LISTED = 0;
    final int LISTED = 1;

    int totalAmount = 0;

    ProgressDialog progressDialog;

    LocationRequest locationRequest;

    int CONFIRM_CHECKER = 0;

    LocationModel locationModel;

    List<String> productIDs;

    int deliveryFee = 20;

    int listed = LISTED;
    String img;

    boolean isQuick;
    String productID;
    ProductModel quickProductModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comfirm_order);

        totalAmount = Prevalent.CURRENT_USER.getCartPrice();

        isQuick = getIntent().getBooleanExtra("isQuick", false);
        productID = getIntent().getStringExtra("productID");

        deliveryName = findViewById(R.id.delivery_name);
        deliveryPhone = findViewById(R.id.delivery_phone);
        confirm_order = findViewById(R.id.confirm_order_btn);
        totalPriceTV = findViewById(R.id.checkout_cost);
        useCurrentLocation = findViewById(R.id.use_current_location);

        progressDialog = new ProgressDialog(ConfirmOrderActivity.this);
        progressDialog.setMessage("Preparing...");
        progressDialog.setCancelable(false);
        progressDialog.show();



        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);

        getDetails();

        if (!isQuick) {
            productIDs = getCartItems();
        } else{
            checkListing();
        }
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                checkListings(productIDs);
//
//            }
//        },1000);


        confirm_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (check()) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(ConfirmOrderActivity.this);

                    View view = LayoutInflater.from(ConfirmOrderActivity.this)
                            .inflate(R.layout.clarity_dialog, null, false);

                    builder.setView(view);

                    AlertDialog dialog = builder.show();

                    TextView clarity_txt = view.findViewById(R.id.clarity_txt);
                    EditText clarity_password = view.findViewById(R.id.clarity_password);
                    Button clarity_btn = view.findViewById(R.id.clarity_btn);

                    int totalSum = totalAmount + deliveryFee;

                    String clarity = "A sum of K" + totalSum + " is expected on delivery. Your order costs K" + totalAmount + " plus a delivery fee of K" + deliveryFee + ".";

                    clarity_txt.setText(clarity);


                    clarity_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String password = clarity_password.getText().toString();

                            if (TextUtils.isEmpty(password)) {
                                clarity_password.setError("Required");
                                clarity_password.requestFocus();

                            } else if (!password.matches(Prevalent.CURRENT_USER.getPassword())) {
                                clarity_password.setError("Incorrect");
                                clarity_password.requestFocus();

                            } else {
                                dialog.dismiss();
                                if (useCurrentLocation.isChecked()) {
                                    getCurrentLocation();

                                } else {
                                    if (Prevalent.CURRENT_USER.getDefaultLocation() == null
                                            || TextUtils.isEmpty(Prevalent.CURRENT_USER.getDefaultLocation())) {

                                        Toast.makeText(ConfirmOrderActivity.this, "Default location not set", Toast.LENGTH_SHORT).show();

                                    } else {

                                        if (isQuick) {
                                            quickBuy();
                                        } else {
                                            confirmOrder();
                                        }
                                    }
                                }
                            }
                        }
                    });


                }
            }
        });

    }


    private void getDetails() {

        if (!isQuick) {

            totalPriceTV.setText("Cost: K" + totalAmount);
        } else {
            Prevalent.PRODUCTS_COLLECTION.document(productID).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            quickProductModel = documentSnapshot.toObject(ProductModel.class);
                            quickProductModel.setProductId(documentSnapshot.getId());

                            totalPriceTV.setText("Cost: K" + quickProductModel.getPrice());

                            totalAmount = Integer.parseInt(quickProductModel.getPrice());

                            img = quickProductModel.getDefaultImage();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ConfirmOrderActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        }

        String name;
        if (Prevalent.CURRENT_USER.getOtherName() != null &&
                !TextUtils.isEmpty(Prevalent.CURRENT_USER.getOtherName())) {

            name = Prevalent.CURRENT_USER.getFirstName()
                    + " " + Prevalent.CURRENT_USER.getOtherName() + " " + Prevalent.CURRENT_USER.getLastName();
        } else {
            name = Prevalent.CURRENT_USER.getFirstName()
                    + " " + Prevalent.CURRENT_USER.getLastName();
        }
        deliveryName.setText(name);

        deliveryPhone.setText(Prevalent.CURRENT_USER.getPhone());


    }

    private boolean check() {

        if (TextUtils.isEmpty(deliveryName.getText().toString())) {
            deliveryName.setError("Required");
            return false;
        } else if (TextUtils.isEmpty(deliveryPhone.getText().toString())) {
            deliveryPhone.setError("Required");
            return false;
        } else if (listed == DE_LISTED) {
            Toast.makeText(this, "Some Products are no longer available", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }


    private void getCurrentLocation() {

        Dexter.withContext(this).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {

                if (isGPSEnabled()) {

                    nowGetLocation();

                } else {
                    turnOnGPS();
                }

            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();
    }

    private void nowGetLocation() {
        if (ActivityCompat.checkSelfPermission(ConfirmOrderActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(ConfirmOrderActivity.this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }


        progressDialog.show();

        LocationServices.getFusedLocationProviderClient(ConfirmOrderActivity.this)
                .requestLocationUpdates(locationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(@NonNull LocationResult locationResult) {
                        super.onLocationResult(locationResult);

                        LocationServices.getFusedLocationProviderClient(ConfirmOrderActivity.this)
                                .removeLocationUpdates(this);

                        if (locationResult != null && locationResult.getLocations().size() > 0) {

                            int index = locationResult.getLocations().size() - 1;


                            locationModel = new LocationModel();
                            locationModel.setLatitude(locationResult.getLocations().get(index).getLatitude());
                            locationModel.setLongitude(locationResult.getLocations().get(index).getLongitude());

                            progressDialog.dismiss();
                            if (CONFIRM_CHECKER == 0) {
                                if (isQuick) {
                                    quickBuy();
                                } else {
                                    confirmOrder();
                                }
                            }

                        } else {
                            progressDialog.dismiss();

                        }


                    }
                }, Looper.getMainLooper());

    }

    private void turnOnGPS() {

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(getApplicationContext())
                .checkLocationSettings(builder.build());
        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {

                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    nowGetLocation();

                } catch (ApiException e) {

                    switch (e.getStatusCode()) {
                        case LocationSettingsStatusCodes
                                .RESOLUTION_REQUIRED:
                            try {
                                ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                                resolvableApiException.startResolutionForResult(ConfirmOrderActivity.this, 2);
                            } catch (IntentSender.SendIntentException sendIntentException) {
                                sendIntentException.printStackTrace();
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            Toast.makeText(ConfirmOrderActivity.this, "Your device does not have location", Toast.LENGTH_SHORT).show();
                            break;
                    }

                }

            }
        });
    }

    private boolean isGPSEnabled() {
        LocationManager locationManager = null;
        boolean isEnabled = false;

        if (locationManager == null) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        }
        isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return isEnabled;
    }

    private void quickBuy() {

        CONFIRM_CHECKER = 1;

        String locationJson;

        progressDialog.show();
        locationJson = Prevalent.CURRENT_USER.getDefaultLocation();
        if (useCurrentLocation.isChecked()) {

            Gson gson = new Gson();

            locationJson = gson.toJson(locationModel);
        }
        List<String> list = new ArrayList<>();
        list.add(quickProductModel.getProductId());

        OrderModel order = new OrderModel();

        order.setTotalAmount(totalAmount);
        order.setCustomerName(deliveryName.getText().toString());
        order.setPhone(deliveryPhone.getText().toString());
        order.setUid(Prevalent.CURRENT_USER.getUserID());
        order.setLocation(locationJson);
        order.setTimeStamp(System.currentTimeMillis());
        order.setState("Not delivered");
        order.setProducts(list);
        order.setItem_count(1);
        order.setImg(img);
        order.setType(OrderModel.TYPE_SINGLE);

        Prevalent.PRODUCTS_COLLECTION.document(productID)
                .update("status", "Sold");

        Prevalent.ORDER_COLLECTION.document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("Orders").add(order)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {


                            Prevalent.USERS_COLLECTION.document(Prevalent.CURRENT_USER.getUserID())
                                    .set(Prevalent.CURRENT_USER)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {

                                                progressDialog.dismiss();
                                                Intent intent = new Intent(ConfirmOrderActivity.this,
                                                        HomeActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                                        Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                                Toast.makeText(ConfirmOrderActivity.this, "Order Placed", Toast.LENGTH_SHORT).show();
                                            } else {

                                                Toast.makeText(ConfirmOrderActivity.this, task.getException().getMessage()
                                                        , Toast.LENGTH_SHORT).show();
                                                progressDialog.dismiss();
                                            }

                                        }
                                    });


                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(ConfirmOrderActivity.this, task.getException().getMessage()
                                    , Toast.LENGTH_SHORT).show();
                        }

                    }
                });

    }

    private void confirmOrder() {

        CONFIRM_CHECKER = 1;

        String locationJson;

        progressDialog.show();
        locationJson = Prevalent.CURRENT_USER.getDefaultLocation();
        if (useCurrentLocation.isChecked()) {

            Gson gson = new Gson();

            locationJson = gson.toJson(locationModel);
        }

        OrderModel order = new OrderModel();

        order.setTotalAmount(totalAmount);
        order.setCustomerName(deliveryName.getText().toString());
        order.setPhone(deliveryPhone.getText().toString());
        order.setUid(Prevalent.CURRENT_USER.getUserID());
        order.setLocation(locationJson);
        order.setTimeStamp(System.currentTimeMillis());
        order.setState("Not delivered");
        order.setProducts(productIDs);
        order.setItem_count(productIDs.size());
        order.setImg(img);
        order.setType(OrderModel.TYPE_MULTI);

        for (String productID : productIDs) {
            Prevalent.PRODUCTS_COLLECTION.document(productID)
                    .update("status", "Sold");
        }

        Prevalent.ORDER_COLLECTION.document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("Orders").add(order)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            for (String itemID : productIDs) {
                                Prevalent.CART_COLLECTION.document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .collection("Cart")
                                        .document(itemID).delete();
                            }
                            Prevalent.CART_COLLECTION.document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Prevalent.CURRENT_USER.setCartPrice(0);

                                                Prevalent.USERS_COLLECTION.document(Prevalent.CURRENT_USER.getUserID())
                                                        .set(Prevalent.CURRENT_USER)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {

                                                                    progressDialog.dismiss();
                                                                    Intent intent = new Intent(ConfirmOrderActivity.this,
                                                                            HomeActivity.class);
                                                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                                                            Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                    startActivity(intent);
                                                                    Toast.makeText(ConfirmOrderActivity.this, "Order Placed", Toast.LENGTH_SHORT).show();
                                                                } else {

                                                                    Toast.makeText(ConfirmOrderActivity.this, task.getException().getMessage()
                                                                            , Toast.LENGTH_SHORT).show();
                                                                    progressDialog.dismiss();
                                                                }

                                                            }
                                                        });

                                            } else {
                                                Toast.makeText(ConfirmOrderActivity.this, task.getException().getMessage()
                                                        , Toast.LENGTH_SHORT).show();
                                                progressDialog.dismiss();
                                            }
                                        }
                                    });
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(ConfirmOrderActivity.this, task.getException().getMessage()
                                    , Toast.LENGTH_SHORT).show();
                        }

                    }
                });


    }

    private List<String> getCartItems() {
        ArrayList<String> returnList = new ArrayList<>();

        Prevalent.CART_COLLECTION.document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("Cart").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                                CartItem cartItem = snapshot.toObject(CartItem.class);
                                cartItem.setItemId(snapshot.getId());

                                if (img == null) {
                                    img = cartItem.getProductImg();
                                }

                                if (!returnList.contains(cartItem.getPid())) {
                                    returnList.add(cartItem.getPid());
                                }

                            }
                            checkListings(productIDs);
                        }
                    }
                });

        return returnList;
    }

    private void checkListings(List<String> listings) {

        for (int i = 0; i < listings.size(); i++) {
            String productID = listings.get(i);
            final boolean[] bool = {false};
            int finalI = i;
            Prevalent.PRODUCTS_COLLECTION.document(productID).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            ProductModel model = documentSnapshot.toObject(ProductModel.class);

                            progressDialog.dismiss();
                            progressDialog.setMessage("Loading...");

                            if (!model.getStatus().equals("listed")) {
                                listed = DE_LISTED;
                                Toast.makeText(ConfirmOrderActivity.this, model.getProductName() + " is no longer available",
                                        Toast.LENGTH_SHORT).show();

                                bool[0] = true;
                            }

                        }
                    });
            if (listed == DE_LISTED) {
                break;
            }

        }
    }

    private void checkListing() {

            Prevalent.PRODUCTS_COLLECTION.document(productID).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            ProductModel model = documentSnapshot.toObject(ProductModel.class);

                            progressDialog.dismiss();
                            progressDialog.setMessage("Loading...");

                            if (!model.getStatus().equals("listed")) {
                                listed = DE_LISTED;
                                Toast.makeText(ConfirmOrderActivity.this, model.getProductName() + " is no longer available",
                                        Toast.LENGTH_SHORT).show();

                            }

                        }
                    });


    }


}