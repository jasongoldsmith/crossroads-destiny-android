package co.crossroadsapp.destiny.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Race {

    @SerializedName("raceHash")
    @Expose
    private Double raceHash;
    @SerializedName("raceType")
    @Expose
    private Integer raceType;
    @SerializedName("raceName")
    @Expose
    private String raceName;
    @SerializedName("raceNameMale")
    @Expose
    private String raceNameMale;
    @SerializedName("raceNameFemale")
    @Expose
    private String raceNameFemale;
    @SerializedName("raceDescription")
    @Expose
    private String raceDescription;
    @SerializedName("hash")
    @Expose
    private Double hash;
    @SerializedName("index")
    @Expose
    private Integer index;
    @SerializedName("redacted")
    @Expose
    private Boolean redacted;

    public Double getRaceHash() {
        return raceHash;
    }

    public void setRaceHash(Double raceHash) {
        this.raceHash = raceHash;
    }

    public Integer getRaceType() {
        return raceType;
    }

    public void setRaceType(Integer raceType) {
        this.raceType = raceType;
    }

    public String getRaceName() {
        return raceName;
    }

    public void setRaceName(String raceName) {
        this.raceName = raceName;
    }

    public String getRaceNameMale() {
        return raceNameMale;
    }

    public void setRaceNameMale(String raceNameMale) {
        this.raceNameMale = raceNameMale;
    }

    public String getRaceNameFemale() {
        return raceNameFemale;
    }

    public void setRaceNameFemale(String raceNameFemale) {
        this.raceNameFemale = raceNameFemale;
    }

    public String getRaceDescription() {
        return raceDescription;
    }

    public void setRaceDescription(String raceDescription) {
        this.raceDescription = raceDescription;
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