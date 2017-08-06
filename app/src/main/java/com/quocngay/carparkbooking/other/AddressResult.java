package com.quocngay.carparkbooking.other;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

import com.google.android.gms.maps.model.Marker;
import com.quocngay.carparkbooking.model.GarageModel;

/**
 * Created by Quang Si on 7/22/2017.
 */

public class AddressResult extends ResultReceiver {

    private GarageModel garageModel;

    public AddressResult(Handler handler, GarageModel garageModel) {
        super(handler);
        this.garageModel = garageModel;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {

        String mAddressOutput = resultData.getString(Constant.RESULT_DATA_KEY);
        String mAddressTitle = resultData.getString(Constant.RESULT_TITLE);
        garageModel.setAddress(mAddressOutput);

    }
}
