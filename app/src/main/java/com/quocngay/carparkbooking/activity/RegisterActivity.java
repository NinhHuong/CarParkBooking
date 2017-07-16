package com.quocngay.carparkbooking.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
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
import java.security.SecureRandom;
import java.util.Random;

public class RegisterActivity extends AppCompatActivity {

    public static final int ROLE_USER_VALUE = 4;
    public static final String ROLE_ID = "roleID";

    private EditText edtEmail, edtPass, edtRetypePass;
    private Button btnRegist;
    private Toolbar toolbar;

    private Socket mSocket;
    private Emitter.Listener onNewMessage_ResultRegistNewAccount = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    Log.i("Data", data.toString());
                    try {
                        boolean res = data.getBoolean("res");
                        String message = data.getString("response");

                        Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();

                        if (res)
                            finish();
//                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
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
        setContentView(R.layout.activity_register);

        toolbar = (Toolbar) findViewById(R.id.toolbar_regist);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        edtEmail = (EditText) findViewById(R.id.edtEmailRegist);
        edtPass = (EditText) findViewById(R.id.edtPassRegist);
        edtRetypePass = (EditText) findViewById(R.id.edtRePassRegist);
        btnRegist = (Button) findViewById(R.id.btnRegist);

        btnRegist.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                registNewAccount();
            }
        });
        mSocket.connect();
        // runFadeInAnimation();

    }

    private void runFadeInAnimation() {
        Animation a = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        a.reset();
        RelativeLayout ll = (RelativeLayout) findViewById(R.id.form_register);
        ll.clearAnimation();
        ll.startAnimation(a);
    }

    private void registNewAccount() {
        String email = edtEmail.getText().toString();
        String password = edtPass.getText().toString();
        if (password.compareTo(edtRetypePass.getText().toString()) != 0) {
            Toast.makeText(getBaseContext(), "Password and re-type password not the same!!!" +
                    " Try again", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!mSocket.connected())
            mSocket.connect();
        String salt = createSalt();
        String hashPassword = sha512Password(password, salt);
        JSONObject j = new JSONObject();
        try {
            j.put("email", email);
            j.put("password", hashPassword);
            j.put("salt", salt);
            j.put(ROLE_ID, ROLE_USER_VALUE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mSocket.emit("request_create_account", j.toString());
        mSocket.on("response_create_account", onNewMessage_ResultRegistNewAccount);
    }

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
    protected void onDestroy() {
        super.onDestroy();
        mSocket.disconnect();
        mSocket.off();
    }
}
