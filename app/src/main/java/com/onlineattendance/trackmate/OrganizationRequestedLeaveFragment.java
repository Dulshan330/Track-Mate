package com.onlineattendance.trackmate;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onlineattendance.trackmate.databinding.FragmentOrganizationRequestedLeaveBinding;


public class OrganizationRequestedLeaveFragment extends Fragment {


    private FragmentOrganizationRequestedLeaveBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentOrganizationRequestedLeaveBinding.inflate(inflater,container,false);
        View rootview = binding.getRoot();


        return rootview;
    }
}