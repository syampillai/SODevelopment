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
		return null;
	}

	public static String getClassDirectory() {
		return null;
	}
	
	public static String getJarsDirectory() {
		return null;
	}
	
	public static void loadDefinitions(TransactionManager tm, InputStream data, StyledBuilder message) throws Exception {
	}
	
	public static void compareDefinitions(TransactionManager tm, InputStream data, StyledBuilder message) throws Exception {
	}
	
	public static void updateDefinitions(TransactionManager tm, InputStream data, StyledBuilder message) throws Exception {
	}
}
