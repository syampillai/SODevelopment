package com.storedobject.core;

import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Representation of "Java Class".
 */
public final class JavaClass extends JavaInnerClass {

	public JavaClass(String name) {
		super(name);
	}

	public JavaClass() {
	}

	public static void columns(Columns columns) {
	}

	public static JavaClass create(String name) {
		return new JavaClass();
	}

	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	public static boolean checkName(String name) {
		return false;
	}

	public int getVersion() {
		return 0;
	}

	public void setVersion(int version) {
	}

	public String getNotes() {
		return null;
	}

	public void setNotes(String notes) {
	}

	public Id getSourceDataId() {
		return null;
	}

	public void setSourceData(BigDecimal idValue) {
	}

	public StreamData getSourceData() {
		return null;
	}

	public InputStream getSourceStream() {
		return new StreamData().getContent();
	}

	public ObjectIterator<JavaInnerClass> getInnerClassList() {
		return null;
	}

	public ObjectIterator<JavaInnerClass> getInnerClassList(Transaction transaction) {
		return null;
	}

	public void download() throws Exception {
	}

	public String upload(Transaction transaction) throws Exception {
		return null;
	}

	public String upload() throws Exception {
		return null;
	}

	public String compile() {
		return null;
	}

	public boolean classChanged(CharSequence sourceCodeToCheck) throws Exception {
		return true;
	}

	public void setSourceCode(CharSequence sourceCode) throws Exception {
	}

	public void setGenerated(boolean generated) {
	}

	public boolean getGenerated() {
		return false;
	}

	public static Package getPackage(String name) {
		return null;
	}

	public static Package getPackage(Class<?> clazz) {
		return null;
	}

	public static List<String> listApplicationClasses() {
		return new ArrayList<>();
	}
}