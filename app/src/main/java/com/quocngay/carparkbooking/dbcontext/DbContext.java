package com.quocngay.carparkbooking.dbcontext;

import com.quocngay.carparkbooking.model.GarageModel;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by ninhh on 5/24/2017.
 */

public class DbContext {
    private static DbContext inst;
    public Realm realm;

    private DbContext() {
        realm = Realm.getDefaultInstance();
    }

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
}
