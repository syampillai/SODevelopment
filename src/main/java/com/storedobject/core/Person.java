package com.storedobject.core;

import java.sql.Date;

public final class Person extends StoredObject implements HasContacts, Comparable<Person>, RequiresApproval {

	public Person(String name) {
	}

	public Person(String firstName, String middleName, String lastName) {
	}

	public Person() {
	}

	public static void columns(Columns columns) {
	}

	public static String[] getTitleValues() {
		return new String[] {};
	}

	public static String[] getTitleValues(int gender) {
		return new String[] {};
	}

	public String getTitleValue() {
		return "";
	}

	public int getTitle() {
		return 0;
	}

	public void setTitle(int title) throws Invalid_Value {
	}

	@Override
	public String getName() {
		return null;
	}

	public void setName(String name) {
	}

	public void setFirstName(String name) {
	}

	public String getFirstName() {
		return "";
	}

	public void setMiddleName(String name) {
	}

	public String getMiddleName() {
		return "";
	}

	public void setLastName(String name) {
	}

	public String getLastName() {
		return "";
	}

	public void setShortName(String name) {
	}

	public String getShortName() {
		return null;
	}

	public static String[] getSuffixValues() {
		return null;
	}

	public static String getSuffixValue(int suffix) {
		return null;
	}

	public String getSuffixValue() {
		return null;
	}

	public int getSuffix() {
		return 0;
	}

	public void setSuffix(int suffix) {
	}

	public void setGenderSpecificTitle(int index) throws Invalid_Value {
	}

	public static String[] getGenderValues() {
		return null;
	}

	public void setGender(int gender) throws Invalid_Value {
	}

	public int getGender() {
		return 0;
	}

	public String getGenderValue() {
		return null;
	}

	public void setDateOfBirth(Date dateOfBirth) {
	}

	public Date getDateOfBirth() {
		return DateUtility.today();
	}

	public boolean isMinor() {
		return false;
	}

	public boolean isMinor(java.util.Date date) {
		return false;
	}

	public int getAge() {
		return 0;
	}

	public int getAge(java.util.Date date) {
		return -1;
	}

	public static String[] getMaritalStatusValues() {
		return null;
	}

	public void setMaritalStatus(int maritalStatus) throws Invalid_Value {
	}

	public int getMaritalStatus() {
		return 0;
	}

	public String getMaritalStatusValue() {
		return "";
	}

    public static Person get(String name) {
		return Math.random() > 0.5 ? null : new Person();
    }

    public static ObjectIterator<Person> list(String name) {
		return ObjectIterator.create();
    }

	@Override
	public int compareTo(@SuppressWarnings("NullableProblems") Person person) {
		return 0;
	}
}