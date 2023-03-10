package com.example.welfarecontribution;

import static java.security.AccessController.getContext;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Random;

public class WaitingUsers extends AppCompatActivity {
    RecyclerView recyclerView;
    UsersAdapter usersAdapter;
    ArrayList<User> users;
    FirebaseDatabase database;
    DatabaseReference ref, usersRef, awardedUsersRef;
    ImageView spinImage;
    Animation spinAnimation;
    ProgressDialog loader;
    AlertDialog.Builder builder;
    Button btnAwardedUsers;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_users);
        btnAwardedUsers = findViewById(R.id.btnAwardedUsers);

        // show progress dialog
        loader = new ProgressDialog(this);
        builder = new AlertDialog.Builder(WaitingUsers.this);

        loader.setMessage("Getting un-awarded members...");
        loader.setCanceledOnTouchOutside(false);
        loader.show();

        // set data for the view
        users = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView);
        spinImage = findViewById(R.id.spinImage);
        usersAdapter = new UsersAdapter(this, users);
        database = FirebaseDatabase.getInstance();
        ref = database.getReference();
        usersRef = database.getReference("Users");
        awardedUsersRef = database.getReference("AwardedUsers");

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(usersAdapter);

        // listen for data change in users database reference
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // check if all users have been awarded
                if (!snapshot.exists()) {
                    loader.dismiss();
                    Toast.makeText(WaitingUsers.this, "No Members registered in the system.", Toast.LENGTH_SHORT).show();
                    return;
                }
                // clear existing users
                users.clear();
                User user;
                boolean allUsersAwarded = true; // will change once we find a member who is not awarded;
                for (DataSnapshot ds : snapshot.getChildren()) {
                    user = ds.getValue(User.class);
                    // Only display members who are not yet awarded
                    if (!user.isAwarded()) {
                        allUsersAwarded = false;
                        users.add(user);
                    }
                }

                usersAdapter.notifyDataSetChanged();

                if (loader.isShowing()) loader.dismiss();

                if (allUsersAwarded) {
                    Toast.makeText(WaitingUsers.this, "All members have been awarded.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (loader.isShowing()) loader.dismiss();
            }
        });

        // set spin animation
        spinAnimation = AnimationUtils.loadAnimation(WaitingUsers.this, R.anim.spin);
        // listen when spinning finishes and select a winner
        spinAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                getWinner();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        // randomly select a user
        spinImage.setOnClickListener(v -> {
            if (users.size() == 0) {
                Toast.makeText(WaitingUsers.this, "All members have been awarded.", Toast.LENGTH_SHORT).show();
                return;
            }

            spinImage.startAnimation(spinAnimation);
            /**
             * NOTE: Selecting a winner will be invoked once the spin animation finishes
             * in the onAnimationEnd() method
             */
        });

        // move to awarded members
        btnAwardedUsers.setOnClickListener(view -> {
            Intent intent = new Intent(WaitingUsers.this, AwardedUsers.class);
            startActivity(intent);
        });
    }

    public User getRandomUser() {
        Random random = new Random();
        if (users.size() == 0) return null;

        return users.get(random.nextInt(users.size()));
    }

    // get winner
    public void getWinner() {
        // put winner selection logic here
        User user = getRandomUser();
        // when all users have been awarded
        if (user == null) {
            Toast.makeText(WaitingUsers.this, "All members have been awarded.", Toast.LENGTH_SHORT).show();
            return;
        }
        ;
        // get the position of the user in users ArrayList
        int userPosition = users.indexOf(user);

        users.remove(user);
        usersAdapter.notifyItemRemoved(userPosition);
        usersAdapter.notifyItemRangeChanged(userPosition, users.size());

        Toast.makeText(WaitingUsers.this, "The winner was: " + user.getName(), Toast.LENGTH_SHORT).show();

        // update this user as awarded (in the /user ref)
        user.setAwarded(true);
        usersRef.child(user.getId()).setValue(user);

        // add the selected user to awarded list database (in the /awardedUsers ref)
        awardedUsersRef.child(user.getId()).setValue(user)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(WaitingUsers.this, user.getName() + " successfully added to Awarded Members list.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(WaitingUsers.this, "Unable to add " + user.getName() + " to Awarded Members list", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
