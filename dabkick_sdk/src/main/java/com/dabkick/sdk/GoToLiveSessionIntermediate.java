package com.dabkick.sdk;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Timer;

public class GoToLiveSessionIntermediate extends AppCompatActivity {
    CircularImageView profilePic;
    RelativeLayout end;
    RelativeLayout leaveMessage, waitingLayout, endConfirmLayout, maskLayout;
    TextView friendName, OKBtn, NOThanksBtn, cancelBtn;
    boolean isEndClicked;
    boolean mFlag1 = false;
    TextView waitingText;
    String grpName, singleGroupName;
    private String TAG = "AutoSlider";

    boolean isStart = true;

    float conatinerX1;

    float containerX2;
    Bitmap bm;

    float scrollBarWidth;
    FrameLayout scrollBar;

    TranslateAnimation moveLefttoRight;

    TranslateAnimation moveRightToLeft;

    boolean setExpired = true;
    Timer expiredTimer;
    boolean mFlag = false;

    public void init() {
        end = (RelativeLayout) findViewById(R.id.end);
        profilePic = (CircularImageView) findViewById(R.id.friend_image_layout);
        friendName = (TextView) findViewById(R.id.friend_name);
        waitingLayout = (RelativeLayout) findViewById(R.id.waiting_layout);
        endConfirmLayout = (RelativeLayout) findViewById(R.id.endConfirmLayout);
        OKBtn = (TextView) findViewById(R.id.OKBtn);
        NOThanksBtn = (TextView) findViewById(R.id.NOThanksBtn);
        cancelBtn = (TextView) findViewById(R.id.CancelBtn);
        maskLayout = (RelativeLayout) findViewById(R.id.mask);

        scrollBar = (FrameLayout) findViewById(R.id.scrollbar);
        isEndClicked = false;

        waitingText = (TextView) findViewById(R.id.waiting);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_go_to_live_session_intermediate);
        init();
        Picasso.with(GoToLiveSessionIntermediate.this).load("enter path").placeholder(R.drawable.generic_avatar_cat_v70).into(profilePic);
        friendName.setText(getIntent().getStringExtra("friendName"));

        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoToLiveSessionIntermediate.this.finish();
            }
        });

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus) {
            createAnim();
        }
    }

    private void createAnim() {
        conatinerX1 = waitingLayout.getLeft();

        containerX2 = waitingLayout.getWidth();

        scrollBarWidth = scrollBar.getWidth();

        containerX2 = containerX2 - scrollBarWidth;

        moveLefttoRight = new TranslateAnimation(conatinerX1, containerX2, 0, 0);
        // moveLefttoRight.setInterpolator(new AccelerateInterpolator());
        moveLefttoRight.setDuration(2000);
        moveLefttoRight.setFillAfter(true);

        moveRightToLeft = new TranslateAnimation(containerX2, conatinerX1, 0, 0);
        // moveLefttoRight.setInterpolator(new AccelerateInterpolator());
        moveRightToLeft.setDuration(2000);
        moveRightToLeft.setFillAfter(true);

        moveLefttoRight.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // TODO Auto-generated method stub
                scrollBar.startAnimation(moveRightToLeft);

            }
        });

        moveRightToLeft.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // TODO Auto-generated method stub
                scrollBar.startAnimation(moveLefttoRight);
            }
        });

        scrollBar.startAnimation(moveLefttoRight);
    }
}
