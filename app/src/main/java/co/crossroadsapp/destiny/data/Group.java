package co.crossroadsapp.destiny.data;

/**
 * Created by sharmha on 11/16/16.
 */
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class Group {

    private boolean muteNotification;
    private String groupId;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     *
     * @return
     * The muteNotification
     */
    public boolean isMuteNotification() {
        return muteNotification;
    }

    /**
     *
     * @param muteNotification
     * The muteNotification
     */
    public void setMuteNotification(boolean muteNotification) {
        this.muteNotification = muteNotification;
    }

    /**
     *
     * @return
     * The groupId
     */
    public String getGroupId() {
        return groupId;
    }

    /**
     *
     * @param groupId
     * The groupId
     */
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
