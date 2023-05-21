package com.onlineattendance.trackmate;

import android.media.JetPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.onlineattendance.trackmate.databinding.FragmentOrganizationAddEmployeeBinding;

import java.util.HashMap;
import java.util.Map;

public class OrganizationAddEmployeeFragment extends Fragment {
    DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    private FragmentOrganizationAddEmployeeBinding binding;
    private EditText name, empNo, nic, designation, mobile, password, username;
    private Button addBtn, clearBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentOrganizationAddEmployeeBinding.inflate(inflater,container,false);
        View rootview = binding.getRoot();

        // Link the ID of widgets with variable
        name = rootview.findViewById(R.id.org_dash_nameofemployee);
        empNo = rootview.findViewById(R.id.org_dash_empno);
        nic = rootview.findViewById(R.id.org_dash_nic);
        designation = rootview.findViewById(R.id.org_dash_designation);
        mobile = rootview.findViewById(R.id.org_dash_mobile);
        username = rootview.findViewById(R.id.org_dash_username);
        password = rootview.findViewById(R.id.org_dash_password);

        addBtn = rootview.findViewById(R.id.org_dash_addbtn);
        clearBtn = rootview.findViewById(R.id.org_dash_clearbtn);

        // Clear the Edit text fields
        clearBtn.setOnClickListener(v -> {
            dataClearmethod();
            Toast.makeText(getActivity(), "Text Fields are clear!", Toast.LENGTH_SHORT).show();
        });


        addBtn.setOnClickListener(v -> {
            // Get the values from the EditText views
            String empName = name.getText().toString();
            String empEmpNo = empNo.getText().toString();
            String empNic = nic.getText().toString();
            String empDesignation = designation.getText().toString();
            String empMobile = mobile.getText().toString();
            String empUsername = username.getText().toString();
            String empPassword = password.getText().toString();

            // Check whether there are empty fields
            if (empName.isEmpty() || empEmpNo.isEmpty() || empNic.isEmpty() || empDesignation.isEmpty() || empMobile.isEmpty() || empUsername.isEmpty() || empPassword.isEmpty()){
                Toast.makeText(getActivity(), "Please fill out all fields.", Toast.LENGTH_SHORT).show();
            }
            else {
                // Create a new data object with the values - 1st tree
                Map<String, Object> data = new HashMap<>();
                data.put("Name_of_the_Employee", empName);
                data.put("Emp_No", empEmpNo);
                data.put("NIC", empNic);
                data.put("Designation", empDesignation);
                data.put("Mobile_No", empMobile);
                data.put("Username", empUsername);
                data.put("Password", empPassword);

                // Create a new data object with the values - 2nd tree
                Map<String,Object> user = new HashMap<>();
                user.put("Emp_No", empEmpNo);
                user.put("Username", empUsername);
                user.put("Password", empPassword);

                // clear data
                dataClearmethod();

                // Push the data to the database
                database.child("Users").child(empEmpNo).setValue(data)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(getActivity(), "Data saved successfully", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getActivity(), "Error saving data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                // Push the data to the database
                database.child("Employee_Login").child(empUsername).setValue(user);
            }

        });

        return rootview;
    }

    // Create a method to clear the data
    private void dataClearmethod(){
        name.setText(null);
        empNo.setText(null);
        nic.setText(null);
        designation.setText(null);
        mobile.setText(null);
        username.setText(null);
        password.setText(null);
    }

}