package co.crossroadsapp.destiny.data;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sharmha on 11/18/16.
 */
public class ValidateUserRequest {
    String consoleType;
    Object bungieResponse;
    String bungieURL;

    public void setConsoleType(String console) {
        consoleType = console;
    }

    public String getConsoleType() {
        return this.consoleType;
    }

    public void setBungieResponse(Object value) {
        bungieResponse = value;
    }

    public Object getBungieResponse() {
        return this.bungieResponse;
    }

    public void setBungieURL(String url) {
        bungieURL = url;
    }

    public String getBungieURL() {
        return this.bungieURL;
    }
}
