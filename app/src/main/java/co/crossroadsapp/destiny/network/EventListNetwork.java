package co.crossroadsapp.destiny.network;

import android.content.Context;

import co.crossroadsapp.destiny.ControlManager;
import co.crossroadsapp.destiny.data.ActivityData;
import co.crossroadsapp.destiny.data.ActivityList;
import co.crossroadsapp.destiny.utils.Util;
import co.crossroadsapp.destiny.data.EventData;
import co.crossroadsapp.destiny.data.EventList;
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
    private String url = "api/v1/a/feed/get";

    private EventList eventList;
    private ActivityList actList;
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

        actList = new ActivityList();
    }

    public void getEvents() throws JSONException {
        if (Util.isNetworkAvailable(mContext)) {
            ntwrk.get(url, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    parseFeed(response);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    try {
                        parseEventList(response);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    mManager.showErrorDialogue(statusCode + " - server failed");
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    mManager.showErrorDialogue(Util.getErrorMessage(errorResponse));
                }
            });
        }else {
            Util.createNoNetworkDialogue(mContext);
        }
    }

    private void parseFeed(JSONObject response) {
        //according to new complete feed
        if (response.has("currentEvents")) {
            JSONArray currArray = response.optJSONArray("currentEvents");
            if (currArray != null) {
                try {
                    parseEvents(currArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        if (response.has("futureEvents")) {
            JSONArray futArray = response.optJSONArray("futureEvents");
            if (futArray != null) {
                try {
                    parseEvents(futArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        if (response.has("adActivities")){
            JSONArray adActivitiesArray = response.optJSONArray("adActivities");
            if (adActivitiesArray != null) {
                try {
                    parseAddActList(adActivitiesArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        mManager.setEventList(eventList);
        mManager.setadList(actList);

        setChanged();
        notifyObservers(this);

    }

    private void parseAddActList(JSONArray adActivitiesArray) throws JSONException {
        for (int i = 0; i < adActivitiesArray.length(); i++) {
            JSONObject jsonobject = adActivitiesArray.getJSONObject(i);
            ActivityData eData = new ActivityData();
            eData.toJson(jsonobject);
            actList.appendActivityList(eData);
        }
    }

    public EventList getEventList() {
        if (eventList!= null) {
            return this.eventList;
        }
        return null;
    }

    public ActivityList getActList() {
        if (actList!= null) {
            return this.actList;
        }
        return null;
    }

    private void parseEvents(JSONArray response) throws JSONException {
        for (int i = 0; i < response.length(); i++) {
            JSONObject jsonobject = response.getJSONObject(i);
            EventData eData = new EventData();
            eData.toJson(jsonobject);
            eventList.appendEventList(eData);
        }
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
