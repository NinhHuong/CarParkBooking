package com.quocngay.carparkbooking.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.preference.Preference;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.quocngay.carparkbooking.R;
import com.quocngay.carparkbooking.other.Constant;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

public class Login extends AppCompatActivity implements View.OnClickListener {
    EditText edtEmail, edtPass;
    Button btnLogin, btnRegister, btnForgotPassword;
    CheckBox chbRemember;
    private Socket mSocket;
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
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnForgotPassword = (Button) findViewById(R.id.btnForgotPass);
        chbRemember = (CheckBox) findViewById(R.id.checkBox);

        btnLogin.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
        btnForgotPassword.setOnClickListener(this);

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
            case R.id.btnRegister:
                startActivity(new Intent(Login.this, Register.class));
                break;
            case R.id.btnForgotPass:
                break;
            default:
                return;
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
                        boolean isEmailCorrect =data.getBoolean("email");
                        boolean isPasswordCorrect =data.getBoolean("password");

                        if(!isEmailCorrect)
                            Toast.makeText(getBaseContext(),"Wrong email, try again",Toast.LENGTH_SHORT).show();
                        else if(!isPasswordCorrect)
                            Toast.makeText(getBaseContext(),"Wrong password, try again",Toast.LENGTH_SHORT).show();
                        else {
                            boolean remember = chbRemember.isChecked();
                            if(remember){
                                SharedPreferences sharedPref = getApplication().
                                        getSharedPreferences(Constant.APP_PREF,MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putString(Constant.APP_PREF_TOKEN, data.getString("Token"));
                                editor.commit();
                            }

                            Toast.makeText(getBaseContext(),"Login success",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Login.this, MainActivity.class);
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
}
