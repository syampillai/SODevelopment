package com.storedobject.core;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * A builder class for constructing queries and retrieving {@link StoredObject} instances based on various criteria.
 * This class provides methods to specify conditions, sorting and other query parameters. This can be used for
 * querying and/or retrieving {@link StoredObject} instances, their link instances  and master instances.
 *
 * @param <T> The type of {@link StoredObject} this builder targets.
 *
 * @author Syam
 */
public class QueryBuilder<T extends StoredObject> {

    private Class<T> objectClass;
    private String columns, where, orderBy;
    private int limit = 0, skip = 0;
    private boolean any = false;
    private int[] distinctColumns;
    private Transaction transaction;

    private QueryBuilder() {
    }

    /**
     * Creates a new {@code QueryBuilder} instance for the specified class type.
     *
     * @param <O>         The type of objects that extend {@code StoredObject}.
     * @param objectClass The class of the object for which the query builder is to be created.
     * @return A new {@code QueryBuilder} instance for the specified object type.
     */
    public static <O extends StoredObject> QueryBuilder<O> from(Class<O> objectClass) {
        QueryBuilder<O> builder = new QueryBuilder<>();
        builder.objectClass = objectClass;
        return builder;
    }

    /**
     * Retrieves the class type of the object represented by this instance.
     *
     * @return the class type of the object
     */
    public Class<T> getObjectClass() {
        return objectClass;
    }

    /**
     * Sets the "where" clause for the query being built.
     *
     * @param where The condition to filter the query results. It should be a valid query condition string.
     * @return The current {@code QueryBuilder} instance with the specified "where" clause applied.
     */
    public QueryBuilder<T> where(String where) {
        this.where = where;
        return this;
    }

    /**
     * Sets the "order by" clause for the query being built.
     *
     * @param orderBy The column or columns by which the query results should be ordered.
     *                It should be a valid column name or a comma-separated list of column names.
     * @return The current {@code QueryBuilder} instance with the specified "order by" clause applied.
     */
    public QueryBuilder<T> orderBy(String orderBy) {
        this.orderBy = orderBy;
        return this;
    }

    /**
     * Sets a limit on the number of records to be retrieved by the query.
     *
     * @param limit The maximum number of results the query should return.
     * @return The current {@code QueryBuilder} instance with the specified limit applied.
     */
    public QueryBuilder<T> limit(int limit) {
        this.limit = limit;
        return this;
    }

    /**
     * Sets an offset to skip a specified number of records or results in the query being built.
     *
     * @param skip The number of records to skip in the result set.
     * @return The current {@code QueryBuilder} instance with the specified skip value applied.
     */
    public QueryBuilder<T> skip(int skip) {
        this.skip = skip;
        return this;
    }

    /**
     * Enables the "any" condition so that all subclasses to be included in the output.
     *
     * @return The current {@code QueryBuilder} instance with the "any" condition applied.
     */
    public QueryBuilder<T> any() {
        this.any = true;
        return this;
    }

    /**
     * Sets the "any" condition for the query, a boolean value indicating whether to retrieve all subclasses or not.
     *
     * @param any A value of true means all subclasses to be included.
     * @return the updated QueryBuilder instance.
     */
    public QueryBuilder<T> any(boolean any) {
        this.any = any;
        return this;
    }

    /**
     * Sets the column names to be retrieved or operated upon in the query being built.
     *
     * @param columns A string representing the column names to include in the query. The column names should
     *                be separated by commas if specifying multiple columns. Since the SO platform supports
     *                dot (.) notation to retrieve related class instances, it may be used to obtain attributes of
     *                related class instances.
     * @return The current {@code QueryBuilder} instance with the specified columns applied.
     */
    public QueryBuilder<T> columns(String columns) {
        this.columns = columns;
        return this;
    }

    /**
     * Specifies the columns by their indexes to be considered as distinct in the query.
     *
     * @param columnNumbers The indexes of the columns to be included as distinct in the query.
     * @return The current {@code QueryBuilder} instance with the specified distinct columns applied.
     */
    public QueryBuilder<T> distinctColumns(int... columnNumbers) {
        this.distinctColumns = columnNumbers;
        return this;
    }

    /**
     * Sets the transaction for the query being built.
     *
     * @param transaction The transaction to be associated with the query.
     * @return The current {@code QueryBuilder} instance with the specified transaction applied.
     */
    public QueryBuilder<T> transaction(Transaction transaction) {
        this.transaction = transaction;
        return this;
    }

    /**
     * Counts the number of records that match the specified query.
     *
     * @param query The {@code Query} object representing the query criteria used to filter
     *              the records to be counted.
     * @return The number of records that match the specified query criteria.
     */
    private int count(Query query) {
        String columns = this.columns;
        this.columns = StoredObject.COUNT_STAR;
        try {
            return Id.count(query);
        } finally {
            this.columns = columns;
        }
    }

    /**
     * Executes the query built using the current configurations of the {@code QueryBuilder}.
     *
     * @return The {@code Query} object representing the query configured with the specified parameters such as
     *         object class, columns, where clause, order by clause, distinct columns, transaction, and limits.
     */
    public Query query() {
        return StoredObject.query(transaction, objectClass, columns, where, orderBy, any, skip, limit, distinctColumns);
    }

    /**
     * Retrieves an iterator containing the results that match the query constructed
     * with the current configurations of {@code QueryBuilder}.
     *
     * @return An {@code ObjectIterator} containing objects of type {@code T} that
     *         satisfy the query criteria, including filters, ordering, limits, and
     *         other specified parameters.
     */
    public ObjectIterator<T> list() {
        return StoredObject.list(transaction, objectClass, where, orderBy, any, skip, limit, distinctColumns);
    }

    /**
     * Counts the number of records that match the query constructed using the current
     * configurations of the {@code QueryBuilder}.
     *
     * @return The number of records that match the specified query criteria.
     */
    public int count() {
        return count(query());
    }

    /**
     * Constructs a query to retrieve the links associated with the specified parent object.
     *
     * @param parent The parent object for which linked objects are to be queried.
     * @return A {@code Query} object representing the links associated with the given parent object.
     */
    public Query queryLinks(StoredObject parent) {
        return queryLinks(parent, 0);
    }

    /**
     * Constructs a query to retrieve the links associated with the specified parent object
     * and of a specific link type.
     *
     * @param parent The parent object for which linked objects of the specified link type are to be queried.
     * @param linkType The type of links to filter the query results.
     * @return A {@code Query} object representing the links of the given type associated with the specified parent object.
     */
    public Query queryLinks(StoredObject parent, int linkType) {
        return parent.queryLinks(transaction, StoredObject.TYPE_EQUALS + linkType, objectClass, columns, where,
                orderBy, any, skip, limit, distinctColumns);
    }

    /**
     * Returns an iterator over the linked objects of the specified parent object.
     *
     * @param parent the parent StoredObject whose linked objects are to be listed
     * @return an iterator over the linked objects of the specified parent
     */
    public ObjectIterator<T> listLinks(StoredObject parent) {
        return listLinks(parent, 0);
    }

    /**
     * Retrieves an iterator over objects that are linked to the given parent object with the specified link type.
     *
     * @param parent the parent object whose links are to be listed
     * @param linkType the type of links to filter the results
     * @return an iterator over the linked objects of the specified type
     */
    public ObjectIterator<T> listLinks(StoredObject parent, int linkType) {
        return parent.listLinks(transaction, StoredObject.TYPE_EQUALS + linkType, objectClass, where, orderBy,
                any, skip, limit, distinctColumns);
    }

    /**
     * Counts the number of links associated with the given parent object.
     *
     * @param parent the parent StoredObject for which the links are to be counted
     * @return the total number of links associated with the specified parent
     */
    public int countLinks(StoredObject parent) {
        return countLinks(parent, 0);
    }

    /**
     * Counts the number of links of a specified type for a given parent object.
     *
     * @param parent    the parent object whose links are to be counted
     * @param linktType the type of links to be counted
     * @return the count of links of the specified type for the given parent object
     */
    public int countLinks(StoredObject parent, int linktType) {
        return count(queryLinks(parent, linktType));
    }

    /**
     * Queries and retrieves master objects related to the given stored object link.
     *
     * @param link the StoredObject that serves as a reference for querying master objects
     * @return a Query object containing the result set of master objects matching the criteria
     */
    public Query queryMasters(StoredObject link) {
        return queryMasters(link, 0);
    }

    /**
     * Queries the master objects associated with a given stored object link and link type.
     *
     * @param link      the stored object link to query masters for
     * @param linkType  the type of link defining the relationship between the object and its masters
     * @return a Query object containing the results of the master objects query
     */
    public Query queryMasters(StoredObject link, int linkType) {
        return link.queryMasters(transaction, StoredObject.TYPE_EQUALS + linkType, objectClass, columns, where,
                orderBy, any, skip, limit, distinctColumns);
    }

    /**
     * Retrieves an iterator over master objects linked to the specified stored object.
     *
     * @param link the stored object for which the master objects are to be listed
     * @return an iterator over the master objects associated with the given stored object
     */
    public ObjectIterator<T> listMasters(StoredObject link) {
        return listMasters(link, 0);
    }

    /**
     * Retrieves an iterator over the master objects linked to the specified stored object.
     *
     * @param link The stored object for which to list the master objects.
     * @param linkType The type of the link used to filter the master objects.
     * @return An iterator over the master objects linked to the specified stored object.
     */
    public ObjectIterator<T> listMasters(StoredObject link, int linkType) {
        return link.listMasters(transaction, StoredObject.TYPE_EQUALS + linkType, objectClass, where, orderBy,
                any, skip, limit, distinctColumns);
    }

    /**
     * Counts the number of master objects associated with the given link.
     *
     * @param link The StoredObject instance representing the link for which the count of masters is required.
     * @return The count of master objects associated with the provided link.
     */
    public int countMasters(StoredObject link) {
        return countMasters(link, 0);
    }

    /**
     * Constructs and returns an SQL query string based on the given parameters and object class.
     * This method uses the StoredObjectUtility class to create an SQL statement that includes
     * the specified column selections, filtering conditions, ordering, and other query attributes.
     *
     * @return a String representing the constructed SQL query based on the provided parameters.
     */
    public String querySQL() {
        return StoredObjectUtility.createSQL(ClassAttribute
                .get(objectClass), columns, where, orderBy, any, true, skip, limit, distinctColumns);
    }

    /**
     * Counts the number of master objects associated with a given link and link type.
     *
     * @param link the StoredObject instance representing the link.
     * @param linkType an integer representing the type of link.
     * @return the count of master objects associated with the specified link and link type.
     */
    public int countMasters(StoredObject link, int linkType) {
        return count(queryMasters(link, linkType));
    }

    /**
     * Create an "exists condition" that can be used for retrieving or querying {@link StoredObject} instances.
     *
     * @param objectClass The object class against which the existence should be checked.
     * @param attributeName Attribute name that matches the {@link Id} of the object that is going to be
     *                      retrieved or queried.
     * @param <O> Type of this object class.
     * @return Condition string suitable for retrieving or querying another type of {@link StoredObject} instances
     * where this object is related.
     */
    public static <O extends StoredObject> String getExistsCondition(Class<O> objectClass, String attributeName) {
        return getExistsCondition(objectClass, attributeName, null);
    }

    /**
     * Create an "exists condition" that can be used for retrieving or querying {@link StoredObject} instances.
     *
     * @param objectClass The object class against which the existence should be checked.
     * @param attributeName Attribute name that matches the {@link Id} of the object that is going to be
     *                      retrieved or queried.
     * @param condition Condition to be applied to this object class. Null should be passed if no condition needs
     *                  to be applied.
     * @param <O> Type of this object class.
     * @return Condition string suitable for retrieving or querying another type of {@link StoredObject} instances
     * where this object is related.
     */
    public static <O extends StoredObject> String getExistsCondition(Class<O> objectClass, String attributeName,
                                                                     String condition) {
        return condition("", objectClass, attributeName, condition);
    }

    /**
     * Create a "not exists condition" that can be used for retrieving or querying {@link StoredObject} instances.
     *
     * @param objectClass The object class against which the existence should be checked.
     * @param attributeName Attribute name that matches the {@link Id} of the object that is going to be
     *                      retrieved or queried.
     * @param <O> Type of this object class.
     * @return Condition string suitable for retrieving or querying another type of {@link StoredObject} instances
     * where this object is related.
     */
    public static <O extends StoredObject> String getNotExistsCondition(Class<O> objectClass, String attributeName) {
        return getNotExistsCondition(objectClass, attributeName, null);
    }

    /**
     * Create a "not exists condition" that can be used for retrieving or querying {@link StoredObject} instances.
     *
     * @param objectClass The object class against which the existence should be checked.
     * @param attributeName Attribute name that matches the {@link Id} of the object that is going to be
     *                      retrieved or queried.
     * @param condition Condition to be applied to this object class. Null should be passed if no condition needs
     *                  to be applied.
     * @param <O> Type of this object class.
     * @return Condition string suitable for retrieving or querying another type of {@link StoredObject} instances
     * where this object is related.
     */
    public static <O extends StoredObject> String getNotExistsCondition(Class<O> objectClass, String attributeName,
                                                                        String condition) {
        return condition(" NOT", objectClass, attributeName, condition);
    }

    private static <O extends StoredObject> String condition(String not, Class<O> objectClass, String attributeName,
                                                             String condition) {
        return (char)2 + Base64.getEncoder().encodeToString(("T.Id" + not + " IN ("
                + StoredObject.createSQL(ClassAttribute.get(objectClass),
                "T." + attributeName, condition, null, true, false, 0, 0, null) + ")")
                .getBytes(StandardCharsets.UTF_8)) + (char)3;
    }
}
