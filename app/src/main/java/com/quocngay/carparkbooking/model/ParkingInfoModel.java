package com.quocngay.carparkbooking.model;

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

    public ParkingInfoModel() {
    }

    public ParkingInfoModel(int id, int carID, int garageID, String timeBooked, String timeGoIn, String timeGoOut, int parkingStatus) {
        this.id = id;
        this.carID = carID;
        this.garageID = garageID;
        this.timeBooked = timeBooked;
        this.timeGoIn = timeGoIn;
        this.timeGoOut = timeGoOut;
        this.parkingStatus = parkingStatus;
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
