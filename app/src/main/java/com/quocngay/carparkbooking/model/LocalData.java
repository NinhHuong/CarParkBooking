package com.quocngay.carparkbooking.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.quocngay.carparkbooking.other.Constant;

/**
 * Created by Quang Si on 7/21/2017.
 */

public class LocalData {
    private String id;
    private String email;
    private String token;
    private String firebaseToken;
    private Boolean remmember;
    private String role;
    private boolean isLogin;
    private String garageId;

    private SharedPreferences mSharedPref;
    private SharedPreferences.Editor editor;

    @SuppressLint("CommitPrefEdits")
    public LocalData(Context context) {
        mSharedPref = context.getSharedPreferences(Constant.APP_PREF, Context.MODE_PRIVATE);
        editor = mSharedPref.edit();
    }

    public void clearData() {
        setId("");
        setGarageID("");
        setEmail("");
        setRemmember(false);
        setIsLogin(false);
        setRole("");
        setToken("");
    }

    public String getId() {
        return mSharedPref.getString(Constant.APP_PREF_ID, "");
    }

    public void setId(String id) {
        this.id = id;
        editor.putString(Constant.APP_PREF_ID, id);
        editor.apply();
    }

    public String getEmail() {
        return mSharedPref.getString(Constant.APP_PREF_EMAIL, "");
    }

    public void setEmail(String email) {
        this.email = email;
        editor.putString(Constant.APP_PREF_EMAIL, email);
        editor.apply();
    }

    public String getToken() {
        return mSharedPref.getString(Constant.APP_PREF_TOKEN, "");
    }

    public void setToken(String token) {
        editor.putString(Constant.APP_PREF_TOKEN, token);
        editor.apply();
    }

    public Boolean getRemmember() {
        return mSharedPref.getBoolean(Constant.APP_PREF_REMEMBER, false);
    }

    public boolean getIsLogin() {
        return mSharedPref.getBoolean(Constant.APP_PREF_IS_LOGIN, false);
    }

    public String getGarageID() {
        return mSharedPref.getString(Constant.APP_PREF_GARAGE_ID, "");
    }

    public String getRole() {
        return mSharedPref.getString(Constant.APP_PREF_ROLE, "");
    }

    public void setRole(String role) {
        editor.putString(Constant.APP_PREF_ROLE, role);
        editor.apply();
    }

    public void setRemmember(Boolean remmember) {
        this.remmember = remmember;
        editor.putBoolean(Constant.APP_PREF_REMEMBER, this.remmember);
        editor.apply();
    }

    public void setIsLogin(boolean isLogin) {
        editor.putBoolean(Constant.APP_PREF_IS_LOGIN, isLogin);
        editor.apply();
    }

    public void setGarageID(String garageID) {
        editor.putString(Constant.APP_PREF_GARAGE_ID, garageID);
        editor.apply();
    }

    public String getFirebaseToken() {
        return mSharedPref.getString(Constant.APP_PREF_FIREBASE_REMEMBER, "");
    }

    public void setFirebaseToken(String firebaseToken) {
        this.firebaseToken = firebaseToken;
        editor.putString(Constant.APP_PREF_FIREBASE_REMEMBER, this.firebaseToken);
        editor.apply();
    }
}
