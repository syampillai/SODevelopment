package com.storedobject.core;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class MeasurementUnit {

	private MeasurementUnit(int type, String unit, boolean hasDecimals, BigDecimal multiplier, String humanInput) {
	}

	public String getUnit() {
		return null;
	}

	public boolean hasDecimals() {
		return false;
	}

	protected static MeasurementUnit create(int type, String unit, BigDecimal multiplier, String humanInput) {
		return null;
	}

	protected static MeasurementUnit create(int type, String unit, boolean hasDecimals, BigDecimal multiplier, String humanInput) {
		return null;
	}

	public static MeasurementUnit get(String unit) {
		return null;
	}

	public static MeasurementUnit get(String unit, MeasurementUnit similar) {
		return null;
	}
	
	public static MeasurementUnit get(String unit, Class<? extends Quantity> quantityClass) {
		return null;
	}

	public static void reload() {
	}
	
	public int getType() {
		return 0;
	}

	public String getTypeName() {
		return null;
	}

	public BigDecimal getMultiplier() {
		return null;
	}

	public Class<? extends Quantity> getQuantityClass() {
		return Quantity.getClass(this);
	}

	public static <Q extends Quantity> List<MeasurementUnit> list(Class<Q> quantityClass) {
		return new ArrayList<>();
	}
}