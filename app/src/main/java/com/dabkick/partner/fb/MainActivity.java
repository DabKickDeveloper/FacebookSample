package com.dabkick.partner.fb;

import android.content.Intent;
import android.content.pm.PackageInstaller;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.dabkick.sdk.Dabkick;
import com.dabkick.sdk.Global.DialogHelper;
import com.dabkick.sdk.Global.GlobalHandler;
import com.dabkick.sdk.Global.UserIdentifier;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.jakewharton.rxbinding.view.RxView;


import java.util.concurrent.TimeUnit;

import rx.functions.Action1;

public class MainActivity extends AppCompatActivity {

    private RelativeLayout userDetails;
    private CustomEdTxt userName;
    private CustomEdTxt profilePicPath;
    private CustomEdTxt unId;
    private LinearLayout registeredInfo;
    private TextView nameText;
    private TextView picText;
    private TextView uniqueIDText;
    private Button resetBtn;
    private Button regBtn;

    private LoginButton loginButton;
    private CallbackManager callbackManager;

    String get_id, get_name, get_profile_image;

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2016-04-08 23:40:36 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        userDetails = (RelativeLayout)findViewById( R.id.user_details );
        userName = (CustomEdTxt)findViewById( R.id.user_name);
        profilePicPath = (CustomEdTxt)findViewById( R.id.pic_path);
        unId = (CustomEdTxt)findViewById( R.id.un_id );
        registeredInfo = (LinearLayout)findViewById( R.id.registeredInfo );
        nameText = (TextView)findViewById( R.id.nme_txt);
        picText = (TextView)findViewById( R.id.pic_txt);
        uniqueIDText = (TextView)findViewById( R.id.uniqueIDText );
        resetBtn = (Button)findViewById( R.id.reset_btn );
        regBtn = (Button)findViewById( R.id.reg_btn);
        loginButton = (LoginButton)findViewById(R.id.login_button);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.activity_main);

        findViews();

        RxView.clicks(resetBtn).throttleFirst(300, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                Dabkick.reset();
                registeredInfo.setVisibility(View.GONE);
                userDetails.setVisibility(View.VISIBLE);
                resetBtn.setVisibility(View.GONE);
            }//.l
        });

        RxView.clicks(regBtn).throttleFirst(300, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {

                if (Dabkick.isRegistered(MainActivity.this)) {
                    Intent selectVideo = new Intent(MainActivity.this, SelectVideo.class);
                    selectVideo.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(selectVideo);
                    //finish();
                    return;
                }

                if (!unId.getText().toString().isEmpty()) {

                    String name = userName.getText().toString();
                    String profilePic = profilePicPath.getText().toString();
                    String partnerID = unId.getText().toString();

                    UserIdentifier identifier = new UserIdentifier();
                    identifier.userName = name;
                    identifier.userProfilePic = profilePic;
                    identifier.uniqueID = partnerID;
                    identifier.email = null;
                    identifier.phoneNumber = null;

                    Dabkick.setOnRegisterFinished(new Dabkick.OnRegisterFinishedListener() {
                        @Override
                        public void onRegistered(boolean b, String s) {

                            Runnable ok = new Runnable() {
                                @Override
                                public void run() {
                                    Intent selectVideo = new Intent(MainActivity.this, SelectVideo.class);
                                    selectVideo.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(selectVideo);
                                    //finish();
                                }
                            };
                            DialogHelper.popupAlertDialog(MainActivity.this, null, "The app is now registered with DabKick with the provided user credentials.", "ok", ok);
                        }
                    });
                    String packageName = MainActivity.this.getPackageName();
                    Dabkick.register(MainActivity.this, packageName, identifier);
                } else {
                    Toast.makeText(MainActivity.this, "All fields Empty. Enter at least one field", Toast.LENGTH_SHORT).show();
                }
            }
        });

        userName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    requestFocus(profilePicPath);
                    return true;
                }
                return handled;
            }
        });

        profilePicPath.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    requestFocus(unId);
                    return true;
                }
                return handled;
            }
        });

        //Deepak added
        //for facebook login
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            private ProfileTracker mProfileTracker;
            @Override
            public void onSuccess(LoginResult loginResult) {

                if (Dabkick.isRegistered(MainActivity.this)) {
                    Intent selectVideo = new Intent(MainActivity.this, SelectVideo.class);
                    selectVideo.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(selectVideo);
                    //finish();
                    return;
                }

                if(Profile.getCurrentProfile() == null) {
                    mProfileTracker = new ProfileTracker() {
                        @Override
                        protected void onCurrentProfileChanged(Profile profile, Profile profile2) {
                            // profile2 is the new profile
                            get_id = profile2.getId().toString();
                            get_name = profile2.getName().toString();
                            get_profile_image = profile2.getProfilePictureUri(400, 400).toString();

                            Log.e("deepak","profile details: FacebookID: "+get_id+" Facebook profile name: "+get_name+" profile picture: "+get_profile_image);
                            mProfileTracker.stopTracking();

                            registerUser();
                        }
                    };
                    mProfileTracker.startTracking();
                }
                else {
                    Profile profile = Profile.getCurrentProfile();
                    if (profile != null) {
                        get_id = profile.getId();
                        get_name = profile.getName();
                        get_profile_image = profile.getProfilePictureUri(400, 400).toString();

                        registerUser();

                        Log.e("deepak","profile details: FacebookID: "+get_id+" Facebook profile name: "+get_name+" profile picture: "+get_profile_image);
                    }
                }
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException e) {

            }
        });
    }

    private void registerUser(){
        UserIdentifier identifier = new UserIdentifier();
        identifier.userName = get_name;
        identifier.userProfilePic = get_profile_image;
        identifier.uniqueID = get_id;
        identifier.email = null;
        identifier.phoneNumber = null;

        Dabkick.setOnRegisterFinished(new Dabkick.OnRegisterFinishedListener() {
            @Override
            public void onRegistered(boolean b, String s) {

                Runnable ok = new Runnable() {
                    @Override
                    public void run() {
                        Intent selectVideo = new Intent(MainActivity.this, SelectVideo.class);
                        selectVideo.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(selectVideo);
                        //finish();
                    }
                };
                DialogHelper.popupAlertDialog(MainActivity.this, null, "The app is now registered with DabKick with the provided user credentials.", "ok", ok);
            }
        });
        String packageName = MainActivity.this.getPackageName();
        Dabkick.register(MainActivity.this, packageName, identifier);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (Dabkick.isRegistered(this))
        {
            registeredInfo.setVisibility(View.VISIBLE);
            userDetails.setVisibility(View.GONE);
            resetBtn.setVisibility(View.VISIBLE);

            UserIdentifier userIdentifier = UserIdentifier.getStoredValue(this);
            nameText.setText(userIdentifier.userName);
            picText.setText(userIdentifier.userProfilePic);
            uniqueIDText.setText(userIdentifier.uniqueID);

        }
        else {
            registeredInfo.setVisibility(View.GONE);
            userDetails.setVisibility(View.VISIBLE);
            resetBtn.setVisibility(View.GONE);
        }
    }

    void requestFocus(final EditText editText) {
        editText.post(new Runnable() {
            @Override
            public void run() {
                editText.requestFocus();
                editText.setSelection(editText.getText().length());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
