package com.quocngay.carparkbooking.activity;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.quocngay.carparkbooking.R;
import com.quocngay.carparkbooking.model.LocalData;
import com.quocngay.carparkbooking.other.Constant;
import com.quocngay.carparkbooking.other.SocketIOClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class RegisterForOtherActivity extends AppCompatActivity {
    public static String REGISTER_EXTRA = "isAdmin";

    String email, password, hashPassword, roleNewUser ;
    boolean isAdmin;

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
                        boolean res = data.getBoolean(Constant.RESULT);
                        if (res) {
                            Toast.makeText(getApplicationContext(),
                                    getResources().getString(R.string.register_successfull),
                                    Toast.LENGTH_SHORT).show();
                            SocketIOClient.client.mSocket.off(Constant.RESPONSE_CREATE_ACCOUNT_SECURITY);
                            finish();
                        } else if (data.getString(Constant.MESSAGE).equals("email_registered")) {
                            Toast.makeText(getApplicationContext(), getResources().
                                            getString(R.string.error_server_email_registered),
                                    Toast.LENGTH_SHORT).show();

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    LocalData p;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_for_security);

        isAdmin = getIntent().getBooleanExtra(REGISTER_EXTRA,false);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_regist);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        p = new LocalData(getApplicationContext());
        edtEmail = (EditText) findViewById(R.id.edtAdminEmailRegist);
        edtPass = (EditText) findViewById(R.id.edtAdminPassRegist);
        edtRetypePass = (EditText) findViewById(R.id.edtAdminRePassRegist);
        Button btnRegist = (Button) findViewById(R.id.btnAdminRegist);

        btnRegist.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                registNewAccount();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void registNewAccount() {
        email = edtEmail.getText().toString();
        password = edtPass.getText().toString();
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
        hashPassword = sha512Password(password);

        String idAccountAdmin = p.getId();

        if(!isAdmin) {
            SocketIOClient.client.mSocket.emit(Constant.REQUEST_CREATE_ACCOUNT_SECURITY, email, hashPassword, idAccountAdmin);
            SocketIOClient.client.mSocket.on(Constant.RESPONSE_CREATE_ACCOUNT_SECURITY, onNewMessageResultRegistNewAccount);
        }else{
            SocketIOClient.client.mSocket.emit(Constant.REQUEST_CREATE_ACCOUNT_ADMIN, email, hashPassword);
            SocketIOClient.client.mSocket.on(Constant.RESPONSE_CREATE_ACCOUNT_ADMIN, onNewMessageResultRegistNewAccountAdmin);
        }
    }

    private Emitter.Listener onNewMessageResultRegistNewAccountAdmin = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    Log.i("Data", data.toString());
                    try {
                        boolean res = data.getBoolean(Constant.RESULT);
                        if (res) {
                            Toast.makeText(getApplicationContext(),
                                    getResources().getString(R.string.register_successfull),
                                    Toast.LENGTH_SHORT).show();
                            SocketIOClient.client.mSocket.off(Constant.RESPONSE_CREATE_ACCOUNT_ADMIN);

                            String accountID = data.getJSONObject(Constant.DATA).getString(Constant.ACCOUNT_ID);
                            Intent addGarage = new Intent(RegisterForOtherActivity.this, AddGarageActivity.class);
                            addGarage.putExtra(Constant.ACCOUNT_ID,accountID);
                            startActivity(addGarage);

                            finish();
                        } else if (data.getString(Constant.MESSAGE).equals("email_registered")) {
                            Toast.makeText(getApplicationContext(), getResources().
                                            getString(R.string.error_server_email_registered),
                                    Toast.LENGTH_SHORT).show();

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    //create hash pass from salt code and original pass
    public String sha512Password(String passwordToHash) {
        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
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
