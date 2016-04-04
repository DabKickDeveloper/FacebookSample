package com.dabkick.sdk.DabKickVideoAgent;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by SHWETHA RAO on 04-04-2016.
 */
public class DabKickGlobalData implements Serializable {

    public String YOUTUBE_KEY;
    public String YOUTUBE_SEARCH;
    public Map<String, DabKickVideoDetail> GlobalVideoDetails;

    private static DabKickGlobalData instance;
    public static Context appContext;

    public static DabKickGlobalData getInstance(){
        if(instance == null)
            instance = new DabKickGlobalData();
        return instance;
    }
    public DabKickGlobalData() {
        YOUTUBE_KEY = "AIzaSyCEc7GPSj9CRZJkj1r7hQpnCCMVOhhHnYY";
        YOUTUBE_SEARCH = "https://www.googleapis.com/youtube/v3/search?part=snippet&type=video&maxResults=25&q=";
        GlobalVideoDetails = new HashMap<String,DabKickVideoDetail>();
    }

    static public long getFileSize(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            return file.length();
        } else
            return 0;
    }

    static public String getFileNameFromPath(String filePath) {
        return filePath.substring(filePath.lastIndexOf("/") + 1);
    }

    static public void runOnUIThread(Runnable runnable) {
        new Handler(Looper.getMainLooper()).post(runnable);
    }
}

