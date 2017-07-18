package com.quocngay.carparkbooking.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

public class NewPasswordActivity extends AppCompatActivity {

    private EditText edNewPass, edReNewPass;
    private Button btnNewPass;
    private Emitter.Listener onResponseChangePassword = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject jsonObject = (JSONObject) args[0];
                    try {
                        Boolean result = jsonObject.getBoolean(Constant.RESULT);
                        if (result) {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.forget_reset_success), Toast.LENGTH_SHORT).show();
                            finish();
                            SocketIOClient.client.mSocket.off();
                        } else {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_network), Toast.LENGTH_SHORT).show();
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
        setContentView(R.layout.activity_new_password);

        edNewPass = (EditText) findViewById(R.id.edt_newpass);
        edReNewPass = (EditText) findViewById(R.id.edt_renewpass);
        btnNewPass = (Button) findViewById(R.id.btn_newpass);
        final String email = getIntent().getStringExtra(Constant.EMAIL);
        btnNewPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newPass = edNewPass.getText().toString();
                String reNewPass = edReNewPass.getText().toString();
                if (newPass.isEmpty() || newPass.length() < Constant.PASSWORD_LENGTH) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_password, Constant.PASSWORD_LENGTH), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!reNewPass.equals(newPass)) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_repassword), Toast.LENGTH_SHORT).show();
                    return;
                }
                SocketIOClient.client.mSocket.emit(Constant.SERVER_REQUEST_CHANGE_PASSWORD, email, sha512Password(newPass));
                SocketIOClient.client.mSocket.on(Constant.SERVER_RESPONSE_CHANGE_PASSWORD, onResponseChangePassword);

            }
        });
    }

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
            Log.d("hashpasss", generatedPassword);
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return generatedPassword;
    }
}
