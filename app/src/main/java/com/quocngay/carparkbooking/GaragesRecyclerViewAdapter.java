package com.quocngay.carparkbooking;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.quocngay.carparkbooking.activity.NearestGaraActivity;
import com.quocngay.carparkbooking.model.LocationDataModel;

import java.util.List;

public class GaragesRecyclerViewAdapter extends RecyclerView.Adapter<GaragesRecyclerViewAdapter.ViewHolder> {

    private final List<LocationDataModel> mValues;
    private final NearestGaraActivity.OnListInteractionListener mListener;

    //
    public GaragesRecyclerViewAdapter(List<LocationDataModel> items, NearestGaraActivity.OnListInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_garage, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        if (mValues.get(position) == null) return;
        holder.mGaraTitle.setText(mValues.get(position).getGarageModel().getName());
        holder.mGaraAddress.setText(mValues.get(position).getGarageModel().getAddress());
        holder.mDuration.setText(mValues.get(position).getDuration());
        holder.mDistance.setText(mValues.get(position).getDistance());
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mGaraTitle;
        public final TextView mGaraAddress;
        public final TextView mDuration;
        public final TextView mDistance;
        public LocationDataModel mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mGaraTitle = (TextView) view.findViewById(R.id.tv_gara_title);
            mGaraAddress = (TextView) view.findViewById(R.id.tv_gara_address);
            mDuration = (TextView) view.findViewById(R.id.tv_duration);
            mDistance = (TextView) view.findViewById(R.id.tv_distance);
        }
    }
}
