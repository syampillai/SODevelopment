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

	/**
	 * Multiply the amount with a value
	 * @param multiplicand Multiplicand
	 * @return Result
	 */
	public Money multiply(double multiplicand) {
		return new Money();
	}

	/**
	 * Multiply the amount with a value and convert to another currency.
	 * @param multiplicand Multiplicand
	 * @return Result
	 */
	public Money multiply(double multiplicand, Currency currency) {
		return new Money();
	}

	/**
	 * Multiply the amount with a value
	 * @param multiplicand Multiplicand
	 * @return Result
	 */
	public Money multiply(BigDecimal multiplicand) {
		return new Money();
	}

	/**
	 * Multiply the amount with a rate and convert to another currency.
	 * @param multiplicand Multiplicand
	 * @param currency Currency to convert to.
	 * @return Result
	 */
	public Money multiply(BigDecimal multiplicand, Currency currency) {
		return new Money();
	}

	/**
	 * Multiply the amount with a rate
	 * @param multiplicand Multiplicand
	 * @return Result
	 */
	public Money multiply(DecimalNumber multiplicand) {
		return multiply(multiplicand.getValue());
	}

	/**
	 * Multiply the amount with a rate and convert to another currency.
	 * @param multiplicand Multiplicand
	 * @param currency Currency to convert to.
	 * @return Result
	 */
	public Money multiply(DecimalNumber multiplicand, Currency currency) {
		return multiply(multiplicand.getValue(), currency);
	}

	/**
	 * Convert this monetary value to another currency (by multiplying with the average rate).
	 *
	 * @param currency Currency to convert to.
	 * @param rateProvider Currency rate provider.
	 *
	 * @return Converted monetary value.
	 */
	public Money convert(Currency currency, CurrencyRateProvider rateProvider) {
		return new Money();
	}

	/**
	 * Convert this monetary value to another currency (by multiplying with the average rate).
	 *
	 * @param currency Currency to convert to.
	 *
	 * @return Converted monetary value.
	 */
	public Money convert(Currency currency) {
		return convert(DateUtility.today(), currency);
	}

	/**
	 * Convert this monetary value to another currency (by multiplying with the average rate).
	 *
	 * @param date Effective date.
	 * @param currency Currency to convert to.
	 *
	 * @return Converted monetary value.
	 */
	public Money convert(Date date, Currency currency) {
		return new Money();
	}

	/**
	 * Convert this monetary value to the equivalent in local currency by multiplying with the given "exchange rate".
	 * @param exchangeRate Exchange rate
	 * @param tm Transaction manager (Local currency will be determined from this)
	 * @return Result
	 */
	public Money toLocal(Rate exchangeRate, TransactionManager tm) {
		return multiply(exchangeRate, tm.getCurrency());
	}

	/**
	 * Convert this monetary value to the equivalent in local currency by multiplying with the given "exchange rate".
	 * @param exchangeRate Exchange rate
	 * @param systemEntity System entity (Local currency will be determined from this)
	 * @return Result
	 */
	public Money toLocal(Rate exchangeRate, SystemEntity systemEntity) {
		return multiply(exchangeRate, Currency.getInstance(systemEntity.getCurrency()));
	}

	/**
	 * Convert this monetary value to the equivalent in local currency by multiplying with the given "exchange rate".
	 * @param tm Transaction manager (Local currency will be determined from this)
	 * @return Result
	 */
	public Money toLocal(TransactionManager tm) {
		return toLocal(tm.getEntity());
	}

	/**
	 * Convert this monetary value to the equivalent in local currency by multiplying with the given "exchange rate".
	 * @param date Effective date.
	 * @param tm Transaction manager (Local currency will be determined from this)
	 * @return Result
	 */
	public Money toLocal(Date date, TransactionManager tm) {
		return toLocal(date, tm.getEntity());
	}

	/**
	 * Convert this monetary value to the equivalent in local currency by multiplying with the given "exchange rate".
	 * @param systemEntity System entity (Local currency will be determined from this)
	 * @return Result
	 */
	public Money toLocal(SystemEntity systemEntity) {
		return toLocal((Date) null, systemEntity);
	}

	/**
	 * Convert this monetary value to the equivalent in local currency by multiplying with the given "exchange rate".
	 * @param date Effective date.
	 * @param systemEntity System entity (Local currency will be determined from this)
	 * @return Result
	 */
	public Money toLocal(Date date, SystemEntity systemEntity) {
		return new Money();
	}

	/**
	 * Convert this monetary value to another currency by applying the buying rate.
	 *
	 * @param currency Currency to convert to.
	 * @param rateProvider Currency rate provider.
	 *
	 * @return Converted monetary value.
	 */
	public Money buy(Currency currency, CurrencyRateProvider rateProvider) {
		return new Money();
	}

	/**
	 * Convert this monetary value to another currency by applying the buying rate.
	 *
	 * @param currency Currency to convert to.
	 *
	 * @return Converted monetary value.
	 */
	public Money buy(Currency currency) {
		return buy(DateUtility.today(), currency);
	}

	/**
	 * Convert this monetary value to another currency by applying the buying rate.
	 *
	 * @param date Effective date.
	 * @param currency Currency to convert to.
	 *
	 * @return Converted monetary value.
	 */
	public Money buy(Date date, Currency currency) {
		return new Money();
	}

	/**
	 * Convert this monetary value to another currency by applying the selling rate.
	 *
	 * @param currency Currency to convert to.
	 * @param rateProvider Currency rate provider.
	 *
	 * @return Converted monetary value.
	 */
	public Money sell(Currency currency, CurrencyRateProvider rateProvider) {
		return new Money();
	}

	/**
	 * Convert this monetary value to another currency by applying the selling rate.
	 *
	 * @param currency Currency to convert to.
	 *
	 * @return Converted monetary value.
	 */
	public Money sell(Currency currency) {
		return sell(DateUtility.today(), currency);
	}

	/**
	 * Convert this monetary value to another currency by applying the selling rate.
	 *
	 * @param date Effective date.
	 * @param currency Currency to convert to.
	 *
	 * @return Converted monetary value.
	 */
	public Money sell(Date date, Currency currency) {
		return new Money();
	}


	/**
	 * Gets the selling rate.
	 * <p>Note: To convert to the target currency, you need to divide the monetary value with this rate.</p>
	 * @param currency Currency for which rate is required.
	 * @return Rate
	 */
	public Rate getBuyingRate(Currency currency) {
		return getBuyingRate(DateUtility.today(), currency);
	}

	/**
	 * Gets the selling rate.
	 * <p>Note: To convert to the target currency, you need to divide the monetary value with this rate.</p>
	 * @param date Effective date.
	 * @param currency Currency for which rate is required.
	 * @return Rate
	 */
	public Rate getBuyingRate(Date date, Currency currency) {
		return findRate(date, currency, null).getBuyingRate();
	}

	/**
	 * Gets the selling rate.
	 * <p>Note: To convert to the target currency, you need to divide the monetary value with this rate.</p>
	 * @param currency Currency for which rate is required.
	 * @return Rate
	 */
	public Rate getSellingRate(Currency currency) {
		return getSellingRate(DateUtility.today(), currency);
	}

	/**
	 * Gets the selling rate.
	 * <p>Note: To convert to the target currency, you need to divide the monetary value with this rate.</p>
	 * @param date Effective date.
	 * @param currency Currency for which rate is required.
	 * @return Rate
	 */
	public Rate getSellingRate(Date date, Currency currency) {
		return findRate(date, currency, null).getSellingRate();
	}

	/**
	 * Gets the exchange rate (average of selling rate and buying rate).
	 * <p>Note: To convert to the target currency, you need to divide the monetary value with this rate.</p>
	 * @param currency Currency for which rate is required.
	 * @return Rate
	 */
	public Rate getExchangeRate(Currency currency) {
		return getExchangeRate(DateUtility.today(), currency);
	}

	/**
	 * Gets the exchange rate (average of selling rate and buying rate).
	 * <p>Note: To convert to the target currency, you need to divide the monetary value with this rate.</p>
	 * @param date Effective date.
	 * @param currency Currency for which rate is required.
	 * @return Rate
	 */
	public Rate getExchangeRate(Date date, Currency currency) {
		return findRate(date, currency, null).getRate();
	}

	/**
	 * Gets the buying rate.
	 * <p>Note: To convert to the target currency, you need to divide the monetary value with this rate.</p>
	 * @param currency Currency for which rate is required.
	 * @param systemEntity System Entity.
	 * @return Rate
	 */
	public Rate getBuyingRate(Currency currency, SystemEntity systemEntity) {
		return getBuyingRate(DateUtility.today(), currency, systemEntity);
	}

	/**
	 * Gets the buying rate.
	 * <p>Note: To convert to the target currency, you need to divide the monetary value with this rate.</p>
	 * @param date Effective date.
	 * @param currency Currency for which rate is required.
	 * @param systemEntity System Entity.
	 * @return Rate
	 */
	public Rate getBuyingRate(Date date, Currency currency, SystemEntity systemEntity) {
		return findRate(date, currency, systemEntity).getBuyingRate();
	}

	/**
	 * Gets the selling rate.
	 * <p>Note: To convert to the target currency, you need to divide the monetary value with this rate.</p>
	 * @param currency Currency for which rate is required.
	 * @param systemEntity System Entity.
	 * @return Rate
	 */
	public Rate getSellingRate(Currency currency, SystemEntity systemEntity) {
		return getSellingRate(DateUtility.today(), currency, systemEntity);
	}

	/**
	 * Gets the selling rate.
	 * <p>Note: To convert to the target currency, you need to divide the monetary value with this rate.</p>
	 * @param date Effective date.
	 * @param currency Currency for which rate is required.
	 * @param systemEntity System Entity.
	 * @return Rate
	 */
	public Rate getSellingRate(Date date, Currency currency, SystemEntity systemEntity) {
		return findRate(date, currency, systemEntity).getSellingRate();
	}

	/**
	 * Gets the exchange rate (average of selling rate and buying rate).
	 * <p>Note: To convert to the target currency, you need to divide the monetary value with this rate.</p>
	 * @param currency Currency for which rate is required.
	 * @param systemEntity System Entity.
	 * @return Rate
	 */
	public Rate getExchangeRate(Currency currency, SystemEntity systemEntity) {
		return getExchangeRate(DateUtility.today(), currency, systemEntity);
	}

	/**
	 * Gets the exchange rate (average of selling rate and buying rate).
	 * <p>Note: To convert to the target currency, you need to divide the monetary value with this rate.</p>
	 * @param date Effective date.
	 * @param currency Currency for which rate is required.
	 * @param systemEntity System Entity.
	 * @return Rate
	 */
	public Rate getExchangeRate(Date date, Currency currency, SystemEntity systemEntity) {
		return findRate(date, currency, systemEntity).getRate();
	}

	/**
	 * Gets the selling rate.
	 * <p>Note: To convert to the target currency, you need to divide the monetary value with this rate.</p>
	 * @param from Currency for which rate is required.
	 * @param to Target currency to convert to.
	 * @return Rate
	 */
	public static Rate getBuyingRate(Currency from, Currency to) {
		return getBuyingRate(DateUtility.today(), from, to);
	}

	/**
	 * Gets the selling rate.
	 * <p>Note: To convert to the target currency, you need to divide the monetary value with this rate.</p>
	 * @param date Effective date.
	 * @param from Currency for which rate is required.
	 * @param to Target currency to convert to.
	 * @return Rate
	 */
	public static Rate getBuyingRate(Date date, Currency from, Currency to) {
		return findRate(date, from, to, null).getBuyingRate();
	}

	/**
	 * Gets the selling rate.
	 * <p>Note: To convert to the target currency, you need to divide the monetary value with this rate.</p>
	 * @param from Currency for which rate is required.
	 * @param to Target currency to convert to.
	 * @return Rate
	 */
	public static Rate getSellingRate(Currency from, Currency to) {
		return getSellingRate(DateUtility.today(), from, to);
	}

	/**
	 * Gets the selling rate.
	 * <p>Note: To convert to the target currency, you need to divide the monetary value with this rate.</p>
	 * @param date Effective date.
	 * @param from Currency for which rate is required.
	 * @param to Target currency to convert to.
	 * @return Rate
	 */
	public static Rate getSellingRate(Date date, Currency from, Currency to) {
		return findRate(date, from, to, null).getSellingRate();
	}

	/**
	 * Gets the exchange rate (average of selling rate and buying rate).
	 * <p>Note: To convert to the target currency, you need to divide the monetary value with this rate.</p>
	 * @param from Currency for which rate is required.
	 * @param to Target currency to convert to.
	 * @return Rate
	 */
	public static Rate getExchangeRate(Currency from, Currency to) {
		return getExchangeRate(DateUtility.today(), from, to, null);
	}

	/**
	 * Gets the exchange rate (average of selling rate and buying rate).
	 * <p>Note: To convert to the target currency, you need to divide the monetary value with this rate.</p>
	 * @param date Effective date.
	 * @param from Currency for which rate is required.
	 * @param to Target currency to convert to.
	 * @return Rate
	 */
	public static Rate getExchangeRate(Date date, Currency from, Currency to) {
		return findRate(date, from, to, null).getRate();
	}

	/**
	 * Gets the buying rate.
	 * <p>Note: To convert to the target currency, you need to divide the monetary value with this rate.</p>
	 * @param from Currency for which rate is required.
	 * @param to Target currency to convert to.
	 * @param systemEntity System Entity.
	 * @return Rate
	 */
	public static Rate getBuyingRate(Currency from, Currency to, SystemEntity systemEntity) {
		return getBuyingRate(DateUtility.today(), from, to, systemEntity);
	}

	/**
	 * Gets the buying rate.
	 * <p>Note: To convert to the target currency, you need to divide the monetary value with this rate.</p>
	 * @param date Effective date.
	 * @param from Currency for which rate is required.
	 * @param to Target currency to convert to.
	 * @param systemEntity System Entity.
	 * @return Rate
	 */
	public static Rate getBuyingRate(Date date, Currency from, Currency to, SystemEntity systemEntity) {
		return findRate(date, from, to, systemEntity).getBuyingRate();
	}

	/**
	 * Gets the selling rate.
	 * <p>Note: To convert to the target currency, you need to divide the monetary value with this rate.</p>
	 * @param from Currency for which rate is required.
	 * @param to Target currency to convert to.
	 * @param systemEntity System Entity.
	 * @return Rate
	 */
	public static Rate getSellingRate(Currency from, Currency to, SystemEntity systemEntity) {
		return getSellingRate(DateUtility.today(), from, to, systemEntity);
	}

	/**
	 * Gets the selling rate.
	 * <p>Note: To convert to the target currency, you need to divide the monetary value with this rate.</p>
	 * @param date Effective date.
	 * @param from Currency for which rate is required.
	 * @param to Target currency to convert to.
	 * @param systemEntity System Entity.
	 * @return Rate
	 */
	public static Rate getSellingRate(Date date, Currency from, Currency to, SystemEntity systemEntity) {
		return findRate(date, from, to, systemEntity).getSellingRate();
	}

	/**
	 * Gets the exchange rate (average of selling rate and buying rate).
	 * <p>Note: To convert to the target currency, you need to divide the monetary value with this rate.</p>
	 * @param from Currency for which rate is required.
	 * @param to Target currency to convert to.
	 * @param systemEntity System Entity.
	 * @return Rate
	 */
	public static Rate getExchangeRate(Currency from, Currency to, SystemEntity systemEntity) {
		return getExchangeRate(DateUtility.today(), from, to, systemEntity);
	}

	/**
	 * Gets the exchange rate (average of selling rate and buying rate).
	 * <p>Note: To convert to the target currency, you need to divide the monetary value with this rate.</p>
	 * @param date Effective date.
	 * @param from Currency for which rate is required.
	 * @param to Target currency to convert to.
	 * @param systemEntity System Entity.
	 * @return Rate
	 */
	public static Rate getExchangeRate(Date date, Currency from, Currency to, SystemEntity systemEntity) {
		return findRate(date, from, to, systemEntity).getRate();
	}

	private CurrencyRateProvider findRate(Date date, Currency currency, SystemEntity systemEntity) {
		return new ExchangeRate();
	}

	private static CurrencyRateProvider findRate(Date date, Currency from, Currency to, SystemEntity systemEntity) {
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
