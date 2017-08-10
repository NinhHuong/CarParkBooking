package com.quocngay.carparkbooking.other;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.quocngay.carparkbooking.R;
import com.quocngay.carparkbooking.model.AccountModel;

import java.util.List;

/**
 * Created by Windows on 07-Aug-17.
 */

public class AccountListAdapter extends BaseAdapter {
    private Context mContext;
    private List<AccountModel> mAccountList;
    private AlertDialog.Builder mDeleteDialog;
    private Activity parentActivity;


    public AccountListAdapter(Activity parentActivity, Context mContext, List<AccountModel> mAccountList) {
        this.parentActivity = parentActivity;
        this.mContext = mContext;
        this.mAccountList = mAccountList;
    }

    @Override
    public int getCount() {
        return mAccountList.size();
    }

    @Override
    public Object getItem(int position) {
        return mAccountList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = View.inflate(mContext, R.layout.item_account_management, null);
        TextView txtFirstName, txtLastName, txtEmail, txtRole, txtphone, txtdob, txtAddress;
        txtFirstName = (TextView) v.findViewById(R.id.txtFirstName);
        txtLastName = (TextView) v.findViewById(R.id.txtLastName);
        txtEmail = (TextView) v.findViewById(R.id.txtEmail);
        txtRole = (TextView) v.findViewById(R.id.txtRole);
        txtphone = (TextView) v.findViewById(R.id.txtPhone);
        txtdob = (TextView) v.findViewById(R.id.txtdob);
        txtAddress = (TextView) v.findViewById(R.id.txtAddress);
        Button btnDelete = (Button) v.findViewById(R.id.btnDelete);

        txtFirstName.setText(mAccountList.get(position).getFirstName());
        txtLastName.setText(mAccountList.get(position).getLastName());
        txtEmail.setText(mAccountList.get(position).getEmail());
        txtRole.setText(mAccountList.get(position).getRoleID().compareTo("3") == 0 ? "Bảo vệ" : " ");
        txtphone.setText(mAccountList.get(position).getPhone());
        txtdob.setText(mAccountList.get(position).getDateOfBirth());
        txtAddress.setText(mAccountList.get(position).getAddress());

        btnDelete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mDeleteDialog = new AlertDialog.Builder(parentActivity);
                mDeleteDialog.setTitle(R.string.dialog_delete_title);
                mDeleteDialog.setMessage(R.string.dialog_delete_car)
                        .setPositiveButton(R.string.fire, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
//                                SocketIOClient.client.mSocket.emit(Constant.REQUEST_REMOVE_CAR_BY_ID, mAccountList.get(position).getId());
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                // Create the AlertDialog object and return it
                mDeleteDialog.create().show();

            }
        });
        v.setTag(mAccountList.get(position).getId());

        return v;

    }


}