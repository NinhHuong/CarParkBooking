package com.quocngay.carparkbooking.activity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.github.nkzawa.emitter.Emitter;
import com.google.gson.Gson;
import com.quocngay.carparkbooking.R;
import com.quocngay.carparkbooking.fragment.DatePickerFragment;
import com.quocngay.carparkbooking.model.LocalData;
import com.quocngay.carparkbooking.model.UserModel;
import com.quocngay.carparkbooking.other.Constant;
import com.quocngay.carparkbooking.other.SocketIOClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ProfileActivity extends AppCompatActivity {

    private static final int ID_FIRSTNAME = 0;
    private static final int ID_LASTNAME = 1;
    private static final int ID_ADDRESS = 2;
    private static final int ID_PHONE = 3;
    private static final int ID_DOB = 4;
    TextView tvName, tvEmail;
    EditText edtFirstName, edtLastName;
    LocalData localData;
    Boolean isView = true;
    ViewSwitcher swName;
    LayoutInflater layoutInflater;
    private UserModel mUserModel;
    ViewSwitcher switcherName, switcherContent;
    LinearLayout containerProfileContent, containerProfileContentEdit;
    FloatingActionButton fab, fabCancel;
    EditText edtAddress, edtPhone, edtDob;
    TextView tvAddress, tvPhone, tvDob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initToolbar();
        initElements();

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fabCancel = (FloatingActionButton) findViewById(R.id.fab_cancel);
        fabCancel.hide();
        fabCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isView) {
                    //change to edit state
                    isView = true;
                    changeState(isView);
                    fabCancel.hide();
                    fab.setImageResource(R.drawable.ic_mode_edit_white);
                }
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isView) {
                    //change to edit state
                    isView = false;
                    changeState(isView);
                    fabCancel.show();
                    fab.setImageResource(R.drawable.ic_check_white);
                } else {
                    //change to view state
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(ProfileActivity.this);
                    alertDialog.setTitle(getResources().getString(R.string.dialog_save_profile_title));
                    alertDialog.setMessage(R.string.dialog_save_profile_message)
                            .setPositiveButton(R.string.fire, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    saveUserDetail();
                                }
                            })
                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });
                    // Create the AlertDialog object and return it
                    alertDialog.create().show();

                }
            }
        });
        getUserDetail();
    }

    private TextView addToProfileContainer(@NonNull LinearLayout containerView, int iconId,
                                           String defaultValue) {
        View profileContent = layoutInflater.inflate(R.layout.item_profile, null);
        ImageView profileIcon = (ImageView) profileContent.findViewById(R.id.iv_profile_icon);
        TextView profileText = (TextView) profileContent.findViewById(R.id.tv_profile_text);

        profileIcon.setImageResource(iconId);
        profileText.setText(defaultValue);
        containerView.addView(profileContent);
        return profileText;
    }

    private EditText addToProfileEditContainer(@NonNull LinearLayout containerView,
                                               int resId, int inputType, String defaultHint,
                                               String defaultValue) {
        View profileContent = layoutInflater.inflate(R.layout.item_profile_edit, null);
        ImageView profileIcon = (ImageView) profileContent.findViewById(R.id.iv_profile_icon);
        EditText edtText = (EditText) profileContent.findViewById(R.id.edt_profile_edit);
        edtText.setMaxLines(1);
        edtText.setInputType(inputType);
        profileIcon.setImageResource(resId);
        edtText.setHint(defaultHint);
        edtText.setText(defaultValue);
        containerView.addView(profileContent);
        return edtText;
    }

    private void initElements() {
        localData = new LocalData(getApplicationContext());

        switcherName = (ViewSwitcher) findViewById(R.id.sw_profile_name);
        switcherContent = (ViewSwitcher) findViewById(R.id.sw_profile_content);
        tvEmail = (TextView) findViewById(R.id.tv_profile_email);
        tvName = (TextView) switcherName.findViewById(R.id.tv_profile_name);
        edtFirstName = (EditText) switcherName.findViewById(R.id.edt_profile_first_name);
        edtLastName = (EditText) switcherName.findViewById(R.id.edt_profile_last_name);
        tvEmail.setText(localData.getEmail());

        layoutInflater = LayoutInflater.from(this);

    }

    private void changeState(Boolean isView) {
        if (!isView) {
            switcherName.showNext();
            switcherContent.showNext();
        } else {
            switcherName.showPrevious();
            switcherContent.showPrevious();
        }
    }

    private void saveUserDetail() {
        UserModel updateModel = new UserModel();
        updateModel.setFirstName(edtFirstName.getText().toString().trim());
        updateModel.setLastName(edtLastName.getText().toString().trim());
        updateModel.setAddress(edtAddress.getText().toString().trim());
        updateModel.setPhone(edtPhone.getText().toString().trim());
        updateModel.setDateOfBirth(edtDob.getText().toString().trim());

        Emitter.Listener onResponseEditUser = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject jsonObject = (JSONObject) args[0];
                        try {
                            if (jsonObject.getBoolean(Constant.RESULT)) {
                                Toast.makeText(getApplicationContext(),
                                        getResources().getString(R.string.profile_save_success),
                                        Toast.LENGTH_SHORT).show();
                                isView = true;
                                fab.setImageResource(R.drawable.ic_mode_edit_white);
                                fabCancel.hide();
                                changeState(isView);
                                getUserDetail();
                            } else {
                                Log.e(getClass().getName(), "Error: " +
                                        jsonObject.getString(Constant.MESSAGE));
                            }
                            SocketIOClient.client.mSocket.off(Constant.RESPONSE_EDIT_USER_BY_ID);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        };

        SocketIOClient.client.mSocket.emit(Constant.REQUEST_EDIT_USER_BY_ID, mUserModel.getId(),
                updateModel.getFirstName(), updateModel.getLastName(),
                updateModel.getDateOfBirth(), updateModel.getPhone(), updateModel.getAddress());
        SocketIOClient.client.mSocket.on(Constant.RESPONSE_EDIT_USER_BY_ID, onResponseEditUser);

    }

    private void getUserDetail() {
        Emitter.Listener onResponseFindUserByAccId = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject jsonObject = (JSONObject) args[0];
                        Gson gson = new Gson();
                        try {
                            if (jsonObject.getBoolean(Constant.RESULT)) {
                                mUserModel = gson.fromJson(jsonObject.getJSONArray(Constant.DATA)
                                        .getJSONObject(0).toString(), UserModel.class);
                            } else {
                                Log.e(getClass().getName(), jsonObject.getString(Constant.MESSAGE));
                            }
                            setElementsContent();

                            SocketIOClient.client.mSocket.off(
                                    Constant.RESPONSE_FIND_USER_BY_ACCOUNT_ID);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        };

        SocketIOClient.client.mSocket.emit(Constant.REQUEST_FIND_USER_BY_ACCOUNT_ID,
                localData.getId());
        SocketIOClient.client.mSocket.on(Constant.RESPONSE_FIND_USER_BY_ACCOUNT_ID,
                onResponseFindUserByAccId);
    }

    private void setElementsContent() {
        tvEmail.setText(localData.getEmail().trim());
        tvName.setText(mUserModel.getFullName());
        edtFirstName.setText(mUserModel.getFirstName());
        edtLastName.setText(mUserModel.getLastName());

        containerProfileContent =
                (LinearLayout) findViewById(R.id.container_profile_content);
        containerProfileContent.removeAllViews();
        tvAddress = addToProfileContainer(containerProfileContent, R.drawable.ic_home_black_24dp,
                mUserModel.getAddress());
        tvPhone = addToProfileContainer(containerProfileContent, R.drawable.ic_local_phone_black,
                mUserModel.getPhone());
        tvDob = addToProfileContainer(containerProfileContent, R.drawable.ic_today_black_24dp,
                mUserModel.getDateOfBirth());

        containerProfileContentEdit =
                (LinearLayout) findViewById(R.id.container_profile_content_edit);
        containerProfileContentEdit.removeAllViews();
        edtAddress = addToProfileEditContainer(containerProfileContentEdit,
                R.drawable.ic_home_black_24dp, InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS,
                getResources().getString(R.string.address),
                mUserModel.getAddress());
        edtPhone = addToProfileEditContainer(containerProfileContentEdit,
                R.drawable.ic_local_phone_black, InputType.TYPE_CLASS_PHONE, getResources().getString(R.string.phone_number),
                mUserModel.getPhone());
        edtDob = addToProfileEditContainer(containerProfileContentEdit,
                R.drawable.ic_today_black_24dp, InputType.TYPE_CLASS_DATETIME, getResources().getString(R.string.day_of_birth),
                mUserModel.getDateOfBirth());
        showDatePickerDialog(edtDob);

    }

    public void showDatePickerDialog(final EditText editText) {
        final Calendar calendar = Calendar.getInstance();
        if(!editText.getText().toString().isEmpty()){
            try {
                calendar.setTime(new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        .parse(editText.getText().toString()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

                editText.setText(sdf.format(calendar.getTime()));
            }

        };

        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog(ProfileActivity.this, date, calendar
                        .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH));
                dialog.getDatePicker().setMaxDate(Calendar.getInstance().getTimeInMillis());
                dialog.show();
            }
        });

        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    DatePickerDialog dialog = new DatePickerDialog(ProfileActivity.this, date, calendar
                            .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH));
                    dialog.getDatePicker().setMaxDate(Calendar.getInstance().getTimeInMillis());
                    dialog.show();
                }
            }
        });
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();

        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
    }

}
