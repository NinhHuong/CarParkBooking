package com.quocngay.carparkbooking.activity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;
import com.google.gson.Gson;
import com.quocngay.carparkbooking.R;
import com.quocngay.carparkbooking.model.GarageModel;
import com.quocngay.carparkbooking.model.LocalData;
import com.quocngay.carparkbooking.model.ParkingInfoSecurityModel;
import com.quocngay.carparkbooking.other.AdminHistoryListAdapter;
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

public class AdminActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, DatePickerDialog.OnDateSetListener {

    private ImageButton btnDate;
    private TextView txtDate;
    private TextView txtNotCar;
    private ListView lvHistory;
    DatePickerDialog datePickerDialog;

    private AdminHistoryListAdapter adapter;

    int year;
    int month;
    int day;

    private String accountId;
    private GarageModel garageModel;
    private LocalData localData;
    private List<ParkingInfoSecurityModel> allList;
    private List<ParkingInfoSecurityModel> listShow;

    Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        btnDate = (ImageButton) findViewById(R.id.btnCalendar);
        txtDate = (TextView) findViewById(R.id.txtTime);
        txtNotCar = (TextView) findViewById(R.id.txtNotCar);
        lvHistory = (ListView) findViewById(R.id.lvHistory);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_admin);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_admin);
        navigationView.setNavigationItemSelectedListener(this);
        localData = new LocalData(getApplicationContext());
        accountId = localData.getId();

        myCalendar = Calendar.getInstance();
        year = myCalendar.get(Calendar.YEAR);
        month = myCalendar.get(Calendar.MONTH);
        day = myCalendar.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = new DatePickerDialog(
                this, AdminActivity.this, year, month, day);
        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (datePickerDialog != null)
                    datePickerDialog.show();
            }
        });

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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_add_security:
                startActivity(new Intent(AdminActivity.this, RegisterForOtherActivity.class));
                break;
            case R.id.nav_all_security:
                startActivity(new Intent(AdminActivity.this, SecurityManagerActivity.class));
                break;
            case R.id.nav_logout:
                activityLogout();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_admin);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

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

    private void activityLogout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialog_logout_message)
                .setPositiveButton(R.string.fire, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SharedPreferences mSharedPref = getSharedPreferences(Constant.APP_PREF, MODE_PRIVATE);
                        SharedPreferences.Editor editor = mSharedPref.edit();
                        editor.remove(Constant.APP_PREF_TOKEN);
                        editor.remove(Constant.APP_PREF_REMEMBER);
                        editor.apply();
                        Intent intent = new Intent(AdminActivity.this, LoginActivity.class);
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
                        allList = new ArrayList<ParkingInfoSecurityModel>();
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

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        searchCarParking(year, month, dayOfMonth);
    }

    private void searchCarParking(int year, int month, int dayOfMonth) {
        txtDate.setText(dayOfMonth + "/" + (month + 1) + "/" + year);

        listShow = new ArrayList<>();
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

                adapter = new AdminHistoryListAdapter(getBaseContext(), listShow, this);
                lvHistory.setAdapter(adapter);

                txtNotCar.setVisibility(listShow.size() == 0?View.VISIBLE:View.INVISIBLE);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    //endregion
}
