package com.giveblood.bloodhub.others;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import static android.content.Context.LOCATION_SERVICE;

public class CheckConnectivity extends BroadcastReceiver{
    public LocationManager locationManager;
    @Override
    public void onReceive(Context context, Intent arg1) {
        locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                //wifi enabled
                if(locationTrackerEnabled()){
                    if(!isServiceRunning(context)){
                        context.startService(new Intent(context, UpdateLocationService.class));
                    }
                }
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                // Mobile net enabled
                if(locationTrackerEnabled()){
                    if(!isServiceRunning(context)){
                        context.startService(new Intent(context, UpdateLocationService.class));
                    }
                }
            }
        } else {
            // If Location Tracker is running then stop it
            if(isServiceRunning(context)){
                context.stopService(new Intent(context, UpdateLocationService.class));
            }
        }

    }
    private boolean isServiceRunning(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
            if(UpdateLocationService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    public Boolean locationTrackerEnabled() {
        return !(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER));
    }
}