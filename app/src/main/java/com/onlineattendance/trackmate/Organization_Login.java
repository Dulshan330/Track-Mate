package com.onlineattendance.trackmate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
public class Organization_Login extends AppCompatActivity {

    // Declare all variables
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private Button sign;
    private TextInputEditText id, username, password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organization_login);
        // Link the ID of widgets with variable
        sign = (Button) findViewById(R.id.orgsign);
        id = findViewById(R.id.orgid);
        username = findViewById(R.id.orgusername);
        password = findViewById(R.id.orgpassword);

        //  set on click listener to signin button and call openOraganizationDashboard method to native next screen
        sign.setOnClickListener(v -> {
            String orgId = id.getText().toString();
            String orgUsername = username.getText().toString();
            String orgPassword = password.getText().toString();

            DatabaseReference getOrgLogins = database.getReference("Org_Login").child(orgId);

            getOrgLogins.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        // Pass the stored data to a string
                        String getOrgId = snapshot.child("Org_ID").getValue(String.class);
                        String getOrgUsername = snapshot.child("Username").getValue(String.class);
                        String getOrgPassword = snapshot.child("Password").getValue(String.class);

                        //Validate the login details
                        if ((orgId.equals(getOrgId))&&(orgUsername.equals(getOrgUsername))&&(orgPassword.equals(getOrgPassword))){
                            openOraganizationNavigation();
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

        });

    }
    // Navigation method to other screen
    public void openOraganizationNavigation(){
        Intent intent = new Intent(this,Organization_Navigation.class);
        startActivity(intent);
    }
}