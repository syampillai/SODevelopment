package com.storedobject.report;

import com.storedobject.core.ClassAttribute;
import com.storedobject.common.MethodInvoker;
import com.storedobject.core.StoredObject;

import java.util.ArrayList;

public class ReportColumn<T extends StoredObject> extends ObjectData<T> {

	private final ArrayList<Object> values = new ArrayList<>();
	private int width;

	public ReportColumn(ClassAttribute<T> ca, String name) {
		this(ca, name, null);
	}
	
	public ReportColumn(ClassAttribute<T> ca, String name, MethodInvoker methodInvoker) {
		super(ca, name, methodInvoker);
		width = getMinumumWidth();
	}

	public void setValue(T object) {
		Object v = method.invoke(object, false);
		if(v == null) {
			v = "";
		}
		String s;
		if(valueConverter != null) {
			v = valueConverter.format(v);
		}
		if(v == null) {
			v = "";
		}
		s = v.toString();
		if(s == null) {
			s = "";
		}
		if(s.length() > width) {
			width = Math.min(Math.max(width, 30), s.length());
		}
		values.add(v);
	}

	public boolean valueExists() {
		return values.size() > 0;
	}

	public Object removeValue() {
		return values.size() > 0 ? values.remove(0) : null;
	}

	public int getWidth() {
		return Math.max(width, getMinumumWidth());
	}
	
	public void setWidht(int width) {
		if(this.width < width) {
			this.width = width;
		}
	}
}
