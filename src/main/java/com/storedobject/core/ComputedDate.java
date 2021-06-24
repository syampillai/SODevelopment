package com.storedobject.core;

import com.storedobject.common.ComputedValue;
import com.storedobject.common.Storable;

import java.sql.Date;
import java.text.ParseException;

public class ComputedDate extends Date implements ComputedValue<Date>, Storable {

	private boolean computed;

	public ComputedDate() {
		this(DateUtility.today(), true);
	}

	public ComputedDate(java.util.Date date) {
		this(date.getTime(), false);
	}

	public ComputedDate(java.util.Date date, boolean computed) {
		this((date == null ? DateUtility.today() : date).getTime(), computed);
	}

	public ComputedDate(long date) {
		this(date, false);
	}

	public ComputedDate(long date, boolean computed) {
		super(date);
		this.computed = computed;
	}

	public ComputedDate(ComputedDate date) {
		this(date, date.computed);
	}

	public static ComputedDate create(Object value) {
		if(value == null) {
			return null;
		}
		if(value instanceof ComputedDate) {
			return (ComputedDate)value;
		}
		if(value instanceof java.util.Date) {
			return new ComputedDate((java.util.Date)value);
		}
		String v = value.toString();
		try {
			return new ComputedDate(Database.dateFormat().parse(v.substring(1, 11)), v.endsWith("t)"));
		} catch (ParseException e) {
			return null;
		}
	}

	public void set(ComputedDate value) {
		if(value == null) {
			setTime(DateUtility.today().getTime());
			setComputed(true);
		} else {
			setTime(value.getTime());
			setComputed(value.computed);
		}
	}
	
	@Override
	public Date getValueObject() {
		return this;
	}
	
	public void setValue(Date value) {
		setTime(value.getTime());
	}

	@Override
	public String getStorableValue() {
		return "ROW('" + Database.format(this) + "','" + (computed ? "t" : "f") + "')::CDATE";
	}

	public boolean isComputed() {
		return computed;
	}

	public void setComputed(boolean computed) {
		this.computed = computed;
	}

	@Override
	public String toString() {
		return DateUtility.formatDate(this);
	}

	@Override
	public boolean equals(Object another) {
		if(!(another instanceof ComputedDate)) {
			return false;
		}
		ComputedDate cd = (ComputedDate)another;
		return cd.computed == computed && DateUtility.isSameDate(this, cd);
	}
	
	@SuppressWarnings("MethodDoesntCallSuperMethod")
	@Override
	public ComputedDate clone() {
		return new ComputedDate(this);
	}
}
