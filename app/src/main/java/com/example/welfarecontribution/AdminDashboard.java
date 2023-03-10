package com.example.welfarecontribution;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import java.nio.file.SecureDirectoryStream;

public class AdminDashboard extends AppCompatActivity {

    Button btnViewMembers, btnSendSMS;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        btnViewMembers = findViewById(R.id.btnViewMembers);
        btnSendSMS = findViewById(R.id.btnSendSMS);

        // view members in waiting list
        btnViewMembers.setOnClickListener(view -> {
            Intent intent = new Intent(AdminDashboard.this, WaitingUsers.class);
            startActivity(intent);
        });

        // send sms to members
        btnSendSMS.setOnClickListener(view -> {
            Intent intent = new Intent(AdminDashboard.this, SendSMS.class);
            startActivity(intent);
        });
    }
}