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
import android.text.InputType;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
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

        localData = new LocalData(this);
        SocketIOClient.client.mSocket.emit(Constant.REQUEST_GET_GARAGE_ID, localData.getId());
        SocketIOClient.client.mSocket.on(Constant.RESPONSE_GET_GARAGE_ID, onGetGarageID);

        SocketIOClient.client.mSocket.on(Constant.REQUEST_REFRESH_SECURITY_PARKING_LIST, onRequestResetList);
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
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_logout) {
            logout();
        } else if (id == R.id.nav_profile) {
            startActivity(new Intent(SecurityHomeActivity.this, ProfileActivity.class));
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
                        localData.clearData();
                        Intent intent = new Intent(SecurityHomeActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
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

                        String requestGarageID = data.
                                getJSONObject(Constant.DATA).
                                getString("garageID");

                        if (!result || requestGarageID.compareTo(garageId) != 0)
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


//    private void dialogOpenGarage(final int totalSlot) {
//        AlertDialog.Builder alertDialog = new AlertDialog.Builder(SecurityHomeActivity.this);
//
//        // Setting Dialog Title
//        TextView txtTitle = new TextView(SecurityHomeActivity.this);
//        txtTitle.setText(getResources().getString(R.string.dialog_title_open_garage));
//        txtTitle.setPadding(40, 40, 40, 40);
//        txtTitle.setGravity(Gravity.CENTER);
//        txtTitle.setTextSize(30);
//        alertDialog.setCustomTitle(txtTitle);
//
//        // Setting Dialog Message
//        alertDialog.setMessage(getResources().getString(R.string.dialog_message_total_slot) + ": " + totalSlot);
//
//        final EditText input = new EditText(SecurityHomeActivity.this);
//        input.setInputType(InputType.TYPE_CLASS_NUMBER);
////        input.setHint("Số xe hiện tại");
//        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.MATCH_PARENT);
//        input.setLayoutParams(lp);
//        alertDialog.setView(input);
//
//        // Setting Positive "Yes" Button
//        alertDialog.setPositiveButton(getResources().getString(R.string.dialog_button_ok),
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        String s = input.getText().toString();
//                        if (s.matches("\\d+")) {
//                            int currentSlotBusy = Integer.parseInt(input.getText().toString());
//                            if (currentSlotBusy > totalSlot) {
//                                Toast.makeText(getBaseContext(), getResources().getString(R.string.error_incorrect_number_slot), Toast.LENGTH_SHORT).show();
//                                dialogOpenGarage(totalSlot);
//                            } else {
//                                SocketIOClient.client.mSocket.emit(Constant.REQUEST_EDIT_GARAGE_STATUS, garageId, currentSlotBusy, Constant.STATUS_GARAGE_OPEN);
//                                SocketIOClient.client.mSocket.off(Constant.RESPONSE_GET_GARAGE_ID);
//                            }
//                        } else {
//                            Toast.makeText(getBaseContext(), getResources().getString(R.string.error_field_required), Toast.LENGTH_SHORT).show();
//                            dialogOpenGarage(totalSlot);
//                        }
//                    }
//                });
//        // Setting Negative "NO" Button
//        alertDialog.setNegativeButton(getResources().getString(R.string.dialog_btn_cancel),
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        // Write your code here to execute after dialog
//                        dialog.cancel();
//                        finish();
//                    }
//                });
//        // closed
//
//        // Showing Alert Message
//        alertDialog.show();
//    }
}
