package com.storedobject.core;

import java.math.BigDecimal;

public class Volume extends com.storedobject.core.Quantity {

    public static com.storedobject.core.MeasurementUnit defaultUnit;

    public Volume() {
        super((java.math.BigDecimal) null, (com.storedobject.core.MeasurementUnit) null);
    }

    public Volume(double p1, java.lang.String p2) {
        this();
    }

    public Volume(double p1, com.storedobject.core.MeasurementUnit p2) {
        this();
    }

    public Volume(java.math.BigDecimal p1, java.lang.String p2) {
        this();
    }

    public Volume(java.math.BigDecimal p1, com.storedobject.core.MeasurementUnit p2) {
        this();
    }
	
	/**
	 * Create a quantity of this type with zero value.
	 * @return Result
	 */
	@Override
	public Volume zero() {
		return null;
	}

	/**
	 * Add quantity value
	 *
	 * @param quantity The quantity value to add
     * @return Result
	 */
	@Override
	public Volume add(String quantity) {
		return null;
	}

	/**
	 * Add quantity
	 *
	 * @param quantity The quantity value to add
     * @return Result
	 */
	@Override
	public Volume add(BigDecimal quantity) {
		return null;
	}

	/**
	 * Add quantity
	 *
	 * @param quantity The quantity value to add
     * @return Result
	 */
	@Override
	public Volume add(Quantity quantity) {
		return null;
	}
	
	/**
	 * Subtract quantity
	 *
	 * @param quantity The quantity value to subtract
     * @return Result
	 */
	@Override
	public Volume subtract(String quantity) {
		return null;
	}

	/**
	 * Subtract quantity
	 *
	 * @param quantity The quantity value to subtract
     * @return Result
	 */
	@Override
	public Volume subtract(BigDecimal quantity) {
		return null;
	}

	/**
	 * Subtract quantity
	 *
	 * @param quantity The quantity value to subtract
     * @return Result
	 */
	@Override
	public Volume subtract(Quantity quantity) {
		return null;
	}

	/**
	 * Multiply
	 * @param multiplicand
	 * @return Result
	 */
	@Override
	public Volume multiply(BigDecimal multiplicand) {
		return null;
	}

	/**
	 * Multiply
	 * @param multiplicand
	 * @return Result
	 */
	@Override
	public Volume multiply(double multiplicand) {
		return null;
	}

	/**
	 * Divide the quantity with a value
	 * @param divisor
	 * @return Result
	 */
	@Override
	public Volume divide(double divisor) {
		return null;
	}

	/**
	 * Divide the quantity with a value
	 * @param divisor
	 * @return Result
	 */
	@Override
	public Volume divide(BigDecimal divisor) {
		return null;
	}

	/**
	 * Reverses the sign of this quantity
     * @return Negated value
	 */
	@Override
	public Volume negate() {
		return null;
	}
	
	/**
	 * Absolute value of this quantity.
	 * 
	 * @return Absolute value
	 */
	@Override
	public Volume absolute() {
		return null;
	}
}
