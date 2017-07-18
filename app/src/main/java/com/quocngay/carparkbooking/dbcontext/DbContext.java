package com.quocngay.carparkbooking.dbcontext;

import io.realm.Realm;

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
}
