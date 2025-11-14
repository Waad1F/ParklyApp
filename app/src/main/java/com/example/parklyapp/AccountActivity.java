package com.example.parklyapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AccountActivity extends BaseActivity {

    ImageView userImage;
    TextView userName, changeUsername, changePassword;
    Button btnLanguage;
    LinearLayout logoutLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        userImage = findViewById(R.id.userImage);
        userName = findViewById(R.id.userName);
        logoutLayout = findViewById(R.id.logoutLayout);
        btnLanguage = findViewById(R.id.btnLanguage);
        changeUsername = findViewById(R.id.changeUsername);
        changePassword = findViewById(R.id.changePassword);

        logoutLayout.setOnClickListener(v -> {
            Intent intent = new Intent(AccountActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        btnLanguage.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(AccountActivity.this, btnLanguage);
            popup.getMenuInflater().inflate(R.menu.language_menu, popup.getMenu());

            popup.setOnMenuItemClickListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.lang_english) {
                    saveLanguageAndRestart("en");
                    return true;
                } else if (itemId == R.id.lang_arabic) {
                    saveLanguageAndRestart("ar");
                    return true;
                } else {
                    return false;
                }
            });

            popup.show();
        });

        changeUsername.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.change_username_title));

            final EditText input = new EditText(this);
            input.setHint(getString(R.string.change_username_hint));
            builder.setView(input);

            builder.setPositiveButton(getString(R.string.save), (dialog, which) -> {
                String newUsername = input.getText().toString().trim();
                if (!newUsername.isEmpty()) {
                    userName.setText(newUsername);
                }
            });

            builder.setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.cancel());
            builder.show();
        });

        changePassword.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.change_password_title));

            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setPadding(50, 20, 50, 10);

            final EditText oldPassword = new EditText(this);
            oldPassword.setHint(getString(R.string.old_password));
            layout.addView(oldPassword);

            final EditText newPassword = new EditText(this);
            newPassword.setHint(getString(R.string.new_password));
            layout.addView(newPassword);

            final EditText confirmPassword = new EditText(this);
            confirmPassword.setHint(getString(R.string.confirm_password));
            layout.addView(confirmPassword);

            builder.setView(layout);
            builder.setPositiveButton(getString(R.string.update), (dialog, which) -> {
                String newPass = newPassword.getText().toString().trim();
                String confirmPass = confirmPassword.getText().toString().trim();
                if (newPass.equals(confirmPass)) {Toast.makeText(this, getString(R.string.password_updated), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, getString(R.string.password_mismatch), Toast.LENGTH_SHORT).show();
                }
            });
            builder.setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.cancel());
            builder.show();
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_profile);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                startActivity(new Intent(this, HomeActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_book) {
                startActivity(new Intent(this, ReserveParking.class));
                finish();
                return true;
            } else if (id == R.id.nav_info) {
                startActivity(new Intent(this, AboutUsActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_profile) {
                return true;
            }

            return false;
        });
        userName.setText(getCurrentUserName());
    }

    private void saveLanguageAndRestart(String langCode) {
        SharedPreferences preferences = getSharedPreferences("settings", MODE_PRIVATE);
        preferences.edit().putString("app_lang", langCode).apply();

        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    private String getCurrentUserName() {
        // Implement your logic to get current user ID
        // This could be from FirebaseAuth or your shared preferences
        //return "user123"; // Example - replace with actual implementation
        SharedPreferences preferences = getSharedPreferences("user_session", MODE_PRIVATE);
        String userName = preferences.getString("name", null);

        if (userName == null) {
            // User not logged in or session expired
            // Handle accordingly, maybe redirect to login
        }

        return userName;
    }
}