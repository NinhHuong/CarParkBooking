package com.quocngay.carparkbooking.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.google.gson.Gson;
import com.quocngay.carparkbooking.R;
import com.quocngay.carparkbooking.model.ParkingInfoModel;
import com.quocngay.carparkbooking.other.Constant;
import com.quocngay.carparkbooking.other.HistoryListAdapter;
import com.quocngay.carparkbooking.other.SocketIOClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ForgetPasswordActivity extends AppCompatActivity {

    private Button btnForget;
    private String email;
    private EditText edEmail;
    private Dialog dalInputCode;
    private Button btnDalContinue;
    private TextView tvDalCancel;
    private EditText edCode1, edCode2, edCode3, edCode4;
    private String code;
    private Emitter.Listener onResponseCompareCode = new Emitter.Listener() {

        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    try {
                        Boolean result = data.getBoolean(Constant.RESULT);
                        if (result) {
                            Intent intent = new Intent(ForgetPasswordActivity.this, NewPasswordActivity.class);
                            intent.putExtra(Constant.EMAIL, email);
                            dalInputCode.dismiss();
                            startActivity(intent);
                            finish();
                            SocketIOClient.client.mSocket.off();
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.error_code, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_forget);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        initCodeDialog();
        edEmail = (EditText) findViewById(R.id.edtForgetEmail);
        btnForget = (Button) findViewById(R.id.btnForget);

        btnForget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = edEmail.getText().toString().trim();
                if (!email.isEmpty() || Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    SocketIOClient.client.mSocket.emit(Constant.SERVER_REQUEST_RESET_PASSWORD, email);
                    SocketIOClient.client.mSocket.on(Constant.SERVER_RESPONSE_RESET_PASSWORD, new Emitter.Listener() {
                        @Override
                        public void call(final Object... args) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        final JSONObject jsonObject = ((JSONObject) args[0]);
                                        Boolean result = jsonObject.getBoolean(Constant.SERVER_RESPONSE_RESULT);
                                        if (result) {
                                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.forget_reset_success), Toast.LENGTH_SHORT).show();
//                                            changePassword();
                                            checkCode();
                                        } else {
                                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.server_error_email), Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), R.string.error_email, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void checkCode() {
        dalInputCode.show();
    }

    private void initCodeDialog() {
        dalInputCode = new Dialog(this);
        dalInputCode.setTitle(R.string.dialog_reset_title);
        dalInputCode.setContentView(R.layout.dialog_input_code);
        btnDalContinue = (Button) dalInputCode.findViewById(R.id.btn_continue);
        tvDalCancel = (TextView) dalInputCode.findViewById(R.id.tv_cancel);
        tvDalCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dalInputCode.dismiss();
            }
        });
        edCode1 = (EditText) dalInputCode.findViewById(R.id.edt_code1);
        edCode2 = (EditText) dalInputCode.findViewById(R.id.edt_code2);
        edCode3 = (EditText) dalInputCode.findViewById(R.id.edt_code3);
        edCode4 = (EditText) dalInputCode.findViewById(R.id.edt_code4);

        btnDalContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!edCode1.getText().toString().isEmpty() &&
                        !edCode2.getText().toString().isEmpty() &&
                        !edCode3.getText().toString().isEmpty() &&
                        !edCode4.getText().toString().isEmpty()) {
                    code = edCode1.getText().toString()
                            + edCode2.getText().toString()
                            + edCode3.getText().toString()
                            + edCode4.getText().toString();
                    SocketIOClient.client.mSocket.emit(Constant.REQUEST_COMPARE_CODE, email, code);
                    SocketIOClient.client.mSocket.on(Constant.RESPONSE_COMPARE_CODE, onResponseCompareCode);
                } else {
                    Toast.makeText(getApplicationContext(), R.string.error_code, Toast.LENGTH_SHORT).show();
                }
            }
        });

        edCode1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                edCode1.clearFocus();
                if (count == 1) {
                    edCode2.requestFocusFromTouch();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edCode2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                edCode2.clearFocus();
                if (count == 1) {
                    edCode3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        edCode3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                edCode3.clearFocus();
                if (count == 1) {
                    edCode4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        edCode4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count == 1) {
                    edCode4.clearFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    public static class History_Activity2 extends AppCompatActivity {

        ListView lvHistory;
        HistoryListAdapter adapter;
        List<ParkingInfoModel> mHistoryList;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_history_2);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            lvHistory = (ListView) findViewById(R.id.lvHistory);

            SocketIOClient.client.mSocket.emit("request_booking_account_id", 5);
            SocketIOClient.client.mSocket.on("response_booking_account_id", onGetParkingInfo);
        }

        private Emitter.Listener onGetParkingInfo = new Emitter.Listener() {

            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject data = (JSONObject) args[0];
                        try {
                            Boolean result = data.getBoolean(Constant.RESULT);
                            if (result) {
                                Log.i("Data park info", data.toString());
                                addDataToList(data);
                            } else {
                                Toast.makeText(getApplicationContext(), R.string.not_have_recort, Toast.LENGTH_SHORT).show();
                            }

                            SocketIOClient.client.mSocket.off();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        };

        void addDataToList(JSONObject data) {
            ParkingInfoModel p;
            Gson gson = new Gson();

            JSONArray listJsonGarasParkInfo = null;
            try {
                listJsonGarasParkInfo = data.getJSONArray(Constant.SERVER_PARK_INFO_RESULT);
                mHistoryList = new ArrayList<ParkingInfoModel>();
                for (int i = 0; i < listJsonGarasParkInfo.length(); i++) {
                    p = gson.fromJson(listJsonGarasParkInfo.getJSONObject(i).toString(), ParkingInfoModel.class);
                    p.setTimeBooked( ChangeDateTime(p.getTimeBooked()));
                    p.setTimeGoIn(ChangeDateTime(p.getTimeGoIn()));
                    p.setTimeGoOut(ChangeDateTime(p.getTimeGoOut()));
                    mHistoryList.add(p);
                }
                adapter = new HistoryListAdapter(getBaseContext(), mHistoryList);
                lvHistory.setAdapter(adapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        String ChangeDateTime(String timeMysql) {
            String year, time;
            year = timeMysql.substring(0,10);

            String inputPattern = "yyyy-mm-dd";
            String outputPattern = "dd/mm/yyyy";
            SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
            SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

            Date date = null;

            try {
                date = inputFormat.parse(year);
                year = outputFormat.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            time = timeMysql.substring(11,19);
            return time + "     "+year;
        }

    }
}


