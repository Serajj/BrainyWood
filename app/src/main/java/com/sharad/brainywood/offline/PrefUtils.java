package com.sharad.brainywood.offline;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import static com.sharad.brainywood.offline.Constants.SECRET_KEY;


public class PrefUtils {

    public static final PrefUtils prefUtils = new PrefUtils();
    public static SharedPreferences myPrefs = null;

    public static PrefUtils getInstance(Context context) {
        if (null == myPrefs)
            myPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefUtils;
    }

    public void saveSecretKey(String value) {
        SharedPreferences.Editor editor = myPrefs.edit();
        editor.putString(SECRET_KEY, value);
        editor.commit();
    }

    public void encStatus(boolean value) {
        SharedPreferences.Editor editor = myPrefs.edit();
        editor.putBoolean("mystatus", value);
        editor.commit();
    }
    public void dcrptstatus(boolean value) {
        SharedPreferences.Editor editor = myPrefs.edit();
        editor.putBoolean("myestring", value);
        editor.commit();
    }

    public String getSecretKey() {
        return myPrefs.getString(SECRET_KEY, null);
    }
    public boolean getDcryStatus() {
        return myPrefs.getBoolean("myestring", true);
    }
    public boolean getEnc() {
        return myPrefs.getBoolean("mystatus", true);
    }
}
