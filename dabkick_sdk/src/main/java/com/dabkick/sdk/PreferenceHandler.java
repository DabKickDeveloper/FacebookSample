package com.dabkick.sdk;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by SHWETHA RAO on 04-04-2016.
 */
public class PreferenceHandler {

    public static final String USER_DETAIL = "USER_DETAIL";

    static public SharedPreferences.Editor getUserDetailEditor(Context c) {
        SharedPreferences preferences = c.getSharedPreferences(USER_DETAIL, c.MODE_PRIVATE);
        return preferences.edit();
    }

    static public void setUserID(Context c, int id) {
        SharedPreferences.Editor editor = getUserDetailEditor(c);
        editor.putString("userid", String.valueOf(id));
        editor.commit();
    }

    static public int getUserID(Context c) {
        SharedPreferences preferences = c.getSharedPreferences(USER_DETAIL, c.MODE_PRIVATE);
        return Integer.parseInt(preferences.getString("userid", "0"));
    }
}

