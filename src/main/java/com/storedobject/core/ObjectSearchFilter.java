package com.storedobject.core;

import com.storedobject.common.FilterProvider;

import java.util.function.Predicate;

/**
 * Class that maintains a filter condition that can be used in {@link StoredObject}'s query/list/get methods.
 *
 * @author Syam
 */
public class ObjectSearchFilter {

	private String condition;
	private FilterProvider filterProvider;

	private String getFilterInt() {
		FilterProvider filterProvider = getFilterProvider();
		String condition = getCondition();
		if(filterProvider == null) {
			return condition;
		}
		String c = filterProvider.getFilterCondition();
		if(StringUtility.isWhite(c)) {
			return condition;
		}
		if(condition == null) {
			return c;
		}
		return "(" + c + ") AND (" + condition + ")";
	}

	/**
	 * Get the current filter condition.
	 *
	 * @return Filter condition that can be used in {@link StoredObject}'s query/list/get methods.
	 */
	public String getFilter() {
		return getFilter(null);
	}

	/**
	 * Get the current filter condition with an extra condition appended to it.
	 *
	 * @param extraCondition Extra condition to AND it together with the current condition.
	 * @return Filter condition that can be used in {@link StoredObject}'s query/list/get methods.
	 */
	public String getFilter(String extraCondition) {
		if(StringUtility.isWhite(extraCondition)) {
			extraCondition = null;
		}
		String c = getFilterInt();
		if(StringUtility.isWhite(c)) {
			return extraCondition;
		}
		if(StringUtility.isWhite(extraCondition)) {
			return c;
		}
		return "(" + c + ") AND (" + extraCondition + ")";
	}

	/**
	 * Set the values from another.
	 *
	 * @param filter Another filter from which values will be set.
	 */
	public void set(ObjectSearchFilter filter) {
		if(filter == null) {
			condition = null;
			filterProvider = null;
			return;
		}
		condition = filter.condition;
		filterProvider = filter.filterProvider;
	}

	/**
	 * Get the condition.
	 *
	 * @return Condition.
	 */
	public String getCondition() {
		return condition;
	}

	/**
	 * Set the condition.
	 *
	 * @param condition Condition to set.
	 */
	public void setCondition(String condition) {
		this.condition = StringUtility.isWhite(condition) ? null : condition;
	}

	/**
	 * Add a new condition to the existing one if any.
	 *
	 * @param condition Condition to add.
	 */
	public void addCondition(String condition) {
		if(StringUtility.isWhite(condition)) {
			return;
		}
		if(this.condition == null) {
			this.condition = condition;
			return;
		}
		this.condition = "(" + this.condition + ") AND (" + condition + ")";
	}

	/**
	 * Get the "filter provider".
	 *
	 * @return Filter provider.
	 */
	public FilterProvider getFilterProvider() {
		return filterProvider;
	}

	/**
	 * Set a "filter provider".
	 *
	 * @param filterProvider Filter provider to set.
	 */
	public void setFilterProvider(FilterProvider filterProvider) {
		this.filterProvider = filterProvider;
	}

	/**
	 * Add a "filter provider".
	 *
	 * @param filterProvider Filter provider to add.
	 */
	public void addFilterProvider(FilterProvider filterProvider) {
		if(filterProvider == null) {
			return;
		}
		if(this.filterProvider == null) {
			this.filterProvider = filterProvider;
			return;
		}
		FilterProvider old = this.filterProvider;
		this.filterProvider = new DualFilterProvider(old, filterProvider);
	}

	/**
	 * Create a "predicate" that can be used in the Java program to apply the equivalent filter condition of using
	 * {@link #getFilter()}.
	 *
	 * @return Predicate.
	 */
	public Predicate<StoredObject> getPredicate() {
		return getPredicate(null);
	}

	/**
	 * Create a "predicate" that can be used in the Java program to apply the equivalent filter condition of using
	 * {@link #getFilter(String)}.
	 *
	 * @param extraCondition Extra condition.
	 * @return Predicate.
	 */
	public Predicate<StoredObject> getPredicate(String extraCondition) {
		return so -> filter(so) != null;
	}

	/**
	 * Apply filter to an object instance.
	 *
	 * @param object Object to filter.
	 * @param <T> Type of the object.
	 * @return The object is returned if the filter condition is satisfied. Otherwise, <code>null</code>.
	 */
	public <T extends StoredObject> T filter(T object) {
		return filter(object, null);
	}

	/**
	 * Apply filter to an object instance.
	 *
	 * @param object Object to filter.
	 * @param extraCondition Extra condition.
	 * @param <T> Type of the object.
	 * @return The object is returned if the filter condition is satisfied. Otherwise, <code>null</code>.
	 */
	public <T extends StoredObject> T filter(T object, String extraCondition) {
		String f = getFilter(extraCondition);
		if(f == null) {
			return object;
		}
		//noinspection unchecked
		return StoredObject.get((Class<T>)object.getClass(), "T.Id=" + object.getId() + " AND (" + f + ")");
	}

	private static record DualFilterProvider(FilterProvider one, FilterProvider two) implements FilterProvider {

		@Override
		public String getFilterCondition() {
			String a = one.getFilterCondition(), b = two.getFilterCondition();
			if(StringUtility.isWhite(a)) {
				a = null;
			}
			if(StringUtility.isWhite(b)) {
				b = null;
			}
			if(a != null && b != null) {
				return "(" + a + ") AND (" + b + ")";
			}
			return a == null ? b : a;
		}
	}
}
