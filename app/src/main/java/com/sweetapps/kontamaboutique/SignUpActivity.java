package com.sweetapps.kontamaboutique;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.sweetapps.kontamaboutique.Models.User;
import com.sweetapps.kontamaboutique.Prevalent.Prevalent;

public class SignUpActivity extends AppCompatActivity {

    EditText firstName_input;
    EditText lastName_input;
    EditText otherName_input;
    EditText email_input;
    EditText password_input;
    EditText re_password_input;
    RadioGroup sex_radio_group;
    MaterialButton sign_up_btn;
    TextView signIn_txt;

    FirebaseAuth mAuth;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        firstName_input = findViewById(R.id.first_name_input);
        lastName_input = findViewById(R.id.last_name_input);
        otherName_input = findViewById(R.id.other_name_input);
        email_input = findViewById(R.id.email_input);
        password_input = findViewById(R.id.password_input);
        re_password_input = findViewById(R.id.re_password_input);
        sex_radio_group = findViewById(R.id.sex_radio_group);
        sign_up_btn = findViewById(R.id.sign_up_btn);
        signIn_txt = findViewById(R.id.signIn_txt);

        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

        signIn_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this, LogInActivity.class));
                finish();

            }
        });

        sign_up_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstName = firstName_input.getText().toString().trim();
                String lastName = lastName_input.getText().toString().trim();
                String otherName = otherName_input.getText().toString().trim();
                String email = email_input.getText().toString().trim();
                String password = password_input.getText().toString().trim();
                String re_password = re_password_input.getText().toString().trim();

                if (TextUtils.isEmpty(firstName)) {
                    firstName_input.setError("Required");
                    firstName_input.requestFocus();

                } else if (TextUtils.isEmpty(lastName)) {
                    lastName_input.setError("Required");
                    lastName_input.requestFocus();

                } else if (TextUtils.isEmpty(email)) {
                    email_input.setError("Required");
                    email_input.requestFocus();

                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    email_input.setError("Invalid");
                    email_input.requestFocus();

                } else if (TextUtils.isEmpty(password)) {
                    password_input.setError("Required");
                    password_input.requestFocus();

                } else if (TextUtils.isEmpty(re_password)) {
                    re_password_input.setError("Required");
                    re_password_input.requestFocus();

                } else if (!password.equals(re_password)) {
                    password_input.setError("Passwords do not match");
                    re_password_input.setError("Passwords do not match");
                    password_input.requestFocus();

                } else {

                    createAccount(firstName, lastName, otherName, email, password);

                }
            }
        });
    }

    private void createAccount(String firstName, String lastName, String otherName, String email, String password) {
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            User user = new User();
                            user.setFirstName(firstName);
                            user.setLastName(lastName);
                            user.setOtherName(otherName);
                            user.setEmail(email);
                            user.setPassword(password);
                            user.setUserID(mAuth.getCurrentUser().getUid());

                            Prevalent.USERS_COLLECTION.document(mAuth.getCurrentUser().getUid())
                                    .set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                progressDialog.dismiss();

                                                Prevalent.CURRENT_USER = user;

                                                Intent intent = new Intent(SignUpActivity.this, HomeActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                mAuth.signOut();
                                                Toast.makeText(SignUpActivity.this, task.getException().getMessage()
                                                        , Toast.LENGTH_SHORT).show();
                                                progressDialog.dismiss();

                                            }
                                        }
                                    });

                        } else {
                            Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();


                        }

                    }
                });
    }
}