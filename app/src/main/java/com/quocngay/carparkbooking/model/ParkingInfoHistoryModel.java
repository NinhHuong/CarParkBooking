package com.quocngay.carparkbooking.model;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.quocngay.carparkbooking.R;
import com.quocngay.carparkbooking.other.Constant;
import com.quocngay.carparkbooking.other.HistoryListAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Windows on 20-Jul-17.
 */

public class ParkingInfoHistoryModel implements Item, Comparable<ParkingInfoHistoryModel> {
    public int id;
    public int carID;
    public String name;
    public String timeBooked;
    public String timeGoIn;
    public String timeGoOut;
    public int parkingStatus;

    public ParkingInfoHistoryModel(int id, int carID, String name, String timeBooked, String timeGoIn, String timeGoOut, int parkingStatus) {
        this.id = id;
        this.carID = carID;
        this.name = name;
        this.timeBooked = timeBooked;
        this.timeGoIn = timeGoIn;
        this.timeGoOut = timeGoOut;
        this.parkingStatus = parkingStatus;
    }

    public int getId() {
        return id;
    }

    public int getCarID() {
        return carID;
    }

    public String getName() {
        return name;
    }

    public String getTimeBooked() {
        return timeBooked;
    }

    public String getTimeGoIn() {
        return timeGoIn;
    }

    public String getTimeGoOut() {
        return timeGoOut;
    }

    public int getParkingStatus() {
        return parkingStatus;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCarID(int carID) {
        this.carID = carID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTimeBooked(String timeBooked) {
        this.timeBooked = timeBooked;
    }

    public void setTimeGoIn(String timeGoIn) {
        this.timeGoIn = timeGoIn;
    }

    public void setTimeGoOut(String timeGoOut) {
        this.timeGoOut = timeGoOut;
    }

    public void setParkingStatus(int parkingStatus) {
        this.parkingStatus = parkingStatus;
    }

    @Override
    public int getViewType() {
        return HistoryListAdapter.RowType.LIST_ITEM.ordinal();
    }

    @Override
    public View getView(LayoutInflater inflater, View convertView) {
        View view;
        if (convertView == null) {
            view = inflater.inflate(R.layout.item_history, null);
            // Do some initialization
        } else {
            view = convertView;
        }
        TextView tvTimeBooked, tvTimeIn, tvTimeOut, tvGarageName;
        String timeBook, timeCheckin, timeCheckout;

        tvTimeBooked = (TextView) view.findViewById(R.id.tvTimeBooked);
        tvTimeIn = (TextView) view.findViewById(R.id.tvTimeStart);
        tvTimeOut = (TextView) view.findViewById(R.id.tvTimeEnd);
        tvGarageName = (TextView) view.findViewById(R.id.edtGarageName);

        timeBook = timeBooked;
        timeCheckin = timeGoIn == null ? "" : timeGoIn;
        timeCheckout = timeGoOut == null ? "" : timeGoOut;


        SimpleDateFormat inputFormat =
                new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault());
        SimpleDateFormat timeFormat =
                new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

        try {
            if (!timeBook.isEmpty()) {
                Date book = inputFormat.parse(timeBook.replaceAll("Z$", "+0000"));
                timeBook = timeFormat.format(book);

            }
            if (!timeCheckin.isEmpty()) {
                Date start = inputFormat.parse(timeCheckin.replaceAll("Z$", "+0000"));
                timeCheckin = timeFormat.format(start);
            }
            if (!timeCheckout.isEmpty()) {
                Date end = inputFormat.parse(timeCheckout.replaceAll("Z$", "+0000"));
                timeCheckout = timeFormat.format(end);
            }


        } catch (ParseException e) {
            e.printStackTrace();
        }

        tvTimeBooked.setText(timeBook);
        tvTimeIn.setText(timeCheckin);
        tvTimeOut.setText(timeCheckout);
        tvGarageName.setText(name);

        return view;
    }

    @Override
    public int compareTo(@NonNull ParkingInfoHistoryModel parkingInfoHistoryModel) {

        if (getTimeBooked() == null || parkingInfoHistoryModel.getTimeBooked() == null) {
            return 0;
        }
        return getTimeBooked()
                .compareTo(parkingInfoHistoryModel.getTimeBooked());
    }
}
