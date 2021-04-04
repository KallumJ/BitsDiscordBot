package schedule;

/**
 * A class to model an RSVP to a community event
 */
public class Rsvp {
    private final int id;
    private final int userId;
    private final int eventId;

    /**
     * Constructs Rsvp object
     *
     * @param id      the rsvp id
     * @param userId  the user id
     * @param eventId the event id
     */
    public Rsvp(int id, int userId, int eventId) {
        this.id = id;
        this.userId = userId;
        this.eventId = eventId;
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
     * Returns the user id
     *
     * @return the user id
     */
    public int getUserId() {
        return userId;
    }

    /**
     * Returns the event id
     *
     * @return the event id
     */
    public int getEventId() {
        return eventId;
    }
}
