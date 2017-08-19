package com.quocngay.carparkbooking.activity;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.quocngay.carparkbooking.R;
import com.quocngay.carparkbooking.other.Constant;
import com.quocngay.carparkbooking.other.SocketIOClient;

import org.json.JSONException;
import org.json.JSONObject;

public class AddGarageActivity extends GeneralActivity {

    private EditText edtName, edtAddress,edtNumberSlot,edtLongitude, edtLatitude;
    String accountAdminId;
    private Button addNewGarage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_garage);

        accountAdminId = getIntent().getStringExtra(Constant.ACCOUNT_ADMIN_ID);
        initToolbar(R.id.toolbar, false, true);

        edtName = (EditText)findViewById(R.id.edtName);

        edtAddress = (EditText)findViewById(R.id.edtAddress);
        edtNumberSlot = (EditText)findViewById(R.id.edtTotalSlot);
        edtLongitude = (EditText)findViewById(R.id.edtLocationX);
        edtLatitude = (EditText)findViewById(R.id.edtLocationY);
        addNewGarage = (Button) findViewById(R.id.btnAddNewGarage);
        addNewGarage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewGarage();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SocketIOClient.client.mSocket.emit(Constant.REQUEST_REMOVE_ACCOUNT_BY_ID, accountAdminId);
        SocketIOClient.client.mSocket.on(Constant.RESPONSE_REMOVE_ACCOUNT_BY_ID, onRemoveAccount);
    }


    private void createNewGarage(){
        String name,address,timeStart,timeEnd,totalSlot,longitude,latitude;
        name = edtName.getText().toString();
        address = edtAddress.getText().toString();
        timeStart = "00:00:00";
        timeEnd = "00:00:00";
        totalSlot = edtNumberSlot.getText().toString();
        longitude = edtLongitude.getText().toString();
        latitude = edtLatitude.getText().toString();

        SocketIOClient.client.mSocket.emit(Constant.REQUEST_ADD_NEW_GARAGE,
                name,
                address,
                totalSlot,
                0,
                longitude,
                latitude,
                accountAdminId,
                timeStart,
                timeEnd,
                0);
        SocketIOClient.client.mSocket.on(Constant.RESPONSE_ADD_NEW_GARAGE, onNewMessageResultRegistNewAccount);
    }

    private Emitter.Listener onNewMessageResultRegistNewAccount = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    Log.i("Data", data.toString());
                    try {
                        boolean res = data.getBoolean(Constant.RESULT);
                        if (res) {
                            Toast.makeText(getApplicationContext(),
                                    getResources().getString(R.string.message_regist_garage),
                                    Toast.LENGTH_SHORT).show();
                            SocketIOClient.client.mSocket.off(Constant.RESPONSE_ADD_NEW_GARAGE);
                            finish();
                        } else if (data.getString(Constant.MESSAGE).equals("email_registered")) {
                            Toast.makeText(getApplicationContext(), getResources().
                                            getString(R.string.error_general),
                                    Toast.LENGTH_SHORT).show();

                        }
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
                    Log.i("Data", data.toString());
                    try {
                        boolean res = data.getBoolean(Constant.RESULT);
                        if (res) {
                            Toast.makeText(getApplicationContext(),
                                    getResources().getString(R.string.message_cancel_regist_garage),
                                    Toast.LENGTH_SHORT).show();
                            SocketIOClient.client.mSocket.off(Constant.RESPONSE_REMOVE_ACCOUNT_BY_ID);
                            finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };
}
