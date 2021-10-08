package com.proj.mobileAtm.geofence;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.proj.mobileAtm.common.Constants;
import com.proj.mobileAtm.common.SharedPreferenceUtils;

import javax.inject.Inject;

import dagger.android.DaggerBroadcastReceiver;

public class GeoFencingBroadcastReceiver extends DaggerBroadcastReceiver {

    @Inject
    public SharedPreferences sharedPreferences;

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            return;
        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {

            setGeoFencingTransitionDetail(context, geofenceTransition);

        } else {
            Log.e("GEOFENCE", "Invalid Transition type");
        }
    }

    private void setGeoFencingTransitionDetail(Context context, int geoFenceTransition) {
        SharedPreferenceUtils.putBoolean(sharedPreferences,Constants.PREF_GEOFENCING_ENABLED,true);
        switch (geoFenceTransition) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                Log.e("GEOFENCE", "IN");
                SharedPreferenceUtils.putBoolean(sharedPreferences,Constants.PREF_USER_LOCATION,true);
                sendLocalBroadcastThatDeviceHasEntered(context,true);
                break;
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                Log.e("GEOFENCE", "OUT");
                SharedPreferenceUtils.putBoolean(sharedPreferences,Constants.PREF_USER_LOCATION,false);
                sendLocalBroadcastThatDeviceHasEntered(context,false);
                break;
            default:
                break;
        }
    }

    private void sendLocalBroadcastThatDeviceHasEntered(Context context, boolean enabled) {
        Intent intent = new Intent(Constants.ACTION_GEOFENCING);
        intent.putExtra("flag", enabled);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

}
