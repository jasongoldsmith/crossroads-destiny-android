package co.crossroadsapp.destiny.network;

import android.content.Context;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Observable;

import co.crossroadsapp.destiny.ControlManager;
import co.crossroadsapp.destiny.utils.Constants;
import co.crossroadsapp.destiny.utils.Util;
import cz.msebera.android.httpclient.Header;

/**
 * Created by sharmha on 8/19/16.
 */
public class TrackingNetwork extends Observable {

    private final Context mContext;
    private final ControlManager mManager;
    private NetworkEngine ntwrk;
    private String url = Constants.MIXPANEL_TRACK_URL;

    public TrackingNetwork(Context c) {
        mContext = c;
        ntwrk = NetworkEngine.getmInstance(c);
        mManager = ControlManager.getmInstance();
    }

    public void postTracking(RequestParams params) throws JSONException {
        if (Util.isNetworkAvailable(mContext)) {
            ntwrk.post(url, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    if (response.has("trackingKey") && !response.isNull("trackingKey")) {
                        try {
                            if(response.get("trackingKey").toString().equalsIgnoreCase(Constants.APP_INSTALL)) {
                                setChanged();
                                notifyObservers();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    mManager.showErrorDialogue(null);
                }
            });
        }else {
            Util.createNoNetworkDialogue(mContext);
        }
    }
}
