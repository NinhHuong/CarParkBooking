package com.quocngay.carparkbooking.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.google.gson.Gson;
import com.quocngay.carparkbooking.R;
import com.quocngay.carparkbooking.model.CarModel;
import com.quocngay.carparkbooking.model.ParkingInfoHistoryModel;
import com.quocngay.carparkbooking.model.Principal;
import com.quocngay.carparkbooking.other.CarListAdapter;
import com.quocngay.carparkbooking.other.Constant;
import com.quocngay.carparkbooking.other.HistoryListAdapter;
import com.quocngay.carparkbooking.other.SocketIOClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CarManagerActivity extends AppCompatActivity {

    ListView lvCarList;
    CarListAdapter adapter;
    List<CarModel> mCarList;
    String accountid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_manager);
        accountid = new Principal(getApplicationContext()).getId();

        lvCarList = (ListView) findViewById(R.id.lvCarManager);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();

        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);

        ImageButton btnAddNewCar = (ImageButton) findViewById(R.id.btnAddNewCar);
        btnAddNewCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
// Creating alert Dialog with one Button
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(CarManagerActivity.this);

                //AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();

                // Setting Dialog Title
                alertDialog.setTitle(getResources().getString(R.string.add_new_car));

                // Setting Dialog Message
                alertDialog.setMessage(getResources().getString(R.string.booking_license_title));
                final EditText input = new EditText(CarManagerActivity.this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                alertDialog.setView(input);
                //alertDialog.setView(input);

                // Setting Icon to Dialog
                alertDialog.setIcon(R.drawable.ic_directions_car_black_24dp);

                // Setting Positive "Yes" Button
                alertDialog.setPositiveButton(getResources().getString(R.string.dialog_button_add),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int which) {
                                // Write your code here to execute after dialog
                                String newVerhicleNumber = input.getText().toString();
                                SocketIOClient.client.mSocket.emit(Constant.REQUEST_ADD_NEW_CAR,accountid, newVerhicleNumber);
                                SocketIOClient.client.mSocket.on(Constant.RESPONSE_ADD_NEW_CAR, onAddCar);
                            }
                        });
                // Setting Negative "NO" Button
                alertDialog.setNegativeButton(getResources().getString(R.string.dialog_btn_cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Write your code here to execute after dialog
                                dialog.cancel();
                            }
                        });
                // closed

                // Showing Alert Message
                alertDialog.show();
            }
        });
        SocketIOClient.client.mSocket.emit(Constant.REQUEST_FIND_CAR_BY_ACCOUNT_ID, accountid);

        SocketIOClient.client.mSocket.on(Constant.RESPONSE_REMOVE_CAR_BY_ID, onDeleteCar);
        SocketIOClient.client.mSocket.on(Constant.RESPONSE_FIND_CAR_BY_ACCOUNT_ID, onGetCar);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private Emitter.Listener onDeleteCar = new Emitter.Listener() {

        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    try {
                        Boolean result = data.getBoolean(Constant.RESULT);
                        if (result) {
                            Toast.makeText(getApplicationContext(), R.string.had_delete_car, Toast.LENGTH_SHORT).show();
                            SocketIOClient.client.mSocket.emit(Constant.REQUEST_FIND_CAR_BY_ACCOUNT_ID, accountid);
                        } else
                            Toast.makeText(getApplicationContext(),R.string.error_general, Toast.LENGTH_SHORT).show();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private Emitter.Listener onAddCar = new Emitter.Listener() {

        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    try {
                        Boolean result = data.getBoolean(Constant.RESULT);
                        if (result) {
                            Toast.makeText(getApplicationContext(),R.string.add_new_car, Toast.LENGTH_SHORT).show();
                            SocketIOClient.client.mSocket.emit(Constant.REQUEST_FIND_CAR_BY_ACCOUNT_ID, accountid);
                        } else
                            Toast.makeText(getApplicationContext(),R.string.log_cannt_add, Toast.LENGTH_SHORT).show();


                        SocketIOClient.client.mSocket.off(Constant.RESPONSE_REMOVE_CAR_BY_ID);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };


    private Emitter.Listener onGetCar = new Emitter.Listener() {

        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    try {
                        Boolean result = data.getBoolean(Constant.RESULT);
                        if (result) {
                            Log.i("Cars ", data.toString());
                            addDataToList(data);
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.not_have_car, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    void addDataToList(JSONObject data) {
        CarModel p;
        Gson gson = new Gson();

        JSONArray  listJsonCar = null;
        try {
            listJsonCar = data.getJSONArray(Constant.DATA);
            mCarList = new ArrayList<CarModel>();
            for (int i = 0; i <  listJsonCar.length(); i++) {
                p = gson.fromJson( listJsonCar.getJSONObject(i).toString(), CarModel.class);
                Log.i("car number ",p.getVehicleNumber());
                mCarList.add(p);
            }

            adapter = new CarListAdapter(this,getBaseContext(), mCarList);
            lvCarList.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
