package com.quocngay.carparkbooking.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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
import com.quocngay.carparkbooking.R;
import com.quocngay.carparkbooking.model.CarModel;
import com.quocngay.carparkbooking.model.GarageModel;
import com.quocngay.carparkbooking.model.LocationDataModel;
import com.quocngay.carparkbooking.model.ParkingInfoModel;
import com.quocngay.carparkbooking.model.LocalData;
import com.quocngay.carparkbooking.other.Constant;
import com.quocngay.carparkbooking.other.SocketIOClient;
import com.quocngay.carparkbooking.services.FetchAddressIntentService;
import com.quocngay.carparkbooking.tasks.GetDirectionApiData;
import com.quocngay.carparkbooking.tasks.GetLocationDistanceDuration;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class BookingDetailActivity extends AppCompatActivity {

    private Button btnBookCancel;
    private Button btnBookRefresh;
    private TextView tvLicenseNumber;
    private ImageView mMapImage;
    private TextView tvBookingDetailTitle, tvBookingDetailDes, tvBookingDetailDuration, tvBookingDetailDistance;
    private TextView tvBookingDetailTime, tvBookingDetailLicense;
    private ParkingInfoModel mParkingInfoModel;
    private CarModel mCarModel;
    private GarageModel mGaraModel;
    private LocalData localData;
    private AlertDialog.Builder mBookAlertDialog;
    private TextView tvRemainSlots;

    private Emitter.Listener onResponseGetGaraById = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject jsonObject = (JSONObject) args[0];
                    Gson gson = new Gson();
                    try {
                        if (jsonObject.getBoolean(Constant.RESULT)) {
                            mGaraModel = gson.fromJson(
                                    jsonObject.getJSONArray(Constant.DATA).getJSONObject(0).toString(),
                                    GarageModel.class);
                            initBookingDetailContent();
                        } else {
                            Log.e("Server error", jsonObject.getString(Constant.MESSAGE));
                        }
                        SocketIOClient.client.mSocket.off(Constant.RESPONSE_GET_GARAGE_BY_ID);
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
        setContentView(R.layout.activity_booking_detail);
        initToolbar();
        initBookingDetailElements();
        if(SocketIOClient.client == null){
            new SocketIOClient();
        }
        SocketIOClient.client.mSocket.emit(Constant.REQUEST_PARKING_INFO_BY_ACCOUNT_ID,
                new LocalData(getApplicationContext()).getId());
        SocketIOClient.client.mSocket.on(Constant.RESPONSE_PARKING_INFO_BY_ACCOUNT_ID,
                onResponseGetStatusParkingInfo);


    }

    private Emitter.Listener onResponseGetStatusParkingInfo = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject jsonObject = (JSONObject) args[0];
                    Gson gson = new Gson();
                    try {
                        if (jsonObject.getBoolean(Constant.RESULT)) {
                            mParkingInfoModel = gson.fromJson(
                                    jsonObject.getJSONObject(Constant.DATA).toString(),
                                    ParkingInfoModel.class);
                            if (mParkingInfoModel.getParkingStatus() == Constant.PARKING_INFO_STATUS_BOOKED) {
                                tvBookingDetailTime.setText(mParkingInfoModel.getTimeBookedFormatted());
                                requestGaraDetail(mParkingInfoModel.getGarageID());
                                requestCarDetail(mParkingInfoModel.getCarID());
                            }

                        } else {
                            Log.e("Server", jsonObject.getString(Constant.MESSAGE));
                        }
                        SocketIOClient.client.mSocket.off(Constant.RESPONSE_PARKING_INFO_BY_ACCOUNT_ID);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        }
    };

    private Emitter.Listener onResponseGetCarById = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject jsonObject = (JSONObject) args[0];
                    Gson gson = new Gson();
                    try {
                        if (jsonObject.getBoolean(Constant.RESULT)) {
                            mCarModel = gson.fromJson(
                                    jsonObject.getJSONArray(Constant.DATA).getJSONObject(0).toString(),
                                    CarModel.class);
                            tvBookingDetailLicense.setText(mCarModel.getVehicleNumber());

                        } else {
                            Log.e("Server", jsonObject.getString(Constant.MESSAGE));
                        }
                        SocketIOClient.client.mSocket.off(Constant.RESPONSE_FIND_CAR_BY_ID);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        }
    };

    private void requestGaraDetail(int garaID) {
        SocketIOClient.client.mSocket.emit(Constant.REQUEST_GET_GARAGE_BY_ID, garaID);
        SocketIOClient.client.mSocket.on(Constant.RESPONSE_GET_GARAGE_BY_ID, onResponseGetGaraById);
    }

    private void requestCarDetail(int carId) {
        SocketIOClient.client.mSocket.emit(Constant.REQUEST_FIND_CAR_BY_ID, carId);
        SocketIOClient.client.mSocket.on(Constant.RESPONSE_FIND_CAR_BY_ID, onResponseGetCarById);
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_booking_image);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();

        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
    }

    private void initBookingDetailElements() {
        localData = new LocalData(getApplicationContext());
        mMapImage = (ImageView) findViewById(R.id.iv_map);
        tvRemainSlots = (TextView) findViewById(R.id.tv_booking_detail_remain);

        btnBookCancel = (Button) findViewById(R.id.btn_book_cancel);
        btnBookCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelBooking();
            }
        });
        btnBookRefresh = (Button) findViewById(R.id.btn_book_refresh);

        tvBookingDetailTitle = (TextView) findViewById(R.id.tv_booking_detail_title);
        tvBookingDetailDes = (TextView) findViewById(R.id.tv_booking_detail_des);
        tvBookingDetailTime = (TextView) findViewById(R.id.tv_booking_detail_time);
        tvBookingDetailLicense = (TextView) findViewById(R.id.tv_booking_detail_license);
        tvBookingDetailDuration = (TextView) findViewById(R.id.tv_duration_booking_detail);
        tvBookingDetailDistance = (TextView) findViewById(R.id.tv_distance_booking_detail);

    }

    @SuppressLint("SimpleDateFormat")
    private void initBookingDetailContent() {
        Picasso.with(this).load(getMapImageUrl()).into(mMapImage);

        if (mGaraModel.getRemainSlot() <= 0) {
            tvRemainSlots.setText(getResources().getString(R.string.booking_slot_not_available));
            tvRemainSlots.setTextColor(getResources().getColor(R.color.colorNotAvailable));
        } else {
            tvRemainSlots.setText(getResources().getString(R.string.booking_slot_available,
                    mGaraModel.getRemainSlot(),
                    mGaraModel.getTotalSlot()));
            tvRemainSlots.setTextColor(getResources().getColor(R.color.colorAvailable));
        }

        tvBookingDetailTitle.setText(mGaraModel.getName());
        getLocationAddress(mGaraModel.getLocation());

        String directionsUrl = getDirectionsUrl(
                new LatLng(MapActivity.mLastKnownLocation.getLatitude(),
                        MapActivity.mLastKnownLocation.getLongitude()),
                mGaraModel.getPosition(), false);

        new GetDirectionApiData(mGaraModel) {
            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                new GetLocationDistanceDuration(mGaraModel) {

                    @Override
                    protected void onPostExecute(LocationDataModel result) {
                        tvBookingDetailDistance.setText(result.getDistance());
                        tvBookingDetailDuration.setText(result.getDuration());
                    }
                }.execute(result);

            }
        }.execute(directionsUrl);
    }

    private String getMapImageUrl() {
        return getResources().getString(
                R.string.book_url_map_image,
                mGaraModel.getLocationX() + "," + mGaraModel.getLocationY(),
                Constant.DEFAULT_ZOOM,
                Constant.BOOKING_MAP_SIZE,
                mGaraModel.getLocationX() + "," + mGaraModel.getLocationY()).replaceAll(" ", "");

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                return false;
        }
        return super.onOptionsItemSelected(item);
    }

    public String getDirectionsUrl(LatLng origin, LatLng dest, Boolean redirect) {

        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        String sensor = "sensor=false";
        String mode = "mode=driving";
        String language = "language=" + Locale.getDefault().getLanguage();
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode + "&" + language;
        String output = "json";
        String url;
        if (redirect) {
            url = getResources().getString(R.string.google_direction_api_redirect, parameters);
        } else {
            url = getResources().getString(R.string.google_direction_api, output, parameters);
        }
        Log.d("APIUrl", url);

        return url;
    }

    protected void getLocationAddress(Location location) {
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        AddressResultReceiver mResultReceiver = new AddressResultReceiver(new Handler());
        intent.putExtra(Constant.RECEIVER, mResultReceiver);
        intent.putExtra(Constant.LOCATION_DATA_EXTRA, location);
        startService(intent);
    }


    private class AddressResultReceiver extends ResultReceiver {
        AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            String mAddressOutput = resultData.getString(Constant.RESULT_DATA_KEY);
            String mAddressTitle = resultData.getString(Constant.RESULT_TITLE);
            tvBookingDetailDes.setText(mAddressOutput);

        }
    }


    private Emitter.Listener onResponseCancelBooking = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject jsonObject = (JSONObject) args[0];
                    try {
                        if (jsonObject.getBoolean(Constant.RESULT)) {
                            Toast.makeText(BookingDetailActivity.this,
                                    getResources().getString(R.string.book_cancel_successfull),
                                    Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(BookingDetailActivity.this, MapActivity.class);
                            intent.putExtra(Constant.BOOKING_DETAIL_STATUS,
                                    Constant.BOOKING_DETAIL_STATUS_CANCEL);
                            setResult(RESULT_OK, intent);
                            finish();
                        } else {
                            Log.d("Cancel book", jsonObject.getString(Constant.MESSAGE));
                        }
                        SocketIOClient.client.mSocket.off(Constant.RESPONSE_EDIT_PARKING_INFO_BY_ID_STATUS);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        }
    };

    private void cancelBooking() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialog_book_cancel_message)
                .setPositiveButton(R.string.fire, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        SocketIOClient.client.mSocket.emit(
                                Constant.REQUEST_EDIT_PARKING_INFO_BY_ID_STATUS,
                                mParkingInfoModel.getId(), Constant.PARKING_INFO_STATUS_CANCEL);
                        SocketIOClient.client.mSocket.on(
                                Constant.RESPONSE_EDIT_PARKING_INFO_BY_ID_STATUS,
                                onResponseCancelBooking);
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
}
