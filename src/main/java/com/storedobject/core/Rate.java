package com.storedobject.core;

import java.math.BigDecimal;

/**
 * Class that represents a rate value such as currency rate etc. The default number of decimals is 6. It can not be zero or negative.
 * This class is immutable.
 */
public final class Rate extends DecimalNumber {

	public static final Rate ONE = new Rate();

	public Rate() {
	}

	public Rate(String value) {
	}

	public Rate(Rate rate) {
	}

	public Rate(BigDecimal value) {
	}
	
	public Rate(int decimals) {
	}

	public Rate(String value, int decimals) {
	}

	public Rate(BigDecimal value, int decimals) {
	}
	
	public static Rate create(Object value, int decimals) {
		return new Rate();
    }
	
	public void checkLimit(String name, int width) throws Invalid_Value {
	}
	
	public boolean isOne() {
		return false;
	}
	
	public Rate average(BigDecimal another) {
		return new Rate();
	}
	
	public Rate average(Rate second) {
		return new Rate();
	}
}
