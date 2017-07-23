package com.giveblood.bloodhub.loginsignup;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.giveblood.bloodhub.R;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.sinch.verification.CodeInterceptionException;
import com.sinch.verification.InvalidInputException;
import com.sinch.verification.PhoneNumberUtils;
import com.sinch.verification.ServiceErrorException;
import com.sinch.verification.SinchVerification;
import com.sinch.verification.Verification;
import com.sinch.verification.VerificationListener;

import butterknife.ButterKnife;
import butterknife.InjectView;
import mehdi.sakout.fancybuttons.FancyButton;

/**
 * Created by YouCaf Iqbal on 12/6/2016.
 */

public class GenderAndMobile extends AppCompatActivity {

    public  String name,password,age,username;
    ProgressDialog pDialog;
    FancyButton _nextStep;
    public  boolean mobnoOk =false;
    public String VerifiedMobNo;
    @InjectView(R.id.input_number) TextView _numText;
    @Override
    public void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //getSupportActionBar().hide(); //<< this
        setContentView(R.layout.genderandmobile);
        ButterKnife.inject(this);
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        password = intent.getStringExtra("password");
        age = intent.getStringExtra("age");
        username = intent.getStringExtra("username");
        _nextStep = (FancyButton) findViewById(R.id.verifyMobNo);
        _nextStep.setFontIconSize(20);
        _nextStep.setTextSize(20);
        _nextStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(_numText.getText().length()!=0){
                    mobnoOk = true;
                    pDialog = new ProgressDialog(GenderAndMobile.this);
                    pDialog.setMessage("Sending sms for verification...");
                    pDialog.setIndeterminate(false);
                    pDialog.setCancelable(false);
                    pDialog.show();
                    startVerification(_numText.getText().toString());
                }
                else {
                    Toast.makeText(GenderAndMobile.this,"Enter mobile number and verify",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void startVerification(String phoneNumber) {
        String Appkey = "0a556fc7-d6b4-42ba-8840-b84ecc5e1453";
        com.sinch.verification.Config config = SinchVerification.config().applicationKey(Appkey).context(getApplicationContext()).build();
        VerificationListener listener = new MyVerificationListener();
        String defaultRegion = PhoneNumberUtils.getDefaultCountryIso(GenderAndMobile.this);
        String phoneNumberInE164 = PhoneNumberUtils.formatNumberToE164(phoneNumber, defaultRegion);
        Verification verification = SinchVerification.createSmsVerification(config, phoneNumberInE164, listener);
        verification.initiate();
    }

    // Inner Class for mobile verification
    private class MyVerificationListener implements VerificationListener {
        @Override
        public void onInitiated() {
        }

        @Override
        public void onInitiationFailed(Exception e) {
            pDialog.dismiss();
            if (e instanceof InvalidInputException) {
                Toast.makeText(GenderAndMobile.this,"Incorrect number",Toast.LENGTH_SHORT).show();
                Log.e("wrong","Incorrect number provided");
            } else if (e instanceof ServiceErrorException) {
                Toast.makeText(GenderAndMobile.this,"Ops...error occured...Try again please",Toast.LENGTH_SHORT).show();
                Log.e("wrong","Sinch service error");
            } else {
                Toast.makeText(GenderAndMobile.this,"Ops...error occured...Try again please", Toast.LENGTH_SHORT).show();
                Log.e("wrong","Other system error, check your network state");
            }

        }

        @Override
        public void onVerified() {
            mobnoOk = true;
            VerifiedMobNo = _numText.getText().toString();
            pDialog.dismiss();
            Toast.makeText(GenderAndMobile.this,"Verified Successfully",Toast.LENGTH_SHORT).show();
            Intent privacyIntent = new Intent(GenderAndMobile.this,PrivacyActivity.class);
            privacyIntent.putExtra("name",name);
            privacyIntent.putExtra("password",password);
            privacyIntent.putExtra("age",age);
            privacyIntent.putExtra("username",username);
            privacyIntent.putExtra("mobilenumber",VerifiedMobNo);
            startActivity(privacyIntent);

        }

        @Override
        public void onVerificationFailed(Exception e) {
            mobnoOk = false;
            pDialog.dismiss();
            if (e instanceof CodeInterceptionException) {
                Toast.makeText(GenderAndMobile.this,"Verification failed...Try again",Toast.LENGTH_SHORT).show();
            } else if (e instanceof ServiceErrorException) {
                Toast.makeText(GenderAndMobile.this,"Verification failed...Try again",Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(GenderAndMobile.this,"Verification failed...Try again",Toast.LENGTH_SHORT).show();
            }

        }
    }

}
