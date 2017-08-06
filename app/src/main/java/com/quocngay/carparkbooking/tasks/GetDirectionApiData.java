package com.quocngay.carparkbooking.tasks;

import com.quocngay.carparkbooking.model.GarageModel;
import com.quocngay.carparkbooking.model.LocationDataModel;

/**
 * Created by Quang Si on 8/2/2017.
 */

public class GetDirectionApiData extends DownloadTask {

    private GarageModel garageModel;

    public GetDirectionApiData(GarageModel garageModel) {
        this.garageModel = garageModel;
    }

}