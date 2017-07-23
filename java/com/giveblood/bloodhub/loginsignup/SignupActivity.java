package com.giveblood.bloodhub.loginsignup;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.giveblood.bloodhub.R;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;
import com.vstechlab.easyfonts.EasyFonts;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.InjectView;
import mehdi.sakout.fancybuttons.FancyButton;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SignupActivity extends AppCompatActivity {
    ShimmerTextView signUpText;
    public String name, password, username, age;
    @InjectView(R.id.input_name) EditText _nameText;
    @InjectView(R.id.input_username) EditText _usernameText;
    @InjectView(R.id.input_password) EditText _passwordText;
    @InjectView(R.id.againpassword) TextView _PasswordAgain;
    @InjectView(R.id.input_age) TextView _age;
    FancyButton _nextStep;
    public Boolean check = false;
    public boolean failed = false;
    ProgressDialog pDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
       // getSupportActionBar().hide(); //<< this

        setContentView(R.layout.activity_signup);
        signUpText = (ShimmerTextView) findViewById(R.id.SignUpText);
        signUpText.setTypeface(EasyFonts.captureIt(this));
        Shimmer signUpTextShrimmer = new Shimmer();
        signUpTextShrimmer.start(signUpText);
        signUpTextShrimmer.setDuration(3000);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ButterKnife.inject(this);
        _nextStep = (FancyButton) findViewById(R.id.nextStep);
        _nextStep.setFontIconSize(20);

        _nextStep.setTextSize(20);
        _nextStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                failed = false;
                signup();
                if (failed) {
                    return;
                } else {
                    if(_passwordText.getText().toString().equals(_PasswordAgain.getText().toString())){
                        CheckUserNameAsync checkUsername = new CheckUserNameAsync();
                        checkUsername.execute();
                    }else {
                        Toast.makeText(getBaseContext(), getBaseContext().getResources().getString(R.string.passdoesnotmatch), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void signup() {

        if (!validate()) {
            onSignupFailed();
            return;
        }
    }
    public void onSignupSuccess() {
        _nextStep.setEnabled(true);
        Intent intent = new Intent(getApplicationContext(), GenderAndMobile.class);
        intent.putExtra("name",name);
        intent.putExtra("password",password);
        intent.putExtra("age",age);
        intent.putExtra("username",username);
        startActivity(intent);
        //finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), getBaseContext().getResources().getString(R.string.regfailed), Toast.LENGTH_SHORT).show();
        _nextStep.setEnabled(true);
        failed = true;
    }

    public boolean validate() {
        boolean valid = true;
        GetUserDetails();
        if (name.isEmpty() || name.length() < 4 || name.length() > 20) {
            _nameText.setError(getBaseContext().getResources().getString(R.string.wrongname));
            valid = false;
        } else {
            _nameText.setError(null);
        }

        if (username.isEmpty() || name.length() < 4 || name.length() > 20) {
            _usernameText.setError(getBaseContext().getResources().getString(R.string.wrongnamex));
            valid = false;
        } else {
            _usernameText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 16) {
            _passwordText.setError(getBaseContext().getResources().getString(R.string.wrongpass));
            valid = false;
        } else {
            _passwordText.setError(null);
        }
        if (age.isEmpty() || age.length() > 2 || Integer.parseInt(_age.getText().toString()) < 0) {
            _age.setError(getBaseContext().getResources().getString(R.string.wrongage));
            valid = false;
        } else {
            _age.setError(null);
        }

        return valid;
    }

    public void GetUserDetails() {
        name = _nameText.getText().toString();
        username = _usernameText.getText().toString();
        password = _passwordText.getText().toString();
        age = _age.getText().toString();
    }


    public class CheckUserNameAsync extends AsyncTask<Void, Void, Void> {
        private ProgressDialog pDialog;

        @Override
        protected Void doInBackground(Void... voids) {
            check = UserNameExisted();
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(SignupActivity.this);
            pDialog.setMessage(getBaseContext().getResources().getString(R.string.checkbc) + "...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pDialog.dismiss();
            if (check) {
                Toast.makeText(getBaseContext(), "Username already existed, Try another", Toast.LENGTH_SHORT).show();
                _usernameText.setError("Username already existed, Try another");
            }else{
                onSignupSuccess();
            }
        }
    }

    public boolean UserNameExisted() {
        check = false;
        JSONObject json = null;
        String checking = null;
        String username = _usernameText.getText().toString();
        username = username.trim().toLowerCase();
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(50, TimeUnit.SECONDS)
                .writeTimeout(50, TimeUnit.SECONDS)
                .readTimeout(50, TimeUnit.SECONDS)
                .build();
        FormBody.Builder formBuilder = new FormBody.Builder().add("username", username);
        RequestBody formBody = formBuilder.build();
        Request request = new Request.Builder()
                .url("http://faceblood.website/bhub/UsernameExisted.php")
                .post(formBody)
                .build();

        try {
            Response response = client.newCall(request).execute();
            String res = response.body().string();
            json = new JSONObject(res);
        } catch (IOException | JSONException e) {
            check = false;
            e.printStackTrace();
        }
        if (json != null) {
            try {
                checking = json.getString("check");
            } catch (JSONException e) {
                check = false;
                e.printStackTrace();
            }
            if (checking.equals("true")) {
                check = true;
            } else {
                check = false;
            }
        }
        return check;
    }

}

