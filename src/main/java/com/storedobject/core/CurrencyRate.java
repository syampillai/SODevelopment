package com.storedobject.core;

import com.storedobject.core.annotation.Column;
import com.storedobject.core.annotation.SetNotAllowed;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.util.Currency;

public class CurrencyRate extends StoredObject implements CurrencyRateProvider {

	private String currency;
	private Id systemEntityId;
	private Date effectiveDate = DateUtility.today();
	private Rate buyingRate = new Rate(6), sellingRate = new Rate(6);

	/**
	 * Constructs a Currency Rate
	 *
	 * @param systemEntityId Id of the System Entity where this rate is applicable.
	 * @param currency The currency.
	 * @param effectiveDate Effective date of this rate.
	 * @param buyingRate Buying rate.
	 * @param sellingRate Selling rate.
	 */
	public CurrencyRate(Id systemEntityId, String currency, Date effectiveDate, Rate buyingRate, Rate sellingRate) {
		this.systemEntityId = systemEntityId;
		this.currency = currency;
		this.effectiveDate = new Date(effectiveDate.getTime());
		this.buyingRate = buyingRate;
		this.sellingRate = sellingRate;
	}

	/**
	 * Constructor for internal use only
	 */
	public CurrencyRate() {
	}

	public static void columns(Columns columns) {
		columns.add("SystemEntity", "id");
		columns.add("Currency", "currency");
		columns.add("EffectiveDate", "date");
		columns.add("BuyingRate", "rate");
		columns.add("SellingRate", "rate");
	}

	public static void indices(Indices indices) {
		indices.add("SystemEntity,Currency", true);
	}

	@Override
	public String getUniqueCondition() {
		return "SystemEntity=" + systemEntityId + " AND lower(Currency)='" + currency.trim().toLowerCase() + "'";
	}

	public static String[] displayColumns() {
		return new String[] {
				"SystemEntity.Entity.Name as Entity", "SystemEntity.Entity.Location as Location",
				"Currency", "EffectiveDate", "BuyingRate", "SellingRate"
		};
	}

	public static String[] searchColumns() {
		return new String[] { "SystemEntity.Entity.Name as Entity", "SystemEntity.Entity.Location as Location", "Currency" };
	}

	public static String[] protectedColumns() {
		return new String[] { "SystemEntity" };
	}

	/**
	 * Gets the currency.
	 *
	 * @return The currency
	 */
	@SetNotAllowed
	public String getCurrency() {
		return currency;
	}

	// For internal use only.
	public void setCurrency(String currency) {
		if(!loading()) {
			throw new Set_Not_Allowed("Currency");
		}
		this.currency = currency;
	}

	/**
	 * Gets the Id of the System Entity.
	 *
	 * @return The Id of the System Entity
	 */
	@SetNotAllowed
	public Id getSystemEntityId() {
		return systemEntityId;
	}

	// For internal use only.
	public void setSystemEntity(BigDecimal idValue) {
		if(!loading()) {
			throw new Set_Not_Allowed("System Entity");
		}
		this.systemEntityId = new Id(idValue);
	}

	/**
	 * Gets the System Entity.
	 *
	 * @return The System Entity
	 */
	public SystemEntity getSystemEntity() {
		return get(getTransaction(), SystemEntity.class, systemEntityId);
	}

	public void setBuyingRate(Rate buyingRate) {
		this.buyingRate = new Rate(buyingRate.getValue(), 6);
	}

	public void setBuyingRate(Object value) {
		setBuyingRate(Rate.create(value, 6));
	}

	@Column(style = "(d:14,6)")
	public Rate getBuyingRate() {
		return buyingRate;
	}

	public void setSellingRate(Rate sellingRate) {
		this.sellingRate = new Rate(sellingRate.getValue(), 6);
	}

	public void setSellingRate(Object value) {
		setSellingRate(Rate.create(value, 6));
	}

	@Column(style = "(d:14,6)")
	public Rate getSellingRate() {
		return sellingRate;
	}

	/**
	 * Set the effective date
	 *
	 * @param effectiveDate Effective date to be set
	 */
	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = new Date(effectiveDate.getTime());
	}

	/**
	 * Get the effective date
	 *
	 * @return Effective date
	 */
	public Date getEffectiveDate() {
		return new Date(effectiveDate.getTime());
	}

	@Override
	public void validateData(TransactionManager tm) throws Exception {
		if(inserted() && systemEntityId == null) {
			Transaction t = getTransaction();
			if(t != null) {
				SystemEntity se = t.getManager().getEntity();
				if(se != null) {
					systemEntityId = se.getId();
				}
			}
		} else {
			systemEntityId = tm.checkType(this, systemEntityId, SystemEntity.class);
		}
		currency = checkCurrency(currency);
		if(systemEntityId != null && currency.equals(getSystemEntity().getCurrency())) {
			buyingRate = sellingRate = new Rate(BigDecimal.ONE);
		}
		if(buyingRate.isZero() || sellingRate.isZero()) {
			throw new Invalid_State("Currency rate can not be zero");
		}
		super.validateData(tm);
	}

	public Rate getRate() {
		return sellingRate.average(buyingRate);
	}

	public CurrencyRate reverse() {
		CurrencyRate cr = new CurrencyRate();
		cr.currency = currency;
		cr.systemEntityId = systemEntityId;
		cr.buyingRate = new Rate(BigDecimal.ONE.divide(sellingRate.getValue(), 6, RoundingMode.HALF_UP));
		cr.sellingRate = new Rate(BigDecimal.ONE.divide(buyingRate.getValue(), 6, RoundingMode.HALF_UP));
		cr.effectiveDate = effectiveDate;
		return cr;
	}

	public static CurrencyRate get(Currency currency, SystemEntity entity) {
		return list(CurrencyRate.class, "SystemEntity=" + entity.getId() + " AND lower(Currency)='"
				+ currency.getCurrencyCode().toLowerCase() + "'", "EffectiveDate DESC").findFirst();
	}
}