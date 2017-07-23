package com.quocngay.carparkbooking.model;

import java.io.Serializable;

/**
 * Created by Quang Si on 7/22/2017.
 */

public class CarModel implements Serializable{

    private String id;
    private String accountID;
    private String vehicleNumber;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccountId() {
        return accountID;
    }

    public void setAccountId(String accountId) {
        this.accountID = accountId;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }
}
