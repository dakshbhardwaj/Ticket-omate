package com.dexter.ticket_omate.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.RemoteException;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.dexter.ticket_omate.R;
import com.dexter.ticket_omate.adapters.PassengersAdapter;
import com.dexter.ticket_omate.interfaces.VolleyCallback;
import com.dexter.ticket_omate.models.Passenger;
import com.dexter.ticket_omate.utility.NetUtil;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;


public class MainActivity extends AppCompatActivity  implements BeaconConsumer {

    private static final String TAG = MainActivity.class.getSimpleName();

    private BeaconManager beaconManager;
    private PassengersAdapter mPassengersAdapter;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private RecyclerView mPassengerRV;
    private ArrayList<Passenger> mPassengers;
    private String mCurrBusStop="";

    private TextView mCurrentStop;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // getSupportActionBar().hide(); //<< this
        setContentView(R.layout.activity_main);

        mCurrentStop = findViewById(R.id.curr_Station);

        mPassengers=new ArrayList<>();
        mPassengerRV=findViewById(R.id.passenger_recycler_view);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(MainActivity.this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mPassengerRV.setLayoutManager(mLayoutManager);

        mPassengersAdapter= new PassengersAdapter(mPassengers,MainActivity.this);
        mPassengerRV.setAdapter(mPassengersAdapter);


        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);

        beaconManager = BeaconManager.getInstanceForApplication(this);

        verifyBluetooth();
        logToDisplay("Application just launched");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android M Permission check
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("This app needs location access");
                builder.setMessage("Please grant location access so this app can detect beacons in the background.");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                    @TargetApi(23)
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                PERMISSION_REQUEST_COARSE_LOCATION);
                    }

                });
                builder.show();
            }
        }

        beaconManager.bind(this);

    }

    private void verifyBluetooth() {

        try {
            if (!BeaconManager.getInstanceForApplication(this).checkAvailability()) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Bluetooth not enabled");
                builder.setMessage("Please enable bluetooth inwhat is android R settings and restart this application.");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        finish();
                        System.exit(0);
                    }
                });
                builder.show();
            }
        }
        catch (RuntimeException e) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Bluetooth LE not available");
            builder.setMessage("Sorry, this device does not support Bluetooth LE.");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                @Override
                public void onDismiss(DialogInterface dialog) {
                    finish();
                    System.exit(0);
                }

            });
            builder.show();

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "coarse location permission granted");
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }

                    });
                    builder.show();
                }
                return;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (beaconManager.isBound(this)) beaconManager.setBackgroundMode(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (beaconManager.isBound(this)) beaconManager.setBackgroundMode(false);
    }

    TreeMap<String, Date> map = new TreeMap();
    TreeMap<String, Date> confirmMap = new TreeMap();
    TreeSet<String> addedData = new TreeSet();

    final int REMOVALLIMIT = 10*1000;
    final int ADDLIMIT = 10*1000;
    final String REMOTE_URL = "http://f0ff8182.ngrok.io";

    @Override
    public void onBeaconServiceConnect() {

        beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                Log.d("BeaconInfo","in range "+beacons.size());
                if (beacons.size() > 0) {
                    //EditText editText = (EditText)RangingActivity.this.findViewById(R.id.rangingText);
                    for (Beacon beacon : beacons) {
                        Log.d("BeaconInfo","Distance = " +beacon.getDistance());
                        String uuid = beacon.getId1().toString();
                        String type = beacon.getId2().toString();

                        if (type.equals("0"))
                        {
                            //user came
                            if (!confirmMap.containsKey(uuid))
                            {
                                confirmMap.put(uuid, new Date());
                                addPassenger(uuid);

                                logToDisplay("PASSENGER ENTRY ID: " + uuid);
                            }

                            map.put(uuid, new Date());
                        }
                        else
                        {
                            //bus arrived at busstop

                            if (!uuid.equals(STOPID))
                            {
                                STOPID = uuid;
                                setCurrentBusStop(uuid);

                                logToDisplay("BUS STOP ID: " + STOPID);
                            }

                        }
                    }
                }

                ArrayList<String> removeList = new ArrayList<String>();

                Date curr = new Date();

                for(Map.Entry cd: map.entrySet())
                {
                    if ((curr.getTime() - ((Date)cd.getValue()).getTime()) >= REMOVALLIMIT)
                    {
                        removeList.add(cd.getKey().toString());
                    }
                }

                for (String key: removeList)
                {
                    if (addedData.contains(key))
                    {
                        //create a end request
                        updateEnd(key,STOPID);
                        addedData.remove(key);

                        logToDisplay("PASSENGER EXIT ID: " + key + " " + STOPID);
                        confirmMap.remove(key);
                        map.remove(key);
                        removePassenger(key);
                    }
                }

                //all active values
                for(Map.Entry cd: confirmMap.entrySet())
                {
                    String key = cd.getKey().toString();
                    if (!addedData.contains(key) && ((curr.getTime() - ((Date)cd.getValue()).getTime()) >= ADDLIMIT))
                    {
                        updateStart(key, STOPID);
                        addedData.add(key);
                        confirmPassenger(key);
                        logToDisplay("PASSENGER CONFIRMED ID: " + key);
                    }
                }
            }
        });

        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRegion", null, null, null));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    String STOPID = "2";
    final String BUSID = "1";
    private void addPassenger(String uuid){
        mPassengers.add(new Passenger(uuid,uuid,mCurrBusStop,"",NetUtil.USER_ENTERED));
        setData();
    }
    private void setData(){
        mPassengersAdapter.setData(mPassengers);

    }
    private void setCurrentBusStop(String busStop){
        mCurrBusStop = busStop;
        mCurrentStop.setText(mCurrBusStop);
    }
    private void removePassenger(String uuid){
        for(int i=0;i<mPassengers.size();i++){
            Log.d("PassengerRemove",mPassengers.get(i).getBeaconId() + ", " +uuid);
            if(mPassengers.get(i).getBeaconId().equals(uuid)){
                Log.d("PassengerRemove","removed");
                mPassengers.remove(i);
                break;
            }
        }
        setData();
    }
    private void confirmPassenger(String uuid){
        for(int i=0;i<mPassengers.size();i++){
            if(mPassengers.get(i).getBeaconId().equals(uuid)){
                mPassengers.get(i).setStatus(NetUtil.USER_CONFIRMED);
                break;
            }
        }
        setData();
    }
    private void updateStart(final String beaconID, final String stopID)
    {
      /*  new Thread(){
            public void run(){
                OkHttpClient client = new OkHttpClient();

                RequestBody formBody = new FormBody.Builder()
                        .add("busID", BUSID)
                        .add("beaconID", beaconID)
                        .add("startingPoint", stopID)
                        .build();
                Request request = new Request.Builder()
                        .url(REMOTE_URL + "/startingLocation")
                        .post(formBody)
                        .build();

                try {
                    Response response = client.newCall(request).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();*/
    }

    private void updateEnd(final String beaconID, final String stopID)
    {
        /*new Thread(){
            public void run() {
                OkHttpClient client = new OkHttpClient();

                RequestBody formBody = new FormBody.Builder()
                        .add("busID", BUSID)
                        .add("beaconID", beaconID)
                        .add("endPoint", stopID)
                        .build();
                Request request = new Request.Builder()
                        .url(REMOTE_URL + "/endingLocation")
                        .post(formBody)
                        .build();

                try {
                    Response response = client.newCall(request).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();*/
    }

    ArrayAdapter<String> adapter;
    ArrayList<String> list = new ArrayList<String>();

    private void logToDisplay(final String line) {
        Log.v(TAG, line);
        runOnUiThread(new Runnable() {
            public void run() {
                EditText editText = (EditText)MainActivity.this.findViewById(R.id.logText);
                editText.append(line + "\n");

                int y = (editText.getLineCount() - 1) * editText.getLineHeight(); // the " - 1" should send it to the TOP of the last line, instead of the bottom of the last line
                editText.scrollTo(0, y);
            }
        });
    }
}
