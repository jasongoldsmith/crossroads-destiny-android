package co.crossroadsapp.destiny.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BungieNetUser {

    @SerializedName("membershipId")
    @Expose
    private String membershipId;
    @SerializedName("uniqueName")
    @Expose
    private String uniqueName;
    @SerializedName("displayName")
    @Expose
    private String displayName;
    @SerializedName("profilePicture")
    @Expose
    private Integer profilePicture;
    @SerializedName("profileTheme")
    @Expose
    private Integer profileTheme;
    @SerializedName("userTitle")
    @Expose
    private Integer userTitle;
    @SerializedName("successMessageFlags")
    @Expose
    private String successMessageFlags;
    @SerializedName("isDeleted")
    @Expose
    private Boolean isDeleted;
    @SerializedName("about")
    @Expose
    private String about;
    @SerializedName("firstAccess")
    @Expose
    private String firstAccess;
    @SerializedName("lastUpdate")
    @Expose
    private String lastUpdate;
    @SerializedName("context")
    @Expose
    private Context context;
    @SerializedName("psnDisplayName")
    @Expose
    private String psnDisplayName;
    @SerializedName("xboxDisplayName")
    @Expose
    private String xboxDisplayName;
    @SerializedName("showActivity")
    @Expose
    private Boolean showActivity;
    @SerializedName("locale")
    @Expose
    private String locale;
    @SerializedName("localeInheritDefault")
    @Expose
    private Boolean localeInheritDefault;
    @SerializedName("showGroupMessaging")
    @Expose
    private Boolean showGroupMessaging;
    @SerializedName("profilePicturePath")
    @Expose
    private String profilePicturePath;
    @SerializedName("profilePictureWidePath")
    @Expose
    private String profilePictureWidePath;
    @SerializedName("profileThemeName")
    @Expose
    private String profileThemeName;
    @SerializedName("userTitleDisplay")
    @Expose
    private String userTitleDisplay;
    @SerializedName("statusText")
    @Expose
    private String statusText;
    @SerializedName("statusDate")
    @Expose
    private String statusDate;

    public String getMembershipId() {
        return membershipId;
    }

    public void setMembershipId(String membershipId) {
        this.membershipId = membershipId;
    }

    public String getUniqueName() {
        return uniqueName;
    }

    public void setUniqueName(String uniqueName) {
        this.uniqueName = uniqueName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Integer getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(Integer profilePicture) {
        this.profilePicture = profilePicture;
    }

    public Integer getProfileTheme() {
        return profileTheme;
    }

    public void setProfileTheme(Integer profileTheme) {
        this.profileTheme = profileTheme;
    }

    public Integer getUserTitle() {
        return userTitle;
    }

    public void setUserTitle(Integer userTitle) {
        this.userTitle = userTitle;
    }

    public String getSuccessMessageFlags() {
        return successMessageFlags;
    }

    public void setSuccessMessageFlags(String successMessageFlags) {
        this.successMessageFlags = successMessageFlags;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getFirstAccess() {
        return firstAccess;
    }

    public void setFirstAccess(String firstAccess) {
        this.firstAccess = firstAccess;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getPsnDisplayName() {
        return psnDisplayName;
    }

    public void setPsnDisplayName(String psnDisplayName) {
        this.psnDisplayName = psnDisplayName;
    }

    public String getXboxDisplayName() {
        return xboxDisplayName;
    }

    public void setXboxDisplayName(String xboxDisplayName) {
        this.xboxDisplayName = xboxDisplayName;
    }

    public Boolean getShowActivity() {
        return showActivity;
    }

    public void setShowActivity(Boolean showActivity) {
        this.showActivity = showActivity;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public Boolean getLocaleInheritDefault() {
        return localeInheritDefault;
    }

    public void setLocaleInheritDefault(Boolean localeInheritDefault) {
        this.localeInheritDefault = localeInheritDefault;
    }

    public Boolean getShowGroupMessaging() {
        return showGroupMessaging;
    }

    public void setShowGroupMessaging(Boolean showGroupMessaging) {
        this.showGroupMessaging = showGroupMessaging;
    }

    public String getProfilePicturePath() {
        return profilePicturePath;
    }

    public void setProfilePicturePath(String profilePicturePath) {
        this.profilePicturePath = profilePicturePath;
    }

    public String getProfilePictureWidePath() {
        return profilePictureWidePath;
    }

    public void setProfilePictureWidePath(String profilePictureWidePath) {
        this.profilePictureWidePath = profilePictureWidePath;
    }

    public String getProfileThemeName() {
        return profileThemeName;
    }

    public void setProfileThemeName(String profileThemeName) {
        this.profileThemeName = profileThemeName;
    }

    public String getUserTitleDisplay() {
        return userTitleDisplay;
    }

    public void setUserTitleDisplay(String userTitleDisplay) {
        this.userTitleDisplay = userTitleDisplay;
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    public String getStatusDate() {
        return statusDate;
    }

    public void setStatusDate(String statusDate) {
        this.statusDate = statusDate;
    }

}
