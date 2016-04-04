package com.dabkick.partner.fb;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.dabkick.sdk.DabKick_Agent;


import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loginWithFB();
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
