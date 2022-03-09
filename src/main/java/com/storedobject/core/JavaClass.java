package com.storedobject.core;

import java.io.InputStream;
import java.math.BigDecimal;
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
		return "";
	}

	public void setNotes(String notes) {
	}

	public Id getSourceDataId() {
		return new Id();
	}

	public void setSourceData(BigDecimal idValue) {
	}

	public StreamData getSourceData() {
		return new StreamData();
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
		return "";
	}

	public String upload() throws Exception {
		return "";
	}

	public String compile() {
		return "";
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
		return JavaClass.class.getClassLoader().getDefinedPackage(name);
	}

	public static Package getPackage(Class<?> clazz) {
		return JavaClass.class.getClassLoader().getDefinedPackage("X");
	}

	public static List<String> listApplicationClasses() {
		return new ArrayList<>();
	}
}