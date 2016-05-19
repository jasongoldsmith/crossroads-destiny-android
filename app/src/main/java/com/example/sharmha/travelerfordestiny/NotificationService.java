package com.example.sharmha.travelerfordestiny;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

import com.example.sharmha.travelerfordestiny.data.ActivityData;
import com.example.sharmha.travelerfordestiny.data.EventData;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sharmha on 3/25/16.
 */
public class NotificationService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent i, int flags, int startId) {

        String payload = null;
        String alert = null;
        //get the data using the keys you entered at the service
        Bundle extra = i.getExtras();
        if (extra != null) {
            alert = extra.getString("message");
            payload = extra.getString("payload");
        }


        JSONObject jsonObj = null;
        try {
            jsonObj = (payload != null && !payload.equals(""))?new JSONObject(payload): new JSONObject();
            EventData ed = new EventData();
            Intent in = new Intent("subtype_flag");

            if(jsonObj.has("notificationName")) {
                String notiName = (String) jsonObj.get("notificationName");
                if (notiName.equalsIgnoreCase("messageFromPlayer")) {
                    in.putExtra("playerMessage", true);
                }
            }

            if(jsonObj.has("eventName")) {
                String eName = (String) jsonObj.get("eventName");
                if (!eName.equalsIgnoreCase(null)) {
                        in.putExtra("subtype", eName);
                    }
            }

//            if(jsonObj.has("event")) {
//                ed.toJson((JSONObject) jsonObj.get("event"));
//            } else {
//                ed.toJson(jsonObj);
//            }
//            if(ed.getActivityData()!=null){
//                if(ed.getActivityData().getActivitySubtype()!=null){
//                    ActivityData ad = ed.getActivityData();
//                    String st = ad.getActivitySubtype();
//                    if (!st.equalsIgnoreCase(null)) {
//                        in.putExtra("subtype", st);
//                    }
//                }
//            }
//
//            if(jsonObj.has("playerMessage") || payload == null){
//                in.putExtra("playerMessage", true);
//            }
            if(alert!=null && alert.length()>0){
                in.putExtra("message", alert);
            }
            sendBroadcast(in);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return Service.START_NOT_STICKY;
    }
}
