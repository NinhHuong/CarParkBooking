package com.quocngay.carparkbooking.model;

import android.support.annotation.NonNull;

import java.io.Serializable;

/**
 * Created by Quang Si on 7/20/2017.
 */

public class LocationDataModel implements Serializable, Comparable<LocationDataModel>{

    private String duration;
    private long durationValue;
    private String distance;
    private Long distanceValue;
    private GarageModel garageModel;

    public LocationDataModel( GarageModel garageModel, String duration, Long durationValue, String distance, Long distanceValue) {
        this.duration = duration;
        this.durationValue = durationValue;
        this.distance = distance;
        this.distanceValue = distanceValue;
        this.garageModel = garageModel;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public Long getDurationValue() {
        return durationValue;
    }

    public void setDurationValue(long durationValue) {
        this.durationValue = durationValue;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public Long getDistanceValue() {
        return distanceValue;
    }

    public void setDistanceValue(long distanceValue) {
        this.distanceValue = distanceValue;
    }

    public GarageModel getGarageModel() {
        return garageModel;
    }

    public void setGarageModel(GarageModel garageModel) {
        this.garageModel = garageModel;
    }

    @Override
    public int compareTo(@NonNull LocationDataModel locationDataModel) {
        if (getDuration() == null || locationDataModel.getDuration() == null) {
            return 0;
        }
        int durationCompare = getDurationValue().compareTo(locationDataModel.getDurationValue());
        if(durationCompare == 0){
            if (getDistanceValue() == null || locationDataModel.getDistanceValue() == null) {
                return 0;
            }
            return getDistanceValue().compareTo(locationDataModel.getDistanceValue());
        }
        return durationCompare;
    }
}
