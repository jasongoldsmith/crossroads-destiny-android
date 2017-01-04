package co.crossroadsapp.destiny.data;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Response {

    @SerializedName("destinyAccounts")
    @Expose
    private List<DestinyAccount> destinyAccounts = null;
    @SerializedName("bungieNetUser")
    @Expose
    private BungieNetUser bungieNetUser;
    @SerializedName("clans")
    @Expose
    private List<Clan> clans = null;
    @SerializedName("relatedGroups")
    @Expose
    private RelatedGroups relatedGroups;
    @SerializedName("destinyAccountErrors")
    @Expose
    private List<Object> destinyAccountErrors = null;

    public List<DestinyAccount> getDestinyAccounts() {
        return destinyAccounts;
    }

    public void setDestinyAccounts(List<DestinyAccount> destinyAccounts) {
        this.destinyAccounts = destinyAccounts;
    }

    public BungieNetUser getBungieNetUser() {
        return bungieNetUser;
    }

    public void setBungieNetUser(BungieNetUser bungieNetUser) {
        this.bungieNetUser = bungieNetUser;
    }

    public List<Clan> getClans() {
        return clans;
    }

    public void setClans(List<Clan> clans) {
        this.clans = clans;
    }

    public RelatedGroups getRelatedGroups() {
        return relatedGroups;
    }

    public void setRelatedGroups(RelatedGroups relatedGroups) {
        this.relatedGroups = relatedGroups;
    }

    public List<Object> getDestinyAccountErrors() {
        return destinyAccountErrors;
    }

    public void setDestinyAccountErrors(List<Object> destinyAccountErrors) {
        this.destinyAccountErrors = destinyAccountErrors;
    }

}