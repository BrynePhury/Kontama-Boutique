package com.sweetapps.kontamaboutique;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.sweetapps.kontamaboutique.Models.LocationModel;
import com.sweetapps.kontamaboutique.Prevalent.Prevalent;

public class EditProfileActivity extends AppCompatActivity {

    EditText first_name_input;
    EditText last_name_input;
    EditText other_name_input;
    EditText phone_input;
    EditText email_input;
    MaterialButton save_btn;
    ImageView back_icon;
    TextView back_txt;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        first_name_input = findViewById(R.id.first_name_input);
        last_name_input = findViewById(R.id.last_name_input);
        other_name_input = findViewById(R.id.other_name_input);
        phone_input = findViewById(R.id.phone_input);
        email_input = findViewById(R.id.email_input);
        save_btn = findViewById(R.id.save_btn);
        back_icon = findViewById(R.id.back_icon);
        back_txt = findViewById(R.id.back_txt);

        progressDialog = new ProgressDialog(EditProfileActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

        getUserInfo();


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

        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserDetails();

            }
        });
    }

    private void getUserInfo() {

        first_name_input.setText(Prevalent.CURRENT_USER.getFirstName());
        last_name_input.setText(Prevalent.CURRENT_USER.getLastName());

        if (Prevalent.CURRENT_USER.getOtherName() != null) {
            other_name_input.setText(Prevalent.CURRENT_USER.getOtherName());
        }
        if (Prevalent.CURRENT_USER.getPhone() != null) {
            phone_input.setText(Prevalent.CURRENT_USER.getPhone());
        }
        email_input.setText(Prevalent.CURRENT_USER.getEmail());

        if (Prevalent.CURRENT_USER.isAuthPhone()) {
            phone_input.setEnabled(false);

        } else {
            email_input.setEnabled(false);
        }



    }


    private void saveUserDetails() {
        progressDialog.show();

        String firstName = first_name_input.getText().toString().trim();
        String lastName = last_name_input.getText().toString().trim();
        String otherName = other_name_input.getText().toString().trim();

        if (!Prevalent.CURRENT_USER.isAuthPhone()) {
            String phone = phone_input.getText().toString().trim();
            Prevalent.CURRENT_USER.setPhone(phone);

        } else {
            String email = email_input.getText().toString().trim();
            Prevalent.CURRENT_USER.setEmail(email);
        }

        Prevalent.CURRENT_USER.setFirstName(firstName);
        Prevalent.CURRENT_USER.setLastName(lastName);
        Prevalent.CURRENT_USER.setOtherName(otherName);

        Prevalent.USERS_COLLECTION.document(Prevalent.CURRENT_USER.getUserID())
                .set(Prevalent.CURRENT_USER)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(EditProfileActivity.this, "Saved!", Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        } else {
                            Toast.makeText(EditProfileActivity.this, task.getException().getMessage()
                                    , Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                    }
                });
    }

}