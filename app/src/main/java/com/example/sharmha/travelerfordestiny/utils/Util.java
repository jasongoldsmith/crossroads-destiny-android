package com.example.sharmha.travelerfordestiny.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.widget.ImageView;

import com.example.sharmha.travelerfordestiny.ControlManager;
import com.example.sharmha.travelerfordestiny.data.UserData;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by sharmha on 2/23/16.
 */
public class Util {

    private static final String TAG = Util.class.getName();

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
        DisplayMetrics displayMetrics = c.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
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

//    public static String getGCMBodyRequest() {
//        JSONObject jo = new JSONObject();
//        String token = getGCMToken();
//        if (token != null) {
//            try {
//                String version = "APP_VERSION_NOT_DEFINED";
////                PackageInfo tmpInfo = Utils.getPackageInfo();
////                if (tmpInfo != null) {
////                    version = tmpInfo.versionName;
////                }
//                String os_version = (Build.VERSION.RELEASE != null ? Build.VERSION.RELEASE : "OS_VERSION_NOT_DEFINED");
//                String model = (Build.MODEL != null ? Build.MODEL : "MODEL_NOT_DEFINED");
//                jo.put("token", token);
//                jo.put("senderId", Constants.GCM_SENDER_ID);
//                jo.put("deviceId", getDeviceID());
//                jo.put("appversion", version);
//                jo.put("osversion", os_version);
//                jo.put("devicemodel", model);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//        return jo.toString();
//    }

    private static String getDeviceID() {
        // get the AndroidId
//        String deviceID = getDeviceSHA256Hash(Settings.Secure.getString(c.getContentResolver(), Settings.Secure.ANDROID_ID));
//        return deviceID;

        return "ANDROID_ID_NOT_DEFINED";
    }

    /**
     * given a string, generate SHA-256 hash
     *
     * @param plain
     * @return
     */
    public static String getDeviceSHA256Hash(String plain) {
        try {
            final MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plain.getBytes());
            byte byteData[] = md.digest();

            // convert byte to hex
            StringBuffer hex_string = new StringBuffer();
            for (int i = 0; i < byteData.length; i++) {
                hex_string.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }
            return hex_string.toString();
        } catch (NoSuchAlgorithmException e) {
            return "Exception_when_trying_to_generate_hash";
        }
    }

//    public static boolean checkForNull(Object ob) {
//        if (ob == null) {
//            throw new TrimbleException(TrimbleException.TRIMBLE_NULL_POINTER_DETECTED, "An object was detected to be null. internal error...");
//        }
//        return false;
//    }

    public static void clearDefaults(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.commit();
    }

    public static String changeTimeFormat(String args, String argsDate) throws Exception {
        SimpleDateFormat displayFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        SimpleDateFormat parseFormat = new SimpleDateFormat("MM-dd-yyyy hh:mm a");
        Date date = parseFormat.parse(argsDate + " "+args);
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
