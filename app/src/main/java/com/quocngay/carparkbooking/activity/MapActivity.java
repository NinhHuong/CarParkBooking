package com.quocngay.carparkbooking.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
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
import com.quocngay.carparkbooking.other.Constant;
import com.quocngay.carparkbooking.other.FetchAddressIntentService;
import com.quocngay.carparkbooking.other.SocketIOClient;
import com.quocngay.carparkbooking.tasks.DirectionParserTask;
import com.quocngay.carparkbooking.tasks.DownloadTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap googleMap;
    private boolean mLocationPermissionGranted;
    private Location mLastKnownLocation;
    private GoogleApiClient mGoogleApiClient;
    private CameraPosition mCameraPosition;
    private LatLng mDefaultLocation;
    private Location mCurrentLocation;
    private int mMaxEntries = 50;
    private FloatingActionButton btnMyLocation;
    private TextView addressTitle;
    private TextView addressDescription;
    private String errorMessage = "";
    private AddressResultReceiver mResultReceiver;
    private Location clickedLocation;
    private CardView mCardView;
    private Marker mMarker;
    private String placeName = "";
    private Button btnBook;
    private Polyline mPolyline;
    private TextView tvDistance;
    private Toolbar toolbar;
    private LatLng selectedGara;
    public static List<GarageModel> garageModelList;
    private FloatingActionButton btnGgDirection;
    private ViewGroup root;

    private Emitter.Listener onResponseGetAllGarages = new Emitter.Listener() {
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
                        garageModelList = new ArrayList<GarageModel>();
                        for (int i = 0; i < listJsonGaras.length(); i++) {
                            GarageModel garageModel =
                                    gson.fromJson(listJsonGaras.getJSONObject(i).toString(), GarageModel.class);
                            garageModelList.add(garageModel);
                            placeCustomMarker(garageModel);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private void defaultToolbar() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        toggle.setDrawerSlideAnimationEnabled(true);
    }

    private void initMapActivity() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        root = (ViewGroup) findViewById(R.id.map_view);

        setSupportActionBar(toolbar);
        defaultToolbar();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        MenuItem menuItem = navigationView.getMenu().findItem(R.id.nav_logout);
        SpannableString s = new SpannableString(menuItem.getTitle());
        s.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.map_direction)), 0, s.length(), 0);
        menuItem.setTitle(s);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
        mGoogleApiClient.connect();
        btnMyLocation = (FloatingActionButton) findViewById(R.id.btnMyLocation);
        btnGgDirection = (FloatingActionButton) findViewById(R.id.btnDirection);

        addressTitle = (TextView) findViewById(R.id.tv_add_title);
        addressTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty()) {
                    mCardView.setVisibility(View.GONE);
                } else {
                    mCardView.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        addressDescription = (TextView) findViewById(R.id.tv_add_description);
        mCardView = (CardView) findViewById(R.id.cvAddress);
        mCardView.setVisibility(View.GONE);
        mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        tvDistance = (TextView) findViewById(R.id.tv_nearest);
        btnBook = (Button) findViewById(R.id.btnFindGara);

        if (selectedGara == null) {
            btnBook.setText(getResources().getString(R.string.map_findgara));
            btnBook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MapActivity.this, NearestGaraActivity.class);
                    intent.putExtra(Constant.MY_LOCATION, mLastKnownLocation == null ? new Location("") : mLastKnownLocation);
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        initMapActivity();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == Constant.REQUEST_CODE_AUTOCOMPLETE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.i("TAG", "Place: " + place.getName());
                placeName = place.getName().toString();
                Location location = new Location("");
                double locationLatitude = place.getLatLng().latitude;
                double locationLongitude = place.getLatLng().longitude;
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(locationLatitude, locationLongitude), Constant.DEFAULT_ZOOM));

                location.setLatitude(locationLatitude);
                location.setLongitude(locationLongitude);
                addMarker(location);
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                Log.i("TAG", status.getStatusMessage());

            }
        }
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Boolean bookingStatus = data.getBooleanExtra(Constant.BOOKING_STATUS, false);
                if (bookingStatus) {
                    if (mPolyline != null) {
                        mPolyline.remove();
                    }
                    final LatLng dest = selectedGara;
                    final LatLng origin = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
                    btnGgDirection.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                    Uri.parse(getDirectionsUrl(origin, dest, true)));
                            startActivity(intent);
                        }
                    });
                    String url = getDirectionsUrl(origin, dest, false);
                    GetAPIData getAPIData = new GetAPIData();
                    getAPIData.execute(url);
                }
            }
        }
    }

    @Override
    public void onMapReady(final GoogleMap map) {
        googleMap = map;
        initMap();
        updateLocationUI();
        getDeviceLocation();
        SocketIOClient.client.mSocket.off();
        SocketIOClient.client.mSocket.emit(Constant.REQUEST_GET_ALL_GARAGES);
        SocketIOClient.client.mSocket.on(Constant.RESPONSE_GET_ALL_GARAGES, onResponseGetAllGarages);
    }

    private void initMap() {
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        googleMap.getUiSettings().setMapToolbarEnabled(true);
        btnMyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLastKnownLocation != null)
                    googleMap.animateCamera(CameraUpdateFactory.newLatLng(
                            new LatLng(mLastKnownLocation.getLatitude(),
                                    mLastKnownLocation.getLongitude())));
            }
        });

        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                clickedLocation = new Location("");
                clickedLocation.setLatitude(latLng.latitude);
                clickedLocation.setLongitude(latLng.longitude);
                addMarker(clickedLocation);
            }
        });

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                selectedGara = marker.getPosition();
                if (selectedGara != null) {
                    btnBook.setText(getResources().getString(R.string.map_gara_choose));
                    btnBook.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent bookingIntent = new Intent(getApplicationContext(), BookingActivity.class);
                            bookingIntent.putExtra(Constant.GARA_LOCATION, selectedGara);
                            startActivityForResult(bookingIntent, Constant.REQUEST_CODE_BOOKING);
                        }
                    });
                }
                Location location = new Location("");
                location.setLatitude(marker.getPosition().latitude);
                location.setLongitude(marker.getPosition().longitude);
                startIntentService(location);
                return false;
            }
        });
    }

    private void placeCustomMarker(GarageModel garageModel) {
        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        Bitmap bmp = Bitmap.createBitmap(60, 60, conf);
        Canvas canvas1 = new Canvas(bmp);

        Paint color = new Paint(Paint.LINEAR_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
        color.setTextSize(16);
        color.setTextAlign(Paint.Align.CENTER);
        color.setFakeBoldText(true);
        color.setStyle(Paint.Style.FILL);
        color.setSubpixelText(true);
        color.setColor(Color.BLACK);

        if (garageModel.getRemainSlot() == 0) {
            canvas1.drawBitmap(BitmapFactory.decodeResource(getResources(),
                    R.mipmap.ic_marker_gara_gray), 0, 0, color);
        } else {
            canvas1.drawBitmap(BitmapFactory.decodeResource(getResources(),
                    R.mipmap.ic_marker_gara_red), 0, 0, color);
        }
        canvas1.drawText(String.valueOf(garageModel.getRemainSlot()), 28, 27, color);
        Marker marker = googleMap.addMarker(new MarkerOptions()
                .position(garageModel.getPosition())
                .icon(BitmapDescriptorFactory.fromBitmap(bmp))
                // Specifies the anchor to be at a particular point in the marker image.
                .anchor(0.4f, 0.7f));
    }

    protected void startIntentService(Location location) {
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        mResultReceiver = new AddressResultReceiver(new Handler());
        intent.putExtra(Constant.RECEIVER, mResultReceiver);
        intent.putExtra(Constant.LOCATION_DATA_EXTRA, location);
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
            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));
        } else if (mLastKnownLocation != null) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(mLastKnownLocation.getLatitude(),
                            mLastKnownLocation.getLongitude()), Constant.DEFAULT_ZOOM));
        } else {
            Log.d("TAG", "Current location is null. Using defaults.");
            mDefaultLocation = new LatLng(-33.852, 151.211);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, Constant.DEFAULT_ZOOM));
            googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        }

    }

    private void addMarker(Location location) {
        if (mMarker != null) {
            mMarker.remove();
        }
        startIntentService(location);
        mMarker = googleMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())));
        if (!placeName.isEmpty()) {
            mMarker.setTitle(placeName);
        }
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
        String sensor = "sensor=false";
        String mode = "mode=driving";
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode;
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

    @Override
    protected void onPause() {
        super.onPause();
        mCameraPosition = googleMap.getCameraPosition();
        mCurrentLocation = mLastKnownLocation;
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
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_logout) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private class AddressResultReceiver extends ResultReceiver {
        AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            String mAddressOutput = resultData.getString(Constant.RESULT_DATA_KEY);
            addressTitle.setText(mAddressOutput);

        }
    }

    public class GetAPIData extends DownloadTask {

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            new GetLocationDirection().execute(result);

        }
    }

    private class GetLocationDirection extends DirectionParserTask {

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();
            String distance = "";
            String duration;
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
                        duration = point.get("duration");
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
            tvDistance.setText(distance);
            mPolyline = googleMap.addPolyline(lineOptions);
        }
    }

    private class GetLocationDirection extends DirectionParserTask {

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();
            String distance = "";
            String duration;
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
                        duration = point.get("duration");
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
            tvDistance.setText(distance);
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

}
