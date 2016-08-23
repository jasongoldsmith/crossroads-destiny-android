package co.crossroadsapp.destiny.data;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sharmha on 8/3/16.
 */
public class LegalData {
    private String privacyVersion = null;
    private String termsVersion = null;
    private boolean termsNeedsUpdate = false;
    private boolean privacyNeedsUpdate = false;

    public void setPrivacyVersion(String pver) {
        this.privacyVersion = pver;
    }

    public String getPrivacyVersion() {
        return privacyVersion;
    }

    public void setTermsVersion(String tver) {
        this.termsVersion = tver;
    }

    public String getTermsVersion() {
        return termsVersion;
    }

    public void setTermsNeedsUpdate(boolean tUpdate) {
        this.termsNeedsUpdate = tUpdate;
    }

    public boolean getTermsNeedsUpdate() {
        return termsNeedsUpdate;
    }

    public void setPrivacyNeedsUpdate(boolean pUpdate) {
        this.privacyNeedsUpdate = pUpdate;
    }

    public boolean getPrivacyNeedsUpdate() {
        return privacyNeedsUpdate;
    }

    public void toJson(JSONObject json) {
        try {
            if(json!=null) {
                if(json.has("privacyVersion") && !json.isNull("privacyVersion")) {
                    setPrivacyVersion(json.getString("privacyVersion"));
                }
                if(json.has("termsVersion") && !json.isNull("termsVersion")) {
                    setTermsVersion(json.getString("termsVersion"));
                }
                if(json.has("termsNeedsUpdate") && !json.isNull("termsNeedsUpdate")) {
                    setTermsNeedsUpdate(json.getBoolean("termsNeedsUpdate"));
                }
                if(json.has("privacyNeedsUpdate") && !json.isNull("privacyNeedsUpdate")) {
                    setPrivacyNeedsUpdate(json.getBoolean("privacyNeedsUpdate"));
                }
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
