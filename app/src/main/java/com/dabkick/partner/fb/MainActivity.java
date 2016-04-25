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
    static boolean isFBLoggedIn = false;
    String get_id, get_name, get_profile_image;
    private RelativeLayout userDetails;
    private TextView userName;
    private TextView unId;
    private Button regBtn;
    private LoginButton loginButton;
    private CallbackManager callbackManager;

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2016-04-08 23:40:36 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        userDetails = (RelativeLayout) findViewById(R.id.user_details);
        userName = (TextView) findViewById(R.id.user_name);
//        profilePicPath = (CustomEdTxt)findViewById( R.id.pic_path);
        unId = (TextView) findViewById(R.id.un_id);
        regBtn = (Button) findViewById(R.id.reg_btn);
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("user_friends");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.dabkick.partner.fb",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.activity_main);

        findViews();

        RxView.clicks(regBtn).throttleFirst(300, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {

                if (Dabkick.isRegistered(MainActivity.this)) {
                    Intent selectVideo = new Intent(MainActivity.this, SelectVideo.class);
                    selectVideo.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(selectVideo);
                    //finish();
                    return;
                }

                if (!unId.getText().toString().isEmpty() && isFBLoggedIn) {

                    String name = userName.getText().toString();
//                    String profilePic = profilePicPath.getText().toString();
                    String partnerID = unId.getText().toString();

                    UserIdentifier identifier = new UserIdentifier();
                    identifier.userName = name;
                    identifier.userProfilePic = get_profile_image;
                    identifier.uniqueID = partnerID;
                    identifier.email = null;
                    identifier.phoneNumber = null;

                    Log.d("yuan", "user identifier:" + identifier.toString());

                    Dabkick.setOnRegisterFinished(new Dabkick.OnRegisterFinishedListener() {
                        @Override
                        public void onRegistered(boolean b, String s) {

                            Runnable ok = new Runnable() {
                                @Override
                                public void run() {
                                    Intent selectVideo = new Intent(MainActivity.this, SelectVideo.class);
                                    selectVideo.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(selectVideo);
                                    //finish();
                                }
                            };
                            DialogHelper.popupAlertDialog(MainActivity.this, null, "Facebook Partner App is now registered with DabKick.", "ok", ok);
                        }
                    });
                    String packageName = MainActivity.this.getPackageName();
                    Dabkick.register(MainActivity.this, packageName, identifier);
                } else {
                    Toast.makeText(MainActivity.this, "Please login before Register", Toast.LENGTH_SHORT).show();
                }
            }
        });

        userName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
//                    requestFocus(profilePicPath);
                    return true;
                }
                return handled;
            }
        });

        //if facebook is login before, show the username and id
        Dabkick.context = this;
        if (AccessToken.getCurrentAccessToken() != null) {
            Log.d("yuan", "facebook is login");
            userName.setText(PreferenceHandler.getUserName());
            unId.setText(PreferenceHandler.getUniqueID());
            regBtn.setVisibility(View.VISIBLE);

            getFriendList();
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
                        get_id = user.optString("id");
                        get_name = user.optString("name");

                        userName.setText(get_name);
                        unId.setText(get_id);

                        String fbID = get_id;
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
                                            get_profile_image = imageURL;

                                            isFBLoggedIn = true;
                                            regBtn.setVisibility(View.VISIBLE);

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

                    isFBLoggedIn = false;

                    Dabkick.reset();
                    userDetails.setVisibility(View.VISIBLE);
                    userName.setText("");
                    userName.clearFocus();
                    unId.setText("Your facebook ID will appear here");

                    regBtn.setVisibility(View.GONE);
                    loginButton.setVisibility(View.VISIBLE);
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


    private void registerUser() {
        UserIdentifier identifier = new UserIdentifier();
        identifier.userName = get_name;
        identifier.userProfilePic = get_profile_image;
        identifier.uniqueID = get_id;
        identifier.email = null;
        identifier.phoneNumber = null;

        Dabkick.setOnRegisterFinished(new Dabkick.OnRegisterFinishedListener() {
            @Override
            public void onRegistered(boolean b, String s) {

                Runnable ok = new Runnable() {
                    @Override
                    public void run() {
                        Intent selectVideo = new Intent(MainActivity.this, SelectVideo.class);
                        selectVideo.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(selectVideo);
                        //finish();
                    }
                };
                DialogHelper.popupAlertDialog(MainActivity.this, null, "The app is now registered with DabKick with the provided user credentials.", "ok", ok);
            }
        });
        String packageName = MainActivity.this.getPackageName();
        Dabkick.register(MainActivity.this, packageName, identifier);
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
