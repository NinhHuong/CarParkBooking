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
import com.quocngay.carparkbooking.dbcontext.DbContext;
import com.quocngay.carparkbooking.model.TicketModel;
import com.quocngay.carparkbooking.model.GarageModel;
import com.quocngay.carparkbooking.other.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by ninhh on 5/23/2017.
 */

public class BookedTicketAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private List<TicketModel> listTicket;
    private Context context;
    private Socket mSocket;
    private DbContext dbContext;

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
        dbContext = DbContext.getInst();
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
        if(holder.timer != null) {
            holder.timer.cancel();
            holder.timer = null;
        }

        if(ticketModel.getCheckinTime() != null) {
//            long diff = (new Date()).getTime() - ticketModel.getCheckinTime().getTime();
            long diff = ticketModel.getCheckinTime().getHours() * 60 * 1000;
            holder.btnCheckin.setVisibility(View.GONE);
            holder.btnCheckout.setVisibility(View.VISIBLE);
            holder.timer = new CountTimer(Long.MAX_VALUE, diff, Constant.KEY_COUNT_UP_INTERVAL, holder.txtCountTime, false);
            holder.timer.start();
        } else {
            holder.btnCheckin.setVisibility(View.VISIBLE);
            holder.btnCheckout.setVisibility(View.GONE);
            holder.timer = new CountTimer(Constant.KEY_EXPIRED_TICKET, 0, Constant.KEY_COUNT_DOWN_INTERVAL, holder.txtCountTime, true);
            holder.timer.start();
        }

        return convertView;
    }

    private void refreshData(){
        this.listTicket = dbContext.getAllOpenBookedTicketModel();
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
        if(alertDialog.getWindow() != null) {
            alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }
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
                                                    //checkin successfully. update time checkin
                                                    JSONArray tickets = jsonObj.getJSONObject(Constant.SERVER_RESPONSE_DATA).getJSONArray(TicketModel.KEY_SERVER_LIST_TICKET);
                                                    for(int i=0; i<tickets.length(); i++) {
                                                        TicketModel ticket = TicketModel.createByJson(tickets.getJSONObject(i));
                                                        if(ticket != null) {
                                                            dbContext.addBookedTicketModel(ticket);
                                                        }
                                                    }
                                                    refreshData();

                                                    Toast.makeText(context, "Correct token. Map will show here", Toast.LENGTH_LONG).show();
                                                    alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
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
        CountTimer timer;
    }

    private class CountTimer extends CountDownTimer {
        TextView txtShow;
        boolean isCountDown;
        long duration;
        long countDownInterval;
        long startMillis;

        private CountTimer(long duration, long startMillis, long countDownInterval, TextView txtShow, boolean isCountDown) {
            super(duration, countDownInterval);
            this.isCountDown = isCountDown;
            this.countDownInterval = countDownInterval;
            this.txtShow = txtShow;
            this.startMillis = startMillis;
            this.duration = duration;
        }

        @Override
        public void onFinish() {
            txtShow.setText(context.getResources().getString(R.string.time_exprited));
            txtShow.setBackgroundColor(context.getResources().getColor(R.color.count_time_expired));
        }

        @Override
        public void onTick(long millisUntilFinished) {
            long current = millisUntilFinished;
            if(isCountDown) {
                txtShow.setText(convertDownMilisToString(current));
            } else {
                current = duration - current;
                txtShow.setText(convertUpMilisToString(current + startMillis));
            }
        }

        private String convertDownMilisToString(long millis) {
            long seconds = millis / 1000;
            long hours = seconds / (60 * 60);
            long minutes = (seconds - hours * 60 * 60) / 60;
            seconds = seconds - hours * 60 * 60 - minutes * 60;
            return String.format(Locale.getDefault(),"%02d:%02d:%02d", hours, minutes, seconds);
        }

        private String convertUpMilisToString(long millis) {
            long minutes = millis / 1000 / 60;
            long days = minutes / (24 * 60);
            long hours = (minutes - days * 24 * 60) / 60;
            minutes = minutes - days * 24 * 60 - hours * 60;
            return String.format(Locale.getDefault(),"%02d %02d:%02d", days, hours, minutes);
        }
    }

}
