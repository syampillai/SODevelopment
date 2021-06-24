package com.storedobject.core;

import com.storedobject.common.Storable;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Class that represents a decimal number value with a defined number of decimals digits.
 * This class is immutable.
 */
public class DecimalNumber implements Storable, Comparable<DecimalNumber> {

	public static final DecimalNumber ZERO = new DecimalNumber(0);

	/**
	 * Create a decimal number of value 0 with 2 decimal digits.
	 */
	public DecimalNumber() {
	}

	/**
	 * Create a decimal number of value 0.
	 * 
	 * @param decimals Number of decimal places
	 */
	public DecimalNumber(int decimals) {
	}
	
	/**
	 * Create a decimal number. Number of decimal places will be taken from what is defined in the value.
	 * 
	 * @param value The value
	 */
	public DecimalNumber(String value) {
	}

	/**
	 * Create a decimal number.
	 * 
	 * @param value The value
	 * @param decimals Number of decimal places
	 */
	public DecimalNumber(String value, int decimals) {
	}

	/**
	 * Construct a decimal number from another one.
	 *
	 * @param number The decimal number to be set.
	 */
	public DecimalNumber(DecimalNumber number) {
	}
	

	/**
	 * Construct a decimal number from a BigDecimal. Number of decimal places will be taken from what is defined in the value.
	 *
	 * @param value The BigDecimal value.
	 */
	public DecimalNumber(BigDecimal value) {
	}

	/**
	 * Construct a decimal number from a BigDecimal.
	 *
	 * @param value The BigDecimal value.
	 * @param decimals Number of decimal places
	 */
	public DecimalNumber(BigDecimal value, int decimals) {
	}
	
	public static DecimalNumber create(Object value) {
		return null;
	}

	public static DecimalNumber create(Object value, int decimals) {
		return null;
    }

	public static DecimalNumber zero(int decimals) {
		return null;
	}

	/**
	 * Get the decimal number as BigDecimal.
	 *
	 * @return The value.
	 */
	public BigDecimal getValue() {
		return null;
	}

	public String getStorableValue() {
		return null;
	}

	@Override
	public int compareTo(@SuppressWarnings("NullableProblems") DecimalNumber number) {
		return 0;
	}
	
	public boolean isZero() {
		return false;
	}
	
	public int getDecimals() {
		return 0;
	}
	
	public DecimalNumber zero() {
		return null;
	}
	
	public void checkLimit(String name, int width) throws Invalid_Value {
	}
	
	public BigDecimal getAverageValue(DecimalNumber second) {
		return null;
	}
	
	public BigDecimal getAverageValue(BigDecimal second) {
		return null;
	}
}
