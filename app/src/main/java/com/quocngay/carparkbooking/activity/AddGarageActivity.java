package com.quocngay.carparkbooking.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class AddGarageActivity extends AppCompatActivity {
    private EditText edtName, edtTimeStart,edtTimeEnd,edtAddress,edtNumberSlot,edtLongitude, edtLatitude;
    String accountID;
    private Button addNewGarage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_garage);

        accountID = getIntent().getStringExtra(Constant.ACCOUNT_ID);

        edtName = (EditText)findViewById(R.id.edtName);
        edtTimeStart = (EditText)findViewById(R.id.edtTimeStart);
        edtTimeEnd = (EditText)findViewById(R.id.edtTimeEnd);
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

    private void createNewGarage(){
        String name,address,timeStart,timeEnd,totalSlot,longitude,latitude;
        name = edtName.getText().toString();
        address = edtAddress.getText().toString();
        timeStart = edtTimeStart.getText().toString();
        timeEnd = edtTimeEnd.getText().toString();
        totalSlot = edtNumberSlot.getText().toString();
        longitude = edtLongitude.getText().toString();
        latitude = edtLatitude.getText().toString();

        SocketIOClient.client.mSocket.emit(Constant.REQUEST_ADD_NEW_GARAGE,
                name,
                address,
                totalSlot,
                0,
                latitude,
                longitude,
                accountID,
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
                                    getResources().getString(R.string.register_successfull),
                                    Toast.LENGTH_SHORT).show();
                            SocketIOClient.client.mSocket.off(Constant.RESPONSE_CREATE_ACCOUNT_SECURITY);
                            finish();
                        } else if (data.getString(Constant.MESSAGE).equals("email_registered")) {
                            Toast.makeText(getApplicationContext(), getResources().
                                            getString(R.string.error_server_email_registered),
                                    Toast.LENGTH_SHORT).show();

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };
}
