package com.dexter.ticket_omate.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dexter.ticket_omate.R;
import com.dexter.ticket_omate.models.Passenger;

import java.util.ArrayList;

public class PassengersAdapter extends RecyclerView.Adapter<PassengersAdapter.PassengerHolder> {
    private ArrayList<Passenger> mPassengers;
    private Context mContext;
    public PassengersAdapter(ArrayList<Passenger> passengers,Context context) {
        mPassengers = passengers;
        mContext= context;
    }

    @NonNull
    @Override
    public PassengerHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        return new PassengerHolder(LayoutInflater.from(mContext).inflate(R.layout.passenger_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull PassengerHolder passengerHolder, int i) {
        Passenger passenger= mPassengers.get(i);
        passengerHolder.userName.setText(passenger.getName());
        passengerHolder.startStation.setText(passenger.getStartStationId());
        passengerHolder.endStation.setText(passenger.getEndStationId());

    }

    @Override
    public int getItemCount() {
        return mPassengers.size();
    }

    protected class PassengerHolder extends RecyclerView.ViewHolder{
        TextView userName;
        TextView startStation;
        TextView endStation;
        public PassengerHolder(@NonNull View itemView) {
            super(itemView);
            userName=itemView.findViewById(R.id.user_name);
            startStation = itemView.findViewById(R.id.start_station);
            endStation = itemView.findViewById(R.id.end_station);

        }
    }
}
