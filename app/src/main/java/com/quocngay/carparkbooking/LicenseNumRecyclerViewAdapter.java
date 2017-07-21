package com.quocngay.carparkbooking;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.quocngay.carparkbooking.activity.NearestGaraActivity;
import com.quocngay.carparkbooking.model.LocationDataModel;

import java.util.List;

public class LicenseNumRecyclerViewAdapter extends RecyclerView.Adapter<LicenseNumRecyclerViewAdapter.ViewHolder> {

    private final List<String> mValues;

    //
    public LicenseNumRecyclerViewAdapter(List<String> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_car_number, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        if (mValues.get(position) == null) return;
        holder.mLiencseNumber.setText(mValues.get(position).toString());

    }

    @Override
    public int getItemCount() {
        return mValues == null ? 0 : mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mLiencseNumber;
        public String mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mLiencseNumber = (TextView) view.findViewById(R.id.tv_license_number);
        }
    }
}
