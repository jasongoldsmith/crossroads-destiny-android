package com.example.sharmha.travelerfordestiny.data;

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
    //private PlayerData playerData;

    public EventData () {
        activityData = new ActivityData();
        playerDataList = new ArrayList<PlayerData>();
        creatorData = new CreatorData();
    }

    public void setEventId(String id) {
        eventId = id;
    }

    public String getEventId() {
        return this.eventId;
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
