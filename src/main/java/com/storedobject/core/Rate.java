package com.storedobject.core;

import com.storedobject.common.SORuntimeException;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Class that represents a rate value such as currency rate etc. The default number of decimals is 6.
 * It can not be zero or negative.
 * This class is immutable.
 *
 * @author Syam
 */
public final class Rate extends DecimalNumber {
	
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
	 * Create a rate with given number of decimals.
	 * @param decimals Decimal places
	 *
	 * @param value The value
	 */
	public Rate(String value, int decimals) {
		super(value, decimals);
		check();
	}

	/**
	 * Construct a rate from a BigDecimal with given number of decimals.
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

	public static Rate create(Object value, int decimals) {
        try {
            return new Rate(value.toString(), decimals);
        } catch(Throwable ignored) {
        }
        throw new SORuntimeException("Invalid Rate '" + value + "'");
    }
	
	public void checkLimit(String name, int width) throws Invalid_Value {
        if(this.value.signum() <= 0) {
        	throw new Invalid_Value(name + " = " + getStorableValue());
        }
		super.checkLimit(name, width);
	}
	
	public boolean isOne() {
		return value.compareTo(BigDecimal.ONE) == 0;
	}
	
	public Rate average(BigDecimal another) {
		return new Rate(getAverageValue(another));
	}
	
	public Rate average(Rate second) {
		return average(second.getValue());
	}

	public Rate reverse() {
		return new Rate(BigDecimal.ONE.divide(getValue(), 6, RoundingMode.HALF_UP));
	}
}
