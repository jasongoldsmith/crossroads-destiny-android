package co.crossroadsapp.destiny.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Gender {

    @SerializedName("genderHash")
    @Expose
    private Double genderHash;
    @SerializedName("genderType")
    @Expose
    private Integer genderType;
    @SerializedName("genderName")
    @Expose
    private String genderName;
    @SerializedName("genderDescription")
    @Expose
    private String genderDescription;
    @SerializedName("hash")
    @Expose
    private Double hash;
    @SerializedName("index")
    @Expose
    private Integer index;
    @SerializedName("redacted")
    @Expose
    private Boolean redacted;

    public Double getGenderHash() {
        return genderHash;
    }

    public void setGenderHash(Double genderHash) {
        this.genderHash = genderHash;
    }

    public Integer getGenderType() {
        return genderType;
    }

    public void setGenderType(Integer genderType) {
        this.genderType = genderType;
    }

    public String getGenderName() {
        return genderName;
    }

    public void setGenderName(String genderName) {
        this.genderName = genderName;
    }

    public String getGenderDescription() {
        return genderDescription;
    }

    public void setGenderDescription(String genderDescription) {
        this.genderDescription = genderDescription;
    }

    public Double getHash() {
        return hash;
    }

    public void setHash(Double hash) {
        this.hash = hash;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public Boolean getRedacted() {
        return redacted;
    }

    public void setRedacted(Boolean redacted) {
        this.redacted = redacted;
    }

}
