package com.quocngay.carparkbooking.other;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by ninhhuong on 5/22/2017.
 */

public class Constant {

    public static final int SUCCESS_RESULT = 0;
    public static final int FAILURE_RESULT = 1;
    //    public static String SERVER_HOST = "http://172.17.183.15:5000";
    public static final String PACKAGE_NAME =
            "com.google.android.gms.location.sample.locationaddress";
    public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";
    public static final String RESULT_DATA_KEY = PACKAGE_NAME + ".RESULT_DATA_KEY";
    public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME +
            ".LOCATION_DATA_EXTRA";
    public static final int REQUEST_CODE_AUTOCOMPLETE = 10;
    public static final int REQUEST_CODE_BOOKING = 1;
    public static final String GARA_LATLNG = "gara_latlng";
    public static final String GARA_ADDRESS = "gara_address";
    public static final int DEFAULT_ZOOM = 17;
    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    public static final String KEY_CAMERA_POSITION = "camera_position";
    public static final String KEY_LOCATION = "location";
    public static final int PASSWORD_LENGTH = 6;
    public static final String BOOKING_STATUS = "book_status";
    public static final int ROLE_USER_VALUE = 4;
    public static final String ROLE_ID = "roleID";
    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";
    public static final String SALT = "salt";
    public static final String REQUEST_CREATE_ACCOUNT = "request_create_account";
    public static final String RESPONSE_CREATE_ACCOUNT = "response_create_account";
    public static final String REQUEST_GET_SALT = "request_get_salt";
    public static final String RESPONSE_GET_SALT = "response_get_salt";
    public static final String REQUEST_CHECK_EMAIL_PASSWORD = "check_email_and_password";
    public static final String RESPONSE_RESULT_LOGIN = "result_login";
    public static int PREF_MODE = 1;
    //    public static String SERVER_HOST = "http://54.255.178.120:5000";
    public static String SERVER_HOST = "http://192.168.1.5:5000";
    public static String APP_PREF = "AppPref";
    public static String APP_PREF_TOKEN = "token";
    public static String SERVER_RESPONSE_LOGIN_PARA_EMAIL = "email";
    public static String SERVER_RESPONSE_LOGIN_PARA_PASSWORD = "password";
    public static String SERVER_RESPONSE_LOGIN_PARA_TOKEN = "token";
    public static String SERVER_REQUEST_CREATE_ACCOUNT = "request create account";
    public static String SERVER_RESPONSE_CREATE_ACCOUNT = "response create account";
    public static String SERVER_REQUEST_RESET_PASSWORD = "request reset password";
    public static String SERVER_RESPONSE_RESET_PASSWORD = "response reset password";
    public static String SERVER_REQUEST_CHANGE_PASSWORD = "request change password";
    public static String SERVER_RESPONSE_CHANGE_PASSWORD = "response change password";

    public static String SERVER_REQUEST_OPEN_TICKETS = "request open tickets";
    public static String SERVER_RESPONSE_OPEN_TICKETS = "response open tickets";
    public static String SERVER_REQUEST_CREATE_TOKEN = "request create token";
    public static String SERVER_RESPONSE_CREATE_TOKEN = "response create ticket";
    public static String SERVER_REQUEST_VALIDATE_TOKEN = "request validate token";
    public static String SERVER_RESPONSE_VALIDATE_TOKEN = "response validate ticket";
    public static String SERVER_RESPONSE_RESULT = "result";
    public static String SERVER_RESPONSE_DATA = "data";
    public static int KEY_EXPIRED_TICKET = 30 * 60 * 1000; //in milliseconds
    public static DateFormat KEY_SERVER_DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()); //2017-05-24T20:05:15.000Z
    public static DateFormat KEY_DATE_TIME_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
    public static DateFormat KEY_TIME_DURATION_FORMAT = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
    public static DateFormat KEY_DATE_TIME_DURATION_FORMAT = new SimpleDateFormat("dd:HH:mm", Locale.getDefault());
    public static int KEY_COUNT_DOWN_INTERVAL = 1000;
    public static int KEY_COUNT_UP_INTERVAL = 60 * 1000;

}
