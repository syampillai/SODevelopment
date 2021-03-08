package com.storedobject.core;

import com.storedobject.common.Storable;

import java.math.BigDecimal;
import java.util.stream.Stream;

public class Quantity implements Storable, Comparable<Quantity> {

	protected Quantity(BigDecimal value, MeasurementUnit unit) {
	}

	public static Quantity create(Object value) {
		return new Quantity(null, null);
	}

	public static <Q extends Quantity> Q create(Object value, Class<Q> qClass) {
		return null;
	}

	public static <Q extends Quantity> Q create(String value, Class<Q> qClass, boolean returnNull) {
		return null;
	}

	public static Quantity create(BigDecimal quantity, MeasurementUnit unit) {
		return null;
	}

	public static Quantity create(MeasurementUnit unit) {
		return null;
	}

	public static Quantity create(double quantity, MeasurementUnit unit) {
		return null;
	}

	public static <Q extends Quantity> Q create(Class<Q> qClass) {
		return null;
	}

	public static <Q extends Quantity> Q create(double quantity, Class<Q> qClass) {
		return null;
	}

	public static <Q extends Quantity> Q create(BigDecimal quantity, Class<Q> qClass) {
		return null;
	}
	
	public static <Q extends Quantity> Q create(Class<Q> qClass, String unit) {
		return null;
	}

	public static <Q extends Quantity> Q create(double quantity, Class<Q> qClass, String unit) {
		return null;
	}
	
	public static <Q extends Quantity> Q create(BigDecimal quantity, Class<Q> qClass, String unit) {
		return null;
	}

	public static Class<? extends Quantity> getClass(MeasurementUnit unit) {
		return null;
	}

	public BigDecimal getValue() {
		return null;
	}

	public MeasurementUnit getUnit() {
		return null;
	}

	public void setUnit(String unit) {
	}

	public void setUnit(MeasurementUnit unit) {
	}

	public static MeasurementUnit getUnit(String unit) {
		return null;
	}

	public static MeasurementUnit getUnit(String unit, Class<? extends Quantity> quantityClass) {
		return null;
	}

	public String getTypeName() {
		return null;
	}

	public static int getType(Class<? extends Quantity> quantityClass) {
		return -1;
	}

	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	public boolean isCompatible(Quantity another) {
		return false;
	}

	public boolean isConvertible(Quantity another) {
        return false;
	}

	public boolean isConvertible(MeasurementUnit toUnit) {
        return false;
	}

	public boolean canConvert(Quantity another) throws Invalid_State {
		return true;
	}

	public boolean canConvert(MeasurementUnit toUnit) throws Invalid_State {
		return true;
	}

	public Quantity zero() {
		return null;
	}

	public Quantity add(String quantity) {
		return null;
	}

	public Quantity add(BigDecimal quantity) {
		return null;
	}

	public Quantity add(Quantity quantity) {
		return null;
	}
	
	public Quantity subtract(String quantity) {
		return null;
	}

	public Quantity subtract(BigDecimal quantity) {
		return null;
	}

	public Quantity subtract(Quantity quantity) {
		return null;
	}

	public Quantity multiply(BigDecimal multiplicand) {
		return null;
	}

	public Quantity multiply(double multiplicand) {
		return null;
	}

	public Money multiply(Money amount) {
		return null;
	}

	public Quantity divide(double divisor) {
		return null;
	}

	public Quantity divide(BigDecimal divisor) {
		return null;
	}

	public Money divide(Money amount) {
		return null;
	}

	public Quantity negate() {
		return null;
	}
	
	public Quantity absolute() {
		return null;
	}

	public <Q extends Quantity> Q convert(Q quantity) {
		return null;
	}

	public Quantity convert(MeasurementUnit u) {
		return null;
	}

    public boolean isGreaterThan(Quantity another) {
        return false;
    }

    public boolean isGreaterThan(long value) {
        return false;
    }

    public boolean isLessThan(Quantity another) {
        return false;
    }

    public boolean isLessThan(long value) {
        return false;
    }

    public boolean isGreaterThanOrEqual(Quantity another) {
        return false;
    }

    public boolean isGreaterThanOrEqual(long value) {
        return false;
    }

    public boolean isLessThanOrEqual(Quantity another) {
        return false;
    }

    public boolean isLessThanOrEqual(long value) {
        return false;
    }

    public boolean isZero() {
        return false;
    }

    public boolean isPositive() {
        return false;
    }

    public boolean isNegative() {
        return false;
    }

	public String toString(boolean showSymbol) {
		return null;
	}
	
	public String toString(int decimals) {
		return null;
	}
	
	public String toString(boolean showSymbol, int decimals) {
		return null;
	}

	@Override
	public String getStorableValue() {
		return null;
	}

	@Override
	public int compareTo(@SuppressWarnings("NullableProblems") Quantity quantity) {
		return 0;
	}

	public static <Q extends Quantity> Stream<Class<Q>> types() {
		return Stream.empty();
	}
}