package co.crossroadsapp.destiny.network;

import android.content.Context;

import co.crossroadsapp.destiny.CrashReport;
import co.crossroadsapp.destiny.utils.Util;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Observable;
import java.util.Observer;

import cz.msebera.android.httpclient.Header;

/**
 * Created by sharmha on 4/1/16.
 */
public class ReportCrashNetwork extends Observable {

    private NetworkEngine ntwrk;
    private String url = "api/v1/a/report/create";
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
                    setChanged();
                    notifyObservers();
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
                    if(mContext!=null) {
                        ((CrashReport)mContext).showError(Util.getErrorMessage(errorResponse));
                    }
                }
            });
        }else {
            Util.createNoNetworkDialogue(mContext);
        }
    }
}
