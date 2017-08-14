package com.quocngay.carparkbooking.model;

import java.io.Serializable;

/**
 * Created by Windows on 30-Jul-17.
 */

public class ParkingInfoSecurityModel implements Serializable{
    public int id;
    public int carID;
    public int garageID;
    public String vehicleNumber;
    public String timeBooked;
    public String timeGoIn;
    public String timeGoOut;
    public int parkingStatus;

    public ParkingInfoSecurityModel() {
    }

    public ParkingInfoSecurityModel(int id, int carID, int garageID,
                                    String vehicleNumber, String timeBooked,
                                    String timeGoIn, String timeGoOut, int parkingStatus) {
        this.id = id;
        this.carID = carID;
        this.garageID = garageID;
        this.vehicleNumber = vehicleNumber;
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

    public int getGarageID() {
        return garageID;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
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

    public void setGarageID(int garageID) {
        this.garageID = garageID;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
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
    public String toString() {
        return vehicleNumber;
    }
}
