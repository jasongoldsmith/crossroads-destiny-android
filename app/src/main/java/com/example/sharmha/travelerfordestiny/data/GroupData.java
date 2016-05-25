package com.example.sharmha.travelerfordestiny.data;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sharmha on 5/19/16.
 */
public class GroupData {

    private String groupId;
    private String groupName;
    private int memberCount;
    private boolean clanEnabled;
    private String groupImageUrl;
    public boolean isSelected;
    private int eventCount;


    public void setGroupId(String id) {
        groupId = id;
    }

    public String getGroupId() {
        return this.groupId;
    }

    public void setGroupName(String name) {
        groupName = name;
    }

    public String getGroupName() {
        return this.groupName;
    }

    public void setEventCount(int count) {
        eventCount = count;
    }

    public int getEventCount() {
        return this.eventCount;
    }

    public void setMemberCount(int count) {
        memberCount = count;
    }

    public int getMemberCount() {
        return this.memberCount;
    }

    public void setGroupImageUrl(String image) {
        groupImageUrl = image;
    }

    public String getGroupImageUrl() {
        return this.groupImageUrl;
    }

    public void setGroupClanEnabled(boolean clan) {
        clanEnabled = clan;
    }

    public boolean getGroupselected() {
        return this.isSelected;
    }

    public void setGroupSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean getGroupClanEnabled() {
        return this.clanEnabled;
    }

    public void toJson(JSONObject jsonobject) {
        if (jsonobject!= null) {
            try {
                if (!jsonobject.isNull("groupId")) {
                    setGroupId(jsonobject.getString("groupId"));
                }
                if (jsonobject.has("groupName")) {
                    setGroupName(jsonobject.getString("groupName"));
                }
                if (jsonobject.has("memberCount")) {
                    setMemberCount(jsonobject.getInt("memberCount"));
                }
                if (jsonobject.has("eventCount")) {
                    setEventCount(jsonobject.getInt("eventCount"));
                }
                if (jsonobject.has("clanEnabled")) {
                    setGroupClanEnabled(jsonobject.getBoolean("clanEnabled"));
                }
                if (jsonobject.has("avatarPath")) {
                    setGroupImageUrl(jsonobject.getString("avatarPath"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
