package co.crossroadsapp.destiny.data;

/**
 * Created by sharmha on 11/16/16.
 */
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class Value {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("uDate")
    @Expose
    private String uDate;
    @SerializedName("mpDistinctId")
    @Expose
    private String mpDistinctId;
    @SerializedName("bungieMemberShipId")
    @Expose
    private String bungieMemberShipId;
    @SerializedName("imageUrl")
    @Expose
    private String imageUrl;
    @SerializedName("clanName")
    @Expose
    private String clanName;
    @SerializedName("__v")
    @Expose
    private Integer v;
    @SerializedName("clanImageUrl")
    @Expose
    private String clanImageUrl;
    @SerializedName("isInvited")
    @Expose
    private Boolean isInvited;
    @SerializedName("mpDistinctIdRefreshed")
    @Expose
    private Boolean mpDistinctIdRefreshed;
    @SerializedName("stats")
    @Expose
    private Stats stats;
    @SerializedName("legal")
    @Expose
    private Legal legal;
    @SerializedName("hasReachedMaxReportedComments")
    @Expose
    private Boolean hasReachedMaxReportedComments;
    @SerializedName("commentsReported")
    @Expose
    private Integer commentsReported;
    @SerializedName("notifStatus")
    @Expose
    private List<Object> notifStatus = null;
    @SerializedName("isLoggedIn")
    @Expose
    private Boolean isLoggedIn;
    @SerializedName("lastActiveTime")
    @Expose
    private String lastActiveTime;
    @SerializedName("groups")
    @Expose
    private List<Group> groups = null;
    @SerializedName("clanId")
    @Expose
    private String clanId;
    @SerializedName("consoles")
    @Expose
    private List<Console> consoles = null;
    @SerializedName("verifyStatus")
    @Expose
    private String verifyStatus;

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
     * The date
     */
    public String getDate() {
        return date;
    }

    /**
     *
     * @param date
     * The date
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     *
     * @return
     * The uDate
     */
    public String getUDate() {
        return uDate;
    }

    /**
     *
     * @param uDate
     * The uDate
     */
    public void setUDate(String uDate) {
        this.uDate = uDate;
    }

    /**
     *
     * @return
     * The mpDistinctId
     */
    public String getMpDistinctId() {
        return mpDistinctId;
    }

    /**
     *
     * @param mpDistinctId
     * The mpDistinctId
     */
    public void setMpDistinctId(String mpDistinctId) {
        this.mpDistinctId = mpDistinctId;
    }

    /**
     *
     * @return
     * The bungieMemberShipId
     */
    public String getBungieMemberShipId() {
        return bungieMemberShipId;
    }

    /**
     *
     * @param bungieMemberShipId
     * The bungieMemberShipId
     */
    public void setBungieMemberShipId(String bungieMemberShipId) {
        this.bungieMemberShipId = bungieMemberShipId;
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
     * The clanName
     */
    public String getClanName() {
        return clanName;
    }

    /**
     *
     * @param clanName
     * The clanName
     */
    public void setClanName(String clanName) {
        this.clanName = clanName;
    }

    /**
     *
     * @return
     * The v
     */
    public int getV() {
        return v;
    }

    /**
     *
     * @param v
     * The __v
     */
    public void setV(int v) {
        this.v = v;
    }

    /**
     *
     * @return
     * The clanImageUrl
     */
    public String getClanImageUrl() {
        return clanImageUrl;
    }

    /**
     *
     * @param clanImageUrl
     * The clanImageUrl
     */
    public void setClanImageUrl(String clanImageUrl) {
        this.clanImageUrl = clanImageUrl;
    }

    /**
     *
     * @return
     * The isInvited
     */
    public boolean isIsInvited() {
        return isInvited;
    }

    /**
     *
     * @param isInvited
     * The isInvited
     */
    public void setIsInvited(boolean isInvited) {
        this.isInvited = isInvited;
    }

    /**
     *
     * @return
     * The mpDistinctIdRefreshed
     */
    public boolean isMpDistinctIdRefreshed() {
        return mpDistinctIdRefreshed;
    }

    /**
     *
     * @param mpDistinctIdRefreshed
     * The mpDistinctIdRefreshed
     */
    public void setMpDistinctIdRefreshed(boolean mpDistinctIdRefreshed) {
        this.mpDistinctIdRefreshed = mpDistinctIdRefreshed;
    }

    /**
     *
     * @return
     * The stats
     */
    public Stats getStats() {
        return stats;
    }

    /**
     *
     * @param stats
     * The stats
     */
    public void setStats(Stats stats) {
        this.stats = stats;
    }

    /**
     *
     * @return
     * The legal
     */
    public Legal getLegal() {
        return legal;
    }

    /**
     *
     * @param legal
     * The legal
     */
    public void setLegal(Legal legal) {
        this.legal = legal;
    }

    /**
     *
     * @return
     * The hasReachedMaxReportedComments
     */
    public boolean isHasReachedMaxReportedComments() {
        return hasReachedMaxReportedComments;
    }

    /**
     *
     * @param hasReachedMaxReportedComments
     * The hasReachedMaxReportedComments
     */
    public void setHasReachedMaxReportedComments(boolean hasReachedMaxReportedComments) {
        this.hasReachedMaxReportedComments = hasReachedMaxReportedComments;
    }

    /**
     *
     * @return
     * The commentsReported
     */
    public int getCommentsReported() {
        return commentsReported;
    }

    /**
     *
     * @param commentsReported
     * The commentsReported
     */
    public void setCommentsReported(int commentsReported) {
        this.commentsReported = commentsReported;
    }

    /**
     *
     * @return
     * The notifStatus
     */
    public List<Object> getNotifStatus() {
        return notifStatus;
    }

    /**
     *
     * @param notifStatus
     * The notifStatus
     */
    public void setNotifStatus(List<Object> notifStatus) {
        this.notifStatus = notifStatus;
    }

    /**
     *
     * @return
     * The isLoggedIn
     */
    public boolean isIsLoggedIn() {
        return isLoggedIn;
    }

    /**
     *
     * @param isLoggedIn
     * The isLoggedIn
     */
    public void setIsLoggedIn(boolean isLoggedIn) {
        this.isLoggedIn = isLoggedIn;
    }

    /**
     *
     * @return
     * The lastActiveTime
     */
    public String getLastActiveTime() {
        return lastActiveTime;
    }

    /**
     *
     * @param lastActiveTime
     * The lastActiveTime
     */
    public void setLastActiveTime(String lastActiveTime) {
        this.lastActiveTime = lastActiveTime;
    }

    /**
     *
     * @return
     * The groups
     */
    public List<Group> getGroups() {
        return groups;
    }

    /**
     *
     * @param groups
     * The groups
     */
    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    /**
     *
     * @return
     * The clanId
     */
    public String getClanId() {
        return clanId;
    }

    /**
     *
     * @param clanId
     * The clanId
     */
    public void setClanId(String clanId) {
        this.clanId = clanId;
    }

    /**
     *
     * @return
     * The consoles
     */
    public List<Console> getConsoles() {
        return consoles;
    }

    /**
     *
     * @param consoles
     * The consoles
     */
    public void setConsoles(List<Console> consoles) {
        this.consoles = consoles;
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

//    public Map<String, Object> getAdditionalProperties() {
//        return this.additionalProperties;
//    }
//
//    public void setAdditionalProperty(String name, Object value) {
//        this.additionalProperties.put(name, value);
//    }

    public String getPrimaryConsoleType() {
        if(consoles!=null){
            for(int n=0; n<consoles.size(); n++) {
                if(consoles.get(n).isIsPrimary()) {
                    return consoles.get(n).getConsoleId();
                }
            }
        }
        return null;
    }

    public String getPrimaryConsoleId() {
        if(consoles!=null) {
            for (int n = 0; n < consoles.size(); n++) {
                if (consoles.get(n).isIsPrimary()) {
                    return consoles.get(n).getConsoleId();
                }
            }
        }
        return null;
    }

}
