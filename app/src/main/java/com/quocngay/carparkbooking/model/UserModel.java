package com.quocngay.carparkbooking.model;

import java.io.Serializable;

/**
 * Created by Quang Si on 8/7/2017.
 */

public class UserModel implements Serializable {

    private String id;
    private String firstName;
    private String lastName;
    private String phone;
    private String dateOfBirth;
    private String address;

    public UserModel() {
        firstName = "";
        lastName = "";
        phone = "";
        dateOfBirth = "";
        address = "";
    }

    public String getFullName() {
        StringBuilder result = new StringBuilder();
        result.append(lastName == null ? "" : lastName);
        result.append(" ");
        result.append(firstName == null ? "" : firstName);
        return result.toString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
