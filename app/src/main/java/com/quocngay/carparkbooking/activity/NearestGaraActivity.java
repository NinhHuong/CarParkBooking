package com.quocngay.carparkbooking.activity;

import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.quocngay.carparkbooking.GaragesRecyclerViewAdapter;
import com.quocngay.carparkbooking.R;
import com.quocngay.carparkbooking.model.GarageModel;
import com.quocngay.carparkbooking.model.LocationDataModel;
import com.quocngay.carparkbooking.other.Constant;
import com.quocngay.carparkbooking.tasks.DirectionParserTask;
import com.quocngay.carparkbooking.tasks.DownloadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NearestGaraActivity extends AppCompatActivity {

    private static final String ARG_COLUMN_COUNT = "column-count";
    String distance;
    String duration;
    List<LocationDataModel> dataModelList;
    GaragesRecyclerViewAdapter recyclerViewAdapter;
    private int mColumnCount = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearest_gara);
        Location lastKnownLocation = getIntent().getParcelableExtra(Constant.MY_LOCATION);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list_gara);
        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(this, mColumnCount));
        }
        for (GarageModel garageModel : MapActivity.garageModelList) {
            String url = getDirectionsUrl(new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()), garageModel.getPosition(), false);
            GetAPIDataForNearest getAPIData = new GetAPIDataForNearest(garageModel);
            getAPIData.execute(url);
        }
        dataModelList = new ArrayList<>();
        recyclerViewAdapter = new GaragesRecyclerViewAdapter(dataModelList);
        recyclerView.setAdapter(recyclerViewAdapter);
        if (dataModelList != null) {
            recyclerViewAdapter.notifyDataSetChanged();
        }
    }

    public String getDirectionsUrl(LatLng origin, LatLng dest, Boolean redirect) {

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

    private class GetAPIDataForNearest extends DownloadTask {

        private GarageModel garageModel;

        private GetAPIDataForNearest(GarageModel garageModel) {
            this.garageModel = garageModel;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            new GetLocationDirection(garageModel).execute(result);

        }
    }

    private class GetLocationDirection extends DirectionParserTask {

        private GarageModel garageModel;

        private GetLocationDirection(GarageModel garageModel) {
            this.garageModel = garageModel;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            for (int i = 0; i < result.size(); i++) {
                List<HashMap<String, String>> path = result.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);
                    if (j == 0) {
                        distance = point.get("distance");
                    } else if (j == 1) {
                        duration = point.get("duration");
                    }
                }
            }
            dataModelList.add(new LocationDataModel(garageModel, duration, distance));
            recyclerViewAdapter.notifyDataSetChanged();
        }
    }
}
