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
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.quocngay.carparkbooking.R;
import com.quocngay.carparkbooking.other.Constant;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int PASSWORD_LENGTH = 6;

    EditText edtEmail, edtPass;
    Button btnLogin;
    TextView tvRegister, tvForgotPassword;
    private Socket mSocket;
    private CheckBox cbRemember;
    private String mSalt = "";
    private String email;
    private String password;

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
                            Toast.makeText(getBaseContext(), getResources().getString(R.string.server_error_email), Toast.LENGTH_SHORT).show();
                        else if (!isPasswordCorrect)
                            Toast.makeText(getBaseContext(), getResources().getString(R.string.server_error_password), Toast.LENGTH_SHORT).show();
                        else {

                            SharedPreferences sharedPref = getApplication().
                                    getSharedPreferences(Constant.APP_PREF, MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString(Constant.APP_PREF_TOKEN, data.getString(Constant.SERVER_RESPONSE_LOGIN_PARA_TOKEN));
                            editor.apply();

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

    private Emitter.Listener onNewMessage_GetSalt = new Emitter.Listener() {
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
                            j.put("email", email);
                            j.put("password", hashPassword);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        mSocket.emit("check_email_and_password", j.toString());
                        mSocket.on("result_login", onNewMessage_ResultLogin);
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
        cbRemember = (CheckBox) findViewById(R.id.cbRemember);

        btnLogin.setOnClickListener(this);
        tvRegister.setOnClickListener(this);
        tvForgotPassword.setOnClickListener(this);

        mSocket.connect();
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

    private void checkValid() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLogin:
                email = edtEmail.getText().toString();
                password = edtPass.getText().toString();
                if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.login_error_email), Toast.LENGTH_LONG).show();
                    break;
                }
                if (password.isEmpty() || password.length() < PASSWORD_LENGTH) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.login_error_password, PASSWORD_LENGTH), Toast.LENGTH_LONG).show();
                    break;
                }
                mSocket.emit("request_get_salt", email);
                mSocket.on("response_get_salt", onNewMessage_GetSalt);
                if (cbRemember.isChecked()) {

                }
                break;
            case R.id.tvRegister:

                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                break;
            case R.id.tvForgotPass:
                break;
        }
    }

    //TODO: Fix socket connect bug
    @Override
    protected void onPause() {
        super.onPause();
        mSocket.disconnect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSocket.connect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSocket.disconnect();
        mSocket.off();
    }
}
