package co.crossroadsapp.destiny.network;

import android.content.Context;

import co.crossroadsapp.destiny.utils.Util;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by sharmha on 3/14/16.
 */
public class postGcmNetwork {

    private Context mContext;
    private NetworkEngine ntwrk;
    private String url = "api/v1/a/installation/android";

    public postGcmNetwork(Context context) {
        mContext = context;
        ntwrk = NetworkEngine.getmInstance(context);
    }

    public void postGcmToken(RequestParams params) throws JSONException {
        if (Util.isNetworkAvailable(mContext)) {
            ntwrk.post(url, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    // If the response is JSONObject instead of expected JSONArray
                    //parseEventObject(response);

//                    Toast.makeText(mContext, "GCM success ",
//                            Toast.LENGTH_LONG).show();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    // If the response is JSONObject instead of expected JSONArray
//                    Toast.makeText(mContext, "GCM success " + statusCode,
//                            Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
//                    Toast.makeText(mContext, "List error from server  - " + statusCode + " ",
//                            Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    //todo handle this error better
                    //((CreateNewEvent)mContext).finish();
//                    Toast.makeText(mContext, "List error from server  - " + statusCode + " ",
//                            Toast.LENGTH_LONG).show();
                }
            });
        }else {
            Util.createNoNetworkDialogue(mContext);
        }
    }
}
