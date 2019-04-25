package com.storedobject.core;

import java.sql.Date;

import com.storedobject.core.converter.DaysValueConverter;

public class DateBasedTask extends AbstractTask {

	public DateBasedTask() {
	}

	public static void columns(Columns columns) {
	}

	public void setNextDue(Date nextDue) {
	}

	public Date getNextDue() {
		return null;
	}

	public int getPeriodicity() {
		return 0;
	}

	public static DaysValueConverter getPeriodicityConverter() {
		return null;
	}

	public static String getPeriodicityValue(int value) {
		return null;
	}

	public String getPeriodicityValue() {
		return null;
	}

	@Override
	public void computeNextDue() {
	}

	@Override
	public boolean isDue() {
		return false;
	}

	@Override
	public String getNextDueValue() {
		return null;
	}

	@Override
	public String formatNextDue() {
		return null;
	}
}