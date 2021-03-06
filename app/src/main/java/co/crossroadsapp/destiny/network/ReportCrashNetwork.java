package co.crossroadsapp.destiny.network;

import android.content.Context;

import co.crossroadsapp.destiny.ControlManager;
import co.crossroadsapp.destiny.CrashReport;
import co.crossroadsapp.destiny.utils.Constants;
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

    private final ControlManager mManager;
    private NetworkEngine ntwrk;
    private String url = Constants.SIGNED_CRASH_REPORT_URL;
    private String urlUnsigned = Constants.UNSIGNED_CRASH_REPORT_URL;
    private Context mContext;

    public ReportCrashNetwork(Context c) {
        mContext = c;
        mManager = ControlManager.getmInstance();
        ntwrk = NetworkEngine.getmInstance(c);
    }

    public void doCrashReport(RequestParams params, int type) throws JSONException {
        if (Util.isNetworkAvailable(mContext)) {
            String localUrl = urlUnsigned;
            if(mManager!=null && mManager.getUserData()!=null && mManager.getUserData().getUserId()!=null) {
                localUrl = url;
            }
            if(type == Constants.REPORT_COMMENT_NEXT) {
                localUrl = Constants.REPORT_COMMENT_URL;
            }
            ntwrk.post(localUrl, params, new JsonHttpResponseHandler() {
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
                    mManager.showErrorDialogue(Util.getErrorMessage(errorResponse));
                }
            });
        }else {
            Util.createNoNetworkDialogue(mContext);
        }
    }
}
