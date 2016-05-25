package com.example.sharmha.travelerfordestiny.network;

import android.content.Context;

import com.example.sharmha.travelerfordestiny.ControlManager;
import com.example.sharmha.travelerfordestiny.data.GroupData;
import com.example.sharmha.travelerfordestiny.data.UserData;
import com.example.sharmha.travelerfordestiny.utils.Util;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Observable;

import cz.msebera.android.httpclient.Header;

/**
 * Created by sharmha on 5/19/16.
 */
public class GroupListNetwork extends Observable {

    private Context mContext;
    private NetworkEngine ntwrk;
    private String url = "a/account/group/list";
    private String selectedGroupUrl = "a/user/updateGroup";
    private ArrayList<GroupData> groupList;

    //private EventList eventList;
    private ControlManager mManager;

    public GroupListNetwork(Context c) {
        //listA = act;
        mContext = c;
        mManager = ControlManager.getmInstance();
        ntwrk = NetworkEngine.getmInstance(c);

        groupList = new ArrayList<GroupData>();
//        if (eventList != null) {
//
//        } else {
//            eventList = new EventList();
//        }
    }

    public void getGroups() throws JSONException {
        if (Util.isNetworkAvailable(mContext)) {
            ntwrk.get(url, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    // If the response is JSONObject instead of expected JSONArray
//                    Toast.makeText(mContext, "List server call Success",
//                            Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    // If the response is JSONObject instead of expected JSONArray
//                    Toast.makeText(mContext, "List server call Success",
//                            Toast.LENGTH_SHORT).show();
                    try {
                        parseGroupList(response);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
//                    Toast.makeText(mContext, "List error from server  - " + statusCode + " ",
//                            Toast.LENGTH_LONG).show();
                    mManager.showErrorDialogue(statusCode + " - server failed");
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
//                        Toast.makeText(mContext, "List error from server  - " + statusCode + " " + errorResponse.getString("message"),
//                                Toast.LENGTH_LONG).show();
                    mManager.showErrorDialogue(Util.getErrorMessage(errorResponse));
                }
            });
        }else {
            Util.createNoNetworkDialogue(mContext);
        }
    }

    private void parseGroupList(JSONArray response) throws JSONException {
        for (int i = 0; i < response.length(); i++) {
            JSONObject jsonobject = response.getJSONObject(i);
            if (jsonobject.length()>0) {
                GroupData eData = new GroupData();
                eData.toJson(jsonobject);
                groupList.add(eData);
            }
        }
        setChanged();
        notifyObservers(this.groupList);
        //listA.updateEventList(this.eventList);
    }

    private void parseUserObj(JSONObject response) {
        UserData user = new UserData();
        //todo debug change
        try {
            String n = response.getString("userName");
//            String p = jsonData.getString("passWord");
            String psnId = response.getString("psnId");
            String profileImg = response.getString("imageUrl");
            String uId = response.getString("_id");
            String clanId = response.getString("clanId");
            String psnVer = response.getString("psnVerified");

            if (n!=null && !n.isEmpty()){
                user.setUser(n);
            }

            user.setPsnId(psnId);
            user.setImageUrl(profileImg);
            user.setUserId(uId);
            user.setClanId(clanId);
            user.setPsnVerify(psnVer);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        //user.toJson(response);
        //user.setAuthenticationId(Constants.LOGIN);
        setChanged();
        notifyObservers(user);
    }

    public void postSelectGroup(RequestParams params) throws JSONException {
        if (Util.isNetworkAvailable(mContext)) {
            ntwrk.post(selectedGroupUrl, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    // If the response is JSONObject instead of expected JSONArray
                    parseUserObj(response);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    // If the response is JSONObject instead of expected JSONArray

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    mManager.showErrorDialogue(statusCode + " - server failed for create event");
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
