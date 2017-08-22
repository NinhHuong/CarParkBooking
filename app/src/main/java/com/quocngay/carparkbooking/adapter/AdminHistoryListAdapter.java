package com.quocngay.carparkbooking.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.quocngay.carparkbooking.R;
import com.quocngay.carparkbooking.model.CarModel;
import com.quocngay.carparkbooking.model.ParkingInfoSecurityModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by Windows on 11-Aug-17.
 */

public class AdminHistoryListAdapter extends BaseAdapter {
    private Context mContext;
    private List<ParkingInfoSecurityModel> mParkingModel;
    private Activity parentActivity;

    public AdminHistoryListAdapter(Context mContext, List<ParkingInfoSecurityModel> mParkingModel, Activity parentActivity) {
        this.mContext = mContext;
        this.mParkingModel = mParkingModel;
        this.parentActivity = parentActivity;

    }

    @Override
    public int getCount() {
        return mParkingModel.size();
    }

    @Override
    public Object getItem(int position) {
        return mParkingModel.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mParkingModel.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = View.inflate(mContext, R.layout.item_admin_history,null);

        TextView txtVehicleNumber,txtTimeBooked,txtTimeGoIn, txtTimeGoOut;
        txtTimeBooked = (TextView) v.findViewById(R.id.txtTimeBooked);
        txtTimeGoIn = (TextView) v.findViewById(R.id.txtTimeGoIn);
        txtTimeGoOut = (TextView) v.findViewById(R.id.txtTimeGoOut);
        txtVehicleNumber = (TextView) v.findViewById(R.id.txtVehicleNumber);



        SimpleDateFormat inputFormat =
                new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        SimpleDateFormat dateFormat =
                new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

        String timeBooked = "", timeGoIn = "", timeGoOut = "";
        try {
            if(mParkingModel.get(position).getTimeBooked().compareTo("0000-00-00 00:00:00")!=0){
                timeBooked = dateFormat.format(inputFormat.parse(mParkingModel.get(position).getTimeBooked()));
                timeGoIn = dateFormat.format(inputFormat.parse(mParkingModel.get(position).getTimeGoIn()));
                timeGoOut = dateFormat.format(inputFormat.parse(mParkingModel.get(position).getTimeGoOut()));
            }else{
                timeBooked = "";
                timeGoIn = dateFormat.format(inputFormat.parse(mParkingModel.get(position).getTimeGoIn()));
                timeGoOut = dateFormat.format(inputFormat.parse(mParkingModel.get(position).getTimeGoOut()));
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        txtVehicleNumber.setText("Biển số: "+mParkingModel.get(position).getVehicleNumber());
        txtTimeBooked.setText(timeBooked);
        txtTimeGoIn.setText(timeGoIn);
        txtTimeGoOut.setText(timeGoOut);

        return v;
    }
}
