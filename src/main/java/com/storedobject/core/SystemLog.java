package com.storedobject.core;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * This class can be used for logging adhoc information. This is not a "data" class. Also, information logged via this
 * class is permanent (means, it will not be rolled back even if your transaction is failed and rolled back).
 *
 * @author Syam
 */
public class SystemLog {

    private static final RawSQL sql = new RawSQL();
    static {
        sql.setAutoCommit(true);
    }
    private final Timestamp loggedAt;
    private final String message;

    /**
     * Constructs a SystemLog with the specified timestamp and message.
     *
     * @param loggedAt the timestamp when the log entry was created
     * @param message  the log message
     */
    SystemLog(Timestamp loggedAt, String message) {
        this.loggedAt = loggedAt;
        this.message = message;
    }

    /**
     * Gets the timestamp when the log entry was created.
     *
     * @return the timestamp of logging.
     */
    public Timestamp getLoggedAt() {
        return loggedAt;
    }

    /**
     * Retrieves the message associated with this log entry.
     *
     * @return the message of the log entry.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Logs a message into the core.SystemLog table with the specified name.
     * Both the name and message parameters are required and cannot be null.
     * The name is processed to be uppercase and trimmed to remove leading and trailing whitespace before being logged.
     * This method guarantees that the log operation is thread-safe.
     *
     * @param name the name associated with the log entry, processed to uppercase and trimmed
     * @param message the message to be logged, with any single quotes properly escaped
     */
    public static void log(String name, String message) {
        if(name == null || message == null) {
            return;
        }
        synchronized (sql) {
            sql.execute("INSERT INTO core.SystemLog(Name,Log) VALUES('"
                    + name.toUpperCase().trim().replace("'", "''") + "','"
                    + message.replace("'", "''") + "')");
        }
    }

    /**
     * Retrieves a list of system logs based on the provided name and condition.
     *
     * @param name The name associated with the logs to be retrieved.
     * @param condition An optional SQL condition to filter the logs.
     * @param limit Number of entries to return.
     * @return A list of SystemLog instances matching the given name and condition.
     */
    public static List<SystemLog> list(String name, String condition, int limit) {
        if(limit <= 0) {
            limit = 50;
        }
        List<SystemLog> list = new ArrayList<>();
        if(name != null && !name.isEmpty()) {
            name = name.toUpperCase().trim().replace("'", "''");
            synchronized (sql) {
                RawSQL.debug = true;
                sql.execute("SELECT LoggedAt,Log FROM core.SystemLog WHERE Name='" + name + "'"
                        + (condition == null ? "" : (" AND (" + condition + ")"))
                        + " ORDER BY Name,LoggedAt LIMIT " + limit);
                RawSQL.debug = false;
                if(!sql.eoq()) {
                    ResultSet rs = sql.getResult();
                    while(!sql.eoq()) {
                        try {
                            list.add(new SystemLog(rs.getTimestamp(1), rs.getString(2)));
                        } catch (SQLException ignored) {
                        }
                        sql.skip();
                    }
                }
            }
        }
        return list;
    }

    /**
     * Retrieves a list of system logs based on the provided name within the specified timestamp range.
     *
     * @param name the name associated with the system logs
     * @param from the starting timestamp for filtering the logs
     * @param to the ending timestamp for filtering the logs
     * @param limit Number of entries to return.
     * @return a list of system logs matching the specified name and timestamp range
     */
    public static List<SystemLog> list(String name, Timestamp from, Timestamp to, int limit) {
        return list(name, "LoggedAt BETWEEN '" + Database.formatWithTime(from) + "' AND '"
                + Database.formatWithTime(to) + "'", limit);
    }

    /**
     * Retrieves a list of SystemLog entries filtered by the specified name and starting from a given timestamp.
     *
     * @param name The name of the log entries to retrieve.
     * @param from The starting timestamp for log entries to be retrieved.
     * @param limit Number of entries to return.
     * @return A list of SystemLog entries matching the specified name and timestamp conditions.
     */
    public static List<SystemLog> list(String name, Timestamp from, int limit) {
        return list(name, "LoggedAt >= '" + Database.formatWithTime(from) + "'", limit);
    }

    /**
     * Retrieves a list of SystemLog entries filtered by the specified name and starting from a given timestamp.
     *
     * @param name The name of the log entries to retrieve.
     * @param period The period for which log entries to be retrieved.
     * @param limit Number of entries to return.
     * @return A list of SystemLog entries matching the specified name and timestamp conditions.
     */
    public static List<SystemLog> list(String name, TimestampPeriod period, int limit) {
        return list(name, period.getFrom(), period.getTo(), limit);
    }
}
