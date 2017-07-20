package com.quocngay.carparkbooking.model;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

/**
 * Created by ninhh on 5/24/2017.
 */

public class GarageModel {
//    private static String KEY_SERVER_ID = "id";
//    private static String KEY_SERVER_NAME = "name";
//    private static String KEY_SERVER_ADDRESS = "address";
//    private static String KEY_SERVER_PICTURE = "picture";
//    private static String KEY_SERVER_TOTAL = "total_slot";
//    private static String KEY_SERVER_BUSY = "busy_slot";
//    private static String KEY_SERVER_BOOKING = "booking_slot";
//    private static String KEY_SERVER_LOCATION_X = "location_x";
//    private static String KEY_SERVER_LOCATION_Y = "location_y";
//    private static String KEY_SERVER_LOCATION_Z = "location_z";
//    public static String KEY_SERVER_LIST_GARAGE = "garageList";
//
//    private static String TAG = GarageModel.class.getSimpleName();

    private int id;
    private String name;
    private String address;
    private int totalSlot;
    private int busySlot;
    private String locationX;
    private String locationY;
    private Date timeStart;
    private Date timeEnd;
    private String xStatus;

    public GarageModel(int id, String name, String address, int totalSlot, int busySlot, String locationX, String locationY, Date timeStart, Date timeEnd, String xStatus) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.totalSlot = totalSlot;
        this.busySlot = busySlot;
        this.locationX = locationX;
        this.locationY = locationY;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.xStatus = xStatus;
    }

    public GarageModel() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getTotalSlot() {
        return totalSlot;
    }

    public void setTotalSlot(int totalSlot) {
        this.totalSlot = totalSlot;
    }

    public int getBusySlot() {
        return busySlot;
    }

    public void setBusySlot(int busySlot) {
        this.busySlot = busySlot;
    }

    public String getLocationX() {
        return locationX;
    }

    public void setLocationX(String locationX) {
        this.locationX = locationX;
    }

    public String getLocationY() {
        return locationY;
    }

    public void setLocationY(String locationY) {
        this.locationY = locationY;
    }

    public Date getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(Date timeStart) {
        this.timeStart = timeStart;
    }

    public Date getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(Date timeEnd) {
        this.timeEnd = timeEnd;
    }

    public String getxStatus() {
        return xStatus;
    }

    public void setxStatus(String xStatus) {
        this.xStatus = xStatus;
    }

    public LatLng getPosition() {
        return new LatLng(Double.valueOf(locationX), Double.valueOf(locationY));
    }

    public int getRemainSlot() {
        return totalSlot - busySlot;
    }
}
