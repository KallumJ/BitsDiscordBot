package schedule;

/**
 * A class to model a user for the community events system
 */
public class User {
    private final int id;
    private final String username;

    /**
     * Constructs User object
     *
     * @param id       the id
     * @param username the username
     */
    public User(int id, String username) {
        this.id = id;
        this.username = username;
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
     * Returns the username
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }
}
