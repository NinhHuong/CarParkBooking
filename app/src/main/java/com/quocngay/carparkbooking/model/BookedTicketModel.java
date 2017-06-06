package com.quocngay.carparkbooking.model;

import android.util.Log;

import com.quocngay.carparkbooking.dbcontext.DbContext;
import com.quocngay.carparkbooking.other.Constant;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by ninhh on 5/23/2017.
 */

public class BookedTicketModel extends RealmObject {

    public static String KEY_SERVER_ID = "id";
    public static String KEY_SERVER_ACCOUNT_ID = "account_id";
    public static String KEY_SERVER_GARA_ID = "garage_id";
    public static String KEY_SERVER_BOOKED_TIME = "booked_time";
    public static String KEY_SERVER_CHECKIN_TIME = "checkin_time";
    public static String KEY_SERVER_CHECKOUT_TIME = "checkout_time";
    public static String KEY_SERVER_IS_EXPIRED = "is_expired";
    public static String KEY_SERVER_USER_INPUT = "user_input";
    public static String KEY_SERVER_IS_VALID_TOKEN = "is_valid_token";
    private static String TAG = BookedTicketModel.class.getSimpleName();
    
    @PrimaryKey
    private int id;
    private int accountId;
    private GarageModel garageModel;
    private Date bookedTime;
    private boolean isExpired;
    private Date checkinTime;
    private Date checkoutTime;

    public static BookedTicketModel create(int accountId, GarageModel garageModel, Date bookedTime, boolean isExpired, Date checkinTime, Date checkoutTime) {
        BookedTicketModel bookedTicketModel = new BookedTicketModel();
        bookedTicketModel.accountId = accountId;
        bookedTicketModel.garageModel = garageModel;
        bookedTicketModel.bookedTime = bookedTime;
        bookedTicketModel.isExpired = isExpired;
        bookedTicketModel.checkinTime = checkinTime;
        bookedTicketModel.checkoutTime = checkoutTime;
        return bookedTicketModel;
    }

    public static BookedTicketModel createwithoutId(GarageModel garageModel, Date bookedTime, boolean isExpired, Date checkinTime, Date checkoutTime) {
        BookedTicketModel bookedTicketModel = new BookedTicketModel();
        DbContext dbContext = DbContext.getInst();
        bookedTicketModel.accountId = dbContext.getMaxBookedTicketModelId() + 1;
        bookedTicketModel.garageModel = garageModel;
        bookedTicketModel.bookedTime = bookedTime;
        bookedTicketModel.isExpired = isExpired;
        bookedTicketModel.checkinTime = checkinTime;
        bookedTicketModel.checkoutTime = checkoutTime;
        return bookedTicketModel;
    }
    public static BookedTicketModel createByJson(JSONObject obj) {
        BookedTicketModel bookedTicketModel = new BookedTicketModel();

        if(obj != null) {
            try{
                DbContext dbContext = DbContext.getInst();
                GarageModel gara = dbContext.getGaraModelByID(obj.getInt(KEY_SERVER_GARA_ID));

                bookedTicketModel.id = obj.getInt(KEY_SERVER_ID);
                bookedTicketModel.accountId = obj.getInt(KEY_SERVER_ACCOUNT_ID);
                bookedTicketModel.garageModel = gara;
                bookedTicketModel.bookedTime = Constant.KEY_DATE_TIME_FORMAT.parse(obj.getString(KEY_SERVER_BOOKED_TIME));
                bookedTicketModel.isExpired = obj.getInt(KEY_SERVER_IS_EXPIRED) == 1 ? true : false;
                bookedTicketModel.checkinTime = obj.getString(KEY_SERVER_CHECKIN_TIME).equals("") ? null : Constant.KEY_DATE_TIME_FORMAT.parse(obj.getString(KEY_SERVER_CHECKIN_TIME));
                bookedTicketModel.checkoutTime =  obj.getString(KEY_SERVER_CHECKOUT_TIME).equals("") ? null : Constant.KEY_DATE_TIME_FORMAT.parse(obj.getString(KEY_SERVER_CHECKOUT_TIME));
            } catch (JSONException e) {
                Log.e(TAG, "error while get json attribute: " + e.getMessage());
                return null;
            } catch (ParseException e) {
                Log.e(TAG, "error while parse time: " + e.getMessage());
                return null;
            }
        }

        return bookedTicketModel;
    }

    public GarageModel getGarageModel() {
        return garageModel;
    }

    public void setGarageModel(GarageModel garageModel) {
        this.garageModel = garageModel;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public void setBookedTime(Date bookedTime) {
        this.bookedTime = bookedTime;
    }

    public void setExpired(boolean expired) {
        isExpired = expired;
    }

    public void setCheckinTime(Date checkinTime) {
        this.checkinTime = checkinTime;
    }

    public void setCheckoutTime(Date checkoutTime) {
        this.checkoutTime = checkoutTime;
    }

    public int getId() {
        return id;
    }

    public int getAccountId() {
        return accountId;
    }

    public Date getBookedTime() {
        return bookedTime;
    }

    public boolean isExpired() {
        return isExpired;
    }

    public Date getCheckinTime() {
        return checkinTime;
    }

    public Date getCheckoutTime() {
        return checkoutTime;
    }


}
