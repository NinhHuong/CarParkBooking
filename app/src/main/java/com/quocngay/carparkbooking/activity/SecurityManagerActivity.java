package com.quocngay.carparkbooking.activity;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.google.gson.Gson;
import com.quocngay.carparkbooking.R;
import com.quocngay.carparkbooking.model.AccountModel;
import com.quocngay.carparkbooking.model.GarageModel;
import com.quocngay.carparkbooking.model.LocalData;
import com.quocngay.carparkbooking.other.AccountListAdapter;
import com.quocngay.carparkbooking.other.Constant;
import com.quocngay.carparkbooking.other.SocketIOClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SecurityManagerActivity extends AppCompatActivity {

    private LocalData localData;
    private ListView lvSecurity;
    AccountListAdapter adapter;
    List<AccountModel> mAccountList;
    String accountID;
    String garageID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_manager);

        localData = new LocalData(getApplicationContext());
        accountID = localData.getId();
        lvSecurity = (ListView) findViewById(R.id.lvSecurity);
        garageID = localData.getGarageID();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();

        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);

        SocketIOClient.client.mSocket.emit(Constant.REQUEST_ALL_SECURITY, accountID);

        SocketIOClient.client.mSocket.on(Constant.RESPONSE_ALL_SECURITY, onGetAccountSecurity);
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

        JSONArray listJsonCar = null;
        try {
            listJsonCar = data.getJSONArray(Constant.DATA);
            mAccountList = new ArrayList<AccountModel>();
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
