package co.crossroadsapp.destiny.network;

import android.content.Context;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import co.crossroadsapp.destiny.ControlManager;
import co.crossroadsapp.destiny.utils.Util;
import cz.msebera.android.httpclient.Header;

/**
 * Created by sharmha on 8/19/16.
 */
public class TrackingNetwork {

    private final Context mContext;
    private NetworkEngine ntwrk;
    private String url = "api/v2/mixpanel/track";

    public TrackingNetwork(Context c) {
        mContext = c;
        ntwrk = NetworkEngine.getmInstance(c);
    }

    public void postTracking(RequestParams params) throws JSONException {
        if (Util.isNetworkAvailable(mContext)) {
            ntwrk.post(url, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    //
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    //mManager.showErrorDialogue(Util.getErrorMessage(errorResponse));
                }
            });
        }else {
            Util.createNoNetworkDialogue(mContext);
        }
    }
}
