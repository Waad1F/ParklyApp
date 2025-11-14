package com.example.parklyapp;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class UpdateParking extends AppCompatActivity {

    private ListView listView;
    private ArrayList<Reservation> reservationList = new ArrayList<>();
    private ReservationAdapter adapter;
    private DatabaseReference parkingRef;
    private String userId;
    /////////////////////////////////
    private HashMap<ImageView, Boolean> parkingSlots = new HashMap<>();
    private ImageView selectedSlot = null;
    private RadioButton availableRadio, unavailableRadio;
    private MaterialButton updateButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_parking);

        //    availableRadio = findViewById(R.id.radio_available);
        //   unavailableRadio = findViewById(R.id.radio_unavailable);
        //updateButton = findViewById(R.id.updateButton);

        //    if (availableRadio == null  unavailableRadio == null  updateButton == null) {
        //       Toast.makeText(this, "خطأ: لم يتم العثور على بعض المكونات في XML", Toast.LENGTH_LONG).show();
        //     return;
        //   }

        setupParkingSlots();

        //   updateButton.setOnClickListener(view -> updateParkingStatus());

        listView = findViewById(R.id.reservationListView);
        parkingRef = FirebaseDatabase.getInstance().getReference("parkingSlots");
        userId = getSharedPreferences("user_session", MODE_PRIVATE).getString("user_key", null);

        adapter = new ReservationAdapter(this, reservationList);
        listView.setAdapter(adapter);

        loadUserReservations();
        loadParkingSlotsFromFirebase();
    }

    private void loadUserReservations() {
        parkingRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                reservationList.clear();
                for (DataSnapshot slotSnapshot : snapshot.getChildren()) {
                    String reservedBy = slotSnapshot.child("reservedBy").getValue(String.class);

                    String slotId = slotSnapshot.getKey();
                    String reservedTime = slotSnapshot.child("reservedTime").getValue(String.class);
                    Boolean reservedStatus = slotSnapshot.child("reservedStatus").getValue(Boolean.class);

                    reservationList.add(new Reservation(slotId, reservedBy, reservedTime, reservedStatus != null && reservedStatus));


                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UpdateParking.this, "Error loading reservations", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void setupParkingSlots() {
        int[] slotIds = {
                R.id.slot1, R.id.slot2, R.id.slot3, R.id.slot4, R.id.slot5, R.id.slot6,
                R.id.slot7, R.id.slot8, R.id.slot9, R.id.slot10, R.id.slot11, R.id.slot12,
                R.id.slot13, R.id.slot14, R.id.slot15, R.id.slot16

        };for (int id : slotIds) {
            ImageView slot = findViewById(id);
            if (slot != null) {
                parkingSlots.put(slot, true);
                slot.setOnClickListener(this::onSlotClick);
            }
        }
    }

    private void onSlotClick(View view) {
        if (!(view instanceof ImageView)) return;

        if (selectedSlot != null) {
            removeColorFilter(selectedSlot);
        }

        selectedSlot = (ImageView) view;

        applyColorFilter(selectedSlot);

        availableRadio.setChecked(false);
        unavailableRadio.setChecked(false);
    }

    private void applyColorFilter(ImageView slot) {
        slot.setColorFilter(Color.LTGRAY, PorterDuff.Mode.SRC_ATOP);
    }

    private void removeColorFilter(ImageView slot) {
        slot.clearColorFilter();
    }

    private void updateParkingStatus() {
        if (selectedSlot != null) {
            boolean isAvailable = availableRadio.isChecked();
            parkingSlots.put(selectedSlot, isAvailable);

            if (isAvailable) {
                selectedSlot.setImageResource(R.drawable.ic_car_red);
            } else {
                selectedSlot.setImageResource(R.drawable.ic_car_green);
            }

            removeColorFilter(selectedSlot);

            Toast.makeText(this, "تم تحديث حالة الموقف", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "يرجى تحديد موقف أولًا", Toast.LENGTH_SHORT).show();
        }
    }

    private ImageView findSlotViewById(String slotId) {
        int resId = getResources().getIdentifier(slotId, "id", getPackageName());
        return findViewById(resId);
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
                Toast.makeText(UpdateParking.this, "Failed to load parking data", Toast.LENGTH_SHORT).show();
            }
        });
    }

}