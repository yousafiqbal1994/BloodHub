package com.giveblood.bloodhub.loginsignup;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import com.giveblood.bloodhub.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import mehdi.sakout.fancybuttons.FancyButton;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * Created by YouCaf Iqbal on 12/7/2016.
 */

@SuppressWarnings("deprecation")
public class SaveLocation extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
    public  String bloodgroup,name,password,age,username,mobilenumber,gender,search,internet,mobile;
    public double latitude,longitude;
    public boolean locationGot = false;
    public ProgressDialog pDialog;
    // LogCat tag
    private static final String TAG = SaveLocation.class.getSimpleName();

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private Location mLastLocation;
    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;
    // boolean flag to toggle periodic location updates
    private boolean mRequestingLocationUpdates = false;
    private LocationRequest mLocationRequest;
    // Location updates intervals in sec
    private static int UPDATE_INTERVAL = 2000; // 2 sec
    private static int FATEST_INTERVAL = 1000; // 1 sec
    private static int DISPLACEMENT = 0; // 0 meters
    public LocationManager locationManager;
    // UI elements
    private TextView lblLocation;
    private FancyButton btnShowLocation, btnDone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //getSupportActionBar().hide(); //<< this
        setContentView(R.layout.location);

        Intent intent = getIntent();
        bloodgroup = intent.getStringExtra("bloodgroup");
        name = intent.getStringExtra("name");
        password = intent.getStringExtra("password");
        age = intent.getStringExtra("age");
        username = intent.getStringExtra("username");
        mobilenumber = intent.getStringExtra("mobilenumber");
        gender = intent.getStringExtra("gender");
        search = intent.getStringExtra("search");
        internet = intent.getStringExtra("internet");
        mobile = intent.getStringExtra("mobile");
//        If internet privacy and mobile privacy both set to Only Female then search set to everyone doesnot make sense for males so make search automatically to Only Females
//        as well *
//        Same goes for Only Male
        if(internet.equals("Only Female") && mobile.equals("Only Female")){
            search = "Only Female";
        }
        if(internet.equals("Only Male") && mobile.equals("Only Male")){
            search = "Only Male";
        }
//        Everyone internet and Everyone mobile but search only one gender  It's safe as contact is decided after the search filter.

        lblLocation = (TextView) findViewById(R.id.lblLocation);
        btnShowLocation = (FancyButton) findViewById(R.id.btnShowLocation);
        btnDone = (FancyButton) findViewById(R.id.done);
        locationManager = (LocationManager) SaveLocation.this.getSystemService(LOCATION_SERVICE);
        if(locationTrackerEnabled()){
            btnShowLocation.setVisibility(View.GONE);
            pDialog = new ProgressDialog(SaveLocation.this);
            pDialog.setMessage("Getting your location, Please Wait");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
            lblLocation.setText("Getting your location...");

        }
        // First we need to check availability of play services
        if (checkPlayServices()) {
            // Building the GoogleApi client
            buildGoogleApiClient();
            createLocationRequest();
        }

        // Show location button click listener
        btnShowLocation.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                displayLocation();
            }
        });

        // Toggling the periodic location updates
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(locationGot){
                    ////  send all the details to the database file
                    new AddUserAsync().execute();
                }else{
                    Toast.makeText(getApplicationContext(),
                            "Save location to get registered", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });

    }

    public Boolean locationTrackerEnabled() {
        return !(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER));
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
        // Resuming the periodic location updates
        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
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
            latitude = mLastLocation.getLatitude();
            longitude = mLastLocation.getLongitude();

            if(latitude != 0.0 && longitude != 0.0){
                locationGot = true;
            }else {
                locationGot = false;
            }
            Geocoder geocoder = new Geocoder(SaveLocation.this, Locale.getDefault());
            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(latitude,longitude, 1);
                lblLocation.setText("Your location is " +addresses.get(0).getAddressLine(1)+ ", "+addresses.get(0).getCountryName());
                if(pDialog!=null){
                    pDialog.dismiss();
                }
                btnShowLocation.setVisibility(View.GONE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            lblLocation.setText("Turn on Location Please");
            showDialog();
        }
    }

    private  void showDialog(){
        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(SaveLocation.this);
        // Setting Dialog Title
        alertDialog.setTitle(getBaseContext().getResources().getString(R.string.gpsnhsta));
        // Setting Dialog Message
        alertDialog.setMessage(getBaseContext().getResources().getString(R.string.seteka));
        // On pressing Settings button
        alertDialog.setPositiveButton(getBaseContext().getResources().getString(R.string.kuna), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                SaveLocation.this.startActivity(intent);
            }
        });
        // on pressing cancel button
        alertDialog.setNegativeButton(getBaseContext().getResources().getString(R.string.taty), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        // Showing Alert Message
        alertDialog.show();
    }
    /**
     * Method to toggle periodic location updates
     * */
    private void togglePeriodicLocationUpdates() {
        if (!mRequestingLocationUpdates) {
            // Changing the button text
            btnDone.setText(getString(R.string.btn_stop_location_updates));

            mRequestingLocationUpdates = true;

            // Starting the location updates
            startLocationUpdates();

            Log.d(TAG, "Periodic location updates started!");

        } else {
            // Changing the button text
            btnDone.setText(getString(R.string.btn_start_location_updates));

            mRequestingLocationUpdates = false;

            // Stopping the location updates
            stopLocationUpdates();

            Log.d(TAG, "Periodic location updates stopped!");
        }
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
     * Method to verify google play services on the device
     * */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            return false;
        }
        return true;
    }

    /**
     * Starting the location updates
     * */
    protected void startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }else{
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        }

    }

    /**
     * Stopping location updates
     */
    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    /**
     * Google api callback methods
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
    }

    @Override
    public void onConnected(Bundle arg0) {

        // Once connected with google api, get the location
        displayLocation();

        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        // Assign the new location
        mLastLocation = location;
        Toast.makeText(getApplicationContext(), "Location changed!",
                Toast.LENGTH_SHORT).show();
        displayLocation();
    }

    public class AddUserAsync extends AsyncTask<Void,Void,Void> {
        JSONObject json = null;
        String fromServer = "";
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(SaveLocation.this);
            pDialog.setMessage(getBaseContext().getResources().getString(R.string.jorom)+"...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }
        @Override
        protected Void doInBackground(Void... voids) {
            username= username.trim().toLowerCase();
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(50, TimeUnit.SECONDS)
                    .writeTimeout(50, TimeUnit.SECONDS)
                    .readTimeout(50, TimeUnit.SECONDS)
                    .build();
            FormBody.Builder formBuilder = new FormBody.Builder()
                    .add("Name",name)
                    .add("username",username)
                    .add("password",password)
                    .add("number",mobilenumber)
                    .add("age",age)
                    .add("bloodgroup",bloodgroup)
                    .add("lat",latitude+"")
                    .add("longi",longitude+"")
                    .add("gender",gender)
                    .add("search",search)
                    .add("mobilecontact",mobile)
                    .add("internetcontact",internet);

            RequestBody formBody = formBuilder.build();
            Request request = new Request.Builder()
                    .url("http://faceblood.website/bhub/Adduser.php")
                    .post(formBody)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                String res = response.body().string();
                json = new JSONObject(res);
                fromServer = json.getString("added");
                Log.e("stringtest",json.getString("added"));
            } catch (IOException e) {
                Log.e("stringtest IO",e.toString());
                e.printStackTrace();
            } catch (JSONException e) {
                Log.e("stringtest JSON",e.toString());
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pDialog.dismiss();
            Log.e("fromServer",fromServer);
            if(fromServer.equals("addeduser")){
                Toast.makeText(getBaseContext(), getBaseContext().getResources().getString(R.string.successsignup), Toast.LENGTH_SHORT).show();
                onSignupSuccess();
            }else {
                Toast.makeText(getBaseContext(), getBaseContext().getResources().getString(R.string.pesh), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onSignupSuccess() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("afterSignUp","yes");
        intent.putExtra("username",username);
        intent.putExtra("password",password);
        startActivity(intent);
        finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }
}