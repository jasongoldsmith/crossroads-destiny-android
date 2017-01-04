package co.crossroadsapp.destiny.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CharacterClass {

    @SerializedName("classHash")
    @Expose
    private Double classHash;
    @SerializedName("classType")
    @Expose
    private Integer classType;
    @SerializedName("className")
    @Expose
    private String className;
    @SerializedName("classNameMale")
    @Expose
    private String classNameMale;
    @SerializedName("classNameFemale")
    @Expose
    private String classNameFemale;
    @SerializedName("classIdentifier")
    @Expose
    private String classIdentifier;
    @SerializedName("mentorVendorIdentifier")
    @Expose
    private String mentorVendorIdentifier;
    @SerializedName("hash")
    @Expose
    private Double hash;
    @SerializedName("index")
    @Expose
    private Integer index;
    @SerializedName("redacted")
    @Expose
    private Boolean redacted;

    public Double getClassHash() {
        return classHash;
    }

    public void setClassHash(Double classHash) {
        this.classHash = classHash;
    }

    public Integer getClassType() {
        return classType;
    }

    public void setClassType(Integer classType) {
        this.classType = classType;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassNameMale() {
        return classNameMale;
    }

    public void setClassNameMale(String classNameMale) {
        this.classNameMale = classNameMale;
    }

    public String getClassNameFemale() {
        return classNameFemale;
    }

    public void setClassNameFemale(String classNameFemale) {
        this.classNameFemale = classNameFemale;
    }

    public String getClassIdentifier() {
        return classIdentifier;
    }

    public void setClassIdentifier(String classIdentifier) {
        this.classIdentifier = classIdentifier;
    }

    public String getMentorVendorIdentifier() {
        return mentorVendorIdentifier;
    }

    public void setMentorVendorIdentifier(String mentorVendorIdentifier) {
        this.mentorVendorIdentifier = mentorVendorIdentifier;
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
