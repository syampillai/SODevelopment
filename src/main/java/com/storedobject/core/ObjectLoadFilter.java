package com.storedobject.core;

import com.storedobject.common.FilterProvider;

import java.util.function.Predicate;

/**
 * Class that maintains filter conditions and associated details that can be used in {@link StoredObject}'s
 * query/list/get methods and {@link ObjectLoader}s.
 *
 * @param <T> Type of object.
 * @author Syam
 */
public class ObjectLoadFilter<T extends StoredObject> {

	private String condition;
	private FilterProvider filterProvider;
	private ObjectLoadFilter<T> child;
	private Predicate<? super T> viewFilter;
	private Predicate<T> loadingPredicate;
	private String orderBy;
	private boolean any;
	private StoredObject master;
	private int linkType = 0;

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
			viewFilter = null;
			return;
		}
		condition = filter.condition;
		filterProvider = filter.filterProvider;
		child = filter.child;
		viewFilter = filter.viewFilter;
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
	 * Set the filter that needs to be applied after loading into the memory from the DB - for "view filtering".
	 *
	 * @param viewFilter Filter predicate for viewing.
	 */
	public void setViewFilter(Predicate<? super T> viewFilter) {
		this.viewFilter = viewFilter;
	}

	/**
	 * Get the filter that needs to be applied after loading into the memory from the DB - for "view filtering".
	 *
	 * @return Filter predicate for viewing.
	 */
	public Predicate<? super T> getViewFilter() {
		return viewFilter;
	}

	/**
	 * Set the filter that needs to be applied while loading into the memory from the DB - for "view filtering".
	 *
	 * @param loadingPredicate Filter predicate to be applied while loading.
	 */
	public void setLoadingPredicate(Predicate<T> loadingPredicate) {
		this.loadingPredicate = loadingPredicate;
	}

	/**
	 * Get the filter that needs to be applied while loading into the memory from the DB - for "view filtering".
	 *
	 * @return Filter predicate to be applied while loading.
	 */
	public Predicate<T> getLoadingPredicate() {
		return loadingPredicate;
	}

	/**
	 * Create a "predicate" that can be used in the Java program to apply the equivalent filter condition of using
	 * {@link #getFilter()} and {@link #getLoadingPredicate()}.
	 *
	 * @return Predicate.
	 */
	public Predicate<T> getPredicate() {
		return getPredicate(null);
	}

	/**
	 * Create a "predicate" that can be used in the Java program to apply the equivalent filter condition of using
	 * {@link #getFilter(String)} and {@link #getLoadingPredicate()}.
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
		Predicate<T> lf = getLoadingPredicate();
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

	/**
	 * Set the "ORDER BY" clause.
	 * <p>Note: This is used for convenience only, will not affect any filter conditions.</p>
	 *
	 * @param orderBy "ORDER BY" clause to set.
	 */
	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	/**
	 * Get the "ORDER BY" clause set via {@link #setOrderBy(String)}.
	 * <p>Note: This is used for convenience only, will not affect any filter conditions.</p>
	 *
	 * @return "ORDER BY" clause set earlier.
	 */
	public String getOrderBy() {
		return orderBy;
	}

	/**
	 * Set the "any". (Can be used to determine whether subclasses to retrieved or not)
	 * <p>Note: This is used for convenience only, will not affect any filter conditions.</p>
	 *
	 * @param any True/false.
	 */
	public void setAny(boolean any) {
		this.any = any;
	}

	/**
	 * Get the value of "any". (Can be used to determine whether subclasses to retrieved or not)
	 * @return The value of "any" set earlier.
	 */
	public boolean isAny() {
		return any;
	}

	/**
	 * Set the master instance for this filter. (Can be used when retrieving links).
	 * <p>Note: This is used for convenience only, will not affect any filter conditions.</p>
	 * @param master Master instance to set.
	 */
	public void setMaster(StoredObject master) {
		this.master = master;
	}

	/**
	 * Get the master instance for this filter. (Can be used when retrieving links).
	 * <p>Note: This is used for convenience only, will not affect any filter conditions.</p>
	 * @return Instance of the master object if set.
	 */
	public StoredObject getMaster() {
		return master;
	}

	/**
	 * Set the "link type" for this filter. (Can be used when retrieving links).
	 * <p>Note: This is used for convenience only, will not affect any filter conditions.</p>
	 * @param linkType Type of link to set.
	 */
	public void setLinkType(int linkType) {
		this.linkType = linkType;
	}

	/**
	 * Get the "link type" for this filter. (Can be used when retrieving links).
	 * <p>Note: This is used for convenience only, will not affect any filter conditions.</p>
	 * @return Link type.
	 */
	public int getLinkType() {
		return linkType;
	}

	private record DualFilterProvider(FilterProvider one, FilterProvider two) implements FilterProvider {

		@Override
		public String getFilterCondition() {
			return and(one.getFilterCondition(), two.getFilterCondition());
		}
	}
}
