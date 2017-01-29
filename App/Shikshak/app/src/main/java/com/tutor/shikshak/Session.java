package com.tutor.shikshak;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Lokesh on 29-Jan-17.
 */

public class Session {

    Session(){
        // blank constructor
    }

    private SharedPreferences prefs;
    private static String PREF_NAME = "prefs";

    /*public Session(Context cntx) {
        prefs = PreferenceManager.getDefaultSharedPreferences(cntx);
    }

    public void setusename(String usename) {
        prefs.edit().putString("usename", usename).commit();
    }

    public String getusename() {
        String usename = prefs.getString("usename","");
        return usename;
    }*/

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static String getUsername(Context context) {
        return getPrefs(context).getString("username_key", "default_username");
    }

    public static void setUsername(Context context, String input) {
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putString("username_key", input);
        editor.commit();
    }

}

