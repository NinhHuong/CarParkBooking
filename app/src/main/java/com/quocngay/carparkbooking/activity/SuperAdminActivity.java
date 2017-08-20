package com.quocngay.carparkbooking.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.DividerItemDecoration;
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

import static com.quocngay.carparkbooking.activity.NearestGaraActivity.GARA_SELECTED;

public class SuperAdminActivity extends GeneralActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private LocalData localData;
    private RecyclerView mRecyclerView;
    private int mColumnCount = 1;
    private List<GarageModel> dataModelList;
    private GaragesDetailRecyclerViewAdapter recyclerViewAdapter;
    private List<GarageModel> garageModelList;

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
            public void onGarageClickListener(GarageModel item) {
//                Intent intent = new Intent(getApplicationContext(), MapActivity.class);
//                Bundle bundle = new Bundle();
//                bundle.putSerializable(GARA_SELECTED, item);
//                intent.putExtras(bundle);
//                setResult(RESULT_OK, intent);
//                finish();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.super_admin, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
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
                        try {
                            JSONArray listJsonGaras = jsonObject.getJSONArray(Constant.SERVER_GARAGES_RESULT);
                            for (int i = 0; i < listJsonGaras.length(); i++) {
                                GarageModel garageModel =
                                        gson.fromJson(listJsonGaras.getJSONObject(i).toString(),
                                                GarageModel.class);
                                garageModelList.add(garageModel);
                                recyclerViewAdapter.notifyDataSetChanged();
                            }
                            SocketIOClient.client.mSocket.off(Constant.RESPONSE_GET_ALL_GARAGES);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        };

        SocketIOClient.client.mSocket.emit(Constant.REQUEST_GET_ALL_GARAGES);
        SocketIOClient.client.mSocket.on(Constant.RESPONSE_GET_ALL_GARAGES, onResponseGetAllGarages);
    }
}
