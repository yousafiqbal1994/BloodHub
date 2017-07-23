package com.giveblood.bloodhub.userprofile;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.giveblood.bloodhub.R;
import com.giveblood.bloodhub.loginsignup.LoginActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by YouCaf Iqbal on 6/29/2016.
 */
public class UpdateActivity  extends AppCompatActivity {
    @InjectView(R.id.image) ImageView _USERImage;
    @InjectView(R.id.input_name) EditText _nameText;
    @InjectView(R.id.btnchangephoto) Button changePhoto;
    @InjectView(R.id.input_password) EditText _passwordText;
    @InjectView(R.id.input_number) TextView _numText;
    public static boolean UpdatingPhoto = false;
    public String encodedPhotoString = null;
    public String ChangedName = null;
    public String ChangedPassword = null;
    public String ChangedContactNo = null;
    //HashMap<String, String> ThingsTobeUpdated = null;
    FormBody.Builder formBuilder =null;
    public  ProgressDialog pDialog;
    public Bitmap newimage=null;
    public static boolean imageChanged = false;
    Bitmap resizedBitmap=null;
    private static final float BYTES_PER_PX = 4.0f;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //Intent intent = new Intent(getApplicationContext(), MainGUI.class);
        //startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{
            setContentView(R.layout.updateactivity);
            ButterKnife.inject(this);
        }catch (OutOfMemoryError e){
            Toast.makeText(getBaseContext(), getBaseContext().getResources().getString(R.string.OOM), Toast.LENGTH_SHORT).show();
        }

        imageChanged = false;
        //ThingsTobeUpdated  = new HashMap<>();
        //newimage = CropForUpload.newImage;
        CheckImage();

        changePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  UpdatingPhoto = true;
               // Intent intent = new Intent(getApplicationContext(), CropForUpload.class);
               // startActivity(intent);

              //  UpdateActivity.this.finish();
            }
        });

    }
    public void CheckImage() {

        if (newimage != null) {
            //SetNewImage();
           // loadImage();
            Uri uri = getImageUri(newimage);
            String url = getRealPathFromURI(uri);
            File file = new File(url);
            Glide.with(UpdateActivity.this).load(file).asBitmap().diskCacheStrategy( DiskCacheStrategy.NONE ).skipMemoryCache( true ).override(300,300)
                    .transform(new CenterCrop(UpdateActivity.this),new CustomCenterCrop(UpdateActivity.this)).into(_USERImage);
            EncodethePhoto();
        } else {
           encodedPhotoString =  null;
         }
    }

    public void EncodethePhoto() {
        // resize the image to store to database
        newimage=  getResizedBitmap(newimage,400,400);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        newimage.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byte_arr = stream.toByteArray();
        encodedPhotoString = Base64.encodeToString(byte_arr, 0);
    }

    // Resize the image ====================================
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

    public String getRealPathFromURI(Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = UpdateActivity.this.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public Uri getImageUri( Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(UpdateActivity.this.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getImageURL(){
        SharedPreferences userProfile = UpdateActivity.this.getSharedPreferences("ProfileDetails", Context.MODE_PRIVATE);
        String ImageofLoggedINUser = userProfile.getString("image","");
        return "http://faceblood.website/bhub/images/"+ImageofLoggedINUser;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_update, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String ChangesHappened = "no";
        if (item.getItemId() == R.id.done_update) {
                // start updating process
            if(_nameText.getText().toString().equals("")){
                ChangedName = null;
            }else {
                ChangedName = _nameText.getText().toString();
                ChangesHappened = "yes";
            }
            if(_passwordText.getText().toString().equals("")){
                ChangedPassword = null;
            }else {
                ChangedPassword = _passwordText.getText().toString();
                ChangesHappened = "yes";
            }
            if(_numText.getText().toString().equals("")){
                ChangedContactNo = null;
            }else {
                ChangedContactNo = _numText.getText().toString();
                ChangesHappened = "yes";
            }
            if(encodedPhotoString!=null){
                ChangesHappened = "yes";

                Log.e("photo",encodedPhotoString.toString());
            }else {
                encodedPhotoString = null;
               // Log.e("photo",encodedPhotoString.toString());
            }
            if(ChangesHappened=="yes"){
                 StartUpdate();
                 return true;
            }else {
                Toast.makeText(getBaseContext(), getBaseContext().getResources().getString(R.string.nshtaupdates), Toast.LENGTH_SHORT).show();
                if(newimage!=null) {
                    newimage.recycle();
                    newimage = null;
                }
                finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void StartUpdate() {
        SharedPreferences userProfile = UpdateActivity.this.getSharedPreferences("ProfileDetails", Context.MODE_PRIVATE);
        String EmailofLoggedInUser = userProfile.getString("email","");
        formBuilder = new FormBody.Builder().add("email",EmailofLoggedInUser);
        //ThingsTobeUpdated.put("email", LoginActivity.EmailofLoggedInUser);
        if (encodedPhotoString != null) {
            //ThingsTobeUpdated.put("image", encodedPhotoString);
            formBuilder.add("image", encodedPhotoString);
        }
        if (ChangedName != null) {
            //ThingsTobeUpdated.put("name", ChangedName);
            formBuilder.add("name", ChangedName);
        }
        if (ChangedPassword != null) {
            //ThingsTobeUpdated.put("password", ChangedPassword);
            formBuilder.add("password", ChangedPassword);
        }
        if (ChangedContactNo != null) {
            //ThingsTobeUpdated.put("contact", ChangedContactNo);
            formBuilder.add("contact", ChangedContactNo);
        }

        UpdateAsync updatestart = new UpdateAsync();
        updatestart.execute();

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
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //json = new HttpCall().postForJSON("http://faceblood.website/bhub/UpdateProfile.php", ThingsTobeUpdated);
            return null;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(UpdateActivity.this);
            pDialog.setMessage(getBaseContext().getResources().getString(R.string.updateskana)+"...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pDialog.dismiss();
            if (json != null) {
                Toast.makeText(getBaseContext(), getBaseContext().getResources().getString(R.string.ruksha), Toast.LENGTH_SHORT).show();
                imageChanged =  true;
                if(newimage!=null) {
                    newimage.recycle();
                    newimage = null;
                }
                UpdateActivity.this.finish();
            } else {
                Toast.makeText(getBaseContext(), getBaseContext().getResources().getString(R.string.erroza), Toast.LENGTH_SHORT).show();
            }
        }

    }
}
