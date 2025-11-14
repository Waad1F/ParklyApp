package com.example.parklyapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    EditText emailEditText, passwordEditText;
    Button loginButton;
    TextView signUpText, forgotPasswordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        signUpText = findViewById(R.id.signUpText);
        forgotPasswordText = findViewById(R.id.forgotPasswordText);


        signUpText.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
        });

        forgotPasswordText.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });

        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "يرجى إدخال البريد وكلمة المرور", Toast.LENGTH_SHORT).show();
            } else {
                if (email.equals("admin@parkly.com") && password.equals("admin")) {
                    Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
                    intent.putExtra("role", "admin");
                    startActivity(intent);
                    finish();
                } else {
                    loginUser(email, password);                    Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
                    intent.putExtra("role", "user");

                }
            }
        });
    }

    private void loginUser(String emailOrPhone, String password) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userRef = database.getReference("users");

        boolean isEmail = android.util.Patterns.EMAIL_ADDRESS.matcher(emailOrPhone).matches();

        Query query;
        if (isEmail) {
            query = userRef.orderByChild("email").equalTo(emailOrPhone);
        } else {
            query = userRef.orderByChild("phone").equalTo(emailOrPhone);
        }

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        String dbPassword = userSnapshot.child("password").getValue(String.class);
                        String dbUserType = userSnapshot.child("userType").getValue(String.class);

                        if (dbPassword != null && dbPassword.equals(password)) {
                            User user = userSnapshot.getValue(User.class);
                            String userKey = userSnapshot.getKey();
                            saveUserSession(userKey, user);
                            if(dbUserType != null && dbUserType.equals("admin")){
                                Intent intent = new Intent(LoginActivity.this, UpdateParking.class);
                                startActivity(intent);
                                finish();
                            }
                            else{
                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                startActivity(intent);
                                finish();
                            }

                        } else {
                            Toast.makeText(LoginActivity.this, "Incorrect password", Toast.LENGTH_SHORT).show();
                        }
                        return;
                    }
                } else {
                    String errorMessage = isEmail ?
                            "Email not found" : "Phone number not found";
                    Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(LoginActivity.this, "Database error", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void saveUserSession(String userKey, User user) {
        SharedPreferences preferences = getSharedPreferences("user_session", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString("user_key", userKey);
        editor.putString("name", user.getName());
        editor.putString("email", user.getEmail());
        editor.putString("phone", user.getPhone());
        editor.putString("userType", user.getUserType());
        editor.apply();
    }

}