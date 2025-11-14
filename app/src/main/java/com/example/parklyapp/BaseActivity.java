package com.example.parklyapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        SharedPreferences preferences = newBase.getSharedPreferences("settings", MODE_PRIVATE);
        String lang = preferences.getString("app_lang", "en");

        Locale locale = new Locale(lang);
        Locale.setDefault(locale);

        Configuration config = newBase.getResources().getConfiguration();
        config.setLocale(locale);
        config.setLayoutDirection(Locale.ENGLISH);

        Context context = newBase.createConfigurationContext(config);
        super.attachBaseContext(context);
    }
}