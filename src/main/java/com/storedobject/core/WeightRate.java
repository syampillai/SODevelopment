package com.storedobject.core;

import java.math.BigDecimal;

public class WeightRate extends Quantity {

	public static MeasurementUnit defaultUnit = null;

	public WeightRate() {
		this(0.0, defaultUnit);
	}

	public WeightRate(double value, String unit) {
		this(BigDecimal.valueOf(value), unit);
	}

	public WeightRate(double value, MeasurementUnit unit) {
		this(BigDecimal.valueOf(value), unit);
	}

	public WeightRate(BigDecimal value, String unit) {
		this(value, MeasurementUnit.get(unit, WeightRate.class));
	}

	public WeightRate(BigDecimal value, MeasurementUnit unit) {
		super(value, unit);
	}
	
	/**
	 * Create a quantity of this type with zero value.
	 * @return Result
	 */
	@Override
	public WeightRate zero() {
		return null;
	}

	/**
	 * Add quantity value
	 *
	 * @param quantity The quantity value to add
     * @return Result
	 */
	@Override
	public WeightRate add(String quantity) {
		return null;
	}

	/**
	 * Add quantity
	 *
	 * @param quantity The quantity value to add
     * @return Result
	 */
	@Override
	public WeightRate add(BigDecimal quantity) {
		return null;
	}

	/**
	 * Add quantity
	 *
	 * @param quantity The quantity value to add
     * @return Result
	 */
	@Override
	public WeightRate add(Quantity quantity) {
		return null;
	}
	
	/**
	 * Subtract quantity
	 *
	 * @param quantity The quantity value to subtract
     * @return Result
	 */
	@Override
	public WeightRate subtract(String quantity) {
		return null;
	}

	/**
	 * Subtract quantity
	 *
	 * @param quantity The quantity value to subtract
     * @return Result
	 */
	@Override
	public WeightRate subtract(BigDecimal quantity) {
		return null;
	}

	/**
	 * Subtract quantity
	 *
	 * @param quantity The quantity value to subtract
     * @return Result
	 */
	@Override
	public WeightRate subtract(Quantity quantity) {
		return null;
	}

	/**
	 * Multiply
	 * @param multiplicand Multiplicand 
	 * @return Result
	 */
	@Override
	public WeightRate multiply(BigDecimal multiplicand) {
		return null;
	}

	/**
	 * Multiply
	 * @param multiplicand Multiplicand 
	 * @return Result
	 */
	@Override
	public WeightRate multiply(double multiplicand) {
		return null;
	}

	/**
	 * Divide the quantity with a value
	 * @param divisor Divisor
	 * @return Result
	 */
	@Override
	public WeightRate divide(double divisor) {
		return null;
	}

	/**
	 * Divide the quantity with a value
	 * @param divisor Divisor
	 * @return Result
	 */
	@Override
	public WeightRate divide(BigDecimal divisor) {
		return null;
	}

	/**
	 * Reverses the sign of this quantity
     * @return Negated value
	 */
	@Override
	public WeightRate negate() {
		return null;
	}
	
	/**
	 * Absolute value of this quantity.
	 * 
	 * @return Absolute value
	 */
	@Override
	public WeightRate absolute() {
		return null;
	}
}
