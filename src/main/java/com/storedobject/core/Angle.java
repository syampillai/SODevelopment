package com.storedobject.core;

import java.math.BigDecimal;

public class Angle extends Quantity {

	public static MeasurementUnit defaultUnit = null;

	public Angle() {
		this(BigDecimal.ZERO, defaultUnit);
	}

	public Angle(double value, String unit) {
		this(BigDecimal.valueOf(value), unit);
	}

	public Angle(double value, MeasurementUnit unit) {
		this(BigDecimal.valueOf(value), unit);
	}

	public Angle(BigDecimal value, String unit) {
		this(value, MeasurementUnit.get(unit, Angle.class));
	}

	public Angle(BigDecimal value, MeasurementUnit unit) {
		super(value, unit);
	}
	
	/**
	 * Create a quantity of this type with zero value.
	 * @return Result
	 */
	@Override
	public Angle zero() {
		return (Angle)super.zero();
	}

	/**
	 * Add quantity value
	 *
	 * @param quantity The quantity value to add
     * @return Result
	 */
	@Override
	public Angle add(String quantity) {
        return (Angle)super.add(quantity);
	}

	/**
	 * Add quantity
	 *
	 * @param quantity The quantity value to add
     * @return Result
	 */
	@Override
	public Angle add(BigDecimal quantity) {
        return (Angle)super.add(quantity);
	}

	/**
	 * Add quantity
	 *
	 * @param quantity The quantity value to add
     * @return Result
	 */
	@Override
	public Angle add(Quantity quantity) {
        return (Angle)super.add(quantity);
	}
	
	/**
	 * Subtract quantity
	 *
	 * @param quantity The quantity value to subtract
     * @return Result
	 */
	@Override
	public Angle subtract(String quantity) {
        return (Angle)super.subtract(quantity);
	}

	/**
	 * Subtract quantity
	 *
	 * @param quantity The quantity value to subtract
     * @return Result
	 */
	@Override
	public Angle subtract(BigDecimal quantity) {
        return (Angle)super.subtract(quantity);
	}

	/**
	 * Subtract quantity
	 *
	 * @param quantity The quantity value to subtract
     * @return Result
	 */
	@Override
	public Angle subtract(Quantity quantity) {
        return (Angle)super.subtract(quantity);
	}

	/**
	 * Multiply
	 * @param multiplicand Multiplicand 
	 * @return Result
	 */
	@Override
	public Angle multiply(BigDecimal multiplicand) {
        return (Angle)super.multiply(multiplicand);
	}

	/**
	 * Multiply
	 * @param multiplicand Multiplicand 
	 * @return Result
	 */
	@Override
	public Angle multiply(double multiplicand) {
        return (Angle)super.multiply(multiplicand);
	}

	/**
	 * Divide the quantity with a value
	 * @param divisor Divisor
	 * @return Result
	 */
	@Override
	public Angle divide(double divisor) {
        return (Angle)super.divide(divisor);
	}

	/**
	 * Divide the quantity with a value
	 * @param divisor Divisor
	 * @return Result
	 */
	@Override
	public Angle divide(BigDecimal divisor) {
        return (Angle)super.divide(divisor);
	}

	/**
	 * Reverses the sign of this quantity
     * @return Negated value
	 */
	@Override
	public Angle negate() {
        return (Angle)super.negate();
	}
	
	/**
	 * Absolute value of this quantity.
	 * 
	 * @return Absolute value
	 */
	@Override
	public Angle absolute() {
        return (Angle)super.absolute();
	}
}