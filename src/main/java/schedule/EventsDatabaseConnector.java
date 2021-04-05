package schedule;

import com.mysql.cj.jdbc.MysqlDataSource;
import main.Main;
import net.dv8tion.jda.api.entities.Member;
import util.TextUtils;

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
     * Returns a list of all events on the database
     *
     * @return a List of Event objects
     */
    public List<Event> getAgenda() {
        LinkedList<Event> eventList = new LinkedList<>();
        try {
            PreparedStatement preparedStatement = databaseConnection.prepareStatement("SELECT * FROM event");
            ResultSet eventsSqlSet = preparedStatement.executeQuery();
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
            String query = "INSERT INTO event (event_Name, event_Date, event_Time) values (?, ?, ?)";

            PreparedStatement preparedStatement = databaseConnection.prepareStatement(query);
            preparedStatement.setString(1, eventObj.getName());
            preparedStatement.setDate(2, eventObj.getDate());
            preparedStatement.setTime(3, eventObj.getTime());

            preparedStatement.execute();

            return true;
        } catch (SQLException ex) {
            return false;
        }
    }

    /**
     * Return the corresponding Event object for the provided string
     *
     * @param eventStr the name of the event to return
     * @return an Event object
     */
    public Event getEventFromName(String eventStr) {
        try {
            PreparedStatement preparedStatement = databaseConnection.prepareStatement("SELECT * FROM event WHERE event_Name=?");
            preparedStatement.setString(1, TextUtils.capitaliseEachWord(eventStr));
            ResultSet relevantSqlSet = preparedStatement.executeQuery();

            if (relevantSqlSet.next()) {
                return new Event(relevantSqlSet.getInt("event_ID"),
                        relevantSqlSet.getString("event_Name"),
                        relevantSqlSet.getDate("event_Date"),
                        relevantSqlSet.getTime("event_Time"));
            } else {
                return null;
            }
        } catch (SQLException ex) {
            return null;
        }
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

                String query = "INSERT INTO rsvp (event_id, user_id) values (?, ?)";

                PreparedStatement preparedStatement = databaseConnection.prepareStatement(query);
                preparedStatement.setInt(1, eventId);
                preparedStatement.setInt(2, userId);

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
        return getRsvp(userId, eventId) != null;
    }

    /**
     * Returns the user id for the provided Discord Member
     *
     * @param member the member
     * @return the user id
     */
    private int getUserIdFromMember(Member member) {
        try {
            PreparedStatement preparedStatement = databaseConnection.prepareStatement("SELECT * FROM user where username=?");
            preparedStatement.setString(1, member.getUser().getName());
            ResultSet relevantSqlSet = preparedStatement.executeQuery();

            if (relevantSqlSet.next()) {
                return relevantSqlSet.getInt("user_ID");
            } else {
                throw new RuntimeException("Failed to get the user id from the member");
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to get the user id from the member", ex);
        }
    }

    /**
     * Registers the provided Discord member if they are not already in the table
     *
     * @param member the member to register
     */
    private void registerUserIfNew(Member member) {

        if (!isUserRegistered(member)) {
            try {
                String query = "INSERT INTO user (username) values (?)";

                PreparedStatement preparedStatement = databaseConnection.prepareStatement(query);
                preparedStatement.setString(1, member.getUser().getName());

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
        try {
            PreparedStatement preparedStatement = databaseConnection.prepareStatement("SELECT * FROM user where username=?");
            preparedStatement.setString(1, member.getUser().getName());
            ResultSet relevantSqlSet = preparedStatement.executeQuery();

            return relevantSqlSet.next();
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to get the user id from the member", ex);
        }
    }

    /**
     * Get RSVP for the provided user id and event id
     *
     * @param userId  the user id
     * @param eventId the event id
     * @return the found RSVP object, null if none is found
     */
    private Rsvp getRsvp(int userId, int eventId) {
        try {
            PreparedStatement preparedStatement = databaseConnection.prepareStatement("SELECT * FROM rsvp WHERE user_id=? AND event_id=?");
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, eventId);
            ResultSet relevantRsvpSqlSet = preparedStatement.executeQuery();

            if (relevantRsvpSqlSet.next()) {
                return new Rsvp(relevantRsvpSqlSet.getInt("rsvp_ID"), relevantRsvpSqlSet.getInt("user_id"), relevantRsvpSqlSet.getInt("event_id"));
            }
            return null;
        } catch (SQLException ex) {
            return null;
        }
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

            PreparedStatement preparedStatement = databaseConnection.prepareStatement("DELETE FROM rsvp WHERE rsvp_ID=?");
            preparedStatement.setInt(1, Objects.requireNonNull(getRsvp(userId, eventId)).getId());
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
        List<User> attendees = new LinkedList<>();

        if (eventObj != null) {
            try {
                PreparedStatement preparedStatement = databaseConnection.prepareStatement("SELECT * FROM rsvp WHERE event_id=?");
                preparedStatement.setInt(1, eventObj.getId());
                ResultSet relevantSqlSet = preparedStatement.executeQuery();

                while (relevantSqlSet.next()) {
                    attendees.add(getUserFromId(relevantSqlSet.getInt("user_id")));
                }
            } catch (SQLException ex) {
                return attendees;
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
        try {
            PreparedStatement preparedStatement = databaseConnection.prepareStatement("SELECT * FROM user WHERE user_ID=?");
            preparedStatement.setInt(1, userId);
            ResultSet relevantSqlSet = preparedStatement.executeQuery();

            if (relevantSqlSet.next()) {
                return new User(relevantSqlSet.getInt("user_ID"), relevantSqlSet.getString("username"));
            }
            return null;
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to get user from the provided id", ex);
        }
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

            String rsvpDeleteQuery = "DELETE FROM rsvp WHERE event_ID=?";
            PreparedStatement rsvpDeleteStatement = databaseConnection.prepareStatement(rsvpDeleteQuery);
            rsvpDeleteStatement.setInt(1, eventId);
            rsvpDeleteStatement.execute();

            String eventDeleteQuery = "DELETE FROM event WHERE event_ID=?";
            PreparedStatement eventDeleteStatement = databaseConnection.prepareStatement(eventDeleteQuery);
            eventDeleteStatement.setInt(1, eventId);
            eventDeleteStatement.execute();

            return true;
        } catch (SQLException | NullPointerException ex) {
            return false;
        }
    }
}
