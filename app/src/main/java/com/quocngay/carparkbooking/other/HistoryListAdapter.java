package com.quocngay.carparkbooking.other;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.quocngay.carparkbooking.R;
import com.quocngay.carparkbooking.model.Item;
import com.quocngay.carparkbooking.model.ParkingInfoHistoryModel;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.zip.Inflater;

import de.halfbit.pinnedsection.PinnedSectionListView;

/**
 * Created by Windows on 20-Jul-17.
 */

public class HistoryListAdapter extends ArrayAdapter<Item>
        implements PinnedSectionListView.PinnedSectionListAdapter{

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_HEADER = 1;

    private Context mContext;
    private ArrayList<Item> mParkingList;
    private LayoutInflater mInflater;

    public HistoryListAdapter(@NonNull Context context,
                              @NonNull ArrayList<Item> parkingList) {
        super(context, 0, parkingList);
        mInflater = LayoutInflater.from(context);
    }

    public enum RowType {
        LIST_ITEM, HEADER_ITEM
    }

    @Override
    public int getViewTypeCount() {
        return RowType.values().length;

    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getViewType();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getItem(position).getView(mInflater, convertView);
    }

    public static class ViewHolder {
        public View View;
    }

    @Override
    public boolean isItemViewTypePinned(int viewType) {
        return viewType == TYPE_HEADER;
    }

}
