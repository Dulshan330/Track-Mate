package com.onlineattendance.trackmate;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.badge.BadgeUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.onlineattendance.trackmate.databinding.FragmentOrganizationRemoveEmployeeBinding;

public class OrganizationRemoveEmployeeFragment extends Fragment {

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FragmentOrganizationRemoveEmployeeBinding binding;
    private EditText empNo, name,nic,designation,mobile,username;
    private Button searchBtn, clearBtn, removeBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentOrganizationRemoveEmployeeBinding.inflate(inflater,container,false);
        View rootview = binding.getRoot();

        // Pass IDs to variables
        empNo = rootview.findViewById(R.id.org_remove_empno);
        name = rootview.findViewById(R.id.org_remove_nameofemployee);
        nic = rootview.findViewById(R.id.org_remove_nic);
        designation =rootview.findViewById(R.id.org_remove_designation);
        mobile = rootview.findViewById(R.id.org_remove_mobile);
        username = rootview.findViewById(R.id.org_remove_username);

        clearBtn = rootview.findViewById(R.id.org_remove_clearbtn);
        searchBtn = rootview.findViewById(R.id.org_remove_search);
        removeBtn = rootview.findViewById(R.id.org_remove_remove);


        // Implement a button to clear text fields
        clearBtn.setOnClickListener(v -> {
            dataClearmethod();
        });

        // Implement a button to search information of the employee
        searchBtn.setOnClickListener(v -> {
            String searchEmpNo = empNo.getText().toString();
            DatabaseReference reference = database.getReference("Users").child(searchEmpNo);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        // Pass the stored data to a string
                        String getName = snapshot.child("Name_of_the_Employee").getValue(String.class);
                        String getNic = snapshot.child("NIC").getValue(String.class);
                        String getDesignation = snapshot.child("Designation").getValue(String.class);
                        String getMobile = snapshot.child("Mobile_No").getValue(String.class);
                        String getUsername = snapshot.child("Username").getValue(String.class);

                        // Set the retrieved data to EditText Views
                        name.setText(getName);
                        nic.setText(getNic);
                        designation.setText(getDesignation);
                        mobile.setText(getMobile);
                        username.setText(getUsername);

                        Toast.makeText(getActivity(), "Data retrieved successfully", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(getActivity(), "Not exist", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getActivity(), "Error saving data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        // Implement a button to remove information of the employee
        removeBtn.setOnClickListener(v -> {
            String removeEmpNo = empNo.getText().toString();
            String removeLogins = username.getText().toString();
            DatabaseReference removeEmployee = database.getReference("Users").child(removeEmpNo);
            DatabaseReference removeUsername = database.getReference("Employee_Login").child(removeLogins);
            removeEmployee.removeValue();
            removeUsername.removeValue();


            // call to dataClearmethod to clear text fields
            dataClearmethod();

            Toast.makeText(getActivity(), "Data removed successfully", Toast.LENGTH_SHORT).show();
        });

        return rootview;
    }

    // create a method to clear text fields
    private void dataClearmethod(){
        empNo.setText(null);
        name.setText(null);
        nic.setText(null);
        designation.setText(null);
        mobile.setText(null);
        username.setText(null);
    }
}