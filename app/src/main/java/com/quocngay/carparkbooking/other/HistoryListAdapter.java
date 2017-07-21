package com.quocngay.carparkbooking.other;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.quocngay.carparkbooking.R;
import com.quocngay.carparkbooking.model.ParkingInfoModel;

import java.util.List;

/**
 * Created by Windows on 20-Jul-17.
 */

public class HistoryListAdapter extends BaseAdapter {

    private Context mContext;
    private List<ParkingInfoModel> mParkingList;

    public HistoryListAdapter(Context mContext, List<ParkingInfoModel> mParkingList) {
        this.mContext = mContext;
        this.mParkingList = mParkingList;
    }

    @Override
    public int getCount() {
        return mParkingList.size();
    }

    @Override
    public Object getItem(int position) {
        return mParkingList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = View.inflate(mContext, R.layout.history_item,null);
        TextView edtTimeBooked,edtTimeIn,edtTimeOut,edtGarageName;
        edtTimeBooked = (TextView) v.findViewById(R.id.edtTimeBooked);
        edtTimeIn = (TextView) v.findViewById(R.id.edtTimeIn);
        edtTimeOut = (TextView) v.findViewById(R.id.edtTimeOut);
        edtGarageName = (TextView) v.findViewById(R.id.edtGarageName);

        edtTimeBooked.setText(mParkingList.get(position).getTimeBooked());
        edtTimeIn.setText(mParkingList.get(position).getTimeGoIn());
        edtTimeOut.setText(mParkingList.get(position).getTimeGoOut());
        edtGarageName.setText(mParkingList.get(position).getName());

        v.setTag(mParkingList.get(position).getId());

        return v;
    }
}
