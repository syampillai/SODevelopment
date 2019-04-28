package com.storedobject.core;

import java.math.BigDecimal;

public class Count extends com.storedobject.core.Quantity {

    public static com.storedobject.core.Count ZERO;
    public static com.storedobject.core.Count ONE;
    public static com.storedobject.core.MeasurementUnit defaultUnit;

    public Count() {
        super(null, null);
    }

    public Count(long p1) {
        this();
    }

    public Count(java.math.BigInteger p1) {
        this();
    }

    public Count(java.math.BigDecimal p1) {
        this();
    }

    public Count(long p1, java.lang.String p2) {
        this();
    }

    public Count(long p1, com.storedobject.core.MeasurementUnit p2) {
        this();
    }

    public Count(java.math.BigInteger p1, java.lang.String p2) {
        this();
    }

    public Count(java.math.BigInteger p1, com.storedobject.core.MeasurementUnit p2) {
        this();
    }

    public Count(java.math.BigDecimal p1, com.storedobject.core.MeasurementUnit p2) {
        this();
    }
	
	/**
	 * Create a quantity of this type with zero value.
	 * @return Result
	 */
	@Override
	public Count zero() {
		return null;
	}

	/**
	 * Add quantity value
	 *
	 * @param quantity The quantity value to add
     * @return Result
	 */
	@Override
	public Count add(String quantity) {
		return null;
	}

	/**
	 * Add quantity
	 *
	 * @param quantity The quantity value to add
     * @return Result
	 */
	@Override
	public Count add(BigDecimal quantity) {
		return null;
	}

	/**
	 * Add quantity
	 *
	 * @param quantity The quantity value to add
     * @return Result
	 */
	@Override
	public Count add(Quantity quantity) {
		return null;
	}
	
	/**
	 * Subtract quantity
	 *
	 * @param quantity The quantity value to subtract
     * @return Result
	 */
	@Override
	public Count subtract(String quantity) {
		return null;
	}

	/**
	 * Subtract quantity
	 *
	 * @param quantity The quantity value to subtract
     * @return Result
	 */
	@Override
	public Count subtract(BigDecimal quantity) {
		return null;
	}

	/**
	 * Subtract quantity
	 *
	 * @param quantity The quantity value to subtract
     * @return Result
	 */
	@Override
	public Count subtract(Quantity quantity) {
		return null;
	}

	/**
	 * Multiply
	 * @param multiplicand Multiplicand
	 * @return Result
	 */
	@Override
	public Count multiply(BigDecimal multiplicand) {
		return null;
	}

	/**
	 * Multiply
	 * @param multiplicand Multiplicand
	 * @return Result
	 */
	@Override
	public Count multiply(double multiplicand) {
		return null;
	}

	/**
	 * Divide the quantity with a value
	 * @param divisor Divisor
	 * @return Result
	 */
	@Override
	public Count divide(double divisor) {
		return null;
	}

	/**
	 * Divide the quantity with a value
	 * @param divisor Divisor
	 * @return Result
	 */
	@Override
	public Count divide(BigDecimal divisor) {
		return null;
	}

	/**
	 * Reverses the sign of this quantity
     * @return Negated value
	 */
	@Override
	public Count negate() {
		return null;
	}
	
	/**
	 * Absolute value of this quantity.
	 * 
	 * @return Absolute value
	 */
	@Override
	public Count absolute() {
		return null;
	}
}
