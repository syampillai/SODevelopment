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
     * @param any A value of true means all subclasses to be included. If any is null, it is considered as false.
     * @return the updated QueryBuilder instance.
     */
    public QueryBuilder<T> any(Boolean any) {
        return any(any != null && any);
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
     * Get an object instance. The first item is retrieved.
     *
     * @return An object instance or null.
     */
    public T get() {
        int limit = this.limit;
        this.limit = 1;
        try {
            return list().findFirst();
        } finally {
            this.limit = limit;
        }
    }

    /**
     * Get the first master object.
     *
     * @param link Link object.
     * @return A master instance or null.
     */
    public T getMaster(StoredObject link) {
        return getMaster(link.getId());
    }

    /**
     * Get the first master object.
     *
     * @param link Link object.
     * @param linkType Link type.
     * @return A master instance or null.
     */
    public T getMaster(StoredObject link, int linkType) {
        return getMaster(link.getId(), linkType);
    }

    /**
     * Get the first master object.
     *
     * @param linkId Link Id.
     * @return A master instance or null.
     */
    public T getMaster(Id linkId) {
        return getMaster(linkId, 0);
    }

    /**
     * Get the first master object.
     *
     * @param linkId Link Id.
     * @param linkType Link type.
     * @return A master instance or null.
     */
    public T getMaster(Id linkId, int linkType) {
        int limit = this.limit;
        this.limit = 1;
        try {
            return listMasters(linkId, linkType).findFirst();
        } finally {
            this.limit = limit;
        }
    }

    /**
     * Get the first link object.
     *
     * @param master Master object.
     * @return A master instance or null.
     */
    public T getLink(StoredObject master) {
        return getLink(master.getId());
    }

    /**
     * Get the first link object.
     *
     * @param master Master object.
     * @param linkType Link type.
     * @return A master instance or null.
     */
    public T getLink(StoredObject master, int linkType) {
        return getLink(master.getId(), linkType);
    }

    /**
     * Get the first link object.
     *
     * @param masterId Master Id.
     * @return A master instance or null.
     */
    public T getLink(Id masterId) {
        return getLink(masterId, 0);
    }

    /**
     * Get the first link object.
     *
     * @param masterId Master Id.
     * @param linkType Link type.
     * @return A master instance or null.
     */
    public T getLink(Id masterId, int linkType) {
        int limit = this.limit;
        this.limit = 1;
        try {
            return listLinks(masterId, linkType).findFirst();
        } finally {
            this.limit = limit;
        }
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
     * Check whether any entry exists or not.
     *
     * @return True/false
     */
    public boolean exists() {
        return StoredObject.exists(transaction, objectClass, where, any);
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
        return queryLinks(parent.getId());
    }

    /**
     * Constructs a query to retrieve the links associated with the specified parent object.
     *
     * @param parentId The parent object Id for which linked objects are to be queried.
     * @return A {@code Query} object representing the links associated with the given parent object.
     */
    public Query queryLinks(Id parentId) {
        return queryLinks(parentId, 0);
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
        return queryLinks(parent.getId(), linkType);
    }

    /**
     * Constructs a query to retrieve the links associated with the specified parent object
     * and of a specific link type.
     *
     * @param parentId The parent object Id for which linked objects of the specified link type are to be queried.
     * @param linkType The type of links to filter the query results.
     * @return A {@code Query} object representing the links of the given type associated with the specified parent object.
     */
    public Query queryLinks(Id parentId, int linkType) {
        return parentId.queryLinks(transaction, StoredObject.TYPE_EQUALS + linkType, objectClass, columns, where,
                orderBy, any, skip, limit, distinctColumns);
    }

    /**
     * Check whether link objects exists associated with the specified parent object.
     *
     * @param parent The parent object for which linked objects are to be queried.
     * @return True/false.
     */
    public boolean existsLinks(StoredObject parent) {
        return existsLinks(parent.getId());
    }

    /**
     * Check whether link objects exists associated with the specified parent object.
     *
     * @param parentId The parent object Id for which linked objects are to be queried.
     * @return True/false.
     */
    public boolean existsLinks(Id parentId) {
        return existsLinks(parentId, 0);
    }

    /**
     * Check whether link objects exists  associated with the specified parent object
     * and of a specific link type.
     *
     * @param parent The parent object for which linked objects of the specified link type are to be queried.
     * @param linkType The type of links to filter the query results.
     * @return True/false.
     */
    public boolean existsLinks(StoredObject parent, int linkType) {
        return StoredObject.exists(queryLinks(parent, linkType));
    }

    /**
     * Check whether link objects exists  associated with the specified parent object
     * and of a specific link type.
     *
     * @param parentId The parent object Id for which linked objects of the specified link type are to be queried.
     * @param linkType The type of links to filter the query results.
     * @return True/false.
     */
    public boolean existsLinks(Id parentId, int linkType) {
        return StoredObject.exists(queryLinks(parentId, linkType));
    }

    /**
     * Returns an iterator over the linked objects of the specified parent object.
     *
     * @param parent the parent StoredObject whose linked objects are to be listed
     * @return an iterator over the linked objects of the specified parent
     */
    public ObjectIterator<T> listLinks(StoredObject parent) {
        return listLinks(parent.getId());
    }

    /**
     * Returns an iterator over the linked objects of the specified parent object.
     *
     * @param parentId the parent object Id whose linked objects are to be listed
     * @return an iterator over the linked objects of the specified parent
     */
    public ObjectIterator<T> listLinks(Id parentId) {
        return listLinks(parentId, 0);
    }

    /**
     * Retrieves an iterator over objects that are linked to the given parent object with the specified link type.
     *
     * @param parent the parent object whose links are to be listed
     * @param linkType the type of links to filter the results
     * @return an iterator over the linked objects of the specified type
     */
    public ObjectIterator<T> listLinks(StoredObject parent, int linkType) {
        return listLinks(parent.getId(), linkType);
    }

    /**
     * Retrieves an iterator over objects that are linked to the given parent object with the specified link type.
     *
     * @param parentId the parent object Id whose links are to be listed
     * @param linkType the type of links to filter the results
     * @return an iterator over the linked objects of the specified type
     */
    public ObjectIterator<T> listLinks(Id parentId, int linkType) {
        return parentId.listLinks(transaction, StoredObject.TYPE_EQUALS + linkType, objectClass, where, orderBy,
                any, skip, limit, distinctColumns);
    }

    /**
     * Counts the number of links associated with the given parent object.
     *
     * @param parent the parent object for which the links are to be counted
     * @return the total number of links associated with the specified parent
     */
    public int countLinks(StoredObject parent) {
        return countLinks(parent, 0);
    }

    /**
     * Counts the number of links associated with the given parent object.
     *
     * @param parentId the parent object Id for which the links are to be counted
     * @return the total number of links associated with the specified parent
     */
    public int countLinks(Id parentId) {
        return countLinks(parentId, 0);
    }

    /**
     * Counts the number of links of a specified type for a given parent object.
     *
     * @param parent    the parent object whose links are to be counted
     * @param linkType the type of links to be counted
     * @return the count of links of the specified type for the given parent object
     */
    public int countLinks(StoredObject parent, int linkType) {
        return countLinks(parent.getId(), linkType);
    }

    /**
     * Counts the number of links of a specified type for a given parent object.
     *
     * @param parentId the parent object Id whose links are to be counted
     * @param linkType the type of links to be counted
     * @return the count of links of the specified type for the given parent object
     */
    public int countLinks(Id parentId, int linkType) {
        return count(queryLinks(parentId, linkType));
    }

    /**
     * Check whether master objects exists related to the given stored object link.
     *
     * @param link the StoredObject that serves as a reference for querying master objects
     * @return True/false.
     */
    public boolean existsMasters(StoredObject link) {
        return existsMasters(link.getId());
    }

    /**
     * Check whether master objects exists related to the given stored object link.
     *
     * @param linkId the link object that serves as a reference for querying master objects
     * @return True/false.
     */
    public boolean existsMasters(Id linkId) {
        return existsMasters(linkId, 0);
    }

    /**
     * Check whether master objects exists associated with a given stored object link and link type.
     *
     * @param link      the stored object link to query masters for
     * @param linkType  the type of link defining the relationship between the object and its masters
     * @return True/false.
     */
    public boolean existsMasters(StoredObject link, int linkType) {
        return StoredObject.exists(queryMasters(link, linkType));
    }

    /**
     * Check whether master objects exists associated with a given stored object link and link type.
     *
     * @param link      the stored object link to query masters for
     * @param linkType  the type of link defining the relationship between the object and its masters
     * @return True/false.
     */
    public boolean existsMasters(Id link, int linkType) {
        return StoredObject.exists(queryMasters(link, linkType));
    }

    /**
     * Queries and retrieves master objects related to the given stored object link.
     *
     * @param link the StoredObject that serves as a reference for querying master objects
     * @return a Query object containing the result set of master objects matching the criteria
     */
    public Query queryMasters(StoredObject link) {
        return queryMasters(link.getId());
    }

    /**
     * Queries and retrieves master objects related to the given stored object link.
     *
     * @param linkId the object Id that serves as a reference for querying master objects
     * @return a Query object containing the result set of master objects matching the criteria
     */
    public Query queryMasters(Id linkId) {
        return queryMasters(linkId, 0);
    }

    /**
     * Queries the master objects associated with a given stored object link and link type.
     *
     * @param link the stored object link to query masters for
     * @param linkType the type of link defining the relationship between the object and its masters
     * @return a Query object containing the results of the master objects query
     */
    public Query queryMasters(StoredObject link, int linkType) {
        return queryMasters(link.getId(), linkType);
    }

    /**
     * Queries the master objects associated with a given stored object link and link type.
     *
     * @param linkId the stored object link Id to query masters for
     * @param linkType  the type of link defining the relationship between the object and its masters
     * @return a Query object containing the results of the master objects query
     */
    public Query queryMasters(Id linkId, int linkType) {
        return linkId.queryMasters(transaction, StoredObject.TYPE_EQUALS + linkType, objectClass, columns, where,
                orderBy, any, skip, limit, distinctColumns);
    }

    /**
     * Retrieves an iterator over master objects linked to the specified stored object.
     *
     * @param link the stored object for which the master objects are to be listed
     * @return an iterator over the master objects associated with the given stored object
     */
    public ObjectIterator<T> listMasters(StoredObject link) {
        return listMasters(link.getId());
    }

    /**
     * Retrieves an iterator over master objects linked to the specified stored object.
     *
     * @param linkId the link object Id for which the master objects are to be listed
     * @return an iterator over the master objects associated with the given stored object
     */
    public ObjectIterator<T> listMasters(Id linkId) {
        return listMasters(linkId, 0);
    }

    /**
     * Retrieves an iterator over the master objects linked to the specified stored object.
     *
     * @param link The stored object for which to list the master objects.
     * @param linkType The type of the link used to filter the master objects.
     * @return An iterator over the master objects linked to the specified stored object.
     */
    public ObjectIterator<T> listMasters(StoredObject link, int linkType) {
        return listMasters(link.getId(), linkType);
    }

    /**
     * Retrieves an iterator over the master objects linked to the specified stored object.
     *
     * @param linkId The link object Id for which to list the master objects.
     * @param linkType The type of the link used to filter the master objects.
     * @return An iterator over the master objects linked to the specified stored object.
     */
    public ObjectIterator<T> listMasters(Id linkId, int linkType) {
        return linkId.listMasters(transaction, StoredObject.TYPE_EQUALS + linkType, objectClass, where, orderBy,
                any, skip, limit, distinctColumns);
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
     * Counts the number of master objects associated with the given link.
     *
     * @param link The link object representing the link for which the count of masters is required.
     * @return The count of master objects associated with the provided link.
     */
    public int countMasters(StoredObject link) {
        return countMasters(link.getId());
    }

    /**
     * Counts the number of master objects associated with the given link.
     *
     * @param link The link object Id representing the link for which the count of masters is required.
     * @return The count of master objects associated with the provided link.
     */
    public int countMasters(Id link) {
        return countMasters(link, 0);
    }

    /**
     * Counts the number of master objects associated with a given link and link type.
     *
     * @param link the link object representing the link.
     * @param linkType an integer representing the type of link.
     * @return the count of master objects associated with the specified link and link type.
     */
    public int countMasters(StoredObject link, int linkType) {
        return count(queryMasters(link, linkType));
    }

    /**
     * Counts the number of master objects associated with a given link and link type.
     *
     * @param linkId the link object Id representing the link.
     * @param linkType an integer representing the type of link.
     * @return the count of master objects associated with the specified link and link type.
     */
    public int countMasters(Id linkId, int linkType) {
        return count(queryMasters(linkId, linkType));
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
