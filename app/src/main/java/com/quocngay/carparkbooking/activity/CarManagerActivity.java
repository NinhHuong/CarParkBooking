package com.quocngay.carparkbooking.activity;

import android.app.Dialog;
import android.content.DialogInterface;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.google.gson.Gson;
import com.quocngay.carparkbooking.R;
import com.quocngay.carparkbooking.model.CarModel;
import com.quocngay.carparkbooking.model.LocalData;
import com.quocngay.carparkbooking.other.CarListAdapter;
import com.quocngay.carparkbooking.other.Constant;
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
    private Dialog dalAddLinense;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_manager);
        accountid = new LocalData(getApplicationContext()).getId();

        lvCarList = (ListView) findViewById(R.id.lvCarManager);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();

        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);

        ImageView btnAddNewCar = (ImageView) findViewById(R.id.btnAddNewCar);
        btnAddNewCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCarList.size() >= 5){
                    Toast.makeText(CarManagerActivity.this,
                            getResources().getString(R.string.error_add_license_limit),
                            Toast.LENGTH_SHORT).show();
                }else {
                    initAddLicenseDialog();
                }
            }
        });
        SocketIOClient.client.mSocket.emit(Constant.REQUEST_FIND_CAR_BY_ACCOUNT_ID, accountid);

        SocketIOClient.client.mSocket.on(Constant.RESPONSE_REMOVE_CAR_BY_ID, onDeleteCar);
        SocketIOClient.client.mSocket.on(Constant.RESPONSE_FIND_CAR_BY_ACCOUNT_ID, onGetCar);
    }

    private void initAddLicenseDialog() {
        dalAddLinense = new Dialog(this);
        dalAddLinense.setTitle(R.string.dialog_add_car_title);
        dalAddLinense.setContentView(R.layout.dialog_add_license_number);
        Button btnDalAdd = (Button) dalAddLinense.findViewById(R.id.btn_continue);
        final EditText edtLicenseNumber = (EditText) dalAddLinense.findViewById(R.id.edt_license_number);
        btnDalAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String licenseNumber = edtLicenseNumber.getText().toString().replaceAll(" ", "").trim();
                if (licenseNumber.isEmpty()) {
                    Toast.makeText(getApplicationContext(),
                            R.string.dialog_car_empty_message,
                            Toast.LENGTH_SHORT).show();
                }else {
                    SocketIOClient.client.mSocket.emit(Constant.REQUEST_ADD_NEW_CAR, accountid, licenseNumber);
                    SocketIOClient.client.mSocket.on(Constant.RESPONSE_ADD_NEW_CAR, onAddCar);
                }
            }
        });
        TextView tvDalCancel = (TextView) dalAddLinense.findViewById(R.id.tv_cancel);
        tvDalCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dalAddLinense.dismiss();
            }
        });
        dalAddLinense.show();
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
                            Toast.makeText(getApplicationContext(), R.string.error_general, Toast.LENGTH_SHORT).show();

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

                        if (data.getBoolean(Constant.RESULT)) {
                            SocketIOClient.client.mSocket.emit(
                                    Constant.REQUEST_FIND_CAR_BY_ACCOUNT_ID, accountid);
                            Toast.makeText(getApplicationContext(),
                                    R.string.log_cannt_add, Toast.LENGTH_SHORT).show();
                            dalAddLinense.dismiss();
                        } else if (data.getString(Constant.MESSAGE).equals("car_limit")) {
                            Toast.makeText(CarManagerActivity.this,
                                    getResources().getString(R.string.error_add_license_limit),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Log.w(getClass().getName(), "Error: " +
                                    data.getString(Constant.MESSAGE));
                        }

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

        JSONArray listJsonCar = null;
        try {
            listJsonCar = data.getJSONArray(Constant.DATA);
            mCarList = new ArrayList<CarModel>();
            for (int i = 0; i < listJsonCar.length(); i++) {
                p = gson.fromJson(listJsonCar.getJSONObject(i).toString(), CarModel.class);
                Log.i("car number ", p.getVehicleNumber());
                mCarList.add(p);
            }

            adapter = new CarListAdapter(this, getBaseContext(), mCarList);
            lvCarList.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
