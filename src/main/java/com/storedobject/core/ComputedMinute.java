package com.storedobject.core;

import com.storedobject.core.converter.MinutesValueConverter;

public class ComputedMinute extends AbstractComputedInteger implements Comparable<ComputedMinute> {

	public ComputedMinute() {
		this(0, true);
	}

	public ComputedMinute(int value) {
		this(value, false);
	}

	public ComputedMinute(int value, boolean computed) {
		this.setValue(value);
		this.computed = computed;
	}

	public ComputedMinute(ComputedMinute value) {
		this(value.getValue(), value.computed);
	}
	
	public static ComputedMinute create(Object value) {
		return null;
	}
	
	public void set(ComputedMinute value) {
		super.set(value);
	}

	@Override
	public String toString() {
		return MinutesValueConverter.format(getValue(), false);
	}

	@Override
	public int compareTo(ComputedMinute o) {
		return super.compareTo(o);
	}
	
	@Override
	public ComputedMinute clone() {
		return new ComputedMinute(this);
	}

	@Override
	protected String getDBType() {
		return "CMINUTE";
	}
}