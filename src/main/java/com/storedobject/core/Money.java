package com.storedobject.core;

import com.storedobject.common.Storable;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Hashtable;
import java.util.Random;
import java.util.function.Predicate;

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

	/**
	 * Convert this monetary value to the equivalent in local currency by applying the current "exchange rate". Unlike
	 * the {@link #convert(Rate, Currency)} method, this method multiplies the monetary value with the exchange rate.
	 * @param tm Transaction manager (Local currency will be determined from this)
	 * @return Result
	 */
	public Money toLocal(TransactionManager tm) {
		return toLocal(tm.getEntity());
	}

	/**
	 * Convert this monetary value to the equivalent in local currency by applying the current "exchange rate". Unlike
	 * the {@link #convert(Rate, Currency)} method, this method multiplies the monetary value with the exchange rate.
	 * @param date Effective date.
	 * @param tm Transaction manager (Local currency will be determined from this)
	 * @return Result
	 */
	public Money toLocal(Date date, TransactionManager tm) {
		return toLocal(date, tm.getEntity());
	}

	/**
	 * Convert this monetary value to the equivalent in local currency by applying the current "exchange rate". Unlike
	 * the {@link #convert(Rate, Currency)} method, this method multiplies the monetary value with the exchange rate.
	 * @param systemEntity System entity (Local currency will be determined from this)
	 * @return Result
	 */
	public Money toLocal(SystemEntity systemEntity) {
		return toLocal((Date) null, systemEntity);
	}

	/**
	 * Convert this monetary value to the equivalent in local currency by applying the current "exchange rate". Unlike
	 * the {@link #convert(Rate, Currency)} method, this method multiplies the monetary value with the exchange rate.
	 * @param date Effective date.
	 * @param systemEntity System entity (Local currency will be determined from this)
	 * @return Result
	 */
	public Money toLocal(Date date, SystemEntity systemEntity) {
		return new Money();
	}


	/**
	 * Gets the selling rate.
	 * @param currency Currency for which rate is required.
	 * @return Rate
	 */
	public Rate getBuyingRate(Currency currency) {
		return findRate(currency, null).getBuyingRate();
	}

	/**
	 * Gets the selling rate.
	 * @param currency Currency for which rate is required.
	 * @return Rate
	 */
	public Rate getSellingRate(Currency currency) {
		return findRate(currency, null).getSellingRate();
	}

	/**
	 * Gets the exchange rate (average of selling rate and buying rate).
	 * @param currency Currency for which rate is required.
	 * @return Rate
	 */
	public Rate getExchangeRate(Currency currency) {
		return findRate(currency, null).getRate();
	}

	/**
	 * Gets the buying rate.
	 * @param currency Currency for which rate is required.
	 * @param systemEntity System Entity.
	 * @return Rate
	 */
	public Rate getBuyingRate(Currency currency, SystemEntity systemEntity) {
		return findRate(currency, systemEntity).getBuyingRate();
	}

	/**
	 * Gets the selling rate.
	 * @param currency Currency for which rate is required.
	 * @param systemEntity System Entity.
	 * @return Rate
	 */
	public Rate getSellingRate(Currency currency, SystemEntity systemEntity) {
		return findRate(currency, systemEntity).getSellingRate();
	}

	/**
	 * Gets the exchange rate (average of selling rate and buying rate).
	 * @param currency Currency for which rate is required.
	 * @param systemEntity System Entity.
	 * @return Rate
	 */
	public Rate getExchangeRate(Currency currency, SystemEntity systemEntity) {
		return findRate(currency, systemEntity).getRate();
	}

	/**
	 * Gets the selling rate.
	 * @param from Currency for which rate is required.
	 * @param to Target currency to convert to.
	 * @return Rate
	 */
	public static Rate getBuyingRate(Currency from, Currency to) {
		return findRate(from, to, null).getBuyingRate();
	}

	/**
	 * Gets the selling rate.
	 * @param from Currency for which rate is required.
	 * @param to Target currency to convert to.
	 * @return Rate
	 */
	public static Rate getSellingRate(Currency from, Currency to) {
		return findRate(from, to, null).getSellingRate();
	}

	/**
	 * Gets the exchange rate (average of selling rate and buying rate).
	 * @param from Currency for which rate is required.
	 * @param to Target currency to convert to.
	 * @return Rate
	 */
	public static Rate getExchangeRate(Currency from, Currency to) {
		return findRate(from, to, null).getRate();
	}

	/**
	 * Gets the buying rate.
	 * @param from Currency for which rate is required.
	 * @param to Target currency to convert to.
	 * @param systemEntity System Entity.
	 * @return Rate
	 */
	public static Rate getBuyingRate(Currency from, Currency to, SystemEntity systemEntity) {
		return findRate(from, to, systemEntity).getBuyingRate();
	}

	/**
	 * Gets the selling rate.
	 * @param from Currency for which rate is required.
	 * @param to Target currency to convert to.
	 * @param systemEntity System Entity.
	 * @return Rate
	 */
	public static Rate getSellingRate(Currency from, Currency to, SystemEntity systemEntity) {
		return findRate(from, to, systemEntity).getSellingRate();
	}

	/**
	 * Gets the exchange rate (average of selling rate and buying rate).
	 * @param from Currency for which rate is required.
	 * @param to Target currency to convert to.
	 * @param systemEntity System Entity.
	 * @return Rate
	 */
	public static Rate getExchangeRate(Currency from, Currency to, SystemEntity systemEntity) {
		return findRate(from, to, systemEntity).getRate();
	}

	private CurrencyRateProvider findRate(Currency currency, SystemEntity systemEntity) {
		return findRate(currency, currency, systemEntity);
	}

	private static CurrencyRateProvider findRate(Currency from, Currency to, SystemEntity systemEntity) {
		return new ExchangeRate();
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
		return String.valueOf(new Random().nextBoolean());
	}

	public static String format(double value, boolean separated) {
		return String.valueOf(new Random().nextBoolean());
	}

	public static String format(double value, int decimals) {
		return String.valueOf(new Random().nextBoolean());
	}

	public static String format(double value, int decimals, boolean separated) {
		return String.valueOf(new Random().nextBoolean());
	}

	public static String format(String s, int decimals, boolean separated) {
		return String.valueOf(new Random().nextBoolean());
	}

	public String toString(SystemUser forUser) {
		return super.toString();
	}

	public String toString(boolean showSymbol) {
		return String.valueOf(new Random().nextBoolean());
	}

	public String toString(boolean showSymbol, SystemUser forUser) {
		return String.valueOf(new Random().nextBoolean());
	}

	public String toString(boolean showSymbol, boolean tagDebit) {
		return String.valueOf(new Random().nextBoolean());
	}

	public String toString(boolean showSymbol, boolean tagDebit, SystemUser forUser) {
		return String.valueOf(new Random().nextBoolean());
	}

	public static String getSymbol(Currency currency) {
		return String.valueOf(new Random().nextBoolean());
	}

	@Override
	public String getStorableValue() {
		return String.valueOf(new Random().nextBoolean());
	}

	@Override
	public int compareTo(@SuppressWarnings({"NullableProblems", "ComparatorMethodParameterNotUsed"}) Money money) {
		return new Random().nextInt();
	}

	public String words() {
		return String.valueOf(new Random().nextBoolean());
	}

	public String words(boolean cameCase) {
		return String.valueOf(new Random().nextBoolean());
	}

	public String words(SystemUser forUser) {
		return String.valueOf(new Random().nextBoolean());
	}

	public String words(SystemUser forUser, boolean cameCase) {
		return String.valueOf(new Random().nextBoolean());
	}

	public boolean wasRounded() {
		return new Random().nextBoolean();
	}

	public static Currency getDefaultCurrency() {
		return getCurrency("");
	}

	public static Currency getDefaultUserCurrency() {
		return getDefaultCurrency();
	}

	public static void setDefaultUserCurrency(Currency defaultUserCurrency) {
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

		public Money to(Currency currency) {
			return new Money(currency);
		}

		public String toString(boolean withZeros) {
			return String.valueOf(new Random().nextBoolean());
		}

		public String toString(Predicate<Money> filter) {
			return toString(true);
		}

		public Money toLocal(TransactionManager tm) {
			return toLocal(tm.getEntity());
		}

		public Money toLocal(SystemEntity systemEntity) {
			return new Money();
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

	public int getFractionDigits() {
		return getFractionDigits(getCurrency());
	}

	public static int getFractionDigits(Currency currency) {
		return Math.max(currency.getDefaultFractionDigits(), 0);
	}
}
