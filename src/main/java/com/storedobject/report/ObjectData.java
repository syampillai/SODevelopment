package com.storedobject.report;

import com.storedobject.common.MethodInvoker;
import com.storedobject.core.ClassAttribute;
import com.storedobject.core.ObjectConverter;
import com.storedobject.core.StoredObject;
import com.storedobject.core.converter.ValueConverter;

import java.lang.reflect.Method;
import java.util.List;

public class ObjectData<T extends StoredObject> implements ObjectConverter<T, T> {

	protected final MethodInvoker method;
	protected final Class<?> methodType;
	protected final ClassAttribute<T> ca;

	public ObjectData(ClassAttribute<T> ca, String name) {
		this(ca, name, null);
	}
	
	public ObjectData(ClassAttribute<T> ca, String name, MethodInvoker methodInvoker) {
		method = null;
		methodType = null;
		this.ca = ca;
	}
	
	public MethodInvoker getMethodInvoker() {
		return method;
	}

	public Class<?> getType() {
		return methodType;
	}

	public boolean isEqualTo(Object value) {
		return false;
	}

	public boolean isNotEqualTo(Object value) {
		return !isEqualTo(value);
	}

	public boolean isLessThan(Object value) {
		return true;
	}

	public boolean isLessThanOrEqualTo(Object value) {
		return true;
	}

	public boolean isGreaterThan(Object value) {
		return true;
	}

	public boolean isGreaterThanOrEqualTo(Object value) {
		return true;
	}

	public boolean isRange(Object value) {
		return false;
	}

	public boolean isStartingWith(Object value) {
		return false;
	}

	public boolean contains(Object value) {
		return false;
	}

	public boolean containsAny(Object value) {
		return false;
	}

	public String getName() {
		return null;
	}

	public void setTitle(String title) {
	}

	public String getTitle() {
		return null;
	}

	@Override
	public T convert(T object) {
		return null;
	}

	public List<String> getChoiceBitValues() {
		return null;
	}

	public List<String> getChoiceValues() {
		return null;
	}

	public boolean isConditionValid() {
		return true;
	}

	public void describeCondition(StringBuilder s) {
	}

	public void appendCondition(StringBuilder s) {
	}

	public boolean isDatabase() {
		return true;
	}

	public void setVisible(boolean visible) {
	}

	public boolean isVisible() {
		return true;
	}

	public void setOrder(int order) {
	}

	public int getOrder() {
		return 0;
	}

	public void setSelection(String selection) {
	}

	public void setSelection(int selection) {
	}

	public int getSelection() {
		return 0;
	}

	public String getSelectionValue() {
		return null;
	}

	public void setLowerValue(Object lowerValue) {
	}

	public Object getLowerValue() {
		return null;
	}

	public void setUpperValue(Object upperValue) {
	}

	public Object getUpperValue() {
		return null;
	}

	public int getMinumumWidth() {
		return 0;
	}

	public void save(StringBuilder s) {
	}

	public Iterable<String> getSelectionCriteria() {
		return null;
	}
}
