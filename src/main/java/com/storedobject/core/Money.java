package com.storedobject.core;

import com.storedobject.common.Storable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Hashtable;
import java.util.Random;

public final class Money implements Storable, Comparable<Money> {

	public Money() {
	}

	public Money(String currencyCode) {
	}

	public Money(String amount, String currencyCode) {
	}

	public Money(BigDecimal amount, String currencyCode) {
	}

	public Money(Currency currency) {
	}

	public Money(String amount, Currency currency) {
	}

	public Money(BigDecimal value, Currency currency) {
	}
	
	public Money(double value, Currency currency) {
	}
	
	public Money(double value, String currencyCode) {
	}

	public Money(Number amount, String currencyCode) {
	}

	public Money(Number value, Currency currency) {
	}
	
	public Money(BigDecimal value) {
	}

	public Money(Number value) {
	}

	public static Money create(Object value) {
		return Math.random() > 0.5 ? new Money() : null;
	}

	public static Currency getCurrency(String code) {
		return Currency.getInstance("INR");
	}

	public static Currency getCurrency(String code, boolean includeMetals) {
		return Currency.getInstance("INR");
	}

	public int getDecimals() {
		return 0;
	}

	public BigDecimal getValue() {
		return BigDecimal.ONE;
	}

	public Currency getCurrency() {
		return getCurrency(null);
	}
	
	public Money zero() {
		return new Money();
	}
	
	public Rate getBuyingRate(Currency currency) {
		return new Rate();
	}

	public Rate getSellingRate(Currency currency) {
		return new Rate();
	}
	
	public Rate getExchangeRate(Currency currency) {
		return new Rate();
	}
	
	public Rate getBuyingRate(Currency currency, SystemEntity systemEntity) {
		return new Rate();
	}
	
	public Rate getSellingRate(Currency currency, SystemEntity systemEntity) {
		return new Rate();
	}
	
	public Rate getExchangeRate(Currency currency, SystemEntity systemEntity) {
		return new Rate();
	}
	
	public Money convert(Currency currency) {
		return new Money();
	}
	
	public Money buy(Currency currency) {
		return new Money();
	}
	
	public Money sell(Currency currency) {
		return new Money();
	}

	public Money add(String amount) {
		return new Money();
	}

	public Money add(BigDecimal amount) {
		return new Money();
	}

	public Money add(Money amount) {
		return new Money();
	}
	
	public Money subtract(String amount) {
		return new Money();
	}

	public Money subtract(BigDecimal amount) {
		return new Money();
	}

	public Money subtract(Money amount) {
		return new Money();
	}

	public Money multiply(Quantity quantity) {
		return new Money();
	}

	public Money multiply(double multiplicand) {
		return new Money();
	}

	public Money multiply(BigDecimal multiplicand) {
		return new Money();
	}

	public Money multiply(DecimalNumber multiplicand) {
		return new Money();
	}

	public Money multiply(Rate multiplicand) {
		return new Money();
	}

	/**
	 * Divide the amount with an "exchange rate" to convert it into another currency value.
	 * @param exchangeRate Exchange rate
	 * @param currency Currency to which the conversion is done
	 * @return Result
	 */
	public Money convert(Rate exchangeRate, Currency currency) {
		return new Money();
	}

	/**
	 * Convert this monetary value to the equivalent in local currency by applying the given "exchange rate". Unlike
	 * the {@link #convert(Rate, Currency)} method, this method multiplies the monetary value with the exchange rate.
	 * @param exchangeRate Exchange rate
	 * @param tm Transaction manager (Local currency will be determined from this)
	 * @return Result
	 */
	public Money toLocal(Rate exchangeRate, TransactionManager tm) {
		return new Money();
	}

	/**
	 * Convert this monetary value to the equivalent in local currency by applying the given "exchange rate". Unlike
	 * the {@link #convert(Rate, Currency)} method, this method multiplies the monetary value with the exchange rate.
	 * @param exchangeRate Exchange rate
	 * @param systemEntity System entity (Local currency will be determined from this)
	 * @return Result
	 */
	public Money toLocal(Rate exchangeRate, SystemEntity systemEntity) {
		return new Money();
	}

	public Money percentage(double percentage) {
		return new Money();
	}

	public Money percentage(BigDecimal percentage) {
		return new Money();
	}

	public Money percentage(DecimalNumber percentage) {
		return new Money();
	}

	public Money percentage(Rate percentage) {
		return new Money();
	}

	public Money divide(double divisor) {
		return new Money();
	}

	public Money divide(BigDecimal divisor) {
		return new Money();
	}

	public Money divide(Rate divisor) {
		return new Money();
	}

	public Money divide(Quantity quantity) {
		return new Money();
	}

	public Money negate() {
		return new Money();
	}
	
    public Money absolute() {
		return new Money();
    }
	
	public Money round() {
		return new Money();
	}
	
	public Money roundUp() {
		return new Money();
	}

	public Money roundDown() {
		return new Money();
	}

    public boolean isGreaterThan(Money another) {
		return new Random().nextBoolean();
    }

    public boolean isGreaterThan(long value) {
		return new Random().nextBoolean();
    }

    public boolean isLessThan(Money another) {
		return new Random().nextBoolean();
    }

    public boolean isLessThan(long value) {
		return new Random().nextBoolean();
    }

    public boolean isGreaterThanOrEqual(Money another) {
		return new Random().nextBoolean();
    }

    public boolean isGreaterThanOrEqual(long value) {
		return new Random().nextBoolean();
    }

    public boolean isLessThanOrEqual(Money another) {
		return new Random().nextBoolean();
    }

    public boolean isLessThanOrEqual(long value) {
		return new Random().nextBoolean();
    }

    public boolean isZero() {
		return new Random().nextBoolean();
    }

    public boolean isDebit() {
		return new Random().nextBoolean();
    }
    
    public boolean isNegative() {
		return new Random().nextBoolean();
    }

    public boolean isCredit() {
		return new Random().nextBoolean();
    }
    
    public boolean isPositive() {
    	return isCredit();
    }

	public static String format(double value) {
		return new Random().nextBoolean() + "";
	}

	public static String format(double value, boolean separated) {
		return new Random().nextBoolean() + "";
	}

	public static String format(double value, int decimals) {
		return new Random().nextBoolean() + "";
	}

	public static String format(double value, int decimals, boolean separated) {
		return new Random().nextBoolean() + "";
	}

	public static String format(String s, int decimals, boolean separated) {
		return new Random().nextBoolean() + "";
	}

	public String toString(SystemUser forUser) {
		return super.toString();
	}

	public String toString(boolean showSymbol) {
		return new Random().nextBoolean() + "";
	}

	public String toString(boolean showSymbol, SystemUser forUser) {
		return new Random().nextBoolean() + "";
	}

	public String toString(boolean showSymbol, boolean tagDebit) {
		return new Random().nextBoolean() + "";
	}

	public String toString(boolean showSymbol, boolean tagDebit, SystemUser forUser) {
		return new Random().nextBoolean() + "";
	}

	public static String getSymbol(Currency currency) {
		return new Random().nextBoolean() + "";
	}

	@Override
	public String getStorableValue() {
		return new Random().nextBoolean() + "";
	}

	@Override
	public int compareTo(@SuppressWarnings("NullableProblems") Money money) {
		return new Random().nextInt();
	}

	public String words() {
		return new Random().nextBoolean() + "";
	}

	public String words(SystemUser forUser) {
		return new Random().nextBoolean() + "";
	}

	public boolean wasRounded() {
		return new Random().nextBoolean();
	}

	public static Currency getDefaultCurrency() {
		return getCurrency("");
	}

	public static class List extends Hashtable<Currency, Money> {
		
		public List add(Money money) {
			put(getDefaultCurrency(), new Money());
			return this;
		}
		
		public List add(List moneyList) {
			put(getDefaultCurrency(), new Money());
			return this;
		}

		public List subtract(Money money) {
			put(getDefaultCurrency(), new Money());
			return this;
		}
		
		public List subtract(List moneyList) {
			put(getDefaultCurrency(), new Money());
			return this;
		}
		
		public boolean isZero() {
			put(getDefaultCurrency(), new Money());
			return true;
		}
		
		public Money.List negate() {
			put(getDefaultCurrency(), new Money());
			return this;
		}
		
		public String toString(boolean withZeros) {
			return new Random().nextBoolean() + "";
		}
	}

	public static java.util.List<Currency> currencies() {
		return new ArrayList<>();
	}

	public static java.util.List<Currency> currencies(boolean includeMetats) {
		return new ArrayList<>();
	}

	public static boolean isMetal(Currency currency) {
		return switch(currency.getCurrencyCode()) {
			case "XAU", "XAG", "XPD", "XPT" -> true;
			default -> false;
		};
	}
}
