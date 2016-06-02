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
 * Created by sharmha on 4/1/16.
 */
public class ReportCrashNetwork {

    private NetworkEngine ntwrk;
    private String url = "a/report/create";
    private Context mContext;

    public ReportCrashNetwork(Context c) {
        mContext = c;
        ntwrk = NetworkEngine.getmInstance(c);
    }

    public void doCrashReport(RequestParams params) throws JSONException {
        if (Util.isNetworkAvailable(mContext)) {
            ntwrk.post(url, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    // If the response is JSONObject instead of expected JSONArray
//                    Toast.makeText(mContext, "Lis server call Success",
//                            Toast.LENGTH_SHORT).show();
//                    parseVersion(response);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    // If the response is JSONObject instead of expected JSONArray
//                    Toast.makeText(mContext, "List server call Success",
//                            Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
//                    Toast.makeText(mContext, "List error from server  - " + statusCode + " ",
//                            Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
//                    Toast.makeText(mContext, "List error from server  - " + statusCode + " " + " ",
//                            Toast.LENGTH_LONG).show();
                }
            });
        }else {
            Util.createNoNetworkDialogue(mContext);
        }
    }
}
