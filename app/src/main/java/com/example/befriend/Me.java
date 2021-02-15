package com.example.befriend;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class Me extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private RecyclerView recyclerView;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_me);

        auth = FirebaseAuth.getInstance();

        toolbar = (Toolbar) findViewById(R.id.me_tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Setting");

        drawerLayout = (DrawerLayout) findViewById(R.id.setting_drawer);
        actionBarDrawerToggle = new ActionBarDrawerToggle(Me.this, drawerLayout, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        recyclerView = (RecyclerView) findViewById(R.id.setting_list);
        navigationView = (NavigationView) findViewById(R.id.setting_navigationView);

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
