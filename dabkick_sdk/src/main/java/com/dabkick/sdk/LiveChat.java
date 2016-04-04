package com.dabkick.sdk;

import android.content.Intent;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dabkick.sdk.Adapter.VideoHorizontalAdapter;
import com.dabkick.sdk.DabKickVideoAgent.DabKickGlobalData;
import com.dabkick.sdk.DabKickVideoAgent.DabKickVideoDetail;
import com.dabkick.sdk.DabKickVideoAgent.DabKickVideoManagerAgent;
import com.dabkick.sdk.DabKickVideoAgent.DabKickVideoPlayer.PlayDabKickVideoActivity;
import com.dabkick.sdk.Horizontal.HorizontalListView;

import java.util.ArrayList;

public class LiveChat extends AppCompatActivity {

    private TextView dummyText;
    private LinearLayout chatKBLayout;
    private EditText chatEditText;
    private ListView chatListLayout;
    private RelativeLayout listLayout, chatEmojiLayout;
    private boolean isReceiver;
    private ImageView videoImageWhenClicked,  chatImgWhenClicked;
    private HorizontalListView videoListview;
    private RelativeLayout videoRelativelayout;
    private VideoHorizontalAdapter videoHorizontalAdapter;
    private DabKickVideoManagerAgent dabKickVideoManagerAgent = DabKickVideoManagerAgent.getInstance();
    private ArrayList<ArrayList> VideosList = new ArrayList<ArrayList>();
    private DabKickVideoDetail mDabKickVideoDetailSingleItem;
    private boolean videoClickShow = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_chat);

        init();

       /* dummyText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatEditText.post(new Runnable() {
                    @Override
                    public void run() {
                        chatEditText.setSelection(chatEditText.getText().length());
                    }
                });
                chatKBLayout.setVisibility(View.VISIBLE);
                chatEditText.requestFocus();
                chatEditText.setCursorVisible(true);

                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(chatEditText, InputMethodManager.SHOW_IMPLICIT);

                RelativeLayout.LayoutParams lps = (RelativeLayout.LayoutParams) listLayout.getLayoutParams();
                lps.addRule(RelativeLayout.ABOVE, 0);
                lps.addRule(RelativeLayout.ABOVE, R.id.kb_layout);
                listLayout.requestLayout();
                listLayout.setLayoutParams(lps);

                View rootView = getWindow().getDecorView().getRootView();
                rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        Rect rect = getVisibleScreenRect();
                        chatKBLayout.setY(rect.height() - chatKBLayout.getHeight());
                    }
                });
            }
        });*/

        chatEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    String text = chatEditText.getText().toString();

                    if (text.trim().length() < 1) {

                    } else {
                        addText(isReceiver, text);
                    }

                }
                return false;
            }
        });

        videoListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Call this method to get the selected video
                mDabKickVideoDetailSingleItem = (DabKickVideoDetail) videoHorizontalAdapter.getItem(position);
                //PlayDabKickVideoActivity is an dabkick library class, use this to get a lib built-in player.
                Intent intent = new Intent(LiveChat.this, PlayDabKickVideoActivity.class);
                intent.putExtra(PlayDabKickVideoActivity.EXTRA_VIDEO_ID, mDabKickVideoDetailSingleItem.videoID);
                intent.putExtra("fromLiveChat", true);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

    }

    public Rect getVisibleScreenRect() {
        Rect rectangle = new Rect();
        //activity.getWindow().getDecorView().getGlobalVisibleRect(rectangle);
        LiveChat.this.getWindow().getDecorView().getWindowVisibleDisplayFrame(rectangle);
        return rectangle;
    }

    public void init() {
        dummyText = (TextView) findViewById(R.id.dummy_text);
        chatKBLayout = (LinearLayout) findViewById(R.id.kb_layout);
        chatEditText = (EditText) findViewById(R.id.chat_text);
        chatListLayout = (ListView) findViewById(R.id.chat_listView);listLayout = (RelativeLayout) findViewById(R.id.list);
        videoImageWhenClicked = (ImageView) findViewById(R.id.video_image_when_clicked);
        videoListview = (HorizontalListView) findViewById(R.id.video_main_listview);
        videoRelativelayout = (RelativeLayout)findViewById(R.id.video_frag);
        chatEmojiLayout = (RelativeLayout) findViewById(R.id.chat_view_emoji);
        chatImgWhenClicked = (ImageView) findViewById(R.id.chat_image_when_clicked);

    }

    public void endBtnClicked(View v) {
        if (v.getId() == R.id.end)
            onBackPressed();
    }

    public void onClickSmile(View view) {
    }

    public void onClickLuv(View view) {
    }

    public void onClickLol(View view) {
    }

    public void onClickWink(View view) {
    }

    public void onClickDude(View view) {
    }

    public void onClickRofl(View view) {
    }

    public void onClickChat(View view) {
        if(chatEmojiLayout.getVisibility() == View.GONE) {
            chatEmojiLayout.setVisibility(View.VISIBLE);
            chatImgWhenClicked.setVisibility(View.VISIBLE);
        }else{
            chatEmojiLayout.setVisibility(View.GONE);
            chatImgWhenClicked.setVisibility(View.GONE);
        }

    }

    public void onClickVideo(View view) {
        videoRelativelayout.setVisibility(View.VISIBLE);
        videoListview.setVisibility(View.VISIBLE);
        videoImageWhenClicked.setVisibility(View.VISIBLE);
        chatEmojiLayout.setVisibility(View.GONE);
        //chatImage.setImageResource(R.drawable.chat_only_gray_nav_v70);
        dabKickVideoManagerAgent.setOnSearchFinishedLoading(new DabKickVideoManagerAgent.OnSearchFinishedLoadingListener() {
            @Override
            public ArrayList onSearchFinishedLoading(boolean success) {
                DabKickGlobalData.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        //call this method with the search term to get the results
                        VideosList = dabKickVideoManagerAgent.getSearchResultByTerm(dabKickVideoManagerAgent.TERM_TELCO);
                        videoHorizontalAdapter = new VideoHorizontalAdapter(LiveChat.this, R.layout.video_view_item, VideosList, false);
                        videoListview.setAdapter(videoHorizontalAdapter);
                        videoHorizontalAdapter.notifyDataSetChanged();
                    }
                });
                return null;
            }
        });
        //Condition check to load the searched videos if not throw alert(logic done in DabKickVideoManagerAgent)
        dabKickVideoManagerAgent.searchVideo(dabKickVideoManagerAgent.TERM_TELCO);

        increasevideoHeight();
        if (!videoClickShow) {
            videoImageWhenClicked.setVisibility(View.VISIBLE);
            videoRelativelayout.setVisibility(View.VISIBLE);
            videoListview.setVisibility(View.VISIBLE);
            videoClickShow = true;
        } else if (videoClickShow) {
            hideVideos();
            videoClickShow = false;

        }
    }

    public void increasevideoHeight() {

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) videoRelativelayout.getLayoutParams();
        Display display = getWindowManager().getDefaultDisplay();
        int height = display.getHeight();
        if (DabKickVideoManagerAgent.getInstance().getVideoSearchResultLists().size() == 1 || DabKickVideoManagerAgent.getInstance().getVideoSearchResultLists().size() == 0) {
            //int ratio = (470 * height) / 888;
            int ratio = (520 * height) / 888;
            lp.topMargin = ratio;
        } else {
            int ratio = (430 * height) / 888;
            lp.topMargin = ratio;
        }
        videoRelativelayout.setLayoutParams(lp);
    }

    public void hideVideos(){
        videoRelativelayout.setVisibility(View.GONE);
        videoListview.setVisibility(View.GONE);
        videoImageWhenClicked.setVisibility(View.GONE);

    }

    public void onClickHideChatKB(View view) {
        chatKBLayout.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        Log.d("ashwini", "onBackPress");
        if (chatKBLayout.getVisibility() == View.VISIBLE)
            chatKBLayout.setVisibility(View.GONE);
        else
            super.onBackPressed();
    }

    public void addText(boolean isReceiver, String text){
       /* adapter.add(new DataProvider(isReceiver, text));
        adapter.notifyDataSetChanged();*/

    }
}