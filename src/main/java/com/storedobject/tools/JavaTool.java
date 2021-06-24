package com.storedobject.tools;

import com.storedobject.common.StyledBuilder;
import com.storedobject.core.TransactionManager;

import java.io.File;
import java.io.InputStream;

public class JavaTool {
	
	public synchronized static void close() {
	}
	
	public synchronized static String compile(File... sourceFiles) {
		return null;
	}
	
	public static void setClassDirectory(String sourceDirectory, String classDirectory, String jarsDirectory) {
	}
	
	public static String getSourceDirectory() {
		return "";
	}

	public static String getClassDirectory() {
		return "";
	}
	
	public static String getJarsDirectory() {
		return "";
	}
	
	public static void loadDefinitions(TransactionManager tm, InputStream data, StyledBuilder message) throws Exception {
	}
	
	public static void compareDefinitions(TransactionManager tm, InputStream data, StyledBuilder message) throws Exception {
	}
	
	public static void updateDefinitions(TransactionManager tm, InputStream data, StyledBuilder message) throws Exception {
	}
}
