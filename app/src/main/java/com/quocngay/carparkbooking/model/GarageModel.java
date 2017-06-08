package com.quocngay.carparkbooking.model;

import android.util.Log;

import com.quocngay.carparkbooking.dbcontext.DbContext;

import org.json.JSONException;
import org.json.JSONObject;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by ninhh on 5/24/2017.
 */

public class GarageModel extends RealmObject {
    private static String KEY_SERVER_ID = "id";
    private static String KEY_SERVER_NAME = "name";
    private static String KEY_SERVER_ADDRESS = "address";
    private static String KEY_SERVER_PICTURE = "picture";
    private static String KEY_SERVER_TOTAL = "total_slot";
    private static String KEY_SERVER_BUSY = "busy_slot";
    private static String KEY_SERVER_BOOKING = "booking_slot";
    private static String KEY_SERVER_LOCATION_X = "location_x";
    private static String KEY_SERVER_LOCATION_Y = "location_y";
    private static String KEY_SERVER_LOCATION_Z = "location_z";
    public static String KEY_SERVER_LIST_GARAGE = "garageList";

    private static String TAG = GarageModel.class.getSimpleName();
    
    @PrimaryKey
    private int id;
    private String name;
    private String address;
    private String picture;
    private int totalSlot;
    private int busySlot;
    private int bookedSlot;
    private String locationX;
    private String locationY;
    private String locationZ;

    public static GarageModel create(int id, String name, String address, String picture, int totalSlot, int busySlot, int bookedSlot,
                                     String locationX, String locationY, String locationZ) {
        GarageModel garageModel = new GarageModel();
        garageModel.id = id;
        garageModel.name = name;
        garageModel.address = address;
        garageModel.picture = picture;
        garageModel.totalSlot = totalSlot;
        garageModel.busySlot = busySlot;
        garageModel.bookedSlot = bookedSlot;
        garageModel.locationX = locationX;
        garageModel.locationY = locationY;
        garageModel.locationZ = locationZ;
        return garageModel;
    }

    public static GarageModel createwithoutId(String name, String address, String picture, int totalSlot, int busySlot, int bookedSlot,
                                              String locationX, String locationY, String locationZ) {
        GarageModel garageModel = new GarageModel();
        DbContext dbContext = DbContext.getInst();
        garageModel.id = dbContext.getMaxGaraModelId() + 1;
        garageModel.name = name;
        garageModel.address = address;
        garageModel.picture = picture;
        garageModel.totalSlot = totalSlot;
        garageModel.busySlot = busySlot;
        garageModel.bookedSlot = bookedSlot;
        garageModel.locationX = locationX;
        garageModel.locationY = locationY;
        garageModel.locationZ = locationZ;
        return garageModel;
    }

    public static GarageModel createByJson(JSONObject obj) {
        GarageModel garageModel = new GarageModel();
        if(obj != null) {
            try{
                garageModel.id = obj.getInt(KEY_SERVER_ID);
                garageModel.name = obj.getString(KEY_SERVER_NAME);
                garageModel.address = obj.getString(KEY_SERVER_ADDRESS);
                garageModel.picture = obj.getString(KEY_SERVER_PICTURE);
                garageModel.totalSlot = obj.getInt(KEY_SERVER_TOTAL);
                garageModel.busySlot = obj.getInt(KEY_SERVER_BUSY);
                garageModel.bookedSlot = obj.getInt(KEY_SERVER_BOOKING);
                garageModel.locationX = obj.getString(KEY_SERVER_LOCATION_X);
                garageModel.locationY = obj.getString(KEY_SERVER_LOCATION_Y);
                garageModel.locationZ = obj.getString(KEY_SERVER_LOCATION_Z);
            } catch(JSONException ex) {
                Log.e(TAG, "Error while get json attribute: " + ex.getMessage());
                return null;
            }
        }
        return garageModel;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public void setTotalSlot(int totalSlot) {
        this.totalSlot = totalSlot;
    }

    public void setBusySlot(int busySlot) {
        this.busySlot = busySlot;
    }

    public void setBookedSlot(int bookedSlot) {
        this.bookedSlot = bookedSlot;
    }

    public void setLocationX(String locationX) {
        this.locationX = locationX;
    }

    public void setLocationY(String locationY) {
        this.locationY = locationY;
    }

    public void setLocationZ(String locationZ) {
        this.locationZ = locationZ;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getPicture() {
        return picture;
    }

    public int getTotalSlot() {
        return totalSlot;
    }

    public int getBusySlot() {
        return busySlot;
    }

    public int getBookedSlot() {
        return bookedSlot;
    }

    public String getLocationX() {
        return locationX;
    }

    public String getLocationY() {
        return locationY;
    }

    public String getLocationZ() {
        return locationZ;
    }
}
