package com.quocngay.carparkbooking.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.google.gson.Gson;
import com.quocngay.carparkbooking.R;
import com.quocngay.carparkbooking.model.CarModel;
import com.quocngay.carparkbooking.model.ParkingInfoHistoryModel;
import com.quocngay.carparkbooking.model.Principal;
import com.quocngay.carparkbooking.other.CarListAdapter;
import com.quocngay.carparkbooking.other.Constant;
import com.quocngay.carparkbooking.other.HistoryListAdapter;
import com.quocngay.carparkbooking.other.SocketIOClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CarManagerActivity extends AppCompatActivity {

    ListView lvCarList;
    CarListAdapter adapter;
    List<CarModel> mCarList;
    String accountid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_manager);

        lvCarList = (ListView) findViewById(R.id.lvCarManager);

        accountid = new Principal(getApplicationContext()).getId();
        SocketIOClient.client.mSocket.emit(Constant.REQUEST_FIND_CAR_BY_ACCOUNT_ID, accountid);

        SocketIOClient.client.mSocket.on(Constant.RESPONSE_DELETE_CAR, onDeleteCar);
        SocketIOClient.client.mSocket.on(Constant.RESPONSE_FIND_CAR_BY_ACCOUNT_ID, onGetCar);
    }

    private Emitter.Listener onDeleteCar = new Emitter.Listener() {

        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    try {
                        Boolean result = data.getBoolean(Constant.RESULT);
                        if (result) {
                            Toast.makeText(getApplicationContext(), R.string.had_delete_car, Toast.LENGTH_SHORT).show();
                            SocketIOClient.client.mSocket.emit(Constant.REQUEST_FIND_CAR_BY_ACCOUNT_ID, accountid);
                        } else {
                            Toast.makeText(getApplicationContext(),R.string.error_general, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private Emitter.Listener onGetCar = new Emitter.Listener() {

        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    try {
                        Boolean result = data.getBoolean(Constant.RESULT);
                        if (result) {
                            Log.i("Cars ", data.toString());
                            addDataToList(data);
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.not_have_car, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    void addDataToList(JSONObject data) {
        CarModel p;
        Gson gson = new Gson();

        JSONArray  listJsonCar = null;
        try {
            listJsonCar = data.getJSONArray(Constant.DATA);
            mCarList = new ArrayList<CarModel>();
            for (int i = 0; i <  listJsonCar.length(); i++) {
                p = gson.fromJson( listJsonCar.getJSONObject(i).toString(), CarModel.class);
                Log.i("car number ",p.getVehicleNumber());
                mCarList.add(p);
            }

            adapter = new CarListAdapter(getBaseContext(), mCarList);
            lvCarList.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
