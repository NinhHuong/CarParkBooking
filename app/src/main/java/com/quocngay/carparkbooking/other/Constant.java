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
    public static final int REQUEST_CODE_NEAREST = 9;
    public static final int REQUEST_CODE_BOOKING_DETAIL = 8;


    public static final String GARA_LOCATION = "gara_latlng";
    public static final String GARA_ADDRESS = "gara_address";
    public static final int DEFAULT_ZOOM = 15;
    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    public static final String KEY_CAMERA_POSITION = "camera_position";
    public static final String KEY_LOCATION = "location";
    public static final int PASSWORD_LENGTH = 6;
    public static final String BOOKING_STATUS = "book_status";
    public static final int ROLE_USER_VALUE = 4;
    public static final int ROLE_SUPER_ADMIN_VALUE = 1;
    public static final int ROLE_ADMIN_VALUE = 2;
    public static final int ROLE_SECURITY_VALUE = 3;
    public static final int STATUS_GARAGE_OPEN = 1;
    public static final int STATUS_GARAGE_CLOSE = 0;
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
    public static final String RESPONSE_GARAGE_UPDATED = "response_garage_updated";

    public static final String SERVER_GARAGES_RESULT = "Garages";
    public static final String SERVER_PARKING_INFO_RESULT = "ParkingInfo";
    public static final String MY_LOCATION = "my_location";
    public static final String RESULT_TITLE = "address_title";
    public static final String REQUEST_FIND_CAR_BY_ACCOUNT_ID = "request_find_car_by_account_id";
    public static final String RESPONSE_FIND_CAR_BY_ACCOUNT_ID = "response_find_car_by_account_id";
    public static final String APP_PREF_ID = "user_id";
    public static final String SERVER_RESPONSE_LOGIN_PARA_ID = "id";
    public static final String SERVER_RESPONSE_LOGIN_PARA_ROLE = "role";
    public static final String VEHICLE_NUMBER = "vehicleNumber";
    public static final String GARA_DETAIL = "gara_detail";
    public static final String REQUEST_ADD_NEW_PARKING_INFO = "request_add_new_booking";
    public static final String RESPONSE_ADD_NEW_PARKING_INFO = "response_add_new_booking";
    public static final String REQUEST_PARKING_INFO_BY_ACCOUNT_ID = "request_booking_account_id";
    public static final String RESPONSE_PARKING_INFO_BY_ACCOUNT_ID = "response_booking_account_id";

    //region USER

    public static final String REQUEST_FIND_USER_BY_ACCOUNT_ID = "request_find_user_by_account_id";
    public static final String RESPONSE_FIND_USER_BY_ACCOUNT_ID = "response_find_user_by_account_id";
    public static final String REQUEST_EDIT_USER_BY_ID = "request_edit_user_by_id";
    public static final String RESPONSE_EDIT_USER_BY_ID = "response_edit_user_by_id";
    public static final String REQUEST_ADD_NEW_USER_BY_ACCOUNT_ID =
            "request_add_new_user_by_account_id";
    public static final String RESPONSE_ADD_NEW_USER_BY_ACCOUNT_ID =
            "response_add_new_user_by_account_id";


    //endregion


    //region PARKING INFO
    public static final String REQUEST_ADD_NEW_PARKING_INFO_BY_USER =
            "request_add_new_booking_by_user";
    public static final String RESPONSE_ADD_NEW_PARKING_INFO_BY_USER =
            "response_add_new_booking_by_user";

    public static final String REQUEST_BOOK_HISTORY = "request_booking_history_account_id";
    public static final String RESPONSE_BOOK_HISTORY = "response_booking_history_account_id";

    public static final String REQUEST_EDIT_PARKING_INFO_BY_ID_STATUS =
            "request_edit_parking_info_id_status";
    public static final String RESPONSE_EDIT_PARKING_INFO_BY_ID_STATUS =
            "response_edit_parking_info_id_status";
    public static final String KEY_MAIN_MAP = "google_map";
    public static final String BOOKING_MAP_SIZE = "600x300";
    public static final String REQUEST_REFRESH_BOOKING_TIMEOUT = "request_refresh_booking_timeout";
    public static final String RESPONSE_REFRESH_BOOKING_TIMEOUT = "response_refresh_booking_timeout";

    public static final String RESPONSE_BOOKING_CANCELED = "response_booking_canceled";

    //endregion

    //region HISTORY
    public static final String REQUEST_BOOKING_HISTORY_ACCOUNT_ID = "request_booking_history_account_id";
    public static final String RESPONSE_BOOKING_HISTORY_ACCOUNT_ID = "response_booking_history_account_id";
    //endregion

    //region CAR
    public static final String REQUEST_CAR_CUSTOMER = "request_find_car_by_account_id";
    public static final String RESPONSE_CAR_CUSTOMER = "response_find_car_by_account_id";
    public static final String REQUEST_FIND_CAR_BY_ID = "request_find_car_by_id";
    public static final String REQUEST_ADD_NEW_CAR = "request_add_new_car";
    public static final String REQUEST_REMOVE_CAR_BY_ID = "request_remove_car_by_id";

    public static final String RESPONSE_FIND_CAR_BY_ID = "response_find_car_by_id";

    public static final String RESPONSE_ADD_NEW_CAR = "response_add_new_car";
    public static final String RESPONSE_REMOVE_CAR_BY_ID = "response_remove_car_by_id";

    //endregion

    //region Security
    public static final String REQUEST_CAR_WILL_IN = "request_car_go_in";
    public static final String RESPONSE_CAR_WILL_IN = "response_car_go_in";

    public static final String REQUEST_CAR_WILL_OUT = "request_car_go_out";
    public static final String RESPONSE_CAR_WILL_OUT = "response_car_go_out";

    public static final String REQUEST_ONE_CAR_IN_ID = "request_one_car_in_by_id";
    public static final String RESPONSE_ONE_CAR_IN_ID = "request_one_car_in_by_id";
    public static final String REQUEST_ONE_CAR_IN_NUMBER = "request_one_car_in_by_vehicle_number";
    public static final String RESPONSE_ONE_CAR_IN_NUMBER = "response_one_car_in_by_vehicle_number";
    public static final String RESPONSE_ONE_CAR_IN = "response_one_car_in";

    public static final String REQUEST_ONE_CAR_OUT = "request_one_car_out";
    public static final String RESPONSE_ONE_CAR_OUT = "response_one_car_out";

    public static final String REQUEST_GET_GARAGE_ID = "request_find_security_by_account_id";
    public static final String RESPONSE_GET_GARAGE_ID = "response_find_security_by_account_id";

    public static final String REQUEST_EDIT_GARAGE_STATUS = "request_edit_status_garage_by_id";
    //endregion

    //region ADMIN
    public static final String REQUEST_GET_GARAGE_BY_ACCOUNT_ID = "request_get_garage_by_account_id";
    public static final String RESPONSE_GET_GARAGE_BY_ACCOUNT_ID = "response_get_garage_by_account_id";

    public static final String REQUEST_CREATE_ACCOUNT_SECURITY = "request_create_new_account_for_security";
    public static final String RESPONSE_CREATE_ACCOUNT_SECURITY = "response_create_new_account_for_security";

    public static final String REQUEST_ALL_SECURITY = "request_all_security";
    public static final String RESPONSE_ALL_SECURITY = "response_all_security";

    public static final String REQUEST_HISTORY = "request_history";
    public static final String RESPONSE_HISTORY = "response_history";
    //endregion


    //endregion

    public static final String REQUEST_GET_GARAGE_BY_ID = "request_get_garage_by_id";
    public static final String RESPONSE_GET_GARAGE_BY_ID = "response_get_garage_by_id";
    public static final int NOTIFICATION_TIME_OUT = 0;
    public static final String RESPONSE_NOTI_TIME_OUT = "response_notification_time_out";
    public static final String BOOKING_DETAIL_STATUS = "booking_detail_status";
    public static final String BOOKING_DETAIL_STATUS_CANCEL = "cancel";

    public static int PREF_MODE = 1;
    //        public static String SERVER_HOST = "http://54.255.178.120:5000";

    //public static String SERVER_HOST = "http://192.168.0.110:5000";


    public static String SERVER_HOST = "http://192.168.1.2:5000";

    //    public static String SERVER_HOST = "http://52.15.194.52:5000";
    public static String APP_PREF = "AppPref";
    public static String APP_PREF_TOKEN = "token";
    public static String APP_PREF_REMEMBER = "remember_status";
    public static final String APP_PREF_FIREBASE_REMEMBER = "firebase_token";
    public static String APP_PREF_ROLE = "role";
    public static String APP_PREF_IS_LOGIN = "is_login";
    public static String APP_PREF_GARAGE_ID = "garage_id";
    public static final String APP_PREF_EMAIL = "email";

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
