package com.dabkick.sdk.DabKickVideoAgent;

import android.app.Activity;
import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by SHWETHA RAO on 04-04-2016.
 */
public class DabKickVideoDetail {

    public String videoURL;
    public String videoID;
    public String videoImageURL;
    public Bitmap videoImage;
    public String videoTitle;
    public String videoDesc ="";
    public String videoPlayURL;
    public int refreshTimeStamp = 0;
    public boolean loaded = false;
    public boolean loadRelatedVideos = false;
    public Date linkFormationTime;
    public double videoDuration;
    public boolean isFavorate = false;
    public boolean isSelected = false;

    public boolean isVideoSelectedToPlay = false;

    public ArrayList<DabKickVideoDetail> mRelatedVideos = new ArrayList<DabKickVideoDetail>();

    public DabKickVideoDetail() {
    }

//    public DabKickVideoDetail(StreamVideoMessage message)
//    {
//        videoImageURL = message.getUrl();
//        videoURL = message.videoURL;
//        videoID = message.videoID;
//        videoTitle = message.videoTitle;
//        videoDesc = message.videoDesc;
//        isFavorate = message.isFavorate;
//
//    }
//
//    public DabKickVideoDetail(MusicMessage message)
//    {
//        videoImageURL = message.getUrl();
//        videoURL = message.musicURL;
//        videoID = message.musicID;
//        videoTitle = message.musicTitle;
//        videoDesc = message.musicDesc;
//    }

    public DabKickVideoDetail getRelatedVideoByID(String id) {
        for (DabKickVideoDetail dabKickVideoDetail : mRelatedVideos) {
            if (dabKickVideoDetail.videoID.equals(id))
                return dabKickVideoDetail;
        }
        return null;
    }

    public ArrayList<DabKickVideoDetail> getRelativeVideos() {
        return mRelatedVideos;
    }

    public boolean isRelatedVideoLoaded() {
        return loadRelatedVideos;
    }

    public boolean isStreamURLExpired() {
        if (linkFormationTime == null)
            return true;

        Date now = new Date();
        long difference = now.getTime() - linkFormationTime.getTime();
        int days = (int) (difference / (1000 * 60 * 60 * 24));
        int hours = (int) ((difference - (1000 * 60 * 60 * 24 * days)) / (1000 * 60 * 60));
        int min = (int) (difference - (1000 * 60 * 60 * 24 * days) - (1000 * 60 * 60 * hours)) / (1000 * 60);

        return min > 60;
    }

    public boolean isStreamURLLoaded()
    {
        if (!isStreamURLExpired() && videoPlayURL != null)
            return true;
        else
            return false;
    }

    public void LoadStreamURL(final Activity activity) {
        if (!isStreamURLExpired()) {
            if (mFinishedLoadStreamURLListener != null) {
                mFinishedLoadStreamURLListener.OnFinishedLoadStreamURL(videoPlayURL);
            }
            return;
        }

        if (videoPlayURL == null && loaded) {
            if (mFinishedLoadStreamURLListener != null) {
                mFinishedLoadStreamURLListener.OnFinishedLoadStreamURL(videoPlayURL);
            }
            return;
        }


        new Thread(new Runnable() {
            @Override
            public void run() {
                DabKickVideoManagerAgent.getInstance().addOnFinishedLoadStreamURLListener(videoID, new DabKickVideoManagerAgent.OnFinishedLoadStreamURLListener() {
                    @Override
                    public void OnFinishedLoadStreamURL(boolean success, String fullStreamURL) {

                        loaded = true;
                        linkFormationTime = new Date();

                        //Fail due to http connection issue
                        if (success && fullStreamURL == null) {
                            loaded = false;
                            linkFormationTime = null;
                        }

                        videoPlayURL = fullStreamURL;

                        if (mFinishedLoadStreamURLListener != null)
                            mFinishedLoadStreamURLListener.OnFinishedLoadStreamURL(fullStreamURL);
                    }
                });
                DabKickVideoManagerAgent.getInstance().loadYoutubeStreamURL(videoID, true, activity);

            }
        }).start();
    }

    OnFinishedLoadStreamURLListener mFinishedLoadStreamURLListener;

    public interface OnFinishedLoadStreamURLListener {
        void OnFinishedLoadStreamURL(String fullStreamURL);
    }

    public void setOnFinishedLoadStreamURLListener(OnFinishedLoadStreamURLListener listener) {
        mFinishedLoadStreamURLListener = listener;
    }

    OnRelatedFinishedLoadingListener mRelatedFinishedLoadingListener;

    public interface OnRelatedFinishedLoadingListener {
        void OnRelatedFinishedLoading(boolean success);
    }

    public void setOnRelatedFinishedLoadingListener(OnRelatedFinishedLoadingListener listener) {
        mRelatedFinishedLoadingListener = listener;
    }

}
