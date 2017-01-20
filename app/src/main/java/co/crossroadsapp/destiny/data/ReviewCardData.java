package co.crossroadsapp.destiny.data;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sharmha on 1/11/17.
 */
public class ReviewCardData {
    String mId = null;
    String mName = null;
    String mImageUrl = null;
    String mCardText = null;
    String mButtonText = null;

    public void setmId(String id) {
        this.mId = id;
    }

    public String getmId() {
        return mId;
    }

    public void setmName(String name) {
        this.mName = name;
    }

    public String getmName() {
        return mName;
    }

    public void setmImageUrl(String image) {
        this.mImageUrl = image;
    }

    public String getmImageUrl() {
        return mImageUrl;
    }

    public void setmCardText(String cardText) {
        this.mCardText = cardText;
    }

    public String getmCardText() {
        return mCardText;
    }

    public void setmButtonText(String buttonText) {
        this.mButtonText = buttonText;
    }

    public String getmButtonText() {
        return mButtonText;
    }

    public void toJson(JSONObject object) {
        try {
            if(object.has("_id") && !object.isNull("_id")) {
                setmId(object.getString("_id"));
            }
            if(object.has("name") && !object.isNull("name")) {
                setmName(object.getString("name"));
            }
            if(object.has("imageUrl") && !object.isNull("imageUrl")) {
                setmImageUrl(object.getString("imageUrl"));
            }
            if(object.has("cardText") && !object.isNull("cardText")) {
                setmCardText(object.getString("cardText"));
            }
            if(object.has("buttonText") && !object.isNull("buttonText")) {
                setmButtonText(object.getString("buttonText"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

