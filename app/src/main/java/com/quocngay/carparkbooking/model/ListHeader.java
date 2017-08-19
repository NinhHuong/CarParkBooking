package com.quocngay.carparkbooking.model;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.quocngay.carparkbooking.R;
import com.quocngay.carparkbooking.adapter.HistoryListAdapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Quang Si on 7/26/2017.
 */

public class ListHeader implements Item {
    private final String date;

    public ListHeader(String date) {
        this.date = date;
    }

    @Override
    public int getViewType() {
        return HistoryListAdapter.RowType.HEADER_ITEM.ordinal();
    }

    @Override
    public View getView(LayoutInflater inflater, View convertView) {
        View view;
        if (convertView == null) {
            view = inflater.inflate(R.layout.item_header, null);
        } else {
            view = convertView;
        }
        SimpleDateFormat dateFormat =
                new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        TextView text = (TextView) view.findViewById(R.id.tv_header);
        String currentDate = dateFormat.format(new Date());
        if (date.equals(currentDate)) {
            text.setText(view.getResources().getString(R.string.today));
        } else {
            text.setText(date);
        }
        return view;
    }
}
