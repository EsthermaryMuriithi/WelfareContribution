package com.example.welfarecontribution;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class UserReg extends AppCompatActivity {

    private EditText registerFullName, registerPhoneNumber, registerEmail, registerUserID, registerPassword;
    private Button userRegisterButton;
    private ProgressDialog loader;
    private FirebaseAuth mAuth;
    private DatabaseReference userDatabaseRef;
    TextView tvLogin;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_reg);
        registerFullName = findViewById(R.id.registerFullName);
        registerPhoneNumber = findViewById(R.id.registerPhoneNumber);
        registerEmail = findViewById(R.id.registerEmail);
        registerPassword = findViewById(R.id.registerPassword);
        tvLogin = findViewById(R.id.tvLogin);

        userRegisterButton = findViewById(R.id.userRegisterButton);
        loader = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        userRegisterButton.setOnClickListener(view -> {
            final String email = registerEmail.getText().toString().trim();
            final String password = registerPassword.getText().toString().trim();
            final String fullName = registerFullName.getText().toString().trim();
            final String phoneNumber = registerPhoneNumber.getText().toString().trim();


            if (TextUtils.isEmpty(email)) {
                registerEmail.setError("Email is Required!");
                return;
            }
            if (TextUtils.isEmpty(password)) {
                registerPassword.setError("Password is Required!");
                return;
            }
            if (TextUtils.isEmpty(fullName)) {
                registerFullName.setError("FullName is Required!");
                return;
            }
            if (TextUtils.isEmpty(phoneNumber)) {
                registerPhoneNumber.setError("PhoneNumber is Required!");
                return;
            }
            loader.setMessage("Registering User ...");
            loader.setCanceledOnTouchOutside(false);
            loader.show();

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {

                if (!task.isSuccessful()) {
                    String error = task.getException().getMessage();
                    Toast.makeText(UserReg.this, "Error" + error, Toast.LENGTH_LONG).show();
                    loader.dismiss();
                    return;
                }

                String currentUserId = mAuth.getCurrentUser().getUid();
                userDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);
                HashMap userInfo = new HashMap();
                userInfo.put("id", currentUserId);
                userInfo.put("name", fullName);
                userInfo.put("email", email);
                userInfo.put("phoneNumber", phoneNumber);
                userInfo.put("type", "user");

                userDatabaseRef.updateChildren(userInfo).addOnCompleteListener(task1 -> {
                    if (!task1.isSuccessful()) {
                        Toast.makeText(UserReg.this, task1.getException().toString(), Toast.LENGTH_SHORT).show();
                        loader.dismiss();
                        return;
                    }

                    Toast.makeText(UserReg.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                    loader.dismiss();

                    Intent intent = new Intent(UserReg.this, WaitingUsers.class);
                    startActivity(intent);
                });
            });
        });

        // Login in case a user already has an account
        tvLogin.setOnClickListener(view -> {
            Intent intent = new Intent(UserReg.this, UserLogin.class);
            startActivity(intent);
        });

    }
}