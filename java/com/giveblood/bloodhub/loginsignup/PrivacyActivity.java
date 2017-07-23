package com.giveblood.bloodhub.loginsignup;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import com.giveblood.bloodhub.R;
import com.jaredrummler.materialspinner.MaterialSpinner;
import mehdi.sakout.fancybuttons.FancyButton;

/**
 * Created by YouCaf Iqbal on 12/7/2016.
 */

public class PrivacyActivity extends AppCompatActivity{

    public  String bloodgroup,name,password,age,username,mobilenumber,gender,search,internet,mobile;
    public MaterialSpinner myBloodGroupSpinner;
    public MaterialSpinner myGenderSpinner;
    public MaterialSpinner mySeacrchSpinner;
    public MaterialSpinner myNumberSpinner;
    public MaterialSpinner myInternetSpinner;
    FancyButton _nextStep;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //getSupportActionBar().hide(); //<< this

        setContentView(R.layout.privacy);
        Intent intent = getIntent();

        name = intent.getStringExtra("name");
        password = intent.getStringExtra("password");
        age = intent.getStringExtra("age");
        username = intent.getStringExtra("username");
        mobilenumber = intent.getStringExtra("mobilenumber");

        myBloodGroupSpinner = (MaterialSpinner) findViewById(R.id.bgspinner);
        myBloodGroupSpinner.setBackgroundColor(Color.GRAY);
        myBloodGroupSpinner.setTextColor(Color.WHITE);
        myBloodGroupSpinner.setItems("A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-");

        myGenderSpinner = (MaterialSpinner) findViewById(R.id.spinner);
        myGenderSpinner.setBackgroundColor(Color.GRAY);
        myGenderSpinner.setTextColor(Color.WHITE);
        myGenderSpinner.setItems("Male","Female");

        myNumberSpinner = (MaterialSpinner) findViewById(R.id.mobilenumberspinner);
        myNumberSpinner.setBackgroundColor(Color.GRAY);
        myNumberSpinner.setTextColor(Color.WHITE);
        myNumberSpinner.setItems("Everyone","Only Male","Only Female","Nobody");

        mySeacrchSpinner = (MaterialSpinner) findViewById(R.id.searchspinner);
        mySeacrchSpinner.setBackgroundColor(Color.GRAY);
        mySeacrchSpinner.setTextColor(Color.WHITE);
        mySeacrchSpinner.setItems("Everyone","Only Male","Only Female", "Nobody");

        myInternetSpinner =  (MaterialSpinner) findViewById(R.id.internetspinner);
        myInternetSpinner.setBackgroundColor(Color.GRAY);
        myInternetSpinner.setTextColor(Color.WHITE);
        myInternetSpinner.setItems("Everyone","Only Male","Only Female","Nobody");

        _nextStep = (FancyButton) findViewById(R.id.next);
        _nextStep.setFontIconSize(20);
        _nextStep.setTextSize(20);
        _nextStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PrivacyActivity.this,SaveLocation.class);
                gender = myGenderSpinner.getText().toString();
                search = mySeacrchSpinner.getText().toString();
                mobile = myNumberSpinner.getText().toString();
                internet = myInternetSpinner.getText().toString();
                bloodgroup = myBloodGroupSpinner.getText().toString();
                intent.putExtra("bloodgroup",bloodgroup);
                intent.putExtra("name",name);
                intent.putExtra("password",password);
                intent.putExtra("age",age);
                intent.putExtra("username",username);
                intent.putExtra("mobilenumber",mobilenumber);
                intent.putExtra("gender",gender);
                intent.putExtra("search",search);
                intent.putExtra("mobile",mobile);
                intent.putExtra("internet",internet);
                startActivity(intent);
            }
        });

    }

}
