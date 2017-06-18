package com.quocngay.carparkbooking.other;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by ninhh on 5/22/2017.
 */

public class Constant {
    public static int PREF_MODE = 1;

    public static String SERVER_HOST = "http://54.255.178.120:5000";

    public static String APP_PREF = "AppPref";
    public static String APP_PREF_TOKEN = "token";

    public static String SERVER_RESPONSE_LOGIN_PARA_EMAIL = "email";
    public static String SERVER_RESPONSE_LOGIN_PARA_PASSWORD = "password";
    public static String SERVER_RESPONSE_LOGIN_PARA_TOKEN = "token";

    public static String SERVER_REQUEST_OPEN_TICKETS = "request open tickets";
    public static String SERVER_RESPONSE_OPEN_TICKETS = "response open tickets";
    public static String SERVER_REQUEST_CREATE_TOKEN = "request create token";
    public static String SERVER_RESPONSE_CREATE_TOKEN = "response create ticket";
    public static String SERVER_REQUEST_VALIDATE_TOKEN = "request validate token";
    public static String SERVER_RESPONSE_VALIDATE_TOKEN = "response validate ticket";

    public static String SERVER_RESPONSE_RESULT = "result";
    public static String SERVER_RESPONSE_DATA = "data";

    public static int KEY_EXPIRED_TICKET = 30 * 60 * 1000000; //in milliseconds
    public static DateFormat KEY_SERVER_DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US); //2017-05-24T20:05:15.000Z
    public static DateFormat KEY_DATE_TIME_FORMAT = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss", Locale.US);
    public static DateFormat KEY_TIME_DURATION_FORMAT = new SimpleDateFormat("hh:mm:ss", Locale.US);
    public static DateFormat KEY_DATE_TIME_DURATION_FORMAT = new SimpleDateFormat("dd:hh:mm", Locale.US);

}
