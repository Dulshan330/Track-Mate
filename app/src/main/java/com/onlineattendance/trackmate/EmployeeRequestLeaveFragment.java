package com.onlineattendance.trackmate;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.onlineattendance.trackmate.databinding.FragmentEmployeeRequestLeaveBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class EmployeeRequestLeaveFragment extends Fragment {

    // Declare all variables
    DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    private FragmentEmployeeRequestLeaveBinding binding;
    private Spinner dropdown;
    private ArrayAdapter<String> dropDownList;
    final Calendar calendar = Calendar.getInstance();
    final Calendar calendar2 = Calendar.getInstance();
    private Button resetBtn,applyBtn;
    private TextView reason;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentEmployeeRequestLeaveBinding.inflate(inflater, container, false);
        View rootview = binding.getRoot();

        // Initialize SharedPreferences to retrieve Emp_No
        android.content.SharedPreferences sharedPreferences = getActivity().getSharedPreferences("EMP_INFO", Context.MODE_PRIVATE);
        String getEmpNo = sharedPreferences.getString("EMP_NO","");

        // Link the ID of widgets with variable
        resetBtn = rootview.findViewById(R.id.emp_leave_reset_button);
        applyBtn = rootview.findViewById(R.id.emp_leave_apply_button);
        reason = rootview.findViewById(R.id.emp_leave_reason);

        dropdown = rootview.findViewById(R.id.emp_leave_dropdown);
        dropDownList = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, new String[]{"-Select Leave-","Casual Leave","Short Leave","No Pay Leave","Medical Leave"});
        dropDownList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdown.setAdapter(dropDownList);

        final TextInputEditText calendarInput = rootview.findViewById(R.id.calendar_input);
        calendarInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create a DatePickerDialog with the current date and set an OnDateSetListener
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        requireContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                // Update the calendar with the selected date
                                calendar.set(Calendar.YEAR, year);
                                calendar.set(Calendar.MONTH, month);
                                calendar.set(Calendar.DAY_OF_MONTH, day);

                                // Update the calendar input text with the selected date
                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                                calendarInput.setText(dateFormat.format(calendar.getTime()));
                            }
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                );
                // Show the DatePickerDialog
                datePickerDialog.show();
            }
        });

        final TextInputEditText calendarInput2 = rootview.findViewById(R.id.calendar_input_2);
        calendarInput2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create a DatePickerDialog with the current date and set an OnDateSetListener
                DatePickerDialog datePickerDialog2 = new DatePickerDialog(
                        requireContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                // Update the second calendar with the selected date
                                calendar2.set(Calendar.YEAR, year);
                                calendar2.set(Calendar.MONTH, month);
                                calendar2.set(Calendar.DAY_OF_MONTH, day);

                                // Update the second calendar input text with the selected date
                                SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                                calendarInput2.setText(dateFormat2.format(calendar2.getTime()));
                            }
                        },
                        calendar2.get(Calendar.YEAR),
                        calendar2.get(Calendar.MONTH),
                        calendar2.get(Calendar.DAY_OF_MONTH)
                );
                // Show the second DatePickerDialog
                datePickerDialog2.show();
            }
        });

        // Set OnClickListener to apply button to store leave request to database
        applyBtn.setOnClickListener(v -> {
            String getLeaveType = dropdown.getSelectedItem().toString();
            String getReason = reason.getText().toString();
            String getBeginDate = calendarInput.getText().toString();
            String getEndDate = calendarInput2.getText().toString();

            // Check whether there are empty fields
            if (getLeaveType.isEmpty() || getReason.isEmpty() || getBeginDate.isEmpty() || getEndDate.isEmpty()){
                Toast.makeText(getActivity(), "Please fill out all fields.", Toast.LENGTH_SHORT).show();
            }
            else {
                // Create a new data object with the values
                Map<String, Object> data = new HashMap<>();
                data.put("Emp No",getEmpNo);
                data.put("Leave Type",getLeaveType);
                data.put("Reason",getReason);
                data.put("Begin Date",getBeginDate);
                data.put("End Date", getEndDate);

                // Clear the Entered data
                dropdown.setSelection(0);
                reason.setText("");
                calendarInput.setText("");
                calendarInput2.setText("");

                // Push the data to the database
                database.child("Leave Request").child(getEmpNo).setValue(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getActivity(), "Leave Request is Submitted", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Error saving data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });


        // Set OnClickListener to  reset button to reset the input field
        resetBtn.setOnClickListener(v -> {
            dropdown.setSelection(0);
            reason.setText("");
            calendarInput.setText("");
            calendarInput2.setText("");
        });

        return rootview;
    }


}