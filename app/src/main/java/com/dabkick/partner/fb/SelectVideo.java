package com.dabkick.partner.fb;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dabkick.sdk.Dabkick;
import com.dabkick.sdk.Global.GlobalHandler;
import com.dabkick.sdk.Global.HorizontalListView;
import com.dabkick.sdk.Global.PreferenceHandler;
import com.dabkick.sdk.Global.VideoManager;
import com.dabkick.sdk.Livesession.VideoHorizontalAdapter;
import com.dabkick.sdk.Livesession.YouTubeVideoDetail;
import com.dabkick.sdk.Video.PlayDabKickVideoActivity;

import java.util.ArrayList;

public class SelectVideo extends AppCompatActivity {

    //your own listview
    public HorizontalListView hListView;
    //your own adapter
    public VideoHorizontalAdapter mVideoHorizontalAdapter;
    //Dabkick agent init to make use of lib
    VideoManager videoManager = VideoManager.getInstance();
    //local array list to get the results
    ArrayList VideosList;

    //Dabkickvideodetail
    YouTubeVideoDetail mYoutubeVideoDetailSingleItem;

    TextView statusMsg,userInfo;
    Button watchWithFriends;

    ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_video);

        init();

        statusMsg.setMovementMethod(new ScrollingMovementMethod());

        try {
            String loginInfo = "Email:" + PreferenceHandler.getEmail() + "\nPhone number:" + PreferenceHandler.getPhoneNum()
                    + "\nUnique ID:" + PreferenceHandler.getUniqueID();
            statusMsg.setText(loginInfo);
        }
        catch (Exception e){}

        mProgressBar.setVisibility(View.VISIBLE);

        hListView = (HorizontalListView) findViewById(R.id.testvideosListView);

        //make a call to this agent to get the search results and pass the results to your adapter to show it in list view
        videoManager.setOnSearchFinishedLoading(new VideoManager.OnSearchFinishedLoadingListener() {
            @Override
            public void onSearchFinishedLoading(boolean b) {
                GlobalHandler.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        mProgressBar.setVisibility(View.GONE);
                        //call this method with the search term to get the results
                        VideosList = videoManager.getSearchResultByTerm("facebook");
                        mVideoHorizontalAdapter = new VideoHorizontalAdapter(SelectVideo.this, R.layout.video_view_item, VideosList, false);
                        hListView.setAdapter(mVideoHorizontalAdapter);
                        mVideoHorizontalAdapter.notifyDataSetChanged();

                    }
                });
            }
        });
        //Condition check to load the searched videos if not throw alert(logic done in DabKickVideoManagerAgent)
        videoManager.searchVideo("facebook");

        hListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Call this method to get the selected video
                mYoutubeVideoDetailSingleItem = (YouTubeVideoDetail)mVideoHorizontalAdapter.getItem(position);
                Intent intent = new Intent(SelectVideo.this, PlayDabKickVideoActivity.class);
                intent.putExtra(PlayDabKickVideoActivity.EXTRA_VIDEO_ID, mYoutubeVideoDetailSingleItem.videoID);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    void init(){

        statusMsg = (TextView)findViewById(R.id.statusMsg);
        mProgressBar = (ProgressBar)findViewById(R.id.progress_bar);

    }
}
