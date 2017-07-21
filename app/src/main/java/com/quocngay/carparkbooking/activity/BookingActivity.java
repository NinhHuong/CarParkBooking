package com.quocngay.carparkbooking.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bigkoo.pickerview.MyOptionsPickerView;
import com.github.nkzawa.emitter.Emitter;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.quocngay.carparkbooking.LicenseNumRecyclerViewAdapter;
import com.quocngay.carparkbooking.R;
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
    MyOptionsPickerView licensePicker;
    TextView tvLicenseNumber;
    LinearLayoutManager layoutManager;

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

        tvLicenseNumber = (TextView) findViewById(R.id.tv_license_number);

        Principal principal = new Principal(getApplicationContext());
        SocketIOClient.client.mSocket.emit(Constant.REQUEST_FIND_CAR_BY_ACCOUNT_ID, principal.getId());
        SocketIOClient.client.mSocket.on(Constant.RESPONSE_FIND_CAR_BY_ACCOUNT_ID, onResponseFindCar);
        final BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(R.layout.dialog_license_list);
        dialog.setTitle(getResources().getString(R.string.title_licence_picker));

        mRecyclerView = (RecyclerView) dialog.findViewById(R.id.list_license);
        layoutManager = new LinearLayoutManager(dialog.getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        final OnListInteractionListener listener = new OnListInteractionListener() {
            @Override
            public void onListInteraction(String item) {
                tvLicenseNumber.setText(item);
                dialog.dismiss();
            }
        };
        tvLicenseNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerViewAdapter = new LicenseNumRecyclerViewAdapter(licenseList, listener);
                mRecyclerView.setAdapter(recyclerViewAdapter);
                dialog.show();
            }
        });
    }

    public interface OnListInteractionListener {
        void onListInteraction(String item);
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
                    tvLicenseNumber.setText(licenseList.get(0));

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
