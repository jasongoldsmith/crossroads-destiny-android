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
            JSONObject conData = (JSONObject) conArray.get(0);
//            if(conData.has("consoleType")) {
//                String cType = conData.getString("consoleType");
//                setConsoleType(cType);
//            }

            if(conData.has("consoleId")) {
                String id = conData.getString("consoleId");
                setPsnId(id);
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
