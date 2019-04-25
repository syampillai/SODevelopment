package com.storedobject.core;

public abstract class AbstractComputedInteger extends AbstractComputedValue<Integer> {
	
	public AbstractComputedInteger() {
		this(0, true);
	}

	public AbstractComputedInteger(int value) {
		this(value, false);
	}

	public AbstractComputedInteger(int value, boolean computed) {
	}

	public AbstractComputedInteger(AbstractComputedInteger value) {
	}

	public void set(AbstractComputedInteger value) {
	}

	public int getValue() {
		return 0;
	}
	
	public void setValue(int value) {
	}

	public int compareTo(AbstractComputedInteger o) {
		return 0;
	}
	
	@Override
	public void setValue(Integer value) {
	}

	@Override
	public Integer getValueObject() {
		return null;
	}
	
	protected abstract String getDBType();
	
	@Override
	public final String getStorableValue() {
		return null;
	}
}