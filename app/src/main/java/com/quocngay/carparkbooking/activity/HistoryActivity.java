package com.quocngay.carparkbooking.activity;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;
import com.quocngay.carparkbooking.R;
import com.quocngay.carparkbooking.model.Item;
import com.quocngay.carparkbooking.model.ListHeader;
import com.quocngay.carparkbooking.model.ParkingInfoHistoryModel;
import com.quocngay.carparkbooking.model.Principal;
import com.quocngay.carparkbooking.other.Constant;
import com.quocngay.carparkbooking.other.HistoryListAdapter;
import com.quocngay.carparkbooking.other.SocketIOClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.halfbit.pinnedsection.PinnedSectionListView;

public class HistoryActivity extends AppCompatActivity {
    ListView lvHistory;
    HistoryListAdapter adapter;
    List<ParkingInfoHistoryModel> mHistoryList;
    ArrayList<Item> mItems;
    ProgressBar mProgressBar;

    private Emitter.Listener onGetParkingInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();

        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        lvHistory = (ListView) findViewById(R.id.lvHistory);

        String id = new Principal(getApplicationContext()).getId();
        mProgressBar = (ProgressBar) findViewById(R.id.progress_history);

        SocketIOClient.client.mSocket.emit("request_booking_history_account_id", id);
        SocketIOClient.client.mSocket.on("response_booking_history_account_id", new Emitter.Listener() {

            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject data = (JSONObject) args[0];
                        try {
                            Boolean result = data.getBoolean(Constant.RESULT);
                            if (result) {
                                Log.i("Data park info", data.toString());
                                addDataToList(data);
                            } else {
                                Toast.makeText(getApplicationContext(), R.string.not_have_recort, Toast.LENGTH_SHORT).show();
                            }
                            SocketIOClient.client.mSocket.off("response_booking_history_account_id");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        mProgressBar.setVisibility(View.GONE);
                    }
                });
            }
        });
        Log.d("Socket", String.valueOf(SocketIOClient.client.mSocket.connected()));
    }


    void addDataToList(JSONObject data) {
        ParkingInfoHistoryModel p;
        Gson gson = new Gson();
        JSONArray listJsonGarasParkInfo = null;
        String header = "";
        SimpleDateFormat inputFormat =
                new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault());
        SimpleDateFormat dateFormat =
                new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        mHistoryList = new ArrayList<>();
        try {
            listJsonGarasParkInfo = data.getJSONArray(Constant.DATA);
            mItems = new ArrayList<>();
            for (int i = 0; i < listJsonGarasParkInfo.length(); i++) {
                p = gson.fromJson(listJsonGarasParkInfo.getJSONObject(i).toString(), ParkingInfoHistoryModel.class);
                mHistoryList.add(p);
            }
            Collections.sort(mHistoryList);
            Collections.reverse(mHistoryList);
            for (ParkingInfoHistoryModel model : mHistoryList) {
                Date book = inputFormat.parse(model.getTimeBooked().replaceAll("Z$", "+0000"));
                if (header.isEmpty() || !dateFormat.format(book).equals(header)) {
                    header = dateFormat.format(book);
                    mItems.add(new ListHeader(header));
                } else if (header.equals(dateFormat.format(book))) {
                    header = dateFormat.format(book);
                }
                mItems.add(model);
            }
            adapter = new HistoryListAdapter(getBaseContext(), mItems);
            lvHistory.setAdapter(adapter);
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }
    }

}
