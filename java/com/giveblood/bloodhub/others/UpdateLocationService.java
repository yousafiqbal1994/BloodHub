package com.giveblood.bloodhub.others;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UpdateLocationService extends Service implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener {
    // LogCat tag
    private static final String TAG = MainActivity.class.getSimpleName();
    private Location mLastLocation;
    private String newlatitude,newlongitude;
    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    // Location updates intervals in sec
    private static int UPDATE_INTERVAL = 10000; // 10 sec
    private static int FATEST_INTERVAL = 5000; // 5 sec
    private static int DISPLACEMENT = 0; // 0 meters

    @Override
    public void onCreate() {
        super.onCreate();
        buildGoogleApiClient();
        createLocationRequest();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    /**
     * Method to display the location on UI
     * */
    private void displayLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        } else {
            mLastLocation = LocationServices.FusedLocationApi
                    .getLastLocation(mGoogleApiClient);
        }
        if (mLastLocation != null) {
            double latitude = mLastLocation.getLatitude();
            double longitude = mLastLocation.getLongitude();
            if(latitude!=0.0 && longitude !=0.0){
                Location loc1 = new Location(""); // Location fetched by GPS now
                loc1.setLatitude(latitude);
                loc1.setLongitude(longitude);
                Location loc2 = new Location(""); // Location locally
                SharedPreferences userProfile = getApplicationContext().getSharedPreferences("ProfileDetails", Context.MODE_PRIVATE);
                String userID = userProfile.getString("id","");
                String bloodgroup = userProfile.getString("bloodgroup","");
                loc2.setLatitude(Double.parseDouble(userProfile.getString("latitude","")));
                loc2.setLongitude(Double.parseDouble(userProfile.getString("longitude","")));
                double Dist = loc1.distanceTo(loc2)/1000;
                if(Dist>5){
                    updateLocationinDatabase(userID,latitude+"",longitude+"",bloodgroup);
                }
            }
        }
    }
    private void updateLocationinDatabase(String userID,String newLatitude, String newLongitude,String bloodgroup) {
        newlatitude = newLatitude;
        newlongitude = newLongitude;
        OkHttpClient clientx = new OkHttpClient.Builder()
                .connectTimeout(50, TimeUnit.SECONDS)
                .writeTimeout(50, TimeUnit.SECONDS)
                .readTimeout(50, TimeUnit.SECONDS)
                .build();
        FormBody.Builder formBuilder = new FormBody.Builder()
                .add("id",userID)
                .add("lat", newLatitude)
                .add("longi", newLongitude)
                .add("bloodgroup",bloodgroup);
        RequestBody formBodyx = formBuilder.build();
        Request requestx = new Request.Builder()
                .url("http://faceblood.website/bhub/UpdateUser.php")
                .post(formBodyx)
                .build();
        clientx.newCall(requestx).enqueue(new Callback() {
            @Override
            public void onFailure(final Call call, IOException e) {
            }
            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                // Update the lat, long locally in preference
                Log.e("locationupdates", "Response from server received");
                updateLocationLocally();
            }
        });
    }

    private void updateLocationLocally() {
        SharedPreferences userProfile = UpdateLocationService.this.getSharedPreferences("ProfileDetails", MODE_APPEND);
        SharedPreferences.Editor profilerEditor = userProfile.edit();
        profilerEditor.putString("latitude",newlatitude);
        profilerEditor.putString("longitude",newlongitude);
        profilerEditor.commit();
    }
    /**
     * Creating google api client object
     * */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }
    /**
     * Creating location request object
     * */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    /**
     * Google api callback methods
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
//        ShowToast("Failed to connect to API");
    }
    @Override
    public void onConnected(Bundle arg0) {
        // Once connected with google api, get the location
        displayLocation();
    }
    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }
    @Override
    public void onLocationChanged(Location location) {
        // Assign the new location
        mLastLocation = location;
        displayLocation();
    }

    public void ShowToast(final String sText) {
        final Context MyContext = this;
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override public void run() {
                Toast toast = Toast.makeText(MyContext, sText, Toast.LENGTH_LONG);
                toast.show();
            }
        });
    };
}