package co.crossroadsapp.destiny.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Character {

    @SerializedName("characterId")
    @Expose
    private String characterId;
    @SerializedName("raceHash")
    @Expose
    private Double raceHash;
    @SerializedName("genderHash")
    @Expose
    private Double genderHash;
    @SerializedName("classHash")
    @Expose
    private Double classHash;
    @SerializedName("emblemHash")
    @Expose
    private Double emblemHash;
    @SerializedName("race")
    @Expose
    private Race race;
    @SerializedName("gender")
    @Expose
    private Gender gender;
    @SerializedName("characterClass")
    @Expose
    private CharacterClass characterClass;
    @SerializedName("emblemPath")
    @Expose
    private String emblemPath;
    @SerializedName("backgroundPath")
    @Expose
    private String backgroundPath;
    @SerializedName("level")
    @Expose
    private Integer level;
    @SerializedName("powerLevel")
    @Expose
    private Integer powerLevel;
    @SerializedName("dateLastPlayed")
    @Expose
    private String dateLastPlayed;
    @SerializedName("membershipId")
    @Expose
    private String membershipId;
    @SerializedName("membershipType")
    @Expose
    private Integer membershipType;
    @SerializedName("levelProgression")
    @Expose
    private LevelProgression levelProgression;
    @SerializedName("isPrestigeLevel")
    @Expose
    private Boolean isPrestigeLevel;
    @SerializedName("genderType")
    @Expose
    private Integer genderType;
    @SerializedName("classType")
    @Expose
    private Integer classType;
    @SerializedName("percentToNextLevel")
    @Expose
    private Double percentToNextLevel;
    @SerializedName("currentActivityHash")
    @Expose
    private Integer currentActivityHash;

    public String getCharacterId() {
        return characterId;
    }

    public void setCharacterId(String characterId) {
        this.characterId = characterId;
    }

    public Double getRaceHash() {
        return raceHash;
    }

    public void setRaceHash(Double raceHash) {
        this.raceHash = raceHash;
    }

    public Double getGenderHash() {
        return genderHash;
    }

    public void setGenderHash(Double genderHash) {
        this.genderHash = genderHash;
    }

    public Double getClassHash() {
        return classHash;
    }

    public void setClassHash(Double classHash) {
        this.classHash = classHash;
    }

    public Double getEmblemHash() {
        return emblemHash;
    }

    public void setEmblemHash(Double emblemHash) {
        this.emblemHash = emblemHash;
    }

    public Race getRace() {
        return race;
    }

    public void setRace(Race race) {
        this.race = race;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public CharacterClass getCharacterClass() {
        return characterClass;
    }

    public void setCharacterClass(CharacterClass characterClass) {
        this.characterClass = characterClass;
    }

    public String getEmblemPath() {
        return emblemPath;
    }

    public void setEmblemPath(String emblemPath) {
        this.emblemPath = emblemPath;
    }

    public String getBackgroundPath() {
        return backgroundPath;
    }

    public void setBackgroundPath(String backgroundPath) {
        this.backgroundPath = backgroundPath;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getPowerLevel() {
        return powerLevel;
    }

    public void setPowerLevel(Integer powerLevel) {
        this.powerLevel = powerLevel;
    }

    public String getDateLastPlayed() {
        return dateLastPlayed;
    }

    public void setDateLastPlayed(String dateLastPlayed) {
        this.dateLastPlayed = dateLastPlayed;
    }

    public String getMembershipId() {
        return membershipId;
    }

    public void setMembershipId(String membershipId) {
        this.membershipId = membershipId;
    }

    public Integer getMembershipType() {
        return membershipType;
    }

    public void setMembershipType(Integer membershipType) {
        this.membershipType = membershipType;
    }

    public LevelProgression getLevelProgression() {
        return levelProgression;
    }

    public void setLevelProgression(LevelProgression levelProgression) {
        this.levelProgression = levelProgression;
    }

    public Boolean getIsPrestigeLevel() {
        return isPrestigeLevel;
    }

    public void setIsPrestigeLevel(Boolean isPrestigeLevel) {
        this.isPrestigeLevel = isPrestigeLevel;
    }

    public Integer getGenderType() {
        return genderType;
    }

    public void setGenderType(Integer genderType) {
        this.genderType = genderType;
    }

    public Integer getClassType() {
        return classType;
    }

    public void setClassType(Integer classType) {
        this.classType = classType;
    }

    public Double getPercentToNextLevel() {
        return percentToNextLevel;
    }

    public void setPercentToNextLevel(Double percentToNextLevel) {
        this.percentToNextLevel = percentToNextLevel;
    }

    public Integer getCurrentActivityHash() {
        return currentActivityHash;
    }

    public void setCurrentActivityHash(Integer currentActivityHash) {
        this.currentActivityHash = currentActivityHash;
    }

}
