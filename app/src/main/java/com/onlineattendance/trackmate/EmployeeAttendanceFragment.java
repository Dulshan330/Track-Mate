package com.onlineattendance.trackmate;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.onlineattendance.trackmate.databinding.FragmentEmployeeAttendanceBinding;


public class EmployeeAttendanceFragment extends Fragment {

    // Declare all variables
    private FragmentEmployeeAttendanceBinding binding;
    private Spinner dropdownYear;
    private ArrayAdapter<String> dropdownYearList;
    private Spinner dropdownMonth;
    private ArrayAdapter<String> dropdownMonthList;
    private Button reset;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this activity
        binding = FragmentEmployeeAttendanceBinding.inflate(inflater,container,false);
        View rootview = binding.getRoot();

        reset = rootview.findViewById(R.id.emp_attendance_reset_button);

//      Implement the code for dropdown items
        dropdownYear = rootview.findViewById(R.id.emp_attendance_dropdown_year);
        dropdownMonth = rootview.findViewById(R.id.emp_attendance_dropdown_month);

        dropdownYearList = new ArrayAdapter<>(getContext(),android.R.layout.simple_spinner_item, new String[]{"-Select Year-","2023","2022"} );
        dropdownMonthList = new ArrayAdapter<>(getContext(),android.R.layout.simple_spinner_item, new String[]{"-Select Leave-","January","February","March","April","May","June","July","August","September","October","November","December"});

        dropdownYearList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdownMonthList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        dropdownYear.setAdapter(dropdownYearList);
        dropdownMonth.setAdapter(dropdownMonthList);

//      Set OnClickListener to reset the input fields
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dropdownMonth.setSelection(0);
                dropdownYear.setSelection(0);
            }
        });


        return rootview;
    }
}