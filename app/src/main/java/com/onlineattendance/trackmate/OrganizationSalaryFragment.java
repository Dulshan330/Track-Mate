package com.onlineattendance.trackmate;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.onlineattendance.trackmate.databinding.FragmentOrganizationSalaryBinding;


public class OrganizationSalaryFragment extends Fragment {

    private FragmentOrganizationSalaryBinding binding;
    private EditText empNo, hourlyRate, otRate, reimburse, allowance;
    private Button submit, clear;

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

        clear = rootview.findViewById(R.id.org_salary_clear_button);

        clear.setOnClickListener(v -> {
            empNo.setText(null);
            hourlyRate.setText(null);
            otRate.setText(null);
            reimburse.setText(null);
            allowance.setText(null);
        });


        return rootview;
    }
}