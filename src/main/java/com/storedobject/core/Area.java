package com.storedobject.core;

import java.math.BigDecimal;

public class Area extends com.storedobject.core.Quantity {

    public static com.storedobject.core.MeasurementUnit defaultUnit;

    public Area() {
        super((java.math.BigDecimal) null, (com.storedobject.core.MeasurementUnit) null);
    }

    public Area(double p1, java.lang.String p2) {
        this();
    }

    public Area(double p1, com.storedobject.core.MeasurementUnit p2) {
        this();
    }

    public Area(java.math.BigDecimal p1, java.lang.String p2) {
        this();
    }

    public Area(java.math.BigDecimal p1, com.storedobject.core.MeasurementUnit p2) {
        this();
    }
	
	/**
	 * Create a quantity of this type with zero value.
	 * @return Result
	 */
	@Override
	public Area zero() {
		return null;
	}

	/**
	 * Add quantity value
	 *
	 * @param quantity The quantity value to add
     * @return Result
	 */
	@Override
	public Area add(String quantity) {
		return null;
	}

	/**
	 * Add quantity
	 *
	 * @param quantity The quantity value to add
     * @return Result
	 */
	@Override
	public Area add(BigDecimal quantity) {
		return null;
	}

	/**
	 * Add quantity
	 *
	 * @param quantity The quantity value to add
     * @return Result
	 */
	@Override
	public Area add(Quantity quantity) {
		return null;
	}
	
	/**
	 * Subtract quantity
	 *
	 * @param quantity The quantity value to subtract
     * @return Result
	 */
	@Override
	public Area subtract(String quantity) {
		return null;
	}

	/**
	 * Subtract quantity
	 *
	 * @param quantity The quantity value to subtract
     * @return Result
	 */
	@Override
	public Area subtract(BigDecimal quantity) {
		return null;
	}

	/**
	 * Subtract quantity
	 *
	 * @param quantity The quantity value to subtract
     * @return Result
	 */
	@Override
	public Area subtract(Quantity quantity) {
		return null;
	}

	/**
	 * Multiply
	 * @param multiplicand
	 * @return Result
	 */
	@Override
	public Area multiply(BigDecimal multiplicand) {
		return null;
	}

	/**
	 * Multiply
	 * @param multiplicand
	 * @return Result
	 */
	@Override
	public Area multiply(double multiplicand) {
		return null;
	}

	/**
	 * Divide the quantity with a value
	 * @param divisor
	 * @return Result
	 */
	@Override
	public Area divide(double divisor) {
		return null;
	}

	/**
	 * Divide the quantity with a value
	 * @param divisor
	 * @return Result
	 */
	@Override
	public Area divide(BigDecimal divisor) {
		return null;
	}

	/**
	 * Reverses the sign of this quantity
     * @return Negated value
	 */
	@Override
	public Area negate() {
		return null;
	}
	
	/**
	 * Absolute value of this quantity.
	 * 
	 * @return Absolute value
	 */
	@Override
	public Area absolute() {
		return null;
	}
}
