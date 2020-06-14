package com.storedobject.core;

import com.storedobject.common.Storable;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Hashtable;

public final class Money implements Storable, Comparable<Money> {

	public static Currency defaultCurrency = Currency.getInstance("USD");

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
		return new Money();
	}

	public static Currency getCurrency(String code) {
		return defaultCurrency;
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
		return this;
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
		return this;
	}
	
	public Money buy(Currency currency) {
		return this;
	}
	
	public Money sell(Currency currency) {
		return this;
	}

	public Money add(String amount) {
		return this;
	}

	public Money add(BigDecimal amount) {
		return this;
	}

	public Money add(Money amount) {
		return this;
	}
	
	public Money subtract(String amount) {
		return this;
	}

	public Money subtract(BigDecimal amount) {
		return this;
	}

	public Money subtract(Money amount) {
		return this;
	}

	public Money multiply(Quantity quantity) {
		return this;
	}

	public Money multiply(double multiplicand) {
		return this;
	}

	public Money multiply(BigDecimal multiplicand) {
		return this;
	}

	public Money multiply(DecimalNumber multiplicand) {
		return this;
	}

	public Money multiply(Rate multiplicand) {
		return this;
	}
	
	public Money percentage(double percentage) {
		return this;
	}

	public Money percentage(BigDecimal percentage) {
		return this;
	}

	public Money percentage(DecimalNumber percentage) {
		return this;
	}

	public Money percentage(Rate percentage) {
		return this;
	}

	public Money divide(double divisor) {
		return this;
	}

	public Money divide(BigDecimal divisor) {
		return this;
	}

	public Money divide(Rate divisor) {
		return this;
	}

	public Money divide(Quantity quantity) {
		return this;
	}

	public Money negate() {
		return this;
	}
	
    public Money absolute() {
		return this;
    }
	
	public Money round() {
		return this;
	}
	
	public Money roundUp() {
		return this;
	}

	public Money roundDown() {
		return this;
	}

    public boolean isGreaterThan(Money another) {
		return false;
    }

    public boolean isGreaterThan(long value) {
		return false;
    }

    public boolean isLessThan(Money another) {
		return false;
    }

    public boolean isLessThan(long value) {
		return false;
    }

    public boolean isGreaterThanOrEqual(Money another) {
		return false;
    }

    public boolean isGreaterThanOrEqual(long value) {
		return false;
    }

    public boolean isLessThanOrEqual(Money another) {
		return false;
    }

    public boolean isLessThanOrEqual(long value) {
		return false;
    }

    public boolean isZero() {
		return false;
    }

    public boolean isDebit() {
		return false;
    }
    
    public boolean isNegative() {
		return false;
    }

    public boolean isCredit() {
		return false;
    }
    
    public boolean isPositive() {
    	return isCredit();
    }

	public static String format(double value) {
		return "";
	}

	public static String format(double value, boolean separated) {
		return "";
	}

	public static String format(double value, int decimals) {
		return "";
	}

	public static String format(double value, int decimals, boolean separated) {
		return "";
	}

	public static String format(String s, int decimals, boolean separated) {
		return "";
	}

	public String toString(boolean showSymbol) {
		return "";
	}

	public static String getSymbol(Currency currency) {
		return "";
	}

	@Override
	public String getStorableValue() {
		return "";
	}

	@Override
	public int compareTo(@SuppressWarnings("NullableProblems") Money money) {
		return 0;
	}
	
	public String words() {
		return "";
	}
	
	public static class List extends Hashtable<Currency, Money> {
		
		public List add(Money money) {
			return this;
		}
		
		public List add(List moneyList) {
			return this;
		}

		public List subtract(Money money) {
			return this;
		}
		
		public List subtract(List moneyList) {
			return this;
		}
		
		public boolean isZero() {
			return true;
		}
		
		public Money.List negate() {
			return this;
		}
		
		public String toString(boolean withZeros) {
			return "";
		}
	}
}
