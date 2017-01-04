package co.crossroadsapp.destiny.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BungieResponseData {

    @SerializedName("Response")
    @Expose
    private Response response;
    @SerializedName("ErrorCode")
    @Expose
    private Integer errorCode;
    @SerializedName("ThrottleSeconds")
    @Expose
    private Integer throttleSeconds;
    @SerializedName("ErrorStatus")
    @Expose
    private String errorStatus;
    @SerializedName("Message")
    @Expose
    private String message;
    @SerializedName("MessageData")
    @Expose
    private MessageData messageData;

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public Integer getThrottleSeconds() {
        return throttleSeconds;
    }

    public void setThrottleSeconds(Integer throttleSeconds) {
        this.throttleSeconds = throttleSeconds;
    }

    public String getErrorStatus() {
        return errorStatus;
    }

    public void setErrorStatus(String errorStatus) {
        this.errorStatus = errorStatus;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public MessageData getMessageData() {
        return messageData;
    }

    public void setMessageData(MessageData messageData) {
        this.messageData = messageData;
    }

}
