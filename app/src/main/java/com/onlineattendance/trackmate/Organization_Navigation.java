package com.onlineattendance.trackmate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.onlineattendance.trackmate.databinding.ActivityOrganizationNavigationBinding;

public class Organization_Navigation extends AppCompatActivity {

    // Declare all variables
    private BottomNavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organization_navigation);
        // Replace the default fragment with the dashboard fragment
        replaceFragement(new OrganizationDashboardFragment());
        // Initialize the bottom navigation view
        BottomNavigationView navigationView = findViewById(R.id.orgNavigation);

//        Implement the navigation bar to transfer other fragments
        navigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.dashbord_nav:
                    replaceFragement(new OrganizationDashboardFragment());
                    break;
                case R.id.logout_nav:
                    logout();
                    break;
            }
            return true;
        });
    }

    // Method to replace the current fragment with another fragment
    private void replaceFragement(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.org_frame_layout,fragment);
        fragmentTransaction.commit();
    }
    // Method to logout the user
    private void logout(){
        // Clear the data that is stored to SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("ORG_INFO", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        // Start the employee login activity
        Intent intent = new Intent(this,Login_Selection.class);
        startActivity(intent);
    }
}