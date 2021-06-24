package com.storedobject.core;

import java.math.BigDecimal;

public class Volume extends Quantity {

	public static final MeasurementUnit defaultUnit = MeasurementUnit.get("m3");

	public Volume() {
		this(BigDecimal.ZERO, defaultUnit);
	}

	public Volume(double value, String unit) {
		this(BigDecimal.valueOf(value), unit);
	}

	public Volume(double value, MeasurementUnit unit) {
		this(BigDecimal.valueOf(value), unit);
	}

	public Volume(BigDecimal value, String unit) {
		this(value, MeasurementUnit.get(unit, Volume.class));
	}

	public Volume(BigDecimal value, MeasurementUnit unit) {
		super(value, unit);
	}

	/**
	 * Box.
	 * @param length Length.
	 * @param width Width.
	 * @param height Height.
	 * @return Box volume.
	 */
	public static Volume box(Distance length, Distance width, Distance height) {
		return length.rectangle(width).prism(height);
	}

	/**
	 * Cube.
	 * @param side Side of the cube.
	 * @return Cubic volume.
	 */
	public static Volume cube(Distance side) {
		return side.square().prism(side);
	}

	/**
	 * Prism.
	 * @param base Base area of the prism.
	 * @param height Height of the prism.
	 * @return Prism volume.
	 */
	public static Volume prism(Area base, Distance height) {
		return base.prism(height);
	}

	/**
	 * Cone.
	 * @param radius Radius of the base area of the cone.
	 * @param height Height of the cone.
	 * @return Cone volume.
	 */
	public static Volume cone(Distance radius, Distance height) {
		return radius.circle().prism(height);
	}

	/**
	 * Get the volumetric weight of this volume. By default, volumetric weight factor of 4000 is used unless a
	 * {@link GlobalProperty} with the name "VOLUMETRIC-WEIGHT-FACTOR" is defined.
	 *
	 * @return Volumetric weight.
	 */
	public Weight getWeight() {
		int factor = GlobalProperty.getInteger("VOLUMETRIC-WEIGHT-FACTOR");
		BigDecimal f = BigDecimal.valueOf(factor == 0 ? 4000 : factor);
		return new Weight(convert(MeasurementUnit.get("cm3", Volume.class)).getValue().
				divide(f, PRECISION), MeasurementUnit.get("kg", Weight.class));
	}

	/**
	 * Create a quantity of this type with zero value.
	 * @return Result
	 */
	@Override
	public Volume zero() {
		return (Volume)super.zero();
	}

	/**
	 * Add quantity value
	 *
	 * @param quantity The quantity value to add
	 * @return Result
	 */
	@Override
	public Volume add(String quantity) {
		return (Volume)super.add(quantity);
	}

	/**
	 * Add quantity
	 *
	 * @param quantity The quantity value to add
	 * @return Result
	 */
	@Override
	public Volume add(BigDecimal quantity) {
		return (Volume)super.add(quantity);
	}

	/**
	 * Add quantity
	 *
	 * @param quantity The quantity value to add
	 * @return Result
	 */
	@Override
	public Volume add(Quantity quantity) {
		return (Volume)super.add(quantity);
	}

	/**
	 * Subtract quantity
	 *
	 * @param quantity The quantity value to subtract
	 * @return Result
	 */
	@Override
	public Volume subtract(String quantity) {
		return (Volume)super.subtract(quantity);
	}

	/**
	 * Subtract quantity
	 *
	 * @param quantity The quantity value to subtract
	 * @return Result
	 */
	@Override
	public Volume subtract(BigDecimal quantity) {
		return (Volume)super.subtract(quantity);
	}

	/**
	 * Subtract quantity
	 *
	 * @param quantity The quantity value to subtract
	 * @return Result
	 */
	@Override
	public Volume subtract(Quantity quantity) {
		return (Volume)super.subtract(quantity);
	}

	/**
	 * Multiply
	 * @param multiplicand Multiplicand
	 * @return Result
	 */
	@Override
	public Volume multiply(BigDecimal multiplicand) {
		return (Volume)super.multiply(multiplicand);
	}

	/**
	 * Multiply
	 * @param multiplicand Multiplicand
	 * @return Result
	 */
	@Override
	public Volume multiply(double multiplicand) {
		return (Volume)super.multiply(multiplicand);
	}

	/**
	 * Divide the quantity with a value
	 * @param divisor Divisor
	 * @return Result
	 */
	@Override
	public Volume divide(double divisor) {
		return (Volume)super.divide(divisor);
	}

	/**
	 * Divide the quantity with a value
	 * @param divisor Divisor
	 * @return Result
	 */
	@Override
	public Volume divide(BigDecimal divisor) {
		return (Volume)super.divide(divisor);
	}

	/**
	 * Reverses the sign of this quantity
	 * @return Negated value
	 */
	@Override
	public Volume negate() {
		return (Volume)super.negate();
	}

	/**
	 * Absolute value of this quantity.
	 *
	 * @return Absolute value
	 */
	@Override
	public Volume absolute() {
		return (Volume)super.absolute();
	}
}
