package com.onlineattendance.trackmate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.onlineattendance.trackmate.databinding.FragmentEmployeeProfileBinding;
import com.squareup.picasso.Picasso;

import static android.app.Activity.RESULT_OK;

import java.util.HashMap;
import java.util.Map;

public class EmployeeProfileFragment extends Fragment {

    private FragmentEmployeeProfileBinding binding;
    private Button mTakePictureButton, passwordChangeButton;
    private TextView name, designation, empNo, nic, hourlyRate, otRate, allowance, reimburse;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static final int PICK_IMAGE_REQUEST_CODE = 1;
    private FirebaseFirestore firestore;
    private Uri selectedImageUri;
    private ImageView profilePhoto;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEmployeeProfileBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();

        // Initialize Firebase
        FirebaseApp.initializeApp(requireContext());
        firestore = FirebaseFirestore.getInstance();

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("EMP_INFO", Context.MODE_PRIVATE);
        String getEmpNo = sharedPreferences.getString("EMP_NO", "");

        name = rootView.findViewById(R.id.emp_profile_name);
        designation = rootView.findViewById(R.id.emp_profile_designation);
        empNo = rootView.findViewById(R.id.emp_profile_empNo);
        nic = rootView.findViewById(R.id.emp_profile_nic);
        hourlyRate = rootView.findViewById(R.id.emp_profile_hourltRate);
        otRate = rootView.findViewById(R.id.emp_profile_OTRate);
        allowance = rootView.findViewById(R.id.emp_profile_allowance);
        reimburse = rootView.findViewById(R.id.emp_profile_reimburse);

        profilePhoto = rootView.findViewById(R.id.emp_profile_photo);

        mTakePictureButton = rootView.findViewById(R.id.emp_profilePhotoButton);
        passwordChangeButton = rootView.findViewById(R.id.emp_profile_changePassword);

        mTakePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImagePicker();
            }
        });

        DatabaseReference getEmpLogins = database.getReference("Users").child(getEmpNo);
        DatabaseReference getSalaryRates = database.getReference("EMP_Salary").child(getEmpNo);
        getEmpLogins.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String getname = snapshot.child("Name_of_the_Employee").getValue(String.class);
                    String getNic = snapshot.child("NIC").getValue(String.class);
                    String getDesignation = snapshot.child("Designation").getValue(String.class);

                    empNo.setText(getEmpNo);
                    name.setText(getname);
                    nic.setText(getNic);
                    designation.setText(getDesignation);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Error saving data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        getSalaryRates.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String getHourlyRate = snapshot.child("Hourly_Rate").getValue(String.class);
                    String getOTRate = snapshot.child("OT_Rate").getValue(String.class);
                    String getAllowance = snapshot.child("Atterdance_Allowance").getValue(String.class);
                    String getReimburse = snapshot.child("Reimburse").getValue(String.class);

                    hourlyRate.setText(getHourlyRate);
                    otRate.setText(getOTRate);
                    allowance.setText(getAllowance);
                    reimburse.setText(getReimburse);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Error saving data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Retrieve and display the profile photo
        retrieveProfilePhoto();

        passwordChangeButton.setOnClickListener(v -> {
            showpopup();
        });

        return rootView;
    }

    private void showpopup(){
        LayoutInflater inflater = (LayoutInflater) requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewLayout = inflater.inflate(R.layout.employee_profile_popup,null);

        // Create a new popup window
        PopupWindow popupWindow = new PopupWindow(viewLayout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.showAtLocation(viewLayout, Gravity.CENTER, 0, 0);

        // Find and set the click listener for the save button
        Button saveBtn = viewLayout.findViewById(R.id.emp_profile_changePasswordBtn);
        saveBtn.setOnClickListener(v -> {
            // Find the input fields in the layout
            EditText currentPassword = viewLayout.findViewById(R.id.emp_profile_currentpassword);
            EditText newPassword = viewLayout.findViewById(R.id.emp_profile_newpassword);
            EditText confirmPassword = viewLayout.findViewById(R.id.emp_profile_confirmpassword);

            // Retrieve the input values
            String getCurrentPassword = currentPassword.getText().toString();
            String getNewPassword = newPassword.getText().toString();
            String getConfirmPassword = confirmPassword.getText().toString();

            if (getConfirmPassword.isEmpty() || getCurrentPassword.isEmpty() || getNewPassword.isEmpty()){
                Toast.makeText(getActivity(), "Please fill all the fields", Toast.LENGTH_SHORT).show();
                return;
            }
            else {
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("EMP_INFO", Context.MODE_PRIVATE);
                String getEmpNo = sharedPreferences.getString("EMP_NO", "");
                String getUsername = sharedPreferences.getString("Username", "");

                DatabaseReference reference1 = database.getReference("Users").child(getEmpNo);
                DatabaseReference reference2 = database.getReference("Employee_Login").child(getUsername);
                reference1.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            String getExistCurrentPassword = snapshot.child("Password").getValue(String.class);

                            if ((getExistCurrentPassword.equals(getCurrentPassword)) && (getNewPassword.equals(getConfirmPassword))){
                                // Create a new data object with the input values
                                Map<String, Object> data = new HashMap<>();
                                data.put("Password",getNewPassword);

                                Map<String, Object> data1 = new HashMap<>();
                                data1.put("Password",getNewPassword);

                                // Put the data to the database
                                reference1.updateChildren(data);
                                reference2.updateChildren(data1);

                                // Dismiss the popup window
                                popupWindow.dismiss();

                                Toast.makeText(getActivity(), "Password update successfully", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getActivity(), "Error saving data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE);
    }

    private void retrieveProfilePhoto() {
        String empNoValue = empNo.getText().toString();
        DatabaseReference empRef = database.getReference("Users").child(empNoValue);
        empRef.child("profile_photo_url").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String downloadUrl = snapshot.getValue(String.class);
                    if (downloadUrl != null) {
                        Picasso.get().load(downloadUrl).into(profilePhoto);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Error retrieving profile photo: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            binding.empProfilePhoto.setImageURI(selectedImageUri);
            uploadImageToFirestore();
        }
    }

    private void uploadImageToFirestore() {
        String empNoValue = empNo.getText().toString();
        String filename = empNoValue + ".jpg";
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imageRef = storageRef.child("profile_photos").child(filename);

        imageRef.putFile(selectedImageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        imageRef.getDownloadUrl()
                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String downloadUrl = uri.toString();
                                        saveDownloadUrlToFirestore(downloadUrl);
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Error uploading image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveDownloadUrlToFirestore(String downloadUrl) {
        String empNoValue = empNo.getText().toString();
        DatabaseReference empRef = database.getReference("Users").child(empNoValue);
        empRef.child("profile_photo_url").setValue(downloadUrl)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity(), "Profile photo uploaded successfully!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Error saving profile photo: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
