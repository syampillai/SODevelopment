package com.storedobject.core;

import java.sql.Date;
import java.util.Currency;

public class ExchangeRate extends StoredObject implements CurrencyRateProvider {

    public ExchangeRate() {
    }

    public static void columns(Columns columns) {
    }

    public void setEffectiveFrom(Date effectiveFrom) {
    }

    public Date getEffectiveFrom() {
        return null;
    }

    public void setLocalCurrency(String localCurrency) {
    }

    public String getLocalCurrency() {
        return "";
    }

    public void setForeignCurrency(String foreignCurrency) {
    }

    public String getForeignCurrency() {
        return "";
    }

    public void setBuyingRate(Rate buyingRate) {
    }

    public void setBuyingRate(Object value) {
    }

    public Rate getBuyingRate() {
        return new Rate();
    }

    public void setSellingRate(Rate sellingRate) {
    }

    public void setSellingRate(Object value) {
    }

    public Rate getSellingRate() {
        return new Rate();
    }
	
	public Rate getRate() {
        return new Rate();
	}

	public ExchangeRate reverse() {
        return new ExchangeRate();
	}
	
	public static ExchangeRate get(Currency local, Currency foreign) {
		return Math.random() > 0.5 ? null : new ExchangeRate();
	}

    public static ExchangeRate get(Date date, Currency local, Currency foreign) {
        return Math.random() > 0.5 ? null : new ExchangeRate();
    }
}