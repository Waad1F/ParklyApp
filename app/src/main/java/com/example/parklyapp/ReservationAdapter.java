package com.example.parklyapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class ReservationAdapter extends ArrayAdapter<Reservation> {

    private Context context;
    private List<Reservation> reservations;
    private DatabaseReference parkingRef = FirebaseDatabase.getInstance().getReference("parkingSlots");

    public ReservationAdapter(Context context, List<Reservation> reservations) {
        super(context, 0, reservations);
        this.context = context;
        this.reservations = reservations;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Reservation reservation = reservations.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.reservation_item, parent, false);
        }

        TextView tvSlotInfo = convertView.findViewById(R.id.tvSlotInfo);
        TextView tvTime = convertView.findViewById(R.id.tvTime);
        Button btnConfirm = convertView.findViewById(R.id.btnConfirm);
        Button btnCancel = convertView.findViewById(R.id.btnCancel);

        tvSlotInfo.setText("Slot: " + reservation.getSlotId());
        tvTime.setText("Time: " + reservation.getReservedTime());

        btnConfirm.setOnClickListener(v -> {
            parkingRef.child(reservation.getSlotId()).child("reservedStatus").setValue(true);
            Toast.makeText(context, "Reservation confirmed", Toast.LENGTH_SHORT).show();
        });

        btnCancel.setOnClickListener(v -> {
            parkingRef.child(reservation.getSlotId()).removeValue();
            reservations.remove(position);
            notifyDataSetChanged();
            Toast.makeText(context, "Reservation cancelled", Toast.LENGTH_SHORT).show();
        });

        return convertView;
    }
}