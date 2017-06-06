package com.quocngay.carparkbooking.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.quocngay.carparkbooking.R;
import com.quocngay.carparkbooking.model.BookedTicketModel;
import com.quocngay.carparkbooking.model.GarageModel;
import com.quocngay.carparkbooking.other.Constant;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by ninhh on 5/23/2017.
 */

public class BookedTicketAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private List<BookedTicketModel> listTicket;
    private Context context;
    private static String TAG = BookedTicketAdapter.class.getSimpleName();

    public BookedTicketAdapter(List<BookedTicketModel> listTicket, Context context) {
        if(listTicket != null) {
            this.listTicket = listTicket;
        } else {
            this.listTicket = new ArrayList<>();
        }
        this.listTicket = listTicket;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final BookedTicketModel bookedTicketModel = listTicket.get(position);
        ViewHolder holder = null;

        if(convertView == null) {
            LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.item_booked_ticket, null);

            holder = new ViewHolder();
            holder.txtGaraName = (TextView) convertView.findViewById(R.id.tv_gara_name);
            holder.txtGaraAddress = (TextView) convertView.findViewById(R.id.tv_gara_address);
            holder.txtGaraTotalSlot = (TextView) convertView.findViewById(R.id.tv_gara_total_slot);
            holder.txtGaraBookedSlot = (TextView) convertView.findViewById(R.id.tv_gara_booked_slot);
            holder.txtCountTime = (TextView) convertView.findViewById(R.id.tv_time_count);
            holder.btnCheckin = (Button) convertView.findViewById(R.id.btn_checkin);
            holder.btnCheckout = (Button) convertView.findViewById(R.id.btn_checkout);
            convertView.setTag(holder);

            new cdTimer(10000,1000,holder.txtCountTime);

            //on click event
            holder.btnCheckin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //send request to server
                    /*try {
                        JSONObject param = new JSONObject();
                        param.put(BookedTicketModel.KEY_SERVER_ID, bookedTicketModel.getId());
                        ServerRequest sr = new ServerRequest();
                        JSONObject jsonObj = sr.getResponse("http://54.255.178.120:5000/ticket/create", param);
                        if(jsonObj == null || !jsonObj.getBoolean(Constant.SERVER_RESPONSE)) {
                            Toast.makeText(context, context.getResources().getString(R.string.fail_internet_connection), Toast.LENGTH_LONG).show();;
                        } else {
                            validateToken(bookedTicketModel);
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, e.getMessage());
                    }*/
                }
            });
            holder.btnCheckout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        GarageModel garageModel = bookedTicketModel.getGarageModel();
        holder.txtGaraName.setText(garageModel.getName());
        holder.txtGaraAddress.setText(garageModel.getAddress());
        holder.txtGaraTotalSlot.setText(context.getResources().getString(R.string.gara_total) + " " + garageModel.getTotalSlot());
        holder.txtGaraBookedSlot.setText(context.getResources().getString(R.string.gara_booked) + " " + garageModel.getBookedSlot());

        if(bookedTicketModel.getCheckoutTime() != null) {
            long diff = bookedTicketModel.getCheckoutTime().getTime() - bookedTicketModel.getCheckinTime().getTime();
            holder.txtCountTime.setText(Constant.KEY_DATE_TIME_DURATION_FORMAT.format(new Date(diff)));
            holder.btnCheckin.setVisibility(View.GONE);
            holder.btnCheckout.setVisibility(View.INVISIBLE);
        } else if(bookedTicketModel.getCheckinTime() != null) {
            long diff = (new Date()).getTime() - bookedTicketModel.getCheckinTime().getTime();
            holder.txtCountTime.setText(Constant.KEY_DATE_TIME_DURATION_FORMAT.format(new Date(diff)));
            holder.btnCheckin.setVisibility(View.GONE);
            holder.btnCheckout.setVisibility(View.VISIBLE);
        } else {
            long diff = (new Date()).getTime() - bookedTicketModel.getBookedTime().getTime();
            holder.txtCountTime.setText(Constant.KEY_TIME_DURATION_FORMAT.format(new Date(diff)));
            holder.btnCheckin.setVisibility(View.VISIBLE);
            holder.btnCheckout.setVisibility(View.GONE);
        }

        return convertView;
    }

    public void swap(List<BookedTicketModel> list1){
        this.listTicket.clear();
        this.listTicket.addAll(list1);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return listTicket.size();
    }

    @Override
    public Object getItem(int position) {
        return listTicket.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private void validateToken(final BookedTicketModel bookedTicketModel) {
        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(context);
        View tokenView = li.inflate(R.layout.dialog_validate_token, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(tokenView);

        final EditText userInput = (EditText) tokenView.findViewById(R.id.edt_token_input);
        // set dialog message
        alertDialogBuilder.setPositiveButton("Ok", null);
        alertDialogBuilder.setNegativeButton("Cancel", null);
        // create alert dialog
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                Button positiveBtn = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positiveBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /*try{
                            JSONObject param = new JSONObject();
                            param.put(BookedTicketModel.KEY_SERVER_ID, bookedTicketModel.getId());
                            param.put(BookedTicketModel.KEY_SERVER_USER_INPUT, userInput.getText().toString().trim());
                            ServerRequest sr = new ServerRequest();
                            JSONObject jsonObj = sr.getResponse("http://54.255.178.120:5000/ticket/validate", param);
                            Log.e(TAG, "Response from url: " + jsonObj);

                            if(jsonObj == null || !jsonObj.getBoolean(Constant.SERVER_RESPONSE)) {
                                Toast.makeText(context, context.getResources().getString(R.string.fail_internet_connection), Toast.LENGTH_LONG).show();
                            } else {
                                if(jsonObj.getJSONObject(Constant.SERVER_RESPONSE_DATA).getBoolean(BookedTicketModel.KEY_SERVER_IS_VALID_TOKEN)) {
                                    Toast.makeText(context, "Correct toked. Map will show here", Toast.LENGTH_LONG).show();
                                    dialog.dismiss();
                                } else {
                                    Toast.makeText(context, context.getResources().getString(R.string.invalid_token_message), Toast.LENGTH_LONG).show();
                                    userInput.setText("");
                                }
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, e.getMessage());
                        }*/
                    }
                });

                Button nagativeBtn = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                nagativeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });

        // show it
        alertDialog.show();
    }

    private class ViewHolder{
        TextView txtGaraName;
        TextView txtGaraAddress;
        TextView txtGaraTotalSlot;
        TextView txtGaraBookedSlot;
        TextView txtCountTime;
        Button btnCheckin;
        Button btnCheckout;
    }

    public class cdTimer extends CountDownTimer {
        TextView txtShow;
        long second = 1000, minute =second*60;
        public cdTimer(long millisInFuture, long countDownInterval,TextView txtShow) {
            super(millisInFuture, countDownInterval);
            second = countDownInterval;
            this.txtShow = txtShow;
        }

        @Override
        public void onFinish() {
            txtShow.setText("Time out.");
        }

        @Override
        public void onTick(long millisUntilFinished) {
            txtShow.setText(changeMillisToMinute(millisUntilFinished));
        }

        private String changeMillisToMinute(long millisUntilFinished){
            String result = "";
            result += millisUntilFinished /minute;
            result +=(millisUntilFinished - millisUntilFinished /minute)/second;
            return  result;
        }
    }
}
