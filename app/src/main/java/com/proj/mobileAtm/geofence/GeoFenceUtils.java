package com.proj.mobileAtm.geofence;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.proj.mobileAtm.R;
import com.proj.mobileAtm.common.Constants;

import java.util.ArrayList;
import java.util.List;


public class GeoFenceUtils {

    public static void initGeoFencing(final Activity activity) {

        String latitude = Constants.ATM_LATITUDE;
        String longitude = Constants.ATM_LONGITUDE;
        int radius = Constants.ATM_GEO_RADIUS;

        double latitudeVal = Double.parseDouble(latitude);
        double longitudeVal = Double.parseDouble(longitude);

        List<Geofence> geoFenceList = new ArrayList<>();
        geoFenceList.add(new Geofence.Builder()


                .setRequestId(activity.getString(R.string.GEOFENCINGID))

                .setCircularRegion(
                        latitudeVal,
                        longitudeVal,
                        radius
                )
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT)
                .build());

        final GeofencingRequest geofencingRequest = getGeoFencingRequest(geoFenceList);

        final GeofencingClient geofencingClient = new GeofencingClient(activity.getApplicationContext());
        initGeoFence(geofencingClient, geofencingRequest, activity);

    }

    private static void initGeoFence(GeofencingClient geofencingClient, GeofencingRequest geofencingRequest, Activity activity) {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        geofencingClient.addGeofences(geofencingRequest, getGeoFencePendingIntent(activity))
                .addOnSuccessListener(activity, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("GEOFENCE", "GeoFencing Success");
                    }
                })
                .addOnFailureListener(activity, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("GEOFENCE", "GeoFencing failure");
                    }
                });
    }


    private static PendingIntent getGeoFencePendingIntent(Activity activity) {

        Intent intent = new Intent(activity.getApplicationContext(), GeoFencingBroadcastReceiver.class);

        return PendingIntent.getBroadcast(activity.getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static GeofencingRequest getGeoFencingRequest(List<Geofence> geofenceList) {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_EXIT | GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(geofenceList);
        return builder.build();
    }

}

