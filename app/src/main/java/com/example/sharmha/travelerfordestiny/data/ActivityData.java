package com.example.sharmha.travelerfordestiny.data;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sharmha on 2/28/16.
 */
public class ActivityData {

    private String id;
    private String activityType;
    private String activitySubtype;
    private int activityLight;
    private int minPlayer;
    private int maxPlayer;
    private String activityCheckpoint;
    private String activityDifficulty;
    private String activityIconUrl;
    private String activityLevel;

    public void setId(String aId) {
        id = aId;
    }

    public String getId() {
        return this.id;
    }

    public void setActivityType(String aType) {
        activityType = aType;
    }

    public String getActivityType() {
        return this.activityType;
    }

    public void setActivitySubtype(String aSubtype) {
        activitySubtype = aSubtype;
    }

    public String getActivitySubtype() {
        return this.activitySubtype;
    }

    public void setActivityLight(int aLight) {
        activityLight = aLight;
    }

    public int getActivityLight() {
        return this.activityLight;
    }

    public void setActivityCheckpoint(String aCheckpoint) {
        activityCheckpoint = aCheckpoint;
    }

    public String getActivityCheckpoint() {
        return this.activityCheckpoint;
    }

    public void setActivityIconUrl(String aIconUrl) {
        activityIconUrl = aIconUrl;
    }

    public String getActivityIconUrl() {
        return this.activityIconUrl;
    }

    public void setActivityDifficulty(String aDifficulty) {
        activityDifficulty = aDifficulty;
    }

    public String getActivityDifficulty() {
        return this.activityDifficulty;
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

    public void setActivityLevel(String level) {
        activityLevel = level;
    }

    public String getActivityLevel() {
        return this.activityLevel;
    }

    public void toJson(JSONObject actData) {
        try {
            setId(actData.getString("_id"));
            setActivityType(actData.getString("aType"));
            setActivitySubtype(actData.getString("aSubType"));
            setMinPlayer(actData.getInt("minPlayers"));
            setMaxPlayer(actData.getInt("maxPlayers"));
            setActivityCheckpoint(actData.getString("aCheckpoint"));
            setActivityDifficulty(actData.getString("aDifficulty"));
            if (!actData.isNull("aIconUrl")) {
                setActivityIconUrl(actData.getString("aIconUrl"));
            } else {
                setActivityIconUrl(null);
            }
            setActivityLight(actData.getInt("aLight"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
