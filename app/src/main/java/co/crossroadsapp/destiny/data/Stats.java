package co.crossroadsapp.destiny.data;

/**
 * Created by sharmha on 11/16/16.
 */
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class Stats {

    private int eventsFull;
    private int eventsLeft;
    private int eventsJoined;
    private int eventsCreated;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     *
     * @return
     * The eventsFull
     */
    public int getEventsFull() {
        return eventsFull;
    }

    /**
     *
     * @param eventsFull
     * The eventsFull
     */
    public void setEventsFull(int eventsFull) {
        this.eventsFull = eventsFull;
    }

    /**
     *
     * @return
     * The eventsLeft
     */
    public int getEventsLeft() {
        return eventsLeft;
    }

    /**
     *
     * @param eventsLeft
     * The eventsLeft
     */
    public void setEventsLeft(int eventsLeft) {
        this.eventsLeft = eventsLeft;
    }

    /**
     *
     * @return
     * The eventsJoined
     */
    public int getEventsJoined() {
        return eventsJoined;
    }

    /**
     *
     * @param eventsJoined
     * The eventsJoined
     */
    public void setEventsJoined(int eventsJoined) {
        this.eventsJoined = eventsJoined;
    }

    /**
     *
     * @return
     * The eventsCreated
     */
    public int getEventsCreated() {
        return eventsCreated;
    }

    /**
     *
     * @param eventsCreated
     * The eventsCreated
     */
    public void setEventsCreated(int eventsCreated) {
        this.eventsCreated = eventsCreated;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
