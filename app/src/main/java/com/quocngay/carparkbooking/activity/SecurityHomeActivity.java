package com.quocngay.carparkbooking.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

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

/**
 * Created by Quang Si on 8/13/2017.
 */

public class SecurityHomeActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private Toolbar toolbar;
    private LinearLayout btnCheckin, btnCheckout;
    private GarageModel garageModel;
    private LocalData localData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_home);
        initSecurityActivity();
    }

    private void initSecurityActivity() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        defaultToolbar();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_security);
        navigationView.setNavigationItemSelectedListener(this);
        MenuItem menuItem = navigationView.getMenu().findItem(R.id.nav_logout);
        SpannableString s = new SpannableString(menuItem.getTitle());
        s.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.map_direction)), 0, s.length(), 0);
        menuItem.setTitle(s);

        navigationView.getMenu().findItem(R.id.nav_car_manager).setVisible(false);
        navigationView.getMenu().findItem(R.id.nav_history).setVisible(false);
        btnCheckin = (LinearLayout) findViewById(R.id.btn_checkin);
        btnCheckout = (LinearLayout) findViewById(R.id.btn_checkout);
        btnCheckin.setOnClickListener(this);
        btnCheckout.setOnClickListener(this);

        localData = new LocalData(this);
        SocketIOClient.client.mSocket.emit(Constant.REQUEST_GET_GARAGE_ID, localData.getId());
        SocketIOClient.client.mSocket.on(Constant.RESPONSE_GET_GARAGE_ID, onGetGarageID);
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
                startActivity(new Intent(SecurityHomeActivity.this, CheckInActivity.class));
                break;
            case R.id.btn_checkout:
                startActivity(new Intent(SecurityHomeActivity.this, CheckOutActivity.class));
                break;
            default:
        }
    }


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

                        JSONArray jsSecurity = data.getJSONArray(Constant.DATA);
                        String garageId = jsSecurity.getJSONObject(0).getString("garageID");
                        localData.setGarageID(garageId);
//                        int garageStatus = jsSecurity.getJSONObject(0).getInt("xStatus");
//                        final int totalSlot = jsSecurity.getJSONObject(0).getInt("totalSlot");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };
}
