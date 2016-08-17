package co.crossroadsapp.destiny.data;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by sharmha on 2/23/16.
 */
public class UserData implements Parcelable {

    private LegalData legal;
    private String user=null;
    private String password=null;
    private String psnId=null;
    private String imageUrl=null;
    private String userId=null;
    private String clanId=null;
    private String psnVerify=null;
    private String membershipId=null;
    private String consoleType=null;
    private int authenticationId;
    private ArrayList<ConsoleData> consoles;
    private String clanTag=null;

    public UserData() {
        consoles = new ArrayList<ConsoleData>();
        legal = new LegalData();
    }

    public void setUser(String name) {
        if (name!=null && !name.isEmpty()) {
            this.user = name;
        }
    }

    public void setPassword(String pswd) {
        if (pswd!=null && !pswd.isEmpty()) {
            this.password = pswd;
        }
    }

    public void setLegal(LegalData lData) {
        this.legal = lData;
    }

    public LegalData getLegal() {
        return legal;
    }

    public void setPsnVerify(String psnVeri) {
        this.psnVerify = psnVeri;
    }

    public String getPsnVerify() {
        return psnVerify;
    }

    public void setMembershipId(String memId) {
        this.membershipId = memId;
    }

    public String getMembershipId() {
        return membershipId;
    }

    public void setConsoleType(String cType) {
        this.consoleType = cType;
    }

    public String getConsoleType() {
        return consoleType;
    }

    public void setClanId(String id) {
        clanId = id;
    }

    public String getClanId() {
        return this.clanId;
    }

    public void setUserId(String id) {
        userId = id;
    }

    public String getUserId() {
        return this.userId;
    }

    public String getUser() {
        return this.user;
    }

    public String getPswd() {
        return this.password;
    }

    public void setPsnId(String id) {
        psnId = id;
    }

    public String getPsnId() {
        return this.psnId;
    }

    public void setImageUrl(String img) {
        imageUrl = img;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }

    public void setClanTag(String clanT) {
        clanTag = clanT;
    }

    public String getClanTag() {
        return this.clanTag;
    }

    public void setAuthenticationId(int id) {
        authenticationId = id;
    }

    public int getAuthenticationId() {
        return this.authenticationId;
    }

    public ArrayList<ConsoleData> getConsoles() {
        return this.consoles;
    }

    public void toJson(JSONObject json) {
        try {
            if (json.has("value")) {
                JSONObject jsonData = json.optJSONObject("value");
                if (jsonData!=null) {
                    if (jsonData.has("userName")) {
                        String n = jsonData.getString("userName");
                        setUser(n);
                    }
                if (jsonData.has("bungieMemberShipId")) {
                    String memid = jsonData.getString("bungieMemberShipId");
                    setMembershipId(memid);
                }

                if (jsonData.has("clanId")) {
                    String clanId = jsonData.getString("clanId");
                    setClanId(clanId);
                }
                if (jsonData.has("imageUrl")) {
                    String profileImg = jsonData.getString("imageUrl");
                    setImageUrl(profileImg);
                }
                if (jsonData.has("_id")) {
                    String uId = jsonData.getString("_id");
                    setUserId(uId);
                }
                    if(jsonData.has("legal")) {
                        JSONObject legalData = jsonData.optJSONObject("legal");
                        legal.toJson(legalData);
                    }
                    if (jsonData.has("consoles")) {
                    JSONArray conArray = jsonData.optJSONArray("consoles");
                    if (conArray != null) {
                        for (int i=0; i<conArray.length();i++) {
                        JSONObject conData = (JSONObject) conArray.get(i);
                        if (conData != null) {
                            ConsoleData cData = new ConsoleData();
                            if(conData.has("isPrimary")) {
                                cData.setPrimary(conData.getBoolean("isPrimary"));
                            }
                            if (conData.has("consoleType")) {
                                String cType = conData.getString("consoleType");
                                if(cData.getPrimary()) {
                                    setConsoleType(cType);
                                }
                                cData.setcType(cType);
                            }
                            if (conData.has("consoleId")) {
                                String id = conData.getString("consoleId");
                                if(cData.getPrimary()) {
                                    setPsnId(id);
                                }
                                cData.setcId(id);
                            }
                            if (conData.has("verifyStatus")) {
                                String verifyS = conData.getString("verifyStatus");
                                if(cData.getPrimary()) {
                                    setPsnVerify(verifyS);
                                }
                                cData.setVerifyStatus(verifyS);
                            }
                            if (conData.has("clanTag")) {
                                String clanTag = conData.getString("clanTag");
                                if(cData.getPrimary()) {
                                    setClanTag(clanTag);
                                }
                                cData.setClanTag(clanTag);
                            }
                            consoles.add(cData);
                        }
                        }
                    }
                }
            }
        }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static final Creator<UserData> CREATOR = new Creator<UserData>() {
        @Override
        public UserData createFromParcel(Parcel in) {
            return new UserData(in);
        }

        @Override
        public UserData[] newArray(int size) {
            return new UserData[size];
        }
    };

    public UserData(Parcel in){
        this.user = in.readString();
        this.password = in.readString();
        this.psnId = in.readString();
        this.imageUrl = in.readString();
        this.userId = in.readString();
        this.clanId = in.readString();
        this.psnVerify = in.readString();
        this.consoleType = in.readString();
        this.membershipId = in.readString();
        this.authenticationId = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.user);
        dest.writeString(this.password);
        dest.writeString(this.psnId);
        dest.writeString(this.imageUrl);
        dest.writeString(this.userId);
        dest.writeString(this.clanId);
        dest.writeString(this.psnVerify);
        dest.writeString(this.consoleType);
        dest.writeString(this.membershipId);
        dest.writeInt(this.authenticationId);
    }
}
