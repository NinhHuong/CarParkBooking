package com.quocngay.carparkbooking.activity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;
import com.google.gson.Gson;
import com.quocngay.carparkbooking.R;
import com.quocngay.carparkbooking.model.GarageModel;
import com.quocngay.carparkbooking.model.LocalData;
import com.quocngay.carparkbooking.model.ParkingInfoSecurityModel;
import com.quocngay.carparkbooking.adapter.AdminHistoryListAdapter;
import com.quocngay.carparkbooking.other.Constant;
import com.quocngay.carparkbooking.other.SocketIOClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdminActivity extends GeneralActivity
        implements NavigationView.OnNavigationItemSelectedListener, DatePickerDialog.OnDateSetListener {

    DatePickerDialog datePickerDialog;
    int year;
    int month;
    int day;
    Toolbar toolbar;
    Calendar myCalendar = Calendar.getInstance();
    private TextView txtNotCar;
    private ListView lvHistory;
    private GarageModel garageModel;
    private LocalData localData;
    private List<ParkingInfoSecurityModel> allList;
    //region Create chart
    private Emitter.Listener onGetHistory = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    try {
                        Log.i("Data history", data.toString());
                        Boolean result = data.getBoolean(Constant.RESULT);

                        if (!result) return;
                        allList = new ArrayList<>();
                        Gson gson = new Gson();

                        JSONArray garage = data.getJSONArray(Constant.DATA);
                        for (int i = 0; i < garage.length(); i++) {
                            allList.add(gson.fromJson(
                                    garage.getJSONObject(i).toString(),
                                    ParkingInfoSecurityModel.class));
                        }

                        searchCarParking(year, month, day);

                        SocketIOClient.client.mSocket.off(Constant.RESPONSE_HISTORY);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };
    private Emitter.Listener onGetGarage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    try {
                        Log.i("Data garage", data.toString());
                        Boolean result = data.getBoolean(Constant.RESULT);

                        if (!result) return;

                        Gson gson = new Gson();

                        JSONArray garage = data.getJSONArray(Constant.DATA);
                        garageModel = gson.fromJson(
                                garage.getJSONObject(0).toString(),
                                GarageModel.class);

                        localData.setGarageID(String.valueOf(garageModel.getId()));
                        SocketIOClient.client.mSocket.emit(Constant.REQUEST_HISTORY, garageModel.getId());
                        SocketIOClient.client.mSocket.on(Constant.RESPONSE_HISTORY, onGetHistory);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        txtNotCar = (TextView) findViewById(R.id.txtNotCar);
        lvHistory = (ListView) findViewById(R.id.lvHistory);

        initToolbarWithDrawer(R.id.toolbar, R.id.drawer_layout_admin);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_admin);
        navigationView.setNavigationItemSelectedListener(this);
        localData = new LocalData(getApplicationContext());
        String accountId = localData.getId();

        myCalendar = Calendar.getInstance();
        year = myCalendar.get(Calendar.YEAR);
        month = myCalendar.get(Calendar.MONTH);
        day = myCalendar.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = new DatePickerDialog(
                this, AdminActivity.this, year, month, day);

        SocketIOClient.client.mSocket.emit(Constant.REQUEST_GET_GARAGE_BY_ACCOUNT_ID, accountId);
        SocketIOClient.client.mSocket.on(Constant.RESPONSE_GET_GARAGE_BY_ACCOUNT_ID, onGetGarage);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_admin);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.admin, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_date) {
            if (datePickerDialog != null) {
                datePickerDialog.show();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_add_security:
                startActivity(new Intent(AdminActivity.this, RegisterForOtherActivity.class));
                break;
            case R.id.nav_all_security:
                startActivity(new Intent(AdminActivity.this, SecurityManagerActivity.class));
                break;
            case R.id.nav_logout:
                actionLogout();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_admin);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        searchCarParking(year, month, dayOfMonth);
    }

    private void searchCarParking(int year, int month, int dayOfMonth) {
        toolbar.setTitle(dayOfMonth + "/" + (month + 1) + "/" + year);

        List<ParkingInfoSecurityModel> listShow = new ArrayList<>();
        String selectDate = dayOfMonth + "-" + (month + 1) + "-" + year;
        SimpleDateFormat inputFormat =
                new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        SimpleDateFormat dateFormat =
                new SimpleDateFormat("d-M-yyyy", Locale.getDefault());

        for (int i = 0; i < allList.size(); i++) {
            try {
                Date date = inputFormat.parse(allList.get(i).getTimeGoIn());
                String d = dateFormat.format(date);
                if (d.compareTo(selectDate) == 0) {
                    listShow.add(allList.get(i));
                }

                AdminHistoryListAdapter adapter = new AdminHistoryListAdapter(getBaseContext(), listShow, this);
                lvHistory.setAdapter(adapter);

                txtNotCar.setVisibility(listShow.size() == 0 ? View.VISIBLE : View.GONE);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    //endregion
}
