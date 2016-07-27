package co.crossroadsapp.destiny.data;

/**
 * Created by sharmha on 7/26/16.
 */
public class PushNotification {
    private String eId=null;
    private String eUpdated=null;
    private boolean typeMessage=false;
    private String eName =null;
    private String message=null;

    public void seteId(String id) {
        eId = id;
    }

    public String geteId() {
        return this.eId;
    }

    public void seteUpdated(String update) {
        eUpdated = update;
    }

    public String geteUpdated() {
        return this.eUpdated;
    }

    public void seteName(String name) {
        eName = name;
    }

    public String geteName() {
        return this.eName;
    }

    public void setMessage(String msg) {
        message = msg;
    }

    public String getMessage() {
        return this.message;
    }

    public void setTypeMessage(boolean tMsg) {
        typeMessage = tMsg;
    }

    public boolean getTypeMessage() {
        return this.typeMessage;
    }
}
