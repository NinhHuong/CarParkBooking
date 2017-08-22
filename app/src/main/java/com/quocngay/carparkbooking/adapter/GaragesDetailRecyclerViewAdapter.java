package com.quocngay.carparkbooking.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.quocngay.carparkbooking.R;
import com.quocngay.carparkbooking.model.GarageAdminModel;
import com.quocngay.carparkbooking.other.OnListInteractionListener;

import java.util.List;

public class GaragesDetailRecyclerViewAdapter
        extends RecyclerView.Adapter<GaragesDetailRecyclerViewAdapter.ViewHolder> {

    private final List<GarageAdminModel> mValues;
    private final OnListInteractionListener mListener;

    //
    public GaragesDetailRecyclerViewAdapter(List<GarageAdminModel> items,
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
        holder.mEmail.setText(mValues.get(position).getEmail());

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onGarageDeleteListener(holder.mItem);
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
        public final TextView mEmail;
        public final ImageView btnDelete;
        public GarageAdminModel mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mGaraTitle = (TextView) view.findViewById(R.id.tv_gara_detail_title);
            mGaraDes = (TextView) view.findViewById(R.id.tv_gara_detail_des);
            mSlots = (TextView) view.findViewById(R.id.tv_gara_detail_slots);
            mEmail = (TextView) view.findViewById(R.id.tv_gara_detail_email);
            btnDelete = (ImageView) view.findViewById(R.id.btn_gara_delete);
        }
    }
}
