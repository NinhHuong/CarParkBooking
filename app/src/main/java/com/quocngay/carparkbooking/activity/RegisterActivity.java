package com.quocngay.carparkbooking.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import java.security.SecureRandom;
import java.util.Random;

public class RegisterActivity extends AppCompatActivity {

    private EditText edtEmail, edtPass, edtRetypePass;

    private Emitter.Listener onNewMessageResultRegistNewAccount = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    Log.i("Data", data.toString());
                    try {
                        boolean res = data.getBoolean("result");
                        String message = data.getJSONObject("data").getString("mess");
                        Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
                        if (res) {
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                            SocketIOClient.client.mSocket.off();
                            finish();
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
        setContentView(R.layout.activity_register);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_regist);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        edtEmail = (EditText) findViewById(R.id.edtEmailRegist);
        edtPass = (EditText) findViewById(R.id.edtPassRegist);
        edtRetypePass = (EditText) findViewById(R.id.edtRePassRegist);
        Button btnRegist = (Button) findViewById(R.id.btnRegist);

        btnRegist.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                registNewAccount();
            }
        });
    }

    private void registNewAccount() {
        String email = edtEmail.getText().toString();
        String password = edtPass.getText().toString();
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_email), Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.isEmpty() || password.length() < Constant.PASSWORD_LENGTH) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_password, Constant.PASSWORD_LENGTH), Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.compareTo(edtRetypePass.getText().toString()) != 0) {
            Toast.makeText(getBaseContext(), getResources().getString(R.string.error_repassword), Toast.LENGTH_SHORT).show();
            return;
        }
        String salt = createSalt();
        String hashPassword = sha512Password(password, salt);
        JSONObject j = new JSONObject();
        try {
            j.put(Constant.EMAIL, email);
            j.put(Constant.PASSWORD, hashPassword);
            j.put(Constant.SALT, salt);
            j.put(Constant.ROLE_ID, Constant.ROLE_USER_VALUE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SocketIOClient.client.mSocket.emit(Constant.REQUEST_CREATE_ACCOUNT, j.toString());
        SocketIOClient.client.mSocket.on(Constant.RESPONSE_CREATE_ACCOUNT, onNewMessageResultRegistNewAccount);
    }

    //Salt random code for hash pass
    private String createSalt() {
        final Random r = new SecureRandom();
        byte[] salt = new byte[20];
        r.nextBytes(salt);
        StringBuilder sb = new StringBuilder();
        for (byte aByte : salt) {
            sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }

    //create hash pass from salt code and original pass
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
}
