package co.crossroadsapp.destiny.data;

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
            setPsnId(creator.getString("psnId"));
            setPlayerImageUrl("imageUrl");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
