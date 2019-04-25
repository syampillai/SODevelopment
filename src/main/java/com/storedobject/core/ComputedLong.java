package com.storedobject.core;

public class ComputedLong extends AbstractComputedValue<Long> implements Comparable<ComputedLong> {
	
	private long value;

	public ComputedLong() {
		this(0, true);
	}

	public ComputedLong(long value) {
		this(value, false);
	}

	public ComputedLong(long value, boolean computed) {
		this.setValue(value);
		this.setComputed(computed);
	}

	public ComputedLong(ComputedLong value) {
		this(value.value, value.computed);
	}

	public static ComputedLong create(Object value) {
		return null;
	}

	public void set(ComputedLong value) {
		if(value == null) {
			setValue(0);
			setComputed(true);
		} else {
			setValue(value.value);
			setComputed(value.computed);
		}
	}

	@Override
	public String getStorableValue() {
		return "ROW(" + value + ",'" + (computed ? "t" : "f") + "')::CLONG";
	}

	public long getValue() {
		return value;
	}

	public void setValue(long value) {
		this.value = value;
	}

	@Override
	public int compareTo(ComputedLong o) {
		if(o == null || o.value < value) {
			return 1;
		}
		if(o.value > value) {
			return -1;
		}
		return 0;
	}
	
	@Override
	public ComputedLong clone() {
		return new ComputedLong(this);
	}

	@Override
	public void setValue(Long value) {
		setValue(value == null ? 0L : value.longValue());
	}

	@Override
	public Long getValueObject() {
		return value;
	}
}
