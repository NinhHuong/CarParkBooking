package com.quocngay.carparkbooking.activity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.quocngay.carparkbooking.MapActivity;
import com.quocngay.carparkbooking.R;
import com.quocngay.carparkbooking.other.Constant;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    EditText edtEmail, edtPass, edtDalEmail, edtDalNewPass, edtDalCode, edtDalRePass;
    Button btnLogin, btnRegister, btnForgotPassword, btnDalContinue, btnDalCancel;
    Dialog dalReset;
    private static String TAG = LoginActivity.class.getSimpleName();
    String email;

    EditText edtEmail, edtPass;
    Button btnLogin;
    TextView tvRegister, tvForgotPassword;
    private Socket mSocket;
    private Emitter.Listener onNewMessage_ResultLogin = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    Log.i("Data", data.toString());
                    try {
                        boolean isEmailCorrect = data.getBoolean(Constant.SERVER_RESPONSE_LOGIN_PARA_EMAIL);
                        boolean isPasswordCorrect = data.getBoolean(Constant.SERVER_RESPONSE_LOGIN_PARA_PASSWORD);

                        if (!isEmailCorrect)
                            Toast.makeText(getBaseContext(), "Wrong email, try again", Toast.LENGTH_SHORT).show();
                        else if (!isPasswordCorrect)
                            Toast.makeText(getBaseContext(), "Wrong password, try again", Toast.LENGTH_SHORT).show();
                        else {

                            SharedPreferences sharedPref = getApplication().
                                    getSharedPreferences(Constant.APP_PREF, MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString(Constant.APP_PREF_TOKEN, data.getString(Constant.SERVER_RESPONSE_LOGIN_PARA_TOKEN));
                            editor.commit();

                            Toast.makeText(getBaseContext(), "LoginActivity success", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, MapActivity.class);
                            startActivity(intent);

                            finish();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    {
        try {
            mSocket = IO.socket(Constant.SERVER_HOST);
        } catch (URISyntaxException e) {
            Log.e("Error", e.getMessage());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_login);

        edtEmail = (EditText) findViewById(R.id.edtEmail);
        edtPass = (EditText) findViewById(R.id.edtPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        tvRegister = (TextView) findViewById(R.id.tvRegister);
        tvForgotPassword = (TextView) findViewById(R.id.tvForgotPass);

        btnLogin.setOnClickListener(this);
        tvRegister.setOnClickListener(this);
        tvForgotPassword.setOnClickListener(this);

        mSocket.connect();
        mSocket.on("ResultLogin",onNewMessage_ResultLogin);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnLogin:
                JSONObject j = new JSONObject();
                try {
                    j.put("Email",edtEmail.getText().toString());
                    j.put("Password",edtPass.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mSocket.emit("CheckEmailAndPassword",j.toString());
                break;
            case R.id.tvRegister:
                startActivity(new Intent(LoginActivity.this, Register.class));
                break;
            case R.id.tvForgotPass:
                resetPassord();
                break;
        }
    }

    private Emitter.Listener onNewMessage_ResultLogin =  new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    Log.i("Data",data.toString());
                    try {
                        boolean isEmailCorrect =data.getBoolean(Constant.SERVER_RESPONSE_LOGIN_PARA_EMAIL);
                        boolean isPasswordCorrect =data.getBoolean(Constant.SERVER_RESPONSE_LOGIN_PARA_PASSWORD);

                        if(!isEmailCorrect)
                            Toast.makeText(getBaseContext(),"Wrong email, try again",Toast.LENGTH_SHORT).show();
                        else if(!isPasswordCorrect)
                            Toast.makeText(getBaseContext(),"Wrong password, try again",Toast.LENGTH_SHORT).show();
                        else {

                            SharedPreferences sharedPref = getApplication().
                                    getSharedPreferences(Constant.APP_PREF,MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString(Constant.APP_PREF_TOKEN, data.getString(Constant.SERVER_RESPONSE_LOGIN_PARA_TOKEN));
                            editor.commit();

                            Toast.makeText(getBaseContext(),"LoginActivity success",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);

                            finish();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private void resetPassord() {
        dalReset = new Dialog(LoginActivity.this);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        dalReset.setTitle(R.string.dialog_reset_title);
        dalReset.setContentView(R.layout.dialog_reset_password);
        btnDalCancel = (Button) dalReset.findViewById(R.id.btn_cancel);
        btnDalCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dalReset.dismiss();
            }
        });

        btnDalContinue = (Button)dalReset.findViewById(R.id.btn_continue);
        edtDalEmail = (EditText) dalReset.findViewById(R.id.edt_email);
        btnDalContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = edtDalEmail.getText().toString().trim();
                if(isValidEmail(email)) {
                    mSocket.emit(Constant.SERVER_REQUEST_RESET_PASSWORD, email);
                    mSocket.on(Constant.SERVER_RESPONSE_RESET_PASSWORD, new Emitter.Listener() {
                        @Override
                        public void call(final Object... args) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try{
                                        final JSONObject jsonObj = ((JSONObject) args[0]);
                                        String mess = jsonObj.getJSONObject(Constant.SERVER_RESPONSE_DATA).getString(Constant.SERVER_RESPONSE_MESS);
                                        Toast.makeText(LoginActivity.this, mess, Toast.LENGTH_LONG).show();
                                        if(jsonObj.getBoolean(Constant.SERVER_RESPONSE_RESULT)) {
                                            changePassword();
                                         }
                                    } catch (JSONException e) {
                                        Log.e(TAG, e.getMessage());
                                    }
                                }
                            });
                        }
                    });
                } else {
                    Toast.makeText(LoginActivity.this, R.string.mess_invalid_email, Toast.LENGTH_LONG).show();
                }
            }
        });

        dalReset.show();
        if(dalReset.getWindow() != null) {
            dalReset.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
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
                if(pass.equals(edtDalRePass.getText().toString().trim())) {
                    mSocket.emit(Constant.SERVER_REQUEST_CHANGE_PASSWORD, email, code, pass);
                    //
                    mSocket.on(Constant.SERVER_RESPONSE_CHANGE_PASSWORD, new Emitter.Listener() {
                        @Override
                        public void call(final Object... args) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try{
                                        JSONObject resObj = ((JSONObject) args[0]);
                                        String mess = resObj.getJSONObject(Constant.SERVER_RESPONSE_DATA).getString(Constant.SERVER_RESPONSE_MESS);
                                        Toast.makeText(LoginActivity.this, mess, Toast.LENGTH_LONG).show();
                                        if(resObj.getBoolean(Constant.SERVER_RESPONSE_RESULT)) {
                                            dalReset.dismiss();
                                        }
                                    } catch (JSONException e) {
                                        Log.e(TAG, e.getMessage());
                                    }
                                }
                            });
                        }
                    });
                } else {
                    Toast.makeText(LoginActivity.this, R.string.mess_password_mismatch, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private boolean isValidEmail(String email) {
        return !(email.indexOf("@") < 1
                || email.lastIndexOf(".") < email.indexOf("@") + 2
                || email.lastIndexOf(".") + 2 >= email.length());
    }
}
