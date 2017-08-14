package com.quocngay.carparkbooking.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.google.gson.Gson;
import com.quocngay.carparkbooking.LicenseSecurityRecyclerViewAdapter;
import com.quocngay.carparkbooking.R;
import com.quocngay.carparkbooking.model.CarModel;
import com.quocngay.carparkbooking.model.LocalData;
import com.quocngay.carparkbooking.model.LocationDataModel;
import com.quocngay.carparkbooking.model.ParkingInfoSecurityModel;
import com.quocngay.carparkbooking.other.Constant;
import com.quocngay.carparkbooking.other.OnListInteractionListener;
import com.quocngay.carparkbooking.other.SocketIOClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CheckInActivity extends AppCompatActivity {

    public static final String PARKINGINFO_MODEL = "parkinginfor_model";
    SearchView svLicense;
    CardView containerSearch;
    RecyclerView recyclerViewLicense;
    private ArrayList<ParkingInfoSecurityModel> listCarIn;
    private String garageId;
    private LocalData localData;
    LicenseSecurityRecyclerViewAdapter recyclerViewAdapter;
    int mColumnCount = 1;
    private MenuItem refreshItem;
    private OnListInteractionListener listener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in);
        initToolbar();
        initSearchBar();
        localData = new LocalData(getApplicationContext());
        garageId = localData.getGarageID();

        listener = new OnListInteractionListener() {
            @Override
            public void OnLicenseClickListener(final ParkingInfoSecurityModel item) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CheckInActivity.this);
                builder.setTitle(R.string.dialog_security_checkin)
                        .setMessage(R.string.dialog_security_checkin_message)
                        .setPositiveButton(R.string.dialog_button_ok,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        SocketIOClient.client.mSocket.emit(
                                                Constant.REQUEST_ONE_CAR_IN_ID,
                                                item.getId(), garageId);
                                        SocketIOClient.client.mSocket.on(
                                                Constant.RESPONSE_ONE_CAR_IN_ID,
                                                onResponseCheckin);
                                    }
                                })
                        .setNegativeButton(R.string.dialog_btn_cancel,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                    }
                                });
                builder.create().show();
            }
        };

        listCarIn = new ArrayList<>();
        recyclerViewLicense = (RecyclerView) findViewById(R.id.list_license_security);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        if (mColumnCount <= 1) {
            recyclerViewLicense.setLayoutManager(layoutManager);
        } else {
            recyclerViewLicense.setLayoutManager(new GridLayoutManager(this, mColumnCount));
        }
        recyclerViewAdapter = new LicenseSecurityRecyclerViewAdapter(listCarIn, listener);
        recyclerViewLicense.setAdapter(recyclerViewAdapter);
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(
                recyclerViewLicense.getContext(), layoutManager.getOrientation());
        recyclerViewLicense.addItemDecoration(mDividerItemDecoration);

        svLicense.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                recyclerViewAdapter.getFilter().filter(newText);
                return true;
            }
        });

        SocketIOClient.client.mSocket.emit(Constant.REQUEST_CAR_WILL_IN, garageId);
        SocketIOClient.client.mSocket.on(Constant.RESPONSE_CAR_WILL_IN, onCarIn);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.security_check, menu);
        return true;
    }

    public void refresh() {
        LayoutInflater inflater = (LayoutInflater) getApplication()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ImageView iv = (ImageView) inflater.inflate(R.layout.item_refresh, null);

        Animation rotation = AnimationUtils.loadAnimation(getApplication(), R.anim.refresh);
        rotation.setRepeatCount(Animation.INFINITE);
        iv.startAnimation(rotation);

        refreshItem.setActionView(iv);

        //Refresh car list
        recyclerViewAdapter.swap(listCarIn);
        recyclerViewLicense.smoothScrollToPosition(0);
        SocketIOClient.client.mSocket.emit(Constant.REQUEST_CAR_WILL_IN, garageId);
        SocketIOClient.client.mSocket.on(Constant.RESPONSE_CAR_WILL_IN, onCarIn);
    }

    public void completeRefresh() {
        if (refreshItem != null && refreshItem.getActionView() != null) {
            refreshItem.getActionView().clearAnimation();
            refreshItem.setActionView(null);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_reload) {
            refreshItem = item;
            refresh();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private Emitter.Listener onCarIn = new Emitter.Listener() {

        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    try {
                        Log.i("Data car in", data.toString());
                        Gson gson = new Gson();
                        if (data.getBoolean(Constant.RESULT)) {
                            JSONArray listJsonGarasParkInfo = data.getJSONArray(Constant.DATA);
                            for (int i = 0; i < listJsonGarasParkInfo.length(); i++) {
                                ParkingInfoSecurityModel p = gson
                                        .fromJson(listJsonGarasParkInfo.getJSONObject(i).toString()
                                                , ParkingInfoSecurityModel.class);
                                listCarIn.add(0, p);
                                recyclerViewAdapter.notifyDataSetChanged();
                            }
                        } else {
                            Log.w(getClass().getName(), "Error: " +
                                    data.getString(Constant.MESSAGE));
                        }
                        SocketIOClient.client.mSocket.off(Constant.RESPONSE_CAR_WILL_IN);
                        completeRefresh();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private Emitter.Listener onResponseCheckin = new Emitter.Listener() {

        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    try {
                        if (data.getBoolean(Constant.RESULT)) {
                            Toast.makeText(CheckInActivity.this,
                                    getResources().getString(R.string.security_checkin_success),
                                    Toast.LENGTH_SHORT).show();
                            refresh();
                        } else {
                            Log.w(getClass().getName(), "Error: " +
                                    data.getString(Constant.MESSAGE));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private void initSearchBar() {
        final TextView tvSearchHint = (TextView) findViewById(R.id.tv_license_search_hint);
        containerSearch = (CardView) findViewById(R.id.container_search_view);
        svLicense = (SearchView) findViewById(R.id.sv_license);
        svLicense.setIconifiedByDefault(true);
        svLicense.setFocusable(true);
        svLicense.setIconified(false);

        containerSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvSearchHint.setVisibility(View.GONE);
                svLicense.setIconified(false);
            }
        });

        svLicense.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvSearchHint.setVisibility(View.GONE);
            }
        });

        svLicense.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                tvSearchHint.setVisibility(View.VISIBLE);
                return false;
            }
        });
    }


    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();

        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
    }
}
