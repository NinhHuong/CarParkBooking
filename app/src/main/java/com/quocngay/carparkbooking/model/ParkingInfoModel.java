package com.quocngay.carparkbooking.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Windows on 20-Jul-17.
 */

public class ParkingInfoModel {
    public int id;
    public int carID;
    public int garageID;
    public String timeBooked;
    public String timeGoIn;
    public String timeGoOut;
    public int parkingStatus;
    SimpleDateFormat inputFormat =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault());
    SimpleDateFormat timeFormat =
            new SimpleDateFormat("HH:mm", Locale.getDefault());

    public String getTimeBookedFormatted(){
        Date book = null;
        try {
            book = inputFormat.parse(getTimeBooked().replaceAll("Z$", "+0000"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeFormat.format(book);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCarID() {
        return carID;
    }

    public void setCarID(int carID) {
        this.carID = carID;
    }

    public int getGarageID() {
        return garageID;
    }

    public void setGarageID(int garageID) {
        this.garageID = garageID;
    }

    public String getTimeBooked() {
        return timeBooked;
    }

    public void setTimeBooked(String timeBooked) {
        this.timeBooked = timeBooked;
    }

    public String getTimeGoIn() {
        return timeGoIn;
    }

    public void setTimeGoIn(String timeGoIn) {
        this.timeGoIn = timeGoIn;
    }

    public String getTimeGoOut() {
        return timeGoOut;
    }

    public void setTimeGoOut(String timeGoOut) {
        this.timeGoOut = timeGoOut;
    }

    public int getParkingStatus() {
        return parkingStatus;
    }

    public void setParkingStatus(int parkingStatus) {
        this.parkingStatus = parkingStatus;
    }
}
