package com.storedobject.core;

import java.math.BigDecimal;

public class Percentage extends Quantity {

	public static MeasurementUnit defaultUnit = null;

	public Percentage() {
		this(BigDecimal.ZERO, defaultUnit);
	}

	public Percentage(double value, String unit) {
		this(BigDecimal.valueOf(value), unit);
	}

	public Percentage(double value, MeasurementUnit unit) {
		this(BigDecimal.valueOf(value), unit);
	}

	public Percentage(BigDecimal value, String unit) {
		this(value, MeasurementUnit.get(unit, Percentage.class));
	}

	public Percentage(BigDecimal value, MeasurementUnit unit) {
		super(value, unit);
	}
	
	/**
	 * Create a quantity of this type with zero value.
	 * @return Result
	 */
	@Override
	public Percentage zero() {
		return (Percentage)super.zero();
	}

	/**
	 * Add quantity value
	 *
	 * @param quantity The quantity value to add
     * @return Result
	 */
	@Override
	public Percentage add(String quantity) {
        return (Percentage)super.add(quantity);
	}

	/**
	 * Add quantity
	 *
	 * @param quantity The quantity value to add
     * @return Result
	 */
	@Override
	public Percentage add(BigDecimal quantity) {
        return (Percentage)super.add(quantity);
	}

	/**
	 * Add quantity
	 *
	 * @param quantity The quantity value to add
     * @return Result
	 */
	@Override
	public Percentage add(Quantity quantity) {
        return (Percentage)super.add(quantity);
	}
	
	/**
	 * Subtract quantity
	 *
	 * @param quantity The quantity value to subtract
     * @return Result
	 */
	@Override
	public Percentage subtract(String quantity) {
        return (Percentage)super.subtract(quantity);
	}

	/**
	 * Subtract quantity
	 *
	 * @param quantity The quantity value to subtract
     * @return Result
	 */
	@Override
	public Percentage subtract(BigDecimal quantity) {
        return (Percentage)super.subtract(quantity);
	}

	/**
	 * Subtract quantity
	 *
	 * @param quantity The quantity value to subtract
     * @return Result
	 */
	@Override
	public Percentage subtract(Quantity quantity) {
        return (Percentage)super.subtract(quantity);
	}

	/**
	 * Multiply
	 * @param multiplicand Multiplicand 
	 * @return Result
	 */
	@Override
	public Percentage multiply(BigDecimal multiplicand) {
        return (Percentage)super.multiply(multiplicand);
	}

	/**
	 * Multiply
	 * @param multiplicand Multiplicand 
	 * @return Result
	 */
	@Override
	public Percentage multiply(double multiplicand) {
        return (Percentage)super.multiply(multiplicand);
	}

	/**
	 * Divide the quantity with a value
	 * @param divisor Divisor
	 * @return Result
	 */
	@Override
	public Percentage divide(double divisor) {
        return (Percentage)super.divide(divisor);
	}

	/**
	 * Divide the quantity with a value
	 * @param divisor Divisor
	 * @return Result
	 */
	@Override
	public Percentage divide(BigDecimal divisor) {
        return (Percentage)super.divide(divisor);
	}

	/**
	 * Reverses the sign of this quantity
     * @return Negated value
	 */
	@Override
	public Percentage negate() {
        return (Percentage)super.negate();
	}
	
	/**
	 * Absolute value of this quantity.
	 * 
	 * @return Absolute value
	 */
	@Override
	public Percentage absolute() {
        return (Percentage)super.absolute();
	}
}
