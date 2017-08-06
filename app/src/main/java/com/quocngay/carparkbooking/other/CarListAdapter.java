package com.quocngay.carparkbooking.other;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.quocngay.carparkbooking.R;
import com.quocngay.carparkbooking.activity.BookingActivity;
import com.quocngay.carparkbooking.activity.CarManagerActivity;
import com.quocngay.carparkbooking.model.CarModel;
import com.quocngay.carparkbooking.model.ParkingInfoHistoryModel;
import com.quocngay.carparkbooking.model.Principal;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Windows on 26-Jul-17.
 */

public class CarListAdapter extends BaseAdapter {
    private Context mContext;
    private List<CarModel> mCarList;
    private AlertDialog.Builder mDeleteDialog;
    private Activity parentActivity;


    public CarListAdapter(Activity parentActivity, Context mContext, List<CarModel> mCarList) {
        this.parentActivity = parentActivity;
        this.mContext = mContext;
        this.mCarList = mCarList;
    }

    @Override
    public int getCount() {
        return mCarList.size();
    }

    @Override
    public Object getItem(int position) {
        return mCarList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = View.inflate(mContext, R.layout.item_car,null);
        TextView txtVehicleNumber;
        Button btnDelete = (Button) v.findViewById(R.id.btnDelete);
        txtVehicleNumber = (TextView) v.findViewById(R.id.txtVehicleNumber);

        txtVehicleNumber.setText(mCarList.get(position).getVehicleNumber());

        btnDelete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mDeleteDialog = new AlertDialog.Builder(parentActivity);
                mDeleteDialog.setTitle(R.string.dialog_delete_title);
                mDeleteDialog.setMessage(R.string.dialog_delete_car)
                        .setPositiveButton(R.string.fire, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                SocketIOClient.client.mSocket.emit(Constant.REQUEST_REMOVE_CAR_BY_ID, mCarList.get(position).getId());
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                // Create the AlertDialog object and return it
                mDeleteDialog.create().show();

            }
        });
        v.setTag(mCarList.get(position).getId());

        return v;
    }
}
