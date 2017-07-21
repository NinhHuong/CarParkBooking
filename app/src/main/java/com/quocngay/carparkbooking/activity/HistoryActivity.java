package com.quocngay.carparkbooking.activity;

import android.os.Debug;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.google.gson.Gson;
import com.quocngay.carparkbooking.R;
import com.quocngay.carparkbooking.model.GarageModel;
import com.quocngay.carparkbooking.model.ParkingInfoModel;
import com.quocngay.carparkbooking.other.Constant;
import com.quocngay.carparkbooking.other.HistoryListAdapter;
import com.quocngay.carparkbooking.other.SocketIOClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {
    ListView lvHistory;
    HistoryListAdapter adapter;
    List<ParkingInfoModel> mHistoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_2);

        lvHistory = (ListView) findViewById(R.id.lvHistory);

        SocketIOClient.client.mSocket.emit("request_booking_account_id", 5);
        SocketIOClient.client.mSocket.on("response_booking_account_id", onGetParkingInfo);
    }

    private Emitter.Listener onGetParkingInfo = new Emitter.Listener() {

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

                        SocketIOClient.client.mSocket.off();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    void addDataToList(JSONObject data) {
        ParkingInfoModel p;
        Gson gson = new Gson();

        JSONArray listJsonGarasParkInfo = null;
        try {
            listJsonGarasParkInfo = data.getJSONArray(Constant.SERVER_PARK_INFO_RESULT);
            mHistoryList = new ArrayList<ParkingInfoModel>();
            for (int i = 0; i < listJsonGarasParkInfo.length(); i++) {
                p = gson.fromJson(listJsonGarasParkInfo.getJSONObject(i).toString(), ParkingInfoModel.class);
                p.setTimeBooked( ChangeDateTime(p.getTimeBooked()));
                p.setTimeGoIn( ChangeDateTime(p.getTimeGoIn()));
                p.setTimeGoOut( ChangeDateTime(p.getTimeGoOut()));

                mHistoryList.add(p);
            }

            adapter = new HistoryListAdapter(getBaseContext(), mHistoryList);
            lvHistory.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    String ChangeDateTime(String timeMysql) {
        String year, time;
        year = timeMysql.substring(0,10);

        String inputPattern = "yyyy-mm-dd";
        String outputPattern = "dd/mm/yyyy";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;

        try {
            date = inputFormat.parse(year);
            year = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        time = timeMysql.substring(11,19);
        return time + "     "+year;
    }
}
