package co.crossroadsapp.destiny.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class IgnoreStatus {

    @SerializedName("isIgnored")
    @Expose
    private Boolean isIgnored;
    @SerializedName("ignoreFlags")
    @Expose
    private Integer ignoreFlags;

    public Boolean getIsIgnored() {
        return isIgnored;
    }

    public void setIsIgnored(Boolean isIgnored) {
        this.isIgnored = isIgnored;
    }

    public Integer getIgnoreFlags() {
        return ignoreFlags;
    }

    public void setIgnoreFlags(Integer ignoreFlags) {
        this.ignoreFlags = ignoreFlags;
    }

}