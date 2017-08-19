package com.quocngay.carparkbooking.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.quocngay.carparkbooking.R;
import com.quocngay.carparkbooking.model.AccountModel;
import com.quocngay.carparkbooking.model.LocalData;
import com.quocngay.carparkbooking.other.Constant;
import com.quocngay.carparkbooking.other.SocketIOClient;

import java.util.List;

/**
 * Created by Windows on 07-Aug-17.
 */

public class AccountListAdapter extends BaseExpandableListAdapter {
    private Context mContext;
    private List<AccountModel> mAccountList;
    private AlertDialog.Builder mDeleteDialog;
    private Activity parentActivity;


    public AccountListAdapter(Activity parentActivity, Context mContext, List<AccountModel> mAccountList) {
        this.parentActivity = parentActivity;
        this.mContext = mContext;
        this.mAccountList = mAccountList;
    }

//    @Override
//    public int getCount() {
//        return mAccountList.size();
//    }
//
//    @Override
//    public Object getItem(int position) {
//        return mAccountList.get(position);
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return position;
//    }
//
//    @Override
//    public View getView(final int position, View convertView, ViewGroup parent) {
//        View v = View.inflate(mContext, R.layout.item_account_management, null);
//        TextView txtFirstName, txtLastName, txtEmail, txtRole, txtphone, txtdob, txtAddress;
//        txtFirstName = (TextView) v.findViewById(R.id.txtFirstName);
//        txtLastName = (TextView) v.findViewById(R.id.txtLastName);
//        txtEmail = (TextView) v.findViewById(R.id.txtEmail);
//        txtRole = (TextView) v.findViewById(R.id.txtRole);
//        txtphone = (TextView) v.findViewById(R.id.txtPhone);
//        txtdob = (TextView) v.findViewById(R.id.txtdob);
//        txtAddress = (TextView) v.findViewById(R.id.txtAddress);
//        Button btnDelete = (Button) v.findViewById(R.id.btnDelete);
//
//        txtFirstName.setText(mAccountList.get(position).getFirstName());
//        txtLastName.setText(mAccountList.get(position).getLastName());
//        txtEmail.setText(mAccountList.get(position).getEmail());
//        txtRole.setText(mAccountList.get(position).getRoleID().compareTo("3") == 0 ? "Bảo vệ" : " ");
//        txtphone.setText(mAccountList.get(position).getPhone());
//        txtdob.setText(mAccountList.get(position).getDateOfBirth());
//        txtAddress.setText(mAccountList.get(position).getAddress());
//
//        btnDelete.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                mDeleteDialog = new AlertDialog.Builder(parentActivity);
//                mDeleteDialog.setTitle(R.string.dialog_delete_sec_title);
//                mDeleteDialog.setMessage(R.string.dialog_delete_sec_mess)
//                        .setPositiveButton(R.string.fire, new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
////                                SocketIOClient.client.mSocket.emit(Constant.REQUEST_REMOVE_SECURITY, mAccountList.get(position).getId());
//                            }
//                        })
//                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
//                                dialog.dismiss();
//                            }
//                        });
//                // Create the AlertDialog object and return it
//                mDeleteDialog.create().show();
//
//            }
//        });
//        v.setTag(mAccountList.get(position).getId());
//
//        return v;
//    }

    @Override
    public int getGroupCount() {
        return mAccountList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mAccountList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mAccountList.get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return mAccountList.get(groupPosition).hashCode();
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return mAccountList.get(childPosition).hashCode();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService
                    (Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.item_account_management, parent, false);
        }
        TextView txtName;
        txtName = (TextView) v.findViewById(R.id.txtName);
        txtName.setText(mAccountList.get(groupPosition).getFullName());
        return v;
    }

    @Override
    public View getChildView(final int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService
                    (Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.item_account_detail, parent, false);
        }
        final AccountModel accountModel = mAccountList.get(groupPosition);
        TextView  txtEmail, txtRole, txtphone, txtdob, txtAddress;
        txtEmail = (TextView) v.findViewById(R.id.txtEmail);
        txtRole = (TextView) v.findViewById(R.id.txtRole);
        txtphone = (TextView) v.findViewById(R.id.txtPhone);
        txtdob = (TextView) v.findViewById(R.id.txtdob);
        txtAddress = (TextView) v.findViewById(R.id.txtAddress);
        Button btnDelete = (Button) v.findViewById(R.id.btnDelete);

        txtEmail.setText(accountModel.getEmail());
        txtRole.setText(accountModel.getRoleID().compareTo("3") == 0 ? "Bảo vệ" : " ");
        txtphone.setText(accountModel.getPhone());
        txtdob.setText(accountModel.getDateOfBirth());
        txtAddress.setText(accountModel.getAddress());


        btnDelete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mDeleteDialog = new AlertDialog.Builder(parentActivity);
                mDeleteDialog.setTitle(R.string.dialog_delete_sec_title);
                mDeleteDialog.setMessage(R.string.dialog_delete_sec_mess)
                        .setPositiveButton(R.string.fire, new DialogInterface.OnClickListener() {
                            LocalData l = new LocalData(mContext);
                            String garageID = l.getGarageID();
                            int accountID =accountModel.getId();

                            public void onClick(DialogInterface dialog, int id) {
                                SocketIOClient.client.mSocket.emit(Constant.REQUEST_REMOVE_SECURITY, accountID,garageID);
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
        v.setTag(mAccountList.get(childPosition).getId());

        return v;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}