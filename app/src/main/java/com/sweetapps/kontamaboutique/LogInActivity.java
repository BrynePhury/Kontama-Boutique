package com.sweetapps.kontamaboutique;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.sweetapps.kontamaboutique.Models.User;
import com.sweetapps.kontamaboutique.Prevalent.Prevalent;

public class LogInActivity extends AppCompatActivity {

    EditText emailInput;
    EditText passwordInput;
    MaterialButton logInBtn;
    TextView signUp_txt;

    FirebaseAuth mAuth;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
        logInBtn = findViewById(R.id.log_in_btn);
        signUp_txt = findViewById(R.id.signUp_txt);

        mAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);

        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

        logInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = emailInput.getText().toString().trim();
                String password = passwordInput.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    emailInput.setError("Required");
                    emailInput.requestFocus();

                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    emailInput.setError("Invalid");
                    emailInput.requestFocus();

                } else if (TextUtils.isEmpty(password)) {
                    passwordInput.setError("Required");
                    passwordInput.requestFocus();

                } else {
                    logIn(email, password);

                }

            }
        });

        signUp_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LogInActivity.this
                , SignUpActivity.class));
            }
        });

    }

    private void logIn(String email, String password) {
        progressDialog.show();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Prevalent.USERS_COLLECTION.document(mAuth.getCurrentUser().getUid())
                                    .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            User user = documentSnapshot.toObject(User.class);
                                            user.setUserID(documentSnapshot.getId());

                                            Prevalent.CURRENT_USER = user;

                                            startActivity(new Intent(LogInActivity.this, HomeActivity.class));
                                            finish();
                                            progressDialog.dismiss();

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            mAuth.signOut();
                                            Toast.makeText(LogInActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();

                                        }
                                    });

                        } else {
                            Toast.makeText(LogInActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();

                        }
                    }
                });
    }
}