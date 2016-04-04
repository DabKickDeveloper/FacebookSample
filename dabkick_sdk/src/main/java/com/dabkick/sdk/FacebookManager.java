package com.dabkick.sdk;

import android.app.Activity;
import android.util.Log;

import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SHWETHA RAO on 04-04-2016.
 */
public final class FacebookManager {

    static ArrayList<JSONObject> friendNames = new ArrayList<JSONObject>();
    public static List<String> friends = new ArrayList<>();

    public static void getFBfriends(LoginResult loginResult, final Activity mActivity){

        new GraphRequest(
                loginResult.getAccessToken(),
                "/me/taggable_friends",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {

                        parseFriends(response);
                        iterateFriends(response, mActivity);
                    }
                }
        ).executeAsync();
    }

    static void iterateFriends(GraphResponse response,final Activity mActivity) {
        GraphRequest next = response.getRequestForPagedResults(GraphResponse.PagingDirection.NEXT);
        if (next != null) {
            Log.e("Lib", "NOT NULL");
            next.setCallback(new GraphRequest.Callback() {
                @Override
                public void onCompleted(GraphResponse response) {
                    parseFriends(response);
                    iterateFriends(response, mActivity);
                }
            });

            GraphRequest.executeBatchAsync(next);
        }else{
            Log.e("Lib", "NULL");
            try {

                /*String message = "";
                for (JSONObject item : friendNames) {

                    message =  message + "\n" + item.getString("name");
                }

                new AlertDialog.Builder(mActivity)
                        .setTitle("Friends: "+ friendNames.size())
                        .setMessage(message)
                        .show();*/

                for (JSONObject item : friendNames) {

                    friends.add(item.getString("name"));
                }

            }catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    static void parseFriends(GraphResponse response){

        try {

            JSONArray rawFriendsData = response.getJSONObject().getJSONArray("data");

            for (int j = 0; j < rawFriendsData.length(); j++) {

                JSONObject photo = new JSONObject();
                photo.put("name", ((JSONObject) rawFriendsData.get(j)).get("name"));

                boolean isUnique = true;

                for (JSONObject item : friendNames) {
                    if (item.toString().equals(photo.toString())) {
                        isUnique = false;
                        break;
                    }
                }

                if (isUnique) friendNames.add(photo);

            }
        }catch (JSONException e) {
            e.printStackTrace();
        }

    }
}