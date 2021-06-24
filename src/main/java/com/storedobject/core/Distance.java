package com.storedobject.core;

import java.math.BigDecimal;

public class Distance extends Quantity {

	public static final BigDecimal BD_KM = new BigDecimal(1000);
	public static final BigDecimal BD_CM = new BigDecimal("0.01");
	public static final BigDecimal BD_MM = new BigDecimal("0.001");
	public static final MeasurementUnit defaultUnit = MeasurementUnit.get("m");

	public Distance() {
		this(BigDecimal.ZERO, defaultUnit);
	}

	public Distance(double value, String unit) {
		this(BigDecimal.valueOf(value), unit);
	}

	public Distance(double value, MeasurementUnit unit) {
		this(BigDecimal.valueOf(value), unit);
	}

	public Distance(BigDecimal value, String unit) {
		this(value, MeasurementUnit.get(unit, Distance.class));
	}

	public Distance(BigDecimal value, MeasurementUnit unit) {
		super(value, unit);
	}

	/**
	 * Create a quantity of this type with zero value.
	 * @return Result
	 */
	@Override
	public Distance zero() {
		return (Distance)super.zero();
	}

	/**
	 * Add quantity value
	 *
	 * @param quantity The quantity value to add
	 * @return Result
	 */
	@Override
	public Distance add(String quantity) {
		return (Distance)super.add(quantity);
	}

	/**
	 * Add quantity
	 *
	 * @param quantity The quantity value to add
	 * @return Result
	 */
	@Override
	public Distance add(BigDecimal quantity) {
		return (Distance)super.add(quantity);
	}

	/**
	 * Add quantity
	 *
	 * @param quantity The quantity value to add
	 * @return Result
	 */
	@Override
	public Distance add(Quantity quantity) {
		return (Distance)super.add(quantity);
	}

	/**
	 * Subtract quantity
	 *
	 * @param quantity The quantity value to subtract
	 * @return Result
	 */
	@Override
	public Distance subtract(String quantity) {
		return (Distance)super.subtract(quantity);
	}

	/**
	 * Subtract quantity
	 *
	 * @param quantity The quantity value to subtract
	 * @return Result
	 */
	@Override
	public Distance subtract(BigDecimal quantity) {
		return (Distance)super.subtract(quantity);
	}

	/**
	 * Subtract quantity
	 *
	 * @param quantity The quantity value to subtract
	 * @return Result
	 */
	@Override
	public Distance subtract(Quantity quantity) {
		return (Distance)super.subtract(quantity);
	}

	/**
	 * Multiply
	 * @param multiplicand Multiplicand
	 * @return Result
	 */
	@Override
	public Distance multiply(BigDecimal multiplicand) {
		return (Distance)super.multiply(multiplicand);
	}

	/**
	 * Multiply
	 * @param multiplicand Multiplicand
	 * @return Result
	 */
	@Override
	public Distance multiply(double multiplicand) {
		return (Distance)super.multiply(multiplicand);
	}

	/**
	 * Divide the quantity with a value
	 * @param divisor Divisor
	 * @return Result
	 */
	@Override
	public Distance divide(double divisor) {
		return (Distance)super.divide(divisor);
	}

	/**
	 * Divide the quantity with a value
	 * @param divisor Divisor
	 * @return Result
	 */
	@Override
	public Distance divide(BigDecimal divisor) {
		return (Distance)super.divide(divisor);
	}

	/**
	 * Reverses the sign of this quantity
	 * @return Negated value
	 */
	@Override
	public Distance negate() {
		return (Distance)super.negate();
	}

	/**
	 * Absolute value of this quantity.
	 *
	 * @return Absolute value
	 */
	@Override
	public Distance absolute() {
		return (Distance)super.absolute();
	}

	/**
	 * Create a rectangle by taking this as the length and the given parameter value as the width.
	 *
	 * @param width Width.
	 * @return Rectangular area.
	 */
	public Area rectangle(Distance width) {
		width = (Distance) width.convert(defaultUnit).multiply(convert(defaultUnit).getValue());
		return new Area(convert(defaultUnit).multiply(width.getValue()).getValue(), Area.defaultUnit);
	}

	/**
	 * Create a square by taking this as the side of the square.
	 *
	 * @return Square area.
	 */
	public Area square() {
		return rectangle(this);
	}

	/**
	 * Create a circle by taking this as the radius of the circle.
	 *
	 * @return Circular area.
	 */
	public Area circle() {
		return square().multiply(Quantity.PI);
	}

	/**
	 * Create a cube by taking this as the side of the cube.
	 *
	 * @return Cubic volume.
	 */
	public Volume cube() {
		return square().prism(this);
	}
}
