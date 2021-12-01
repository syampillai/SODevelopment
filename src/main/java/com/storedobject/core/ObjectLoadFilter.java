package com.storedobject.core;

import com.storedobject.common.FilterProvider;

import java.util.function.Predicate;

/**
 * Class that maintains a filter condition that can be used in {@link StoredObject}'s query/list/get methods.
 *
 * @param <T> Type of object.
 * @author Syam
 */
public class ObjectLoadFilter<T extends StoredObject> {

	private String condition;
	private FilterProvider filterProvider;
	private ObjectLoadFilter<T> child;
	private Predicate<T> loadedPredicate;

	/**
	 * Add a child filter to this filter. The filter condition of the child will be appended to this filter condition.
	 *
	 * @param child Child to be added.
	 */
	public void add(ObjectLoadFilter<T> child) {
		if(child != null) {
			if(this.child == null) {
				this.child = child;
			} else {
				this.child.add(child);
			}
		}
	}

	private String getFilterInt() {
		String f = getFilterOfThis();
		return child == null ? f : and(f, child.getFilterInt());
	}

	private String getFilterOfThis() {
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
	public void set(ObjectLoadFilter<T> filter) {
		if(filter == null) {
			condition = null;
			filterProvider = null;
			child = null;
			loadedPredicate = null;
			return;
		}
		condition = filter.condition;
		filterProvider = filter.filterProvider;
		child = filter.child;
		loadedPredicate = filter.loadedPredicate;
	}

	/**
	 * Clear all values.
	 */
	public void reset() {
		set(null);
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
	 * Set the filter that needs to be applied after loading into the memory from the DB.
	 *
	 * @param loadedPredicate Filter predicate.
	 */
	public void setLoadedPredicate(Predicate<T> loadedPredicate) {
		this.loadedPredicate = loadedPredicate;
	}

	/**
	 * Get the filter that needs to be applied after loading into the memory from the DB.
	 *
	 * @return Filter predicate.
	 */
	public Predicate<T> getLoadedPredicate() {
		return loadedPredicate;
	}

	/**
	 * Create a "predicate" that can be used in the Java program to apply the equivalent filter condition of using
	 * {@link #getFilter()} and {@link #getLoadedPredicate()}.
	 *
	 * @return Predicate.
	 */
	public Predicate<T> getPredicate() {
		return getPredicate(null);
	}

	/**
	 * Create a "predicate" that can be used in the Java program to apply the equivalent filter condition of using
	 * {@link #getFilter(String)} and {@link #getLoadedPredicate()}.
	 *
	 * @param extraCondition Extra condition.
	 * @return Predicate.
	 */
	public Predicate<T> getPredicate(String extraCondition) {
		return so -> filter(so, extraCondition) != null;
	}

	/**
	 * Apply filter to an object instance.
	 *
	 * @param object Object to filter.
	 * @return The object is returned if the filter condition is satisfied. Otherwise, <code>null</code>.
	 */
	public T filter(T object) {
		return filter(object, null);
	}

	/**
	 * Apply filter to an object instance.
	 *
	 * @param object Object to filter.
	 * @param extraCondition Extra condition.
	 * @return The object is returned if the filter condition is satisfied. Otherwise, <code>null</code>.
	 */
	public T filter(T object, String extraCondition) {
		Predicate<T> lf = getLoadedPredicate();
		if(lf != null && !lf.test(object)) {
			return null;
		}
		String f = getFilter(extraCondition);
		if(f == null) {
			return object;
		}
		//noinspection unchecked
		return StoredObject.get((Class<T>)object.getClass(), "T.Id=" + object.getId() + " AND (" + f + ")");
	}

	/**
	 * Join 2 conditions via AND.
	 *
	 * @param one First condition (could be null).
	 * @param two Second condition (could be null).
	 * @return ANDed condition.
	 */
	public static String and(String one, String two) {
		if(StringUtility.isWhite(one)) {
			one = null;
		}
		if(StringUtility.isWhite(two)) {
			two = null;
		}
		if(one != null && two != null) {
			return "(" + one + ") AND (" + two + ")";
		}
		return one == null ? two : one;
	}

	private record DualFilterProvider(FilterProvider one, FilterProvider two) implements FilterProvider {

		@Override
		public String getFilterCondition() {
			return and(one.getFilterCondition(), two.getFilterCondition());
		}
	}
}
