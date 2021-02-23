package com.example.befriend;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;

import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;

public class Me extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private RecyclerView recyclerView;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private FirebaseAuth auth;
    private CircleImageView profileImg;
    private TextView userName;
    private DatabaseReference ref;
    String CurrentUserID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_me);

        auth = FirebaseAuth.getInstance();
        CurrentUserID = auth.getCurrentUser().getUid();
        ref = FirebaseDatabase.getInstance().getReference().child("Users");

        toolbar = (Toolbar) findViewById(R.id.me_tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Setting");

        drawerLayout = (DrawerLayout) findViewById(R.id.setting_drawer);
        actionBarDrawerToggle = new ActionBarDrawerToggle(Me.this, drawerLayout, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();


        navigationView = (NavigationView) findViewById(R.id.setting_navigationView);
        profileImg = (CircleImageView) findViewById(R.id.me_profile_image);
        userName = (TextView) findViewById(R.id.me_userName);
        ref.child(CurrentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    //get value of username and image url from firebase
                    String name = dataSnapshot.child("userName").getValue().toString();
                    String imageUrl = dataSnapshot.child("profileImage").getValue().toString();
                    //display username and profile image
                    userName.setText(name);
                    Picasso.get().load(imageUrl).placeholder(R.drawable.profile).into(profileImg);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                menuSelector(item);
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(actionBarDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void menuSelector(MenuItem item) {
        switch (item.getItemId()){
            case R.id.navigation_home:
                Toast.makeText(this, "Home",Toast.LENGTH_SHORT).show();
                break;
            case R.id.navigation_logout:
                auth.signOut();
                sendToLoginActivity();
                break;
        }
    }

    private void sendToLoginActivity() {
        Intent loginIntent = new Intent(Me.this, login.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }


}
