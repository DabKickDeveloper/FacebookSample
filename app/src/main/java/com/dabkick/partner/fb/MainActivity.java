package com.dabkick.partner.fb;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.dabkick.sdk.Dabkick;
import com.dabkick.sdk.Global.DialogHelper;
import com.dabkick.sdk.Global.UserIdentifier;
import com.jakewharton.rxbinding.view.RxView;


import java.util.concurrent.TimeUnit;

import rx.functions.Action1;

public class MainActivity extends AppCompatActivity {

    private RelativeLayout userDetails;
    private EditText userName;
    private EditText profilePicPath;
    private EditText unId;
    private LinearLayout registeredInfo;
    private TextView nameText;
    private TextView picText;
    private TextView uniqueIDText;
    private Button resetBtn;
    private Button regBtn;

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2016-04-08 23:40:36 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        userDetails = (RelativeLayout)findViewById( R.id.user_details );
        userName = (EditText)findViewById( R.id.user_name);
        profilePicPath = (EditText)findViewById( R.id.pic_path);
        unId = (EditText)findViewById( R.id.un_id );
        registeredInfo = (LinearLayout)findViewById( R.id.registeredInfo );
        nameText = (TextView)findViewById( R.id.nme_txt);
        picText = (TextView)findViewById( R.id.pic_txt);
        uniqueIDText = (TextView)findViewById( R.id.uniqueIDText );
        resetBtn = (Button)findViewById( R.id.reset_btn );
        regBtn = (Button)findViewById( R.id.reg_btn);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();

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
                    finish();
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
                                    finish();
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


    }
}
