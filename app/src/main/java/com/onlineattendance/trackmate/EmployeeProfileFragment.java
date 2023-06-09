package com.onlineattendance.trackmate;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.onlineattendance.trackmate.databinding.FragmentEmployeeProfileBinding;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class EmployeeProfileFragment extends Fragment {

    private FragmentEmployeeProfileBinding binding;
    private Button mTakePictureButton, passwordChangeButton;
    private TextView name, designation, empNo, nic, hourlyRate, otRate, allowance, reimburse;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static final int PICK_IMAGE_REQUEST_CODE = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
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
                    String getdesig = snapshot.child("Designation").getValue(String.class);
                    String getnic = snapshot.child("NIC").getValue(String.class);

                    name.setText(getname);
                    designation.setText(getdesig);
                    empNo.setText(getEmpNo);
                    nic.setText(getnic);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });

        getSalaryRates.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String gethourlyRate = snapshot.child("Hourly_Rate").getValue(String.class);
                    String getotRate = snapshot.child("OT_Rate").getValue(String.class);
                    String getallowance = snapshot.child("Atterdance_Allowance").getValue(String.class);
                    String getreimburse = snapshot.child("Reimburse").getValue(String.class);

                    hourlyRate.setText(gethourlyRate);
                    otRate.setText(getotRate);
                    allowance.setText(getallowance);
                    reimburse.setText(getreimburse);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });

        passwordChangeButton.setOnClickListener(v -> {
            showpopup();
        });

        // Retrieve and display the profile photo
        retrieveProfilePhoto();

        return rootView;
    }

    private void openImagePicker() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Choose an option")
                .setItems(new CharSequence[]{"Take Photo", "Choose from Gallery"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0:
                                openCamera();
                                break;
                            case 1:
                                openGallery();
                                break;
                        }
                    }
                })
                .show();
    }

    private void openCamera() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CAMERA}, REQUEST_IMAGE_CAPTURE);
        } else {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private void openGallery() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PICK_IMAGE_REQUEST_CODE);
        } else {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            binding.empProfilePhoto.setImageURI(selectedImageUri);
            uploadImageToFirestore();
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            selectedImageUri = getImageUri(requireContext(), imageBitmap);
            binding.empProfilePhoto.setImageBitmap(imageBitmap);
            uploadImageToFirestore();
        }
    }

    private Uri getImageUri(Context context, Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Profile Photo", null);
        return Uri.parse(path);
    }

    private void uploadImageToFirestore() {
        if (selectedImageUri != null) {

            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("EMP_INFO", Context.MODE_PRIVATE);
            String getEmpNo = sharedPreferences.getString("EMP_NO", "");
            // Show progress dialog
            final PopupWindow popupWindow = new PopupWindow(requireContext());
            View popupView = getLayoutInflater().inflate(R.layout.popup_progress, null);
            popupWindow.setContentView(popupView);
            popupWindow.showAtLocation(binding.getRoot(), Gravity.CENTER, 0, 0);

            // Create a storage reference from Firebase
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("profile_photos/" + getEmpNo + ".jpg");

            // Upload the file to Firebase Storage
            storageReference.putFile(selectedImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Get the download URL of the uploaded file
                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    // Save the download URL to Firestore
                                    Map<String, Object> data = new HashMap<>();
                                    data.put("photoUrl", uri.toString());

                                    firestore.collection("employees").document(getEmpNo).set(data)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    // Dismiss progress dialog
                                                    popupWindow.dismiss();
                                                    Toast.makeText(getContext(), "Profile photo uploaded successfully", Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    // Dismiss progress dialog
                                                    popupWindow.dismiss();
                                                    Toast.makeText(getContext(), "Failed to upload profile photo", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Dismiss progress dialog
                            popupWindow.dismiss();
                            Toast.makeText(getContext(), "Failed to upload profile photo", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            // Track the progress of the upload
                            double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                            TextView progressText = popupView.findViewById(R.id.popup_progress_text);
                            progressText.setText("Uploading: " + (int) progress + "%");
                        }
                    });
        }
    }

    private void retrieveProfilePhoto() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("EMP_INFO", Context.MODE_PRIVATE);
        String getEmpNo = sharedPreferences.getString("EMP_NO", "");

        firestore.collection("employees").document(getEmpNo).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String downloadUrl = documentSnapshot.getString("photoUrl");
                            if (downloadUrl != null) {
                                Picasso.get().load(downloadUrl).into(profilePhoto);
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Error retrieving profile photo: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
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


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
