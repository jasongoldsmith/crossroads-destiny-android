package co.crossroadsapp.destiny.data;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DestinyAccount {

    @SerializedName("userInfo")
    @Expose
    private UserInfo userInfo;
    @SerializedName("grimoireScore")
    @Expose
    private Integer grimoireScore;
    @SerializedName("characters")
    @Expose
    private List<Character> characters = null;
    @SerializedName("lastPlayed")
    @Expose
    private String lastPlayed;
    @SerializedName("versions")
    @Expose
    private Integer versions;

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public Integer getGrimoireScore() {
        return grimoireScore;
    }

    public void setGrimoireScore(Integer grimoireScore) {
        this.grimoireScore = grimoireScore;
    }

    public List<Character> getCharacters() {
        return characters;
    }

    public void setCharacters(List<Character> characters) {
        this.characters = characters;
    }

    public String getLastPlayed() {
        return lastPlayed;
    }

    public void setLastPlayed(String lastPlayed) {
        this.lastPlayed = lastPlayed;
    }

    public Integer getVersions() {
        return versions;
    }

    public void setVersions(Integer versions) {
        this.versions = versions;
    }

}
