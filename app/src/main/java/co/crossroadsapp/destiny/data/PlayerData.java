package co.crossroadsapp.destiny.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import co.crossroadsapp.destiny.utils.Constants;

/**
 * Created by sharmha on 2/28/16.
 */
public class PlayerData {

    private String playerId;
    private String username;
    private String psnId;
    private String playerImageUrl;
    private String playerClanTag;
    private boolean maxReported = false;
    private int commentsReported;
    private String psnVerify=null;

    public void setPlayerId(String id) {
        playerId = id;
    }

    public String getPlayerId() {
        return this.playerId;
    }

    public void setUsername(String name) {
        username = name;
    }

    public String getUsername() {
        return this.username;
    }

    public void setPsnId(String psnid) {
        psnId = psnid;
    }

    public String getPsnId() {
        return this.psnId;
    }

    public void setPlayerImageUrl(String image) {
        playerImageUrl = image;
    }

    public String getPlayerImageUrl() {
        return this.playerImageUrl;
    }

    public void setClanTag(String clanT) {
        playerClanTag = clanT;
    }

    public String getClanTag() {
        if(psnVerify!=null && psnVerify.equalsIgnoreCase(Constants.PSN_VERIFIED)) {
            return playerClanTag;
        }
        return null;
    }

    public void setCommentsReported(int num) {
        commentsReported = num;
    }

    public int getCommentsReported() {
        return  this.commentsReported;
    }

    public void setMaxReported(boolean maxReport) {
        maxReported = maxReport;
    }

    public boolean getMaxReported() {
        return this.maxReported;
    }

    public void setPsnVerify(String psnVeri) {
        this.psnVerify = psnVeri;
    }

    public String getPsnVerify() {
        return psnVerify;
    }

    public void toJson(JSONObject jsonobject) {
        if (jsonobject!= null) {
            try {
                if(jsonobject.has("consoles") && !jsonobject.isNull("consoles")) {
                    JSONArray conArray = jsonobject.optJSONArray("consoles");
//                if(conData.has("consoleType")) {
//                    String cType = conData.getString("consoleType");
//                    setConsoleType(cType);
//                }
                    if (conArray != null) {
                        for (int i = 0; i < conArray.length(); i++) {
                            JSONObject conData = (JSONObject) conArray.get(i);
                            if (conData.has("isPrimary") && !conData.isNull("isPrimary")) {
                                if (conData.getBoolean("isPrimary")) {
                                    if (conData.has("consoleId") && !conData.isNull("consoleId")) {
                                        String id = conData.getString("consoleId");
                                        setPsnId(id);
                                    }
                                    if (conData.has("clanTag") && !conData.isNull("clanTag")) {
                                        String clanTag = conData.getString("clanTag");
                                        setClanTag(clanTag);
                                    }
                                }
                            }
                        }
                    }
                }

//                if(conData.has("verifyStatus")){
//                    String verifyS = conData.getString("verifyStatus");
//                    setPsnVerify(verifyS);
//                }
//                if (!jsonobject.isNull("psnId")) {
//                    setPsnId(jsonobject.getString("psnId"));
//                }
                if(jsonobject.has("userName") && !jsonobject.isNull("userName")) {
                    setUsername(jsonobject.getString("userName"));
                }
                if(jsonobject.has("_id") && !jsonobject.isNull("_id")) {
                    setPlayerId(jsonobject.getString("_id"));
                }
                if(jsonobject.has("imageUrl") && !jsonobject.isNull("imageUrl")) {
                    setPlayerImageUrl(jsonobject.getString("imageUrl"));
                }
                if (jsonobject.has("commentsReported") && !jsonobject.isNull("commentsReported")) {
                    int num = jsonobject.getInt("commentsReported");
                    setCommentsReported(num);
                }
                if (jsonobject.has("verifyStatus") && !jsonobject.isNull("verifyStatus")) {
                    String verifyS = jsonobject.getString("verifyStatus");
                    setPsnVerify(verifyS);
                }
                if (jsonobject.has("hasReachedMaxReportedComments") && !jsonobject.isNull("hasReachedMaxReportedComments")) {
                    boolean maxRepo = jsonobject.getBoolean("hasReachedMaxReportedComments");
                    setMaxReported(maxRepo);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
