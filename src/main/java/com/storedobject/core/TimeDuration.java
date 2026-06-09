package com.storedobject.core;

import com.storedobject.common.SORuntimeException;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

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

	public TimeDuration(double value, TimeUnit unit) {
		this(BigDecimal.valueOf(value), unit(unit));
	}

	public TimeDuration(BigDecimal value, TimeUnit unit) {
		this(value, unit(unit));
	}

	public static MeasurementUnit unit(TimeUnit unit) {
		return switch (unit) {
			case SECONDS -> defaultUnit;
			case DAYS -> MeasurementUnit.get("days", TimeDuration.class);
			case HOURS -> MeasurementUnit.get("hou", TimeDuration.class);
			case MINUTES -> MeasurementUnit.get("min", TimeDuration.class);
			default -> throw new SORuntimeException("Unsupported unit: " + unit);
		};
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

	/**
	 * Format the duration as a string of the form "D days HH:MM:SS".
	 * @return Formatted string
	 */
	public String format() {
		return format(true);
	}

	/**
	 * Format the duration as a string of the form "D days HH:MM:SS" or "HH:MM:SS".
	 * @param allowDays Allow days?
	 * @return Formatted string
	 */
	public String format(boolean allowDays) {
		String s = toString();
		return s.startsWith("0D ") ? s.substring(3) : s;
	}

	/**
	 * Format the duration as a string of the form "D days HH:MM:SS" or "HH:MM:SS".
	 * @param allowDays Allow days?
	 * @param stripSeconds Strip seconds?
	 * @return Formatted string
	 */
	public String format(boolean allowDays, boolean stripSeconds) {
		String s = format(allowDays);
		return stripSeconds ? s.substring(0, s.length() - 3) : s;
	}

	/**
	 * Retrieves the duration in seconds as a long value.
	 *
	 * @return The duration of this TimeDuration instance expressed in seconds.
	 */
	public long seconds() {
		return convert(defaultUnit).getValue().longValue();
	}

	/**
	 * Retrieves the duration in minutes as a long value.
	 *
	 * @return The duration of this TimeDuration instance expressed in minutes.
	 */
	public long minutes() {
		return convert(unit(TimeUnit.MINUTES)).getValue().longValue();
	}

	/**
	 * Retrieves the duration in hours as a long value.
	 *
	 * @return The duration of this TimeDuration instance expressed in the past hours.
	 */
	public long hours() {
		return convert(unit(TimeUnit.HOURS)).getValue().longValue();
	}

	/**
	 * Retrieves the duration in days as a long value.
	 *
	 * @return The duration of this TimeDuration instance expressed in the past days.
	 */
	public long days() {
		return convert(unit(TimeUnit.DAYS)).getValue().longValue();
	}
}