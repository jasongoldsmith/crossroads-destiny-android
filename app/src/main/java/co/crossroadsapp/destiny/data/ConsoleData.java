package co.crossroadsapp.destiny.data;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sharmha on 6/3/16.
 */
public class ConsoleData {
    private String cType= null;
    private String cId = null;
    private String membershipId = null;
    private String verifyStatus = null;

    public void setcId(String id) {
        this.cId = id;
    }

    public String getcId() {
        return cId;
    }

    public void setcType(String type) {
        this.cType = type;
    }

    public String getcType() {
        return cType;
    }

    public void setMembershipId(String mId) {
        this.membershipId = mId;
    }

    public String getMembershipId() {
        return membershipId;
    }

    public void setVerifyStatus(String status) {
        this.verifyStatus = status;
    }

    public String getVerifyStatus() {
        return verifyStatus;
    }

}
