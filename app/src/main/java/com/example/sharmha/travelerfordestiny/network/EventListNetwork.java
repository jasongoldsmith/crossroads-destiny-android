package com.example.sharmha.travelerfordestiny.network;

import android.content.Context;
import android.widget.Toast;

import com.example.sharmha.travelerfordestiny.ControlManager;
import com.example.sharmha.travelerfordestiny.utils.Util;
import com.example.sharmha.travelerfordestiny.data.EventData;
import com.example.sharmha.travelerfordestiny.data.EventList;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Observable;

import cz.msebera.android.httpclient.Header;

/**
 * Created by sharmha on 2/29/16.
 */
public class EventListNetwork extends Observable{

    private Context mContext;
    private NetworkEngine ntwrk;
    private String url = "a/event/list";

    private EventList eventList;
    private ControlManager mManager;

    public EventListNetwork(Context c) {
        //listA = act;
        mContext = c;
        mManager = ControlManager.getmInstance();
        ntwrk = NetworkEngine.getmInstance(c);

        if (eventList != null) {

        } else {
            eventList = new EventList();
        }
    }

    public void getEvents() throws JSONException {
        PersistentCookieStore a = ntwrk.getCookie();
        if (Util.isNetworkAvailable(mContext)) {
            ntwrk.get(url, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    // If the response is JSONObject instead of expected JSONArray
//                    Toast.makeText(mContext, "List server call Success",
//                            Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    // If the response is JSONObject instead of expected JSONArray
//                    Toast.makeText(mContext, "List server call Success",
//                            Toast.LENGTH_SHORT).show();


                    try {
                        parseEventList(response);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
//                    Toast.makeText(mContext, "List error from server  - " + statusCode + " ",
//                            Toast.LENGTH_LONG).show();
                    mManager.showErrorDialogue(statusCode + " - server failed");
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
//                        Toast.makeText(mContext, "List error from server  - " + statusCode + " " + errorResponse.getString("message"),
//                                Toast.LENGTH_LONG).show();
                    mManager.showErrorDialogue(statusCode + " - server failed");
                }
            });
        }else {
            Util.createNoNetworkDialogue(mContext);
        }
    }

    public EventList getEventList() {
        if (eventList!= null) {
            return this.eventList;
        }
        return null;
    }

    private void parseEventList(JSONArray response) throws JSONException {
        for (int i = 0; i < response.length(); i++) {
            JSONObject jsonobject = response.getJSONObject(i);
            EventData eData = new EventData();
            eData.toJson(jsonobject);
            eventList.appendEventList(eData);
        }
        setChanged();
        notifyObservers(this.eventList);
        //listA.updateEventList(this.eventList);
    }
}
