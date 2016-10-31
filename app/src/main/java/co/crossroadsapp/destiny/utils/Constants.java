package co.crossroadsapp.destiny.utils;

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

    public static final String TRAVELER_NOTIFICATION_INTENT = "co.crossroadsapp.destiny.travelerfordestiny";

    // terms of services, legal and privacy policy url links
    public static final String LEGAL = "https://www.crossroadsapp.co/legal";
    public static final String LICENCE = "https://www.crossroadsapp.co/license"; //"http://68.140.240.194:3000/license"
    public static final String TERMS_OF_SERVICE = "https://www.crossroadsapp.co/terms";
    public static final String PRIVACY_POLICY = "https://www.crossroadsapp.co/privacy";
    public static final String NONE = "(none)";
    public static final String PLAYSTATION = "PlayStation";
    public static final String XBOX = "Xbox";


    public static String NOTIFICATION_INTENT_CHANNEL = "com.example.sharmha.notificationintent";
    public static int INTENT_ID = 9999;

    //firebase url
    public static String FIREBASE_DEV_URL = "https://crossroadsapp-dev.firebaseio.com/";
    public static String FIREBASE_PROD_URL = "https://crossroadsapp-live.firebaseio.com/";

    //network base url
    public static String NETWORK_DEV_BASE_URL = "https://travelerbackend.herokuapp.com/";
    public static String NETWORK_PROD_BASE_URL = "https://live.crossroadsapp.co/";
    public static String NETWORK_DEV_BASE_STAGING_URL = "https://travelerbackendproduction.herokuapp.com/";

    //app download links
    public static String DOWNLOAD_DEV_BUILD = "https://goo.gl/6vQpFn";
    public static String DOWNLOAD_PROD_BUILD = "https://goo.gl/GSLxIW";

    //PSN verified keys
    public static String PSN_VERIFIED = "VERIFIED";
    public static String PSN_NOT_INITIATED = "NOT_INITIATED";
    public static String PSN_INITIATED = "INITIATED";
    public static String PSN_FAILED_INITIATED = "FAILED_INITIATION";
    public static String PSN_DELETED = "DELETED";

    public static String CONSOLEXBOXONE = "XBOXONE";
    public static String CONSOLEXBOX360 = "XBOX360";
    public static String CONSOLEPS4 = "PS4";
    public static String CONSOLEPS3 = "PS3";

    public static String CONSOLEXBOXONESTRG = "Xbox One";
    public static String CONSOLEXBOX360STRG = "Xbox 360";
    public static String CONSOLEPS4STRG = "PlayStation 4";
    public static String CONSOLEPS3STRG = "PlayStation 3";


    //clan id keys
    public static String CLAN_NOT_SET = "clan_id_not";
    public static String FREELANCER_GROUP = "clan_id_not_set";

    public static int USER_CHANNEL = 2;
    public static int EVENT_CHANNEL = 1;
    public static int EVENT_COMMENT_CHANNEL = 3;

    public static int PUBLIC_EVENT_FEED = 1;
    public static int EVENT_FEED = 2;

    //deeplink error types
    public static int EVENT_GRP_MISSING = 1;
    public static int EVENT_MISSING = 2;
    public static int EVENT_FULL= 3;
    public static int EVENT_CONSOLE_MISSING= 4;

    public static final int TIME_SECOND = 1000;
    public static final int TIME_MINUTE = 60 * 1000;
    public static final int TIME_HOUR = 60 *  60 * 1000;
    public static final int TIME_DAY = 24 * 60 *  60 * 1000;
    public static final int TIME_TWO_DAY = 2 * 24 * 60 *  60 * 1000;
    public static final int WEEK = 7 * 24 * 60 *  60 * 1000;

    public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    public static final String UNKNOWN_SOURCE = "unknown";
    public static final String ORGANIC_SOURCE = "organic";
    public static final String FACEBOOK_SOURCE = "facebook";
    public static final String BRANCH_SOURCE = "branch";
    public static final String APP_RESUME = "appResume";
    public static final String APP_INSTALL = "appInstall";
    public static final String APP_SIGNUP = "signupInit";
    public static final String APP_PUSHNOTIFICATION = "pushNotification";
    public static final String APP_INIT = "appInit";
    public static final String APP_ADCARD = "adCardInit";
    public static final String APP_EVENTSHARING = "eventSharing";
    public static final String APP_CURRENTTAB = "currentTabInit";
    public static final String APP_UPCOMINGTAB = "upcomingTabInit";
    public static String APP_SHOWPASSWORD = "showPassword";

    //urls
    public static String DEEP_LINK_IMAGE = "http://w3.crossroadsapp.co/bungie/share/branch/v1/";
    public static String BUNGIE_ERROR = "BungieLoginError";
    public static String BUNGIE_CONNECT_ERROR = "BungieConnectError";
    public static String BUNGIE_LEGACY_ERROR = "BungieLegacyConsoleError";

    public static String LEGACY_ERROR_TITLE = "Legacy Consoles";
    public static String LEGACY_ERROR_MSG = "In line with Rise of Iron, we now only support next-gen consoles. When you’ve upgraded your console, please come\n" +
            "back and join us!";

    //error types
    public static int GENERAL_ERROR = 1;
    public static int REPORT_COMMENT = 2;
    public static int REPORT_COMMENT_NEXT = 3;

    //api url
    public static String REPORT_COMMENT_URL = "api/v1/a/event/reportComment";

    //getCurrentUser bungie
    public static String BUGIE_CURRENT_USER = "https://www.bungie.net/Platform/User/GetBungieNetUser";

    public static String BUNGIE_PSN_LOGIN = "https://www.bungie.net/en/User/SignIn/Psnid";
    public static String BUNGIE_XBOX_LOGIN = "https://www.bungie.net/en/User/SignIn/Xuid";
}
