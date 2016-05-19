package com.example.sharmha.travelerfordestiny.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;

import com.example.sharmha.travelerfordestiny.ControlManager;
import com.example.sharmha.travelerfordestiny.data.UserData;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by sharmha on 2/23/16.
 */
public class Util {

    //To switch between production and development server links
    //where 1 points to development and 2 points to production
    private static final int network_connection = 1;

    private static final String TAG = Util.class.getName();
    public static final String trimbleDateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    static SimpleDateFormat formatter = new SimpleDateFormat(trimbleDateFormat);

    public static String getNetworkUrl() {
        if (network_connection==2){
            return Constants.NETWORK_PROD_BASE_URL;
        }
        return Constants.NETWORK_DEV_BASE_URL;
    }

    public static String getFirebaseUrl(String clanId) {
        String url, tempUrl;
        if (network_connection==2) {
            url = Constants.FIREBASE_PROD_URL;
        } else {
            url = Constants.FIREBASE_DEV_URL;
        }
        if (clanId!=null && (!clanId.equalsIgnoreCase("null")) ) {
            url = url + "/" + clanId;
        }
        return url;
    }

    public static String getAppDownloadLink() {
        if (network_connection==2) {
            return Constants.DOWNLOAD_PROD_BUILD;
        }
        return Constants.DOWNLOAD_DEV_BUILD;
    }

    public static boolean isNetworkAvailable(Context c) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void storeUserData(UserData userData, String user, String pass) {
        if (userData!= null) {
            userData.setUser(user);
            userData.setPassword(pass);
        }
    }

    public static void setDefaults(String key, String value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getDefaults(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, null);
    }

    public static void getGCMToken(final Context c, final ControlManager mngr) {
            final InstanceID instanceID = InstanceID.getInstance(c);
            if (instanceID!=null) {
                new Thread() {
                    public void run() {
                        try {
                            String token = instanceID.getToken(Constants.GCM_SENDER_ID, GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                           final String token1 = token;
                            ((Activity)c).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mngr.postGCMToken(token1, c);
                                }
                            });
                        } catch (Exception e) {
                            //handle failure here
                        }
                    }
                }.start();
            }
    }

    public static int dpToPx(int dp, Context c) {
        if(c!=null) {
            DisplayMetrics displayMetrics = c.getResources().getDisplayMetrics();
            int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
            return px;
        }
         return 0;
    }

    public static String getApplicationVersionCode(Context c) {
        PackageInfo pInfo = null;
        String version = "APP_VERSION_NOT_DEFINED";
            //Context ctx = AppEngine.getInstance().getTrimbleContext().getContext();
//            if (ctx != null) {
        try {
            pInfo = c.getPackageManager().getPackageInfo(c.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
//            }
            if (pInfo != null) {
                StringBuilder sb = new StringBuilder();
                sb.append(pInfo.versionName);
                version = sb.toString();
            }
        return version;
    }

    private static String getDeviceID() {
        // get the AndroidId
//        String deviceID = getDeviceSHA256Hash(Settings.Secure.getString(c.getContentResolver(), Settings.Secure.ANDROID_ID));
//        return deviceID;

        return "ANDROID_ID_NOT_DEFINED";
    }

    public static void clearDefaults(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.commit();
    }

    public static String changeTimeFormat(String args, String argsDate) throws Exception {
        SimpleDateFormat displayFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        SimpleDateFormat parseFormat = new SimpleDateFormat("MM-dd-yyyy hh:mm a");
        Date date = parseFormat.parse(argsDate + " " + args);
        return displayFormat.format(date);
        //System.out.println(parseFormat.format(date) + " = " + displayFormat.format(date));
    }

    public static String updateTime(int hours, int mins){
        String timeSet = "";
        if (hours > 12) {
            hours -= 12;
            timeSet = "PM";
        } else if (hours == 0) {
            hours += 12;
            timeSet = "AM";
        } else if (hours == 12)
            timeSet = "PM";
        else
            timeSet = "AM";


        String minutes = "";
        if (mins < 10)
            minutes = "0" + mins;
        else
            minutes = String.valueOf(mins);

        // Append in a StringBuilder
        String aTime = new StringBuilder().append(hours).append(':')
                .append(minutes).append(" ").append(timeSet).toString();

        return (aTime.length()>0?aTime:null);
    }

    public static void createNoNetworkDialogue(Context c) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                c);

        // set title
        alertDialogBuilder.setTitle("          Connection Failure");

        // set dialog message
        alertDialogBuilder
                .setMessage("             No network available")
                .setCancelable(false)
                .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, close
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    public static String createServerFormatedDate() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        return sdf.format(cal.getTime());
    }

    public static String convertUTCtoReadable(String utcDate) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date newDate = null;
        try {
            newDate = format.parse(utcDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        format = new SimpleDateFormat("MMM dd,yyyy hh:mm a");
        String date = format.format(newDate);
        return date;
    }

    public static long parseDate(String dateString) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        if (dateString != null) {
            try {
                format.setTimeZone(TimeZone.getTimeZone("GMT"));
                Date date = format.parse(dateString);
                return date.getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    public static String getErrorMessage(JSONObject jsonResponse) {
        if(jsonResponse!=null) {
            try {
                if(jsonResponse.has("error")) {
                    //JSONObject jsonData = jsonResponse.optJSONObject("message");
                    String n = jsonResponse.getString("error");
                    return n;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static Calendar getCalendar(String dateString) {
        try {
            Calendar cal1 = Calendar.getInstance();
            TimeZone tz = cal1.getTimeZone();
            long msFromEpochGmt = cal1.getTimeInMillis();
            long offsetFromUTC = tz.getOffset(msFromEpochGmt);
            Date date = formatter.parse(dateString);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.setTimeInMillis(cal.getTimeInMillis() + offsetFromUTC);
            return cal;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public static boolean updateCurrEventOnTime(final String date) {
        String mTemp = null;
        Calendar cal = getCalendar(date);
        if (cal == null) {
            return false;
        }
        long timeDif = cal.getTimeInMillis() - System.currentTimeMillis();
        if (timeDif<10*60*1000){
            return true;
        }

        return false;
    }

    public static String getCurrentTime() {
        Calendar cal = Calendar.getInstance();

        SimpleDateFormat df = new SimpleDateFormat("hh:mm a");
        String formattedTime = df.format(cal.getTime());
        return formattedTime;
    }

    public static String getCurrentDate() {
        Calendar c = Calendar.getInstance();

        SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy");
        String formattedDate = df.format(c.getTime());
        return formattedDate;
    }

    public static void picassoLoadIcon(Context c, ImageView eventIcon, String url, int height, int width, int avatar) {
        if (url != null) {
            Picasso.with(c).load(url)
                    .resizeDimen(width, height)
                    .centerCrop().placeholder(avatar)
                    .into(eventIcon, new Callback() {
                        @Override
                        public void onSuccess() {
                            TravellerLog.w(TAG, "success");
                        }

                        @Override
                        public void onError() {
                            TravellerLog.w(TAG, "error");
                        }
                    });
        }
    }
}
