package com.dabkick.sdk.DabKickVideoAgent.DabKickVideoPlayer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.dabkick.sdk.DabKickVideoAgent.DabKickGlobalData;
import com.dabkick.sdk.DabKickVideoAgent.DabKickVideoDetail;
import com.dabkick.sdk.DabKickVideoAgent.DabKickVideoManagerAgent;
import com.dabkick.sdk.DabKick_Agent;
import com.dabkick.sdk.R;

import java.util.ArrayList;

public class PlayDabKickVideoActivity extends Activity {

    final public static String EXTRA_VIDEO_ID = "videoID";

    DabKickVideoDetail mDabKickVideoDetail;

    public int stopPosition;
    long totalDuration;
    long currentDuration;
    boolean away = false;
    String mEntry = "normal";

    SeekBar videoseek;
    VideoView videoView;
    ProgressDialog progressDialog;
    TextView video_name, startpos, stoppos, landscapeEpisodeTitle;

    Button play_pause_button;
    RelativeLayout videocontrol_relative_layout, scrubber_relative, wathWIthFriend, playPauseBtnContainer, closeVideoPlayer;

    DabKickVideoManagerAgent dabKickVideoManagerAgent = DabKickVideoManagerAgent.getInstance();
    RelativeLayout messageWatchWithTogetherBtn;

    public DabkickUtilities dabkickUtilities;

    int positionContinuous;

    private Handler mHandlertoSeek = new Handler();

    CountDownTimer mCountDownTimer = null;

    Boolean isVideoPaused = false, isVideoPausedFromWatchWithFriend = false;

    public static ArrayList<String> videoSelectedForPlay = new ArrayList<>();

    void findViews() {
        videoView = (VideoView) findViewById(R.id.youtubevideoview);
        wathWIthFriend = (RelativeLayout) findViewById(R.id.watch_with_friend);
        play_pause_button = (Button) findViewById(R.id.play_pause_onscreen_btn);
        playPauseBtnContainer = (RelativeLayout) findViewById(R.id.play_pause_btn_container);
//        done = (TextView) findViewById(R.id.done_txt);
        closeVideoPlayer = (RelativeLayout)findViewById(R.id.close_video_player);
        stoppos = (TextView) findViewById(R.id.stop_position);
        startpos = (TextView) findViewById(R.id.start_position);
        video_name = (TextView) findViewById(R.id.video_title_onscreen_txt);
        scrubber_relative = (RelativeLayout) findViewById(R.id.scrubber_relative);
        videocontrol_relative_layout = (RelativeLayout) findViewById(R.id.video_controls_relative);
        videoseek = (SeekBar) findViewById(R.id.seekbar);
    }

    @Override
    protected void onCreate(Bundle bundle) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(bundle);
        setContentView(R.layout.activity_play_dab_kick_video);
        findViews();
        dabkickUtilities = new DabkickUtilities();

        videoseek.getProgressDrawable().setColorFilter(Color.LTGRAY, PorterDuff.Mode.SRC_IN);

        Intent intent = getIntent();
        String videoID = intent.getStringExtra(EXTRA_VIDEO_ID);
        mDabKickVideoDetail = DabKickGlobalData.getInstance().GlobalVideoDetails.get(videoID);
        if (mDabKickVideoDetail == null)
            return;

        if(intent != null && intent.getBooleanExtra("fromLiveChat",false)){
            wathWIthFriend.setVisibility(View.GONE);
        }else{
            wathWIthFriend.setVisibility(View.VISIBLE);
        }
        video_name.setText(mDabKickVideoDetail.videoTitle);

        mDabKickVideoDetail.setOnFinishedLoadStreamURLListener(new DabKickVideoDetail.OnFinishedLoadStreamURLListener() {
            @Override
            public void OnFinishedLoadStreamURL(String fullStreamURL) {
                if (fullStreamURL != null)
                    playVideo(fullStreamURL);
            }
        });
        mDabKickVideoDetail.LoadStreamURL(PlayDabKickVideoActivity.this);

        videoView.setKeepScreenOn(true);

        playPauseBtnContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoView.isPlaying()) {
                    stopPosition = videoView.getCurrentPosition();
                    videoView.pause();
                    play_pause_button.setBackgroundResource(R.drawable.ic_play_arrow_white_24dp);
                    isVideoPaused = true;
                } else if (stopPosition != 0000) {
                    videoView.seekTo(stopPosition);
                    videoView.start();
                    play_pause_button.setBackgroundResource(R.drawable.ic_pause_white_24dp);
                    isVideoPaused = false;
                } else {
                    videoView.start();
                    play_pause_button.setBackgroundResource(R.drawable.ic_pause_white_24dp);
                    isVideoPaused = false;
                }
            }
        });

        closeVideoPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        videoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                setvisib(v);
                return false;
            }
        });

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                videoseek.setProgress(0);
                videoseek.setMax(100);
                updateProgressBar();
            }
        });

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                videoseek.setProgress(0);
                //set stop position to 0
                //so that video starts from the begining when you click play button again
                stopPosition = 0000;
                videoseek.setProgress(stopPosition);
                play_pause_button.setBackgroundResource(R.drawable.ic_play_arrow_white_24dp);

                //on completion of one video show the videos mini window
//                showRelatedVideos();
            }
        });

        videoseek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mHandlertoSeek.removeCallbacks(mUpdateTimeTask);
                int totalDuration = videoView.getDuration();
                int currentPosition = dabkickUtilities.progressToTimer(seekBar.getProgress(), totalDuration);
                videoView.seekTo(currentPosition);
                updateProgressBar();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mHandlertoSeek.removeCallbacks(mUpdateTimeTask);
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    videoView.seekTo(progress);
                }
            }
        });

        wathWIthFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoView.isPlaying()) {
                    videoView.pause();
                    isVideoPausedFromWatchWithFriend = true;
                    play_pause_button.setBackgroundResource(R.drawable.ic_play_arrow_white_24dp);
                }

                if (videocontrol_relative_layout.getVisibility() == View.VISIBLE && scrubber_relative.getVisibility() == View.VISIBLE) {
                    videocontrol_relative_layout.setVisibility(View.GONE);
                    scrubber_relative.setVisibility(View.GONE);
                }

                //ashwini added
                dabKickVideoManagerAgent.getInstance().clearSelectedItem();
                dabKickVideoManagerAgent.getInstance().addToSelectedList(mDabKickVideoDetail);
                //ashwini ended

                //end
                DabKick_Agent.watchWithFriends(PlayDabKickVideoActivity.this);
            }
        });

    }

    public void updateProgressBar() {
        mHandlertoSeek.postDelayed(mUpdateTimeTask, 100);
    }

    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            totalDuration = videoView.getDuration();
            currentDuration = videoView.getCurrentPosition();

            startpos.setText("" + dabkickUtilities.milliSecondsToTimer(currentDuration));
            stoppos.setText("" + dabkickUtilities.milliSecondsToTimer(totalDuration));

            int progress = (int) (dabkickUtilities.getProgressPercentage(currentDuration, totalDuration));
            videoseek.setProgress(progress);

            mHandlertoSeek.postDelayed(this, 100);
        }
    };

    @Override
    public void onDestroy() {

        super.onDestroy();
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.cancel();
        }

        Log.e("Deepak", "Destroying activity");
    }

    private void setvisib(View v) {
        int a = View.GONE;
        boolean vis = videocontrol_relative_layout.getVisibility() == a || scrubber_relative.getVisibility() == a || closeVideoPlayer.getVisibility() == a;
        if (vis)
            a = View.VISIBLE;

        countDownTimeHandler();

        closeVideoPlayer.setVisibility(a);
        videocontrol_relative_layout.setVisibility(a);
        scrubber_relative.setVisibility(a);
    }

    public void countDownTimeHandler() {
        //Cancel the previous one
        if (mCountDownTimer != null)
            mCountDownTimer.cancel();

        mCountDownTimer = new CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                DabKickGlobalData.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        closeVideoPlayer.setVisibility(View.GONE);
                        videocontrol_relative_layout.setVisibility(View.GONE);
                        scrubber_relative.setVisibility(View.GONE);
                    }
                });
            }

            ;
        }.start();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (away) {
            if (isVideoPaused) {
                videoView.seekTo(stopPosition);
                enablePlayControls();
            } else if (isVideoPausedFromWatchWithFriend) {
                videoView.seekTo(stopPosition);
                videoView.start();
                play_pause_button.setBackgroundResource(R.drawable.ic_pause_white_24dp);
            } else {
                videoView.seekTo(stopPosition);
//                enablePlayControls();
//                videoView.seekTo(stopPosition);
//                videoView.start();
                play_pause_button.setBackgroundResource(R.drawable.ic_play_arrow_white_24dp);
            }
            away = false;
        }

    }

    public void onPause() {
        super.onPause();

        if (isVideoPaused) {
            stopPosition = videoView.getCurrentPosition();
        }
        if (isVideoPausedFromWatchWithFriend) {
            stopPosition = videoView.getCurrentPosition();
        }

        stopPosition = videoView.getCurrentPosition();
        videoView.pause();

        away = true;

    }

    void playVideo(final String url) {

        if (url == null)
            return;

        DabKickGlobalData.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                try {
                    Uri uri = Uri.parse(url);
                    videoView.setVideoURI(uri);
                    videoView.requestFocus();
                    videoView.start();
                    isVideoPaused = false;
                } catch (Exception e) {
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        //finish();
//        super.onBackPressed();

        if (videoView.isPlaying()) {
            videoView.pause();
            play_pause_button.setBackgroundResource(R.drawable.ic_play_arrow_white_24dp);

        } else {
            finish();
        }
    }

    public void enablePlayControls() {
        closeVideoPlayer.setVisibility(View.VISIBLE);
        videocontrol_relative_layout.setVisibility(View.VISIBLE);
        scrubber_relative.setVisibility(View.VISIBLE);

        if (!videoView.isPlaying()) {
            play_pause_button.setBackgroundResource(R.drawable.ic_play_arrow_white_24dp);
        } else {
            play_pause_button.setBackgroundResource(R.drawable.ic_pause_white_24dp);
        }

        countDownTimeHandler();
    }

    //when power button is pressed handle the play back
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {

            if (isVideoPaused) {
                videoView.seekTo(stopPosition);
                enablePlayControls();
            }
        } else {
            videoView.pause();
            stopPosition = videoView.getCurrentPosition();
            isVideoPaused = true;
        }
    }
}
