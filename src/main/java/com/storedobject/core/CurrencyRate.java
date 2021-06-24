package com.storedobject.core;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.Currency;

public class CurrencyRate extends StoredObject implements CurrencyRateProvider {

	public CurrencyRate(Id systemEntityId, String currency, Date effectiveDate, Rate buyingRate, Rate sellingRate) {
	}

	public CurrencyRate() {
	}

	public static void columns(Columns columns) {
	}

	public String getCurrency() {
		return null;
	}

	public void setCurrency(String currency) {
	}

	public Id getSystemEntityId() {
		return null;
	}

	public void setSystemEntity(BigDecimal idValue) {
	}

	public SystemEntity getSystemEntity() {
		return null;
	}

	public void setBuyingRate(BigDecimal buyingRate) {
	}

	public Rate getBuyingRate() {
		return null;
	}

	public void setSellingRate(BigDecimal sellingRate) {
	}

	public Rate getSellingRate() {
		return null;
	}

	public void setEffectiveDate(Date effectiveDate) {
	}

	public Date getEffectiveDate() {
		return null;
	}

	public Rate getRate() {
		return null;
	}
	
	public CurrencyRate reverse() {
		return null;
	}
	
	public static CurrencyRate get(Currency currency, SystemEntity entity) {
		return null;
	}
}