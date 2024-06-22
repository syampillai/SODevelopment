package com.storedobject.core;

import com.storedobject.common.SORuntimeException;
import com.storedobject.common.Storable;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

/**
 * Class that represents a decimal number value with a defined number of decimals digits.
 * This class is immutable.
 *
 * @author Syam
 */
public class DecimalNumber implements Storable, Comparable<DecimalNumber> {

	private final static BigDecimal TWO = BigDecimal.valueOf(2);
	private final static DecimalNumber[] zerosDN = new DecimalNumber[10];
	private final static BigDecimal[] zerosBD = new BigDecimal[10];
	public final static DecimalNumber ZERO = dn(0);
	protected BigDecimal value;

	/**
	 * Create a decimal number of value 0 with 2 decimal digits.
	 */
	public DecimalNumber() {
        this(BigDecimal.ZERO, 2);
	}

	/**
	 * Create a decimal number of value 0.
	 * 
	 * @param decimals Number of decimal places
	 */
	public DecimalNumber(int decimals) {
        this(BigDecimal.ZERO, decimals);
	}
	
	/**
	 * Create a decimal number. Number of decimal places will be taken from what is defined in the value.
	 * 
	 * @param value The value
	 */
	public DecimalNumber(String value) {
		this(new BigDecimal(value));
	}

	/**
	 * Create a decimal number.
	 * 
	 * @param value The value
	 * @param decimals Number of decimal places
	 */
	public DecimalNumber(String value, int decimals) {
        this(new BigDecimal(value), decimals);
	}

	/**
	 * Construct a decimal number from another one.
	 *
	 * @param number The decimal number to be set.
	 */
	public DecimalNumber(DecimalNumber number) {
        value = number.value;
	}
	

	/**
	 * Construct a decimal number from a BigDecimal. Number of decimal places will be taken from what is defined in the value.
	 *
	 * @param value The BigDecimal value.
	 */
	public DecimalNumber(BigDecimal value) {
		this(value, -1);
	}

	/**
	 * Construct a decimal number from a BigDecimal.
	 *
	 * @param value The BigDecimal value.
	 * @param decimals Number of decimal places
	 */
	public DecimalNumber(BigDecimal value, int decimals) {
		if(decimals < 0 || value.scale() == decimals) {
			this.value = value;
			return;
		}
		if(value.signum() == 0 && decimals < 10) {
			this.value = bd(decimals);
		} else {
			this.value = value.setScale(decimals, RoundingMode.HALF_UP);
		}
	}
	
	private static BigDecimal bd(int decimals) {
		if(zerosBD[decimals] == null) {
			zerosBD[decimals] = BigDecimal.ZERO.setScale(decimals, RoundingMode.HALF_UP);
		}
		return zerosBD[decimals];
	}
	
	private static DecimalNumber dn(int decimals) {
		if(zerosDN[decimals] == null) {
			zerosDN[decimals] = new DecimalNumber(BigDecimal.ZERO, decimals);
		}
		return zerosDN[decimals];
	}
	
	public static DecimalNumber create(Object value) {
		return create(value, -1);
	}

	public static DecimalNumber create(Object value, int decimals) {
        try {
        	if(value instanceof BigDecimal) {
        		if(value == BigDecimal.ZERO) {
        			value = BigInteger.ZERO;
				} else {
					return new DecimalNumber((BigDecimal) value, decimals);
				}
			}
        	if(value instanceof BigInteger) {
        		if(value == BigInteger.ZERO && decimals >= 0 && decimals <= 9) {
        			return dn(decimals);
				}
        		return new DecimalNumber(new BigDecimal((BigInteger)value), decimals);
			}
            return new DecimalNumber(value.toString(), decimals);
        } catch(Throwable ignored) {
        }
        throw new SORuntimeException("Invalid Decimal Number - '" + value + "'");
    }

    public static DecimalNumber zero(int decimals) {
		return create(BigInteger.ZERO, decimals);
	}

	/**
	 * Get the decimal number as BigDecimal.
	 *
	 * @return The value.
	 */
	public BigDecimal getValue() {
		return value;
	}

	@Override
	public boolean equals(Object another) {
        if(!(another instanceof DecimalNumber)) {
            return false;
        }
        return value.compareTo(((DecimalNumber)another).value) == 0;
	}

	@Override
	public int hashCode() {
		return value == null ? 0 : value.hashCode();
	}

	@Override
	public String toString() {
		return value.toPlainString();
	}

	/**
	 * Convert it into a String with at least minimum decimals places specified.
	 * <p>Example: Value: 4123.4500, Output: "4123.4500" (if decimals is les than zero, no conversion),
	 * "4123.450000" (for 6 decimals), "4123.45" (for 2 decimals or less)</p>
	 * <p>Example: Value: 4123.0000, Output: "4123.0000" (if decimals is les than zero, no conversion),
	 * "4123.000000" (for 6 decimals), "4123.00" (for 2 decimals), "4123" (if decimals is zero)</p>
	 * @param decimals Number of minimum decimals required in the output.
	 * @return String value.
	 */
	public String toString(int decimals) {
		String s = value.toPlainString();
		if(decimals < 0) {
			return s;
		}
		int p = s.indexOf('.');
		if(p < 0) {
			if(decimals == 0) {
				return s;
			}
			return s + "." + StringUtility.padRight("", decimals, '0');
		}
		while(s.endsWith("0")) {
			s = s.substring(0, s.length() - 1);
		}
		String right = s.substring(p + 1);
		if(right.length() >= decimals) {
			if(decimals == 0 && s.endsWith(".")) {
				return s.substring(0, s.length() - 1);
			}
			return s;
		}
		return s.substring(0, p + 1) + StringUtility.padRight(right, decimals, '0');
	}

	@Override
	public String getStorableValue() {
		return value.toPlainString();
	}

	@Override
	public int compareTo(DecimalNumber number) {
		return value.compareTo(number.value);
	}
	
	public boolean isZero() {
		return value.compareTo(BigDecimal.ZERO) == 0;
	}
	
	public int getDecimals() {
		return value.scale();
	}
	
	public DecimalNumber zero() {
		int d = getDecimals();
		if(d < 0 || d >= 10) {
			return new DecimalNumber(BigDecimal.ZERO, d);
		}
		return dn(d);
	}

	public void checkLimit(String name, int width) throws Invalid_Value {
		String s = getStorableValue();
		if(s.length() <= width) {
			return;
		}
		int d = getDecimals();
		String m;
		if(d > 0) {
			m = "." + StringUtility.padRight("", getDecimals(), '9');
		} else {
			m = "";
		}
		m = StringUtility.padLeft(m, width, '9');
		throw new Invalid_Value(name + " = " + s + " Limit exceeded. Maximum allowed value is " + m);
	}

	public BigDecimal getAverageValue(DecimalNumber second) {
		return getAverageValue(second.getValue());
	}
	
	public BigDecimal getAverageValue(BigDecimal second) {
		return value.add(second).divide(TWO, RoundingMode.HALF_UP);
	}
}
