package com.example.welfarecontribution;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AwardedUsers extends AppCompatActivity {
    RecyclerView recyclerView;
    UsersAdapter usersAdapter;
    ArrayList<User> users;
    FirebaseDatabase database;
    DatabaseReference awardedUsersRef, usersRef;
    ProgressDialog loader;
    Button btnRestartCycle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_awarded_users);

        // set layout for your loaded view
        recyclerView = findViewById(R.id.recyclerView);

        loader = new ProgressDialog(this);
        database = FirebaseDatabase.getInstance();
        btnRestartCycle = findViewById(R.id.btnRestartCycle);
        awardedUsersRef = database.getReference("AwardedUsers");
        usersRef = database.getReference("Users");
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        users = new ArrayList<>();
        usersAdapter = new UsersAdapter(this, users);

        recyclerView.setAdapter(usersAdapter);
        loader.setMessage("Getting awarded members ...");
        loader.setCanceledOnTouchOutside(false);
        loader.show();

        // listen for new awarded users
        awardedUsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // check if there are no any awarded users
                if (!snapshot.exists()) {
                    loader.dismiss();
                    Toast.makeText(AwardedUsers.this, "No awarded user currently", Toast.LENGTH_SHORT).show();
                }
                users.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    User newUser = ds.getValue(User.class);
                    users.add(newUser);
                }
                usersAdapter.notifyDataSetChanged();
                if (loader.isShowing()) loader.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AwardedUsers.this, "Database Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                loader.dismiss();
            }
        });

        // restart cycle
        btnRestartCycle.setOnClickListener(view -> {
            awardedUsersRef.removeValue().addOnCompleteListener(task -> usersRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds: dataSnapshot.getChildren()){
                        User user = ds.getValue(User.class);
                        /**
                         * Set member as not awarded and remove them too from the awarded members list
                         */
                        if(user.isAwarded()){
                            user.setAwarded(false);
                            usersRef.child(user.getId()).updateChildren(user.toMap());
                            // delete the user too in awarded list
                            awardedUsersRef.child(user.getId()).removeValue();
                        }
                    }

                    Toast.makeText(AwardedUsers.this, "Cycle restarted successfully", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(AwardedUsers.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }));
        });

    }
}