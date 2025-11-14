package com.example.parklyapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {

    EditText fullNameEditText, emailEditText, phoneEditText, passwordEditText;
    MaterialButton signUpButton;
    TextView loginText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        fullNameEditText = findViewById(R.id.fullNameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        signUpButton = findViewById(R.id.signUpButton);
        loginText = findViewById(R.id.loginText);

        signUpButton.setOnClickListener(view -> {
            String name = fullNameEditText.getText().toString();
            String email = emailEditText.getText().toString();
            String phone = phoneEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            String userType="student";

            if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
                Toast.makeText(SignUpActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else {
                User user = new User(name, email, phone, password,userType);
                signUpToDatabase(user);

            }
        });


        loginText.setOnClickListener(v -> {
            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void signUpToDatabase(User user) {
        HashMap<String,Object> userHashMap = new HashMap<>();
        userHashMap.put("name",user.getName());
        userHashMap.put("email",user.getEmail());
        userHashMap.put("phone",user.getPhone());
        userHashMap.put("password",user.getPassword());
        userHashMap.put("userType","user");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userRef = database.getReference("users");

        String key =userRef.push().getKey();
        userHashMap.put("key",key);

        userRef.child(key).setValue(userHashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(SignUpActivity.this, "Account Created!", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });


    }

}