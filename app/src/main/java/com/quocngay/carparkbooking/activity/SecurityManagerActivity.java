package com.quocngay.carparkbooking.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.google.gson.Gson;
import com.quocngay.carparkbooking.R;
import com.quocngay.carparkbooking.adapter.AccountListAdapter;
import com.quocngay.carparkbooking.model.AccountModel;
import com.quocngay.carparkbooking.model.LocalData;
import com.quocngay.carparkbooking.other.Constant;
import com.quocngay.carparkbooking.other.SocketIOClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SecurityManagerActivity extends GeneralActivity {

    private ExpandableListView lvSecurity;
    AccountListAdapter adapter;
    List<AccountModel> mAccountList;
    String accountID;
    String garageID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_manager);

        initToolbar(R.id.toolbar, true, true);

        LocalData localData = new LocalData(getApplicationContext());
        accountID = localData.getId();
        lvSecurity = (ExpandableListView) findViewById(R.id.lvSecurity);
        garageID = localData.getGarageID();

        SocketIOClient.client.mSocket.emit(Constant.REQUEST_ALL_SECURITY, garageID);

        SocketIOClient.client.mSocket.on(Constant.RESPONSE_ALL_SECURITY, onGetAccountSecurity);
        SocketIOClient.client.mSocket.on(Constant.RESPONSE_REMOVE_SECURITY, onRemoveAccount);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private Emitter.Listener onGetAccountSecurity = new Emitter.Listener() {

        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    try {
                        Boolean result = data.getBoolean(Constant.RESULT);
                        if (result) {
                            Log.i("Security account ", data.toString());
                            addDataToList(data);
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.not_have_security, Toast.LENGTH_SHORT).show();
                        }

                        SocketIOClient.client.mSocket.off(Constant.RESPONSE_ALL_SECURITY);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private Emitter.Listener onRemoveAccount = new Emitter.Listener() {

        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    try {
                        Boolean result = data.getBoolean(Constant.RESULT);
                        if (result) {
                            SocketIOClient.client.mSocket.emit(Constant.REQUEST_ALL_SECURITY, garageID);

                            SocketIOClient.client.mSocket.on(Constant.RESPONSE_ALL_SECURITY, onGetAccountSecurity);
                        } else
                            Toast.makeText(getApplicationContext(), R.string.error_general, Toast.LENGTH_SHORT).show();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    void addDataToList(JSONObject data) {
        AccountModel p;
        Gson gson = new Gson();

        JSONArray listJsonCar;
        try {
            listJsonCar = data.getJSONArray(Constant.DATA);
            mAccountList = new ArrayList<>();
            for (int i = 0; i < listJsonCar.length(); i++) {
                p = gson.fromJson(listJsonCar.getJSONObject(i).toString(), AccountModel.class);
                Log.i("security email ", p.getEmail());
                mAccountList.add(p);
            }
            adapter = new AccountListAdapter(this, getBaseContext(), mAccountList);
            lvSecurity.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
