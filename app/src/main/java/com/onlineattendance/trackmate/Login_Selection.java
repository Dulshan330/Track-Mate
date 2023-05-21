package com.onlineattendance.trackmate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Login_Selection extends AppCompatActivity {

    //  declare all variables
    private Button empBtn,orgBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_selection);

        empBtn = (Button) findViewById(R.id.button);
        orgBtn = (Button) findViewById(R.id.button2);

        // Set OnClickListener and call the method to transfer Employee_Login activity
        empBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEmployeeLogin();
            }
        });
        // Set OnClickListener and call the method to transfer Organization_Login activity
        orgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openOrgLogin();
            }
        });


    }

    // Create a method to transfer Employee_Login activity
    public void openEmployeeLogin(){
        Intent intent = new Intent(this,Employee_Login.class);
        startActivity(intent);
    }

    // Create a method to transfer Organization_Login activity
    public void openOrgLogin(){
        Intent intent = new Intent(this,Organization_Login.class);
        startActivity(intent);
    }

}