package com.storedobject.core;

import java.util.ArrayList;
import java.util.List;

public abstract class SQLConnector {

	public static boolean debug;

	/**
	 * The name of the database. This value may be set by the derived class.
	 */
	protected static String database;
	/**
	 * The name of the database master. This value may be set by the derived class. This could be same as database.
	 */
	protected static String databaseMaster;

	private SQLConnector() {
	}

	/**
	 * Gets the name of the database.
	 *
	 * @return The database name.
	 */
	public static String getDatabaseName() {
		return database;
	}

	/**
	 * Gets the name of the database master.
	 *
	 * @return The database master name.
	 */
	public static String getDatabaseMasterName() {
		return databaseMaster;
	}

	/**
	 * Get debug information on busy connections older than 5 minutes.
	 * @return Stack trace information of busy connections.
	 */
	public static List<String> getDebugInfo() {
		return getDebugInfo(5);
	}

	/**
	 * Get debug information on busy connections.
	 * @param ageInMinutes Age in minutes.
	 * @return Stack trace information of busy connections.
	 */
	public static List<String> getDebugInfo(int ageInMinutes) {
		return new ArrayList<>();
	}

	/**
	 * Dump debug information on busy connections older than 5 minutes.
	 */
	public static void dumpDebugInfo() {
		dumpDebugInfo(5);
	}

	/**
	 * Dump debug information on busy connections.
	 * @param ageInMinutes Age in minutes.
	 */
	public static void dumpDebugInfo(int ageInMinutes) {
	}
}