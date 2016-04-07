package com.example.sharmha.travelerfordestiny.network;

import android.content.Context;
import android.widget.Toast;

import com.example.sharmha.travelerfordestiny.ControlManager;
import com.example.sharmha.travelerfordestiny.data.ActivityData;
import com.example.sharmha.travelerfordestiny.data.ActivityList;
import com.example.sharmha.travelerfordestiny.utils.Util;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Observable;

import cz.msebera.android.httpclient.Header;

/**
 * Created by sharmha on 3/8/16.
 */
public class ActivityListNetwork extends Observable {
    private NetworkEngine ntwrk;
    private String url = "activity/list";
    private Context mContext;
    private ControlManager mManager;

    private ActivityList activityList;

    public ActivityListNetwork(Context c){
        mContext = c;
        ntwrk = NetworkEngine.getmInstance(c);
        mManager = ControlManager.getmInstance();

        if (activityList != null) {

        } else {
            activityList = new ActivityList();
        }
    }

    public void postGetActivityList() throws JSONException {
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

                    parseActivityList(response);

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

    private void parseActivityList(JSONArray response) {
        for (int i = 0; i < response.length(); i++) {
            JSONObject jsonobject = null;
            try {
                jsonobject = response.getJSONObject(i);
                ActivityData eData = new ActivityData();
                eData.toJson(jsonobject);
                this.activityList.appendActivityList(eData);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        setChanged();
        notifyObservers(this.activityList);
    }
}
