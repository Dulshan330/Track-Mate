package com.onlineattendance.trackmate;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.onlineattendance.trackmate.databinding.FragmentEmployeeProfileBinding;

public class EmployeeProfileFragment extends Fragment {

    //  Declare all variables
    private FragmentEmployeeProfileBinding binding;
    private Button mTakePictureButton;
    private TextView name,designation,empNo,nic,salary;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentEmployeeProfileBinding.inflate(inflater, container,false);
        View rootview = binding.getRoot();

        // Initialize SharedPreferences to retrieve Emp_No
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("EMP_INFO", Context.MODE_PRIVATE);
        String getEmpNo = sharedPreferences.getString("EMP_NO","");

        // Link the ID of wedgets with variable
        name = rootview.findViewById(R.id.emp_profile_name);
        designation = rootview.findViewById(R.id.emp_profile_designation);
        empNo = rootview.findViewById(R.id.emp_profile_empNo);
        nic = rootview.findViewById(R.id.emp_profile_nic);

        // Initialize DatabaseReference to access to database
        DatabaseReference getEmpLogins = database.getReference("Users").child(getEmpNo);
        // Initialize addValueEventListener to retrieve user's information from database
        getEmpLogins.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    // Pass the stored data to a string
                    String getname = snapshot.child("Name_of_the_Employee").getValue(String.class);
                    String getNic = snapshot.child("NIC").getValue(String.class);
                    String getDesignation = snapshot.child("Designation").getValue(String.class);

                    // Set the retrieved data to TextViews
                    empNo.setText(getEmpNo);
                    name.setText(getname);
                    nic.setText(getNic);
                    designation.setText(getDesignation);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Error saving data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        return rootview;
    }



}