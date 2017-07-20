package com.quocngay.carparkbooking.model;

/**
 * Created by Quang Si on 7/20/2017.
 */

public class LocationDataModel {

    private String duration;
    private String distance;
    private GarageModel garageModel;

    public LocationDataModel(GarageModel garageModel, String duration, String distance) {
        this.garageModel = garageModel;
        this.duration = duration;
        this.distance = distance;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public GarageModel getGarageModel() {
        return garageModel;
    }

    public void setGarageModel(GarageModel garageModel) {
        this.garageModel = garageModel;
    }
}
