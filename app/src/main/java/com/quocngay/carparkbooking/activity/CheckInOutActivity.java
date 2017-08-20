package com.quocngay.carparkbooking.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.google.gson.Gson;
import com.quocngay.carparkbooking.adapter.LicenseSecurityRecyclerViewAdapter;
import com.quocngay.carparkbooking.R;
import com.quocngay.carparkbooking.model.GarageModel;
import com.quocngay.carparkbooking.model.LocalData;
import com.quocngay.carparkbooking.model.ParkingInfoSecurityModel;
import com.quocngay.carparkbooking.other.Constant;
import com.quocngay.carparkbooking.other.OnListInteractionListener;
import com.quocngay.carparkbooking.other.SocketIOClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CheckInOutActivity extends GeneralActivity {
    public static final String PARKINGINFO_MODEL = "parkinginfor_model";

    public static final String EXTRA_SECURITY_FUNCTION = "security_function";
    public static final String EXTRA_CAR_IN = "car_in";
    public static final String EXTRA_CAR_OUT = "car_out";

    private String requestItemSelect;
    private String responseItemSelect;
    private String requestCarFunc;
    private String responseCarFunc;
    private int dialogTitle;
    private int dialogMessage;
    private int dialogMessageFinish;
    private FloatingActionButton btnAddNewCarIn;

    SearchView svLicense;
    CardView containerSearch;
    RecyclerView recyclerViewLicense;
    private ArrayList<ParkingInfoSecurityModel> listCar;
    private String garageId;
    LicenseSecurityRecyclerViewAdapter recyclerViewAdapter;
    int mColumnCount = 1;
    private MenuItem refreshItem;

    private OnListInteractionListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in_out);

        String resultExtra = getIntent().getStringExtra(EXTRA_SECURITY_FUNCTION);

        initToolbar(R.id.toolbar, true, true);
        initSearchBar();
        LocalData localData;
        initButtonAddCarIn();

        if (resultExtra.compareTo(EXTRA_CAR_IN) == 0) {
            requestItemSelect = Constant.REQUEST_ONE_CAR_IN_ID;
            responseItemSelect = Constant.RESPONSE_ONE_CAR_IN;
            requestCarFunc = Constant.REQUEST_CAR_WILL_IN;
            responseCarFunc = Constant.RESPONSE_CAR_WILL_IN;
            dialogTitle = R.string.dialog_security_checkin;
            dialogMessage = R.string.dialog_security_checkin_message;
            dialogMessageFinish = R.string.security_checkin_success;
            btnAddNewCarIn.setVisibility(View.VISIBLE);
        } else {
            requestItemSelect = Constant.REQUEST_ONE_CAR_OUT;
            responseItemSelect = Constant.RESPONSE_ONE_CAR_OUT;
            requestCarFunc = Constant.REQUEST_CAR_WILL_OUT;
            responseCarFunc = Constant.RESPONSE_CAR_WILL_OUT;
            dialogTitle = R.string.dialog_security_checkout;
            dialogMessage = R.string.dialog_security_checkout_message;
            dialogMessageFinish = R.string.security_checkout_success;
            btnAddNewCarIn.setVisibility(View.INVISIBLE);
        }

        localData = new LocalData(getApplicationContext());
        garageId = localData.getGarageID();

        OnListInteractionListener listener = new OnListInteractionListener() {
            @Override
            public void onLicenseClickListener(final ParkingInfoSecurityModel item) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CheckInOutActivity.this);
                builder.setTitle(dialogTitle)
                        .setMessage(dialogMessage)
                        .setPositiveButton(R.string.dialog_button_ok,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        SocketIOClient.client.mSocket.emit(
                                                requestItemSelect,
                                                item.getId(), garageId);
                                        SocketIOClient.client.mSocket.on(
                                                responseItemSelect,
                                                onResponseCarInOut);
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

            @Override
            public void onGarageClickListener(GarageModel item) {

            }
        };

        listCar = new ArrayList<>();
        recyclerViewLicense = (RecyclerView) findViewById(R.id.list_license_security);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        if (mColumnCount <= 1) {
            recyclerViewLicense.setLayoutManager(layoutManager);
        } else {
            recyclerViewLicense.setLayoutManager(new GridLayoutManager(this, mColumnCount));
        }
        recyclerViewAdapter = new LicenseSecurityRecyclerViewAdapter(listCar, listener);
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

        SocketIOClient.client.mSocket.emit(requestCarFunc, garageId);
        SocketIOClient.client.mSocket.on(responseCarFunc, onCarIn);

        SocketIOClient.client.mSocket.on(Constant.RESPONSE_GARAGE_UPDATED, onRequestResetList);
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

        if (refreshItem != null)
            refreshItem.setActionView(iv);

        //Refresh car list
        recyclerViewAdapter.swap(listCar);

        recyclerViewLicense.smoothScrollToPosition(0);
        SocketIOClient.client.mSocket.emit(requestCarFunc, garageId);
        SocketIOClient.client.mSocket.on(responseCarFunc, onCarIn);
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

                        listCar.clear();

                        Gson gson = new Gson();
                        if (data.getBoolean(Constant.RESULT)) {
                            JSONArray listJsonGarasParkInfo = data.getJSONArray(Constant.DATA);
                            for (int i = 0; i < listJsonGarasParkInfo.length(); i++) {
                                ParkingInfoSecurityModel p = gson
                                        .fromJson(listJsonGarasParkInfo.getJSONObject(i).toString()
                                                , ParkingInfoSecurityModel.class);
                                listCar.add(p);
                                recyclerViewAdapter.notifyDataSetChanged();
                            }
                        } else {
                            Log.w(getClass().getName(), "Error: " +
                                    data.getString(Constant.MESSAGE));
                        }
                        SocketIOClient.client.mSocket.off(responseCarFunc);
                        completeRefresh();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private Emitter.Listener onResponseCarInOut = new Emitter.Listener() {

        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    try {
                        if (data.getBoolean(Constant.RESULT)) {
                            Toast.makeText(CheckInOutActivity.this,
                                    getResources().getString(dialogMessageFinish),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Log.w(getClass().getName(), "Error: " +
                                    data.getString(Constant.MESSAGE));
                        }

                        SocketIOClient.client.mSocket.off(responseItemSelect);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private Emitter.Listener onRequestResetList = new Emitter.Listener() {

        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    try {
                        Log.i("request reset all list", data.toString());
                        Boolean result = data.getBoolean(Constant.RESULT);

                        String requestGarageID = data.
                                getJSONObject(Constant.DATA).
                                getString("garageID");

                        if (!result || requestGarageID.compareTo(garageId) != 0)
                            return;

                        refresh();

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

    private void initButtonAddCarIn(){
        btnAddNewCarIn = (FloatingActionButton) findViewById(R.id.btnAddNewCarIn);

        btnAddNewCarIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(CheckInOutActivity.this);
                // Setting Dialog Title
                alertDialog.setTitle(dialogTitle );

                // Setting Icon to Dialog
                alertDialog.setIcon(R.drawable.ic_directions_car_black_24dp);

                final EditText input = new EditText(CheckInOutActivity.this);
//                input.setHint("Số xe hiện tại");
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                alertDialog.setView(input);

                // Setting Positive "Yes" Button
                alertDialog.setPositiveButton(getResources().getString(R.string.dialog_button_ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                SocketIOClient.client.mSocket.emit(Constant.REQUEST_ONE_CAR_IN_NUMBER, input.getText().toString(), garageId);
                                SocketIOClient.client.mSocket.on(responseItemSelect,onResponseCarInOut);
                            }
                        });
                // Setting Negative "NO" Button
                alertDialog.setNegativeButton(getResources().getString(R.string.dialog_btn_cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Write your code here to execute after dialog
                                dialog.cancel();
                            }
                        });
                // closed

                // Showing Alert Message
                alertDialog.show();
            }
        });
    }
}
