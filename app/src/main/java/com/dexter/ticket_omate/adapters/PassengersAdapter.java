package com.dexter.ticket_omate.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.dexter.ticket_omate.models.Passenger;

import java.util.ArrayList;

public class PassengersAdapter extends RecyclerView.Adapter<PassengersAdapter.PassengerHolder> {
    ArrayList<Passenger> mPassengers;

    public PassengersAdapter(ArrayList<Passenger> passengers) {
        mPassengers = passengers;
    }

    @NonNull
    @Override
    public PassengerHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull PassengerHolder passengerHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    protected class PassengerHolder extends RecyclerView.ViewHolder{

        public PassengerHolder(@NonNull View itemView) {

            super(itemView);

        }
    }
}
