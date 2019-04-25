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
		return null;
	}

	public static Currency getCurrency(String code) {
		return null;
	}

	public int getDecimals() {
		return 0;
	}

	public BigDecimal getValue() {
		return null;
	}

	public Currency getCurrency() {
		return null;
	}
	
	public Money zero() {
		return null;
	}
	
	public Rate getBuyingRate(Currency currency) {
		return null;
	}

	public Rate getSellingRate(Currency currency) {
		return null;
	}
	
	public Rate getExchangeRate(Currency currency) {
		return null;
	}
	
	public Rate getBuyingRate(Currency currency, SystemEntity systemEntity) {
		return null;
	}
	
	public Rate getSellingRate(Currency currency, SystemEntity systemEntity) {
		return null;
	}
	
	public Rate getExchangeRate(Currency currency, SystemEntity systemEntity) {
		return null;
	}
	
	public Money convert(Currency currency) {
		return null;
	}
	
	public Money buy(Currency currency) {
		return null;
	}
	
	public Money sell(Currency currency) {
		return null;
	}

	public Money add(String amount) {
		return null;
	}

	public Money add(BigDecimal amount) {
		return null;
	}

	public Money add(Money amount) {
		return null;
	}
	
	public Money subtract(String amount) {
		return null;
	}

	public Money subtract(BigDecimal amount) {
		return null;
	}

	public Money subtract(Money amount) {
		return null;
	}

	public Money multiply(Quantity quantity) {
		return null;
	}

	public Money multiply(double multiplicand) {
		return null;
	}

	public Money multiply(BigDecimal multiplicand) {
		return null;
	}

	public Money multiply(DecimalNumber multiplicand) {
		return null;
	}

	public Money multiply(Rate multiplicand) {
		return null;
	}
	
	public Money percentage(double percentage) {
		return null;
	}

	public Money percentage(BigDecimal percentage) {
		return null;
	}

	public Money percentage(DecimalNumber percentage) {
		return null;
	}

	public Money percentage(Rate percentage) {
		return null;
	}

	public Money divide(double divisor) {
		return null;
	}

	public Money divide(BigDecimal divisor) {
		return null;
	}

	public Money divide(Rate divisor) {
		return null;
	}

	public Money divide(Quantity quantity) {
		return null;
	}

	public Money negate() {
		return null;
	}
	
    public Money absolute() {
		return null;
    }
	
	public Money round() {
		return null;
	}
	
	public Money roundUp() {
		return null;
	}

	public Money roundDown() {
		return null;
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
		return null;
	}

	public static String format(double value, boolean separated) {
		return null;
	}

	public static String format(double value, int decimals) {
		return null;
	}

	public static String format(double value, int decimals, boolean separated) {
		return null;
	}

	public static String format(String s, int decimals, boolean separated) {
		return null;
	}

	public String toString(boolean showSymbol) {
		return null;
	}

	@Override
	public String getStorableValue() {
		return null;
	}

	@Override
	public int compareTo(Money money) {
		return 0;
	}
	
	public String words() {
		return null;
	}
	
	@SuppressWarnings("serial")
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
			return null;
		}
		
		public String toString(boolean withZeros) {
			return null;
		}
	}
}
