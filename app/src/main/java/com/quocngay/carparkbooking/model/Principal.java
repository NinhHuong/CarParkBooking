package com.quocngay.carparkbooking.model;

import android.content.Context;
import android.content.SharedPreferences;

import com.quocngay.carparkbooking.other.Constant;

/**
 * Created by Quang Si on 7/21/2017.
 */

public class Principal {

    private String id;
    private String token;
    private Boolean remmember;

    SharedPreferences mSharedPref;
    SharedPreferences.Editor editor;

    public Principal(Context context) {
        mSharedPref = context.getSharedPreferences(Constant.APP_PREF, Context.MODE_PRIVATE);
        editor = mSharedPref.edit();
    }

    public String getId() {
        return mSharedPref.getString(Constant.APP_PREF_ID, "");
    }

    public void setId(String id) {
        this.id = id;
        editor.putString(Constant.APP_PREF_ID, id);
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

    public void setRemmember(Boolean remmember) {
        this.remmember = remmember;
        editor.putBoolean(Constant.APP_PREF_REMEMBER, this.remmember);
        editor.apply();
    }
}
