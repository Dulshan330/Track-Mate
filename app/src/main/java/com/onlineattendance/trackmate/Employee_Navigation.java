package com.onlineattendance.trackmate;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.onlineattendance.trackmate.databinding.ActivityEmployeeNavigationBinding;


public class Employee_Navigation extends AppCompatActivity {
    // Declare all variables
    private ActivityEmployeeNavigationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflate the layout for this activity
        binding = ActivityEmployeeNavigationBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_employee_navigation);

        // Initialize the bottom navigation view
        BottomNavigationView navigationView = findViewById(R.id.empNavigation);

        // Replace the default fragment with the dashboard fragment
        replaceFragement(new EmployeeDashboardFragment());

        // Set up the bottom navigation view to switch between fragments
        navigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.dashbord_nav:
                    replaceFragement(new EmployeeDashboardFragment());
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
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.commit();
    }

    // Method to logout the user
    private void logout(){
        // Clear the data stored in SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("EMP_INFO", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        // Start the employee login activity
        Intent intent = new Intent(this,Login_Selection.class);
        startActivity(intent);
    }
}