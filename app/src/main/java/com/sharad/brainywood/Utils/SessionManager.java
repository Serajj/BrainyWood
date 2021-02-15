package com.sharad.brainywood.Utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;


import com.sharad.brainywood.LoginActivity;
import com.sharad.brainywood.MainActivity;
import com.sharad.brainywood.ProfileActivity;

import java.util.HashMap;

public class SessionManager {

    SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;
    private Context context;

    int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "LOGIN";
    private static final String LOGIN = "IS_LOGIN";
    public static final String NAME = "NAME";
    public static final String EMAIL = "EMAIL";
    public static final String ID = "ID";
    public static final String USERNAME = "USERNAME";

    public SessionManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = sharedPreferences.edit();
    }

    public void createSession(String name, String email, String id, String username) {
        editor.putBoolean(LOGIN, true);
        editor.putString(NAME, name);
        editor.putString(EMAIL, email);
        editor.putString(ID, id);
        editor.putString(USERNAME, username);

        editor.apply();

    }

    public boolean isLogin() {
        return sharedPreferences.getBoolean(LOGIN, false);
    }

    public void checkLogin() {
        if (!this.isLogin()) {
            Intent i = new Intent(context, LoginActivity.class);
            context.startActivity(i);
            ((MainActivity) context).finish();
        }
    }

    public void checkLoginProfile() {
        if (!this.isLogin()) {
            Intent i = new Intent(context, LoginActivity.class);
            context.startActivity(i);
            ((ProfileActivity) context).finish();
        }
    }

    public void checkLogin(Context context) {
        if (!this.isLogin()) {
            Intent i = new Intent(context, LoginActivity.class);
            context.startActivity(i);
            ((MainActivity) context).finish();
        }
    }

    public HashMap<String, String> getUserDetail() {

        HashMap<String, String> user = new HashMap<>();
        user.put(NAME, sharedPreferences.getString(NAME, null));
        user.put(EMAIL, sharedPreferences.getString(EMAIL, null));
        user.put(ID, sharedPreferences.getString(ID, null));
        user.put(USERNAME, sharedPreferences.getString(USERNAME, null));



        return user;
    }

    public void logout() {
        editor.clear();
        editor.commit();

           Intent i = new Intent(context, LoginActivity.class);
           context.startActivity(i);
           ((MainActivity) context).finish();
    }

    public void logoutFromProfileActivity() {
        editor.clear();
        editor.commit();

        Intent i = new Intent(context, LoginActivity.class);
        context.startActivity(i);
        ((ProfileActivity) context).finish();
    }
}
