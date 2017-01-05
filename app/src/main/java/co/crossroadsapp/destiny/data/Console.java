package co.crossroadsapp.destiny.data;

/**
 * Created by sharmha on 11/16/16.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class Console {

    @SerializedName("clanTag")
    @Expose
    private String clanTag;
    @SerializedName("destinyMembershipId")
    @Expose
    private String destinyMembershipId;
    @SerializedName("consoleType")
    @Expose
    private String consoleType;
    @SerializedName("consoleId")
    @Expose
    private String consoleId;
    @SerializedName("imageUrl")
    @Expose
    private String imageUrl;
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("isPrimary")
    @Expose
    private Boolean isPrimary;
    @SerializedName("verifyStatus")
    @Expose
    private String verifyStatus;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     *
     * @return
     * The clanTag
     */
    public String getClanTag() {
        return clanTag;
    }

    /**
     *
     * @param clanTag
     * The clanTag
     */
    public void setClanTag(String clanTag) {
        this.clanTag = clanTag;
    }

    /**
     *
     * @return
     * The destinyMembershipId
     */
    public String getDestinyMembershipId() {
        return destinyMembershipId;
    }

    /**
     *
     * @param destinyMembershipId
     * The destinyMembershipId
     */
    public void setDestinyMembershipId(String destinyMembershipId) {
        this.destinyMembershipId = destinyMembershipId;
    }

    /**
     *
     * @return
     * The consoleType
     */
    public String getConsoleType() {
        return consoleType;
    }

    /**
     *
     * @param consoleType
     * The consoleType
     */
    public void setConsoleType(String consoleType) {
        this.consoleType = consoleType;
    }

    /**
     *
     * @return
     * The consoleId
     */
    public String getConsoleId() {
        return consoleId;
    }

    /**
     *
     * @param consoleId
     * The consoleId
     */
    public void setConsoleId(String consoleId) {
        this.consoleId = consoleId;
    }

    /**
     *
     * @return
     * The imageUrl
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     *
     * @param imageUrl
     * The imageUrl
     */
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    /**
     *
     * @return
     * The id
     */
    public String getId() {
        return id;
    }

    /**
     *
     * @param id
     * The _id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     *
     * @return
     * The isPrimary
     */
    public boolean isIsPrimary() {
        return isPrimary;
    }

    /**
     *
     * @param isPrimary
     * The isPrimary
     */
    public void setIsPrimary(boolean isPrimary) {
        this.isPrimary = isPrimary;
    }

    /**
     *
     * @return
     * The verifyStatus
     */
    public String getVerifyStatus() {
        return verifyStatus;
    }

    /**
     *
     * @param verifyStatus
     * The verifyStatus
     */
    public void setVerifyStatus(String verifyStatus) {
        this.verifyStatus = verifyStatus;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
