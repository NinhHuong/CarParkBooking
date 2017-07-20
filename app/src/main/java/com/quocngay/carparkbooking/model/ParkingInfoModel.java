package com.quocngay.carparkbooking.model;

/**
 * Created by Windows on 20-Jul-17.
 */

public class ParkingInfoModel {
    public int id;
    public int carID;
    public String name;
    public String timeBooked;
    public String timeGoIn;
    public String timeGoOut;
    public int parkStatus;

    public ParkingInfoModel(int id, int carID, String name, String timeBooked, String timeGoIn, String timeGoOut, int parkStatus) {
        this.id = id;
        this.carID = carID;
        this.name = name;
        this.timeBooked = timeBooked;
        this.timeGoIn = timeGoIn;
        this.timeGoOut = timeGoOut;
        this.parkStatus = parkStatus;
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

    public int getParkStatus() {
        return parkStatus;
    }
}
