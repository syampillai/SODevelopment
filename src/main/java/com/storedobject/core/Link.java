package com.storedobject.core;

import com.storedobject.common.SORuntimeException;
import com.storedobject.common.StringList;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * A class to represent a link of the master object to a child object.
 *
 * @param <L> Type of the child object.
 * @author Syam
 */
public class Link<L extends StoredObject> {

    private final Class<? extends StoredObject> masterClass;
    private Class<L> linkClass = null;
    private int type = 0;
    private String name, orderBy, condition;
    private Predicate<L> loadPredicate;
    private StringList browseColumns;
    private boolean any, readOnly;

    /**
     * Creates a new Link object with the specified master class.
     *
     * @param masterClass the class of the master object with which the link is associated;
     *                    must be a subclass of StoredObject
     */
    public Link(Class<? extends StoredObject> masterClass) {
        this.masterClass = masterClass;
    }

    /**
     * Creates a new Link object based on the specified master class and link details.
     * <pre>
     *     Format of the link details:
     *     Name|Class name[/Any[/Link type]]|Order by|Columns
     *     Name|Class name[/Link type[/Any]]Order by|Columns
     * </pre>
     *
     * @param masterClass the class of the master object with which the link is associated;
     *                    must be a subclass of StoredObject
     * @param linkDetails a string defining the link details; the format of this string
     *                    determines various properties of the created link
     * @return a newly created Link object configured according to the given master class and link details
     * @throws SOClassError if the link details are invalid for the specified master class
     */
    public static Link<?> create(Class<? extends StoredObject> masterClass, String linkDetails) {
        Link<?> link = new Link<>(masterClass);
        link.type = 0;
        String[] cols = null;
        int slash;
        link.any = false;
        if(linkDetails.equals("Children")) {
            link.name = linkDetails;
            link.setLinkClass(masterClass.getName());
        } else {
            if(!linkDetails.contains("|")) linkDetails = "-|" + linkDetails;
            cols = StringUtility.trim(linkDetails.split("\\|"));
            link.name = cols[0].replace('_', '|');
            try {
                slash = cols[1].indexOf('/');
                if(slash >= 0) {
                    String tail = cols[1].substring(slash).toLowerCase();
                    cols[1] = cols[1].substring(0, slash).trim();
                    link.any = tail.contains("/any");
                    if(link.any) {
                        tail = tail.replace("/any", "").trim();
                    }
                    link.readOnly = tail.contains("/readonly");
                    if(link.readOnly) {
                        tail = tail.replace("/readonly", "").trim();
                    }
                    if(!tail.isEmpty()) {
                        link.type = Integer.parseInt(tail.substring(1));
                    }
                }
                if(cols[1].isEmpty()) {
                    link.setLinkClass(masterClass.getName());
                } else {
                    link.setLinkClass(cols[1]);
                }
            } catch(Throwable e) {
                ApplicationServer.log(e);
                throw new SOClassError(masterClass, "Invalid link definition '" + linkDetails + "'");
            }
        }
        link.browseColumns = null;
        // col[4] was style (numeric). No more used.
        if(cols != null && cols.length > 3 && !cols[3].isEmpty()) {
            link.browseColumns = StringList.create(cols[3]);
        }
        if(cols != null && cols.length > 2 && !cols[2].isEmpty()) {
            link.orderBy = cols[2];
        }
        return link;
    }

    /**
     * Creates a list of {@code Link} objects associated with the specified master class.
     *
     * @param masterClass the class of the master object with which the links are associated;
     *                    must be a subclass of {@code StoredObject}
     * @return a {@code List} of {@code Link} objects associated with the specified master class
     */
    public static List<Link<?>> createList(Class<? extends StoredObject> masterClass) {
        return createList(masterClass, null);
    }

    /**
     * Creates a list of {@code Link} objects associated with the specified master class
     * and additional link details.
     *
     * @param masterClass the class of the master object with which the links are associated;
     *                    must be a subclass of {@code StoredObject}.
     * @param extraLinks  additional link details to be concatenated with the default links
     *                    of the specified master class.
     * @return a {@code List} of {@code Link} objects created based on the specified master
     *         class and additional link details.
     */
    public static List<Link<?>> createList(Class<? extends StoredObject> masterClass, StringList extraLinks) {
        StringList links = ClassAttribute.get(masterClass).links().concat(extraLinks);
        ArrayList<Link<?>> list = new ArrayList<>();
        for(String linkDetails: links) {
            list.add(create(masterClass, linkDetails));
        }
        return list;
    }

    /**
     * Retrieves the class type of the link associated with this object.
     *
     * @return the {@code Class} representing the type of the link
     * @deprecated Use {@link #getLinkClass()} instead.
     */
    public Class<L> getObjectClass() {
        return linkClass;
    }

    /**
     * Retrieves the class type of the link associated with this object.
     *
     * @return the {@code Class} representing the type of the link
     */
    public Class<L> getLinkClass() {
        return linkClass;
    }

    /**
     * Retrieves the class of the master object associated with this link.
     *
     * @return the {@code Class} representing the master object type, which is a subclass of {@code StoredObject}.
     */
    public Class<? extends StoredObject> getMasterClass() {
        return masterClass;
    }

    /**
     * Sets the class type of the link associated with this object.
     *
     * @param linkClass the {@code Class} object representing the type of the link;
     *                  must be compatible with the generic type parameter {@code L}
     * @deprecated Use {@link #setLinkClass(Class)} instead.
     */
    public void setObjectClass(Class<L> linkClass) {
        this.linkClass = linkClass;
    }

    /**
     * Sets the class type of the link associated with this object.
     *
     * @param linkClass the {@code Class} object representing the type of the link;
     *                  must be compatible with the generic type parameter {@code L}
     */
    public void setLinkClass(Class<L> linkClass) {
        this.linkClass = linkClass;
    }

    /**
     * Sets the class type of the link associated with this object using the class name.
     * The class name will be resolved to determine the corresponding class type, which
     * must be compatible with the generic type parameter {@code L}.
     *
     * @param className the fully qualified name of the class to be set as the link class;
     *                  must correspond to a valid class type
     * @throws SORuntimeException if any unexpected error occurs during class resolution
     * @deprecated Use {@link #setLinkClass(String)} instead.
     */
    public void setObjectClass(String className) {
        setLinkClass(className);
    }

    /**
     * Sets the class type of the link associated with this object using the class name.
     * The class name will be resolved to determine the corresponding class type, which
     * must be compatible with the generic type parameter {@code L}.
     *
     * @param className the fully qualified name of the class to be set as the link class;
     *                  must correspond to a valid class type
     * @throws SORuntimeException if any unexpected error occurs during class resolution
     */
    @SuppressWarnings("unchecked")
    public void setLinkClass(String className) {
        linkClass = (Class<L>)StoredObjectUtility.getObjectClass(className);
        if(linkClass == null) {
            throw new SORuntimeException("Invalid link class - " + className);
        }
    }

    /**
     * Retrieves the name associated with this link.
     *
     * @return the name of this link as a {@code String}
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name associated with this link. The given name will be modified
     * by replacing any underscore ('_') characters with a pipe ('|') character.
     *
     * @param name the name to be set; underscores in the provided string
     *             will be replaced with pipe characters
     */
    public void setName(String name) {
        this.name = name.replace('_', '|');
    }

    /**
     * Sets the browser columns for this link.
     * If the provided string is empty or null, the browser columns will be set to null.
     * Otherwise, the string will be processed and stored as a list of columns.
     *
     * @param columns a comma-separated string representing the browser columns to be set;
     *                if the string is null or consists entirely of whitespace, the browser columns will be cleared
     */
    public void setBrowserColumns(String columns) {
        if(StringUtility.isWhite(columns)) {
            browseColumns = null;
        } else {
            setBrowserColumns(StringList.create(columns));
        }
    }

    /**
     * Sets the browser columns for this link using a {@code StringList} of column names.
     * The provided list determines which columns will be displayed in the browser.
     *
     * @param columns a {@code StringList} containing the names of the browser columns to be set;
     *                if the list is null, the browser columns will be cleared.
     */
    public void setBrowserColumns(StringList columns) {
        browseColumns = columns;
    }

    /**
     * Retrieves the list of columns to be displayed in a browser for this link.
     * If the browser columns have not been explicitly set but the link class is defined,
     * the columns are determined based on the attributes of the link class.
     *
     * @return a {@code StringList} containing the names of the browser columns, or {@code null}
     *         if no browser columns are defined and the link class is not specified.
     */
    public StringList getBrowseColumns() {
        if(browseColumns == null && linkClass != null) {
            browseColumns = ClassAttribute.get(linkClass).browseColumns();
        }
        return browseColumns;
    }

    /**
     * Sets the type for this link.
     *
     * @param type the integer value representing the type to be set
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * Retrieves the link type.
     *
     * @return the link type as an integer
     */
    public int getType() {
        return type;
    }

    /**
     * Sets the order by clause for this link. This determines the order
     * in which the linked objects will be retrieved or displayed.
     *
     * @param orderBy the order by clause to be set; should be a valid string
     *                representing the desired sort order, such as a column name
     *                or a comma-separated list of column names
     */
    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    /**
     * Retrieves the order by clause for this link. If the order by clause has not been explicitly
     * set but the link class is defined, the order by clause is determined based on
     * the attributes of the link class.
     *
     * @return the order by clause as a {@code String}, or {@code null} if no order by clause
     *         is defined and the link class is not specified.
     */
    public String getOrderBy() {
        if(orderBy == null && linkClass != null) {
            orderBy = ClassAttribute.get(linkClass).browseOrder();
        }
        return orderBy;
    }

    /**
     * Sets the condition associated with this link. This condition determines
     * additional filtering or constraints for the linked objects.
     *
     * @param condition the condition to be set; should be a valid string
     *                  representing the filtering criteria or constraints for the link.
     */
    public void setCondition(String condition) {
        this.condition = condition;
    }

    /**
     * Retrieves the condition associated with this link.
     * The condition specifies filtering or constraint criteria used
     * for determining the linked objects.
     *
     * @return the condition as a {@code String}, or {@code null} if no condition has been set.
     */
    public String getCondition() {
        return condition;
    }

    /**
     * Sets the predicate used to determine whether a load operation should be
     * performed for a given object of type {@code L}. The provided predicate is
     * applied during the load process to filter or control the loading mechanism.
     * If the provided predicate is {@code null}, a default predicate that always
     * returns {@code true} will be assigned.
     *
     * @param loadPredicate the predicate to evaluate load conditions for objects
     *                      of type {@code L}, or {@code null} to use a default
     *                      predicate that always returns {@code true}.
     */
    public void setLoadPredicate(Predicate<L> loadPredicate) {
        if(loadPredicate == null) {
            loadPredicate = o -> true;
        }
        this.loadPredicate = loadPredicate;
    }

    /**
     * Retrieves the predicate used to evaluate load conditions.
     *
     * @return a Predicate representing the load evaluation logic.
     */
    public Predicate<L> getLoadPredicate() {
        return loadPredicate;
    }

    /**
     * Set the "any" flag to specify whether any sun-classes of the link class should be included.
     */
    public void setAny() {
        any = true;
    }

    /**
     * Checks if the 'any' flag is true. This flag indicates that any sun-classes of the link class should be included.
     *
     * @return true if the 'any' flag is true, false otherwise.
     */
    public boolean isAny() {
        return any;
    }

    /**
     * Sets the read-only status of the link.
     *
     * @param readOnly a boolean value where {@code true} indicates that the link
     *                 should be set to a read-only state, and {@code false} indicates
     *                 that it should be writable.
     */
    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    /**
     * Checks if the object is in a read-only state.
     *
     * @return true if the object is read-only, false otherwise.
     */
    public boolean isReadOnly() {
        return readOnly;
    }

    /**
     * Determines if the link class is a detail of the specified master class.
     *
     * @return true if the link class is a detail of the master class, false otherwise
     */
    public boolean isDetail() {
        return isDetailOf(masterClass);
    }

    /**
     * Determines whether the current class is a detail of the specified master class.
     *
     * @param masterClass the class to check if it is the master class for the detail
     * @return true if the current class is a detail of the specified master class, false otherwise
     */
    public boolean isDetailOf(Class<? extends StoredObject> masterClass) {
        if(!Detail.class.isAssignableFrom(getLinkClass())) {
            return false;
        }
        try {
            return ((Detail) getLinkClass().getDeclaredConstructor().newInstance()).isDetailOf(masterClass);
        } catch(Exception ignored) {
        }
        return false;
    }

    /**
     * Retrieves an iterator over a collection of objects linked to the specified master ID.
     * This method uses the `links` method internally.
     *
     * @param master the ID of the master element whose linked objects are to be retrieved.
     * @return an ObjectIterator over the linked objects.
     */
    public ObjectIterator<L> list(Id master) {
        return links(master,null);
    }

    /**
     * Creates an iterator for a list of objects linked to the specified master object.
     *
     * @param master the master object whose linked objects are to be iterated.
     *               If the ID of the master object is null, an empty iterator is returned.
     * @return an iterator over the objects linked to the specified master object,
     *         or an empty iterator if the master object's ID is null.
     */
    public ObjectIterator<L> list(StoredObject master) {
        if(Id.isNull(master)) {
            return ObjectIterator.create();
        }
        Transaction t = master.getTransaction();
        if(t != null && !t.isActive()) {
            t = null;
        }
        return links(master.getId(), t).filter(loadPredicate);
    }

    private ObjectIterator<L> links(Id master, Transaction t) {
        return master.listLinks(t, type, linkClass, getCondition(), getOrderBy(), any)
                .filter(loadPredicate);
    }

    /**
     * Constructs and executes a query to retrieve linked objects from the specified StoredObject.
     *
     * @param master the StoredObject instance from which the links are queried
     * @return a Query object containing the results of the link query
     */
    public Query query(StoredObject master) {
        Transaction t = master.getTransaction();
        if(t != null && !t.isActive()) {
            t = null;
        }
        return master.queryLinks(t, type, linkClass, getBrowseColumns().toString(), getCondition(),
                getOrderBy(), any);
    }

    @Override
    public boolean equals(Object another) {
        if(!(another instanceof Link<?> a)) {
            return false;
        }
        return linkClass == a.linkClass && type == a.type;
    }
}
