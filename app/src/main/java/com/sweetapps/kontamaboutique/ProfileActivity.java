package com.sweetapps.kontamaboutique;

import static com.sweetapps.kontamaboutique.Prevalent.Prevalent.UPLOAD_IMAGE_QUALITY;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.sweetapps.kontamaboutique.Models.LocationModel;
import com.sweetapps.kontamaboutique.Prevalent.Prevalent;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class ProfileActivity extends AppCompatActivity {

    ImageView profile_img;
    TextView userName;
    TextView location_name;
    ImageView back_icon;
    TextView back_txt;
    LinearLayout edit_profile_ll;
    LinearLayout settings_ll;
    LinearLayout orders_ll;
    LinearLayout help_ll;
    LinearLayout sign_out_ll;
    LinearLayout location_layout;

    final int PICK_PROFILE_IMG = 767;

    Uri imageUri;

    ProgressDialog progressDialog;
    private LocationRequest locationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profile_img = findViewById(R.id.profile_img);
        userName = findViewById(R.id.userName);
        location_name = findViewById(R.id.location_name);
        back_icon = findViewById(R.id.back_icon);
        back_txt = findViewById(R.id.back_txt);
        edit_profile_ll = findViewById(R.id.edit_profile_ll);
        settings_ll = findViewById(R.id.settings_ll);
        orders_ll = findViewById(R.id.orders_ll);
        help_ll = findViewById(R.id.help_ll);
        sign_out_ll = findViewById(R.id.sign_out_ll);
        location_layout = findViewById(R.id.location_layout);

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);


        displayUserInfo();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

        profile_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                CharSequence[] options = new CharSequence[]{"View Image", "Change Image"};
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                if (Prevalent.CURRENT_USER.getProfileImg() != null
                                        && !TextUtils.isEmpty(Prevalent.CURRENT_USER.getProfileImg())) {

                                    startActivity(new Intent(ProfileActivity.this,
                                            PhotoViewActivity.class)
                                            .putExtra("url",
                                                    Prevalent.CURRENT_USER.getProfileImg()));
                                }

                                break;
                            case 1:
                                openGallery(PICK_PROFILE_IMG);
                                break;
                        }

                    }
                });
                builder.show();
            }
        });

        location_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                builder.setMessage("Do you want to change default location to current location?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Dexter.withContext(ProfileActivity.this).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener(new PermissionListener() {
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
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();
            }
        });

        back_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        back_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        edit_profile_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, EditProfileActivity.class));
            }
        });

        orders_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, OrdersActivity.class));
            }
        });

        settings_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, SettingsActivity.class));
            }
        });

        help_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, HelpActivity.class));
            }
        });

        sign_out_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this,LogInActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

                FirebaseAuth.getInstance().signOut();
            }
        });
    }

    private void displayUserInfo() {

        Picasso.get().load(Prevalent.CURRENT_USER.getProfileImg())
                .placeholder(R.drawable.ic_blank_profile).into(profile_img, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(Prevalent.CURRENT_USER.getProfileImg())
                                .placeholder(R.drawable.ic_blank_profile).into(profile_img);
                    }
                });

        String name;
        if (Prevalent.CURRENT_USER.getOtherName() != null &&
                !TextUtils.isEmpty(Prevalent.CURRENT_USER.getOtherName())) {

            name = Prevalent.CURRENT_USER.getFirstName()
                    + " " + Prevalent.CURRENT_USER.getOtherName() + " " + Prevalent.CURRENT_USER.getLastName();
        } else {
            name = Prevalent.CURRENT_USER.getFirstName()
                    + " " + Prevalent.CURRENT_USER.getLastName();
        }

        userName.setText(name);

        if (Prevalent.CURRENT_USER.getLocationName() != null
        && !TextUtils.isEmpty(Prevalent.CURRENT_USER.getLocationName())) {

            location_name.setText(Prevalent.CURRENT_USER.getLocationName());
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }

    private void openGallery(int requestCode) {

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_PROFILE_IMG && resultCode == RESULT_OK && data != null) {

            imageUri = data.getData();

            uploadImage();

        } else {

            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();

        }
    }

    private void uploadImage() {

        progressDialog.show();

        if (imageUri != null) {

            final StorageReference filePath = Prevalent.USER_IMAGES_STORAGE
                    .child(Prevalent.CURRENT_USER.getUserID() + String.valueOf(System.currentTimeMillis()) + ".jpg");
            byte[] image = null;

            try {
                Bitmap original = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                original.compress(Bitmap.CompressFormat.JPEG, UPLOAD_IMAGE_QUALITY, stream);
                image = stream.toByteArray();
                System.out.println(image);

            } catch (IOException e) {
                e.printStackTrace();
            }
            if (image != null) {

                UploadTask uploadTask = filePath.putBytes(image);

                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String message = e.getMessage();

                        Toast.makeText(ProfileActivity.this, message, Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();

                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                                if (!task.isSuccessful()) {

                                    throw task.getException();

                                } else {

                                    return filePath.getDownloadUrl();
                                }

                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {

                                String img = task.getResult().toString();

                                Prevalent.CURRENT_USER.setProfileImg(img);

                                Prevalent.USERS_COLLECTION.document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .set(Prevalent.CURRENT_USER)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Picasso.get().load(img).into(profile_img, new Callback() {
                                                        @Override
                                                        public void onSuccess() {

                                                        }

                                                        @Override
                                                        public void onError(Exception e) {
                                                            Picasso.get().load(img).into(profile_img);
                                                        }
                                                    });

                                                    progressDialog.dismiss();

                                                    Toast.makeText(ProfileActivity.this, "Done!", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(ProfileActivity.this, task.getException().getMessage()
                                                            , Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });


                            }
                        });

                    }
                });

            } else {
                Toast.makeText(this, "Something went wrong, try again", Toast.LENGTH_SHORT).show();
            }
        }

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
                                resolvableApiException.startResolutionForResult(ProfileActivity.this, 2);
                            } catch (IntentSender.SendIntentException sendIntentException) {
                                sendIntentException.printStackTrace();
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            Toast.makeText(ProfileActivity.this, "Your device does not have location", Toast.LENGTH_SHORT).show();
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

    private void nowGetLocation() {
        if (ActivityCompat.checkSelfPermission(ProfileActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(ProfileActivity.this,
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

        LocationServices.getFusedLocationProviderClient(ProfileActivity.this)
                .requestLocationUpdates(locationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(@NonNull LocationResult locationResult) {
                        super.onLocationResult(locationResult);

                        LocationServices.getFusedLocationProviderClient(ProfileActivity.this)
                                .removeLocationUpdates(this);

                        if (locationResult != null && locationResult.getLocations().size() > 0) {

                            int index = locationResult.getLocations().size() - 1;

                            LocationModel locationModel = new LocationModel();
                            locationModel.setLatitude(locationResult.getLocations().get(index).getLatitude());
                            locationModel.setLongitude(locationResult.getLocations().get(index).getLongitude());

                            Gson gson = new Gson();
                            String locale = gson.toJson(locationModel);

                            Prevalent.CURRENT_USER.setDefaultLocation(locale);

                            Geocoder geocoder = new Geocoder(ProfileActivity.this, Locale.getDefault());

                            try{
                                List<Address> addresses = geocoder.getFromLocation(locationModel.getLatitude()
                                        ,locationModel.getLongitude(),1);
                                String locality = addresses.get(0).getLocality() + ", "
                                        + addresses.get(0).getCountryName();
                                location_name.setText(locality);
                                Prevalent.CURRENT_USER.setLocationName(locality);
                            } catch (Exception e ){
                                e.printStackTrace();
                            }

                            Prevalent.USERS_COLLECTION.document(Prevalent.CURRENT_USER.getUserID())
                                    .set(Prevalent.CURRENT_USER)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(ProfileActivity.this, "Location Acquired!", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(ProfileActivity.this, task.getException().getMessage()
                                                        , Toast.LENGTH_SHORT).show();
                                            }
                                            progressDialog.dismiss();
                                        }
                                    });




                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(ProfileActivity.this, "Failed!", Toast.LENGTH_SHORT).show();

                        }

                    }
                }, Looper.getMainLooper());
    }


}