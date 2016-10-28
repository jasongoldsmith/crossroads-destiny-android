package co.crossroadsapp.destiny.network;

import android.content.Context;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Observable;

import co.crossroadsapp.destiny.ControlManager;
import co.crossroadsapp.destiny.utils.Util;
import cz.msebera.android.httpclient.Header;

/**
 * Created by sharmha on 10/26/16.
 */
public class BungieUserNetwork extends Observable {

    private Context mContext;
    private NetworkEngine ntwrk;

    private ControlManager mManager;

    public BungieUserNetwork(String csrf, String cookies, Context c) {
        mContext = c;
        mManager = ControlManager.getmInstance();
        ntwrk = NetworkEngine.getmInstance(c);
        ntwrk.updateBugnieBaseUrlAndHeader(csrf, cookies);
    }

    public void getBungieCurrentUser() throws JSONException {
        if (Util.isNetworkAvailable(mContext)) {
            ntwrk.get(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    setChanged();
                    notifyObservers(response);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {

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
