package com.onlineattendance.trackmate;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.onlineattendance.trackmate.databinding.FragmentEmployeeDashboardBinding;
import com.onlineattendance.trackmate.databinding.FragmentEmployeePunchInOutBinding;


public class EmployeeDashboardFragment extends Fragment {

//  Declare all variables
    private FragmentEmployeeDashboardBinding binding;
    private LinearLayout punchInOutBtn;
    private LinearLayout profileBtn;
    private LinearLayout requestLeave;
    private LinearLayout attendance;

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentEmployeeDashboardBinding.inflate(inflater, container, false);
        View view =binding.getRoot();
        // Link the ID of widgets with variable
        punchInOutBtn = view.findViewById(R.id.emp_punchin);
        profileBtn = view.findViewById(R.id.emp_profile);
        requestLeave = view.findViewById(R.id.emp_leave);
        attendance = view.findViewById(R.id.emp_attendance);

        // Navigate to EmployeePunchInOutFragment fragment
        punchInOutBtn.setOnClickListener(v -> onTransferNewFragment(new EmployeePunchInOutFragment()));

        // Navigate to EmployeeProfileFragment fragment
        profileBtn.setOnClickListener(v -> onTransferNewFragment(new EmployeeProfileFragment()));

        // Navigate to EmployeeRequestLeaveFragment fragment
        requestLeave.setOnClickListener(v -> onTransferNewFragment(new EmployeeRequestLeaveFragment()));

        // Navigate to EmployeeAttendanceFragment fragment
        attendance.setOnClickListener(v -> onTransferNewFragment(new EmployeeAttendanceFragment()));

        return view;
    }


//   Create a method to transfer other fragments from dashboard
    private void onTransferNewFragment(Fragment fragment){
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .addToBackStack(null)
                .commit();
    }


}