package com.onlineattendance.trackmate;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.onlineattendance.trackmate.databinding.FragmentEmployeeAttendanceBinding;


public class EmployeeAttendanceFragment extends Fragment {

    // Declare all variables
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FragmentEmployeeAttendanceBinding binding;
    private Spinner dropdownYear,dropdownMonth,dropdownDate;
    private ArrayAdapter<String> dropdownYearList,dropdownMonthList,dropdownDateList;
    private Button resetBtn,submitBtn;
    private TextView punchDate,punchInTime,punchOutTime,punchInAddress,punchOutAddress;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this activity
        binding = FragmentEmployeeAttendanceBinding.inflate(inflater,container,false);
        View rootview = binding.getRoot();

        // Link the ID of widgets with variable
        resetBtn = rootview.findViewById(R.id.emp_attendance_reset_button);
        submitBtn = rootview.findViewById(R.id.emp_attendance_submit_button);
        punchDate = rootview.findViewById(R.id.emp_attendance_getDate);
        punchInTime = rootview.findViewById(R.id.emp_attendance_getPunchintime);
        punchOutTime = rootview.findViewById(R.id.emp_attendance_getPunchouttime);
        punchInAddress = rootview.findViewById(R.id.emp_attendance_getPunchinaddress);
        punchOutAddress = rootview.findViewById(R.id.emp_attendance_getPunchoutaddress);


        // Initialize SharedPreferences to retrieve Emp_No
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("EMP_INFO", Context.MODE_PRIVATE);
        String getEmpNo = sharedPreferences.getString("EMP_NO","");

        // Implement the code for dropdown items
        dropdownYear = rootview.findViewById(R.id.emp_attendance_dropdown_year);
        dropdownMonth = rootview.findViewById(R.id.emp_attendance_dropdown_month);
        dropdownDate = rootview.findViewById(R.id.emp_attendance_dropdown_date);

        dropdownYearList = new ArrayAdapter<>(getContext(),android.R.layout.simple_spinner_item, new String[]{"-Select Year-","2023","2022"} );
        dropdownMonthList = new ArrayAdapter<>(getContext(),android.R.layout.simple_spinner_item, new String[]{"-Select Month-","01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"});
        dropdownDateList = new ArrayAdapter<>(getContext(),android.R.layout.simple_spinner_item, new String[]{"-Select Date-","01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"});

        dropdownYearList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdownMonthList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdownDateList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        dropdownYear.setAdapter(dropdownYearList);
        dropdownMonth.setAdapter(dropdownMonthList);
        dropdownDate.setAdapter(dropdownDateList);

        // Implement a button to retrive the attendance information of the employees
        submitBtn.setOnClickListener(v -> {
            String getYear = dropdownYear.getSelectedItem().toString();
            String getMonth = dropdownMonth.getSelectedItem().toString();
            String getDate = dropdownDate.getSelectedItem().toString();

            String date = getYear+"-"+getMonth+"-"+getDate;

            // Check whether there are empty fields
            if (getYear.isEmpty() || getMonth.isEmpty() || getDate.isEmpty() ){
                Toast.makeText(getActivity(), "Please fill out all fields.", Toast.LENGTH_SHORT).show();
            }else {
                DatabaseReference reference = database.getReference("Emp_Attendance").child(getEmpNo).child(date);

                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // Pass the stored data to a string
                        String getpunchInTime = snapshot.child("Punch_in_time").getValue(String.class);
                        String getpunchInAddress = snapshot.child("Punch_in_Address").getValue(String.class);
                        String getpunchOutTime = snapshot.child("Punch_out_time").getValue(String.class);
                        String getpunchOutAddress = snapshot.child("Punch_out_Address").getValue(String.class);

                        // Set the retrieved data to EditText Views
                        punchDate.setText(date);
                        punchInTime.setText(getpunchInTime);
                        punchOutTime.setText(getpunchOutTime);
                        punchInAddress.setText(getpunchInAddress);
                        punchOutAddress.setText(getpunchOutAddress);

                        Toast.makeText(getActivity(), "Data retrieved successfully", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getActivity(), "Error saving data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }


        });

        // Set OnClickListener to reset the input fields
        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataClearMethod();
            }
        });


        return rootview;
    }

    // create a method to clear text fields
    private void dataClearMethod(){
        dropdownMonth.setSelection(0);
        dropdownYear.setSelection(0);
        dropdownDate.setSelection(0);
    }
}