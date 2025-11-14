package com.example.parklyapp;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends BaseActivity {

    WebView mapWebView;
    EditText searchBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_home);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
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
                startActivity(new Intent(this, AccountActivity.class));
                finish();
                return true;
            }

            return false;
        });

        mapWebView = findViewById(R.id.mapWebView);
        searchBox = findViewById(R.id.searchBox);

        searchBox.setHint(getString(R.string.search_here));

        WebSettings webSettings = mapWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        mapWebView.setWebViewClient(new WebViewClient());

        String noResultMessage = getString(R.string.map_no_result);

        String mapHtml = "<html><head><meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "<link rel='stylesheet' href='https://unpkg.com/leaflet/dist/leaflet.css'/>" +
                "<style>html, body, #map { height: 100%; margin: 0; padding: 0; }</style></head>" +
                "<body><div id='map'></div>" +
                "<script src='https://unpkg.com/leaflet/dist/leaflet.js'></script>" +
                "<script>" +
                "var map = L.map('map').setView([24.8570, 46.7169], 19);" +
                "L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {" +
                "   attribution: 'Map data © OpenStreetMap contributors'" +
                "}).addTo(map);" +
                "L.marker([24.8570, 46.7169]).addTo(map).bindPopup('A4 - Princess Nourah University').openPopup();" +
                "</script></body></html>";

        mapWebView.loadDataWithBaseURL(null, mapHtml, "text/html", "UTF-8", null);

        searchBox.setOnEditorActionListener((v, actionId, event) -> {
            String location = v.getText().toString().trim();
            if (!location.isEmpty()) {
                String bounded = "&viewbox=46.7161,24.8577,46.7177,24.8563&bounded=1";

                String js = "javascript:" +
                        "fetch('https://nominatim.openstreetmap.org/search?format=json&q=" + location + bounded + "')" +
                        ".then(response => response.json())" +
                        ".then(data => {" +
                        " if (data && data.length > 0) {" +
                        "   var lat = data[0].lat;" +
                        "   var lon = data[0].lon;" +
                        "   map.setView([lat, lon], 20);" +
                        "   L.marker([lat, lon]).addTo(map).bindPopup('" + location + "').openPopup();" +
                        " } else { alert('" + noResultMessage + "'); }" +
                        "})";
                mapWebView.evaluateJavascript(js, null);
            }
            return true;
        });
    }
}
