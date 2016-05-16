package com.example.sharmha.travelerfordestiny.utils;

/**
 * Created by sharmha on 3/4/16.
 */
public class Constants {

    public static final String STATUS_CAN_JOIN = "can_join";
    public static final String STATUS_NEW = "new";
    public static final String STATUS_FULL = "full";
    public static final String STATUS_OPEN = "open";

    public static final String ACTIVITY_RAID = "RAID";
    public static final String ACTIVITY_ARENA = "ARENA";
    public static final String ACTIVITY_CRUCIBLE = "CRUCIBLE";
    public static final String ACTIVITY_FEATURED = "FEATURED";
    public static final String ACTIVITY_STRIKES = "STRIKE";
    public static final String ACTIVITY_PATROL = "PATROL";

    public static final String LAUNCH_STATUS_UPCOMING = "upcoming";
    public static final String LAUNCH_STATUS_NOW = "now";

    public final static String GCM_SENDER_ID = "375926567407";

    public final static int LOGIN = 1;
    public final static int REGISTER = 2;

    public static final String TRAVELER_NOTIFICATION_INTENT = "com.example.sharmha.travelerfordestiny";
    public static String NOTIFICATION_INTENT_CHANNEL = "com.example.sharmha.notificationintent";
    public static int INTENT_ID = 9999;

    //firebase url
    public static String FIREBASE_DEV_URL = "https://traveler-development.firebaseio.com/events";
    public static String FIREBASE_PROD_URL = "https://traveler-production.firebaseio.com/events";

    //network base url
    public static String NETWORK_DEV_BASE_URL = "https://travelerbackend.herokuapp.com/api/v1/";
    public static String NETWORK_PROD_BASE_URL = "https://travelerbackendproduction.herokuapp.com/api/v1/";

    //app download links
    public static String DOWNLOAD_DEV_BUILD = "https://goo.gl/6vQpFn";
    public static String DOWNLOAD_PROD_BUILD = "https://goo.gl/GSLxIW";
}
