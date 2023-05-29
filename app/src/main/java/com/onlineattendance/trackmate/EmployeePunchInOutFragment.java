package com.onlineattendance.trackmate;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.onlineattendance.trackmate.databinding.FragmentEmployeePunchInOutBinding;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class EmployeePunchInOutFragment extends Fragment implements OnMapReadyCallback {
    //  Declare all variables
    DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    private FragmentEmployeePunchInOutBinding binding;
    private GoogleMap mMap;
    private LocationManager locationManager;
    private LatLng userLocation;
    private MapView mapView;
    private Button punchInBtn, punchOutBtn;
    private TextView dateview,punchInTime, punchOutTime ;
    private String locationAddress;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_employee_punch_in_out, container, false);
        mapView = rootView.findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        // Initialize SharedPreferences to retrieve Emp_No
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("EMP_INFO", Context.MODE_PRIVATE);
        String getEmpNo = sharedPreferences.getString("EMP_NO","");

        // Link the ID of widgets with variable
        punchInBtn =  rootView.findViewById(R.id.punch_in_btn);
        punchOutBtn = rootView.findViewById(R.id.punch_out_btn);
        punchInTime = rootView.findViewById(R.id.punchin_time);
        punchOutTime = rootView.findViewById(R.id.punchout_time);
        dateview = rootView.findViewById(R.id.date);

        // Initialize LocationManager
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        // Set OnClickListener to punch in button to take date and time
        punchInBtn.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat day = new SimpleDateFormat("EEEE", Locale.getDefault());
            String currentPunchInTime = time.format(calendar.getTime());
            String currentDate = date.format(calendar.getTime());
            String currentDay = day.format(calendar.getTime());
            dateview.setText(currentDate+"\n"+currentDay);
            punchInTime.setText(currentPunchInTime);

            // Get the fused location provider client.
            FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

            // Request the last known location.
                    fusedLocationProviderClient.getLastLocation()
                        .addOnSuccessListener(new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // Get the latitude and longitude of the location.
                                double latitude = location.getLatitude();
                                double longitude = location.getLongitude();

                                // Create a geocoder object.
                                Geocoder geocoder = new Geocoder(getActivity());

                                // Get the address of the location.
                                List<Address> addresses = null;
                                try {
                                    addresses = geocoder.getFromLocation(latitude, longitude, 1);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }

                                // Get the first address in the list.
                                Address address = addresses.get(0);
                                // Get the city name.
                                String cityName = address.getLocality();
                                // Get the state name.
                                String stateName = address.getAdminArea();
                                // Get the country name.
                                String countryName = address.getCountryName();

                                String userLocationAddress =  cityName + ", " + stateName + ", " + countryName;

                                // Create a new data object with the values for punch in time
                                Map<String, Object> data = new HashMap<>();
                                data.put("Emp_ID",getEmpNo);
                                data.put("Date",currentDate);
                                data.put("Punch_in_time",currentPunchInTime);
                                data.put("Punch_in_Address", userLocationAddress);

                                // Put the data to the database
                                database.child("Emp_Attendance").child(getEmpNo).child(currentDate).setValue(data);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Handle the error.
                                Log.e("Error", e.getMessage());
                            }
                        });

            punchInBtn.setEnabled(false);
        });

        // Set OnClickListener to punch out button to take time
        punchOutBtn.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String currentPunchOutTime = time.format(calendar.getTime());
            String currnetDate = date.format(calendar.getTime());
            punchOutTime.setText(currentPunchOutTime);

            // Get the fused location provider client.
            FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

            // Request the last known location.
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Get the latitude and longitude of the location.
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();

                            // Create a geocoder object.
                            Geocoder geocoder = new Geocoder(getActivity());

                            // Get the address of the location.
                            List<Address> addresses = null;
                            try {
                                addresses = geocoder.getFromLocation(latitude, longitude, 1);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }

                            // Get the first address in the list.
                            Address address = addresses.get(0);
                            // Get the city name.
                            String cityName = address.getLocality();
                            // Get the state name.
                            String stateName = address.getAdminArea();
                            // Get the country name.
                            String countryName = address.getCountryName();

                            String userLocationAddress =  cityName + ", " + stateName + ", " + countryName;


                            // Create a new data object with the values for punch in time
                            Map<String, Object> data = new HashMap<>();
                            data.put("Punch_out_time",currentPunchOutTime);
                            data.put("Punch_out_Address", userLocationAddress);

                            // Put the data to the database
                            database.child("Emp_Attendance").child(getEmpNo).child(currnetDate).updateChildren(data);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Handle the error.
                            Log.e("Error", e.getMessage());
                        }
                    });

            punchOutBtn.setEnabled(false);
        });

        return rootView;
    }

//    implement the code to get permission and track the location of the user.
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        // Check location permission
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            // Enable My Location layer on the map
            mMap.setMyLocationEnabled(true);

        } else {
            // Request location permission
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

}