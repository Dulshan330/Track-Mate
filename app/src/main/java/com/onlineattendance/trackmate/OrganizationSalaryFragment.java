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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.onlineattendance.trackmate.databinding.FragmentOrganizationSalaryBinding;

import java.util.HashMap;
import java.util.Map;


public class OrganizationSalaryFragment extends Fragment {

    // Declare the variables
    DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    private FragmentOrganizationSalaryBinding binding;
    private EditText empNo, hourlyRate, otRate, reimburse, allowance;
    private Button submitBtn, clearBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentOrganizationSalaryBinding.inflate(inflater,container,false);
        View rootview = binding.getRoot();

        empNo = rootview.findViewById(R.id.org_salary_empno);
        hourlyRate = rootview.findViewById(R.id.org_salary_hourlyRate);
        otRate = rootview.findViewById(R.id.org_salary_otRate);
        reimburse = rootview.findViewById(R.id.org_salary_reimburse);
        allowance = rootview.findViewById(R.id.org_salary_allowance);

        submitBtn = rootview.findViewById(R.id.org_salary_submit_button);
        clearBtn = rootview.findViewById(R.id.org_salary_clear_button);

        submitBtn.setOnClickListener( v -> {
            // Get the values from the EditText views
            String getEmpNo = empNo.getText().toString();
            String getHourlyRate = hourlyRate.getText().toString();
            String getOTRate = otRate.getText().toString();
            String getReimburse = reimburse.getText().toString();
            String getAllowance = allowance.getText().toString();

            // Check whether there are empty fields
            if (getEmpNo.isEmpty() || getHourlyRate.isEmpty() || getOTRate.isEmpty() || getReimburse.isEmpty() || getAllowance.isEmpty()){
                Toast.makeText(getActivity(), "Please fill out all fields.", Toast.LENGTH_SHORT).show();
            }
            else {
                // Create a new data object with the values
                Map<String, Object> data = new HashMap<>();
                data.put("Emp_No",getEmpNo);
                data.put("Hourly_Rate",getHourlyRate);
                data.put("OT_Rate",getOTRate);
                data.put("Reimburse",getReimburse);
                data.put("Atterdance_Allowance",getAllowance);

                // clear data
                dataClearmethod();

                database.child("EMP_Salary").child(getEmpNo).setValue(data).addOnSuccessListener(new OnSuccessListener<Void>() {
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
            }
        });

        clearBtn.setOnClickListener(v -> {
            dataClearmethod();
        });


        return rootview;
    }

    private void dataClearmethod(){
        empNo.setText(null);
        hourlyRate.setText(null);
        otRate.setText(null);
        reimburse.setText(null);
        allowance.setText(null);
    }
}