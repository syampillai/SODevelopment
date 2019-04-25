package com.storedobject.core;

import java.util.List;

public abstract class SQLConnector {

	public static boolean debug;

	private SQLConnector() {
	}

	/**
	 * The name of the database. This value may be set by the derived class.
	 */
	protected static String database;

	/**
	 * Gets the name of the database.
	 *
	 * @return The database name.
	 */
	public static String getDatabaseName() {
		return database;
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
		return null;
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