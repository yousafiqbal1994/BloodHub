package com.giveblood.bloodhub.userprofile;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.giveblood.bloodhub.R;
import com.jaredrummler.materialspinner.MaterialSpinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by YouCaf Iqbal on 2/22/2017.
 */

public class DpActivity extends AppCompatActivity {

    public String encodedPhotoString;
    FormBody.Builder formBuilder =null;
    public ProgressDialog pDialog;
    CircleImageView dp;
    public Bitmap myDP=null;
    AlertDialog.Builder alert;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dp_layout);
        alert = new AlertDialog.Builder(this);
        dp = (CircleImageView)findViewById(R.id.dp);
        dp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                Intent intent = new Intent(getApplicationContext(), CroppingActivity.class);
                startActivity(intent);
                DpActivity.this.finish();
            }
        });

        myDP = CroppingActivity.finalImage;
        CheckImage();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setPreviousImage();
    }

    private void CheckImage() {
        if(myDP!=null){
            setPreviousImage();
            encodePhoto();
            SharedPreferences userDetails = DpActivity.this.getSharedPreferences("ProfileDetails", MODE_APPEND);
            String imageName = userDetails.getString("id","");
            formBuilder = new FormBody.Builder().add("imageName", imageName);
            formBuilder.add("imageString",encodedPhotoString);
            new UpdateAsync().execute();
        }else {
            setPreviousImage();
        }
    }


    private void setPreviousImage() {
        SharedPreferences userDetails = DpActivity.this.getSharedPreferences("ProfileDetails", MODE_APPEND);
        String imageString = userDetails.getString("image","");
        if (imageString.length() >= 80) {
            // decode string back to image and set it
            byte[] decodedString = Base64.decode(imageString, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            dp.setImageBitmap(decodedByte);
        }

    }

    public void encodePhoto() {
        myDP=  getResizedBitmap(myDP,400,400);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        myDP.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byte_arr = stream.toByteArray();
        encodedPhotoString = Base64.encodeToString(byte_arr, 0);
        Log.e("photo", encodedPhotoString);
    }
    public void savePhototoSharedPrefrence() {
        SharedPreferences userProfile = DpActivity.this.getSharedPreferences("ProfileDetails", MODE_APPEND);
        SharedPreferences.Editor profileDP = userProfile.edit();
        profileDP.putString("image",encodedPhotoString);
        profileDP.apply();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }
    public class UpdateAsync extends AsyncTask<Void, Void, Void> {

        JSONObject json = null;
        @Override
        protected Void doInBackground(Void... voids) {
            RequestBody formBody = formBuilder.build();
            Request request = new Request.Builder()
                    .url("http://faceblood.website/bhub/UpdateImage.php")
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
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(DpActivity.this);
            pDialog.setMessage(DpActivity.this.getResources().getString(R.string.updateskana)+"...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pDialog.dismiss();
            if (json != null) {
                // photo is updated
                savePhototoSharedPrefrence();
                dp.setImageBitmap(myDP);
                myDP =null;
                CroppingActivity.finalImage =  null;
                Toast.makeText(getApplicationContext(),"Updated Successfully", Toast.LENGTH_SHORT).show();
            } else {
                myDP =null;
                Toast.makeText(getApplicationContext(), DpActivity.this.getResources().getString(R.string.erroza), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth)
    {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // create a matrix for the manipulation
        Matrix matrix = new Matrix();
        // resize the bit map
        matrix.postScale(scaleWidth, scaleHeight);
        // recreate the new Bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        return resizedBitmap;
    }
}
