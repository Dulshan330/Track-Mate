package com.onlineattendance.trackmate;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.onlineattendance.trackmate.databinding.FragmentOrganizationAttendanceBinding;


public class OrganizationAttendanceFragment extends Fragment {
    // Declare the variables
    private Spinner dropdownYear, dropdownMonth;
    private ArrayAdapter<String> dropdownYearList,dropdownMonthList;
    private Button clear;
    private EditText empNo;
    private FragmentOrganizationAttendanceBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentOrganizationAttendanceBinding.inflate(inflater,container,false);
        View rootview = binding.getRoot();

        clear = rootview.findViewById(R.id.org_attendance_clear_button);

//      Implement the code for dropdown items
        empNo = rootview.findViewById(R.id.org_attendance_empno);
        dropdownYear = rootview.findViewById(R.id.org_attendance_dropdown_year);
        dropdownMonth = rootview.findViewById(R.id.org_attendance_dropdown_month);

        dropdownYearList = new ArrayAdapter<>(getContext(),android.R.layout.simple_spinner_item, new String[]{"-Select Year-","2023","2022"} );
        dropdownMonthList = new ArrayAdapter<>(getContext(),android.R.layout.simple_spinner_item, new String[]{"-Select Leave-","January","February","March","April","May","June","July","August","September","October","November","December"});

        dropdownYearList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdownMonthList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        dropdownYear.setAdapter(dropdownYearList);
        dropdownMonth.setAdapter(dropdownMonthList);

        // Clear the data
        clear.setOnClickListener(v -> {
            dropdownMonth.setSelection(0);
            dropdownYear.setSelection(0);
            empNo.setText(null);
        });



        return rootview;
    }
}