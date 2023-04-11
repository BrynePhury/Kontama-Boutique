package com.sweetapps.kontamaboutique;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.sweetapps.kontamaboutique.Models.User;
import com.sweetapps.kontamaboutique.Prevalent.Prevalent;

public class MainActivity extends AppCompatActivity {

    final int SPLASH_TIMEOUT = 500;

    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        mAuth = FirebaseAuth.getInstance();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mAuth.getCurrentUser() != null) {

                    Prevalent.USERS_COLLECTION.document(mAuth.getCurrentUser().getUid())
                            .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    User user = documentSnapshot.toObject(User.class);
                                    user.setUserID(documentSnapshot.getId());

                                    Prevalent.CURRENT_USER = user;

                                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    mAuth.signOut();
                                    Toast.makeText(MainActivity.this, e.getMessage()
                                            , Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(MainActivity.this, LogInActivity.class);
                                    startActivity(intent);
                                }
                            });

                } else {
                    startActivity(new Intent(MainActivity.this, LogInActivity.class));
                    finish();
                }
            }
        }, SPLASH_TIMEOUT);

    }


}