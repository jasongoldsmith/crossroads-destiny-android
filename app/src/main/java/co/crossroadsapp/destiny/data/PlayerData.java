package co.crossroadsapp.destiny.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sharmha on 2/28/16.
 */
public class PlayerData {

    private String playerId;
    private String username;
    private String psnId;
    private String playerImageUrl;

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

    public void toJson(JSONObject jsonobject) {
        if (jsonobject!= null) {
            try {
                JSONArray conArray = jsonobject.optJSONArray("consoles");
                JSONObject conData = (JSONObject) conArray.get(0);
//                if(conData.has("consoleType")) {
//                    String cType = conData.getString("consoleType");
//                    setConsoleType(cType);
//                }

                if(conData.has("consoleId")) {
                    String id = conData.getString("consoleId");
                    setPsnId(id);
                }

//                if(conData.has("verifyStatus")){
//                    String verifyS = conData.getString("verifyStatus");
//                    setPsnVerify(verifyS);
//                }
//                if (!jsonobject.isNull("psnId")) {
//                    setPsnId(jsonobject.getString("psnId"));
//                }
                setUsername(jsonobject.getString("userName"));
                setPlayerId(jsonobject.getString("_id"));
                setPlayerImageUrl(jsonobject.getString("imageUrl"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
