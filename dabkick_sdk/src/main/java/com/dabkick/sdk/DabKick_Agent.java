package com.dabkick.sdk;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginResult;

/**
 * Created by SHWETHA RAO on 04-04-2016.
 */
public class DabKick_Agent {

    public static Activity mActivity;
    public static boolean fbreg = false;
    public static TextView statusMsg, displayUserInfo;
    public static String statusMessage = "", userInfo = "";

    public static void DK_Register(String registrationKey, LoginResult fbLoginResult, String email, String phNum, String uniqueID, final Activity mActivity){

        DabKick_Agent.mActivity = mActivity;
        Log.d("Dabkick lib", "developer key: "+registrationKey);
        Log.d("Dabkick lib", "FB login: "+fbLoginResult);
        Log.d("Dabkick lib", "Email: "+email);
        Log.d("Dabkick lib", "Ph num: "+phNum);
        Log.d("Dabkick lib", "unique key: "+uniqueID);

        statusMessage = statusMessage + "\n" + "Dabkick lib developer key: "+registrationKey;
        statusMessage = statusMessage + "\n" + "Dabkick lib FB login: "+fbLoginResult;
        statusMessage = statusMessage + "\n" + "Dabkick lib Email: "+email;
        statusMessage = statusMessage + "\n" + "Dabkick lib Ph num: "+phNum;
        statusMessage = statusMessage + "\n" + "Dabkick lib unique key: "+uniqueID;

        //Make Server call and get the user id
        //Assuming userID for now

        int userID = 43310;
        userInfo = userInfo + "\n" + "UserID: " + userID;
        Log.e("Dabkick lib", "user id: "+userID);

        if(userID <= 0){

            statusMessage = statusMessage + "\n" + "User does not exist on our server.";

            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mActivity, "User does not exist on our server.", Toast.LENGTH_SHORT).show();
                }
            });

            return;

        }

        //Set userID in preference handler
        PreferenceHandler.setUserID(mActivity,userID);

        //XMPP Connect to be done

        //Check if FB login is provided
        //If provided fetch FB friends
        //Else fetch address book

        if(fbLoginResult != null) {
            FacebookManager.getFBfriends(fbLoginResult, mActivity);
            fbreg = true;
        }
    }

    public static void displayStatusMessages(TextView tv){

        if(tv != null) {
            statusMsg = tv;
            statusMsg.setText(statusMessage);
        }
    }

    public static void checkInStatusMsg(String msg){

        if(statusMsg != null) {

            statusMessage = statusMessage + "\n" + msg;

            statusMsg.setText(statusMessage);
        }
    }

    public static void displayUserInfo(TextView tv){

        if(tv != null) {
            displayUserInfo = tv;
            displayUserInfo.setText(userInfo);
        }

    }

    public static void watchWithFriends(Activity mActivity){

        Intent wwf = new Intent(mActivity, WatchWithFriends.class);
        mActivity.startActivity(wwf);
    }

    public static void goToWaitingScreen(String name){
        Intent goToLiveSession = new Intent(mActivity, GoToLiveSessionIntermediate.class);
        goToLiveSession.putExtra("friendName", name);
        mActivity.startActivity(goToLiveSession);
    }

    public static void goToLiveSession(Activity activity){
        activity.startActivity(new Intent(activity, LiveChat.class));
    }
}
