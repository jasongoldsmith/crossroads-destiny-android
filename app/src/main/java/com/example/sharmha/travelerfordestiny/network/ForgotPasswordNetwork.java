package com.example.sharmha.travelerfordestiny.network;

import android.content.Context;

import com.example.sharmha.travelerfordestiny.ControlManager;
import com.example.sharmha.travelerfordestiny.utils.Util;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Observable;

import cz.msebera.android.httpclient.Header;

/**
 * Created by sharmha on 5/13/16.
 */
public class ForgotPasswordNetwork extends Observable {
    private Context mContext;
    private NetworkEngine ntwrk;
    private String url = "auth/request/resetPassword";
    private String changePswrdUrl = "a/user/updatePassword";
    private ControlManager mManager;

    public ForgotPasswordNetwork(Context c) {
        mContext = c;
        mManager = ControlManager.getmInstance();
        ntwrk = NetworkEngine.getmInstance(c);
    }

    public void doResetPassword(RequestParams params) throws JSONException {
        if (Util.isNetworkAvailable(mContext)) {
            ntwrk.post(url, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    setChanged();
                    notifyObservers();
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

    public void doChangePassword(RequestParams params) throws JSONException {
        if (Util.isNetworkAvailable(mContext)) {
            ntwrk.post(changePswrdUrl, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    setChanged();
                    notifyObservers();
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
