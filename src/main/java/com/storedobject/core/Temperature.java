package com.storedobject.core;

import java.math.BigDecimal;

public class Temperature extends com.storedobject.core.Quantity {

    public static com.storedobject.core.MeasurementUnit defaultUnit;

    public Temperature() {
        super(null, null);
    }

    public Temperature(double p1, java.lang.String p2) {
        this();
    }

    public Temperature(double p1, com.storedobject.core.MeasurementUnit p2) {
        this();
    }

    public Temperature(java.math.BigDecimal p1, java.lang.String p2) {
        this();
    }

    public Temperature(java.math.BigDecimal p1, com.storedobject.core.MeasurementUnit p2) {
        this();
    }

    @Override
	public com.storedobject.core.Quantity convert(com.storedobject.core.MeasurementUnit p1) {
        return null;
    }
	
	/**
	 * Create a quantity of this type with zero value.
	 * @return Result
	 */
	@Override
	public Temperature zero() {
		return null;
	}

	/**
	 * Add quantity value
	 *
	 * @param quantity The quantity value to add
     * @return Result
	 */
	@Override
	public Temperature add(String quantity) {
		return null;
	}

	/**
	 * Add quantity
	 *
	 * @param quantity The quantity value to add
     * @return Result
	 */
	@Override
	public Temperature add(BigDecimal quantity) {
		return null;
	}

	/**
	 * Add quantity
	 *
	 * @param quantity The quantity value to add
     * @return Result
	 */
	@Override
	public Temperature add(Quantity quantity) {
		return null;
	}
	
	/**
	 * Subtract quantity
	 *
	 * @param quantity The quantity value to subtract
     * @return Result
	 */
	@Override
	public Temperature subtract(String quantity) {
		return null;
	}

	/**
	 * Subtract quantity
	 *
	 * @param quantity The quantity value to subtract
     * @return Result
	 */
	@Override
	public Temperature subtract(BigDecimal quantity) {
		return null;
	}

	/**
	 * Subtract quantity
	 *
	 * @param quantity The quantity value to subtract
     * @return Result
	 */
	@Override
	public Temperature subtract(Quantity quantity) {
		return null;
	}

	/**
	 * Multiply
	 * @param multiplicand Multiplicand
	 * @return Result
	 */
	@Override
	public Temperature multiply(BigDecimal multiplicand) {
		return null;
	}

	/**
	 * Multiply
	 * @param multiplicand Multiplicand
	 * @return Result
	 */
	@Override
	public Temperature multiply(double multiplicand) {
		return null;
	}

	/**
	 * Divide the quantity with a value
	 * @param divisor Divisor
	 * @return Result
	 */
	@Override
	public Temperature divide(double divisor) {
		return null;
	}

	/**
	 * Divide the quantity with a value
	 * @param divisor Divisor
	 * @return Result
	 */
	@Override
	public Temperature divide(BigDecimal divisor) {
		return null;
	}

	/**
	 * Reverses the sign of this quantity
     * @return Negated value
	 */
	@Override
	public Temperature negate() {
		return null;
	}
	
	/**
	 * Absolute value of this quantity.
	 * 
	 * @return Absolute value
	 */
	@Override
	public Temperature absolute() {
		return null;
	}
}
