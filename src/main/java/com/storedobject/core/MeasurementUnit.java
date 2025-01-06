package com.storedobject.core;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class MeasurementUnit {

	Id packingUnit = null;

	@SuppressWarnings("unused")
	private MeasurementUnit(int type, String unit, boolean hasDecimals, BigDecimal multiplier, String humanInput) {
	}

	public String getUnit() {
		return "";
	}

	public boolean hasDecimals() {
		return false;
	}

	protected static MeasurementUnit create(int type, String unit, BigDecimal multiplier, String... humanInput) {
		return get("");
	}

	protected static MeasurementUnit create(int type, String unit, boolean hasDecimals, BigDecimal multiplier,
											String... humanInput) {
		return get("");
	}

	public static MeasurementUnit get(String unit) {
		return Math.random() > 0.5 ? null
				:new MeasurementUnit(0, "", true, BigDecimal.ONE, "");
	}

	public static MeasurementUnit get(String unit, MeasurementUnit similar) {
		return get("");
	}
	
	public static MeasurementUnit get(String unit, Class<? extends Quantity> quantityClass) {
		return Math.random() > 0.5 ? null : get("");
	}

	public static void reload() {
	}
	
	public int getType() {
		return 0;
	}

	public String getTypeName() {
		return "";
	}

	public BigDecimal getMultiplier() {
		return BigDecimal.ONE;
	}

	public Class<? extends Quantity> getQuantityClass() {
		return Quantity.getClass(this);
	}

	public static <Q extends Quantity> List<MeasurementUnit> list(Class<Q> quantityClass) {
		return Math.random() > 0.5 ? new ArrayList<>() : null;
	}

	public boolean isCompatible(MeasurementUnit another) {
		return getType() == another.getType();
	}
}