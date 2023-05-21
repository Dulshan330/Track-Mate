package com.onlineattendance.trackmate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.Context;

public class Employee_Login extends AppCompatActivity {
    //  declare all private variables
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private Button signin;
    private TextInputEditText username, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_login);
        // Pass the IDs from wedget to variable
        signin = (Button) findViewById(R.id.empsign);
        username = findViewById(R.id.login_empUsername);
        password = findViewById(R.id.login_empPassword);

//      set on click listener to sign in button and call openEmployeeNavigation method to native next screen
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get value from TextInputEditText
                String empUsername = username.getText().toString();
                String empPassword = password.getText().toString();
                // Initialize DatabaseReference to access data
                DatabaseReference getEmpLogins = database.getReference("Employee_Login").child(empUsername);
                // Call addValueEventListener to retrieve the data
                getEmpLogins.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            // Pass the stored data to a string
                            String getusername = snapshot.child("Username").getValue(String.class);
                            String getPassword = snapshot.child("Password").getValue(String.class);
                            String getEmpNo = snapshot.child("Emp_No").getValue(String.class);

                            // Validate the login details
                            if ((empUsername.equals(getusername))&&(empPassword.equals(getPassword))){
                                // Initialize SharedPreferences to store Emp_No
                                SharedPreferences sharedPreferences = getSharedPreferences("EMP_INFO", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("EMP_NO", getEmpNo);
                                editor.apply();

                                //Navigate to Employee_Navigation screen
                                openEmployeeNavigation();

                            }
                            else {
                                Toast.makeText(getApplicationContext(), "Invalid Username or Password", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Invalid Username or Password", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getApplicationContext(), "Error saving data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

    }
//   Navigation method to other screen
    private void openEmployeeNavigation(){
        Intent intent = new Intent(this,Employee_Navigation.class);
        startActivity(intent);
    }

}