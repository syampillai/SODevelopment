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

	/**
	 * Creates a new instance of DecimalNumber based on the provided value.
	 *
	 * @param value the input object to be converted or used for creating a DecimalNumber
	 * @return a DecimalNumber instance based on the input value
	 */
	public static DecimalNumber create(Object value) {
		return create(value, -1);
	}

	/**
	 * Creates a new instance of DecimalNumber based on the provided value and decimals.
	 *
	 * @param value the input value used to create the DecimalNumber. It can be an instance of
	 *              BigDecimal, BigInteger, or other types that can be converted to a valid number.
	 * @param decimals the number of decimal places for the DecimalNumber. This value determines
	 *                 the precision of the resulting number.
	 * @return a new instance of DecimalNumber based on the provided value and decimals if
	 *         the input is valid and processed successfully.
	 * @throws SORuntimeException if the input value cannot be converted into a valid DecimalNumber.
	 */
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

	/**
	 * Creates and returns a DecimalNumber instance representing the value zero
	 * with the specified number of decimal places.
	 *
	 * @param decimals The number of decimal places for the returned DecimalNumber.
	 *                 Must be a non-negative integer.
	 * @return A DecimalNumber instance with a value of zero and the specified number of decimal places.
	 */
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

	/**
	 * Compares the value of the current DecimalNumber with the value of the specified DecimalNumber.
	 *
	 * @param number The DecimalNumber to compare against.
	 * @return A negative integer, zero, or a positive integer as the current DecimalNumber
	 *         is less than, equal to, or greater than the specified DecimalNumber.
	 */
	@Override
	public int compareTo(DecimalNumber number) {
		return value.compareTo(number.value);
	}

	/**
	 * Checks whether the current value of the DecimalNumber is equal to zero.
	 *
	 * @return {@code true} if the current value is equal to zero, {@code false} otherwise.
	 */
	public boolean isZero() {
		return value.compareTo(BigDecimal.ZERO) == 0;
	}

	/**
	 * Retrieves the number of decimal places (scale) defined in the value of this DecimalNumber.
	 *
	 * @return The number of decimal places as an integer.
	 */
	public int getDecimals() {
		return value.scale();
	}

	/**
	 * Creates and returns a DecimalNumber instance representing the value zero.
	 * The number of decimal places is determined by the current instance's decimal scale.
	 * If the number of decimals is invalid (less than 0 or greater than or equal to 10),
	 * a new DecimalNumber is constructed with a value of zero and the specified decimal places.
	 * Otherwise, a cached zero value with the appropriate decimal scale is returned.
	 *
	 * @return A DecimalNumber instance representing zero, with the appropriate decimal scale.
	 */
	public DecimalNumber zero() {
		int d = getDecimals();
		if(d < 0 || d >= 10) {
			return new DecimalNumber(BigDecimal.ZERO, d);
		}
		return dn(d);
	}

	/**
	 * Checks whether the length of the storable value of the current DecimalNumber instance
	 * exceeds the specified width. If the length exceeds the width, an {@link Invalid_Value}
	 * exception is thrown with an appropriate error message indicating the limit exceeded.
	 *
	 * @param name A name or identifier for the value being checked, used in error reporting.
	 * @param width The maximum allowable width (number of characters) for the storable value.
	 * @throws Invalid_Value If the storable value's length exceeds the specified width.
	 */
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

	/**
	 * Calculates the average value of the current DecimalNumber and the specified DecimalNumber.
	 *
	 * @param second The DecimalNumber to be averaged with the current DecimalNumber.
	 * @return The average value as a BigDecimal.
	 */
	public BigDecimal getAverageValue(DecimalNumber second) {
		return getAverageValue(second.getValue());
	}

	/**
	 * Calculates the average value of the current value and the specified BigDecimal value.
	 *
	 * @param second The BigDecimal value to be averaged with the current value.
	 * @return The average value as a BigDecimal, calculated using rounding mode {@code RoundingMode.HALF_UP}.
	 */
	public BigDecimal getAverageValue(BigDecimal second) {
		return value.add(second).divide(TWO, RoundingMode.HALF_UP);
	}

	/**
	 * Compares the value of the current DecimalNumber with another BigInteger value
	 * to determine whether they are the same. The comparison considers values
	 * with equivalent numerical representations, regardless of insignificant trailing zeros.
	 *
	 * @param another The {@code BigInteger} to compare against.
	 * @return {@code true} if the values are numerically the same, {@code false} otherwise.
	 */
	public boolean isSameValue(BigInteger another) {
		return isSameValue(new BigDecimal(another));
	}

	/**
	 * Compares the value of the current DecimalNumber with another BigDecimal value
	 * to determine whether they are the same. The comparison considers values
	 * with equivalent numerical representations, regardless of insignificant trailing zeros.
	 *
	 * @param another The {@code BigDecimal} to compare against.
	 * @return {@code true} if the values are numerically the same, {@code false} otherwise.
	 */
	public boolean isSameValue(BigDecimal another) {
		String v1 = trim(value.toPlainString());
		String v2 = trim(another.toPlainString());
		return v1.equals(v2);
	}

	/**
	 * Compares the value of the current DecimalNumber with the value of another DecimalNumber.
	 *
	 * @param another The DecimalNumber to compare against.
	 * @return {@code true} if the values of the two DecimalNumbers are the same, {@code false} otherwise.
	 */
	public boolean isSameValue(DecimalNumber another) {
		return isSameValue(another.getValue());
	}

	private static String trim(String v) {
		if(v.contains(".")) {
			while (v.endsWith("0")) {
				v = v.substring(0, v.length() - 1);
			}
		}
		if(v.endsWith(".")) {
			v = v.substring(0, v.length() - 1);
		}
		return v;
	}

	/**
	 * Checks if the difference between the current decimal number's value and the value of the specified
	 * decimal number is within the given tolerance level.
	 *
	 * @param another The decimal number to compare against.
	 * @param tolerance The tolerance level, represented as a {@link Percentage}.
	 * @return {@code true} if the absolute difference between the values is within the calculated tolerance,
	 *         {@code false} otherwise.
	 */
	public boolean checkTolerance(DecimalNumber another, Percentage tolerance) {
		return checkTolerance(another.value, tolerance);
	}

	/**
	 * Checks if the difference between the current value and the specified value is within the specified tolerance level.
	 *
	 * @param another The value to compare against, represented as a {@link BigDecimal}.
	 * @param tolerance The tolerance level, represented as a {@link Percentage}.
	 * @return {@code true} if the absolute difference between the values is within the calculated tolerance, {@code false} otherwise.
	 */
	public boolean checkTolerance(BigDecimal another, Percentage tolerance) {
		BigDecimal delta = value.multiply(tolerance.getPercentageBy100());
		return value.subtract(another).abs().compareTo(delta) <= 0;
	}
}
