package com.example.sharmha.travelerfordestiny.network;

import android.content.Context;
import android.widget.Toast;

import com.example.sharmha.travelerfordestiny.ControlManager;
import com.example.sharmha.travelerfordestiny.utils.Util;
import com.example.sharmha.travelerfordestiny.data.UserData;
import com.example.sharmha.travelerfordestiny.utils.Constants;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Observable;

import cz.msebera.android.httpclient.Header;

/**
 * Created by sharmha on 3/17/16.
 */
public class LoginNetwork extends Observable {

    private Context mContext;
    private NetworkEngine ntwrk;
    private String url = "auth/login";
    private String url_reg = "auth/register";
    private UserData user;
    private ControlManager mManager;

    public LoginNetwork(Context c) {
        mContext = c;
        mManager = ControlManager.getmInstance();
        ntwrk = NetworkEngine.getmInstance(c);
    }

    public void doSignup(RequestParams params) throws JSONException {
        if (Util.isNetworkAvailable(mContext)) {
            ntwrk.post(url, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    //post gcm token
                    postGcm();
                    parseSignInUser(response);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
//                    Toast.makeText(mContext, "Signup error from server  - " + statusCode,
//                            Toast.LENGTH_LONG).show();
                    mManager.showErrorDialogue(Util.getErrorMessage(errorResponse));
                }
            });
        }else {
            Util.createNoNetworkDialogue(mContext);
        }
    }

    public void doRegister(RequestParams params) throws JSONException {
        if (Util.isNetworkAvailable(mContext)) {
            ntwrk.post(url_reg, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                    //post gcm token
                    postGcm();

                    parseSignUpUser(response);
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

    private void postGcm() {
        //post gcm token
        Util.getGCMToken(mContext, mManager);
    }

    private void parseSignInUser(JSONObject response) {
        user = new UserData();
        user.toJson(response);
        user.setAuthenticationId(Constants.LOGIN);
        setChanged();
        notifyObservers(user);
    }

    private void parseSignUpUser(JSONObject response) {
        user = new UserData();
        user.toJson(response);
        user.setAuthenticationId(Constants.REGISTER);
        setChanged();
        notifyObservers(user);
    }
}
