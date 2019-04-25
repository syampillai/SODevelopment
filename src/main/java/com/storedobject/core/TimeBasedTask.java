package com.storedobject.core;

import java.sql.Timestamp;

import com.storedobject.core.converter.MinutesValueConverter;

public class TimeBasedTask extends AbstractTask {

    public TimeBasedTask() {
    }

    public static void columns(Columns columns) {
    }

    public void setNextDue(Timestamp nextDue) {
    }

    public Timestamp getNextDue() {
        return null;
    }

	public int getPeriodicity() {
		return periodicity;
	}

    public static MinutesValueConverter getPeriodicityConverter() {
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