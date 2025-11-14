package com.example.parklyapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Locale;

public class AboutUsActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        SharedPreferences preferences = getSharedPreferences("settings", MODE_PRIVATE);
        String lang = preferences.getString("app_lang", "en");

        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_info);

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
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, AccountActivity.class));
                finish();
                return true;
            }

            return false;
        });
    }
}