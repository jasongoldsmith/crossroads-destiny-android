package co.crossroadsapp.destiny.data;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sharmha on 11/18/16.
 */
public class ValidateUserRequest {
    Map<String, String> console;
    Map<String, JSONObject> rp;
    Map<String, String> url;

    public void setConsole(String key, String value) {
        console  = new HashMap<>();
        console.put(key, value);
    }

    public void setRp(String key, JSONObject value) {
        rp  = new HashMap<>();
        rp.put(key, value);
    }

    public void setUrl(String key, String value) {
        url = new HashMap<>();
        url.put(key, value);
    }

    public Map<String, String> getConsole() {
        return this.console;
    }

    public Map<String, JSONObject> getRp() {
        return this.rp;
    }

    public Map<String, String> getUrl() {
        return this.url;
    }
}
