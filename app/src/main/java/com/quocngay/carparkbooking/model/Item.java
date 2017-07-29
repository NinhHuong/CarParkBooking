package com.quocngay.carparkbooking.model;

import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by Quang Si on 7/26/2017.
 */

public interface Item {

    int getViewType();

    View getView(LayoutInflater inflater, View convertView);
}
