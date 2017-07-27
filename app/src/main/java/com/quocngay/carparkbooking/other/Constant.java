package com.quocngay.carparkbooking.other;

import android.content.Intent;

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
    public static final String GARA_LOCATION = "gara_latlng";
    public static final String GARA_ADDRESS = "gara_address";
    public static final int DEFAULT_ZOOM = 15;
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
    public static final String RESULT = "result";
    public static final String DATA = "data";
    public static final String MESSAGE = "mess";
    public static final String IS_VERIFY = "is_verify";
    public static final String REQUEST_COMPARE_CODE = "request_compare_code";
    public static final String RESPONSE_COMPARE_CODE = "response_compare_code";
    public static final String REQUEST_CHECK_TOKEN = "request_check_token";
    public static final String RESPONSE_CHECK_TOKEN = "response_check_token";
    public static final String REQUEST_GET_ALL_GARAGES = "request_all_garages";
    public static final String RESPONSE_GET_ALL_GARAGES = "response_all_garages";
    public static final String SERVER_GARAGES_RESULT = "Garages";
    public static final String SERVER_PARKING_INFO_RESULT = "ParkingInfo";
    public static final String MY_LOCATION = "my_location";
    public static final int REQUEST_CODE_NEAREST = 9;
    public static final String RESULT_TITLE = "address_title";
    public static final String REQUEST_FIND_CAR_BY_ACCOUNT_ID = "request_find_car_by_account_id";
    public static final String RESPONSE_FIND_CAR_BY_ACCOUNT_ID = "response_find_car_by_account_id";
    public static final String APP_PREF_ID = "user_id";
    public static final String SERVER_RESPONSE_LOGIN_PARA_ID = "id";
    public static final String VEHICLE_NUMBER = "vehicleNumber";
    public static final String GARA_DETAIL = "gara_detail";
    public static final String REQUEST_ADD_NEW_PARKING_INFO = "request_add_new_booking";
    public static final String RESPONSE_ADD_NEW_PARKING_INFO = "response_add_new_booking";
    public static final String REQUEST_PARKING_INFO_BY_ACCOUNT_ID = "request_booking_account_id";
    public static final String RESPONSE_PARKING_INFO_BY_ACCOUNT_ID = "response_booking_account_id";

    //region PARKING INFO
    public static final String REQUEST_BOOK_HISTORY = "request_booking_history_account_id";
    public static final String RESPONSE_BOOK_HISTORY= "response_booking_history_account_id";

    public static final String REQUEST_EDIT_PARKING_INFO_BY_ID_STATUS =
            "request_edit_parking_info_id_status";
    public static final String RESPONSE_EDIT_PARKING_INFO_BY_ID_STATUS =
            "response_edit_parking_info_id_status";

    //endregion

    //region CAR
    public static final String REQUEST_CAR_CUSTOMER = "request_find_car_by_account_id";
    public static final String RESPONSE_CAR_CUSTOMER= "response_find_car_by_account_id";

    public static final String REQUEST_DELETE_CAR = "request_remove_car_by_id";
    public static final String RESPONSE_DELETE_CAR = "response_remove_car_by_id";

    //endregion

    public static int PREF_MODE = 1;
    //        public static String SERVER_HOST = "http://54.255.178.120:5000";

    public static String SERVER_HOST = "http://192.168.1.30:5000";

//    public static String SERVER_HOST = "http://192.168.196.1:5000";

    //    public static String SERVER_HOST = "http://52.15.194.52:5000";
    public static String APP_PREF = "AppPref";
    public static String APP_PREF_TOKEN = "token";
    public static String APP_PREF_REMEMBER = "remember_status";
    public static String SERVER_RESPONSE_LOGIN_PARA_EMAIL = "email";
    public static String SERVER_RESPONSE_LOGIN_PARA_PASSWORD = "password";
    public static String SERVER_RESPONSE_LOGIN_PARA_TOKEN = "token";
    public static String SERVER_REQUEST_CREATE_ACCOUNT = "request_create_account";
    public static String SERVER_RESPONSE_CREATE_ACCOUNT = "response_create_account";
    public static String SERVER_REQUEST_RESET_PASSWORD = "request_reset_password";
    public static String SERVER_RESPONSE_RESET_PASSWORD = "response_reset_password";
    public static String SERVER_REQUEST_CHANGE_PASSWORD = "request_change_password";
    public static String SERVER_RESPONSE_CHANGE_PASSWORD = "response_change_password";

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
    public static int PARKING_INFO_STATUS_BOOKED = 0;
    public static int PARKING_INFO_STATUS_CHECKEDIN = 1;
    public static int PARKING_INFO_STATUS_CHECKEDOUT = 2;
    public static int PARKING_INFO_STATUS_CANCEL = 3;
}
