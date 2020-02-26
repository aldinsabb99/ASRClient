package com.example.asrclient;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import static com.example.asrclient.PreferencesUtility.ID;
import static com.example.asrclient.PreferencesUtility.IPADD;
import static com.example.asrclient.PreferencesUtility.LOGGED_IN_PREF;
import static com.example.asrclient.PreferencesUtility.NAME;
import static com.example.asrclient.PreferencesUtility.PORT;
import static com.example.asrclient.PreferencesUtility.TOKEN;


public class SharedPreferencesClient {

    static SharedPreferences getPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * Set the Login Status
     * @param context
     * @param loggedIn
     */
    public static void setLoggedIn(Context context, boolean loggedIn) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putBoolean(LOGGED_IN_PREF, loggedIn);
        editor.apply();
    }

    public static void setName(Context context, String name){
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putString(NAME, name);
        editor.apply();
    }

    public static void setId(Context context, int id){
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putInt(ID, id);
        editor.apply();
    }

    public static void setToken(Context context, String token){
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putString(TOKEN, token);
        editor.apply();
    }

    public static void setIP(Context context, String ip){
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putString(IPADD, ip);
        editor.apply();
    }

    public static void setPort(Context context, int port){
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putInt(PORT, port);
        editor.apply();
    }


    /**
     * Get the Login Status
     * @param context
     * @return boolean: login status
     */
    public static boolean getLoggedStatus(Context context) {
        return getPreferences(context).getBoolean(LOGGED_IN_PREF, false);
    }

    public static String getName(Context context) {
        return getPreferences(context).getString(NAME,null );
    }

    public static int getId(Context context) {
        return getPreferences(context).getInt(ID, 0);
    }

    public static String getToken(Context context) {
        return getPreferences(context).getString(TOKEN, null);
    }

    public static String getIP(Context context) {
        return getPreferences(context).getString(IPADD, "192.168.1.6");
    }

    public static int getPort(Context context){
        return getPreferences(context).getInt(PORT,8000);
    }


}
