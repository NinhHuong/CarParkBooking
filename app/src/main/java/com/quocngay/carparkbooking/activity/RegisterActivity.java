package com.quocngay.carparkbooking.activity;

import android.content.Intent;
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
import com.quocngay.carparkbooking.model.Principal;
import com.quocngay.carparkbooking.other.Constant;
import com.quocngay.carparkbooking.other.SocketIOClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class RegisterActivity extends AppCompatActivity {

    String email, password, hashPassword;
    private String roleID;
    private Boolean isLogin;
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
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.register_successfull), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            intent.putExtra(Constant.EMAIL, email);
                            intent.putExtra(Constant.PASSWORD, password);
                            setResult(RESULT_OK, intent);
                            SocketIOClient.client.mSocket.off(Constant.RESPONSE_CREATE_ACCOUNT);
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

        Principal p = new Principal(getApplicationContext());
        roleID = p.getRole();
        isLogin = p.getIsLogin();

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

        if (roleID.matches("\\d+")) {
            int currentUser = Integer.parseInt(roleID);
            int roleNewUser = Constant.ROLE_USER_VALUE;
            if (isLogin)
                switch (currentUser) {
                    case Constant.ROLE_SUPER_ADMIN_VALUE:
                        roleNewUser = Constant.ROLE_ADMIN_VALUE;
                        break;
                    case Constant.ROLE_ADMIN_VALUE:
                        roleNewUser = Constant.ROLE_SECURITY_VALUE;
                        String id = new Principal(getApplicationContext()).getId();
                        SocketIOClient.client.mSocket.emit(Constant.REQUEST_CREATE_ACCOUNT_SECURITY, email, hashPassword, id);
                        SocketIOClient.client.mSocket.on(Constant.RESPONSE_CREATE_ACCOUNT_SECURITY, onNewMessageResultRegistNewAccount);
                        break;
                    default:
                        roleNewUser = Constant.ROLE_USER_VALUE;
                        SocketIOClient.client.mSocket.emit(Constant.REQUEST_CREATE_ACCOUNT, email, hashPassword, roleNewUser);
                        SocketIOClient.client.mSocket.on(Constant.RESPONSE_CREATE_ACCOUNT, onNewMessageResultRegistNewAccount);
                        break;
                }
        }
    }

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
