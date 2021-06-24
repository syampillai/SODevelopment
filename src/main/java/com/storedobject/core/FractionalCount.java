package com.storedobject.core;

import java.math.BigDecimal;

public class FractionalCount extends Quantity {

	public static MeasurementUnit defaultUnit;

	public FractionalCount() {
        super(null, null);
	}

	public FractionalCount(double value, String unit) {
        this();
	}

	public FractionalCount(double value, MeasurementUnit unit) {
        this();
	}

	public FractionalCount(BigDecimal value, String unit) {
        this();
	}

	public FractionalCount(BigDecimal value, MeasurementUnit unit) {
        this();
	}
	
	
	public FractionalCount(double value) {
		this();
	}

	public FractionalCount(BigDecimal value) {
		this();
	}
	
	/**
	 * Create a quantity of this type with zero value.
	 * @return Result
	 */
	@Override
	public FractionalCount zero() {
		return null;
	}

	/**
	 * Add quantity value
	 *
	 * @param quantity The quantity value to add
     * @return Result
	 */
	@Override
	public FractionalCount add(String quantity) {
		return null;
	}

	/**
	 * Add quantity
	 *
	 * @param quantity The quantity value to add
     * @return Result
	 */
	@Override
	public FractionalCount add(BigDecimal quantity) {
		return null;
	}

	/**
	 * Add quantity
	 *
	 * @param quantity The quantity value to add
     * @return Result
	 */
	@Override
	public FractionalCount add(Quantity quantity) {
		return null;
	}
	
	/**
	 * Subtract quantity
	 *
	 * @param quantity The quantity value to subtract
     * @return Result
	 */
	@Override
	public FractionalCount subtract(String quantity) {
		return null;
	}

	/**
	 * Subtract quantity
	 *
	 * @param quantity The quantity value to subtract
     * @return Result
	 */
	@Override
	public FractionalCount subtract(BigDecimal quantity) {
		return null;
	}

	/**
	 * Subtract quantity
	 *
	 * @param quantity The quantity value to subtract
     * @return Result
	 */
	@Override
	public FractionalCount subtract(Quantity quantity) {
		return null;
	}

	/**
	 * Multiply
	 * @param multiplicand Multiplicand
	 * @return Result
	 */
	@Override
	public FractionalCount multiply(BigDecimal multiplicand) {
		return null;
	}

	/**
	 * Multiply
	 * @param multiplicand Multiplicand
	 * @return Result
	 */
	@Override
	public FractionalCount multiply(double multiplicand) {
		return null;
	}

	/**
	 * Divide the quantity with a value
	 * @param divisor Divisor
	 * @return Result
	 */
	@Override
	public FractionalCount divide(double divisor) {
		return null;
	}

	/**
	 * Divide the quantity with a value
	 * @param divisor Divisor
	 * @return Result
	 */
	@Override
	public FractionalCount divide(BigDecimal divisor) {
		return null;
	}

	/**
	 * Reverses the sign of this quantity
     * @return Negated value
	 */
	@Override
	public FractionalCount negate() {
		return null;
	}
	
	/**
	 * Absolute value of this quantity.
	 * 
	 * @return Absolute value
	 */
	@Override
	public FractionalCount absolute() {
		return null;
	}
}
