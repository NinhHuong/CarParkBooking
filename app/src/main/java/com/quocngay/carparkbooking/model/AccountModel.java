package com.quocngay.carparkbooking.model;

/**
 * Created by Windows on 07-Aug-17.
 */

public class AccountModel {
    private int id;
    private String email;
    private String role;
    private boolean isVerify;
    private String firstName;
    private String lastName;
    private String phone;
    private String dateOfBirth;
    private String address;

    public AccountModel() {
    }

    public AccountModel(int id, String email, String role, boolean isVerify, String firstName, String lastName, String phone, String dateOfBirth, String address) {
        this.id = id;
        this.email = email;
        this.role = role;
        this.isVerify = isVerify;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isVerify() {
        return isVerify;
    }

    public void setVerify(boolean verify) {
        isVerify = verify;
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
