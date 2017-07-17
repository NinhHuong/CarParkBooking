package com.quocngay.carparkbooking.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.quocngay.carparkbooking.R;
import com.quocngay.carparkbooking.other.Constant;
import com.quocngay.carparkbooking.other.SocketIOClient;

import org.json.JSONException;
import org.json.JSONObject;

public class ForgetPasswordActivity extends AppCompatActivity {

    private Button btnForget;
    private String email;
    private EditText edEmail;
    private Dialog dalReset;
    private Button btnDalContinue;
    private Button btnDalCancel;
    private EditText edtDalCode;
    private EditText edtDalNewPass;
    private EditText edtDalRePass;

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

        dalReset = new Dialog(this);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dalReset.setTitle(R.string.dialog_reset_title);
        dalReset.setContentView(R.layout.dialog_reset_password);
        btnDalCancel = (Button) dalReset.findViewById(R.id.btn_cancel);
        btnDalCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dalReset.dismiss();
            }
        });

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
                                        String mess = jsonObject.getJSONObject(Constant.SERVER_RESPONSE_DATA).getString(Constant.MESSAGE);
                                        Toast.makeText(getApplicationContext(), mess, Toast.LENGTH_SHORT).show();
                                        if (jsonObject.getBoolean(Constant.SERVER_RESPONSE_RESULT)) {
                                            changePassword();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), R.string.error_email, Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void changePassword() {
        dalReset.setContentView(R.layout.dialog_change_password);
        btnDalContinue = (Button) dalReset.findViewById(R.id.btn_continue);
        btnDalCancel = (Button) dalReset.findViewById(R.id.btn_cancel);
        edtDalCode = (EditText) dalReset.findViewById(R.id.edt_code);
        edtDalNewPass = (EditText) dalReset.findViewById(R.id.edt_new_pass);
        edtDalRePass = (EditText) dalReset.findViewById(R.id.edt_repass);

        btnDalCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dalReset.dismiss();
            }
        });

        btnDalContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = edtDalCode.getText().toString().trim();
                String pass = edtDalNewPass.getText().toString().trim();
                if (pass.equals(edtDalRePass.getText().toString().trim())) {
                    SocketIOClient.client.mSocket.emit(Constant.SERVER_REQUEST_CHANGE_PASSWORD, email, code, pass);
                    SocketIOClient.client.mSocket.on(Constant.SERVER_RESPONSE_CHANGE_PASSWORD, new Emitter.Listener() {
                        @Override
                        public void call(final Object... args) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        JSONObject resObj = ((JSONObject) args[0]);
                                        String mess = resObj.getJSONObject(Constant.SERVER_RESPONSE_DATA).getString(Constant.MESSAGE);
                                        Toast.makeText(getApplicationContext(), mess, Toast.LENGTH_LONG).show();
                                        if (resObj.getBoolean(Constant.SERVER_RESPONSE_RESULT)) {
                                            dalReset.dismiss();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), R.string.error_password, Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}


