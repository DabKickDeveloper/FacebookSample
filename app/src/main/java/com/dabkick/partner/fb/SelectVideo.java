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
import com.dabkick.sdk.Global.UserInfo;
import com.dabkick.sdk.Global.VideoManager;
import com.dabkick.sdk.Livesession.VideoHorizontalAdapter;
import com.dabkick.sdk.Livesession.YouTubeVideoDetail;
import com.dabkick.sdk.Video.PlayDabKickVideoActivity;

import java.util.ArrayList;
import java.util.List;

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
                mYoutubeVideoDetailSingleItem = (YouTubeVideoDetail) mVideoHorizontalAdapter.getItem(position);
                Intent intent = new Intent(SelectVideo.this, PlayDabKickVideoActivity.class);
                intent.putExtra(PlayDabKickVideoActivity.EXTRA_VIDEO_ID, mYoutubeVideoDetailSingleItem.videoID);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        Dabkick.setCustomizedFriendsList(getFacebookFriends());
    }

    void init(){

        statusMsg = (TextView)findViewById(R.id.statusMsg);
        mProgressBar = (ProgressBar)findViewById(R.id.progress_bar);

    }



    List<UserInfo> getFacebookFriends(){

        List<UserInfo> friends = new ArrayList<>();

        UserInfo firstFriend = new UserInfo();
        firstFriend.setName("Chinmaya");
        firstFriend.setImageURL("http://images.all-free-download.com/images/graphiclarge/daisy_pollen_flower_220533.jpg");
        firstFriend.setUniqueID("Chinmaya");

        UserInfo secondFriend = new UserInfo();
        secondFriend.setName("Hitesh");
        secondFriend.setImageURL("https://encrypted-tbn3.gstatic.com/images?q=tbn:ANd9GcTW-hhdcp9Bmxl0l6JP6AxK3up4HjgT1Ss2_wW0ZlxbsW_GV09-");
        secondFriend.setUniqueID("Hitesh");


        UserInfo thirdFriend = new UserInfo();
        thirdFriend.setName("Deepak");
        thirdFriend.setImageURL("https://encrypted-tbn2.gstatic.com/images?q=tbn:ANd9GcTki4U4qokHd7NmDBXDEAr7f5KBCDLXyV6JxHjpLHFlGMKbxXIL");
        thirdFriend.setUniqueID("Deepak");


        UserInfo fourthFriend = new UserInfo();
        fourthFriend.setName("Ashwini");
        fourthFriend.setImageURL("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRfme0I3266Al0RFrFjIFnYdQBiUsI_i7y5iptSsV9tKBVm6A4b");
        fourthFriend.setUniqueID("Ashwini");


        UserInfo fifthFriend = new UserInfo();
        fifthFriend.setName("Vallabh");
        fifthFriend.setImageURL("https://encrypted-tbn2.gstatic.com/images?q=tbn:ANd9GcQ9mTL3v8UVTu-LqYWRghiTi-lJbzI7zk8chJN5cDXqX1IlZiRtew");
        fifthFriend.setUniqueID("Vallabh");

        friends.add(firstFriend);
        friends.add(secondFriend);
        friends.add(thirdFriend);
        friends.add(fourthFriend);
        friends.add(fifthFriend);

        return  friends;
    }
}
