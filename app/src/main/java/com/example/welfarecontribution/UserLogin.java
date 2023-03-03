package com.example.welfarecontribution;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class UserLogin extends AppCompatActivity {

    private TextView loginPageQuestion;
    private Button loginButton;
    private EditText loginEmail;
    SharedPreferences pref;
    private EditText loginPassword;
    private ProgressDialog loader;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);
        loginEmail = findViewById(R.id.loginEmail);
        loginPassword = findViewById(R.id.loginPassword);
        loader = new ProgressDialog(this);
        loginButton = findViewById(R.id.loginButton);
        mAuth = FirebaseAuth.getInstance();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = loginEmail.getText().toString().trim();
                final String password = loginPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    loginEmail.setError("Email is required");
                }
                if (TextUtils.isEmpty(password)) {
                    loginPassword.setError("Password is required");
                } else {
                    loader.setMessage("Log in in Progress");
                    loader.setCanceledOnTouchOutside(false);
                    loader.show();

                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                        // login unsuccessful
                        if (!task.isSuccessful()) {
                            Toast.makeText(UserLogin.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        // login successful
                        Toast.makeText(UserLogin.this, "Login Successful", Toast.LENGTH_SHORT).show();
                        // move to view members
                        Intent intent = new Intent(UserLogin.this, WaitingUsers.class);
                        startActivity(intent);
                        finish();
                        loader.dismiss();
                    });

                }
            }
        });
    }
}