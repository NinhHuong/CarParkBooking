package com.quocngay.carparkbooking.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.github.nkzawa.emitter.Emitter;
import com.google.gson.Gson;
import com.quocngay.carparkbooking.R;
import com.quocngay.carparkbooking.adapter.GaragesDetailRecyclerViewAdapter;
import com.quocngay.carparkbooking.model.GarageAdminModel;
import com.quocngay.carparkbooking.model.GarageModel;
import com.quocngay.carparkbooking.model.LocalData;
import com.quocngay.carparkbooking.model.ParkingInfoSecurityModel;
import com.quocngay.carparkbooking.other.Constant;
import com.quocngay.carparkbooking.other.OnListInteractionListener;
import com.quocngay.carparkbooking.other.SocketIOClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SuperAdminActivity extends GeneralActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final int REQUEST_ADD_GARAGE = 1;

    private LocalData localData;
    private RecyclerView mRecyclerView;
    private int mColumnCount = 1;
    private List<GarageModel> dataModelList;
    private GaragesDetailRecyclerViewAdapter recyclerViewAdapter;
    private List<GarageAdminModel> garageModelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_super_admin);

        localData = new LocalData(getBaseContext());

        initToolbarWithDrawer(R.id.toolbar, R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        mRecyclerView = (RecyclerView) findViewById(R.id.list_gara_detail);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        if (mColumnCount <= 1) {
            mRecyclerView.setLayoutManager(layoutManager);
        } else {
            mRecyclerView.setLayoutManager(new GridLayoutManager(this, mColumnCount));
        }
        garageModelList = new ArrayList<>();
        OnListInteractionListener listener = new OnListInteractionListener() {
            @Override
            public void onLicenseClickListener(ParkingInfoSecurityModel item) {

            }

            @Override
            public void onGarageDeleteListener(final GarageModel item) {
                AlertDialog.Builder builder =
                        new AlertDialog.Builder(SuperAdminActivity.this);
                builder.setTitle(R.string.dialog_garage_detail_remove)
                        .setMessage(R.string.dialog_garage_detail_message)
                        .setPositiveButton(R.string.fire,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        removeGarages(item);
                                    }
                                })
                        .setNegativeButton(R.string.cancel,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        dialog.dismiss();
                                    }
                                });
                builder.create().show();
            }
        };
        recyclerViewAdapter = new GaragesDetailRecyclerViewAdapter(garageModelList, listener);
        mRecyclerView.setAdapter(recyclerViewAdapter);

        getAllGarages();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_add_new_garage) {
            Intent registAccount = new Intent(SuperAdminActivity.this, RegisterForOtherActivity.class);
            registAccount.putExtra(RegisterForOtherActivity.REGISTER_EXTRA, true);
            startActivity(registAccount);
        } else if (id == R.id.nav_logout) {
            actionLogout();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_ADD_GARAGE){
            if (resultCode == RESULT_OK){
                if(data.getBooleanExtra(AddGarageActivity.ADD_GARAGE_STATUS, false)){
                    getAllGarages();
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getAllGarages();
    }

    private void getAllGarages() {
        Emitter.Listener onResponseGetAllGarages = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject jsonObject = (JSONObject) args[0];
                        Log.d("Garas", jsonObject.toString());
                        Gson gson = new Gson();
                        if(garageModelList.size() > 0){
                            garageModelList.clear();
                        }
                        try {
                            if (jsonObject.getBoolean(Constant.RESULT)) {
                                JSONArray listJsonGaras = jsonObject.getJSONArray(Constant.DATA);
                                for (int i = 0; i < listJsonGaras.length(); i++) {
                                    GarageAdminModel garageAdminModel =
                                            gson.fromJson(listJsonGaras.getJSONObject(i).toString(),
                                                    GarageAdminModel.class);
                                    garageModelList.add(garageAdminModel);
                                    recyclerViewAdapter.notifyDataSetChanged();
                                }
                            } else {
                                Log.w(getClass().getName(),
                                        "ERROR: " + jsonObject.getString(Constant.MESSAGE));
                            }
                            SocketIOClient.client.mSocket.off(Constant.RESPONSE_GET_ALL_GARAGES_AND_ADMIN);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        };

        SocketIOClient.client.mSocket.emit(Constant.REQUEST_GET_ALL_GARAGES_AND_ADMIN);
        SocketIOClient.client.mSocket.on(Constant.RESPONSE_GET_ALL_GARAGES_AND_ADMIN, onResponseGetAllGarages);
    }

    private void removeGarages(GarageModel garageModel) {
        Emitter.Listener onResponseRemoveGarage = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject jsonObject = (JSONObject) args[0];
                        Log.d("Garas", jsonObject.toString());
                        Gson gson = new Gson();
                        try {
                            if (jsonObject.getBoolean(Constant.RESULT)) {
                                getAllGarages();
                            } else {
                                Log.w(getClass().getName(),
                                        "ERROR: " + jsonObject.getString(Constant.MESSAGE));
                            }
                            SocketIOClient.client.mSocket.off(Constant.RESPONSE_REMOVE_GARAGE_BY_ID);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        };
        SocketIOClient.client.mSocket.emit(Constant.REQUEST_REMOVE_GARAGE_BY_ID,
                garageModel.getId());
        SocketIOClient.client.mSocket.on(Constant.RESPONSE_REMOVE_GARAGE_BY_ID,
                onResponseRemoveGarage);
    }
}
