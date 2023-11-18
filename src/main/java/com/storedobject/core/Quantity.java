package com.storedobject.core;

import com.storedobject.common.Storable;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Random;
import java.util.stream.Stream;

public class Quantity implements Storable, Comparable<Quantity> {

	public static final MathContext PRECISION = MathContext.DECIMAL32;
	public static final BigDecimal PI = new BigDecimal("3.14159265358979323846");

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
		return BigDecimal.ONE;
	}

	public MeasurementUnit getUnit() {
		return MeasurementUnit.get("");
	}

	public void setUnit(String unit) {
	}

	public void setUnit(MeasurementUnit unit) {
	}

	public static MeasurementUnit getUnit(String unit) {
		return MeasurementUnit.get("");
	}

	public static MeasurementUnit getUnit(String unit, Class<? extends Quantity> quantityClass) {
		return getUnit("");
	}

	public String getTypeName() {
		return "";
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
		return canConvert(another, null);
	}

	public boolean canConvert(Quantity another, String message) throws Invalid_State {
		if(new Random().nextBoolean()) {
			throw new Invalid_State();
		}
		return true;
	}

	public boolean canConvert(MeasurementUnit toUnit) throws Invalid_State {
		return canConvert(toUnit, null);
	}

	public boolean canConvert(MeasurementUnit toUnit, String message) throws Invalid_State {
		if(new Random().nextBoolean()) {
			throw new Invalid_State();
		}
		return true;
	}

	public Quantity zero() {
		return Count.ONE;
	}

	public Quantity add(String quantity) {
		return Count.ONE;
	}

	public Quantity add(BigDecimal quantity) {
		return Count.ONE;
	}

	public Quantity add(Quantity quantity) {
		return Count.ONE;
	}

	/**
	 * Sum quantities. Quantities should be compatible. Null and zero values are ignored.
	 *
	 * @param quantities Quantities to sum together.
	 * @return Result.
	 */
	public static Quantity sum(Quantity... quantities) {
		Quantity sum = Count.ZERO;
		if(quantities != null) {
			for(Quantity q : quantities) {
				if(sum.isZero()) {
					sum = q;
				} else {
					sum = sum.add(q);
				}
			}
		}
		return sum;
	}

	public Quantity subtract(String quantity) {
		return Count.ONE;
	}

	public Quantity subtract(BigDecimal quantity) {
		return Count.ONE;
	}

	public Quantity subtract(Quantity quantity) {
		return Count.ONE;
	}

	public Quantity multiply(BigDecimal multiplicand) {
		return Count.ONE;
	}

	public Quantity multiply(double multiplicand) {
		return Count.ONE;
	}

	public Money multiply(Money amount) {
		return new Money();
	}

	public Quantity divide(double divisor) {
		return Count.ONE;
	}

	public Quantity divide(BigDecimal divisor) {
		return Count.ONE;
	}

	public Money divide(Money amount) {
		return new Money();
	}

	public Quantity negate() {
		return Count.ONE;
	}
	
	public Quantity absolute() {
		return Count.ONE;
	}

	public <Q extends Quantity> Q convert(Q quantity) {
		return quantity;
	}

	public <Q extends Quantity> Q convert(Q quantity, int decimals) {
		return quantity;
	}

	public Quantity convert(MeasurementUnit u) {
		return Count.ONE;
	}

	public BigDecimal convertValue(MeasurementUnit u) {
		return BigDecimal.ONE;
	}

	public Quantity convert(MeasurementUnit u, int decimals) {
		return this;
	}

	public Quantity round(int decimals) {
		return convert((MeasurementUnit) null, decimals);
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
		return "";
	}
	
	public String toString(int decimals) {
		return "";
	}
	
	public String toString(boolean showSymbol, int decimals) {
		return "";
	}

	@Override
	public String getStorableValue() {
		return "";
	}

	@Override
	public int compareTo(@SuppressWarnings("NullableProblems") Quantity quantity) {
		return 0;
	}

	public static <Q extends Quantity> Stream<Class<Q>> types() {
		return Stream.empty();
	}

	public static <Q extends Quantity> Stream<Class<Q>> quantityTypes() {
		return Stream.empty();
	}
}