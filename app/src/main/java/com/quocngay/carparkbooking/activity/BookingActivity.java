package com.quocngay.carparkbooking.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.quocngay.carparkbooking.LicenseNumRecyclerViewAdapter;
import com.quocngay.carparkbooking.R;
import com.quocngay.carparkbooking.model.CarModel;
import com.quocngay.carparkbooking.model.GarageModel;
import com.quocngay.carparkbooking.model.LocationDataModel;
import com.quocngay.carparkbooking.model.Principal;
import com.quocngay.carparkbooking.other.Constant;
import com.quocngay.carparkbooking.other.SocketIOClient;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class BookingActivity extends AppCompatActivity {

    private Button btnBook;
    private LatLng garaLatLng;
    private RecyclerView mRecyclerView;
    private LicenseNumRecyclerViewAdapter recyclerViewAdapter;
    private String clientID;
    private List<CarModel> licenseList;
    private TextView tvLicenseNumber;
    private LinearLayoutManager layoutManager;
    private TextView tvGaraTitle, tvGaraDes, tvGaraDuration, tvGaraDistance;
    private GarageModel markerGara;
    private Principal principal;
    private CarModel mCurrentCar;
    private AlertDialog.Builder mBookAlertDialog;
    private TextView tvRemainSlots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_booking);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();

        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        markerGara = (GarageModel) getIntent().getSerializableExtra(Constant.GARA_DETAIL);
        ImageView imageView = (ImageView) findViewById(R.id.iv_gara_map);
        Picasso.with(this).load(getMapImageUrl()).into(imageView);
        tvRemainSlots = (TextView) findViewById(R.id.tv_gara_detail_remain);

        if (markerGara.getRemainSlot() == 0) {
            tvRemainSlots.setText(getResources().getString(R.string.booking_slot_not_available));
            tvRemainSlots.setTextColor(getResources().getColor(R.color.colorNotAvailable));
        } else {
            tvRemainSlots.setText(getResources().getString(R.string.booking_slot_available,
                    markerGara.getRemainSlot(),
                    markerGara.getTotalSlot()));
            tvRemainSlots.setTextColor(getResources().getColor(R.color.colorAvailable));
        }
        principal = new Principal(getApplicationContext());
        btnBook = (Button) findViewById(R.id.btn_book);
        btnBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBookAlertDialog = new AlertDialog.Builder(BookingActivity.this);
                mBookAlertDialog.setTitle(getResources().getString(R.string.dialog_book_title));
                mBookAlertDialog.setMessage(R.string.dialog_book_message)
                        .setPositiveButton(R.string.fire, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Calendar c = Calendar.getInstance();
                                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                String bookTime = df.format(c.getTime());
                                SocketIOClient.client.mSocket.emit(Constant.REQUEST_ADD_NEW_PARKING_INFO,
                                        mCurrentCar.getId(),
                                        markerGara.getId(),
                                        bookTime);
                                SocketIOClient.client.mSocket.on(Constant.RESPONSE_ADD_NEW_PARKING_INFO, onResponseAddParkingInfo);
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                // Create the AlertDialog object and return it
                mBookAlertDialog.create().show();
            }
        });

        tvGaraTitle = (TextView) findViewById(R.id.tv_gara_detail_title);
        tvGaraDes = (TextView) findViewById(R.id.tv_gara_detail_des);
        tvGaraDuration = (TextView) findViewById(R.id.tv_duration_book);
        tvGaraDistance = (TextView) findViewById(R.id.tv_distance_book);

        tvGaraTitle.setText(markerGara.getName());
        tvGaraDes.setText(markerGara.getAddress());

        tvLicenseNumber = (TextView) findViewById(R.id.tv_license_number);


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
            public void onListInteraction(CarModel item) {
                tvLicenseNumber.setText(item.getVehicleNumber());
                mCurrentCar = item;
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
        void onListInteraction(CarModel item);
    }

    private String getMapImageUrl() {
        String url = getResources().getString(
                R.string.book_url_map_image,
                markerGara.getLocationX() + "," + markerGara.getLocationY(),
                Constant.DEFAULT_ZOOM,
                Constant.BOOKING_MAP_SIZE,
                markerGara.getLocationX() + "," + markerGara.getLocationY()).replaceAll(" ", "");

        return url;
    }


    private Emitter.Listener onResponseAddParkingInfo = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject jsonObject = (JSONObject) args[0];
                    try {
                        if (jsonObject.getBoolean(Constant.RESULT)) {
                            Intent intent = new Intent();
                            intent.putExtra(Constant.BOOKING_STATUS, true);
                            setResult(RESULT_OK, intent);
                            mBookAlertDialog.create().dismiss();
                            finish();
                        } else {
                            Toast.makeText(BookingActivity.this, getResources().getString(R.string.error_book_new), Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

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
                                CarModel carModel =
                                        gson.fromJson(listJsonLicense.getJSONObject(i).toString(), CarModel.class);
                                licenseList.add(carModel);
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mCurrentCar = licenseList.get(0);
                    tvLicenseNumber.setText(licenseList.get(0).getVehicleNumber());
                    SocketIOClient.client.mSocket.off();
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
