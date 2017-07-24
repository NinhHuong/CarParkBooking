package com.quocngay.carparkbooking.activity;

import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.maps.model.LatLng;
import com.quocngay.carparkbooking.GaragesRecyclerViewAdapter;
import com.quocngay.carparkbooking.R;
import com.quocngay.carparkbooking.model.GarageModel;
import com.quocngay.carparkbooking.model.LocationDataModel;
import com.quocngay.carparkbooking.other.Constant;
import com.quocngay.carparkbooking.other.DirectionsJSONParser;
import com.quocngay.carparkbooking.tasks.DownloadTask;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NearestGaraActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String ARG_COLUMN_COUNT = "column-count";
    public static final String GARA_SELECTED = "gara_selected";
    String distance;
    String duration;
    List<LocationDataModel> dataModelList;
    GaragesRecyclerViewAdapter recyclerViewAdapter;
    private int mColumnCount = 1;
    private int progress = 0;
    ProgressBar progressBar;
    RecyclerView mRecyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearest_gara);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        Location lastKnownLocation = getIntent().getParcelableExtra(Constant.MY_LOCATION);
        progressBar = (ProgressBar) findViewById(R.id.nearst_progress);
        mRecyclerView = (RecyclerView) findViewById(R.id.list_gara);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        if (mColumnCount <= 1) {
            mRecyclerView.setLayoutManager(layoutManager);
        } else {
            mRecyclerView.setLayoutManager(new GridLayoutManager(this, mColumnCount));
        }


        for (GarageModel garageModel : MapActivity.garageModelList) {
            String url = getDirectionsUrl(new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()), garageModel.getPosition(), false);
            GetAPIDataForNearest getAPIData = new GetAPIDataForNearest(garageModel);
            getAPIData.execute(url);
        }
        dataModelList = new ArrayList<>();
        OnListInteractionListener listener = new OnListInteractionListener() {
            @Override
            public void onListInteraction(LocationDataModel item) {
                Intent intent = new Intent(NearestGaraActivity.this, MapActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(GARA_SELECTED, item);
                intent.putExtras(bundle);
                setResult(RESULT_OK, intent);
                finish();
            }
        };
        recyclerViewAdapter = new GaragesRecyclerViewAdapter(dataModelList, listener);
        mRecyclerView.setAdapter(recyclerViewAdapter);
        if (dataModelList != null) {
            recyclerViewAdapter.notifyDataSetChanged();
        }
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                layoutManager.getOrientation());
        mRecyclerView.addItemDecoration(mDividerItemDecoration);
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

    @Override
    public void onClick(View v) {

    }

    private class GetAPIDataForNearest extends DownloadTask {

        private GarageModel garageModel;

        private GetAPIDataForNearest(GarageModel garageModel) {
            this.garageModel = garageModel;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            new GetLocationDistanceDuration(garageModel).execute(result);

        }
    }

    public interface OnListInteractionListener {
        void onListInteraction(LocationDataModel item);
    }

    private class GetLocationDistanceDuration extends AsyncTask<String, Integer, LocationDataModel> {

        private GarageModel garageModel;

        private GetLocationDistanceDuration(GarageModel garageModel) {
            this.garageModel = garageModel;
        }

        @Override
        protected LocationDataModel doInBackground(String... params) {
            JSONObject jObject;
            LocationDataModel result = null;
            try {
                jObject = new JSONObject(params[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser(garageModel);
                result = parser.parseWithoutRoutes(jObject);
                progress++;
                publishProgress(progress);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(LocationDataModel result) {
            dataModelList.add(result);
            recyclerViewAdapter.notifyDataSetChanged();
            mRecyclerView.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
//            progressBar.setProgress(values[0], true);
        }
    }
}
