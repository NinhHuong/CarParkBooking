package com.quocngay.carparkbooking.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.github.nkzawa.emitter.Emitter;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.quocngay.carparkbooking.GaragesRecyclerViewAdapter;
import com.quocngay.carparkbooking.LicenseNumRecyclerViewAdapter;
import com.quocngay.carparkbooking.R;
import com.quocngay.carparkbooking.model.GarageModel;
import com.quocngay.carparkbooking.model.LocationDataModel;
import com.quocngay.carparkbooking.model.Principal;
import com.quocngay.carparkbooking.other.Constant;
import com.quocngay.carparkbooking.other.SocketIOClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BookingActivity extends AppCompatActivity {

    private Button btnBook;
    private LatLng garaLatLng;
    private RecyclerView mRecyclerView;
    private LicenseNumRecyclerViewAdapter recyclerViewAdapter;
    private String clientID;
    private List<String> licenseList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();

        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        btnBook = (Button) findViewById(R.id.btn_book);
        btnBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(Constant.BOOKING_STATUS, true);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.list_number);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        Principal principal = new Principal(getApplicationContext());
        SocketIOClient.client.mSocket.emit(Constant.REQUEST_FIND_CAR_BY_ACCOUNT_ID, principal.getId());
        SocketIOClient.client.mSocket.on(Constant.RESPONSE_FIND_CAR_BY_ACCOUNT_ID, onResponseFindCar);

    }

    private Emitter.Listener onResponseFindCar = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject jsonObject = (JSONObject) args[0];
                    Log.d("Cars", jsonObject.toString());
                    Gson gson = new Gson();

                    try {
                        Boolean result = Boolean.valueOf(jsonObject.getString(Constant.RESULT));
                        if (result) {
                            JSONArray listJsonLicense = jsonObject.getJSONArray(Constant.DATA);
                            licenseList = new ArrayList<>();
                            for (int i = 0; i < listJsonLicense.length(); i++) {
                                licenseList.add(listJsonLicense.getJSONObject(i).getString(Constant.VEHICLE_NUMBER));
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    recyclerViewAdapter = new LicenseNumRecyclerViewAdapter(licenseList);
                    mRecyclerView.setAdapter(recyclerViewAdapter);

                }
            });
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                return false;
        }
        return super.onOptionsItemSelected(item);
    }
}
