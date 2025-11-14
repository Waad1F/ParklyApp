package com.example.parklyapp;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;

public class ReserveParking extends BaseActivity {

    private HashMap<ImageView, Boolean> parkingSlots = new HashMap<>();
    private ImageView selectedSlot = null;
    private TextView tvSelectedTime;
    private MaterialButton btnPickTime, updateButton;
    private String selectedTime = null;
    private DatabaseReference parkingRef;
    private String userId;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserve_parking);

        parkingRef = FirebaseDatabase.getInstance().getReference("parkingSlots");
        userId = getCurrentUserId();

        tvSelectedTime = findViewById(R.id.tvSelectedTime);
        btnPickTime = findViewById(R.id.btnPickTime);
        updateButton = findViewById(R.id.updateButton);

        if (tvSelectedTime == null || btnPickTime == null || updateButton == null) {
            Toast.makeText(this, getString(R.string.toast_missing_components), Toast.LENGTH_LONG).show();
            return;
        }

        setupNavigation();
        setupParkingSlots();
        loadParkingSlotsFromFirebase();

        btnPickTime.setOnClickListener(view -> showTimePicker());
        updateButton.setOnClickListener(view -> updateParkingStatus());
    }

    private void setupNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_book);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                startActivity(new Intent(this, HomeActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_book) {
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
    }

    private void setupParkingSlots() {
        int[] slotIds = {
                R.id.slot1, R.id.slot2, R.id.slot3, R.id.slot4, R.id.slot5, R.id.slot6,
                R.id.slot7, R.id.slot8, R.id.slot9, R.id.slot10, R.id.slot11, R.id.slot12,
                R.id.slot13, R.id.slot14, R.id.slot15, R.id.slot16
        };

        for (int id : slotIds) {
            ImageView slot = findViewById(id);
            if (slot != null) {
                parkingSlots.put(slot, true);
                slot.setOnClickListener(this::onSlotClick);
                slot.setImageResource(R.drawable.ic_car);
            }
        }
    }

    private void loadParkingSlotsFromFirebase() {
        parkingRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot slotSnapshot : dataSnapshot.getChildren()) {
                    String slotId = slotSnapshot.getKey();
                    Boolean isAvailable = slotSnapshot.child("available").getValue(Boolean.class);
                    String reservedBy = slotSnapshot.child("reservedBy").getValue(String.class);
                    String reservedTime = slotSnapshot.child("reservedTime").getValue(String.class);
                    Boolean reservedStatus = slotSnapshot.child("reservedStatus").getValue(Boolean.class);

                    String userId = getCurrentUserId();
                    ImageView slotView = findSlotViewById(slotId);
                    if (slotView != null) {
                        if (isAvailable != null && isAvailable) {
                            slotView.setImageResource(R.drawable.ic_car);
                            parkingSlots.put(slotView, true);
                        } else {
                            if(userId.equals(reservedBy)){
                                if(reservedStatus){

                                    slotView.setImageResource(R.drawable.ic_car_green_true);
                                }
                                else {
                                    slotView.setImageResource(R.drawable.ic_car_green);
                                }
                                parkingSlots.put(slotView, false);
                            }
                            else{
                                if(reservedStatus){
                                    slotView.setImageResource(R.drawable.ic_car_red_true);
                                }
                                else {
                                    slotView.setImageResource(R.drawable.ic_car_red);
                                }
                                parkingSlots.put(slotView, false);
                            }

                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ReserveParking.this, "Failed to load parking data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private ImageView findSlotViewById(String slotId) {
        int resId = getResources().getIdentifier(slotId, "id", getPackageName());
        return findViewById(resId);
    }

    private void onSlotClick(View view) {
        if (!(view instanceof ImageView)) return;

        selectedSlot = (ImageView) view;
        boolean isAvailable = parkingSlots.getOrDefault(selectedSlot, false);

        if (!isAvailable) {
            Toast.makeText(this, "This slot is already reserved", Toast.LENGTH_SHORT).show();
            selectedSlot = null;
            return;
        }

        for (ImageView slot : parkingSlots.keySet()) {
            slot.setAlpha(1.0f);
        }

        selectedSlot.setAlpha(0.5f);
    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (TimePicker view, int selectedHour, int selectedMinute) -> {
                    selectedTime = String.format("%02d:%02d", selectedHour, selectedMinute);
                    tvSelectedTime.setText(getString(R.string.selected_time, selectedTime));
                }, hour, minute, true);
        timePickerDialog.show();
    }

    private void updateParkingStatus() {
        if (selectedSlot == null) {
            Toast.makeText(this, getString(R.string.toast_select_slot), Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedTime == null) {
            Toast.makeText(this, getString(R.string.toast_select_time), Toast.LENGTH_SHORT).show();
            return;
        }

        String slotId = getResources().getResourceEntryName(selectedSlot.getId());

        // Update Firebase
        HashMap<String, Object> updateMap = new HashMap<>();
        updateMap.put("available", false);
        updateMap.put("reservedBy", userId);
        updateMap.put("reservedTime", selectedTime);
        updateMap.put("reservedStatus", false);

        parkingRef.child(slotId).updateChildren(updateMap)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        selectedSlot.setImageResource(R.drawable.ic_car_green);
                        Toast.makeText(this, "Parking slot reserved successfully!", Toast.LENGTH_SHORT).show();

                        // Reset selection
                        selectedSlot.setAlpha(1.0f);
                        selectedSlot = null;
                        selectedTime = null;
                        tvSelectedTime.setText("");
                    } else {
                        Toast.makeText(this, "Failed to reserve slot", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String getCurrentUserId() {
        // Implement your logic to get current user ID
        // This could be from FirebaseAuth or your shared preferences
        //return "user123"; // Example - replace with actual implementation
        SharedPreferences preferences = getSharedPreferences("user_session", MODE_PRIVATE);
        String userId = preferences.getString("user_key", null);

        if (userId == null) {
            // User not logged in or session expired
            // Handle accordingly, maybe redirect to login
        }

        return userId;
    }
}