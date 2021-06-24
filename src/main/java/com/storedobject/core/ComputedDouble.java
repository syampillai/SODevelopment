package com.storedobject.core;

public class ComputedDouble extends AbstractComputedValue<Double> implements Comparable<ComputedDouble> {
	
	private double value;

	public ComputedDouble() {
		this(0.0, true);
	}

	public ComputedDouble(double value) {
		this(value, false);
	}

	public ComputedDouble(double value, boolean computed) {
		this.setValue(value);
		this.setComputed(computed);
	}

	public ComputedDouble(ComputedDouble value) {
		this(value.value, value.computed);
	}

	public static ComputedDouble create(Object value) {
		return null;
	}

	public void set(ComputedDouble value) {
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
		return "ROW(" + value + ",'" + (computed ? "t" : "f") + "')::CDOUBLE";
	}
	
	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return Money.format(value, false);
	}

	@Override
	public int compareTo(ComputedDouble o) {
		if(o == null) {
			return 1;
		}
		return MathUtility.compare(value, o.value, 0.000000001);
	}
	
	@Override
	public ComputedDouble clone() {
		return new ComputedDouble(this);
	}

	@Override
	public void setValue(Double value) {
		setValue(value == null ? 0.0 : value.doubleValue());
	}

	@Override
	public Double getValueObject() {
		return value;
	}
}
