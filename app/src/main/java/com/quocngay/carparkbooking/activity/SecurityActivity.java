package com.quocngay.carparkbooking.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.google.gson.Gson;
import com.quocngay.carparkbooking.R;
import com.quocngay.carparkbooking.model.ParkingInfoSecurityModel;
import com.quocngay.carparkbooking.model.Principal;
import com.quocngay.carparkbooking.other.Constant;
import com.quocngay.carparkbooking.other.SocketIOClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SecurityActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private Spinner spnCarIn;
    private Spinner spnCarOut;
    private Button btnCarGoIn;
    private Button btnCarGoOut;
    private EditText edtCarInByHand;
    private Toolbar toolbar;

    private String accountId;
    private String garageId;
    private List<ParkingInfoSecurityModel> listCarIn;
    private List<ParkingInfoSecurityModel> listCarOut;
    private int carOutID = 0;
    private int carInID = 0;

    //    private List<ParkingInfoSecurityModel> listCarInId;
//    private List<ParkingInfoSecurityModel> listCarOutId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security);
        initSecurityActivity();
        accountId = new Principal(getApplicationContext()).getId();

        spnCarIn = (Spinner) findViewById(R.id.spnCarIn);
        spnCarOut = (Spinner) findViewById(R.id.spnCarOut);
        btnCarGoIn = (Button) findViewById(R.id.btnCarGoIn);
        btnCarGoOut = (Button) findViewById(R.id.btnCarGoOut);
        edtCarInByHand = (EditText) findViewById(R.id.edtCarIn);

        SocketIOClient.client.mSocket.emit(Constant.REQUEST_GET_GARAGE_ID, accountId);
        SocketIOClient.client.mSocket.on(Constant.RESPONSE_GET_GARAGE_ID, onGetGarageID);

        SocketIOClient.client.mSocket.on(Constant.RESPONSE_CAR_WILL_IN, onCarIn);
        SocketIOClient.client.mSocket.on(Constant.RESPONSE_CAR_WILL_OUT, onCarOut);

        SocketIOClient.client.mSocket.on(Constant.RESPONSE_ONE_CAR_IN, onCarOut);
        SocketIOClient.client.mSocket.on(Constant.RESPONSE_ONE_CAR_OUT, onCarOut);


        spnCarIn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                ParkingInfoSecurityModel parkingModel = (ParkingInfoSecurityModel) parentView.getItemAtPosition(position);
                carInID = parkingModel.getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });
        spnCarOut.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                ParkingInfoSecurityModel parkingModel = (ParkingInfoSecurityModel) parentView.getItemAtPosition(position);

                carOutID = parkingModel.getId();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });


        btnCarGoIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (carInID > 0)
                    SocketIOClient.client.mSocket.emit(Constant.REQUEST_ONE_CAR_IN_ID, carInID, garageId);
                else if (edtCarInByHand.getText().toString().compareTo("") != 0)
                    SocketIOClient.client.mSocket.emit(
                            Constant.REQUEST_ONE_CAR_IN_NUMBER,
                            edtCarInByHand.getText().toString(),
                            garageId);
                else
                    Toast.makeText(getBaseContext(), R.string.error_select_car, Toast.LENGTH_SHORT).show();

            }
        });

        btnCarGoOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (carOutID > 0)
                    SocketIOClient.client.mSocket.emit(Constant.REQUEST_ONE_CAR_OUT, carOutID, garageId);
                else
                    Toast.makeText(getBaseContext(), R.string.error_select_car, Toast.LENGTH_SHORT).show();
            }
        });

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
                        Log.i("Data JSONObject", jsSecurity.toString());
                        garageId = jsSecurity.getJSONObject(0).getString("garageID");
                        Log.i("Data garageId", garageId);

                        SocketIOClient.client.mSocket.emit(Constant.REQUEST_CAR_WILL_IN, garageId);
                        SocketIOClient.client.mSocket.emit(Constant.REQUEST_CAR_WILL_OUT, garageId);
                        SocketIOClient.client.mSocket.off(Constant.RESPONSE_GET_GARAGE_ID);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private Emitter.Listener onCarIn = new Emitter.Listener() {

        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    try {
                        Log.i("Data car in", data.toString());
                        Boolean result = data.getBoolean(Constant.RESULT);
                        listCarIn = new ArrayList<ParkingInfoSecurityModel>();
                        listCarIn.add(new ParkingInfoSecurityModel());

                        ParkingInfoSecurityModel p = new ParkingInfoSecurityModel();
                        Gson gson = new Gson();

                        JSONArray listJsonGarasParkInfo = data.getJSONArray(Constant.DATA);
                        for (int i = 0; i < listJsonGarasParkInfo.length(); i++) {
                            p = gson.fromJson(listJsonGarasParkInfo.getJSONObject(i).toString()
                                    , ParkingInfoSecurityModel.class);
                            listCarIn.add(p);
                        }

                        if (garageId.compareTo(String.valueOf(p.getGarageID())) != 0)
                            return;

                        ArrayAdapter<ParkingInfoSecurityModel> adapter =
                                new ArrayAdapter<ParkingInfoSecurityModel>(
                                        getBaseContext(), android.R.layout.simple_spinner_item, listCarIn);

                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spnCarIn.setAdapter(adapter);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private Emitter.Listener onCarOut = new Emitter.Listener() {

        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    try {
                        Log.i("Data car out", data.toString());
                        Boolean result = data.getBoolean(Constant.RESULT);
                        listCarOut = new ArrayList<ParkingInfoSecurityModel>();
                        listCarOut.add(new ParkingInfoSecurityModel());
                        ParkingInfoSecurityModel p = new ParkingInfoSecurityModel();
                        Gson gson = new Gson();

                        JSONArray listJsonGarasParkInfo = data.getJSONArray(Constant.DATA);
                        for (int i = 0; i < listJsonGarasParkInfo.length(); i++) {
                            p = gson.fromJson(listJsonGarasParkInfo.getJSONObject(i).toString(),
                                    ParkingInfoSecurityModel.class);
                            listCarOut.add(p);
                        }

                        if (garageId.compareTo(String.valueOf(p.getGarageID())) != 0)
                            return;

                        ArrayAdapter<ParkingInfoSecurityModel> adapter =
                                new ArrayAdapter<ParkingInfoSecurityModel>(
                                        getBaseContext(), android.R.layout.simple_spinner_item, listCarOut);

                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spnCarOut.setAdapter(adapter);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

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
                        SharedPreferences mSharedPref = getSharedPreferences(Constant.APP_PREF, MODE_PRIVATE);
                        SharedPreferences.Editor editor = mSharedPref.edit();
                        editor.remove(Constant.APP_PREF_TOKEN);
                        editor.remove(Constant.APP_PREF_REMEMBER);
                        editor.apply();
                        Intent intent = new Intent(SecurityActivity.this, LoginActivity.class);
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

    private void initSecurityActivity() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        defaultToolbar();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        MenuItem menuItem = navigationView.getMenu().findItem(R.id.nav_logout);
        SpannableString s = new SpannableString(menuItem.getTitle());
        s.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.map_direction)), 0, s.length(), 0);
        menuItem.setTitle(s);

        navigationView.getMenu().findItem(R.id.nav_car_manager).setVisible(false);
        navigationView.getMenu().findItem(R.id.nav_history).setVisible(false);
    }

    private void defaultToolbar() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        toggle.setDrawerSlideAnimationEnabled(true);
    }
}
