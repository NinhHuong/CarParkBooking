package com.quocngay.carparkbooking.model;

import java.io.Serializable;

public class AccountListModel implements Serializable {

    private String name;
    private AccountModel accountModel;

    public AccountListModel(String name, AccountModel accountModel) {
        this.name = name;
        this.accountModel = accountModel;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AccountModel getAccountModel() {
        return accountModel;
    }

    public void setAccountModel(AccountModel accountModel) {
        this.accountModel = accountModel;
    }
}
