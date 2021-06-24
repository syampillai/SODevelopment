package com.storedobject.core;

public interface CurrencyRateProvider {
	
	public Rate getBuyingRate();
	
	public Rate getSellingRate();
	
	public default Rate getRate() {
		return getBuyingRate().average(getSellingRate());
	}
}