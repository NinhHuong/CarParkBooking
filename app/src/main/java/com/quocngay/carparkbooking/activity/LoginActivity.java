package com.quocngay.carparkbooking.activity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.quocngay.carparkbooking.model.Principal;
import com.quocngay.carparkbooking.other.Constant;
import com.quocngay.carparkbooking.other.SocketIOClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_LOGIN_REGISTER = 5;
    SharedPreferences mSharedPref;
    String serverToken;
    private EditText edtEmail, edtPass;
    private CheckBox cbRemember;
    private String mSalt = "";
    private String email;
    private String password;
    private Boolean rememmber;
    private Dialog dalInputCode;
    private Button btnDalContinue;
    private TextView tvDalCancel;
    private EditText edCode1, edCode2, edCode3, edCode4;
    private String code;
    private Emitter.Listener onResponseCheckToken = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject jsonObject = (JSONObject) args[0];
                    try {
                        Boolean result = jsonObject.getBoolean(Constant.RESULT);
                        if (result) {
                            Intent intent = new Intent(LoginActivity.this, MapActivity.class);
                            startActivity(intent);
                            finish();
                            SocketIOClient.client.mSocket.off();
                            return;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };
    private Emitter.Listener onResponseCompareCode = new Emitter.Listener() {

        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    try {
                        Boolean result = data.getBoolean(Constant.RESULT);
                        if (result) {
                            dalInputCode.dismiss();
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.login_successfull), Toast.LENGTH_SHORT).show();
                            SharedPreferences.Editor editor = mSharedPref.edit();
                            editor.putString(Constant.APP_PREF_TOKEN, serverToken);
                            editor.putString(Constant.APP_PREF_ID, serverToken);
                            editor.putBoolean(Constant.APP_PREF_REMEMBER, cbRemember.isChecked());
                            editor.apply();
                            Intent intent = new Intent(LoginActivity.this, MapActivity.class);
                            startActivity(intent);
                            finish();
                            SocketIOClient.client.mSocket.off();
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.error_code, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };
    private String userId;
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
                        String isVerify = data.getString(Constant.IS_VERIFY);
                        serverToken = data.getString(Constant.SERVER_RESPONSE_LOGIN_PARA_TOKEN);
                        userId = data.getString(Constant.SERVER_RESPONSE_LOGIN_PARA_ID);
                        Log.w("Account account",data.toString());
                        Log.w("User id",userId);
                        if (!isEmailCorrect) {
                            Toast.makeText(getBaseContext(), getResources().getString(R.string.server_error_email), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (!isPasswordCorrect) {
                            Toast.makeText(getBaseContext(), getResources().getString(R.string.server_error_password), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (isVerify.equals("1")) {
                            Principal principal = new Principal(getApplicationContext());
                            principal.setId(userId);
                            principal.setToken(serverToken);
                            principal.setRemmember(cbRemember.isChecked());
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.login_successfull), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, MapActivity.class);
                            startActivity(intent);
                            finish();
                            SocketIOClient.client.mSocket.off();
                        } else {
                            initCodeDialog();

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private void initCodeDialog() {
        dalInputCode = new Dialog(this);
        dalInputCode.setTitle(R.string.dialog_verify);
        dalInputCode.setContentView(R.layout.dialog_input_code);
        btnDalContinue = (Button) dalInputCode.findViewById(R.id.btn_continue);
        tvDalCancel = (TextView) dalInputCode.findViewById(R.id.tv_cancel);
        tvDalCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dalInputCode.dismiss();
            }
        });
        edCode1 = (EditText) dalInputCode.findViewById(R.id.edt_code1);
        edCode2 = (EditText) dalInputCode.findViewById(R.id.edt_code2);
        edCode3 = (EditText) dalInputCode.findViewById(R.id.edt_code3);
        edCode4 = (EditText) dalInputCode.findViewById(R.id.edt_code4);

        btnDalContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!edCode1.getText().toString().isEmpty() &&
                        !edCode2.getText().toString().isEmpty() &&
                        !edCode3.getText().toString().isEmpty() &&
                        !edCode4.getText().toString().isEmpty()) {
                    code = edCode1.getText().toString()
                            + edCode2.getText().toString()
                            + edCode3.getText().toString()
                            + edCode4.getText().toString();
                    SocketIOClient.client.mSocket.emit(Constant.REQUEST_COMPARE_CODE, email, code);
                    SocketIOClient.client.mSocket.on(Constant.RESPONSE_COMPARE_CODE, onResponseCompareCode);
                } else {
                    Toast.makeText(getApplicationContext(), R.string.error_code, Toast.LENGTH_SHORT).show();
                }
            }
        });

        edCode1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                edCode1.clearFocus();
                if (count == 1) {
                    edCode2.requestFocusFromTouch();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edCode2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                edCode2.clearFocus();
                if (count == 1) {
                    edCode3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        edCode3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                edCode3.clearFocus();
                if (count == 1) {
                    edCode4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        edCode4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count == 1) {
                    edCode4.clearFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        dalInputCode.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new SocketIOClient();
        mSharedPref = getSharedPreferences(Constant.APP_PREF, MODE_PRIVATE);
        Principal principal = new Principal(getApplicationContext());
        String accountToken = principal.getToken();
        Boolean rememberStatus = principal.getRemmember();
        if (rememberStatus && !accountToken.isEmpty()) {
            SocketIOClient.client.mSocket.emit(Constant.REQUEST_CHECK_TOKEN, accountToken);
            SocketIOClient.client.mSocket.on(Constant.RESPONSE_CHECK_TOKEN, onResponseCheckToken);
        } else {
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
        }

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

                SocketIOClient.client.mSocket.emit(Constant.REQUEST_CHECK_EMAIL_PASSWORD, email, sha512Password(password));
                SocketIOClient.client.mSocket.on(Constant.RESPONSE_RESULT_LOGIN, onNewMessageResultLogin);
                break;
            case R.id.tvRegister:
                startActivityForResult(new Intent(LoginActivity.this, RegisterActivity.class), REQUEST_LOGIN_REGISTER);
                break;
            case R.id.tvForgotPass:
                startActivity(new Intent(LoginActivity.this, ForgetPasswordActivity.class));
                break;
//                resetPassord();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_LOGIN_REGISTER) {
            if (resultCode == RESULT_OK) {
                edtEmail.setText(data.getStringExtra(Constant.EMAIL));
                edtPass.setText(data.getStringExtra(Constant.PASSWORD));
            }
        }
    }
}
