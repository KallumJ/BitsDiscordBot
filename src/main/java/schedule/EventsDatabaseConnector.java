package schedule;

import com.mysql.cj.jdbc.MysqlDataSource;
import main.Main;
import net.dv8tion.jda.api.entities.Member;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * A class to model a connection to the community events database
 */
public class EventsDatabaseConnector {

    private static final String URL = Main.getProperties().getProperty("sqlEventsHostname");
    private static final String USERNAME = Main.getProperties().getProperty("sqlUsername");
    private static final String PASSWORD = Main.getProperties().getProperty("sqlPassword");

    private final Connection databaseConnection;

    /**
     * Constructs an EventsDatabaseConnector
     */
    public EventsDatabaseConnector() {
        try {
            // Creates connection to the database
            MysqlDataSource dataSource = new MysqlDataSource();
            dataSource.setUser(USERNAME);
            dataSource.setPassword(PASSWORD);
            dataSource.setURL(URL);

            this.databaseConnection = dataSource.getConnection();
        } catch (SQLException ex) {
            throw new RuntimeException("Unable to create connection to database.", ex);
        }
    }

    /**
     * Query the database with the provided query
     *
     * @param query The query
     * @return ResultSet of what the database returned
     */
    private ResultSet queryDatabase(String query) {
        try {
            Statement statement = databaseConnection.createStatement();
            return statement.executeQuery(query);
        } catch (SQLException ex) {
            throw new RuntimeException("Unable to perform query " + query + " on database", ex);
        }
    }

    /**
     * Returns a list of all events on the database
     *
     * @return a List of Event objects
     */
    public List<Event> getAgenda() {
        ResultSet eventsSqlSet = queryDatabase("SELECT * FROM event");
        LinkedList<Event> eventList = new LinkedList<>();
        try {
            while (eventsSqlSet.next()) {
                int id = eventsSqlSet.getInt("event_ID");
                String name = eventsSqlSet.getString("event_Name");
                Time time = eventsSqlSet.getTime("event_Time");
                Date date = eventsSqlSet.getDate("event_Date");

                Event event = new Event(id, name, date, time);
                eventList.add(event);
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Unable to generate list of events from database.", ex);
        }
        return eventList;
    }

    /**
     * Schedules the provided event
     *
     * @param eventObj the Event object to schedule
     * @return true if successfully scheduled event, false otherwise
     */
    public boolean scheduleEvent(Event eventObj) {
        try {
            String query = "INSERT INTO event (event_ID, event_Name, event_Date, event_Time) values (?, ?, ?, ?)";

            PreparedStatement preparedStatement = databaseConnection.prepareStatement(query);
            preparedStatement.setInt(1, computeEventID());
            preparedStatement.setString(2, eventObj.getName());
            preparedStatement.setDate(3, eventObj.getDate());
            preparedStatement.setTime(4, eventObj.getTime());

            preparedStatement.execute();

            return true;
        } catch (SQLException ex) {
            return false;
        }
    }

    /**
     * Computes an event id
     *
     * @return the computed id
     */
    private int computeEventID() {
        return mostRecentIdInTable("event") + 1;
    }

    /**
     * Computes an user id
     *
     * @return the computed id
     */
    private int computeUserID() {
        return mostRecentIdInTable("user") + 1;
    }

    /**
     * Computes an rsvp id
     *
     * @return the computed id
     */
    private int computeRSVPID() {
        return mostRecentIdInTable("rsvp") + 1;
    }

    /**
     * Gets the most recent id in the provided table
     *
     * @param table the table to check
     * @return the most recent id in the table
     */
    private int mostRecentIdInTable(String table) {
        ResultSet tableSqlSet = queryDatabase("SELECT * FROM " + table);
        int id = 0;
        try {
            while (tableSqlSet.next()) {
                // If the current id is greater than the previous biggest id, update the biggest id
                if (tableSqlSet.getInt(table + "_ID") > id) {
                    id = tableSqlSet.getInt(table + "_ID");
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Unable to query records from " + table, ex);
        }
        return id;
    }

    /**
     * Return the corresponding Event object for the provided string
     *
     * @param eventStr the name of the event to return
     * @return an Event object
     */
    public Event getEventFromName(String eventStr) {
        List<Event> events = getAgenda();

        for (Event event : events) {
            if (event.getName().equalsIgnoreCase(eventStr)) {
                return event;
            }
        }
        return null;
    }

    /**
     * Rsvps the provided user to the provided event
     *
     * @param member the user
     * @param event  the event
     * @return true if successfully rsvped, false otherwise
     */
    public boolean rsvpUser(Member member, Event event) {
        registerUserIfNew(member);

        try {
            int userId = getUserIdFromMember(member);
            int eventId = event.getId();

            if (!isUserRsvped(userId, eventId)) {

                String query = "INSERT INTO rsvp (rsvp_ID, event_id, user_id) values (?, ?, ?)";

                PreparedStatement preparedStatement = databaseConnection.prepareStatement(query);
                preparedStatement.setInt(1, computeRSVPID());
                preparedStatement.setInt(2, eventId);
                preparedStatement.setInt(3, userId);

                preparedStatement.execute();

                return true;
            }
            return true; // No failure occurred, we just dont need to record them again.
        } catch (SQLException | NullPointerException ex) {
            return false;
        }
    }

    /**
     * Checks whether the provided user is rsvped to the provided event
     *
     * @param userId  the user's id
     * @param eventId the event's id
     * @return true if the user is rsvped, false otherwise
     */
    private boolean isUserRsvped(int userId, int eventId) {
        List<Rsvp> allRSVPs = getAllRsvps();

        for (Rsvp rsvp : allRSVPs) {
            if (rsvp.getEventId() == eventId && rsvp.getUserId() == userId) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns all the rsvps in the table
     *
     * @return List of RSVP objects
     */
    private List<Rsvp> getAllRsvps() {
        ResultSet rsvpSqlSet = queryDatabase("SELECT * FROM rsvp");
        LinkedList<Rsvp> rsvpList = new LinkedList<>();
        try {
            while (rsvpSqlSet.next()) {
                int rsvpId = rsvpSqlSet.getInt("rsvp_ID");
                int userId = rsvpSqlSet.getInt("user_id");
                int eventId = rsvpSqlSet.getInt("event_id");

                rsvpList.add(new Rsvp(rsvpId, userId, eventId));
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Unable to generate list of events from database.", ex);
        }
        return rsvpList;
    }

    /**
     * Returns the user id for the provided Discord Member
     *
     * @param member the member
     * @return the user id, -1 indicates an error.
     */
    private int getUserIdFromMember(Member member) {
        List<User> allUsers = getAllUsers();

        for (User user : allUsers) {
            if (user.getUsername().equals(member.getUser().getName())) {
                return user.getId();
            }
        }
        return -1;
    }

    /**
     * Registers the provided Discord member if they are not already in the table
     *
     * @param member the member to register
     */
    private void registerUserIfNew(Member member) {

        if (!isUserRegistered(member)) {
            try {
                String query = "INSERT INTO user (user_ID, username) values (?, ?)";

                PreparedStatement preparedStatement = databaseConnection.prepareStatement(query);
                preparedStatement.setInt(1, computeUserID());
                preparedStatement.setString(2, member.getUser().getName());

                preparedStatement.execute();
            } catch (SQLException ex) {
                throw new RuntimeException("Unable to register user " + member.getUser().getName(), ex);
            }
        }

    }

    /**
     * Checks whether the provided member is already registered in the users table
     *
     * @param member The member
     * @return true if the user is already registered, false otherwise
     */
    private boolean isUserRegistered(Member member) {
        List<User> allUsers = getAllUsers();

        for (User user : allUsers) {
            if (user.getUsername().equals(member.getUser().getName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a List of User objects for all the users in the table
     *
     * @return List of User objects
     */
    private List<User> getAllUsers() {
        ResultSet userSqlSet = queryDatabase("SELECT * FROM user");
        LinkedList<User> userList = new LinkedList<>();
        try {
            while (userSqlSet.next()) {
                int userId = userSqlSet.getInt("user_ID");
                String username = userSqlSet.getString("username");

                userList.add(new User(userId, username));
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Unable to generate list of events from database.", ex);
        }
        return userList;
    }

    /**
     * Get RSVP for the provided user id and event id
     *
     * @param userId  the user id
     * @param eventId the event id
     * @return the found RSVP object, null if none is found
     */
    private Rsvp getRsvp(int userId, int eventId) {
        List<Rsvp> allRsvps = getAllRsvps();

        for (Rsvp rsvp : allRsvps) {
            if (rsvp.getUserId() == userId && rsvp.getEventId() == eventId) {
                return rsvp;
            }
        }
        return null;
    }

    /**
     * Unrsvps the provided member from the provided event
     *
     * @param member The member
     * @param event  The event
     * @return true if successfully unrsvped the member, false otherwise
     */
    public boolean unRsvpUser(Member member, Event event) {
        try {
            int userId = getUserIdFromMember(member);
            int eventId = event.getId();

            String query = "DELETE FROM rsvp WHERE rsvp_ID = " + Objects.requireNonNull(getRsvp(userId, eventId)).getId();

            PreparedStatement preparedStatement = databaseConnection.prepareStatement(query);
            preparedStatement.execute();

            return true;
        } catch (SQLException | NullPointerException ex) {
            return false;
        }
    }

    /**
     * Returns a list of user objects for all users attending the provided event
     *
     * @param eventObj the event to gather attendees for
     * @return List of User objects of attendees
     */
    public List<User> getAttendeesForEvent(Event eventObj) {
        List<Rsvp> allRsvps = getAllRsvps();

        List<User> attendees = new LinkedList<>();
        if (eventObj != null) {

            for (Rsvp rsvp : allRsvps) {
                if (rsvp.getEventId() == eventObj.getId()) {
                    attendees.add(getUserFromId(rsvp.getUserId()));
                }
            }
        }
        return attendees;
    }

    /**
     * Gets the User object for the provided id
     *
     * @param userId the user id
     * @return the User object of the provided user
     */
    private User getUserFromId(int userId) {
        List<User> allUsers = getAllUsers();

        for (User user : allUsers) {
            if (user.getId() == userId) {
                return user;
            }
        }
        return null;
    }

    /**
     * Deletes the provided event from the events table
     *
     * @param event the event to delete
     * @return true if successfully deleted, false otherwise
     */
    public boolean unscheduleEvent(Event event) {
        try {
            int eventId = event.getId();

            String rsvpDeleteQuery = "DELETE FROM rsvp WHERE event_ID = " + eventId;
            PreparedStatement rsvpDeleteStatement = databaseConnection.prepareStatement(rsvpDeleteQuery);
            rsvpDeleteStatement.execute();

            String eventDeleteQuery = "DELETE FROM event WHERE event_ID = " + eventId;
            PreparedStatement eventDeleteStatement = databaseConnection.prepareStatement(eventDeleteQuery);
            eventDeleteStatement.execute();

            return true;
        } catch (SQLException | NullPointerException ex) {
            return false;
        }
    }
}
