package com.dabkick.partner.fb;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.dabkick.sdk.DabKick_Agent;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;


import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    CallbackManager callbackManager;
    Button fbLogin;
    EditText email, phone, id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        fbLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loginWithFB();

            }
        });
    }

    void init(){

        fbLogin = (Button)findViewById(R.id.cnt_btn);
        email = (EditText)findViewById(R.id.email_id);
        phone = (EditText)findViewById(R.id.ph_num);
        id = (EditText)findViewById(R.id.un_id);

    }

    void loginWithFB(){

        FacebookSdk.sdkInitialize(MainActivity.this.getApplicationContext());
        LoginManager.getInstance().logInWithReadPermissions(
                MainActivity.this,
                Arrays.asList("user_friends"));

        LoginManager.getInstance().setLoginBehavior(LoginBehavior.NATIVE_WITH_FALLBACK);

        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {

                        DabKick_Agent.DK_Register("FB_ID", loginResult, "",
                               "", "", MainActivity.this);

                        Intent selectVideo = new Intent(MainActivity.this, SelectVideo.class);
                        selectVideo.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(selectVideo);
                        finish();
                    }

                    @Override
                    public void onCancel() {
                        showAlert("Unable to perform selected action because permissions were not granted");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        showAlert(exception.getMessage());
                    }

                    private void showAlert(String message) {
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("Cancelled")
                                .setMessage(message)
                                .show();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
