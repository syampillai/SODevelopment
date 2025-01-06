package com.storedobject.core;

import java.math.BigDecimal;

public class WeightOrVolume extends Quantity {

	static String AIRCRAFT = "✈";
	private final static String unit_galUS = "gal(US) ✈", unit_galUK = "gal(UK) ✈",
		unit_kg = "kg ✈", unit_lbs = "lb ✈", unit_l = "L ✈";
	private final static BigDecimal BD_GAL_US = new BigDecimal("2.9904739"), BD_GAL_UK = new BigDecimal("3.5914111"),
		BD_LBS = new BigDecimal("0.45359233"), BD_L = new BigDecimal("0.79");
	public static MeasurementUnit defaultUnit = MeasurementUnit.create(9, unit_kg, BigDecimal.ONE, "|kg");
	static {
		MeasurementUnit.create(9, unit_galUS, BD_GAL_US, "|GallonUS", "|GUS", "|USG", "|USGallon", "|GalUS");
		MeasurementUnit.create(9, unit_galUK, BD_GAL_UK, "|GallonUK", "|GUK", "|UKG", "|UKGallon", "|GalUK");
		MeasurementUnit.create(9, unit_lbs, BD_LBS, "|lbs");
		MeasurementUnit.create(9, unit_l, BD_L, "|lt", "|lit");
	}
	
	public WeightOrVolume() {
		this(BigDecimal.ZERO, defaultUnit);
	}

	public WeightOrVolume(double value, String unit) {
		this(BigDecimal.valueOf(value), unit);
	}

	public WeightOrVolume(double value, MeasurementUnit unit) {
		this(BigDecimal.valueOf(value), unit);
	}

	public WeightOrVolume(BigDecimal value, String unit) {
		this(value, MeasurementUnit.get(unit, WeightOrVolume.class));
	}

	public WeightOrVolume(BigDecimal value, MeasurementUnit unit) {
		super(value, unit);
	}
	
	/**
	 * Create a quantity of this type with zero value.
	 * @return Result
	 */
	@Override
	public WeightOrVolume zero() {
		return (WeightOrVolume)super.zero();
	}

	/**
	 * Add quantity value
	 *
	 * @param quantity The quantity value to add
     * @return Result
	 */
	@Override
	public WeightOrVolume add(String quantity) {
        return (WeightOrVolume)super.add(quantity);
	}

	/**
	 * Add quantity
	 *
	 * @param quantity The quantity value to add
     * @return Result
	 */
	@Override
	public WeightOrVolume add(BigDecimal quantity) {
        return (WeightOrVolume)super.add(quantity);
	}

	/**
	 * Add quantity
	 *
	 * @param quantity The quantity value to add
     * @return Result
	 */
	@Override
	public WeightOrVolume add(Quantity quantity) {
        return (WeightOrVolume)super.add(quantity);
	}
	
	/**
	 * Subtract quantity
	 *
	 * @param quantity The quantity value to subtract
     * @return Result
	 */
	@Override
	public WeightOrVolume subtract(String quantity) {
        return (WeightOrVolume)super.subtract(quantity);
	}

	/**
	 * Subtract quantity
	 *
	 * @param quantity The quantity value to subtract
     * @return Result
	 */
	@Override
	public WeightOrVolume subtract(BigDecimal quantity) {
        return (WeightOrVolume)super.subtract(quantity);
	}

	/**
	 * Subtract quantity
	 *
	 * @param quantity The quantity value to subtract
     * @return Result
	 */
	@Override
	public WeightOrVolume subtract(Quantity quantity) {
        return (WeightOrVolume)super.subtract(quantity);
	}

	/**
	 * Multiply
	 * @param multiplicand Multiplicand 
	 * @return Result
	 */
	@Override
	public WeightOrVolume multiply(BigDecimal multiplicand) {
        return (WeightOrVolume)super.multiply(multiplicand);
	}

	/**
	 * Multiply
	 * @param multiplicand Multiplicand 
	 * @return Result
	 */
	@Override
	public WeightOrVolume multiply(double multiplicand) {
        return (WeightOrVolume)super.multiply(multiplicand);
	}

	/**
	 * Divide the quantity with a value
	 * @param divisor Divisor
	 * @return Result
	 */
	@Override
	public WeightOrVolume divide(double divisor) {
        return (WeightOrVolume)super.divide(divisor);
	}

	/**
	 * Divide the quantity with a value
	 * @param divisor Divisor
	 * @return Result
	 */
	@Override
	public WeightOrVolume divide(BigDecimal divisor) {
        return (WeightOrVolume)super.divide(divisor);
	}

	/**
	 * Reverses the sign of this quantity
     * @return Negated value
	 */
	@Override
	public WeightOrVolume negate() {
        return (WeightOrVolume)super.negate();
	}
	
	/**
	 * Absolute value of this quantity.
	 * 
	 * @return Absolute value
	 */
	@Override
	public WeightOrVolume absolute() {
        return (WeightOrVolume)super.absolute();
	}

	/**
	 * Determines whether the current unit represents a weight type.
	 *
	 * @return true if the unit is either "kg" or "lbs", or false otherwise
	 */
	public boolean isWeightType() {
		return switch (getUnit().getUnit()) {
			case unit_kg, unit_lbs -> true;
			default -> false;
		};
	}
}
