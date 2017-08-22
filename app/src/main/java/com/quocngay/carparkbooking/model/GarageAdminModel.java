package com.quocngay.carparkbooking.model;

import java.io.Serializable;

/**
 * Created by Quang Si on 8/21/2017.
 */

public class GarageAdminModel extends GarageModel implements Serializable {

    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
