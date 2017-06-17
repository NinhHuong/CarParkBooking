package com.quocngay.carparkbooking.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.quocngay.carparkbooking.R;
import com.quocngay.carparkbooking.model.TicketModel;
import com.quocngay.carparkbooking.model.GarageModel;
import com.quocngay.carparkbooking.other.Constant;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by ninhh on 5/23/2017.
 */

public class BookedTicketAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private List<TicketModel> listTicket;
    private Context context;
    private Socket mSocket;
    private static String TAG = BookedTicketAdapter.class.getSimpleName();

    public BookedTicketAdapter(List<TicketModel> listTicket, Context context, Socket mSocket) {
        if(listTicket != null) {
            this.listTicket = listTicket;
        } else {
            this.listTicket = new ArrayList<>();
        }
        this.listTicket = listTicket;
        this.context = context;
        this.mSocket = mSocket;
        this.inflater = LayoutInflater.from(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final TicketModel ticketModel = listTicket.get(position);
        ViewHolder holder;

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


            //on click event
            holder.btnCheckin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //send request to server
                    mSocket.emit(Constant.SERVER_REQUEST_CREATE_TOKEN, ticketModel.getGarageModel().getId());
//                    mSocket.on(Constant.SERVER_RESPONSE_CREATE_TOKEN, onNewMessage_getOpenTickets);
                    validateToken(ticketModel);
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

        GarageModel garageModel = ticketModel.getGarageModel();
        holder.txtGaraName.setText(garageModel.getName());
        holder.txtGaraAddress.setText(garageModel.getAddress());
        holder.txtGaraTotalSlot.setText(context.getResources().getString(R.string.gara_total) + " " + garageModel.getTotalSlot());
        holder.txtGaraBookedSlot.setText(context.getResources().getString(R.string.gara_booked) + " " + garageModel.getBookedSlot());

        if(ticketModel.getCheckoutTime() != null ) {
            long diff = ticketModel.getCheckoutTime().getTime() - ticketModel.getCheckinTime().getTime();
            holder.txtCountTime.setText(Constant.KEY_DATE_TIME_DURATION_FORMAT.format(new Date(diff)));
            holder.btnCheckin.setVisibility(View.GONE);
            holder.btnCheckout.setVisibility(View.GONE);
        } else if(ticketModel.getCheckinTime() != null) {
            long diff = (new Date()).getTime() - ticketModel.getCheckinTime().getTime();
            holder.txtCountTime.setText(Constant.KEY_DATE_TIME_DURATION_FORMAT.format(new Date(diff)));
            holder.btnCheckin.setVisibility(View.GONE);
            holder.btnCheckout.setVisibility(View.VISIBLE);
            holder.SetTimer(Long.MAX_VALUE,1000,holder.txtCountTime,false);
        } else {
            long diff = (new Date()).getTime() - ticketModel.getBookedTime().getTime();
            holder.txtCountTime.setText(Constant.KEY_TIME_DURATION_FORMAT.format(new Date(diff)));
            holder.btnCheckin.setVisibility(View.VISIBLE);
            holder.btnCheckout.setVisibility(View.GONE);
            holder.SetTimer(60*30*1000,1000,holder.txtCountTime,true);
//            new cdTimer(60*30*1000,1000,holder.txtCountTime,true).start();
        }

        return convertView;
    }

    public void swap(List<TicketModel> list1){
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

    private void validateToken(final TicketModel ticketModel) {
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
                        mSocket.emit(Constant.SERVER_REQUEST_VALIDATE_TOKEN, ticketModel.getId(),
                                userInput.getText().toString().trim());

                        mSocket.on(Constant.SERVER_RESPONSE_VALIDATE_TOKEN, new Emitter.Listener() {
                            @Override
                            public void call(final Object... args) {
                                ((Activity) context).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            JSONObject jsonObj = ((JSONObject) args[0]);
                                            if(jsonObj.getBoolean(Constant.SERVER_RESPONSE_RESULT)) {
                                                if(jsonObj.getJSONObject(Constant.SERVER_RESPONSE_DATA).getBoolean(TicketModel.KEY_SERVER_IS_VALID_TOKEN)) {
                                                    //checkin successfully

                                                    Toast.makeText(context, "Correct token. Map will show here", Toast.LENGTH_LONG).show();
                                                    dialog.dismiss();
                                                } else {
                                                    Toast.makeText(context, context.getResources().getString(R.string.invalid_token_message), Toast.LENGTH_LONG).show();
                                                    userInput.setText("");
                                                }
                                            } else {
                                                Toast.makeText(context, "Something went wrong. Please try again", Toast.LENGTH_LONG).show();
                                            }
                                        } catch (JSONException e) {
                                            Log.e(TAG, e.getMessage());
                                        }

                                    }
                                });
                            }
                        });
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

        public void SetTimer(long millisInFuture, long countDownInterval,TextView txtShow,boolean isCountDown){
            new cdTimer(millisInFuture,countDownInterval,txtShow,isCountDown).start();
        }

        public class cdTimer extends CountDownTimer {
            TextView txtShow;
            boolean isCountDown;
            long second = 1000, minute =second*60;
            public cdTimer(long millisInFuture, long countDownInterval,TextView txtShow, boolean isCountDown) {
                super(millisInFuture, countDownInterval);
                this.isCountDown = isCountDown;
                second = countDownInterval;
                this.txtShow = txtShow;
            }

            @Override
            public void onFinish() {
                txtShow.setText("Time out.");
            }

            @Override
            public void onTick(long millisUntilFinished) {
                long current = millisUntilFinished;
                if(!isCountDown)
                    current = Long.MAX_VALUE - current;
                txtShow.setText(changeMillisToMinute(current));
            }

            private String changeMillisToMinute(long millisUntilFinished){
                String result = "";
                long trueSecond = millisUntilFinished / second;
                long trueMinute = trueSecond /60;
                long s = trueSecond - trueMinute *60;
                result += trueMinute+":";
                result +=  s;
                return  result;
            }
        }
    }
}
