package com.quocngay.carparkbooking.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.quocngay.carparkbooking.R;
import com.quocngay.carparkbooking.other.Constant;
import com.quocngay.carparkbooking.other.SocketIOClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText edtEmail, edtPass;
    private CheckBox cbRemember;
    private String mSalt = "";
    private String email;
    private String password;

    private Emitter.Listener onNewMessageResultLogin = new Emitter.Listener() {
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
                            Toast.makeText(getBaseContext(), getResources().getString(R.string.server_error_email), Toast.LENGTH_SHORT).show();
                        else if (!isPasswordCorrect)
                            Toast.makeText(getBaseContext(), getResources().getString(R.string.server_error_password), Toast.LENGTH_SHORT).show();
                        else {

                            SharedPreferences sharedPref = getApplication().
                                    getSharedPreferences(Constant.APP_PREF, MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString(Constant.APP_PREF_TOKEN, data.getString(Constant.SERVER_RESPONSE_LOGIN_PARA_TOKEN));
                            editor.apply();

                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.login_successfull), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, MapActivity.class);
                            startActivity(intent);
                            finish();
                            SocketIOClient.client.mSocket.off();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private Emitter.Listener onNewMessageGetSalt = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mSalt = String.valueOf(args[0]);
                    Log.i("Salt", mSalt);
                    JSONObject j = new JSONObject();
                    if (mSalt.isEmpty()) {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.server_error_email), Toast.LENGTH_LONG).show();
                    } else {
                        String hashPassword = sha512Password(password, mSalt);
                        try {
                            j.put(Constant.EMAIL, email);
                            j.put(Constant.PASSWORD, hashPassword);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        SocketIOClient.client.mSocket.off();
                        SocketIOClient.client.mSocket.emit(Constant.REQUEST_CHECK_EMAIL_PASSWORD, j.toString());
                        SocketIOClient.client.mSocket.on(Constant.RESPONSE_RESULT_LOGIN, onNewMessageResultLogin);
                    }
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_login);

        edtEmail = (EditText) findViewById(R.id.edtEmail);
        edtPass = (EditText) findViewById(R.id.edtPassword);
        Button btnLogin = (Button) findViewById(R.id.btnLogin);
        TextView tvRegister = (TextView) findViewById(R.id.tvRegister);
        TextView tvForgotPassword = (TextView) findViewById(R.id.tvForgotPass);
        cbRemember = (CheckBox) findViewById(R.id.cbRemember);

        btnLogin.setOnClickListener(this);
        tvRegister.setOnClickListener(this);
        tvForgotPassword.setOnClickListener(this);

        new SocketIOClient();
    }

    public String sha512Password(String passwordToHash, String salt) {
        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(salt.getBytes("UTF-8"));
            byte[] bytes = md.digest(passwordToHash.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }
            generatedPassword = sb.toString();
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return generatedPassword;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLogin:
                email = edtEmail.getText().toString();
                password = edtPass.getText().toString();
                if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_email), Toast.LENGTH_SHORT).show();
                    break;
                }
                if (password.isEmpty() || password.length() < Constant.PASSWORD_LENGTH) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_password, Constant.PASSWORD_LENGTH), Toast.LENGTH_SHORT).show();
                    break;
                }
                JSONObject requestJson = new JSONObject();
                try {
                    requestJson.put(Constant.EMAIL, email);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                SocketIOClient.client.mSocket.emit(Constant.REQUEST_GET_SALT, email);
                SocketIOClient.client.mSocket.on(Constant.RESPONSE_GET_SALT, onNewMessageGetSalt);
                if (cbRemember.isChecked()) {
                    //TODO: Remember me function
                }
                break;
            case R.id.tvRegister:
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                break;
            case R.id.tvForgotPass:
                resetPassord();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SocketIOClient.client.mSocket.disconnect();
    }

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

    public static boolean isValidEmail(String email) {
        return !(email.indexOf("@") < 1
                || email.lastIndexOf(".") < email.indexOf("@") + 2
                || email.lastIndexOf(".") + 2 >= email.length());
    }

    public static boolean isValidPassword(String password) {
//        return password.matches("([a-z].*[A-Z])|([A-Z].*[a-z])") && password.length() > 4
//                && password.matches("[0-9]") && password.matches(".[!,@,#,$,%,^,&,*,?,_,~]");
        return password.length() > 4;
    }
}
