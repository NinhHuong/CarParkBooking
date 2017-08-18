package com.quocngay.carparkbooking.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fenchtose.tooltip.Tooltip;
import com.fenchtose.tooltip.TooltipAnimation;
import com.github.nkzawa.emitter.Emitter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.quocngay.carparkbooking.R;
import com.quocngay.carparkbooking.model.GarageModel;
import com.quocngay.carparkbooking.model.LocalData;
import com.quocngay.carparkbooking.model.LocationDataModel;
import com.quocngay.carparkbooking.model.ParkingInfoModel;
import com.quocngay.carparkbooking.model.UserModel;
import com.quocngay.carparkbooking.other.AddressResult;
import com.quocngay.carparkbooking.other.Constant;
import com.quocngay.carparkbooking.other.SocketIOClient;
import com.quocngay.carparkbooking.services.FetchAddressIntentService;
import com.quocngay.carparkbooking.tasks.DirectionParserTask;
import com.quocngay.carparkbooking.tasks.DownloadTask;
import com.quocngay.carparkbooking.tasks.GetDirectionApiData;
import com.quocngay.carparkbooking.tasks.GetLocationDistanceDuration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MapActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String BTN_STATUS_FIND = "find";
    private static final String BTN_STATUS_CHOOSE = "book";
    private static final String BTN_STATUS_BOOK_DETAIL = "cancel";

    public static List<GarageModel> garageModelList;
    public static Location mLastKnownLocation;

    private GoogleMap googleMap;
    private boolean mLocationPermissionGranted;
    private GoogleApiClient mGoogleApiClient;
    private CameraPosition mCameraPosition;
    private FloatingActionButton btnMyLocation;
    private TextView tvAddressTitle, tvAddressDescription, tvAddressDuration, tvAddressDistance;
    private CardView cardViewMarkerInfo;
    private Marker mOrtherMarker, mSelectedGaraMarker;
    private Button btnChoose, btnFind, btnBookDetail;
    private Polyline mPolyline;
    private UserModel mUserModel;
    private FloatingActionButton btnGgDirection;
    private ViewGroup root;
    private ParkingInfoModel mParkingInfoModel;
    private TextView tvGarageSlots;
    private LocalData localData;
    private TextView tvNavHeaderName;
    private List<Marker> garaMarkerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        localData = new LocalData(getApplicationContext());
        initMapActivity();

    }

    @Override
    public void onMapReady(final GoogleMap map) {
        googleMap = map;
        initMap();
        updateLocationUI();
        getDeviceLocation();
        getAllGarages();
        initMapCheckBooking();
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
                            garageModelList = new ArrayList<>();
                            garaMarkerList = new ArrayList<>();

                            for (int i = 0; i < listJsonGaras.length(); i++) {
                                GarageModel garageModel =
                                        gson.fromJson(listJsonGaras.getJSONObject(i).toString(),
                                                GarageModel.class);
                                garageModelList.add(garageModel);
                                garaMarkerList.add(addCustomGaraMarker(garageModel));
                            }
                            SocketIOClient.client.mSocket.off(Constant.RESPONSE_GET_ALL_GARAGES);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        };

        Emitter.Listener onResponseGarageUpdated = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject jsonObject = (JSONObject) args[0];
                        Log.d("Garas", jsonObject.toString());
                        Gson gson = new Gson();
                        try {
                            if (jsonObject.getBoolean(Constant.RESULT)
                                    && garaMarkerList.size() > 0) {
                                GarageModel resultGara = gson.fromJson(
                                        jsonObject
                                                .getJSONArray(Constant.DATA)
                                                .getJSONObject(0).toString(),
                                        GarageModel.class);
                                for (Marker marker : garaMarkerList) {
                                    GarageModel garageModel = (GarageModel) marker.getTag();
                                    assert garageModel != null;
                                    if (resultGara.getId() == garageModel.getId()) {
                                        marker = addCustomGaraMarker(resultGara);
                                        Log.d(getClass().getName(),
                                                "Updated marker: " + marker.getTag());
                                    }
                                }

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        };

        SocketIOClient.client.mSocket.emit(Constant.REQUEST_GET_ALL_GARAGES);
        SocketIOClient.client.mSocket.on(Constant.RESPONSE_GET_ALL_GARAGES, onResponseGetAllGarages);
        SocketIOClient.client.mSocket.off(Constant.RESPONSE_GARAGE_UPDATED);
        SocketIOClient.client.mSocket.on(Constant.RESPONSE_GARAGE_UPDATED, onResponseGarageUpdated);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case Constant.REQUEST_CODE_AUTOCOMPLETE:
                if (resultCode == RESULT_OK) {
                    Place place = PlaceAutocomplete.getPlace(this, data);
                    Log.i("TAG", "Place: " + place.getName());

                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                            place.getLatLng(), Constant.DEFAULT_ZOOM));

                    Location location = new Location("");
                    double locationLatitude = place.getLatLng().latitude;
                    double locationLongitude = place.getLatLng().longitude;
                    location.setLatitude(locationLatitude);
                    location.setLongitude(locationLongitude);
                    addMarker(location);
                } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                    Status status = PlaceAutocomplete.getStatus(this, data);
                    Log.i("TAG", status.getStatusMessage());

                }
                break;
            case Constant.REQUEST_CODE_BOOKING:
                if (resultCode == RESULT_OK) {
                    Boolean bookingStatus = data.getBooleanExtra(Constant.BOOKING_STATUS, false);
                    if (bookingStatus) {
                        if (mPolyline != null) {
                            mPolyline.remove();
                        }
                        final LatLng dest = mSelectedGaraMarker.getPosition();
                        final LatLng origin = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
                        btnGgDirection.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                        Uri.parse(getDirectionsUrl(origin, dest, true)));
                                startActivity(intent);
                            }
                        });
                        //Draw direction to destination point
                        String url = getDirectionsUrl(origin, dest, false);
                        GetAPIData getAPIData = new GetAPIData();
                        getAPIData.execute(url);
                        initMapCheckBooking();
                    }
                }
                break;
            case Constant.REQUEST_CODE_NEAREST:
                if (resultCode == RESULT_OK) {
                    LocationDataModel locationDataModel = (LocationDataModel) data.getSerializableExtra(NearestGaraActivity.GARA_SELECTED);
                    setInfoViewContent(locationDataModel.getGarageModel().getName(),
                            locationDataModel.getGarageModel().getAddress(),
                            locationDataModel.getDuration(),
                            locationDataModel.getDistance());
                    Location location = new Location("");
                    location.setLatitude(Double.valueOf(locationDataModel.getGarageModel().getLocationX()));
                    location.setLongitude(Double.valueOf(locationDataModel.getGarageModel().getLocationY()));
                    getLocationAddress(location);
                    mCameraPosition = new CameraPosition(locationDataModel.getGarageModel().getPosition(),
                            Constant.DEFAULT_ZOOM + 1, 0, 0);
                    for (Marker marker : garaMarkerList) {
                        if (((marker.getTag()) != null ?
                                ((GarageModel) marker.getTag()).getId() : 0) ==
                                locationDataModel.getGarageModel().getId()) {
                            mSelectedGaraMarker = marker;
                        }
                    }
                    setMarkerInfo(mSelectedGaraMarker);
                    btnMapStatus(BTN_STATUS_CHOOSE);
                }
                break;
            case Constant.REQUEST_CODE_BOOKING_DETAIL:
                if (resultCode == RESULT_OK) {
                    if (data.getStringExtra(Constant.BOOKING_DETAIL_STATUS)
                            .equals(Constant.BOOKING_DETAIL_STATUS_CANCEL))
                        initMapGeneralStatus();
                }

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCameraPosition = googleMap.getCameraPosition();
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
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.map, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_search) {
            openAutocompleteActivity();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_profile:
                Intent profileIntent = new Intent(MapActivity.this, ProfileActivity.class);
                startActivity(profileIntent);
                break;
            case R.id.nav_history:
                Intent historyIntent = new Intent(MapActivity.this, HistoryActivity.class);
                startActivity(historyIntent);
                break;
            case R.id.nav_car_manager:
                Intent carManagerIntent = new Intent(MapActivity.this, CarManagerActivity.class);
                startActivity(carManagerIntent);
                break;
            case R.id.nav_logout:
                actionLogout();
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        root = (ViewGroup) findViewById(R.id.map_view);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        toggle.setDrawerSlideAnimationEnabled(true);
    }

    private void initMapActivity() {

//        Intent notificationIntent = new Intent(getApplicationContext(), NotificationIntentService.class);
//        getApplicationContext().startService(notificationIntent);

        initToolbar();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        tvNavHeaderName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.tv_nav_header_name);
        TextView tvNavHeaderEmail = (TextView) navigationView.getHeaderView(0).findViewById(R.id.tv_nav_header_email);
        tvNavHeaderEmail.setText(localData.getEmail());

        getUserDetail();
        MenuItem menuItem = navigationView.getMenu().findItem(R.id.nav_logout);
        SpannableString s = new SpannableString(menuItem.getTitle());
        s.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.map_direction)), 0, s.length(), 0);
        menuItem.setTitle(s);

        initGoogleApi();
        initElements();
    }

    private Emitter.Listener onResponseBookingCanceled = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject jsonObject = (JSONObject) args[0];
                    try {
                        if (jsonObject.getBoolean(Constant.RESULT)) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this);
                            builder.setTitle(R.string.dialog_booking_canceled_title)
                                    .setMessage(R.string.dialog_booking_canceled_message)
                                    .setPositiveButton(R.string.dialog_button_ok, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.dismiss();
                                        }
                                    });
                            builder.create().show();
                            initMapGeneralStatus();
                        } else {
                            initMapBookedStatus();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        }
    };

    private void initMapCheckBooking() {
        Emitter.Listener onResponseGetStatusParkingInfo = new Emitter.Listener() {
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

                            } else {
                                Log.e("Server", jsonObject.getString(Constant.MESSAGE));
                            }
                            if (mParkingInfoModel != null &&
                                    mParkingInfoModel.getParkingStatus() ==
                                            Constant.PARKING_INFO_STATUS_BOOKED) {
                                initMapBookedStatus();
                            } else {
                                initMapGeneralStatus();
                            }
                            SocketIOClient.client.mSocket.off(
                                    Constant.RESPONSE_PARKING_INFO_BY_ACCOUNT_ID);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

            }
        };


        SocketIOClient.client.mSocket.emit(Constant.REQUEST_PARKING_INFO_BY_ACCOUNT_ID,
                localData.getId());
        SocketIOClient.client.mSocket.on(Constant.RESPONSE_PARKING_INFO_BY_ACCOUNT_ID,
                onResponseGetStatusParkingInfo);
        SocketIOClient.client.mSocket.off(Constant.RESPONSE_BOOKING_CANCELED);
        SocketIOClient.client.mSocket.on(Constant.RESPONSE_BOOKING_CANCELED,
                onResponseBookingCanceled);

    }

    private void getUserDetail() {
        Emitter.Listener onResponseFindUserByAccId = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject jsonObject = (JSONObject) args[0];
                        Gson gson = new Gson();
                        try {
                            if (jsonObject.getBoolean(Constant.RESULT)) {
                                mUserModel = gson.fromJson(jsonObject.getJSONArray(Constant.DATA)
                                        .getJSONObject(0).toString(), UserModel.class);
                                tvNavHeaderName.setText(mUserModel.getFullName());
                            } else {
                                Log.w(getClass().getName(), jsonObject.getString(Constant.MESSAGE));
                            }
                            SocketIOClient.client.mSocket.off(
                                    Constant.RESPONSE_FIND_USER_BY_ACCOUNT_ID);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        };

        SocketIOClient.client.mSocket.emit(Constant.REQUEST_FIND_USER_BY_ACCOUNT_ID,
                localData.getId());
        SocketIOClient.client.mSocket.on(Constant.RESPONSE_FIND_USER_BY_ACCOUNT_ID,
                onResponseFindUserByAccId);
    }

    private void initElements() {
        btnMyLocation = (FloatingActionButton) findViewById(R.id.btnMyLocation);
        btnGgDirection = (FloatingActionButton) findViewById(R.id.btnDirection);

        tvAddressTitle = (TextView) findViewById(R.id.tv_info_title);
        tvAddressDescription = (TextView) findViewById(R.id.tv_info_description);

        tvAddressDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty()) {
                    cardViewMarkerInfo.setVisibility(View.GONE);
                } else {
                    cardViewMarkerInfo.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        cardViewMarkerInfo = (CardView) findViewById(R.id.cvAddress);
        cardViewMarkerInfo.setVisibility(View.GONE);

        tvAddressDuration = (TextView) findViewById(R.id.tv_duration);
        tvAddressDistance = (TextView) findViewById(R.id.tv_distance);
        tvGarageSlots = (TextView) findViewById(R.id.tv_info_slots);

        btnChoose = (Button) findViewById(R.id.btnChooseGara);
        btnFind = (Button) findViewById(R.id.btnFindGara);
        btnBookDetail = (Button) findViewById(R.id.btnBookDetail);
        btnMapStatus(BTN_STATUS_FIND);
    }

    private void initGoogleApi() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
        mGoogleApiClient.connect();
    }

    private void showToolTip(View anchorView, View contentView) {
        new Tooltip.Builder(getApplicationContext())
                .anchor(anchorView, Tooltip.LEFT)
                .content(contentView)
                .animate(new TooltipAnimation(TooltipAnimation.FADE, 500))
                .autoAdjust(true)
                .autoCancel(2000)
                .into(root)
                .withTip(new Tooltip.Tip(15, 15, getResources().getColor(R.color.colorPrimary))).show();
    }

    private void openAutocompleteActivity() {
        try {
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN).build(this);
            startActivityForResult(intent, Constant.REQUEST_CODE_AUTOCOMPLETE);
        } catch (GooglePlayServicesRepairableException e) {
            GoogleApiAvailability.getInstance().getErrorDialog(this, e.getConnectionStatusCode(), 0 /* requestCode */).show();
        } catch (GooglePlayServicesNotAvailableException e) {
            String message = "Google Play Services is not available: " +
                    GoogleApiAvailability.getInstance().getErrorString(e.errorCode);

            Log.e("TAG", message);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }


    private void setInfoViewContent(String title, String description, String duration, String distance) {
        tvAddressTitle.setText(title);
        tvAddressDescription.setText(description);
        tvAddressDistance.setText(distance);
        tvAddressDuration.setText(duration);
    }


    private void initMap() {
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        btnMyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLastKnownLocation != null)
                    googleMap.animateCamera(CameraUpdateFactory.newLatLng(
                            new LatLng(mLastKnownLocation.getLatitude(),
                                    mLastKnownLocation.getLongitude())));
            }
        });
    }

    private void initMapBookedStatus() {
        btnMapStatus(BTN_STATUS_BOOK_DETAIL);
        googleMap.setOnMapClickListener(null);

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                if (mOrtherMarker != null) {
                    mOrtherMarker.remove();
                }
                mSelectedGaraMarker = marker;
                Location location = new Location("");
                location.setLatitude(marker.getPosition().latitude);
                location.setLongitude(marker.getPosition().longitude);
                getLocationAddress(location);
                setMarkerInfo(marker);

                return false;
            }
        });
    }

    private void initMapGeneralStatus() {
        btnGgDirection.setVisibility(View.GONE);
        mParkingInfoModel = null;
        if (mPolyline != null) {
            mPolyline.remove();
        }
        if(mSelectedGaraMarker != null) {
            btnMapStatus(BTN_STATUS_CHOOSE);
        }else{
            btnMapStatus(BTN_STATUS_FIND);
        }
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                cardViewMarkerInfo.setVisibility(View.GONE);
                mSelectedGaraMarker = null;
                btnMapStatus(BTN_STATUS_FIND);
                if (mOrtherMarker != null) {
                    mOrtherMarker.remove();
                }

            }
        });

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                if (mOrtherMarker != null) {
                    mOrtherMarker.remove();
                }
                mSelectedGaraMarker = marker;
                Location location = new Location("");
                location.setLatitude(marker.getPosition().latitude);
                location.setLongitude(marker.getPosition().longitude);
                getLocationAddress(location);
                setMarkerInfo(marker);

                btnMapStatus(BTN_STATUS_CHOOSE);
                return false;
            }
        });
    }

    private void setMarkerInfo(final Marker marker) {
        String directionUrl = getDirectionsUrl(
                new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()),
                marker.getPosition(),
                false);
        if (marker.getTag() == null) {
            tvAddressDuration.setText("");
            tvAddressDistance.setText("");
            tvGarageSlots.setText("");
        } else {
            GarageModel garageModel = (GarageModel) marker.getTag();
            tvGarageSlots.setText(garageModel.getRemainSlot() + " / " + garageModel.getTotalSlot());
            if (garageModel.getRemainSlot() <= 0) {
                tvGarageSlots.setTextColor(getResources().getColor(R.color.colorNotAvailable));
            } else {
                tvGarageSlots.setTextColor(getResources().getColor(R.color.colorAvailable));
            }
            new GetDirectionApiData((GarageModel) marker.getTag()) {
                @Override
                protected void onPostExecute(String result) {
                    super.onPostExecute(result);

                    new GetLocationDistanceDuration((GarageModel) marker.getTag()) {

                        @Override
                        protected void onPostExecute(LocationDataModel result) {
                            tvAddressDuration.setText(result.getDuration());
                            tvAddressDistance.setText(result.getDistance());
                        }
                    }.execute(result);

                }
            }.execute(directionUrl);
        }
    }

    private void btnMapStatus(String status) {

        switch (status) {
            case BTN_STATUS_FIND:
                btnBookDetail.setVisibility(View.GONE);
                btnChoose.setVisibility(View.GONE);
                btnFind.setVisibility(View.VISIBLE);
                btnFind.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MapActivity.this, NearestGaraActivity.class);
                        intent.putExtra(Constant.MY_LOCATION, mLastKnownLocation == null ? new Location("") : mLastKnownLocation);
                        startActivityForResult(intent, Constant.REQUEST_CODE_NEAREST);
                    }
                });
                break;
            case BTN_STATUS_CHOOSE:
                btnBookDetail.setVisibility(View.GONE);
                btnFind.setVisibility(View.GONE);
                btnChoose.setVisibility(View.VISIBLE);
                btnChoose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent bookingIntent = new Intent(getApplicationContext(), BookingActivity.class);
                        bookingIntent.putExtra(Constant.GARA_DETAIL, (GarageModel) mSelectedGaraMarker.getTag());
                        bookingIntent.putExtra(Constant.MY_LOCATION, mLastKnownLocation == null ? new Location("") : mLastKnownLocation);
                        startActivityForResult(bookingIntent, Constant.REQUEST_CODE_BOOKING);
                    }
                });
                break;
            case BTN_STATUS_BOOK_DETAIL:

                btnFind.setVisibility(View.GONE);
                btnChoose.setVisibility(View.GONE);
                btnBookDetail.setVisibility(View.VISIBLE);
                btnBookDetail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent bookingIntent = new Intent(getApplicationContext(), BookingDetailActivity.class);
                        startActivityForResult(bookingIntent, Constant.REQUEST_CODE_BOOKING_DETAIL);
                        //cancelBooking();
                    }
                });
                break;
        }
    }


    private Marker addCustomGaraMarker(GarageModel garageModel) {
        getLocationAddressForMarker(garageModel);
        Marker marker = googleMap.addMarker(new MarkerOptions()
                .position(garageModel.getPosition())
                .icon(BitmapDescriptorFactory.fromBitmap(
                        getMarkerBitmapFromView(garageModel.getRemainSlot()))));
        marker.setTag(garageModel);
        return marker;
    }

    private Bitmap getMarkerBitmapFromView(int slotNumber) {

        View customMarkerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker, null);
        customMarkerView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        TextView markerImageView = (TextView) customMarkerView.findViewById(R.id.tv_slot_number);

        if (slotNumber <= 0) {
            customMarkerView.setBackground(getResources().getDrawable(R.mipmap.ic_marker_gara_gray));
            markerImageView.setText(String.valueOf(0));
        } else {
            customMarkerView.setBackground(getResources().getDrawable(R.mipmap.ic_marker_gara_red));
            markerImageView.setText(String.valueOf(slotNumber));
        }

        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());
        customMarkerView.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = customMarkerView.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        customMarkerView.draw(canvas);
        return returnedBitmap;
    }

    protected void getLocationAddress(Location location) {
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        AddressResultReceiver mResultReceiver = new AddressResultReceiver(new Handler());
        intent.putExtra(Constant.RECEIVER, mResultReceiver);
        intent.putExtra(Constant.LOCATION_DATA_EXTRA, location);
        startService(intent);
    }

    protected void getLocationAddressForMarker(GarageModel garageModel) {
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        AddressResult mResultReceiver = new AddressResult(new Handler(), garageModel);
        intent.putExtra(Constant.RECEIVER, mResultReceiver);
        intent.putExtra(Constant.LOCATION_DATA_EXTRA, garageModel.getLocation());
        startService(intent);
    }

    //TODO: is location enabled (temp)
    private boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }
            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void updateLocationUI() {
        if (googleMap == null) {
            return;
        }

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    Constant.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

        if (mLocationPermissionGranted) {
            googleMap.setMyLocationEnabled(true);
        } else {
            googleMap.setMyLocationEnabled(false);
            mLastKnownLocation = null;
        }
    }

    private void getDeviceLocation() {

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    Constant.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

        if (mLocationPermissionGranted) {
            mLastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }

        //TODO: gete bug when gps is not enabled
        // Set the map's camera position to the current location of the device.
        if (mCameraPosition != null) {
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));
        } else if (mLastKnownLocation != null) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(mLastKnownLocation.getLatitude(),
                            mLastKnownLocation.getLongitude()), Constant.DEFAULT_ZOOM));
        } else {
            Log.d("TAG", "Current location is null. Using defaults.");
            LatLng mDefaultLocation = new LatLng(-33.852, 151.211);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, Constant.DEFAULT_ZOOM));
            googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        }

    }

    private void addMarker(Location location) {
        if (mOrtherMarker != null) {
            mOrtherMarker.remove();
        }
        getLocationAddress(location);
        mOrtherMarker = googleMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case Constant.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest, Boolean redirect) {

        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        String mode = "mode=driving";
        String language = "language=" + Locale.getDefault().getLanguage();
        String parameters = str_origin + "&" + str_dest + "&" + mode + "&" + language;
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

    public void centerIncidentRouteOnMap(List<LatLng> copiedPoints) {
        double minLat = Integer.MAX_VALUE;
        double maxLat = Integer.MIN_VALUE;
        double minLon = Integer.MAX_VALUE;
        double maxLon = Integer.MIN_VALUE;
        for (LatLng point : copiedPoints) {
            maxLat = Math.max(point.latitude, maxLat);
            minLat = Math.min(point.latitude, minLat);
            maxLon = Math.max(point.longitude, maxLon);
            minLon = Math.min(point.longitude, minLon);
        }
        final LatLngBounds bounds = new LatLngBounds.Builder().include(new LatLng(maxLat, maxLon)).include(new LatLng(minLat, minLon)).build();
        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
    }


    private class AddressResultReceiver extends ResultReceiver {
        AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            String mAddressOutput = resultData.getString(Constant.RESULT_DATA_KEY);
            String mAddressTitle = resultData.getString(Constant.RESULT_TITLE);
            tvAddressDescription.setText(mAddressOutput);
            tvAddressTitle.setText(mAddressTitle);

        }
    }

    private class GetAPIData extends DownloadTask {

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            new GetLocationDirection().execute(result);

        }
    }

    private class GetLocationDirection extends DirectionParserTask {

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;
            String distance = "";
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();

                List<HashMap<String, String>> path = result.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    if (j == 0) {
                        distance = point.get("distance");
                        continue;
                    } else if (j == 1) {
                        continue;
                    }
                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
                    points.add(position);
                }

                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(getResources().getColor(R.color.map_direction));
                lineOptions.geodesic(true);
                btnGgDirection.setVisibility(View.VISIBLE);
                FrameLayout contentView = (FrameLayout) getLayoutInflater().inflate(R.layout.tooltip, null);
                showToolTip(btnGgDirection, contentView);
                centerIncidentRouteOnMap(points);

            }
            tvAddressDistance.setText(distance);
            mPolyline = googleMap.addPolyline(lineOptions);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (googleMap != null) {
            outState.putParcelable(Constant.KEY_CAMERA_POSITION, googleMap.getCameraPosition());
            outState.putParcelable(Constant.KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }

    private void actionLogout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialog_logout_message)
                .setPositiveButton(R.string.fire, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        localData.clearData();
                        Intent intent = new Intent(MapActivity.this, LoginActivity.class);
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

}
