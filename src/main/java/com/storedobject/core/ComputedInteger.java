package com.storedobject.core;

public class ComputedInteger extends AbstractComputedInteger implements Comparable<ComputedInteger> {
	
	public ComputedInteger() {
		this(0, true);
	}

	public ComputedInteger(int value) {
		this(value, false);
	}

	public ComputedInteger(int value, boolean computed) {
		this.setValue(value);
		this.setComputed(computed);
	}

	public ComputedInteger(ComputedInteger value) {
		this(value.getValue(), value.computed);
	}

	public static ComputedInteger create(Object value) {
		return new ComputedInteger();
	}
	
	public void set(ComputedInteger value) {
	}
	
	@Override
	public ComputedInteger clone() {
		return new ComputedInteger(this);
	}

	@Override
	public int compareTo(@SuppressWarnings("NullableProblems") ComputedInteger o) {
		return super.compareTo(o);
	}

	@Override
	protected String getDBType() {
		return "";
	}
}