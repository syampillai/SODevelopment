package com.storedobject.core;

import com.storedobject.common.ResourceOwner;

import java.sql.*;

public final class RawSQL implements ResourceOwner {

    public static boolean debug;
    public static int RUNNING = 0;
    public static int SOQ = 0;
    public static int EOQ = 0;
    public static int COMPILE = 0;
    public static int CLOSED = 0;

    RawSQL() {
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
        return null;
    }

    public DatabaseMetaData getDatabaseMetaData() throws java.lang.Exception {
        return null;
    }

    public void setAutoNext(boolean p1) throws java.lang.Exception {
    }

    public boolean getAutoNext() {
        return false;
    }

    public int getError() {
        return 0;
    }

    public PreparedStatement prepare(String p1) throws java.lang.Exception {
        return null;
    }

    public CallableStatement prepareCall(String p1) throws java.lang.Exception {
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
}
