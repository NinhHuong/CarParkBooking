package com.quocngay.carparkbooking.dbcontext;

import com.quocngay.carparkbooking.model.TicketModel;
import com.quocngay.carparkbooking.model.GarageModel;
import com.quocngay.carparkbooking.other.Constant;

import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by ninhh on 5/24/2017.
 */

public class DbContext {
    public Realm realm;

    private DbContext() {
        realm = Realm.getDefaultInstance();
    }

    private static DbContext inst;

    public static DbContext getInst() {
        if (inst == null) {
            return new DbContext();
        }
        return inst;
    }

    //region GarageModel
    public void addGaraModel(GarageModel model) {
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(model);
        realm.commitTransaction();
    }

    public int getMaxGaraModelId() {
        try{
            return realm.where(GarageModel.class).max("id").intValue();
        } catch(Exception ignored){}
        return 0;
    }

    public List<GarageModel> getAllGaraModel(){
        return realm.where(GarageModel.class).findAll();
    }

    public GarageModel getGaraModelByID(int id) {
        return realm.where(GarageModel.class).equalTo("id", id).findFirst();
    }

    public void removeSingleGaraModel(int id) {
        final GarageModel result = realm.where(GarageModel.class).equalTo("id", id).findFirst();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                result.deleteFromRealm();
            }
        });
    }

    public void deleteAllGaraModel() {
        final RealmResults<GarageModel> result = realm.where(GarageModel.class).findAll();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                result.deleteAllFromRealm();
            }
        });
    }
    //endregion

    //region TicketModel
    public void addBookedTicketModel(TicketModel model) {
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(model);
        realm.commitTransaction();
    }

    public int getMaxBookedTicketModelId() {
        try{
            return realm.where(TicketModel.class).max("id").intValue();
        } catch(Exception ignored){}
        return 0;
    }

    public List<TicketModel> getAllBookedTicketModel(){
        return realm.where(TicketModel.class).findAll();
    }

    public List<TicketModel> getAllOpenBookedTicketModel(){
        Date date = new Date(new Date().getTime() - Constant.KEY_EXPIRED_TICKET);
        return realm.where(TicketModel.class)
                .beginGroup().isNotNull("checkinTime").isNull("checkoutTime").endGroup()
                .or()
                .beginGroup().isNull("checkinTime").equalTo("isExpired", false).endGroup()
                .findAll();
    }

    public TicketModel getBookedTicketModelByID(int id) {
        return realm.where(TicketModel.class).equalTo("id", id).findFirst();
    }

    public void removeSingleBookedTicketModel(int id) {
        final TicketModel result = realm.where(TicketModel.class).equalTo("id", id).findFirst();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                result.deleteFromRealm();
            }
        });
    }

    public void deleteAllBookedTicketModel() {
        final RealmResults<TicketModel> result = realm.where(TicketModel.class).findAll();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                result.deleteAllFromRealm();
            }
        });
    }
    //endregion
}
