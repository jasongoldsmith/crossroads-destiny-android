package com.example.sharmha.travelerfordestiny;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.example.sharmha.travelerfordestiny.data.ActivityData;
import com.example.sharmha.travelerfordestiny.data.EventData;
import com.example.sharmha.travelerfordestiny.utils.Constants;
import com.example.sharmha.travelerfordestiny.utils.Util;
import com.example.sharmha.travellerdestiny.R;
import com.example.sharmha.travelerfordestiny.utils.TravellerLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by sharmha on 3/14/16.
 */
public class MyGcmBroadcastReceiver extends BroadcastReceiver {

    private static final String GCM_RECEIVE_INTENT = "com.google.android.c2dm.intent.RECEIVE";
    private final String TAG = this.getClass().getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        TravellerLog.w(TAG, "received from gcm");
        if (intent.getAction().equals(GCM_RECEIVE_INTENT)) {
            String pushDataString = intent.getStringExtra("data");
            Bundle i = intent.getExtras();
            JSONObject pushData = null;
            String alert = null;
            String payload = null;
            if (i != null) {
                    //pushData = new JSONObject(pushDataString);
                    alert = i.getString("message");
                    payload = i.getString("payload");
            }
            if (isAppRunning(context)) {
                Intent in = new Intent(context, NotificationService.class);
                        in.putExtra("payload", payload);
                        in.putExtra("message", alert);
                        in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        JSONObject jsonObj = new JSONObject(payload);
//                        EventData ed = new EventData();
//                        ed.toJson(jsonObj);
//                        ActivityData ad = ed.getActivityData();
//                        String st = ad.getActivitySubtype();
//                        in.putExtra("subtype", st);
                        context.startService(in);

                    //broadcast message to activity
                    //in.putExtra("subtype", ed)

            } //else if (pushData != null) {
//                Intent i = new Intent(context, UpdateCacheService.class);
//                i.putExtra("message", payload);
//                i.putExtra("alert", alert);
//                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                context.startService(i);
                //GCMResponse gcmMessage = Utils.readGCMResponse(payload);
//                if( gcmMessage == null )
//                {
//                    return;
//                }
                //int notificationCounter = Utils.readNewNotificationCount();
//                Intent notificationIntent = createNotificationIntent(context, alert, gcmMessage, payload, gcmMessage.getTimeInMilliseconds());
//                PendingIntent resultIntent = PendingIntent.getActivity(context, gcmMessage.getTimeInMilliseconds(), notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
                if(alert!=null) {
                    mBuilder.setAutoCancel(false).setSmallIcon(R.drawable.img_traveler_badge_icon).setContentTitle(context.getResources().getString(R.string.app_name)).setStyle(new NotificationCompat.BigTextStyle().bigText(alert)).setContentText(alert);
                }else{
                    mBuilder.setSmallIcon(R.drawable.img_traveler_badge_icon).setContentText("New Message Received").setContentTitle(context.getResources().getString(R.string.app_name));
                }
                NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.notify(1, mBuilder.build());
                //Utils.storeNewNotificationCount(notificationCounter);
            //}
        }
    }

    public boolean isAppRunning(Context context) {
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> services = activityManager.getRunningTasks(Integer.MAX_VALUE);
        if (services != null && services.size() > 0 && services.get(0).topActivity.getPackageName().toString()
                .equalsIgnoreCase(context.getPackageName().toString())) {
            return true;
        }
        return false;
    }
}
