package com.quocngay.carparkbooking.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

public class Register extends AppCompatActivity {
    EditText edtEmail, edtPass,edtRetypePass;
    Button btnRegist;

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
        setContentView(R.layout.activity_register);

        edtEmail = (EditText)findViewById(R.id.edtEmail);
        edtPass = (EditText)findViewById(R.id.edtPass);
        edtRetypePass = (EditText)findViewById(R.id.edtRePass);
        btnRegist = (Button) findViewById(R.id.btnRegist);

        btnRegist.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                RegistNewAccount();
            }
        });
        mSocket.connect();
        mSocket.on("ResultRegistNewAccount",onNewMessage_ResultRegistNewAccount);

    }

    void RegistNewAccount(){
        String email = edtEmail.getText().toString();
        String password = edtPass.getText().toString();
        if(password.compareTo(edtRetypePass.getText().toString()) != 0) {
            Toast.makeText(getBaseContext(),"Password and re-type password not the same!!!" +
                    " Try again",Toast.LENGTH_SHORT).show();
            return;
        }

        if(!mSocket.connected())
            mSocket.connect();

        JSONObject j = new JSONObject();
        try {
            j.put("Email",email);
            j.put("Password",password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mSocket.emit("RegistNewAccount",j.toString());
    }

    private Emitter.Listener onNewMessage_ResultRegistNewAccount =  new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    Log.i("Data",data.toString());
                    try {
                        boolean res =data.getBoolean("res");
                        String message = data.getString("response");

                        Toast.makeText(getBaseContext(),message ,Toast.LENGTH_SHORT).show();

                        if(res)
                            finish();
//                            startActivity(new Intent(Register.this, LoginActivity.class));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };
}
