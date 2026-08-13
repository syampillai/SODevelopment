package com.storedobject.core;

import com.storedobject.common.SORuntimeException;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Class that represents a rate value such as currency rate etc. The default number of decimals is 6.
 * It cannot be zero or negative.
 * This class is immutable.
 *
 * @author Syam
 */
public final class Rate extends DecimalNumber {

	/**
	 * A constant representing a rate of value 1.
	 * It is a predefined instance of the {@code Rate} class, providing a convenient way to reference
	 * a standard rate of 1 without the need for explicit instantiation.
	 */
	public static final Rate ONE = new Rate();

	/**
	 * Create a rate of value 1.
	 *
	 */
	public Rate() {
        super(BigDecimal.ONE);
		check();
	}

	/**
	 * Create a rate.
	 *
	 * @param value The value
	 */
	public Rate(String value) {
		super(value, 6);
		check();
	}

	/**
	 * Construct a rate from another one.
	 *
	 * @param rate The rate to be set.
	 */
	public Rate(Rate rate) {
        value = rate.value;
	}

	/**
	 * Construct a rate from a BigDecimal.
	 *
	 * @param value The BigDecimal value.
	 */
	public Rate(BigDecimal value) {
		super(value, 6);
		check();
	}
	
	/**
	 * Create a rate of value 1 with given number of decimals.
	 * @param decimals Decimal places
	 *
	 */
	public Rate(int decimals) {
        super(BigDecimal.ONE, decimals);
		check();
	}

	/**
	 * Create a rate with a given number of decimals.
	 * @param decimals Decimal places
	 *
	 * @param value The value
	 */
	public Rate(String value, int decimals) {
		super(value, decimals);
		check();
	}

	/**
	 * Construct a rate from a BigDecimal with a given number of decimals.
	 * @param decimals Decimal places
	 *
	 * @param value The BigDecimal value.
	 */
	public Rate(BigDecimal value, int decimals) {
		super(value, decimals);
		check();
	}

	/**
	 * Construct a rate from the monetary values passed.
	 * <p>Note: A rate is created by dividing "from" by "to". Only the values are considered, not the currency.</p>
	 * @param from Monetary value.
	 * @param to Monetary value.
	 */
	public Rate(Money from, Money to) {
		super(from.getValue().abs().divide(to.getValue().abs(), 6, RoundingMode.HALF_UP), 6);
	}

	/**
	 * Construct a rate from the values passed.
	 * <p>Note: A rate is created by dividing "from" by "to".</p>
	 * @param from Monetary value.
	 * @param to Monetary value.
	 */
	public Rate(BigDecimal from, BigDecimal to) {
		super(from.abs().divide(to.abs(), 6, RoundingMode.HALF_UP), 6);
	}

	private void check() {
        if(this.value.signum() < 0) {
			throw new SORuntimeException("Invalid Rate '" + value.toPlainString() + "'");
		}
        if(this.value.signum() == 0) {
        	value = BigDecimal.ONE;
        }
	}

	/**
	 * Creates a new Rate object based on the specified value and decimal precision.
	 * The method supports various types for the value parameter:
	 * - If the value is an instance of ExchangeRate, the method returns the rate obtained from the ExchangeRate object.
	 * - If the value is an instance of CurrencyRate, the method returns the rate obtained from the CurrencyRate object.
	 * - Otherwise, it attempts to construct a new Rate instance by converting the value to a string and using the specified decimal precision.
	 *
	 * @param value The value used to create the Rate. This can be an instance of ExchangeRate, CurrencyRate, or another object.
	 * @param decimals The number of decimal places for the Rate.
	 * @return A Rate object created based on the value and decimals.
	 * @throws SORuntimeException if the provided value cannot be used to create a valid Rate.
	 */
	public static Rate create(Object value, int decimals) {
		if(value instanceof ExchangeRate er) {
			return er.getRate();
		} else if(value instanceof CurrencyRate cr) {
			return cr.getRate();
		}
        try {
            return new Rate(value.toString(), decimals);
        } catch(Throwable ignored) {
        }
        throw new SORuntimeException("Invalid Rate '" + value + "'");
    }

	/**
	 * Validates the current value of the instance and ensures it meets specified constraints.
	 * If the value is negative or zero, it throws an {@link Invalid_Value} exception with an
	 * appropriate error message. Additionally, it delegates to the superclass to check whether
	 * the storable value exceeds a specified width.
	 *
	 * @param name A name or identifier for the value being checked, used in error reporting.
	 * @param width The maximum allowable width (number of characters) for the storable value.
	 * @throws Invalid_Value If the current value is non-positive or if the storable value's
	 *                       length exceeds the specified width.
	 */
	public void checkLimit(String name, int width) throws Invalid_Value {
        if(this.value.signum() <= 0) {
        	throw new Invalid_Value(name + " = " + getStorableValue());
        }
		super.checkLimit(name, width);
	}

	/**
	 * Checks if the current value of the rate is equal to one.
	 *
	 * @return true if the value of the rate is exactly one, false otherwise
	 */
	public boolean isOne() {
		return value.compareTo(BigDecimal.ONE) == 0;
	}

	/**
	 * Calculates the average of the current rate and the specified BigDecimal value.
	 *
	 * @param another The BigDecimal value to be averaged with the current rate.
	 * @return A new Rate instance representing the average of the current rate and the specified value.
	 */
	public Rate average(BigDecimal another) {
		return new Rate(getAverageValue(another));
	}

	/**
	 * Calculates the average of the current rate and the specified rate.
	 *
	 * @param second The Rate object to be averaged with the current rate.
	 * @return A new Rate instance representing the average of the current rate and the specified rate.
	 */
	public Rate average(Rate second) {
		return average(second.getValue());
	}

	/**
	 * Returns a new Rate object that is the mathematical reciprocal of the current rate.
	 * The reciprocal is calculated by dividing 1 by the value of the current rate.
	 * The resulting rate is rounded to 6 decimal places using HALF_UP rounding mode.
	 *
	 * @return A new Rate instance representing the reciprocal of the current rate.
	 */
	public Rate reverse() {
		return new Rate(BigDecimal.ONE.divide(getValue(), 6, RoundingMode.HALF_UP));
	}
}
