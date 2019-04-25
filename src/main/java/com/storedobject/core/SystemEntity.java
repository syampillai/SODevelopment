package com.storedobject.core;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.Currency;
import java.util.Locale;

/**
 * This class represents an entity whose accounting system exists in this server.
 */
public class SystemEntity extends StoredObject {

	public SystemEntity(Id entityId, String currency, Date workingDate) {
	}

	public SystemEntity() {
	}

	public static void columns(Columns columns) {
	}

	public String getName() {
		return null;
	}

	public String getLocation() {
		return null;
	}

	public String getCurrency() {
		return null;
	}

	public void setCurrency(String currency) {
	}

	public Id getEntityId() {
		return null;
	}

	public void setEntity(BigDecimal idValue) {
	}

	public Entity getEntity() {
		return null;
	}

	public int getStatus() {
		return 0;
	}

	public void setStatus(int status) {
	}

	public void setWorkingDate(Date workingDate) {
	}

	public Date getWorkingDate() {
		return null;
	}

	public void setStartOfFinancialYear(Date startDate) {
	}

	public Date getStartOfFinancialYear() {
		return null;
	}

	public Date getEndOfFinancialYear() {
		return null;
	}
	
    public void setLogoName(String logoName) {
    }

    public String getLogoName() {
		return null;
    }

	public static SystemEntity get(Entity entity) {
		return null;
	}

	public String getAlternateLocaleLanguage() {
		return null;
	}

	public void setAlternateLocaleLanguage(String alternateLocaleLanguage) {
	}

	public String getAlternateLocaleCountry() {
		return null;
	}

	public void setAlternateLocaleCountry(String alternateLocaleCountry) {
	}
	
	public Locale getAlternateLocale() {
		return null;
	}
	
	public void setAlternateLocale(Locale locale) {
	}
	
	protected static Locale getLocale(String localeCountry, String localeLanguage) {
		return null;
	}
	
	public static SystemEntity get() {
		return null;
	}
	
	public static Currency getDefaultCurrency() {
		return null;
	}
}
