package com.example.welfarecontribution;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

public class Dashboard extends AppCompatActivity {

    private CardView admin;
    private CardView user;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        admin = findViewById(R.id.admin);
        user = findViewById(R.id.user);
        admin.setOnClickListener(View->{
            Intent intent = new Intent(Dashboard.this, AdminLogin.class);
            startActivity(intent);
        });

        user.setOnClickListener(View->{
            Intent intent = new Intent(Dashboard.this, UserLogin.class);
            startActivity(intent);
        });
    }
}