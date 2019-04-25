package com.storedobject.core;

public final class RawSQL {

    public static boolean debug;
    public static int RUNNING = 0;
    public static int SOQ = 0;
    public static int EOQ = 0;
    public static int COMPILE = 0;
    public static int CLOSED = 0;

    protected RawSQL() {
    }

    protected RawSQL(java.lang.String p1) {
        this();
    }

    protected RawSQL(com.storedobject.core.RawSQL p1) {
        this();
    }

    protected RawSQL(java.lang.String p1, com.storedobject.core.RawSQL p2) {
        this();
    }

    protected void finalize() {
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

    public boolean execute(java.lang.String p1) {
        return false;
    }

    public boolean execute() {
        return false;
    }

    public java.sql.ResultSet getResult() {
        return null;
    }

    public java.lang.String getSQL() {
        return null;
    }

    public boolean eoq() {
        return false;
    }

    public void rollback() {
    }

    public void setAutoCommit(boolean p1) {
    }

    public int executeUpdate(java.lang.String p1) {
        return 0;
    }

    public int executeUpdate() {
        return 0;
    }

    public void commit() {
    }

    public void logWarnings(java.sql.SQLException p1) throws com.storedobject.core.Error_Running_SQL {
    }

    public void setSQL(java.lang.String p1) {
    }

    public void addSQL(java.lang.String p1) {
    }

    public void cancel() {
    }

    public java.sql.Statement getStatement() throws java.sql.SQLException {
        return null;
    }

    public java.sql.DatabaseMetaData getDatabaseMetaData() throws java.lang.Exception {
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

    public java.sql.PreparedStatement prepare(java.lang.String p1) throws java.lang.Exception {
        return null;
    }

    public java.sql.CallableStatement prepareCall(java.lang.String p1) throws java.lang.Exception {
        return null;
    }

    public long getRowCount() {
        return 0;
    }

    public static void setNullConnectionHandler(com.storedobject.core.SQLNullConnectionHandler p1) {
    }

    public static com.storedobject.core.SQLNullConnectionHandler getNullConnectionHandler() {
        return null;
    }
}
