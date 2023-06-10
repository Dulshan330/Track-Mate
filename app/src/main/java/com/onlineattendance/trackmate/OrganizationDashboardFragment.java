package com.onlineattendance.trackmate;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.onlineattendance.trackmate.databinding.FragmentOrganizationDashboardBinding;

public class OrganizationDashboardFragment extends Fragment {
    // Declare all variables
    private LinearLayout addEmployees, removeEmployee, requestedLeave, attendance,salary;
    private FragmentOrganizationDashboardBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentOrganizationDashboardBinding.inflate(inflater,container,false);
        View rootview = binding.getRoot();
        // Link the ID of widgets with variable
        addEmployees = rootview.findViewById(R.id.org_adduser);
        removeEmployee = rootview.findViewById(R.id.org_removeuser);
        requestedLeave = rootview.findViewById(R.id.org_requested_leave);
        attendance = rootview.findViewById(R.id.org_attendance);
        salary = rootview.findViewById(R.id.org_salary);

        // Navigate to other fragments
        addEmployees.setOnClickListener(v -> onTransferNewFragment(new OrganizationAddEmployeeFragment()));
        removeEmployee.setOnClickListener(v -> onTransferNewFragment(new OrganizationRemoveEmployeeFragment()));
        requestedLeave.setOnClickListener(v -> onTransferNewFragment(new OrganizationRequestedLeaveFragment()));
        attendance.setOnClickListener(v -> onTransferNewFragment(new OrganizationAttendanceFragment()));
        salary.setOnClickListener(v -> onTransferNewFragment(new OrganizationSalaryFragment()));

        return rootview;
    }

    //   Create a method to transfer other fragments from dashboard
    private void onTransferNewFragment(Fragment fragment){
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.org_frame_layout, fragment)
                .addToBackStack(null)
                .commit();
    }
}