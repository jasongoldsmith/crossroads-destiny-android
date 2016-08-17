package co.crossroadsapp.destiny.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sharmha on 2/29/16.
 */
public class CreatorData extends PlayerData {
    public CreatorData() {
        super();
    }

    public void toJson(JSONObject creator) {
        try {
            setPlayerId(creator.getString("_id"));
            setUsername(creator.getString("userName"));
            JSONArray conArray = creator.optJSONArray("consoles");
            if(conArray!=null) {
                for (int i=0;i<conArray.length();i++) {
                    JSONObject conData = (JSONObject) conArray.get(i);
                    if(conData.has("isPrimary")) {
                        if(conData.getBoolean("isPrimary")){
                            if(conData.has("consoleId")) {
                                String id = conData.getString("consoleId");
                                setPsnId(id);
                            }
                            if(conData.has("clanTag")) {
                                String clanTag = conData.getString("clanTag");
                                setClanTag(clanTag);
                            }
                        }
                    }
                }
            }

//            if(conData.has("verifyStatus")){
//                String verifyS = conData.getString("verifyStatus");
//                setPsnVerify(verifyS);
//            }

            //setPsnId(creator.getString("psnId"));
            setPlayerImageUrl("imageUrl");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
