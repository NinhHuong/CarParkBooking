package com.quocngay.carparkbooking.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.google.gson.Gson;
import com.quocngay.carparkbooking.R;
import com.quocngay.carparkbooking.model.GarageModel;
import com.quocngay.carparkbooking.model.LocalData;
import com.quocngay.carparkbooking.other.Constant;
import com.quocngay.carparkbooking.other.SocketIOClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SecurityHomeActivity extends GeneralActivity implements
        NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private Toolbar toolbar;

    private LinearLayout btnCheckin, btnCheckout;
    private TextView tvAvalibleSlot, tvAddress, tvName;

    private GarageModel garageModel;

    private LocalData localData;

    private String garageId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_home);
        initSecurityActivity();
    }

    private void initSecurityActivity() {
        initToolbarWithDrawer(R.id.toolbar, R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_security);
        navigationView.setNavigationItemSelectedListener(this);
        MenuItem menuItem = navigationView.getMenu().findItem(R.id.nav_logout);
        SpannableString s = new SpannableString(menuItem.getTitle());
        s.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.map_direction)), 0, s.length(), 0);
        menuItem.setTitle(s);

        navigationView.getMenu().findItem(R.id.nav_car_manager).setVisible(false);
        navigationView.getMenu().findItem(R.id.nav_history).setVisible(false);

        LinearLayout btnCheckin = (LinearLayout) findViewById(R.id.btn_checkin);
        LinearLayout btnCheckout = (LinearLayout) findViewById(R.id.btn_checkout);

        tvAvalibleSlot = (TextView) findViewById(R.id.tv_available_slot);
        tvAddress = (TextView) findViewById(R.id.tvAddress);
        tvName = (TextView) findViewById(R.id.tvName);
        btnCheckin.setOnClickListener(this);
        btnCheckout.setOnClickListener(this);
        defaultToolbar();
        localData = new LocalData(this);
        SocketIOClient.client.mSocket.emit(Constant.REQUEST_GET_GARAGE_ID, localData.getId());
        SocketIOClient.client.mSocket.on(Constant.RESPONSE_GET_GARAGE_ID, onGetGarageID);

        SocketIOClient.client.mSocket.on(Constant.RESPONSE_GARAGE_UPDATED, onRequestResetList);
    }

    private void defaultToolbar() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        toggle.setDrawerSlideAnimationEnabled(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        SocketIOClient.client.mSocket.off(Constant.RESPONSE_GARAGE_UPDATED);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_logout) {
            logout();
        } else if (id == R.id.nav_profile) {
            startActivity(new Intent(SecurityHomeActivity.this, ProfileActivity.class));
        }
        if (id == R.id.nav_change_password) {
            startActivity(new Intent(SecurityHomeActivity.this, NewPasswordActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialog_logout_message)
                .setPositiveButton(R.string.fire, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SocketIOClient.client.mSocket.emit(Constant.REQUEST_LOG_OUT, localData.getId());
                        SocketIOClient.client.mSocket.on(Constant.RESPONSE_LOG_OUT, new Emitter.Listener() {
                            @Override
                            public void call(Object... args) {

                                JSONObject data = (JSONObject) args[0];
                                try {
                                    Boolean result = data.getBoolean(Constant.RESULT);
                                    if (result) {
                                        localData.clearData();
                                        Intent intent = new Intent(SecurityHomeActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        // Create the AlertDialog object and return it
        builder.create().show();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_checkin:
                Intent checkInActivity = new Intent(SecurityHomeActivity.this, CheckInOutActivity.class);
                checkInActivity.putExtra(CheckInOutActivity.EXTRA_SECURITY_FUNCTION, CheckInOutActivity.EXTRA_CAR_IN);
                startActivity(checkInActivity);
                break;

            case R.id.btn_checkout:
                Intent checkOutActivity = new Intent(SecurityHomeActivity.this, CheckInOutActivity.class);
                checkOutActivity.putExtra(CheckInOutActivity.EXTRA_SECURITY_FUNCTION, CheckInOutActivity.EXTRA_CAR_OUT);
                startActivity(checkOutActivity);
                break;
            default:
        }
    }

    private Emitter.Listener onRequestResetList = new Emitter.Listener() {

        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    try {
                        Log.i("request reset all list", data.toString());
                        Boolean result = data.getBoolean(Constant.RESULT);

                        Gson gson = new Gson();

                        GarageModel resultGara = gson.fromJson(
                                data
                                        .getJSONArray(Constant.DATA)
                                        .getJSONObject(0).toString(),
                                GarageModel.class);

                        if (!result || String.valueOf(resultGara.getId()).compareTo(garageId) != 0)
                            return;

                        SocketIOClient.client.mSocket.emit(Constant.REQUEST_GET_GARAGE_ID, localData.getId());
                        SocketIOClient.client.mSocket.on(Constant.RESPONSE_GET_GARAGE_ID, onGetGarageID);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };


    private Emitter.Listener onGetGarageID = new Emitter.Listener() {

        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    try {
                        Log.i("Data security account", data.toString());
                        Boolean result = data.getBoolean(Constant.RESULT);

                        if (!result) return;

                        Gson gson = new Gson();

                        JSONArray jsSecurity = data.getJSONArray(Constant.DATA);
                        garageId = jsSecurity.getJSONObject(0).getString("garageID");
                        localData.setGarageID(garageId);

                        garageModel = gson.fromJson(jsSecurity.getJSONObject(0).toString()
                                , GarageModel.class);

                        String avaliableSlot = garageModel.getBusySlot() + " / " + garageModel.getTotalSlot();

                        tvAvalibleSlot.setText(avaliableSlot);
                        tvAddress.setText(garageModel.getAddress());
                        tvName.setText(garageModel.getName());
//                        int garageStatus = jsSecurity.getJSONObject(0).getInt("xStatus");
//                        if (garageStatus == 0) {
//                            final int totalSlot = jsSecurity.getJSONObject(0).getInt("totalSlot");
//
//                            dialogOpenGarage(totalSlot);
//                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

}
