package com.example.rohit.geofencefundo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class LocationAlertBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Enqueues a JobIntentService passing the context and intent as parameters
        GeofenceTransitionsJobIntentService.enqueueWork(context, intent);
    }
}
