package com.quocngay.carparkbooking.other;

import com.quocngay.carparkbooking.model.GarageModel;
import com.quocngay.carparkbooking.model.ParkingInfoSecurityModel;

/**
 * Created by Quang Si on 8/13/2017.
 */

public interface OnListInteractionListener {

    void onLicenseClickListener(ParkingInfoSecurityModel item);

    void onGarageDeleteListener(GarageModel item);

}
