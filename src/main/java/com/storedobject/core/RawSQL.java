package com.storedobject.core;

import com.storedobject.common.ResourceOwner;

import java.io.IOException;
import java.sql.*;
import java.util.Random;

public final class RawSQL implements ResourceOwner {

    public static boolean debug;
    public static int RUNNING = 0;
    public static int SOQ = 0;
    public static int EOQ = 0;
    public static int COMPILE = 0;
    public static int CLOSED = 0;

    RawSQL() {
    }

    RawSQL(String statement) {
    }

    @Override
    public AutoCloseable getResource() {
        return null;
    }

    public boolean next() {
        return false;
    }

    public int getState() {
        return 0;
    }

    public void close() {
    }

    public long skip() {
        return 0;
    }

    public long skip(long p1) {
        return 0;
    }

    public void setError(int p1) {
    }

    public boolean execute(String p1) {
        return false;
    }

    public boolean execute() {
        return false;
    }

    public java.sql.ResultSet getResult() {
        return null;
    }

    public String getSQL() {
        return null;
    }

    public boolean eoq() {
        return false;
    }

    public void rollback() {
    }

    public void setAutoCommit(boolean p1) {
    }

    public int executeUpdate(String p1) {
        return 0;
    }

    public int executeUpdate() {
        return 0;
    }

    public void commit() {
    }

    public void logWarnings(SQLException p1) throws Error_Running_SQL {
    }

    public void setSQL(String p1) {
    }

    public void addSQL(String p1) {
    }

    public void cancel() {
    }

    public Statement getStatement() throws SQLException {
        if(new Random().nextBoolean()) {
            throw new SQLException();
        }
        return null;
    }

    public DatabaseMetaData getDatabaseMetaData() throws java.lang.Exception {
        if(new Random().nextBoolean()) {
            throw new SQLException();
        }
        return null;
    }

    public void setAutoNext(boolean p1) throws java.lang.Exception {
        if(new Random().nextBoolean()) {
            throw new SQLException();
        }
    }

    public boolean getAutoNext() {
        return false;
    }

    public int getError() {
        return 0;
    }

    public PreparedStatement prepare(String p1) throws java.lang.Exception {
        if(new Random().nextBoolean()) {
            throw new SQLException();
        }
        return null;
    }

    public CallableStatement prepareCall(String p1) throws java.lang.Exception {
        if(new Random().nextBoolean()) {
            throw new SQLException();
        }
        return null;
    }

    public long getRowCount() {
        return 0;
    }

    public static void setNullConnectionHandler(SQLNullConnectionHandler p1) {
    }

    public static SQLNullConnectionHandler getNullConnectionHandler() {
        return null;
    }


    /**
     * Dump data of a given database.
     *
     * @param securityPassword Security password.
     * @param sql True if download as plain SQL statements.
     * @param databaseName Database name (if null is passed, current DB is dumped).
     * @return Data dumping process. The {@link java.io.OutputStream} of the process will stream out the data.
     */
    public static Process dumpDatabase(String securityPassword, boolean sql, String databaseName) {
        try {
            return new ProcessBuilder("").start();
        } catch(IOException e) {
            return null;
        }
    }

    /**
     * Restore data to a given database.
     *
     * @param securityPassword Security password.
     * @param databaseName Name of the database to restore.
     * @return Data loading process. The {@link java.io.InputStream} of the process can accept SQL commands for
     * restoring the data.
     */
    public static Process restoreDatabase(String securityPassword, String databaseName) {
        try {
            return new ProcessBuilder("").start();
        } catch(IOException e) {
            return null;
        }
    }
}
