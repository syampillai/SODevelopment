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
        return null;
    }

    public void setForeignCurrency(String foreignCurrency) {
    }

    public String getForeignCurrency() {
        return null;
    }

    public void setBuyingRate(Rate buyingRate) {
    }

    public void setBuyingRate(Object value) {
    }

    public Rate getBuyingRate() {
        return null;
    }

    public void setSellingRate(Rate sellingRate) {
    }

    public void setSellingRate(Object value) {
    }

    public Rate getSellingRate() {
        return null;
    }
	
	public Rate getRate() {
        return null;
	}

	public ExchangeRate reverse() {
        return null;
	}
	
	public static ExchangeRate get(Currency local, Currency foreign) {
		return null;
	}
}