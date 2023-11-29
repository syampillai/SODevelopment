package com.storedobject.core;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.Currency;
import java.util.Locale;

/**
 * This class represents an entity whose accounting system exists in this server.
 */
public final class SystemEntity extends StoredObject implements RequiresApproval, HasName {

	public SystemEntity(Id entityId, String currency, Date workingDate) {
	}

	public SystemEntity() {
	}

	public static void columns(Columns columns) {
	}

	public String getName() {
		return "";
	}

	public String getLocation() {
		return "";
	}

	public String getCurrency() {
		return "";
	}

	public void setCurrency(String currency) {
	}

	public Id getEntityId() {
		return new Id();
	}

	public void setEntity(BigDecimal idValue) {
	}

	public void setEntity(Id entityId) {
	}

	public void setEntity(Entity entity) {
		setEntity(entity.getId());
	}

	public Entity getEntity() {
		return new Entity();
	}

	public int getStatus() {
		return 0;
	}

	public void setStatus(int status) {
	}

	public void setWorkingDate(Date workingDate) {
	}

	public Date getWorkingDate() {
		return DateUtility.today();
	}

	public String getTimeZone() {
		return "";
	}

	public void setTimeZone(String timeZone) {
	}

	public void setStartOfFinancialYear(Date startDate) {
	}

	public Date getStartOfFinancialYear() {
		return DateUtility.today();
	}

	public Date getStartOfFinancialYear(Date today) {
		return DateUtility.today();
	}

	public Date getEndOfFinancialYear() {
		return DateUtility.today();
	}

	public Date getEndOfFinancialYear(Date today) {
		return DateUtility.today();
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
		return "";
	}

	public void setAlternateLocaleCountry(String alternateLocaleCountry) {
	}
	
	public Locale getAlternateLocale() {
		return Locale.ENGLISH;
	}
	
	public void setAlternateLocale(Locale locale) {
	}
	
	public static SystemEntity get() {
		return new SystemEntity();
	}
	
	public static Currency getDefaultCurrency() {
		return Currency.getInstance("");
	}

	public <D extends java.util.Date> D dateGMT(D date) {
		return date;
	}

	public <D extends java.util.Date> D date(D dateGMT) {
		return dateGMT;
	}

	/**
	 * Convert a period value from local to GMT.
	 *
	 * @param period Local value.
	 * @param <D> Date/date-time type.
	 * @param <P> Period type.
	 * @return GMT value.
	 */
	public <D extends java.util.Date, P extends AbstractPeriod<D>> P periodGMT(P period) {
		return period;
	}

	/**
	 * Convert a period value from GMT to local.
	 *
	 * @param periodGMT Local value.
	 * @param <D> Date/date-time type.
	 * @param <P> Period type.
	 * @return Local value.
	 */
	public <D extends java.util.Date, P extends AbstractPeriod<D>> P period(P periodGMT) {
		return periodGMT;
	}

	/**
	 * Get the time difference in minutes.
	 *
	 * @return Time difference.
	 */
	public int getTimeDifference() {
		return 0;
	}

	public static SystemEntity getCached(Id id) {
		return Math.random() > 0.5 ? new SystemEntity() : null;
	}
}
