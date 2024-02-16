package com.storedobject.core;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public abstract class Database {

	private static Database db;

	public static void login(String driver, String ip, int port, String database, String databaseMaster,
							 String user, String password, String securityLogin) throws Exception {
		db = get();
	}

	public static Database get() {
		return db;
	}

	public static void lockDatabase() {
	}

	public abstract String name();

	public final String securityLogin() {
		return "";
	}

	public abstract String modelDatabase();

	public abstract String userSQL();

	public abstract String databaseSQL(String databaseName);

	public abstract boolean isALogin(RawSQL sql, String user);

	public abstract boolean isALogin(String user, String password);

	public String initialPassword() {
		return "";
	}

	public abstract boolean createLogin(String user, String password);

	public abstract boolean dropLogin(String user, String password);

	protected final boolean changePassword(String user, String oldPassword, String newPassword) {
		return true;
	}

	protected abstract boolean alterPassword(String user, String oldPassword, String newPassword);

	protected abstract boolean resetPassword(String user, String securityPassword, String newPassword);

	public String maxTime() {
		return "";
	}

	public abstract String currentTime();

	public Calendar getCurrentTime() {
		return new GregorianCalendar();
	}

	public String currentTimeSQL() {
		return "";
	}

	public abstract String callProc(String name, String parameter);

	public abstract String getSerial(int tag);

	public abstract String getObjectClassName();

	public static <D extends java.util.Date> String format(D date) {
		return "";
	}

	public static <D extends java.util.Date> String formatWithTime(D date) {
		return "";
	}

	public static DateFormat dateFormat() {
		return new SimpleDateFormat();
	}

	public static DateFormat dateTimeFormat() {
		return new SimpleDateFormat();
	}

	public int nanoDigits() {
		return 6;
	}

	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	public final boolean executeSQL(String sql, String securityPassword) {
		return false;
	}

	protected final boolean executeSQL(String sql, String user, String password) {
		return false;
	}

	protected final boolean executeSQL(String sql, String user, String password, boolean ignoreErrors) {
		return false;
	}

	protected final boolean executeSQL(String[] sql, String user, String password) {
		return false;
	}

	protected final boolean executeSQL(String[] sql, String user, String password, boolean ignoreErrors) {
		return false;
	}

	public final Timestamp maxTimeStamp() {
		return new Timestamp(0);
	}

	protected RawSQL getSQL() {
		return new RawSQL();
	}

	public boolean databaseExists(String databaseName) {
		return true;
	}

	public boolean schemaExists(String schemaName) {
		return false;
	}

	public boolean tableExists(String tableName) {
		return false;
	}

	public boolean viewExists(String viewName) {
		return false;
	}

	public List<String> foreignKeyConstraintNames(String tableName) {
		List<String[]> fkc = foreignKeyConstraints(tableName);
		return fkc.stream().map(s -> s[12]).toList();
	}

	public List<String[]> foreignKeyConstraints(String tableName) {
		return new ArrayList<>();
	}

	public List<String[]> dependentConstraints(String tableName) {
		return new ArrayList<>();
	}

	public abstract List<String> parentTable(String tableName);

	public abstract String columnType(String columnType, int width, int precision);

	public List<String[]> columnDetails(String tableName) {
		return new ArrayList<>();
	}

	protected abstract String dropDatabaseDDL(String databaseName);

	public boolean dropDatabase(String databaseName, String securityPassword) {
		return false;
	}

	protected String createDatabaseDDL(String databaseName) {
		return databaseName;
	}

	public boolean createDatabase(String databaseName, String securityPassword) {
		return false;
	}

	protected abstract String[] createSchemaDDL(String schemaName);

	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	public boolean createSchema(String schemaName, String securityPassword) {
		return false;
	}

	public boolean createTable(Class<? extends StoredObject> objectClass, String securityPassword) throws Exception {
		return false;
	}

	public boolean validateSecurityPassword(String password) throws Exception {
		if(new Random().nextBoolean()) {
			throw new SOException();
		}
		return false;
	}

	public static boolean isMaster() {
		return SQLConnector.database.equals(SQLConnector.databaseMaster);
	}

	public static void ensureMaster() throws SOException {
		if(new Random().nextBoolean()) {
			throw new SOException();
		}
	}
}
