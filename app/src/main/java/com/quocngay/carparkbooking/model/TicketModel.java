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

public class TicketModel extends RealmObject {
    private static String KEY_SERVER_ID = "id";
    private static String KEY_SERVER_ACCOUNT_ID = "account_id";
    private static String KEY_SERVER_GARA_ID = "garage_id";
    private static String KEY_SERVER_BOOKED_TIME = "booked_time";
    private static String KEY_SERVER_CHECKIN_TIME = "checkin_time";
    private static String KEY_SERVER_CHECKOUT_TIME = "checkout_time";
    private static String KEY_SERVER_IS_EXPIRED = "is_expired";
    private static String KEY_SERVER_USER_INPUT = "user_input";
    public static String KEY_SERVER_IS_VALID_TOKEN = "is_valid_token";
    public static String KEY_SERVER_LIST_TICKET = "ticketList";

    private static String TAG = TicketModel.class.getSimpleName();
    
    @PrimaryKey
    private int id;
    private int accountId;
    private GarageModel garageModel;
    private Date bookedTime;
    private boolean isExpired;
    private Date checkinTime;
    private Date checkoutTime;

    public static TicketModel create(int accountId, GarageModel garageModel, Date bookedTime, boolean isExpired, Date checkinTime, Date checkoutTime) {
        TicketModel ticketModel = new TicketModel();
        ticketModel.accountId = accountId;
        ticketModel.garageModel = garageModel;
        ticketModel.bookedTime = bookedTime;
        ticketModel.isExpired = isExpired;
        ticketModel.checkinTime = checkinTime;
        ticketModel.checkoutTime = checkoutTime;
        return ticketModel;
    }

    public static TicketModel createwithoutId(GarageModel garageModel, Date bookedTime, boolean isExpired, Date checkinTime, Date checkoutTime) {
        TicketModel ticketModel = new TicketModel();
        DbContext dbContext = DbContext.getInst();
        ticketModel.accountId = dbContext.getMaxBookedTicketModelId() + 1;
        ticketModel.garageModel = garageModel;
        ticketModel.bookedTime = bookedTime;
        ticketModel.isExpired = isExpired;
        ticketModel.checkinTime = checkinTime;
        ticketModel.checkoutTime = checkoutTime;
        return ticketModel;
    }
    public static TicketModel createByJson(JSONObject obj) {
        TicketModel ticketModel = new TicketModel();

        if(obj != null) {
            try{
                DbContext dbContext = DbContext.getInst();
                GarageModel gara = dbContext.getGaraModelByID(obj.getInt(KEY_SERVER_GARA_ID));

                ticketModel.id = obj.getInt(KEY_SERVER_ID);
                ticketModel.accountId = obj.getInt(KEY_SERVER_ACCOUNT_ID);
                ticketModel.garageModel = gara;
                ticketModel.bookedTime = Constant.KEY_SERVER_DATE_TIME_FORMAT.parse(obj.getString(KEY_SERVER_BOOKED_TIME));
                ticketModel.isExpired = obj.getInt(KEY_SERVER_IS_EXPIRED) == 1;
                ticketModel.checkinTime = obj.getString(KEY_SERVER_CHECKIN_TIME).equals("") || obj.get(KEY_SERVER_CHECKIN_TIME).equals("0000-00-00 00:00:00")?
                        null : Constant.KEY_SERVER_DATE_TIME_FORMAT.parse(obj.getString(KEY_SERVER_CHECKIN_TIME));
                ticketModel.checkoutTime =  obj.getString(KEY_SERVER_CHECKOUT_TIME).equals("") || obj.get(KEY_SERVER_CHECKOUT_TIME).equals("0000-00-00 00:00:00") ?
                        null : Constant.KEY_SERVER_DATE_TIME_FORMAT.parse(obj.getString(KEY_SERVER_CHECKOUT_TIME));
            } catch (JSONException e) {
                Log.e(TAG, "error while get json attribute: " + e.getMessage());
                return null;
            } catch (ParseException e) {
                Log.e(TAG, "error while parse time: " + e.getMessage());
                return null;
            }
        }

        return ticketModel;
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
