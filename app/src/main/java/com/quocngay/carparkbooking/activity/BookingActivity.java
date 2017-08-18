package com.quocngay.carparkbooking.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.quocngay.carparkbooking.LicenseNumRecyclerViewAdapter;
import com.quocngay.carparkbooking.R;
import com.quocngay.carparkbooking.model.CarModel;
import com.quocngay.carparkbooking.model.GarageModel;
import com.quocngay.carparkbooking.model.LocationDataModel;
import com.quocngay.carparkbooking.model.LocalData;
import com.quocngay.carparkbooking.other.Constant;
import com.quocngay.carparkbooking.other.SocketIOClient;
import com.quocngay.carparkbooking.tasks.GetDirectionApiData;
import com.quocngay.carparkbooking.tasks.GetLocationDistanceDuration;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class BookingActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private LicenseNumRecyclerViewAdapter recyclerViewAdapter;
    private List<CarModel> licenseList;
    private TextView tvLicenseNumber;
    private TextView tvGaraDuration;
    private TextView tvGaraDistance;
    private GarageModel markerGara;
    private LocalData localData;
    private CarModel mCurrentCar;
    private AlertDialog.Builder mBookAlertDialog;
    private Location mMyLocation;
    private ImageButton btnAddLicense;
    private Dialog dalAddLinense;
    OnListInteractionListener listener;

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
                            markerGara = gson.fromJson(
                                    jsonObject.getJSONArray(Constant.DATA).getJSONObject(0).toString(),
                                    GarageModel.class);
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
        setContentView(R.layout.activity_booking);
        initToolbar();
        localData = new LocalData(getApplicationContext());
        markerGara = (GarageModel) getIntent().getSerializableExtra(Constant.GARA_DETAIL);
        mMyLocation = getIntent().getParcelableExtra(Constant.MY_LOCATION);
        requestGaraDetail();
        initBookingElements();
        initLicenseList();

    }

    private void requestGaraDetail() {
        SocketIOClient.client.mSocket.emit(Constant.REQUEST_GET_GARAGE_BY_ID,
                markerGara.getId());
        SocketIOClient.client.mSocket.on(Constant.RESPONSE_GET_GARAGE_BY_ID, onResponseGetGaraById);
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_booking_image);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();

        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
    }

    @SuppressLint("SimpleDateFormat")
    private void initBookingElements() {
        ImageView imageView = (ImageView) findViewById(R.id.iv_map);
        Picasso.with(this).load(getMapImageUrl()).into(imageView);
        TextView tvRemainSlots = (TextView) findViewById(R.id.tv_gara_remain);

        if (markerGara.getRemainSlot() <= 0) {
            tvRemainSlots.setText(getResources().getString(R.string.booking_slot_not_available));
            tvRemainSlots.setTextColor(getResources().getColor(R.color.colorNotAvailable));
        } else {
            tvRemainSlots.setText(getResources().getString(R.string.booking_slot_available,
                    markerGara.getRemainSlot(),
                    markerGara.getTotalSlot()));
            tvRemainSlots.setTextColor(getResources().getColor(R.color.colorAvailable));
        }

        Button btnBook = (Button) findViewById(R.id.btn_book);
        if (markerGara.getRemainSlot() <= 0) {
            btnBook.setEnabled(false);
        }
        btnBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestGaraDetail();
                if (markerGara.getRemainSlot() <= 0) {
                    Toast.makeText(BookingActivity.this,
                            getResources().getString(R.string.booking_slot_not_available),
                            Toast.LENGTH_SHORT).show();
                } else if (tvLicenseNumber.getText().toString().isEmpty()) {
                    Toast.makeText(BookingActivity.this,
                            getResources().getString(R.string.booking_message_add_license),
                            Toast.LENGTH_SHORT).show();
                } else {
                    mBookAlertDialog = new AlertDialog.Builder(BookingActivity.this);
                    mBookAlertDialog.setTitle(getResources().getString(R.string.dialog_book_title));
                    mBookAlertDialog.setMessage(R.string.dialog_book_message)
                            .setPositiveButton(R.string.fire, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Calendar c = Calendar.getInstance();
                                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    String bookTime = df.format(c.getTime());
                                    SocketIOClient.client.mSocket.emit(
                                            Constant.REQUEST_ADD_NEW_PARKING_INFO_BY_USER,
                                            mCurrentCar.getId(),
                                            markerGara.getId(),
                                            bookTime,
                                            FirebaseInstanceId.getInstance().getToken());
                                    SocketIOClient.client.mSocket.on(
                                            Constant.RESPONSE_ADD_NEW_PARKING_INFO_BY_USER,
                                            onResponseAddParkingInfo);
                                }
                            })
                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });
                    mBookAlertDialog.create().show();
                }
            }
        });

        TextView tvGaraTitle = (TextView) findViewById(R.id.tv_gara_title);
        TextView tvGaraDes = (TextView) findViewById(R.id.tv_gara_des);
        tvGaraDuration = (TextView) findViewById(R.id.tv_duration_book);
        tvGaraDistance = (TextView) findViewById(R.id.tv_distance_book);

        String directionsUrl = getDirectionsUrl(
                new LatLng(mMyLocation.getLatitude(), mMyLocation.getLongitude()),
                markerGara.getPosition(), false);

        new GetDirectionApiData(markerGara) {
            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);

                new GetLocationDistanceDuration(markerGara) {

                    @Override
                    protected void onPostExecute(LocationDataModel result) {
                        tvGaraDistance.setText(result.getDistance());
                        tvGaraDuration.setText(result.getDuration());
                    }
                }.execute(result);

            }
        }.execute(directionsUrl);

        tvGaraTitle.setText(markerGara.getName());
        tvGaraDes.setText(markerGara.getAddress());

        tvLicenseNumber = (TextView) findViewById(R.id.tv_license_number);
        btnAddLicense = (ImageButton) findViewById(R.id.btn_add_license);
        btnAddLicense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initAddLicenseDialog();
            }
        });

        SocketIOClient.client.mSocket.emit(Constant.REQUEST_FIND_CAR_BY_ACCOUNT_ID, localData.getId());
        SocketIOClient.client.mSocket.on(Constant.RESPONSE_FIND_CAR_BY_ACCOUNT_ID, onResponseFindCar);
    }

    private void initLicenseList() {

        final BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(R.layout.dialog_license_list);
        dialog.setTitle(getResources().getString(R.string.title_licence_picker));
        ImageView fbtnAddLicense = (ImageView) dialog.findViewById(R.id.dl_fab_add_license);

        assert fbtnAddLicense != null;
        fbtnAddLicense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (licenseList.size() >= 5) {
                    Toast.makeText(BookingActivity.this,
                            getResources().getString(R.string.error_add_license_limit),
                            Toast.LENGTH_SHORT).show();
                } else {
                    initAddLicenseDialog();
                }
            }
        });

        mRecyclerView = (RecyclerView) dialog.findViewById(R.id.list_license);
        LinearLayoutManager layoutManager = new LinearLayoutManager(dialog.getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        listener = new OnListInteractionListener() {
            @Override
            public void onListInteraction(CarModel item) {
                tvLicenseNumber.setText(item.getVehicleNumber());
                mCurrentCar = item;
                dialog.dismiss();
            }

            @Override
            public void onListRemove(final CarModel item) {
                final Emitter.Listener onResponseRemoveCar = new Emitter.Listener() {
                    @Override
                    public void call(final Object... args) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                JSONObject jsonObject = (JSONObject) args[0];
                                try {
                                    if (jsonObject.getBoolean(Constant.RESULT)) {
                                        licenseList.remove(item);
                                        recyclerViewAdapter.notifyDataSetChanged();
                                    } else {
                                        Toast.makeText(BookingActivity.this, getResources().getString(R.string.error_server), Toast.LENGTH_SHORT).show();
                                    }
                                    SocketIOClient.client.mSocket.off(Constant.RESPONSE_REMOVE_CAR_BY_ID);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                };

                mBookAlertDialog = new AlertDialog.Builder(BookingActivity.this);
                mBookAlertDialog.setTitle(getResources().getString(R.string.dialog_remove_license_item));
                mBookAlertDialog.setMessage(R.string.dialog_remove_license_item_message)
                        .setPositiveButton(R.string.fire, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                SocketIOClient.client.mSocket.emit(Constant.REQUEST_REMOVE_CAR_BY_ID,
                                        item.getId());
                                SocketIOClient.client.mSocket.on(Constant.RESPONSE_REMOVE_CAR_BY_ID, onResponseRemoveCar);
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
        };

        tvLicenseNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerViewAdapter = new LicenseNumRecyclerViewAdapter(licenseList, listener);
                mRecyclerView.setAdapter(recyclerViewAdapter);
                dialog.show();
            }
        });

        tvLicenseNumber.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });
    }

    public interface OnListInteractionListener {
        void onListInteraction(CarModel item);

        void onListRemove(CarModel item);
    }

    private String getMapImageUrl() {
        return getResources().getString(
                R.string.book_url_map_image,
                markerGara.getLocationX() + "," + markerGara.getLocationY(),
                Constant.DEFAULT_ZOOM,
                Constant.BOOKING_MAP_SIZE,
                markerGara.getLocationX() + "," + markerGara.getLocationY()).replaceAll(" ", "");

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
                            finish();

                        } else {
                            Toast.makeText(BookingActivity.this,
                                    getResources().getString(R.string.error_book_new),
                                    Toast.LENGTH_SHORT).show();
                        }
                    SocketIOClient.client.mSocket.off(Constant.REQUEST_ADD_NEW_PARKING_INFO_BY_USER);
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
                                if (recyclerViewAdapter != null) {
                                    recyclerViewAdapter = new LicenseNumRecyclerViewAdapter(licenseList, listener);
                                    mRecyclerView.setAdapter(recyclerViewAdapter);
                                    recyclerViewAdapter.notifyDataSetChanged();
                                }
                            }
                            btnAddLicense.setVisibility(View.GONE);
                            mCurrentCar = licenseList.get(0);
                            tvLicenseNumber.setText(licenseList.get(0).getVehicleNumber());
                        } else {
                            btnAddLicense.setVisibility(View.VISIBLE);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    SocketIOClient.client.mSocket.off(Constant.RESPONSE_FIND_CAR_BY_ACCOUNT_ID);
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

    private Emitter.Listener onResponseAddCar = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject jsonObject = (JSONObject) args[0];
                    try {
                        if (jsonObject.getBoolean(Constant.RESULT)) {
                            dalAddLinense.dismiss();
                            Toast.makeText(BookingActivity.this,
                                    getResources().getString(R.string.success_add_license),
                                    Toast.LENGTH_SHORT).show();
                            dalAddLinense.dismiss();
                            SocketIOClient.client.mSocket.emit(Constant.REQUEST_FIND_CAR_BY_ACCOUNT_ID, localData.getId());
                            SocketIOClient.client.mSocket.on(Constant.RESPONSE_FIND_CAR_BY_ACCOUNT_ID, onResponseFindCar);

                        } else if (jsonObject.getString(Constant.MESSAGE).equals("car_limit")) {
                            Toast.makeText(BookingActivity.this,
                                    getResources().getString(R.string.error_add_license_limit),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(BookingActivity.this,
                                    getResources().getString(R.string.error_server),
                                    Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private void initAddLicenseDialog() {
        dalAddLinense = new Dialog(this);
        dalAddLinense.setTitle(R.string.dialog_add_car_title);
        dalAddLinense.setContentView(R.layout.dialog_add_license_number);
        Button btnDalAdd = (Button) dalAddLinense.findViewById(R.id.btn_continue);
        final EditText edtLicenseNumber = (EditText) dalAddLinense.findViewById(R.id.edt_license_number);
        btnDalAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String licenseNumber = edtLicenseNumber.getText().toString().replaceAll(" ", "").trim();
                if(licenseNumber.isEmpty()){
                    Toast.makeText(getApplicationContext(),
                            R.string.dialog_car_empty_message,
                            Toast.LENGTH_SHORT).show();
                }else {
                    SocketIOClient.client.mSocket.emit(
                            Constant.REQUEST_ADD_NEW_CAR, localData.getId(), licenseNumber);
                    SocketIOClient.client.mSocket.on(
                            Constant.RESPONSE_ADD_NEW_CAR, onResponseAddCar);
                }
            }
        });
        TextView tvDalCancel = (TextView) dalAddLinense.findViewById(R.id.tv_cancel);
        tvDalCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dalAddLinense.dismiss();
            }
        });
        dalAddLinense.show();
    }
}
