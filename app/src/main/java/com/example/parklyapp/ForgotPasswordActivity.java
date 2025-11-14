package com.example.parklyapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class ForgotPasswordActivity extends AppCompatActivity {

    EditText phoneEditText;
    MaterialButton sendButton;
    TextView backToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        phoneEditText = findViewById(R.id.phoneEditText);
        sendButton = findViewById(R.id.sendButton);
        backToLogin = findViewById(R.id.backToLogin);

        sendButton.setOnClickListener(v -> {
            String phoneNumber =phoneEditText.getText().toString().trim();

            if (phoneNumber.isEmpty()) {
                Toast.makeText(this, "Please enter your phone number", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Reset code sent to " + phoneNumber, Toast.LENGTH_SHORT).show();
            }
        });

        backToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}