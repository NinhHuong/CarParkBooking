package com.quocngay.carparkbooking;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;

import com.quocngay.carparkbooking.activity.BookingActivity;
import com.quocngay.carparkbooking.activity.CheckInActivity;
import com.quocngay.carparkbooking.model.CarModel;
import com.quocngay.carparkbooking.model.ParkingInfoSecurityModel;
import com.quocngay.carparkbooking.other.OnListInteractionListener;

import java.util.ArrayList;
import java.util.List;

public class LicenseSecurityRecyclerViewAdapter
        extends RecyclerView.Adapter<LicenseSecurityRecyclerViewAdapter.ViewHolder>
        implements Filterable{

    private final List<ParkingInfoSecurityModel> mValues;
    private List<ParkingInfoSecurityModel> mFilteredList;
    private OnListInteractionListener mListener;

    public LicenseSecurityRecyclerViewAdapter(List<ParkingInfoSecurityModel> items,
                                              OnListInteractionListener listener) {
        mValues = items;
        this.mListener = listener;
        mFilteredList = items;
    }

    public void swap(List list){
        if (mFilteredList != null) {
            mFilteredList.clear();
            mFilteredList.addAll(list);
        }
        else {
            mFilteredList = list;
        }
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_license_security, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mFilteredList.get(position);
        if (mFilteredList.get(position) == null) return;
        holder.mLicenseNumber.setText(mFilteredList.get(position).getVehicleNumber());
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.OnLicenseClickListener(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mFilteredList == null ? 0 : mFilteredList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    mFilteredList = mValues;
                } else {
                    ArrayList<ParkingInfoSecurityModel> filteredList = new ArrayList<>();
                    for (ParkingInfoSecurityModel model : mValues) {
                        if (model.getVehicleNumber().toLowerCase().contains(charString)) {
                            filteredList.add(model);
                        }
                    }
                    mFilteredList = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = mFilteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mFilteredList = (ArrayList<ParkingInfoSecurityModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mLicenseNumber;
        public ParkingInfoSecurityModel mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mLicenseNumber = (TextView) view.findViewById(R.id.tv_license);
        }
    }


}
