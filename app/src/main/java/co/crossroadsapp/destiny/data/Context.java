package co.crossroadsapp.destiny.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Context {

    @SerializedName("isFollowing")
    @Expose
    private Boolean isFollowing;
    @SerializedName("ignoreStatus")
    @Expose
    private IgnoreStatus ignoreStatus;

    public Boolean getIsFollowing() {
        return isFollowing;
    }

    public void setIsFollowing(Boolean isFollowing) {
        this.isFollowing = isFollowing;
    }

    public IgnoreStatus getIgnoreStatus() {
        return ignoreStatus;
    }

    public void setIgnoreStatus(IgnoreStatus ignoreStatus) {
        this.ignoreStatus = ignoreStatus;
    }

}
