package com.storedobject.core;

import java.math.BigDecimal;

public class VolumeRate extends Quantity {

	public static MeasurementUnit defaultUnit = null;

	public VolumeRate() {
		this(0.0, defaultUnit);
	}

	public VolumeRate(double value, String unit) {
		this(BigDecimal.valueOf(value), unit);
	}

	public VolumeRate(double value, MeasurementUnit unit) {
		this(BigDecimal.valueOf(value), unit);
	}

	public VolumeRate(BigDecimal value, String unit) {
		this(value, MeasurementUnit.get(unit, VolumeRate.class));
	}

	public VolumeRate(BigDecimal value, MeasurementUnit unit) {
		super(value, unit);
	}
	
	/**
	 * Create a quantity of this type with zero value.
	 * @return Result
	 */
	@Override
	public VolumeRate zero() {
		return null;
	}

	/**
	 * Add quantity value
	 *
	 * @param quantity The quantity value to add
     * @return Result
	 */
	@Override
	public VolumeRate add(String quantity) {
		return null;
	}

	/**
	 * Add quantity
	 *
	 * @param quantity The quantity value to add
     * @return Result
	 */
	@Override
	public VolumeRate add(BigDecimal quantity) {
		return null;
	}

	/**
	 * Add quantity
	 *
	 * @param quantity The quantity value to add
     * @return Result
	 */
	@Override
	public VolumeRate add(Quantity quantity) {
		return null;
	}
	
	/**
	 * Subtract quantity
	 *
	 * @param quantity The quantity value to subtract
     * @return Result
	 */
	@Override
	public VolumeRate subtract(String quantity) {
		return null;
	}

	/**
	 * Subtract quantity
	 *
	 * @param quantity The quantity value to subtract
     * @return Result
	 */
	@Override
	public VolumeRate subtract(BigDecimal quantity) {
		return null;
	}

	/**
	 * Subtract quantity
	 *
	 * @param quantity The quantity value to subtract
     * @return Result
	 */
	@Override
	public VolumeRate subtract(Quantity quantity) {
		return null;
	}

	/**
	 * Multiply
	 * @param multiplicand Multiplicand 
	 * @return Result
	 */
	@Override
	public VolumeRate multiply(BigDecimal multiplicand) {
		return null;
	}

	/**
	 * Multiply
	 * @param multiplicand Multiplicand 
	 * @return Result
	 */
	@Override
	public VolumeRate multiply(double multiplicand) {
		return null;
	}

	/**
	 * Divide the quantity with a value
	 * @param divisor Divisor
	 * @return Result
	 */
	@Override
	public VolumeRate divide(double divisor) {
		return null;
	}

	/**
	 * Divide the quantity with a value
	 * @param divisor Divisor
	 * @return Result
	 */
	@Override
	public VolumeRate divide(BigDecimal divisor) {
		return null;
	}

	/**
	 * Reverses the sign of this quantity
     * @return Negated value
	 */
	@Override
	public VolumeRate negate() {
		return null;
	}
	
	/**
	 * Absolute value of this quantity.
	 * 
	 * @return Absolute value
	 */
	@Override
	public VolumeRate absolute() {
		return null;
	}
}
