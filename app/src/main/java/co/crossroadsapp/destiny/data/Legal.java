package co.crossroadsapp.destiny.data;

/**
 * Created by sharmha on 11/16/16.
 */
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class Legal {

    private String privacyVersion;
    private String termsVersion;
    private boolean termsNeedsUpdate;
    private boolean privacyNeedsUpdate;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     *
     * @return
     * The privacyVersion
     */
    public String getPrivacyVersion() {
        return privacyVersion;
    }

    /**
     *
     * @param privacyVersion
     * The privacyVersion
     */
    public void setPrivacyVersion(String privacyVersion) {
        this.privacyVersion = privacyVersion;
    }

    /**
     *
     * @return
     * The termsVersion
     */
    public String getTermsVersion() {
        return termsVersion;
    }

    /**
     *
     * @param termsVersion
     * The termsVersion
     */
    public void setTermsVersion(String termsVersion) {
        this.termsVersion = termsVersion;
    }

    /**
     *
     * @return
     * The termsNeedsUpdate
     */
    public boolean isTermsNeedsUpdate() {
        return termsNeedsUpdate;
    }

    /**
     *
     * @param termsNeedsUpdate
     * The termsNeedsUpdate
     */
    public void setTermsNeedsUpdate(boolean termsNeedsUpdate) {
        this.termsNeedsUpdate = termsNeedsUpdate;
    }

    /**
     *
     * @return
     * The privacyNeedsUpdate
     */
    public boolean isPrivacyNeedsUpdate() {
        return privacyNeedsUpdate;
    }

    /**
     *
     * @param privacyNeedsUpdate
     * The privacyNeedsUpdate
     */
    public void setPrivacyNeedsUpdate(boolean privacyNeedsUpdate) {
        this.privacyNeedsUpdate = privacyNeedsUpdate;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
