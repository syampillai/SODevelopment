package com.storedobject.core;

import java.math.BigDecimal;

public class TimeDuration extends Quantity {

	public static MeasurementUnit defaultUnit = null;

	public TimeDuration() {
		this(BigDecimal.ZERO, defaultUnit);
	}

	public TimeDuration(double value, String unit) {
		this(BigDecimal.valueOf(value), unit);
	}

	public TimeDuration(double value, MeasurementUnit unit) {
		this(BigDecimal.valueOf(value), unit);
	}

	public TimeDuration(BigDecimal value, String unit) {
		this(value, MeasurementUnit.get(unit, TimeDuration.class));
	}

	public TimeDuration(BigDecimal value, MeasurementUnit unit) {
		super(value, unit);
	}
	
	/**
	 * Create a quantity of this type with zero value.
	 * @return Result
	 */
	@Override
	public TimeDuration zero() {
		return (TimeDuration)super.zero();
	}

	/**
	 * Add quantity value
	 *
	 * @param quantity The quantity value to add
     * @return Result
	 */
	@Override
	public TimeDuration add(String quantity) {
        return (TimeDuration)super.add(quantity);
	}

	/**
	 * Add quantity
	 *
	 * @param quantity The quantity value to add
     * @return Result
	 */
	@Override
	public TimeDuration add(BigDecimal quantity) {
        return (TimeDuration)super.add(quantity);
	}

	/**
	 * Add quantity
	 *
	 * @param quantity The quantity value to add
     * @return Result
	 */
	@Override
	public TimeDuration add(Quantity quantity) {
        return (TimeDuration)super.add(quantity);
	}
	
	/**
	 * Subtract quantity
	 *
	 * @param quantity The quantity value to subtract
     * @return Result
	 */
	@Override
	public TimeDuration subtract(String quantity) {
        return (TimeDuration)super.subtract(quantity);
	}

	/**
	 * Subtract quantity
	 *
	 * @param quantity The quantity value to subtract
     * @return Result
	 */
	@Override
	public TimeDuration subtract(BigDecimal quantity) {
        return (TimeDuration)super.subtract(quantity);
	}

	/**
	 * Subtract quantity
	 *
	 * @param quantity The quantity value to subtract
     * @return Result
	 */
	@Override
	public TimeDuration subtract(Quantity quantity) {
        return (TimeDuration)super.subtract(quantity);
	}

	/**
	 * Multiply
	 * @param multiplicand Multiplicand 
	 * @return Result
	 */
	@Override
	public TimeDuration multiply(BigDecimal multiplicand) {
        return (TimeDuration)super.multiply(multiplicand);
	}

	/**
	 * Multiply
	 * @param multiplicand Multiplicand 
	 * @return Result
	 */
	@Override
	public TimeDuration multiply(double multiplicand) {
        return (TimeDuration)super.multiply(multiplicand);
	}

	/**
	 * Divide the quantity with a value
	 * @param divisor Divisor
	 * @return Result
	 */
	@Override
	public TimeDuration divide(double divisor) {
        return (TimeDuration)super.divide(divisor);
	}

	/**
	 * Divide the quantity with a value
	 * @param divisor Divisor
	 * @return Result
	 */
	@Override
	public TimeDuration divide(BigDecimal divisor) {
        return (TimeDuration)super.divide(divisor);
	}

	/**
	 * Reverses the sign of this quantity
     * @return Negated value
	 */
	@Override
	public TimeDuration negate() {
        return (TimeDuration)super.negate();
	}
	
	/**
	 * Absolute value of this quantity.
	 * 
	 * @return Absolute value
	 */
	@Override
	public TimeDuration absolute() {
        return (TimeDuration)super.absolute();
	}
}