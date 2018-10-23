package com.example.rohit.geofencefundo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkStatus;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final int LOC_PERM_REQ_CODE = 1;
    //meters
    private static final int GEOFENCE_RADIUS = 150;
    //in milli seconds
    private static final int GEOFENCE_EXPIRATION = 600000;
    LocationManager locationManager;
    android.location.LocationListener locationListener;
    LatLng userLocation;
    Data data;
    private GoogleMap mMap;
    private GeofencingClient geofencingClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);






       /* geofencingClient = LocationServices.getGeofencingClient(this);
        showCurrentLocationOnMap();*/


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.g_map);
        mapFragment.getMapAsync(this);

        geofencingClient = LocationServices.getGeofencingClient(this);

        //addresses = geocoder.getFromLocation(lat, lng, 1);
        data = new Data.Builder()
                .putString(BackgroundWork.EXTRA_TITLE, "Message from Activity!")
                .putString(BackgroundWork.EXTRA_TEXT, "Hi! I have come from activity.")
                .build();


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setMinZoomPreference(15);

        showCurrentLocationOnMap();
        //locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        PeriodicWorkRequest.Builder photoCheckBuilder =
                new PeriodicWorkRequest.Builder(BackgroundWork.class, 15,
                        TimeUnit.MINUTES)
                        .addTag("notifWorker");
// ...if you want, you can apply constraints to the builder here...

// Create the actual work object:
        PeriodicWorkRequest photoCheckWork = photoCheckBuilder.setInputData(data).build();
// Then enqueue the recurring task:
        WorkManager.getInstance().enqueue(photoCheckWork);

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                addLocationAlert(latLng.latitude, latLng.longitude);
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void showCurrentLocationOnMap() {
        if (isLocationAccessPermitted()) {
            requestLocationAccessPermission();
        } else if (mMap != null) {
            mMap.setMyLocationEnabled(true);
        }
    }
    /*public void addFence(View view){

        addLocationAlert(28.360596,75.5863917); //28.360596 75.5863917  budh bhavan
        addLocationAlert( 28.3603219,75.5868534); //ram marg

    }*/






    /*@SuppressLint("MissingPermission")
    private void showCurrentLocationOnMap() {
        if (isLocationAccessPermitted()) {
            requestLocationAccessPermission();
        }
    }*/

    private boolean isLocationAccessPermitted() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestLocationAccessPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                LOC_PERM_REQ_CODE);
    }

    @SuppressLint("MissingPermission")
    private void addLocationAlert(double lat, double lng) {
        if (isLocationAccessPermitted()) {
            requestLocationAccessPermission();
        } else {
            String key = "" + lat + "-" + lng;
            Geofence geofence = getGeofence(lat, lng, key);
           /* geofencingClient.addGeofences(getGeofencingRequest(geofence),
                    getGeofencePendingIntent())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(MainActivity.this,
                                        "Location alter has been added",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MainActivity.this,
                                        "Location alter could not be added",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });*/


            geofencingClient.addGeofences(getGeofencingRequest(geofence),
                    getGeofencePendingIntent())
                    .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(MainActivity.this, "added", Toast.LENGTH_SHORT).show();

                        }
                    })
                    .addOnFailureListener(this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                            e.printStackTrace();

                        }
                    });


        }
    }

    private void removeLocationAlert() {
        if (isLocationAccessPermitted()) {
            requestLocationAccessPermission();
        } else {

            geofencingClient.removeGeofences(getGeofencePendingIntent())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(MainActivity.this,
                                        "Location alters have been removed",
                                        Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(MainActivity.this,
                                        "Location alters could not be removed",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOC_PERM_REQ_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showCurrentLocationOnMap();
                    Toast.makeText(MainActivity.this,
                            "Location access permission granted, you try " +
                                    "add or remove location allerts",
                            Toast.LENGTH_SHORT).show();

                }
                return;
            }

        }
    }


    private PendingIntent getGeofencePendingIntent() {


        Intent intent = new Intent(this, LocationAlertBroadcastReceiver.class);
        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

    }

    private GeofencingRequest getGeofencingRequest(Geofence geofence) {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();

        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_DWELL);
        builder.addGeofence(geofence);
        return builder.build();
    }

    private Geofence getGeofence(double lat, double lang, String key) {
        return new Geofence.Builder()
                .setRequestId(key)
                .setCircularRegion(lat, lang, GEOFENCE_RADIUS)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_DWELL)
                .setLoiteringDelay(10000)
                .build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.remove_loc_alert:
                removeLocationAlert();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }


}