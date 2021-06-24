package com.storedobject.core;

import java.math.BigDecimal;

public class WeightOrVolume extends com.storedobject.core.Quantity {

    public static com.storedobject.core.MeasurementUnit defaultUnit;

    public WeightOrVolume() {
        super(null, null);
    }

    public WeightOrVolume(double p1, java.lang.String p2) {
        this();
    }

    public WeightOrVolume(double p1, com.storedobject.core.MeasurementUnit p2) {
        this();
    }

    public WeightOrVolume(java.math.BigDecimal p1, java.lang.String p2) {
        this();
    }

    public WeightOrVolume(java.math.BigDecimal p1, com.storedobject.core.MeasurementUnit p2) {
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
	public WeightOrVolume zero() {
		return null;
	}

	/**
	 * Add quantity value
	 *
	 * @param quantity The quantity value to add
     * @return Result
	 */
	@Override
	public WeightOrVolume add(String quantity) {
		return null;
	}

	/**
	 * Add quantity
	 *
	 * @param quantity The quantity value to add
     * @return Result
	 */
	@Override
	public WeightOrVolume add(BigDecimal quantity) {
		return null;
	}

	/**
	 * Add quantity
	 *
	 * @param quantity The quantity value to add
     * @return Result
	 */
	@Override
	public WeightOrVolume add(Quantity quantity) {
		return null;
	}
	
	/**
	 * Subtract quantity
	 *
	 * @param quantity The quantity value to subtract
     * @return Result
	 */
	@Override
	public WeightOrVolume subtract(String quantity) {
		return null;
	}

	/**
	 * Subtract quantity
	 *
	 * @param quantity The quantity value to subtract
     * @return Result
	 */
	@Override
	public WeightOrVolume subtract(BigDecimal quantity) {
		return null;
	}

	/**
	 * Subtract quantity
	 *
	 * @param quantity The quantity value to subtract
     * @return Result
	 */
	@Override
	public WeightOrVolume subtract(Quantity quantity) {
		return null;
	}

	/**
	 * Multiply
	 * @param multiplicand Multiplicand
	 * @return Result
	 */
	@Override
	public WeightOrVolume multiply(BigDecimal multiplicand) {
		return null;
	}

	/**
	 * Multiply
	 * @param multiplicand Multiplicand
	 * @return Result
	 */
	@Override
	public WeightOrVolume multiply(double multiplicand) {
		return null;
	}

	/**
	 * Divide the quantity with a value
	 * @param divisor Divisor
	 * @return Result
	 */
	@Override
	public WeightOrVolume divide(double divisor) {
		return null;
	}

	/**
	 * Divide the quantity with a value
	 * @param divisor Divisor
	 * @return Result
	 */
	@Override
	public WeightOrVolume divide(BigDecimal divisor) {
		return null;
	}

	/**
	 * Reverses the sign of this quantity
     * @return Negated value
	 */
	@Override
	public WeightOrVolume negate() {
		return null;
	}
	
	/**
	 * Absolute value of this quantity.
	 * 
	 * @return Absolute value
	 */
	@Override
	public WeightOrVolume absolute() {
		return null;
	}
}
