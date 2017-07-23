package com.giveblood.bloodhub.loginsignup;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.giveblood.bloodhub.R;
import com.giveblood.bloodhub.callfeature.BaseActivity;
import com.giveblood.bloodhub.callfeature.SinchService;
import com.giveblood.bloodhub.others.MainActivity;
import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;
import com.sinch.android.rtc.SinchError;
import com.vstechlab.easyfonts.EasyFonts;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.InjectView;
import mehdi.sakout.fancybuttons.FancyButton;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
public class LoginActivity extends BaseActivity implements SinchService.StartFailedListener {
    public String userName,password; // variables to be send to server
    ShimmerTextView LoginText;
    public ProgressDialog pDialog;
    public String iDofLoggedINUser,nameofLoggedINUser,userNameofLoggedInUser,passwordofLoggedINUser,ageofLoggedINUser;
    public String numberofLoggedINUser,bloodgroupofLoggedINUser,latofLoggedINUser,longiofLoggedINUser,genderofLoggedINUser;
    public String searchPrivacyofLoggedINUser,mobilePrivacyofLoggedINUser,internetPrivacyofLoggedINUser;
    @InjectView(R.id.input_username) EditText _usernameText;
    @InjectView(R.id.input_password) EditText _passwordText;
    FancyButton loginButton;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        getSupportActionBar().hide(); //<< this
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);
        loginButton = (FancyButton) findViewById(R.id.btn_login);
        loginButton.setFontIconSize(20);
        loginButton.setTextSize(20);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        LoginText = (ShimmerTextView) findViewById(R.id.LoginText);
        LoginText.setTypeface(EasyFonts.captureIt(this));
        Shimmer signUpTextShrimmer = new Shimmer();
        signUpTextShrimmer.start(LoginText);
        signUpTextShrimmer.setDuration(3000);
        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(isNetworkAvailable() && _passwordText.getText().length()!=0 && _usernameText.getText().length()!=0){
                    AsyncLogin LoginThread = new AsyncLogin();
                    LoginThread.execute();
                }
                else {
                    if(!isNetworkAvailable()){
                        Toast.makeText(getBaseContext(),getBaseContext().getResources().getString(R.string.nonetwork), Toast.LENGTH_SHORT).show();}
                    else {
                        validate();
                    }
                }
            }
        });
        if(getIntent().getStringExtra("afterSignUp").equals("yes")){
            String userN = getIntent().getStringExtra("username");
            String pasw = getIntent().getStringExtra("password");
            _usernameText.setText(userN);
            _passwordText.setText(pasw);
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public class AsyncLogin extends AsyncTask<Void,Void,Void> {
        JSONObject json =null;
        @Override
        protected Void doInBackground(Void... voids) {
            login();
            return null;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!validate()) {
                onLoginFailed();
                return;
            }
            else {
                loginButton.setEnabled(false);
            pDialog = new ProgressDialog(LoginActivity.this);
            pDialog.setMessage(getBaseContext().getResources().getString(R.string.authentication)+"...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(json!=null){
                try {
                    JSONObject jObject = json.getJSONObject("1");
                    iDofLoggedINUser=jObject.getString("id");
                    nameofLoggedINUser=jObject.getString("name");
                    userNameofLoggedInUser=jObject.getString("username");
                    passwordofLoggedINUser=jObject.getString("password");
                    ageofLoggedINUser=jObject.getString("age");
                    numberofLoggedINUser=jObject.getString("number");
                    bloodgroupofLoggedINUser=jObject.getString("bloodgroup");
                    latofLoggedINUser=jObject.getString("latitude");
                    longiofLoggedINUser=jObject.getString("longitude");
                    genderofLoggedINUser=jObject.getString("gender");
                    searchPrivacyofLoggedINUser=jObject.getString("search");
                    mobilePrivacyofLoggedINUser=jObject.getString("mobilesettings");
                    internetPrivacyofLoggedINUser=jObject.getString("internetsettings");
                    if (!getSinchServiceInterface().isStarted()) {
                        getSinchServiceInterface().startClient(iDofLoggedINUser); // Start SinchClient for first time
                    }
                } catch (JSONException e) {
                    onLoginFailed();
                    e.printStackTrace();
                }
            }
            else{
                onLoginFailed();
            }
        }
        public void login() {
            if(_usernameText.getText().length()==0 || _passwordText.getText().length()==0){
                return;
            }
            else {
                userName = _usernameText.getText().toString().trim().toLowerCase();
                password = _passwordText.getText().toString();
                OkHttpClient client =  new OkHttpClient.Builder()
                        .connectTimeout(50, TimeUnit.SECONDS)
                        .writeTimeout(50, TimeUnit.SECONDS)
                        .readTimeout(50, TimeUnit.SECONDS)
                        .build();
                FormBody.Builder formBuilder = new FormBody.Builder() .add("username",userName)
                        .add("password",password);
                RequestBody formBody = formBuilder.build();
                Request request = new Request.Builder()
                        .url("http://faceblood.website/bhub/AuthenticateUser.php")
                        .post(formBody)
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    String res = response.body().string();
                    json = new JSONObject(res);
                    // Do something with the response.
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void onLoginSuccess() {
        loginButton.setEnabled(true);
        addProfile(); // Store profile details in shared preference
        stopService(new Intent(this, SinchService.class));
        pDialog.dismiss();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void addProfile() {
        SharedPreferences userProfile = LoginActivity.this.getSharedPreferences("ProfileDetails", MODE_APPEND);
        SharedPreferences.Editor profilerEditor = userProfile.edit();
        profilerEditor.putString("registered","true");
        profilerEditor.putString("id",iDofLoggedINUser);
        profilerEditor.putString("name",nameofLoggedINUser);
        profilerEditor.putString("username",userNameofLoggedInUser);
        profilerEditor.putString("password",passwordofLoggedINUser);
        profilerEditor.putString("age",ageofLoggedINUser);
        profilerEditor.putString("number",numberofLoggedINUser);
        profilerEditor.putString("bloodgroup",bloodgroupofLoggedINUser);
        profilerEditor.putString("latitude",latofLoggedINUser);
        profilerEditor.putString("longitude",longiofLoggedINUser);
        profilerEditor.putString("gender",genderofLoggedINUser);
        profilerEditor.putString("searchprivacy",searchPrivacyofLoggedINUser);
        profilerEditor.putString("mobileprivacy",mobilePrivacyofLoggedINUser);
        profilerEditor.putString("internetprivacy",internetPrivacyofLoggedINUser);
        profilerEditor.putString("image",iDofLoggedINUser);
        profilerEditor.commit();
    }


    public void onLoginFailed() {
        pDialog.dismiss();
        Toast.makeText(getBaseContext(),getBaseContext().getResources().getString(R.string.failurelogin), Toast.LENGTH_SHORT).show();
        loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String username = _usernameText.getText().toString();
        String password = _passwordText.getText().toString();

        if (username.isEmpty()) {
            _usernameText.setError(getBaseContext().getResources().getString(R.string.validemail));
            valid = false;
        } else {
            _usernameText.setError(null);
        }
        if (password.isEmpty()) {
            _passwordText.setError(getBaseContext().getResources().getString(R.string.validpassword));
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

    public interface StartFailedListener {
        void onStartFailed(SinchError error);
        void onStarted();
    }

    @Override
    public void onStartFailed(SinchError error) {
        onLoginFailed();
    }

    @Override
    public void onStarted() {
        Toast.makeText(getBaseContext(), getBaseContext().getResources().getString(R.string.successlogin), Toast.LENGTH_SHORT).show();
        onLoginSuccess();

    }
    @Override
    protected void onServiceConnected() {
        getSinchServiceInterface().setStartListener(this);
    }
}
