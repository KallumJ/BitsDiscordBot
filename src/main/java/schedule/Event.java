package schedule;

import java.sql.Date;
import java.sql.Time;

/**
 * A class to model a community event
 */
public class Event {

    private final String name;
    private final Date date;
    private final Time time;
    private int id;

    /**
     * Constructs an Event object
     *
     * @param id   the event id
     * @param name the event name
     * @param date the event date
     * @param time the event time
     */
    public Event(int id, String name, Date date, Time time) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.time = time;
    }

    /**
     * Constructs an Event Object
     *
     * @param name the event name
     * @param date the event date
     * @param time the event time
     */
    public Event(String name, Date date, Time time) {
        this.name = name;
        this.date = date;
        this.time = time;
    }

    /**
     * Returns the name
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the date
     *
     * @return the date
     */
    public Date getDate() {
        return date;
    }

    /**
     * Returns the time
     *
     * @return the time
     */
    public Time getTime() {
        return time;
    }

    /**
     * Returns the id
     *
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the id
     *
     * @param id the id
     */
    public void setId(int id) {
        this.id = id;
    }
}
