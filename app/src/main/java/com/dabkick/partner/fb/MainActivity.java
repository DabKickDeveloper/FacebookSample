package com.dabkick.partner.fb;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dabkick.sdk.Dabkick;
import com.dabkick.sdk.Global.DialogHelper;
import com.dabkick.sdk.Global.GlobalHandler;
import com.dabkick.sdk.Global.PreferenceHandler;
import com.dabkick.sdk.Global.UserIdentifier;
import com.dabkick.sdk.Global.UserInfo;
import com.dabkick.sdk.Retrofit.WebClient;
import com.dabkick.sdk.Web.Webb;
import com.dabkick.sdk.Web.WebbException;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestAsyncTask;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.jakewharton.rxbinding.view.RxView;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import rx.functions.Action1;

public class MainActivity extends AppCompatActivity {

    public static ArrayList<UserInfo> friendsList = new ArrayList<>();
    private LoginButton loginButton;
    private CallbackManager callbackManager;

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2016-04-08 23:40:36 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("user_friends");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.activity_main);

        findViews();

        //if facebook is login before, show the username and id
        Dabkick.context = this;
        if (AccessToken.getCurrentAccessToken() != null) {
            Log.d("yuan", "facebook is login");

            getFriendList();

            if (Dabkick.isRegistered(MainActivity.this)) {
                Intent selectVideo = new Intent(MainActivity.this, SelectVideo.class);
                selectVideo.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(selectVideo);
                finish();
                return;
            }

            Dabkick.setOnRegisterFinished(new Dabkick.OnRegisterFinishedListener() {
                @Override
                public void onRegistered(boolean b, String s) {

                    Intent selectVideo = new Intent(MainActivity.this, SelectVideo.class);
                    selectVideo.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(selectVideo);
                    finish();
                }
            });

            UserIdentifier identifier = new UserIdentifier();
            identifier.userName = PreferenceHandler.getUserName();
            identifier.userProfilePic = PreferenceHandler.getUserProfilePic();
            identifier.uniqueID = PreferenceHandler.getUniqueID();
            identifier.email = null;
            identifier.phoneNumber = null;

            String packageName = MainActivity.this.getPackageName();
            Dabkick.register(MainActivity.this, packageName, identifier);
        }

        //Deepak added
        //for facebook login
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            private ProfileTracker mProfileTracker;

            @Override
            public void onSuccess(final LoginResult loginResult) {

                final AccessToken accessToken = loginResult.getAccessToken();
                GraphRequestAsyncTask request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject user, GraphResponse graphResponse) {
                        final String fbID = user.optString("id");
                        final String userName = user.optString("name");

                        String url = "/" + fbID;

                        Bundle bundle = new Bundle();
                        bundle.putString("fields", "picture");
                        new GraphRequest(
                                AccessToken.getCurrentAccessToken(),
                                url,
                                bundle,
                                HttpMethod.GET,
                                new GraphRequest.Callback() {
                                    public void onCompleted(GraphResponse response) {
                                        try {
                                            String imageURL = response.getJSONObject().getJSONObject("picture").getJSONObject("data").getString("url");

                                            Dabkick.setOnRegisterFinished(new Dabkick.OnRegisterFinishedListener() {
                                                @Override
                                                public void onRegistered(boolean b, String s) {

                                                    Intent selectVideo = new Intent(MainActivity.this, SelectVideo.class);
                                                    selectVideo.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    startActivity(selectVideo);
                                                    finish();
                                                }
                                            });

                                            UserIdentifier identifier = new UserIdentifier();
                                            identifier.userName = userName;
                                            identifier.userProfilePic = imageURL;
                                            identifier.uniqueID = fbID;
                                            identifier.email = null;
                                            identifier.phoneNumber = null;

                                            String packageName = MainActivity.this.getPackageName();
                                            Dabkick.register(MainActivity.this, packageName, identifier);


                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        Log.d("yuan", "profile pic. response:" + response.toString());
                                    }
                                }
                        ).executeAsync();

                    }
                }).executeAsync();

                getFriendList();

            }

            @Override
            public void onCancel() {
                Log.d("yuan", "cancel");
            }

            @Override
            public void onError(FacebookException e) {
                Log.d("yuan", "error:" + e.toString());
            }
        });

        //Deepak added
        //To know whether user logged in or logged out.
        AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                if (currentAccessToken == null) {

                    Dabkick.unregister(MainActivity.this);
                }
            }
        };
    }

    void getFriendList() {
        GraphRequestAsyncTask graphRequestAsyncTask = new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me/friends",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        try {
                            JSONArray rawName = response.getJSONObject().getJSONArray("data");
                            ArrayList<UserInfo> resultList = new ArrayList();
                            Log.e("deepak", "rawdata: " + rawName.toString());
                            Log.e("deepak", "length: " + rawName.length());
                            for (int i = 0; i < rawName.length(); i++) {
                                JSONObject jsonObject = rawName.getJSONObject(i);
                                UserInfo item = new UserInfo();
                                item.setName(jsonObject.getString("name"));
                                item.setUniqueID(jsonObject.getString("id"));
                                resultList.add(item);
                            }

                            addFriendsToList(resultList);

                            //get friend's profile picture
                            for (final UserInfo userInfo : resultList) {
                                String fbID = userInfo.getUniqueID();
                                //https://graph.facebook.com/4?fields=picture
                                String url = "/" + fbID;

                                Bundle bundle = new Bundle();
                                bundle.putString("fields", "picture");

                                new GraphRequest(
                                        AccessToken.getCurrentAccessToken(),
                                        url,
                                        bundle,
                                        HttpMethod.GET,
                                        new GraphRequest.Callback() {
                                            public void onCompleted(GraphResponse response) {
                                                try {
                                                    String imageURL = response.getJSONObject().getJSONObject("picture").getJSONObject("data").getString("url");
                                                    userInfo.setImageURL(imageURL);

                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                Log.d("yuan", "profile pic. response:" + response.toString());
                                            }
                                        }
                                ).executeAsync();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }).executeAsync();
    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    void requestFocus(final EditText editText) {
        editText.post(new Runnable() {
            @Override
            public void run() {
                editText.requestFocus();
                editText.setSelection(editText.getText().length());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void addFriendsToList(ArrayList<UserInfo> list) {
        friendsList = list;
    }
}
