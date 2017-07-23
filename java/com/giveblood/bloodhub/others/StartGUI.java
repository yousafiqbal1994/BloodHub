package com.giveblood.bloodhub.others;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.giveblood.bloodhub.R;
import com.giveblood.bloodhub.loginsignup.LoginActivity;
import com.giveblood.bloodhub.loginsignup.SignupActivity;
import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;
import com.vstechlab.easyfonts.EasyFonts;

import butterknife.ButterKnife;
import mehdi.sakout.fancybuttons.FancyButton;

public class StartGUI extends AppCompatActivity {

    FancyButton loginButton;
    FancyButton regButton;
    ShimmerTextView StartText,quoteText;
    public LocationManager locationManager;
    public String name,number,age;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //getSupportActionBar().hide(); //<< this
        SharedPreferences userProfile = StartGUI.this.getSharedPreferences("ProfileDetails", MODE_PRIVATE);
        String UserRegistered = userProfile.getString("registered", "false");
        if (!UserRegistered.equals("false")) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
        setContentView(R.layout.activity_start);
        StartText = (ShimmerTextView) findViewById(R.id.StartText);
        quoteText = (ShimmerTextView) findViewById(R.id.quote);
        StartText.setTypeface(EasyFonts.captureIt(this));
        quoteText.setTypeface(EasyFonts.captureIt(this));
        loginButton = (FancyButton) findViewById(R.id.btnlogin);
        regButton = (FancyButton) findViewById(R.id.btnregister);
        loginButton.setFontIconSize(20);
        regButton.setFontIconSize(20);
        regButton.setTextSize(20);
        loginButton.setTextSize(20);
        Shimmer signUpTextShrimmer = new Shimmer();
        signUpTextShrimmer.start(StartText);
        signUpTextShrimmer.start(quoteText);
        signUpTextShrimmer.setDuration(3000);
        //Solution of Network on Main thread problem
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        ButterKnife.inject(this);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.putExtra("afterSignUp","no");
                startActivity(intent);
                finish();
            }
        });
        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
                    android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(StartGUI.this);
                    // Setting Dialog Title
                    alertDialog.setTitle(getBaseContext().getResources().getString(R.string.gpsnhsta));
                    // Setting Dialog Message
                    alertDialog.setMessage(getBaseContext().getResources().getString(R.string.seteka));
                    // On pressing Settings button
                    alertDialog.setPositiveButton(getBaseContext().getResources().getString(R.string.kuna), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            StartGUI.this.startActivity(intent);
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
                }else {
                    Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                    startActivity(intent);
                }
            }
        });

        locationManager = (LocationManager) getBaseContext().getSystemService(LOCATION_SERVICE);
    }

}