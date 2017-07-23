package com.giveblood.bloodhub.userprofile;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.giveblood.bloodhub.R;
import com.giveblood.bloodhub.loginsignup.LoginActivity;
import com.giveblood.bloodhub.others.MainActivity;
import com.google.android.gcm.server.Constants;
import com.jaredrummler.materialspinner.MaterialSpinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import mehdi.sakout.fancybuttons.FancyButton;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by YouCaf Iqbal on 8/9/2016.
 */
public class ProfileFragment extends AppCompatActivity {
    private String currentBloodgroup,currentSearchprivacy,currentMobileprivacy,currentInternetprivacy,currentUserID;
    public MaterialSpinner myBloodGroupSpinner;
    public MaterialSpinner mySeacrchSpinner;
    public MaterialSpinner myNumberSpinner;
    public MaterialSpinner myInternetSpinner;
    FormBody.Builder formBuilder =null;
    FancyButton updateButton;
    public  ProgressDialog pDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_layout);
        myBloodGroupSpinner = (MaterialSpinner) findViewById(R.id.bgspinner);
        myBloodGroupSpinner.setBackgroundColor(Color.RED);
        myBloodGroupSpinner.setTextColor(Color.WHITE);
        myBloodGroupSpinner.setItems("A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-");

        myNumberSpinner = (MaterialSpinner) findViewById(R.id.mobilenumberspinner);
        myNumberSpinner.setBackgroundColor(Color.RED);
        myNumberSpinner.setTextColor(Color.WHITE);
        myNumberSpinner.setItems("Everyone","Only Male","Only Female","Nobody");

        mySeacrchSpinner = (MaterialSpinner) findViewById(R.id.searchspinner);
        mySeacrchSpinner.setBackgroundColor(Color.RED);
        mySeacrchSpinner.setTextColor(Color.WHITE);
        mySeacrchSpinner.setItems("Everyone","Only Male","Only Female", "Nobody");

        myInternetSpinner =  (MaterialSpinner) findViewById(R.id.internetspinner);
        myInternetSpinner.setBackgroundColor(Color.RED);
        myInternetSpinner.setTextColor(Color.WHITE);
        myInternetSpinner.setItems("Everyone","Only Male","Only Female","Nobody");


        setValuesInSpinners();
        updateButton = (FancyButton) findViewById(R.id.updateBtn);
        updateButton.setFontIconSize(20);
        updateButton.setTextSize(20);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(somethingChanged()){
                    new UpdateAsync().execute();
                }else{
                    formBuilder = null;
                    Toast.makeText(getApplicationContext(),"Nothing to update",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean somethingChanged() {
        boolean changed = false;
        formBuilder = new FormBody.Builder();
        if(!(currentBloodgroup.equals(myBloodGroupSpinner.getText().toString()))){
            /// add blood group to updated quantities
            formBuilder.add("newbloodgroup",myBloodGroupSpinner.getText().toString());
            changed=true;
        }
        if(!(currentSearchprivacy.equals(mySeacrchSpinner.getText().toString()))){
            /// add search to updated quantities
            formBuilder.add("searchprivacy",mySeacrchSpinner.getText().toString());
            changed=true;
        }
        if(!(currentMobileprivacy.equals(myNumberSpinner.getText().toString()))){
            /// add mobile to updated quantities
            formBuilder.add("mobilehprivacy",myNumberSpinner.getText().toString());
            changed=true;
        }
        if(!(currentInternetprivacy.equals(myInternetSpinner.getText().toString()))){
            /// add internet to updated quantities
            formBuilder.add("internetprivacy",myInternetSpinner.getText().toString());
            changed=true;
        }
        return changed;
    }

    private void setValuesInSpinners() {
        SharedPreferences userProfile = getApplicationContext().getSharedPreferences("ProfileDetails", Context.MODE_PRIVATE);
        currentUserID = userProfile.getString("id","");
        currentBloodgroup = userProfile.getString("bloodgroup","");
        adjustBloodGroupItems();
        currentSearchprivacy = userProfile.getString("searchprivacy","");
        adjustSearchItems();
        currentMobileprivacy = userProfile.getString("mobileprivacy","");
        adjustMobileItems();
        currentInternetprivacy = userProfile.getString("internetprivacy","");
        adjustInternetItems();
    }

    private void adjustInternetItems() {
        if(currentInternetprivacy.equals("Everyone")){
            myInternetSpinner.setItems("Everyone","Only Male","Only Female","Nobody");
        }
        if(currentInternetprivacy.equals("Only Male")){
            myInternetSpinner.setItems("Only Male","Everyone","Only Female","Nobody");
        }
        if(currentInternetprivacy.equals("Only Female")){
            myInternetSpinner.setItems("Only Female","Only Male","Everyone","Nobody");
        }
        if(currentInternetprivacy.equals("Nobody")){
            myInternetSpinner.setItems("Nobody","Only Male","Only Female","Everyone");
        }
    }

    private void adjustMobileItems() {
        if(currentMobileprivacy.equals("Everyone")){
            myNumberSpinner.setItems("Everyone","Only Male","Only Female","Nobody");
        }
        if(currentMobileprivacy.equals("Only Male")){
            myNumberSpinner.setItems("Only Male","Everyone","Only Female","Nobody");
        }
        if(currentMobileprivacy.equals("Only Female")){
            myNumberSpinner.setItems("Only Female","Only Male","Everyone","Nobody");
        }
        if(currentMobileprivacy.equals("Nobody")){
            myNumberSpinner.setItems("Nobody","Only Male","Only Female","Everyone");
        }
    }

    private void adjustSearchItems() {
        if(currentSearchprivacy.equals("Everyone")){
            mySeacrchSpinner.setItems("Everyone","Only Male","Only Female","Nobody");
        }
        if(currentSearchprivacy.equals("Only Male")){
            mySeacrchSpinner.setItems("Only Male","Everyone","Only Female","Nobody");
        }
        if(currentSearchprivacy.equals("Only Female")){
            mySeacrchSpinner.setItems("Only Female","Only Male","Everyone","Nobody");
        }
        if(currentSearchprivacy.equals("Nobody")){
            mySeacrchSpinner.setItems("Nobody","Only Male","Only Female","Everyone");
        }
    }

    private void adjustBloodGroupItems() {
        if(currentBloodgroup.equals("A+")){
            myBloodGroupSpinner.setItems("A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-");
        }
        if(currentBloodgroup.equals("A-")){
            myBloodGroupSpinner.setItems("A-", "A+", "B+", "B-", "O+", "O-", "AB+", "AB-");
        }
        if(currentBloodgroup.equals("B+")){
            myBloodGroupSpinner.setItems("B+", "A-", "A+", "B-", "O+", "O-", "AB+", "AB-");
        }
        if(currentBloodgroup.equals("B-")){
            myBloodGroupSpinner.setItems("B-", "A-", "B+", "A+", "O+", "O-", "AB+", "AB-");
        }
        if(currentBloodgroup.equals("O+")){
            myBloodGroupSpinner.setItems("O+", "A-", "B+", "B-", "A+", "O-", "AB+", "AB-");
        }
        if(currentBloodgroup.equals("O-")){
            myBloodGroupSpinner.setItems("O-", "A-", "B+", "B-", "O+", "A+", "AB+", "AB-");
        }
        if(currentBloodgroup.equals("AB+")){
            myBloodGroupSpinner.setItems("AB+", "A-", "B+", "B-", "O+", "O-", "A+", "AB-");
        }
        if(currentBloodgroup.equals("AB-")){
            myBloodGroupSpinner.setItems("AB-", "A-", "B+", "B-", "O+", "O-", "AB+", "A+");
        }
    }

    public class UpdateAsync extends AsyncTask<Void, Void, Void> {

        JSONObject json = null;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ProfileFragment.this);
            pDialog.setMessage(ProfileFragment.this.getResources().getString(R.string.updateskana)+"...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            formBuilder.add("currentbloodgroup",currentBloodgroup);
            formBuilder.add("currentUserID",currentUserID);
            RequestBody formBody = formBuilder.build();
            Request request = new Request.Builder()
                    .url("http://faceblood.website/bhub/UpdateProfile.php")
                    .post(formBody)
                    .build();
            try {
                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(50, TimeUnit.SECONDS)
                        .writeTimeout(50, TimeUnit.SECONDS)
                        .readTimeout(50, TimeUnit.SECONDS)
                        .build();
                Response response = client.newCall(request).execute();
                String res = response.body().string();
                json = new JSONObject(res);
                // Do something with the response.
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pDialog.dismiss();
            if (json != null) {
                updateThePreferencesValues();
            }else{
                Toast.makeText(ProfileFragment.this,"Update failed, Try again",Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void updateThePreferencesValues() {
        SharedPreferences userProfile = ProfileFragment.this.getSharedPreferences("ProfileDetails", MODE_APPEND);
        SharedPreferences.Editor profilerEditor = userProfile.edit();
        profilerEditor.putString("bloodgroup",myBloodGroupSpinner.getText().toString());
        profilerEditor.putString("searchprivacy",mySeacrchSpinner.getText().toString());
        profilerEditor.putString("mobileprivacy",myNumberSpinner.getText().toString());
        profilerEditor.putString("internetprivacy",myInternetSpinner.getText().toString());
        profilerEditor.commit();
        Toast.makeText(ProfileFragment.this,"Updated Sucessfully",Toast.LENGTH_SHORT).show();
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