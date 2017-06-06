package com.quocngay.carparkbooking.other;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Created by ninhh on 5/22/2017.
 */

public class Constant {
    public static String APP_PREF = "AppPref";
    public static String APP_PREF_TOKEN = "token";

    public static String SERVER_EMIT_APP_REQUEST_OPEN_TICKETS = "request open tickets";
    public static String SERVER_EMIT_SERVER_RESPONSE_OPEN_TICKETS = "response open tickets";

    public static String SERVER_RESPONSE_RESULT = "result";
    public static String SERVER_RESPONSE_DATA = "data";

    public static int KEY_EXPIRED_TICKET = 30 * 60 * 1000000; //in milliseconds
    public static DateFormat KEY_DATE_TIME_FORMAT = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
    public static DateFormat KEY_TIME_DURATION_FORMAT = new SimpleDateFormat("hh:mm:ss");
    public static DateFormat KEY_DATE_TIME_DURATION_FORMAT = new SimpleDateFormat("dd:hh:mm");

}
