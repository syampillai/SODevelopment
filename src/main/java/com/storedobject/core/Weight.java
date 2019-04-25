package com.storedobject.core;

import java.math.BigDecimal;

public class Weight extends com.storedobject.core.Quantity {

    public static com.storedobject.core.MeasurementUnit defaultUnit;

    public Weight() {
        super((java.math.BigDecimal) null, (com.storedobject.core.MeasurementUnit) null);
    }

    public Weight(double p1, java.lang.String p2) {
        this();
    }

    public Weight(double p1, com.storedobject.core.MeasurementUnit p2) {
        this();
    }

    public Weight(java.math.BigDecimal p1, java.lang.String p2) {
        this();
    }

    public Weight(java.math.BigDecimal p1, com.storedobject.core.MeasurementUnit p2) {
        this();
    }
	
	/**
	 * Create a quantity of this type with zero value.
	 * @return Result
	 */
	@Override
	public Weight zero() {
		return null;
	}

	/**
	 * Add quantity value
	 *
	 * @param quantity The quantity value to add
     * @return Result
	 */
	@Override
	public Weight add(String quantity) {
		return null;
	}

	/**
	 * Add quantity
	 *
	 * @param quantity The quantity value to add
     * @return Result
	 */
	@Override
	public Weight add(BigDecimal quantity) {
		return null;
	}

	/**
	 * Add quantity
	 *
	 * @param quantity The quantity value to add
     * @return Result
	 */
	@Override
	public Weight add(Quantity quantity) {
		return null;
	}
	
	/**
	 * Subtract quantity
	 *
	 * @param quantity The quantity value to subtract
     * @return Result
	 */
	@Override
	public Weight subtract(String quantity) {
		return null;
	}

	/**
	 * Subtract quantity
	 *
	 * @param quantity The quantity value to subtract
     * @return Result
	 */
	@Override
	public Weight subtract(BigDecimal quantity) {
		return null;
	}

	/**
	 * Subtract quantity
	 *
	 * @param quantity The quantity value to subtract
     * @return Result
	 */
	@Override
	public Weight subtract(Quantity quantity) {
		return null;
	}

	/**
	 * Multiply
	 * @param multiplicand
	 * @return Result
	 */
	@Override
	public Weight multiply(BigDecimal multiplicand) {
		return null;
	}

	/**
	 * Multiply
	 * @param multiplicand
	 * @return Result
	 */
	@Override
	public Weight multiply(double multiplicand) {
		return null;
	}

	/**
	 * Divide the quantity with a value
	 * @param divisor
	 * @return Result
	 */
	@Override
	public Weight divide(double divisor) {
		return null;
	}

	/**
	 * Divide the quantity with a value
	 * @param divisor
	 * @return Result
	 */
	@Override
	public Weight divide(BigDecimal divisor) {
		return null;
	}

	/**
	 * Reverses the sign of this quantity
     * @return Negated value
	 */
	@Override
	public Weight negate() {
		return null;
	}
	
	/**
	 * Absolute value of this quantity.
	 * 
	 * @return Absolute value
	 */
	@Override
	public Weight absolute() {
		return null;
	}
}
