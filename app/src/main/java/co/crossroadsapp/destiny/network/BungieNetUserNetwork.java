package co.crossroadsapp.destiny.network;

import android.content.Context;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.shaded.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Observable;

import co.crossroadsapp.destiny.ControlManager;
import co.crossroadsapp.destiny.utils.Constants;
import co.crossroadsapp.destiny.utils.Util;
import cz.msebera.android.httpclient.Header;

/**
 * Created by sharmha on 3/13/17.
 */

public class BungieNetUserNetwork extends Observable {

    private Context mContext;
    private NetworkEngine ntwrk;

    private ControlManager mManager;

    public BungieNetUserNetwork(Context c) {
        mContext = c;
        mManager = ControlManager.getmInstance();
        ntwrk = NetworkEngine.getmInstance(c);

    }

    public void postNetUser(RequestParams rp) {
        if(rp!=null) {
            if (Util.isNetworkAvailable(mContext)) {
                ntwrk.post(Constants.BUNGIE_CURRENT_USER, rp, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        //try {
                            //if (response != null && response.has("ErrorStatus") && response.getString("ErrorStatus").equalsIgnoreCase("Success")) {
                                //set cookie validation
                                Util.setDefaults(Constants.USER_EMAIL_SEND, "true", mContext);
                            //}
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        //set cookie validation
                        //Util.setDefaults(Constants.COOKIE_VALID_KEY, "false", mContext);
                        mManager.showErrorDialogue(Util.getErrorMessage(errorResponse));
                    }
                });
            } else {
                Util.createNoNetworkDialogue(mContext);
            }
        }
    }

    public void getBungieNetUser() throws JSONException {
        if (Util.isNetworkAvailable(mContext)) {
            ntwrk.get(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        if(response!=null && response.has("ErrorStatus") && response.getString("ErrorStatus").equalsIgnoreCase("Success")) {
                            try {
                                HashMap<String,Object> map =
                                        new ObjectMapper().readValue(response.toString(), HashMap.class);
                                RequestParams rp = new RequestParams();
                                rp.put("bungieResponse", map);
                                setChanged();
                                notifyObservers(rp);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
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
                    //set cookie validation
                    //Util.setDefaults(Constants.COOKIE_VALID_KEY, "false", mContext);
                    mManager.showErrorDialogue(Util.getErrorMessage(errorResponse));
                }
            });
        }else {
            Util.createNoNetworkDialogue(mContext);
        }
    }
}
