package com.quocngay.carparkbooking.other;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.quocngay.carparkbooking.R;
import com.quocngay.carparkbooking.model.CarModel;
import com.quocngay.carparkbooking.model.ParkingInfoHistoryModel;
import com.quocngay.carparkbooking.model.Principal;

import java.util.List;

/**
 * Created by Windows on 26-Jul-17.
 */

public class CarListAdapter extends BaseAdapter {
    private Context mContext;
    private List<CarModel> mCarList;

    public CarListAdapter(Context mContext, List<CarModel> mCarList) {
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
                // Code here executes on main thread after user presses button
                String accountId = new Principal(mContext).getId();
                SocketIOClient.client.mSocket.emit(Constant.REQUEST_DELETE_CAR, mCarList.get(position).getId(),accountId);
            }
        });
        v.setTag(mCarList.get(position).getId());

        return v;
    }
}
