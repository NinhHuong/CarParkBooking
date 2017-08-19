package com.quocngay.carparkbooking.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.quocngay.carparkbooking.R;
import com.quocngay.carparkbooking.model.GarageModel;
import com.quocngay.carparkbooking.other.OnListInteractionListener;

import java.util.List;

public class GaragesDetailRecyclerViewAdapter
        extends RecyclerView.Adapter<GaragesDetailRecyclerViewAdapter.ViewHolder> {

    private final List<GarageModel> mValues;
    private final OnListInteractionListener mListener;

    //
    public GaragesDetailRecyclerViewAdapter(List<GarageModel> items,
                                            OnListInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_garage_detail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        if (mValues.get(position) == null) return;
        holder.mGaraTitle.setText(mValues.get(position).getName());
        holder.mGaraDes.setText(mValues.get(position).getAddress());
        holder.mSlots.setText(String.valueOf(mValues.get(position).getTotalSlot()));
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onGarageClickListener(holder.mItem);
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
        public final TextView mGaraDes;
        public final TextView mSlots;
        public GarageModel mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mGaraTitle = (TextView) view.findViewById(R.id.tv_gara_detail_title);
            mGaraDes = (TextView) view.findViewById(R.id.tv_gara_detail_des);
            mSlots = (TextView) view.findViewById(R.id.tv_gara_detail_slots);
        }
    }
}
