package com.example.sharmha.travelerfordestiny.data;

import com.example.sharmha.travelerfordestiny.utils.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by sharmha on 2/28/16.
 */
public class EventData {

    private String eventId;
    private String eventStatus;
    private int minPlayer;
    private int maxPlayer;
    private ActivityData activityData;
    private ArrayList<PlayerData> playerDataList;
    private CreatorData creatorData;
    private String launchDate;
    private long _timeInMilliseconds;
    private String launchStatus;
    //private PlayerData playerData;

    public EventData () {
        activityData = new ActivityData();
        playerDataList = new ArrayList<PlayerData>();
        creatorData = new CreatorData();
    }

    public void setEventId(String id) {
        eventId = id;
    }

    public void setTimeInMilliseconds(long timeInMilliseconds) {
        this._timeInMilliseconds = timeInMilliseconds;
    }

    public long getTimeInMilliseconds() {
        ensureTimeInMilliseconds();
        return this._timeInMilliseconds;
    }

    private void ensureTimeInMilliseconds() {
        if (this._timeInMilliseconds == 0) {
            this._timeInMilliseconds = -Util.parseDate(getLaunchDate());
        }
    }

    public String getEventId() {
        return this.eventId;
    }

    public void setLaunchDate(String date){
        if(date!=null){
            this.launchDate = date;
        }
    }

    public String getLaunchDate() {
        return launchDate;
    }

    public void setLaunchEventStatus(String status) {
        launchStatus = status;
    }

    public String getLaunchEventStatus() {
        return this.launchStatus;
    }

    public void setEventStatus(String status) {
        eventStatus = status;
    }

    public String getEventStatus() {
        return this.eventStatus;
    }

    public void setMinPlayer(int min) {
        minPlayer = min;
    }

    public int getMinPlayer() {
        return this.minPlayer;
    }

    public void setMaxPlayer(int max) {
        maxPlayer = max;
    }

    public int getMaxPlayer() {
        return this.maxPlayer;
    }

    public void setCreatorData(CreatorData creator) {
        creatorData = creator;
    }

    public CreatorData getCreatorData() {
        return this.creatorData;
    }

    public void setActivityData(ActivityData aData) {
        activityData = aData;
    }

    public ActivityData getActivityData() {
        return this.activityData;
    }

    public void setPlayerData(PlayerData pData) {
        if (playerDataList != null) {
            playerDataList.add(pData);
        }
    }

    public ArrayList<PlayerData> getPlayerData() {
        return this.playerDataList;
    }

    public void toJson(JSONObject json) {
        try {
            String id = json.getString("_id");
            setEventId(id);
            if (json.has("status")) {
                String status = json.getString("status");
                int minPlayers = json.getInt("minPlayers");
                int maxPlayers = json.getInt("maxPlayers");

                JSONObject actData = json.optJSONObject("eType");
                JSONObject creator = json.optJSONObject("creator");
                JSONArray playerD = json.optJSONArray("players");

                setEventStatus(status);
                setMinPlayer(minPlayers);
                setMaxPlayer(maxPlayers);
                if(json.has("launchDate")){
                    setLaunchDate(json.getString("launchDate"));
                }
                if(json.has("launchStatus")) {
                    setLaunchEventStatus(json.getString("launchStatus"));
                }

                activityData.toJson(actData);
                creatorData.toJson(creator);

                for (int i = 0; i < playerD.length(); i++) {
                    JSONObject jsonobject = playerD.getJSONObject(i);
                    PlayerData pData = new PlayerData();
                    pData.toJson(jsonobject);
                    playerDataList.add(pData);
                }
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
