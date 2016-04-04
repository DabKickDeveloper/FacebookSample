package com.dabkick.sdk.DabKickVideoAgent;

import android.app.Activity;
import android.util.Log;
import android.util.SparseArray;

import com.dabkick.sdk.Web.Webb;
import com.dabkick.sdk.Web.WebbException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import at.huber.youtubeExtractor.YouTubeUriExtractor;
import at.huber.youtubeExtractor.YtFile;

/**
 * Created by SHWETHA RAO on 04-04-2016.
 */
public class DabKickVideoManagerAgent {

    //Fixed Search term
    public final static String TERM_SEARCH_PREFIX = "Search: ";

    public final static String TERM_TELCO = "telco";
    public final static String TERM_FB = "facebook";

    //<searchTerm,list>
    public Map<String, ArrayList> mYouTubeVideoSearchResultLists;

    public Map<String, DabKickVideoDetail> mSelectedVideos;

    private static DabKickVideoManagerAgent instance = null;

    public static DabKickVideoManagerAgent getInstance() {
        if (instance == null) {
            instance = new DabKickVideoManagerAgent();
        }

        return instance;
    }

    public DabKickVideoManagerAgent() {
        mYouTubeVideoSearchResultLists = new LinkedHashMap<String, ArrayList>();
        mSelectedVideos = new HashMap<String, DabKickVideoDetail>();
    }

    public ArrayList<ArrayList> getSearchResultByTerm(String searchTerm) {
        if (searchTerm.startsWith(TERM_SEARCH_PREFIX))
            return mYouTubeVideoSearchResultLists.get(searchTerm);
        return mYouTubeVideoSearchResultLists.get(TERM_SEARCH_PREFIX + searchTerm);
    }

    public ArrayList<String> getVideoSearchTermsList() {
        return new ArrayList<String>(mYouTubeVideoSearchResultLists.keySet());
    }

    public ArrayList<ArrayList<DabKickVideoDetail>> getVideoSearchResultLists() {
        ArrayList<ArrayList<DabKickVideoDetail>> arrayLists = new ArrayList<ArrayList<DabKickVideoDetail>>();

        Collection c = mYouTubeVideoSearchResultLists.values();
        Iterator iterator = c.iterator();
        while (iterator.hasNext()) {
            arrayLists.add((ArrayList<DabKickVideoDetail>) iterator.next());
        }
        return arrayLists;
    }

    public ArrayList<DabKickVideoDetail> getSelectedVideoList() {
        ArrayList<DabKickVideoDetail> arrayLists = new ArrayList<DabKickVideoDetail>();

        Collection c = mSelectedVideos.values();
        Iterator iterator = c.iterator();
        while (iterator.hasNext()) {
            arrayLists.add((DabKickVideoDetail) iterator.next());
        }
        return arrayLists;
    }

    public void clearSelectedItem() {
        mSelectedVideos.clear();
    }

    public void removeFromSelectedList(DabKickVideoDetail dabKickVideoDetail) {
        mSelectedVideos.remove(dabKickVideoDetail.videoID);
    }

    public void addToSelectedList(DabKickVideoDetail dabKickVideoDetail) {
        mSelectedVideos.put(dabKickVideoDetail.videoID, dabKickVideoDetail);
    }

    public DabKickVideoDetail getSelectedVideoByID(String videoID) {
        return mSelectedVideos.get(videoID);
    }

    public boolean isSelected(DabKickVideoDetail detail) {
        return mSelectedVideos.containsKey(detail.videoID);
    }

    public void addSearchResultToList(String searchTerm, ArrayList item) {
        if (searchTerm.startsWith(TERM_SEARCH_PREFIX))
            mYouTubeVideoSearchResultLists.put(searchTerm, item);
        else
            mYouTubeVideoSearchResultLists.put(TERM_SEARCH_PREFIX + searchTerm, item);
    }

    public int getIndexOfSearchTerm(String searchTerm) {
        if (!searchTerm.startsWith(TERM_SEARCH_PREFIX))
            searchTerm = TERM_SEARCH_PREFIX + searchTerm;

        ArrayList<String> list = getVideoSearchTermsList();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).equals(searchTerm))
                return i;
        }
        return -1;
    }

    public void removeSearchResultFromList(String searchTerm) {
        if (searchTerm.startsWith(TERM_SEARCH_PREFIX))
            mYouTubeVideoSearchResultLists.remove(searchTerm);
        else
            mYouTubeVideoSearchResultLists.remove(TERM_SEARCH_PREFIX + searchTerm);
    }

    public boolean isSearchTermLoaded(String searchTerm) {
        if (!searchTerm.startsWith(TERM_SEARCH_PREFIX))
            return mYouTubeVideoSearchResultLists.containsKey(TERM_SEARCH_PREFIX + searchTerm);
        else
            return mYouTubeVideoSearchResultLists.containsKey(searchTerm);

    }

    public void searchVideo(final String searchTerm) {
        if (isSearchTermLoaded(searchTerm)) {
            if (mSearchFinishedLoadingListener != null) {
                mSearchFinishedLoadingListener.onSearchFinishedLoading(true);
                mSearchFinishedLoadingListener = null;
            }
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {

                String encodedSearchTerm = null;
                try {
                    encodedSearchTerm = URLEncoder.encode(searchTerm, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                boolean success = false;
                Webb webb = Webb.create();
                try {
                    String result = webb.get(DabKickGlobalData.getInstance().YOUTUBE_SEARCH + encodedSearchTerm + "&key=" + DabKickGlobalData.getInstance().YOUTUBE_KEY).asString().getBody();
                    try {
                        JSONObject json = new JSONObject(result);
                        JSONArray jsonArray = json.getJSONArray("items");

                        ArrayList<DabKickVideoDetail> resultList = new ArrayList<DabKickVideoDetail>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            JSONObject snippet = jsonObject.getJSONObject("snippet");

                            DabKickVideoDetail item = new DabKickVideoDetail();
                            item.videoID = jsonObject.getJSONObject("id").getString("videoId");
                            item.videoTitle = snippet.getString("title");
                            item.videoDesc = snippet.getString("description");
                            item.videoImageURL = snippet.getJSONObject("thumbnails").getJSONObject("default").getString("url");

                            resultList.add(item);
                            DabKickGlobalData.getInstance().GlobalVideoDetails.put(item.videoID, item);
                        }

                        addSearchResultToList(searchTerm, resultList);
                        success = true;

//                        //Deepak added
//                        //Check if the search results found are not
//                        if (resultList.isEmpty()) {
//                            if (BaseActivity.mCurrentActivity.getClass() == YoutubeVideoActivity.class) {
//                                GlobalHandler.runOnUIThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        Runnable yes = new Runnable() {
//                                            @Override
//                                            public void run() {
//
//                                            }
//                                        };
//                                        DialogHelper.popupAlertDialog(BaseActivity.mCurrentActivity, "Information",
//                                                "No results found", "ok", yes, null, null);
////                                    Toast.makeText(BaseActivity.mCurrentActivity, "No results found", Toast.LENGTH_SHORT).show();
//                                    }
//                                });
//                                success = false;
//                            }
//                            success = false;
//                        } else {
//                            addSearchResultToList(searchTerm, resultList);
//                            success = true;
//                        }
//                        //end

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (WebbException e) {
                    e.printStackTrace();
                }

                if (mSearchFinishedLoadingListener != null) {
                    mSearchFinishedLoadingListener.onSearchFinishedLoading(success);
                    mSearchFinishedLoadingListener = null;
                }

            }
        }).start();

    }

    public void cancelAllLoadingVideoListener()
    {
        if (mFinishedLoadStreamURLListeners != null)
        {
            mFinishedLoadStreamURLListeners.clear();
        }

        if (mFinishedSingleVideoListener != null)
            mFinishedSingleVideoListener.clear();
    }

    public void loadYoutubeStreamURL(final String videoID, boolean forceLoad, Activity activity) {
        if (videoID == null) {
            if (mFinishedLoadStreamURLListeners.containsKey(videoID)) {
                mFinishedLoadStreamURLListeners.get(videoID).OnFinishedLoadStreamURL(false, null);
                mFinishedLoadStreamURLListeners.remove(videoID);
            }
            return;
        }

        DabKickVideoDetail detail = DabKickGlobalData.getInstance().GlobalVideoDetails.get(videoID);
        if (forceLoad == false && detail != null && detail.videoPlayURL != null) {
            if (mFinishedLoadStreamURLListeners.containsKey(videoID))
            {
                mFinishedLoadStreamURLListeners.get(videoID).OnFinishedLoadStreamURL(true, detail.videoPlayURL);
                mFinishedLoadStreamURLListeners.remove(videoID);
            }
            return;
        }

        //20Nov15
        //Updated the youtube stream url extractor library
        //Now it is working on library inside side app not making call to server
        String url = "https://www.youtube.com/watch?v=" + videoID + "&list=FLEYfH4kbq85W_CiOTuSjf8w&feature=mh_lolz";

        //Log.e("deepak", "video raw url " + url);

        YouTubeUriExtractor ytEx = new YouTubeUriExtractor(activity) {
            @Override
            public void onUrisAvailable(String videoId, String videoTitle, SparseArray<YtFile> ytFiles) {
                if (ytFiles != null) {
                    int itag = 18; //YouTube format identifier
                    try {
                        Log.d("yuan_video", "get stream url");
                        String downloadUrl = ytFiles.get(itag).getUrl();
                        if (mFinishedLoadStreamURLListeners.containsKey(videoID)) {
                            mFinishedLoadStreamURLListeners.get(videoID).OnFinishedLoadStreamURL(true, downloadUrl);
                            mFinishedLoadStreamURLListeners.remove(videoID);
                        }
                    } catch (Exception e) {
                        if (mFinishedLoadStreamURLListeners.containsKey(videoID)) {
                            mFinishedLoadStreamURLListeners.get(videoID).OnFinishedLoadStreamURL(false, null);
                            mFinishedLoadStreamURLListeners.remove(videoID);
                        }
                        e.printStackTrace();
                    }
                }
            }
        };
        ytEx.execute(url);
        Log.d("yuan_video", "execute");
    }

    Integer parseYTDuration(String s) {

        char[] dStr = s.toCharArray();

        Integer d = 0;

        for (int i = 0; i < dStr.length; i++) {
            if (Character.isDigit(dStr[i])) {
                String digitStr = "";
                digitStr += dStr[i];
                i++;
                while (Character.isDigit(dStr[i])) {
                    digitStr += dStr[i];
                    i++;
                }

                Integer digit = Integer.valueOf(digitStr);

                if (dStr[i] == 'H')
                    d += digit * 3600;
                else if (dStr[i] == 'M')
                    d += digit * 60;
                else
                    d += digit;
            }
        }

        return d * 1000;
    }

    public String getYoutubeURL(String videoID) {
        if (videoID != null)
            return "http://www.youtube.com/watch?v=" + videoID + "&list=FLEYfH4kbq85W_CiOTuSjf8w&feature=mh_lolz";
        return null;
    }

    public void clearList() {
        mYouTubeVideoSearchResultLists.clear();
        mSelectedVideos.clear();
    }

    public void reset() {
        instance = null;
    }

    OnSearchFinishedLoadingListener mSearchFinishedLoadingListener = null;

    //Listener
    public void setOnSearchFinishedLoading(OnSearchFinishedLoadingListener listener) {
        mSearchFinishedLoadingListener = listener;
    }

    public interface OnSearchFinishedLoadingListener {
        ArrayList onSearchFinishedLoading(boolean success);
    }

    HashMap<String, OnFinishedLoadStreamURLListener> mFinishedLoadStreamURLListeners = new HashMap<>();

    public interface OnFinishedLoadStreamURLListener {
        void OnFinishedLoadStreamURL(boolean success, String fullStreamURL);
    }

    public void addOnFinishedLoadStreamURLListener(String videoID, OnFinishedLoadStreamURLListener listener) {
        mFinishedLoadStreamURLListeners.put(videoID, listener);
    }

    HashMap<String, OnFinishedSingleVideoListener> mFinishedSingleVideoListener;

    public interface OnFinishedSingleVideoListener {
        void OnFinishedSingleVideoListener(boolean success, DabKickVideoDetail detail);
    }

    public void addOnFinishedSingleVideoListener(String videoID, OnFinishedSingleVideoListener listener) {

        if (mFinishedSingleVideoListener == null)
            mFinishedSingleVideoListener = new HashMap<>();

        mFinishedSingleVideoListener.put(videoID, listener);
    }
}
