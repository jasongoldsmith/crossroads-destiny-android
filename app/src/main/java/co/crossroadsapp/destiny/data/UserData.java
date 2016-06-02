package co.crossroadsapp.destiny.data;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sharmha on 2/23/16.
 */
public class UserData implements Parcelable {

    private String user;
    private String password;
    private String psnId;
    private String imageUrl;
    private String userId;
    private String clanId;
    private String psnVerify;
    private int authenticationId;

    public UserData() {

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

    public void setPsnVerify(String psnVeri) {
        psnVerify = psnVeri;
    }

    public String getPsnVerify() {
        return this.psnVerify;
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

    public void setAuthenticationId(int id) {
        authenticationId = id;
    }

    public int getAuthenticationId() {
        return this.authenticationId;
    }

    public void toJson(JSONObject json) {
        try {
            JSONObject jsonData = json.optJSONObject("value");
            String n = jsonData.getString("userName");
//            String p = jsonData.getString("passWord");
            String psnId = jsonData.getString("psnId");
            String profileImg = jsonData.getString("imageUrl");
            String uId = jsonData.getString("_id");
            String clanId = jsonData.getString("clanId");
            String psnVer = jsonData.getString("psnVerified");

            if (n!=null && !n.isEmpty()){
                setUser(n);
            }

            setPsnId(psnId);
            setImageUrl(profileImg);
            setUserId(uId);
            setClanId(clanId);
            setPsnVerify(psnVer);

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
    }
}
