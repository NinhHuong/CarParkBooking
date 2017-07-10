package com.quocngay.carparkbooking.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.quocngay.carparkbooking.R;

public class MapFragment extends Fragment {

    final static public int INIT_ZOOM = 16;

    MapView mMapView;
    TextView txtName;
    EditText edtSearchBar;
    Button btnSearch;
    LocationManager locationManager;
    Criteria mCriteria;
    Location mLocation;
    private GoogleMap googleMap;
    private FusedLocationProviderClient mFusedLocationClient;
    private LatLng currentLocation;
//    public MapFragment() {
//        // Required empty public constructor
//    }

    //Check if system location is enabled or not
    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        //TODO: Check code version
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
    public void onCreate(Bundle savedInstanceState) {
        if (!isLocationEnabled(getContext())) {
            //Alert user gps is not enabled
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
            alertDialog.setTitle(getResources().getString(R.string.map_gps_dialog_title));
            alertDialog.setMessage(getResources().getString(R.string.map_gps_dialog_message));
            alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    getContext().startActivity(intent);
                }
            });
            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            alertDialog.show();
        }

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        edtSearchBar = (EditText) rootView.findViewById(R.id.edtSearchBar);
        btnSearch = (Button) rootView.findViewById(R.id.btnSearch);
        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;
                googleMap.setMyLocationEnabled(true);
                googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                googleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                    @Override
                    public boolean onMyLocationButtonClick() {
                        //Alert user to turn gps on to get current location
                        if (!isLocationEnabled(getContext())) {
                            Snackbar.make(getView(), getResources().getString(R.string.map_mylocation_button), Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                        return false;
                    }
                });
                if (isLocationEnabled(getContext())) {
                    // For showing a move to my location button
                    locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                    mCriteria = new Criteria();
                    String bestProvider = String.valueOf(locationManager.getBestProvider(mCriteria, true));

                    mLocation = locationManager.getLastKnownLocation(bestProvider);
                    if (mLocation != null) {
                        Log.e("TAG", "GPS is on");
                        moveCameraTo(mLocation);
                    }
                }
            }

        });


        return rootView;
    }

    private void moveCameraTo(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        LatLng moveLocation = new LatLng(latitude, longitude);
        CameraPosition cameraPosition = new CameraPosition.Builder().target(moveLocation).zoom(INIT_ZOOM).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    //region Override normal function
    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    //endregion

    private void Search() {
        Toast.makeText(getContext(), "Start search " + edtSearchBar.getText().toString(), Toast.LENGTH_SHORT).show();
    }
}
