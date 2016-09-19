package co.crossroadsapp.destiny.network;

import android.content.Context;

import co.crossroadsapp.destiny.ControlManager;
import co.crossroadsapp.destiny.data.EventList;
import co.crossroadsapp.destiny.data.UserData;
import co.crossroadsapp.destiny.utils.Util;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Observable;

import cz.msebera.android.httpclient.Header;

/**
 * Created by sharmha on 3/30/16.
 */
public class LogoutNetwork extends Observable {

    private Context mContext;
    private NetworkEngine ntwrk;
    private String url = "api/v1/auth/logout";
    private UserData user;
    private ControlManager mManager;

    public LogoutNetwork(Context c) {
        mContext = c;
        mManager = ControlManager.getmInstance();
        ntwrk = NetworkEngine.getmInstance(c);
    }

    public void doLogout(RequestParams params) throws JSONException {
        ntwrk.post(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray
                // go to logout page
                Util.clearDefaults(mContext.getApplicationContext());
                mManager.getEventListCurrent().clear();
                setChanged();
                notifyObservers();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                mManager.showErrorDialogue(Util.getErrorMessage(errorResponse));
            }
        });
    }
}
