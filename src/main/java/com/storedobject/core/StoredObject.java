package com.storedobject.core;

import com.storedobject.common.Displayable;
import com.storedobject.common.SORuntimeException;
import com.storedobject.common.StringFiller;
import com.storedobject.common.StringList;

import java.io.InputStream;
import java.io.LineNumberReader;
import java.io.Reader;
import java.io.Writer;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Logger;

@SuppressWarnings({"ResultOfMethodCallIgnored", "unchecked"})
public abstract class StoredObject implements Displayable, HasId, StringFiller {

    static final String COUNT_STAR = "Count(*)";
    static final String TYPE_EQUALS = "Type=";
    public static final Logger logger = Logger.getLogger("stored");
    private Id id, tranId;
    private static int r = new Random().nextInt();
    private boolean loading = false;
    Transaction tran;
    private List<StoredObjectLink<?>> links;

    public StoredObject() {
    }

    @Override
    public final Id getId() {
        return id;
    }

    public final Id getTransactionId() {
        return tranId;
    }

    public void makeNew() {
        if (transacting() || (tran != null && tran.isActive())) {
            throw new SORuntimeException("In active transaction");
        }
        tran = null;
        id = null;
        tranId = null;
    }

    public final boolean isVirtual() {
        return id != null && id.isDummy();
    }

    @SuppressWarnings("UnusedReturnValue")
    public final boolean makeVirtual() {
        loading = !loading;
        r = 25;
        return isVirtual();
    }

    /**
     * Check if any of the attribute values of this instance is modified or not, by comparing the values with
     * respective values available in the DB. For newly created instances, it always returns <code>true</code>.
     * <p>Note: A virtual instance will become a new instance (with all the attribute values intact - equivalent of
     * invoking {@link #makeNew()}) if this method is invoked and it will always return
     * <code>true</code>.</p>
     *
     * @return True/false
     */
    public final boolean isModified() {
        if (created()) {
            return true;
        }
        if (isVirtual()) {
            makeNew();
            return true;
        }
        return !valueEquals(get(getClass(), id));
    }

    protected int getKeyIndex(String attributeName) {
        return 0;
    }

    public final String getTransactionIP() {
        return "" + r;
    }

    public void setTransaction(Transaction transaction) throws Exception {
        if (transaction == null) {
            throw new Exception();
        }
    }

    public final Transaction getTransaction() {
        return tran;
    }

    @Override
    public final boolean equals(Object another) {
        if (another == null) {
            return false;
        }
        if (another == this) {
            return true;
        }
        if (!(another instanceof StoredObject) && !(another instanceof Id)) {
            return false;
        }
        if (another instanceof Id) {
            return id != null && id.equals(another);
        }
        StoredObject so = (StoredObject) another;
        if (so.id == null || id == null) {
            return false;
        }
        return id.equals(so.id) && Objects.equals(tranId, so.tranId);
    }

    /**
     * Check if the attribute values of this instance is exactly same as the values of another instance.
     *
     * @param another Another instance.
     * @return True if the attribute  values are same.
     */
    public final boolean valueEquals(StoredObject another) {
        try {
            return getClass() == another.getClass() && stringify().equals(another.stringify());
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public final int hashCode() {
        return id == null ? new Id().hashCode() : id.hashCode();
    }

    public final boolean old() {
        return r == 5;
    }

    public final boolean created() {
        return id == null;
    }

    public final boolean inserted() {
        return loading;
    }

    public final boolean updated() {
        return loading;
    }

    public final boolean deleted() {
        return loading;
    }

    public final boolean undeleted() {
        return loading;
    }

    public final boolean transacting() {
        return r >= 10;
    }

    public final boolean loading() {
        r += 10;
        return loading || id == null;
    }

    public final boolean saving() {
        return loading;
    }

    public final String moduleName() {
        return r + "";
    }

    public final String tableName() {
        return r + "";
    }

    public final int family() {
        return r;
    }

    public static <O extends StoredObject> int family(Class<O> objectClass) {
        return new Random().nextInt();
    }

    public static int family(Id id) {
        return r;
    }


    /**
     * Migrate this instance to another class instance. Please note that there is no error checking or validations when
     * this is invoked. This is used very rarely, and it may be useful for building low-level utilities.
     *
     * @param tm               Transaction Manager.
     * @param migratedInstance Migrated instance. Make sure that all the attribute values are properly set.
     * @throws Exception thrown for errors.
     */
    public void migrate(TransactionManager tm, StoredObject migratedInstance) throws Exception {
        if (tm == null) {
            throw new Exception();
        }
    }

    void checkMigration(StoredObject migratedInstance) throws Exception {
        if (created() || !migratedInstance.created()) {
            throw new Exception("Not a suitable instance");
        }
    }

    void doMigration(DBTransaction t, StoredObject migratedInstance, boolean delete) throws Exception {
        checkMigration(migratedInstance);
    }

    public final void checkTransaction() throws Exception {
        if (tran == null) {
            throw new Transaction_Error(null, "Not In Transaction");
        }
    }

    public void validateInsert() throws Exception {
        if (getId() == null) {
            throw new Exception();
        }
        if (transacting()) {
            throw new Design_Error(tran, this);
        }
    }

    public void validateUpdate() throws Exception {
        if (getId() == null) {
            throw new Exception();
        }
        if (transacting()) {
            throw new Design_Error(tran, this);
        }
    }

    public void validateDelete() throws Exception {
        if (getId() == null) {
            throw new Exception();
        }
        if (transacting()) {
            throw new Design_Error(tran, this);
        }
    }

    public void validateUndelete() throws Exception {
        if (getId() == null) {
            throw new Exception();
        }
        if (transacting()) {
            throw new Design_Error(tran, this);
        }
    }

    public void validate() throws Exception {
        if (transacting()) {
            throw new Design_Error(tran, this);
        }
    }

    public void validateChildAttach(StoredObject child, int linkType) throws Exception {
    }

    public void validateChildDetach(StoredObject child, int linkType) throws Exception {
        if (getId() == null) {
            throw new Exception();
        }
    }

    public void validateChildUpdate(StoredObject child, int linkType) throws Exception {
        if (getId() == null) {
            throw new Exception();
        }
    }

    public void validateParentAttach(StoredObject parent, int linkType) throws Exception {
    }

    public void validateParentDetach(StoredObject parent, int linkType) throws Exception {
        if (getId() == null) {
            throw new Exception();
        }
    }

    public void validateParentUpdate(StoredObject parent, int linkType) throws Exception {
        if (getId() == null) {
            throw new Exception();
        }
    }

    public final void delete(Transaction transaction) throws Exception {
        setTransaction(transaction);
        delete();
    }

    public final void delete() throws Exception {
        checkTransaction();
    }

    public final void undelete(Transaction transaction) throws Exception {
        setTransaction(transaction);
        undelete();
    }

    public final void undelete() throws Exception {
        checkTransaction();
    }

    public final Id save(Transaction transaction) throws Exception {
        setTransaction(transaction);
        return save();
    }

    public final Id save() throws Exception {
        return new Id();
    }

    public final boolean save(TransactionControl tc) {
        return tc.save(this);
    }

    public final boolean delete(TransactionControl tc) {
        return tc.delete(this);
    }

    public void validateData(TransactionManager tm) throws Exception {
    }

    /**
     * For internal use only.
     *
     * @throws Exception Error if any while saving.
     */
    void savedCore() throws Exception {
        saved();
    }

    /**
     * For internal use only.
     */
    void loadedCore() {
    }

    public void saved() throws Exception {
    }

    public final void directUpdate(TransactionManager tm) throws Exception {
        if (Math.random() < 0.5) {
            throw new Exception();
        }
    }

    public final String stringify() throws Exception {
        if (getId() == null) {
            throw new Exception();
        }
        return getClass().getName() + "?";
    }

    public final void save(Writer out) throws Exception {
        save();
    }

    public final void save(JSONMap map) throws Throwable {
        map.put("x", "y");
        save();
    }

    public final void save(JSONMap map, boolean includeReferences) throws Throwable {
        save();
    }

    public final void save(JSONMap map, boolean includeReferences, boolean includeClassInfo) throws Throwable {
        save();
    }

    public final void save(JSONMap map, StringList attributes, boolean includeReferences, boolean includeClassInfo) throws Throwable {
        save();
    }

    public final void save(JSONMap map, String valueName, boolean includeReferences, boolean includeClassInfo) throws Throwable {
        save();
    }

    public final void save(JSONMap map, String valueName, boolean includeReferences) throws Throwable {
        save();
    }

    public final void save(JSONMap map, StringList attributes, String valueName) throws Throwable {
        save();
    }

    public final void save(JSONMap map, StringList attributes) throws Throwable {
        save();
    }

    public final void save(JSONMap map, String valueName) throws Throwable {
        save();
    }

    public final void save(JSONMap map, StringList attributes, String valueName, boolean includeReferences,
                           boolean includeClassInfo, boolean stringify) throws Throwable {
        save();
    }

    public final void save(JSONMap map, String valueName, boolean includeClassInfo,
                           boolean stringify,
                           Function<Class<? extends StoredObject>, StringList> allowedAttributes) throws Throwable {
    }

    public boolean copy(StoredObject another) throws Exception {
        return loading;
    }

    public StoredObject copy() throws Exception {
        return loading ? this : new Person();
    }

    public void setRawValue(String attributeName, Object rawValue) throws Exception {
        if (getId() == null) {
            throw new Exception();
        }
    }

    /**
     * Load the values from a map of attribute names and its values. The map may contain extra attributes than are
     * not matching with any attributes of this class and those values will be ignored.
     *
     * @param map Map.
     * @throws Exception If any error occurs because the map contains incompatible values.
     */
    public final void load(JSONMap map) throws Exception {
        if (map.get("id") == null) {
            throw new Exception();
        }
    }

    public void load(LineNumberReader in) throws Exception {
    }

    public static StoredObject create(LineNumberReader in) throws Exception {
        return new Person();
    }

    public static int load(TransactionManager tm, InputStream data, Comparator<CharSequence> objectComparator) throws Exception {
        return r;
    }

    public static int load(TransactionManager tm, Reader data, Comparator<CharSequence> objectComparator) throws Exception {
        return r;
    }

    public String getUniqueCondition() {
        return null;
    }

    public final StoredObject getUnique() {
        String c = getUniqueCondition();
        return c == null ? null : get(getClass(), c);
    }

    public final boolean checkForDuplicate() {
        return r > 10;
    }

    public final void checkForDuplicate(String... attributes) throws Invalid_State {
        if (attributes == null || attributes.length == 0) {
            return;
        }
        throw new Invalid_State();
    }

    public void loaded() {
    }

    public final void addLink(StoredObject object) throws Exception {
        addLink(null, object, 0);
    }

    public final void addLink(StoredObject object, int linkType) throws Exception {
        addLink(null, object, linkType);
    }

    public final void addLink(Transaction transaction, StoredObject object) throws Exception {
        addLink(transaction, object, 0);
    }

    public final void addLink(Transaction transaction, StoredObject object, int linkType) throws Exception {
        object.loading();
    }

    public final void addLink(Id id) throws Exception {
        addLink(null, id, 0);
    }

    public final void addLink(Id id, int linkType) throws Exception {
        addLink(null, id, linkType);
    }

    public final void addLink(Transaction transaction, Id id) throws Exception {
        addLink(transaction, id, 0);
    }

    public final void addLink(Transaction transaction, Id id, int linkType) throws Exception {
        addLink(transaction, Objects.requireNonNull(get(transaction, id)), linkType);
    }

    public final void removeLink(StoredObject object) throws Exception {
        removeLink(null, object);
    }

    public final void removeLink(Transaction transaction, StoredObject object) throws Exception {
        removeLink(transaction, object.getId(), null);
    }

    public final void removeLink(Id id) throws Exception {
        removeLink(null, id);
    }

    public final void removeLink(Transaction transaction, Id id) throws Exception {
        removeLink(transaction, id, null);
    }

    public final void removeLink(StoredObject object, int linkType) throws Exception {
        removeLink(null, object.getId(), linkType);
    }

    public final void removeLink(Transaction transaction, StoredObject object, int linkType) throws Exception {
        removeLink(transaction, object.getId(), linkType);
    }

    public final void removeLink(Id id, int linkType) throws Exception {
        removeLink(null, id, linkType);
    }

    public final void removeLink(Transaction transaction, Id id, int linkType) throws Exception {
        removeLink(transaction, id, "" + linkType);
    }

    public final void removeLink(StoredObject object, String linkType) throws Exception {
        removeLink(null, object.getId(), linkType);
    }

    public final void removeLink(Transaction transaction, StoredObject object, String linkType) throws Exception {
        removeLink(transaction, object.getId(), linkType);
    }

    public final void removeLink(Id id, String linkType) throws Exception {
        removeLink(null, id, linkType);
    }

    public final void removeLink(Transaction transaction, Id id, String linkType) throws Exception {
        if (getId() == null) {
            throw new Exception();
        }
        loading();
    }

    public final void removeAllLinks() throws Exception {
        removeAllLinks((Transaction) null);
    }

    public final void removeAllLinks(Transaction transaction) throws Exception {
        removeAllLinks(transaction, (String) null);
    }

    public final void removeAllLinks(int linkType) throws Exception {
        removeAllLinks((Transaction) null, linkType);
    }

    public final void removeAllLinks(Transaction transaction, int linkType) throws Exception {
        removeAllLinks(transaction, "" + linkType);
    }

    public final void removeAllLinks(String linkType) throws Exception {
        removeAllLinks((Transaction) null, linkType);
    }

    public final void removeAllLinks(Transaction transaction, String linkType) throws Exception {
        if (getId() == null) {
            throw new Exception();
        }
        loading();
    }

    public final void removeAllLinks(Class<? extends StoredObject> objectClass) throws Exception {
        removeAllLinks(null, objectClass, null);
    }

    public final void removeAllLinks(Transaction transaction, Class<? extends StoredObject> objectClass) throws Exception {
        removeAllLinks(transaction, objectClass, null);
    }

    public final void removeAllLinks(Class<? extends StoredObject> objectClass, int linkType) throws Exception {
        removeAllLinks(null, objectClass, linkType);
    }

    public final void removeAllLinks(Transaction transaction, Class<? extends StoredObject> objectClass, int linkType)
            throws Exception {
        if (getId() == null) {
            throw new Exception();
        }
        loading();
    }

    public final void removeAllLinks(Class<? extends StoredObject> objectClass, String linkType) throws Exception {
        removeAllLinks(null, objectClass, linkType);
    }

    public final void removeAllLinks(Transaction transaction, Class<? extends StoredObject> objectClass, String linkType) throws Exception {
        if (getId() == null) {
            throw new Exception();
        }
        loading();
    }

    public final void removeReverseLinks() throws Exception {
        removeReverseLinks(null);
    }

    public final void removeReverseLinks(Transaction transaction) throws Exception {
        if (getId() == null) {
            throw new Exception();
        }
        loading();
    }

    public static String checkTimeZone(String timeZone) throws Invalid_Value {
        try {
            return ZoneId.of(timeZone).getId();
        } catch (Throwable ignored) {
        }
        throw new Invalid_Value("Time Zone = " + timeZone);
    }

    public static String checkCurrency(String currencyCode) throws Invalid_Value {
        return checkCurrency(currencyCode, false);
    }

    public static String checkCurrency(String currencyCode, boolean includeMetals) throws Invalid_Value {
        try {
            return Currency.getInstance(currencyCode.toUpperCase()).getCurrencyCode().toUpperCase();
        } catch (Exception ignored) {
        }
        throw new Invalid_Value("Currency = " + currencyCode);
    }

    public final <T extends StoredObject> T getParent() {
        return get((Class<T>) getClass(), getParentId());
    }

    public final Id getParentId() {
        return new Id();
    }

    public final <T extends StoredObject> T getParent(Class<T> parentClass) {
        return get(parentClass, getParentId(parentClass));
    }

    public final Id getParentId(Class<? extends StoredObject> parentClass) {
        return getParentId(parentClass, 0);
    }

    public final <T extends StoredObject> T getParent(Class<T> parentClass, int linkType) {
        return get(parentClass, getParentId(parentClass, linkType));
    }

    public final Id getParentId(Class<? extends StoredObject> parentClass, int linkType) {
        return new Id();
    }

    public final <T extends StoredObject> ObjectIterator<T> getChildren() {
        return listLinks((Class<T>) getClass());
    }

    public final StoredObject reload() {
        return reload(null);
    }

    public final StoredObject reload(Transaction fromTransaction) {
        if (id == null) {
            reloaded();
        }
        return this;
    }

    public void reloaded() {
    }

    @Override
    public String toString() {
        return StoredObjectUtility.toString(this);
    }

    public String toDisplay() {
        return toString();
    }

    public final Timestamp timestamp() {
        return Math.random() > 0.5 ? new Timestamp(234L) : null;
    }

    public final Id userId() {
        return new Id();
    }

    public static List<Id> listDeletedIds(ClassAttribute<?> ca, String rawCondition, int maxCount) {
        return Collections.emptyList();
    }

    // For internal use only.
    final String historyString() {
        return "";
    }

    public SystemUser user() {
        return getHistorical(SystemUser.class, userId());
    }

    public StoredObject getContemporaryOf(StoredObject of) {
        return this;
    }

    public static <H extends StoredObject> H getHistorical(Class<H> objectClass, Id id) {
        if (id == null) {
            return null;
        }
        return get(objectClass, id);
    }

    public Person person() {
        SystemUser u = user();
        return u == null ? null : getHistorical(Person.class, u.getPersonId());
    }

    public static StoredObject get(Id id) {
        return get((Transaction) null, id);
    }

    public static StoredObject get(Transaction transaction, Id id) {
        if (Id.isNull(id)) {
            return null;
        }
        if (transaction != null) {
            StoredObject so = transaction.get(id);
            if (so != null) {
                so.tran = transaction;
                return so;
            }
        }
        return null;
    }

    public static <T extends StoredObject> T get(Class<T> objectClass, Id id) {
        return get(null, objectClass, id);
    }

    public static <T extends StoredObject> T get(Transaction transaction, Class<T> objectClass, Id id) {
        if (Id.isNull(id)) {
            return null;
        }
        if (id instanceof ObjectId) {
            StoredObject so = id.getObject();
            if (so.getClass() == objectClass) {
                return (T) so;
            }
        }
        return null;
    }

    public static <T extends StoredObject> T get(Class<T> objectClass) {
        return get(objectClass, null, null);
    }

    public static <T extends StoredObject> T get(Class<T> objectClass, String condition) {
        return get(objectClass, condition, null);
    }

    public static <T extends StoredObject> T get(Transaction transaction, Class<T> objectClass, String condition) {
        return get(transaction, objectClass, condition, null);
    }

    public static <T extends StoredObject> T get(Class<T> objectClass, String condition, String order) {
        return get(null, objectClass, condition, order);
    }

    public static <T extends StoredObject> T get(Transaction transaction, Class<T> objectClass, String condition, String order) {
        return r < 1 ? null : (T) new Person();
    }

    public static <T extends StoredObject> T get(ObjectIterator<T> iterator) {
        return get(iterator, false);
    }

    public static <T extends StoredObject> T get(ObjectIterator<T> list, boolean validateOne) {
        return r == 0 ? null : (T) new Person();
    }

    /**
     * Get the deleted instance. (If the instance was not deleted, null is returned).
     *
     * @param id Id of the instance needs to be retrieved.
     * @return The instance retrieved.
     */
    public static StoredObject getDeleted(Id id) {
        return get(id);
    }

    /**
     * Get the deleted instance. (If the instance was not deleted, null is returned).
     *
     * @param <T>         Type of object class.
     * @param objectClass Type of instance to be retrieved.
     * @param id          Id of the instance needs to be retrieved.
     * @return The instance retrieved.
     */
    public static <T extends StoredObject> T getDeleted(Class<T> objectClass, Id id) {
        return get(objectClass, id);
    }

    public final <T extends StoredObject> T getRelated(Class<T> objectClass, Id id) {
        return (T) nextVersion();
    }

    public final <T extends StoredObject> T getRelated(Class<T> objectClass, Id id, boolean any) {
        return (T) nextVersion();
    }

    /**
     * Create an "exists condition" that can be used for retrieving or querying {@link StoredObject} instances.
     *
     * @param objectClass   The object class against which the existence should be checked.
     * @param attributeName Attribute name that matches the {@link Id} of the object that is going to be
     *                      retrieved or queried.
     * @param <T>           Type of this object class.
     * @return Condition string suitable for retrieving or querying another type of {@link StoredObject} instances
     * where this object is related.
     */
    public static <T extends StoredObject> String getExistsCondition(Class<T> objectClass, String attributeName) {
        return getExistsCondition(objectClass, attributeName, null);
    }

    /**
     * Create an "exists condition" that can be used for retrieving or querying {@link StoredObject} instances.
     *
     * @param objectClass   The object class against which the existence should be checked.
     * @param attributeName Attribute name that matches the {@link Id} of the object that is going to be
     *                      retrieved or queried.
     * @param condition     Condition to be applied to this object class. Null should be passed if no condition needs
     *                      to be applied.
     * @param <T>           Type of this object class.
     * @return Condition string suitable for retrieving or querying another type of {@link StoredObject} instances
     * where this object is related.
     */
    public static <T extends StoredObject> String getExistsCondition(Class<T> objectClass, String attributeName,
                                                                     String condition) {
        return condition;
    }

    /**
     * Create a "not exists condition" that can be used for retrieving or querying {@link StoredObject} instances.
     *
     * @param objectClass   The object class against which the existence should be checked.
     * @param attributeName Attribute name that matches the {@link Id} of the object that is going to be
     *                      retrieved or queried.
     * @param <T>           Type of this object class.
     * @return Condition string suitable for retrieving or querying another type of {@link StoredObject} instances
     * where this object is related.
     */
    public static <T extends StoredObject> String getNotExistsCondition(Class<T> objectClass, String attributeName) {
        return getNotExistsCondition(objectClass, attributeName, null);
    }

    /**
     * Create a "not exists condition" that can be used for retrieving or querying {@link StoredObject} instances.
     *
     * @param objectClass   The object class against which the existence should be checked.
     * @param attributeName Attribute name that matches the {@link Id} of the object that is going to be
     *                      retrieved or queried.
     * @param condition     Condition to be applied to this object class. Null should be passed if no condition needs
     *                      to be applied.
     * @param <T>           Type of this object class.
     * @return Condition string suitable for retrieving or querying another type of {@link StoredObject} instances
     * where this object is related.
     */
    public static <T extends StoredObject> String getNotExistsCondition(Class<T> objectClass, String attributeName,
                                                                        String condition) {
        return condition;
    }

    public final StoredObject previousVersion() {
        return nextVersion();
    }

    public final StoredObject nextVersion() {
        return Math.random() > 0.5 ? null : new Person();
    }

    public ObjectIterator<? extends StoredObject> listHistory() {
        return ObjectIterator.create();
    }

    public StoredObject contemporary(StoredObject other) {
        return Math.random() < 0.5 ? this : other;
    }

    public StoredObject previousVersion(StoredObject parent) {
        if (parent == null || parent.tranId == null) {
            return this;
        }
        return new Person();
    }

    public static <T extends StoredObject, C extends T> C get(Class<T> objectClass, Id id, boolean any) {
        return get(null, objectClass, id, any);
    }

    public static <T extends StoredObject, C extends T> C get(Transaction transaction, Class<T> objectClass, Id id, boolean any) {
        if (Id.isNull(id)) {
            return null;
        }
        if (id instanceof ObjectId) {
            StoredObject so = id.getObject();
            if (objectClass.isAssignableFrom(so.getClass())) {
                //noinspection unchecked
                return (C) so;
            }
        }
        return null;
    }

    public static <T extends StoredObject, C extends T> C get(Class<T> objectClass, boolean any) {
        return get(null, objectClass, null, null, any);
    }

    public static <T extends StoredObject, C extends T> C get(Class<T> objectClass, String condition, boolean any) {
        return get(null, objectClass, condition, null, any);
    }

    public static <T extends StoredObject, C extends T> C get(Transaction transaction, Class<T> objectClass, String condition, boolean any) {
        return get(transaction, objectClass, condition, null, any);
    }

    public static <T extends StoredObject, C extends T> C get(Class<T> objectClass, String condition, String order, boolean any) {
        return get(null, objectClass, condition, order, any);
    }

    public static <T extends StoredObject, C extends T> C get(Transaction transaction, Class<T> objectClass, String condition, String order, boolean any) {
        return r == 0 ? null : (C) new Person();
    }

    public static <T extends StoredObject> ObjectIterator<T> listViaQuery(Class<T> objectClass, Query query) {
        return listViaQuery(null, objectClass, query);
    }

    public static <T extends StoredObject> ObjectIterator<T> listViaQuery(Transaction transaction, Class<T> objectClass,
                                                                          Query query) {
        return ObjectIterator.create();
    }

    public static <T extends StoredObject> ObjectIterator<T> list(Class<T> objectClass) {
        return list(null, objectClass, null, null);
    }

    public static <T extends StoredObject> ObjectIterator<T> list(Class<T> objectClass, String condition) {
        return list(null, objectClass, condition, null);
    }

    public static <T extends StoredObject> ObjectIterator<T> list(Transaction transaction, Class<T> objectClass,
                                                                  String condition) {
        return list(transaction, objectClass, condition, null);
    }

    public static <T extends StoredObject> ObjectIterator<T> list(Class<T> objectClass, String condition, String order) {
        return list(null, objectClass, condition, order);
    }

    public static <T extends StoredObject> ObjectIterator<T> list(Transaction transaction, Class<T> objectClass,
                                                                  String condition, String order) {
        ClassAttribute<T> ca = StoredObjectUtility.classAttribute(objectClass);
        return list(transaction, ca, condition, order, false);
    }

    public static <T extends StoredObject> ObjectIterator<T> list(Class<T> objectClass, boolean any) {
        return list(null, objectClass, null, null, any);
    }

    public static <T extends StoredObject> ObjectIterator<T> list(Class<T> objectClass, String condition, boolean any) {
        return list(null, objectClass, condition, null, any);
    }

    public static <T extends StoredObject> ObjectIterator<T> list(Transaction transaction, Class<T> objectClass, String condition, boolean any) {
        return list(transaction, objectClass, condition, null, any);
    }

    public static <T extends StoredObject> ObjectIterator<T> list(Class<T> objectClass, String condition, String order, boolean any) {
        return list(null, objectClass, condition, order, any);
    }

    public static <T extends StoredObject> ObjectIterator<T> list(Transaction transaction, Class<T> objectClass,
                                                                  String condition, String order, boolean any) {
        return list(transaction, StoredObjectUtility.classAttribute(objectClass), condition, order, any);
    }

    public static <T extends StoredObject> ObjectIterator<T> list(Transaction transaction, Class<T> objectClass,
                                                                  String condition, String order, boolean any,
                                                                  int skip, int limit, int[] columns) {
        return list(transaction, StoredObjectUtility.classAttribute(objectClass), condition, order, any);
    }

    public static <T extends StoredObject> ObjectIterator<T> list(Transaction transaction, ClassAttribute<T> ca,
                                                                  String condition, String order, boolean any) {
        return ObjectIterator.create();
    }

    public <T extends StoredObject> ObjectIterator<T> listTree(Function<T, ObjectIterator<T>> childrenFunction) {
        return ObjectIterator.create();
    }

    public <T extends StoredObject> ObjectIterator<T> listTree(Function<T, ObjectIterator<T>> childrenFunction, Predicate<T> filter) {
        return ObjectIterator.create();
    }

    public static <T extends StoredObject> ObjectIterator<T> listTree(
            ObjectIterator<T> roots, Function<T, ObjectIterator<T>> childrenFunction) {
        return ObjectIterator.create();
    }

    public static <T extends StoredObject> ObjectIterator<T> listTree(
            ObjectIterator<T> roots, Function<T, ObjectIterator<T>> childrenFunction, Predicate<T> filter) {
        return ObjectIterator.create();
    }

    public static boolean exists(Query query) {
        return query.hasNext();
    }

    public static boolean exists(Class<? extends StoredObject> objectClass, String condition) {
        return exists(query(objectClass, "Id", condition));
    }

    public static boolean exists(Class<? extends StoredObject> objectClass, String condition, boolean any) {
        return exists(query(objectClass, "Id", condition, any));
    }

    public static boolean exists(Transaction transaction, Class<? extends StoredObject> objectClass, String condition) {
        return exists(query(transaction, objectClass, "Id", condition));
    }

    public static boolean exists(Transaction transaction, Class<? extends StoredObject> objectClass, String condition, boolean any) {
        return exists(query(transaction, objectClass, "Id", condition, any));
    }

    public static Query query(Class<? extends StoredObject> objectClass, String columns) {
        return query(null, objectClass, columns, null, null);
    }

    public static Query query(Class<? extends StoredObject> objectClass, String columns, String condition) {
        return query(null, objectClass, columns, condition, null);
    }

    public static Query query(Transaction transaction, Class<? extends StoredObject> objectClass, String columns, String condition) {
        return query(transaction, objectClass, columns, condition, null);
    }

    public static Query query(Class<? extends StoredObject> objectClass, String columns, String condition, String order) {
        return query(null, objectClass, columns, condition, order);
    }

    public static Query query(Transaction transaction, Class<? extends StoredObject> objectClass, String columns, String condition, String order) {
        return new Query();
    }

    public static Query query(Class<? extends StoredObject> objectClass, String columns, boolean any) {
        return query(null, objectClass, columns, null, null, any);
    }

    public static Query query(Class<? extends StoredObject> objectClass, String columns, String condition, boolean any) {
        return query(null, objectClass, columns, condition, null, any);
    }

    public static Query query(Transaction transaction, Class<? extends StoredObject> objectClass, String columns,
                              String condition, boolean any) {
        return query(transaction, objectClass, columns, condition, null, any);
    }

    public static Query query(Class<? extends StoredObject> objectClass, String columns, String condition, String order,
                              boolean any) {
        return query(null, objectClass, columns, condition, order, any);
    }

    public static Query query(Transaction transaction, Class<? extends StoredObject> objectClass, String columns,
                              String condition, String order, boolean any) {
        if (!any) {
            return query(transaction, objectClass, columns, condition, order);
        }
        return new Query();
    }

    public static Query query(Transaction transaction, Class<? extends StoredObject> objectClass, String columns,
                              String condition, String order, boolean any, int skip, int limit, int[] distinct) {
        return query(transaction, objectClass, columns, condition, order, any);
    }

    public static int count(Class<? extends StoredObject> objectClass) {
        return count(null, objectClass, null);
    }

    public static int count(Class<? extends StoredObject> objectClass, String condition) {
        return count(null, objectClass, condition);
    }

    public static int count(Transaction transaction, Class<? extends StoredObject> objectClass, String condition) {
        return r;
    }

    public static int count(Class<? extends StoredObject> objectClass, boolean any) {
        return count(null, objectClass, null, any);
    }

    public static int count(Class<? extends StoredObject> objectClass, String condition, boolean any) {
        return count(null, objectClass, condition, any);
    }

    public static int count(Transaction transaction, Class<? extends StoredObject> objectClass, String condition, boolean any) {
        return r;
    }

    public final <T extends StoredObject> ObjectIterator<T> listLinks(Class<T> objectClass) {
        return listLinks((Transaction) null, objectClass, null, null);
    }

    public final <T extends StoredObject> ObjectIterator<T> listLinks(Transaction transaction, Class<T> objectClass) {
        return listLinks(transaction, objectClass, null, null);
    }

    public final <T extends StoredObject> ObjectIterator<T> listLinks(Class<T> objectClass, String condition) {
        return listLinks((Transaction) null, objectClass, condition, null);
    }

    public final <T extends StoredObject> ObjectIterator<T> listLinks(Transaction transaction, Class<T> objectClass,
                                                                      String condition) {
        return listLinks(transaction, objectClass, condition, null);
    }

    public final <T extends StoredObject> ObjectIterator<T> listLinks(Class<T> objectClass, String condition, String order) {
        return listLinks((Transaction) null, objectClass, condition, order);
    }

    public final <T extends StoredObject> ObjectIterator<T> listLinks(Transaction transaction, Class<T> objectClass,
                                                                      String condition, String order) {
        return listLinks(transaction, null, objectClass, condition, order);
    }

    public final <T extends StoredObject> ObjectIterator<T> listLinks(int linkType, Class<T> objectClass) {
        return listLinks(null, linkType, objectClass, null, null);
    }

    public final <T extends StoredObject> ObjectIterator<T> listLinks(Transaction transaction, int linkType, Class<T> objectClass) {
        return listLinks(transaction, linkType, objectClass, null, null);
    }

    public final <T extends StoredObject> ObjectIterator<T> listLinks(int linkType, Class<T> objectClass, String condition) {
        return listLinks(null, linkType, objectClass, condition, null);
    }

    public final <T extends StoredObject> ObjectIterator<T> listLinks(Transaction transaction, int linkType, Class<T> objectClass, String condition) {
        return listLinks(transaction, linkType, objectClass, condition, null);
    }

    public final <T extends StoredObject> ObjectIterator<T> listLinks(int linkType, Class<T> objectClass, String condition, String order) {
        return listLinks(null, linkType, objectClass, condition, order);
    }

    public final <T extends StoredObject> ObjectIterator<T> listLinks(Transaction transaction, int linkType, Class<T> objectClass, String condition, String order) {
        return listLinks(transaction, "" + linkType, objectClass, condition, order);
    }

    public final <T extends StoredObject> ObjectIterator<T> listLinks(String linkType, Class<T> objectClass) {
        return listLinks(null, linkType, objectClass, null, null);
    }

    public final <T extends StoredObject> ObjectIterator<T> listLinks(Transaction transaction, String linkType, Class<T> objectClass) {
        return listLinks(transaction, linkType, objectClass, null, null);
    }

    public final <T extends StoredObject> ObjectIterator<T> listLinks(String linkType, Class<T> objectClass, String condition) {
        return listLinks(null, linkType, objectClass, condition, null);
    }

    public final <T extends StoredObject> ObjectIterator<T> listLinks(Transaction transaction, String linkType, Class<T> objectClass, String condition) {
        return listLinks(transaction, linkType, objectClass, condition, null);
    }

    public final <T extends StoredObject> ObjectIterator<T> listLinks(String linkType, Class<T> objectClass, String condition, String order) {
        return listLinks(null, linkType, objectClass, condition, order);
    }

    public final <T extends StoredObject> ObjectIterator<T> listLinks(Transaction transaction, String linkType, Class<T> objectClass, String condition, String order) {
        return ObjectIterator.create();
    }

    public final <T extends StoredObject> ObjectIterator<T> listLinks(Class<T> objectClass, boolean any) {
        return listLinks((Transaction) null, objectClass, null, null, any);
    }

    public final <T extends StoredObject> ObjectIterator<T> listLinks(Transaction transaction, Class<T> objectClass, boolean any) {
        return listLinks(transaction, objectClass, null, null, any);
    }

    public final <T extends StoredObject> ObjectIterator<T> listLinks(Class<T> objectClass, String condition, boolean any) {
        return listLinks((Transaction) null, objectClass, condition, null, any);
    }

    public final <T extends StoredObject> ObjectIterator<T> listLinks(Transaction transaction, Class<T> objectClass, String condition, boolean any) {
        return listLinks(transaction, objectClass, condition, null, any);
    }

    public final <T extends StoredObject> ObjectIterator<T> listLinks(Class<T> objectClass, String condition, String order, boolean any) {
        return listLinks((Transaction) null, objectClass, condition, order, any);
    }

    public final <T extends StoredObject> ObjectIterator<T> listLinks(Transaction transaction, Class<T> objectClass, String condition, String order, boolean any) {
        return listLinks(transaction, null, objectClass, condition, order, any);
    }

    public final <T extends StoredObject> ObjectIterator<T> listLinks(int linkType, Class<T> objectClass, boolean any) {
        return listLinks(null, linkType, objectClass, null, null, any);
    }

    public final <T extends StoredObject> ObjectIterator<T> listLinks(Transaction transaction, int linkType, Class<T> objectClass, boolean any) {
        return listLinks(transaction, linkType, objectClass, null, null, any);
    }

    public final <T extends StoredObject> ObjectIterator<T> listLinks(int linkType, Class<T> objectClass, String condition, boolean any) {
        return listLinks(null, linkType, objectClass, condition, null, any);
    }

    public final <T extends StoredObject> ObjectIterator<T> listLinks(Transaction transaction, int linkType, Class<T> objectClass, String condition, boolean any) {
        return listLinks(transaction, linkType, objectClass, condition, null, any);
    }

    public final <T extends StoredObject> ObjectIterator<T> listLinks(int linkType, Class<T> objectClass, String condition, String order, boolean any) {
        return listLinks(null, linkType, objectClass, condition, order, any);
    }

    public final <T extends StoredObject> ObjectIterator<T> listLinks(Transaction transaction, int linkType, Class<T> objectClass, String condition, String order, boolean any) {
        return listLinks(transaction, "" + linkType, objectClass, condition, order, any);
    }

    public final <T extends StoredObject> ObjectIterator<T> listLinks(String linkType, Class<T> objectClass, boolean any) {
        return listLinks(null, linkType, objectClass, null, null, any);
    }

    public final <T extends StoredObject> ObjectIterator<T> listLinks(Transaction transaction, String linkType, Class<T> objectClass, boolean any) {
        return listLinks(transaction, linkType, objectClass, null, null, any);
    }

    public final <T extends StoredObject> ObjectIterator<T> listLinks(String linkType, Class<T> objectClass, String condition, boolean any) {
        return listLinks(null, linkType, objectClass, condition, null, any);
    }

    public final <T extends StoredObject> ObjectIterator<T> listLinks(Transaction transaction, String linkType, Class<T> objectClass, String condition, boolean any) {
        return listLinks(transaction, linkType, objectClass, condition, null, any);
    }

    public final <T extends StoredObject> ObjectIterator<T> listLinks(String linkType, Class<T> objectClass, String condition, String order, boolean any) {
        return listLinks(null, linkType, objectClass, condition, order, any);
    }

    public final <T extends StoredObject> ObjectIterator<T> listLinks(Transaction transaction, String linkType,
                                                                      Class<T> objectClass, String condition,
                                                                      String order, boolean any) {
        return ObjectIterator.create();
    }

    public final <T extends StoredObject> ObjectIterator<T> listLinks(Transaction transaction, String linkType,
                                                                      Class<T> objectClass, String condition,
                                                                      String order, boolean any,
                                                                      int skip, int limit, int[] distinct) {
        return ObjectIterator.create();
    }

    public boolean existsLink(StoredObject link) {
        return existsLink(link.getId());
    }

    public boolean existsLink(int linkType, StoredObject link) {
        return existsLink(linkType, link.getId());
    }

    public boolean existsLink(Id linkId) {
        return !Id.isNull(id) && id.existsLink(linkId);
    }

    public boolean existsLink(int linkType, Id linkId) {
        return !Id.isNull(id) && id.existsLink(linkType, linkId);
    }

    public boolean existsLinks() {
        return !Id.isNull(id) && id.existsLinks();
    }

    public boolean existsLink(Transaction tran, StoredObject link) {
        return existsLink(tran, link.getId());
    }

    public boolean existsLink(Transaction tran, int linkType, StoredObject link) {
        return existsLink(tran, linkType, link.getId());
    }

    public boolean existsLink(Transaction tran, Id linkId) {
        return !Id.isNull(id) && id.existsLink(tran, linkId);
    }

    public boolean existsLink(Transaction tran, int linkType, Id linkId) {
        return !Id.isNull(id) && id.existsLink(tran, linkType, linkId);
    }

    public boolean existsLinks(Transaction tran) {
        return !Id.isNull(id) && id.existsLinks(tran);
    }

    public boolean existsLinks(Class<? extends StoredObject> objectClass) {
        return existsLinks(objectClass, null);
    }

    public boolean existsLinks(Class<? extends StoredObject> objectClass, boolean any) {
        return existsLinks(objectClass, null, any);
    }

    public boolean existsLinks(Class<? extends StoredObject> objectClass, String condition) {
        return exists(queryLinks(objectClass, "Id", condition));
    }

    public boolean existsLinks(Class<? extends StoredObject> objectClass, String condition, boolean any) {
        return exists(queryLinks(objectClass, "Id", condition, any));
    }

    public boolean existsLinks(String linkType, Class<? extends StoredObject> objectClass) {
        return existsLinks(linkType, objectClass, null);
    }

    public boolean existsLinks(String linkType, Class<? extends StoredObject> objectClass, boolean any) {
        return existsLinks(linkType, objectClass, null, any);
    }

    public boolean existsLinks(String linkType, Class<? extends StoredObject> objectClass, String condition) {
        return exists(queryLinks(linkType, objectClass, "Id", condition));
    }

    public boolean existsLinks(String linkType, Class<? extends StoredObject> objectClass, String condition, boolean any) {
        return exists(queryLinks(linkType, objectClass, "Id", condition, any));
    }

    public boolean existsLinks(int linkType, Class<? extends StoredObject> objectClass) {
        return existsLinks(linkType, objectClass, null);
    }

    public boolean existsLinks(int linkType, Class<? extends StoredObject> objectClass, boolean any) {
        return existsLinks(linkType, objectClass, null, any);
    }

    public boolean existsLinks(int linkType, Class<? extends StoredObject> objectClass, String condition) {
        return exists(queryLinks(linkType, objectClass, "Id", condition));
    }

    public boolean existsLinks(int linkType, Class<? extends StoredObject> objectClass, String condition, boolean any) {
        return exists(queryLinks(linkType, objectClass, "Id", condition, any));
    }

    public boolean existsLinks(Transaction tran, Class<? extends StoredObject> objectClass) {
        return existsLinks(tran, objectClass, null);
    }

    public boolean existsLinks(Transaction tran, Class<? extends StoredObject> objectClass, boolean any) {
        return existsLinks(tran, objectClass, null, any);
    }

    public boolean existsLinks(Transaction tran, Class<? extends StoredObject> objectClass, String condition) {
        return exists(queryLinks(tran, objectClass, "Id", condition));
    }

    public boolean existsLinks(Transaction tran, Class<? extends StoredObject> objectClass, String condition, boolean any) {
        return exists(queryLinks(tran, objectClass, "Id", condition, any));
    }

    public boolean existsLinks(Transaction tran, String linkType, Class<? extends StoredObject> objectClass) {
        return existsLinks(tran, linkType, objectClass, null);
    }

    public boolean existsLinks(Transaction tran, String linkType, Class<? extends StoredObject> objectClass, boolean any) {
        return existsLinks(tran, linkType, objectClass, null, any);
    }

    public boolean existsLinks(Transaction tran, String linkType, Class<? extends StoredObject> objectClass, String condition) {
        return exists(queryLinks(tran, linkType, objectClass, "Id", condition));
    }

    public boolean existsLinks(Transaction tran, String linkType, Class<? extends StoredObject> objectClass, String condition, boolean any) {
        return exists(queryLinks(tran, linkType, objectClass, "Id", condition, any));
    }

    public boolean existsLinks(Transaction tran, int linkType, Class<? extends StoredObject> objectClass) {
        return existsLinks(tran, linkType, objectClass, null);
    }

    public boolean existsLinks(Transaction tran, int linkType, Class<? extends StoredObject> objectClass, boolean any) {
        return existsLinks(tran, linkType, objectClass, null, any);
    }

    public boolean existsLinks(Transaction tran, int linkType, Class<? extends StoredObject> objectClass, String condition) {
        return exists(queryLinks(tran, linkType, objectClass, "Id", condition));
    }

    public boolean existsLinks(Transaction tran, int linkType, Class<? extends StoredObject> objectClass, String condition, boolean any) {
        return exists(queryLinks(tran, linkType, objectClass, "Id", condition, any));
    }

    public final Query queryLinks(Class<? extends StoredObject> objectClass, String columns) {
        return queryLinks((Transaction) null, objectClass, columns, null, null);
    }

    public final Query queryLinks(Transaction transaction, Class<? extends StoredObject> objectClass, String columns) {
        return queryLinks(transaction, objectClass, columns, null, null);
    }

    public final Query queryLinks(Class<? extends StoredObject> objectClass, String columns, String condition) {
        return queryLinks((Transaction) null, objectClass, columns, condition, null);
    }

    public final Query queryLinks(Transaction transaction, Class<? extends StoredObject> objectClass, String columns, String condition) {
        return queryLinks(transaction, objectClass, columns, condition, null);
    }

    public final Query queryLinks(Class<? extends StoredObject> objectClass, String columns, String condition, String order) {
        return queryLinks((Transaction) null, objectClass, columns, condition, order);
    }

    public final Query queryLinks(Transaction transaction, Class<? extends StoredObject> objectClass, String columns, String condition, String order) {
        return queryLinks(transaction, null, objectClass, columns, condition, order);
    }

    public final Query queryLinks(int linkType, Class<? extends StoredObject> objectClass, String columns) {
        return queryLinks(null, linkType, objectClass, columns, null, null);
    }

    public final Query queryLinks(Transaction transaction, int linkType, Class<? extends StoredObject> objectClass, String columns) {
        return queryLinks(transaction, linkType, objectClass, columns, null, null);
    }

    public final Query queryLinks(int linkType, Class<? extends StoredObject> objectClass, String columns, String condition) {
        return queryLinks(null, linkType, objectClass, columns, condition, null);
    }

    public final Query queryLinks(Transaction transaction, int linkType, Class<? extends StoredObject> objectClass, String columns,
                                  String condition) {
        return queryLinks(transaction, linkType, objectClass, columns, condition, null);
    }

    public final Query queryLinks(int linkType, Class<? extends StoredObject> objectClass, String columns, String condition,
                                  String order) {
        return queryLinks(null, linkType, objectClass, columns, condition, order);
    }

    public final Query queryLinks(Transaction transaction, int linkType, Class<? extends StoredObject> objectClass, String columns,
                                  String condition, String order) {
        return queryLinks(transaction, "" + linkType, objectClass, columns, condition, order);
    }

    public final Query queryLinks(String linkType, Class<? extends StoredObject> objectClass, String columns) {
        return queryLinks(null, linkType, objectClass, columns, null, null);
    }

    public final Query queryLinks(Transaction transaction, String linkType, Class<? extends StoredObject> objectClass,
                                  String columns) {
        return queryLinks(transaction, linkType, objectClass, columns, null, null);
    }

    public final Query queryLinks(String linkType, Class<? extends StoredObject> objectClass, String columns, String condition) {
        return queryLinks(null, linkType, objectClass, columns, condition, null);
    }

    public final Query queryLinks(Transaction transaction, String linkType, Class<? extends StoredObject> objectClass,
                                  String columns, String condition) {
        return queryLinks(transaction, linkType, objectClass, columns, condition, null);
    }

    public final Query queryLinks(String linkType, Class<? extends StoredObject> objectClass, String columns, String condition,
                                  String order) {
        return queryLinks(null, linkType, objectClass, columns, condition, order);
    }

    public final Query queryLinks(Transaction transaction, String linkType, Class<? extends StoredObject> objectClass,
                                  String columns, String condition, String order) {
        return new Query();
    }

    public final Query queryLinks(Class<? extends StoredObject> objectClass, String columns, boolean any) {
        return queryLinks((Transaction) null, objectClass, columns, null, null, any);
    }

    public final Query queryLinks(Transaction transaction, Class<? extends StoredObject> objectClass, String columns, boolean any) {
        return queryLinks(transaction, objectClass, columns, null, null, any);
    }

    public final Query queryLinks(Class<? extends StoredObject> objectClass, String columns, String condition, boolean any) {
        return queryLinks((Transaction) null, objectClass, columns, condition, null, any);
    }

    public final Query queryLinks(Transaction transaction, Class<? extends StoredObject> objectClass, String columns,
                                  String condition, boolean any) {
        return queryLinks(transaction, objectClass, columns, condition, null, any);
    }

    public final Query queryLinks(Class<? extends StoredObject> objectClass, String columns, String condition, String order,
                                  boolean any) {
        return queryLinks((Transaction) null, objectClass, columns, condition, order, any);
    }

    public final Query queryLinks(Transaction transaction, Class<? extends StoredObject> objectClass, String columns,
                                  String condition, String order, boolean any) {
        return queryLinks(transaction, null, objectClass, columns, condition, order, any);
    }

    public final Query queryLinks(Transaction transaction, String linkType, Class<? extends StoredObject> objectClass,
                                  String columns, String condition, String order, boolean any,
                                  int skip, int limit, int[] distinct) {
        return queryLinks(transaction, linkType, objectClass, columns, condition, order, any);
    }

    public final Query queryLinks(int linkType, Class<? extends StoredObject> objectClass, String columns, boolean any) {
        return queryLinks(null, linkType, objectClass, columns, null, null, any);
    }

    public final Query queryLinks(Transaction transaction, int linkType, Class<? extends StoredObject> objectClass, String columns, boolean any) {
        return queryLinks(transaction, linkType, objectClass, columns, null, null, any);
    }

    public final Query queryLinks(int linkType, Class<? extends StoredObject> objectClass, String columns, String condition, boolean any) {
        return queryLinks(null, linkType, objectClass, columns, condition, null, any);
    }

    public final Query queryLinks(Transaction transaction, int linkType, Class<? extends StoredObject> objectClass, String columns,
                                  String condition, boolean any) {
        return queryLinks(transaction, linkType, objectClass, columns, condition, null, any);
    }

    public final Query queryLinks(int linkType, Class<? extends StoredObject> objectClass, String columns, String condition, String order, boolean any) {
        return queryLinks(null, linkType, objectClass, columns, condition, order, any);
    }

    public final Query queryLinks(Transaction transaction, int linkType, Class<? extends StoredObject> objectClass, String columns,
                                  String condition, String order, boolean any) {
        return queryLinks(transaction, "" + linkType, objectClass, columns, condition, order, any);
    }

    public final Query queryLinks(String linkType, Class<? extends StoredObject> objectClass, String columns, boolean any) {
        return queryLinks(null, linkType, objectClass, columns, null, null, any);
    }

    public final Query queryLinks(Transaction transaction, String linkType, Class<? extends StoredObject> objectClass, String columns, boolean any) {
        return queryLinks(transaction, linkType, objectClass, columns, null, null, any);
    }

    public final Query queryLinks(String linkType, Class<? extends StoredObject> objectClass, String columns, String condition, boolean any) {
        return queryLinks(null, linkType, objectClass, columns, condition, null, any);
    }

    public final Query queryLinks(Transaction transaction, String linkType, Class<? extends StoredObject> objectClass,
                                  String columns, String condition, boolean any) {
        return queryLinks(transaction, linkType, objectClass, columns, condition, null, any);
    }

    public final Query queryLinks(String linkType, Class<? extends StoredObject> objectClass, String columns, String condition, String order, boolean any) {
        return queryLinks(null, linkType, objectClass, columns, condition, order, any);
    }

    public final Query queryLinks(Transaction transaction, String linkType, Class<? extends StoredObject> objectClass,
                                  String columns, String condition, String order, boolean any) {
        return queryLinksOrMasters(transaction, linkType, objectClass, columns, condition, order, any, true);
    }

    @SuppressWarnings("unused")
    private Query queryLinksOrMasters(Transaction transaction, String linkType, Class<? extends StoredObject> objectClass,
                                      String columns, String condition, String order, boolean any, boolean links) {
        return new Query();
    }

    public final int countLinks(Class<? extends StoredObject> objectClass) {
        return countLinks((Transaction) null, objectClass, null);
    }

    public final int countLinks(Transaction transaction, Class<? extends StoredObject> objectClass) {
        return countLinks(transaction, objectClass, null);
    }

    public final int countLinks(Class<? extends StoredObject> objectClass, String condition) {
        return countLinks((Transaction) null, objectClass, condition);
    }

    public final int countLinks(Transaction transaction, Class<? extends StoredObject> objectClass, String condition) {
        return r;
    }

    public final int countLinks(int linkType, Class<? extends StoredObject> objectClass) {
        return countLinks(null, linkType, objectClass, null);
    }

    public final int countLinks(Transaction transaction, int linkType, Class<? extends StoredObject> objectClass) {
        return countLinks(transaction, linkType, objectClass, null);
    }

    public final int countLinks(int linkType, Class<? extends StoredObject> objectClass, String condition) {
        return countLinks(null, linkType, objectClass, condition);
    }

    public final int countLinks(Transaction transaction, int linkType, Class<? extends StoredObject> objectClass, String condition) {
        return r;
    }

    public final int countLinks(String linkType, Class<? extends StoredObject> objectClass) {
        return countLinks(null, linkType, objectClass, null);
    }

    public final int countLinks(Transaction transaction, String linkType, Class<? extends StoredObject> objectClass) {
        return countLinks(transaction, linkType, objectClass, null);
    }

    public final int countLinks(String linkType, Class<? extends StoredObject> objectClass, String condition) {
        return countLinks(null, linkType, objectClass, condition);
    }

    public final int countLinks(Transaction transaction, String linkType, Class<? extends StoredObject> objectClass,
                                String condition) {
        return r;
    }

    public final int countLinks(Class<? extends StoredObject> objectClass, boolean any) {
        return countLinks((Transaction) null, objectClass, null, any);
    }

    public final int countLinks(Transaction transaction, Class<? extends StoredObject> objectClass, boolean any) {
        return countLinks(transaction, objectClass, null, any);
    }

    public final int countLinks(Class<? extends StoredObject> objectClass, String condition, boolean any) {
        return countLinks((Transaction) null, objectClass, condition, any);
    }

    public final int countLinks(Transaction transaction, Class<? extends StoredObject> objectClass, String condition, boolean any) {
        return r;
    }

    public final int countLinks(int linkType, Class<? extends StoredObject> objectClass, boolean any) {
        return countLinks(null, linkType, objectClass, null, any);
    }

    public final int countLinks(Transaction transaction, int linkType, Class<? extends StoredObject> objectClass, boolean any) {
        return countLinks(transaction, linkType, objectClass, null, any);
    }

    public final int countLinks(int linkType, Class<? extends StoredObject> objectClass, String condition, boolean any) {
        return countLinks(null, linkType, objectClass, condition, any);
    }

    public final int countLinks(Transaction transaction, int linkType, Class<? extends StoredObject> objectClass, String condition,
                                boolean any) {
        return r;
    }

    public final int countLinks(String linkType, Class<? extends StoredObject> objectClass, boolean any) {
        return countLinks(null, linkType, objectClass, null, any);
    }

    public final int countLinks(Transaction transaction, String linkType, Class<? extends StoredObject> objectClass, boolean any) {
        return countLinks(transaction, linkType, objectClass, null, any);
    }

    public final int countLinks(String linkType, Class<? extends StoredObject> objectClass, String condition, boolean any) {
        return countLinks(null, linkType, objectClass, condition, any);
    }

    public final int countLinks(Transaction transaction, String linkType, Class<? extends StoredObject> objectClass,
                                String condition, boolean any) {
        return r;
    }

    public final <T extends StoredObject> ObjectIterator<T> listMasters(Class<T> objectClass) {
        return listMasters((Transaction) null, objectClass, null, null);
    }

    public final <T extends StoredObject> ObjectIterator<T> listMasters(Transaction transaction, Class<T> objectClass) {
        return listMasters(transaction, objectClass, null, null);
    }

    public final <T extends StoredObject> ObjectIterator<T> listMasters(Class<T> objectClass, String condition) {
        return listMasters((Transaction) null, objectClass, condition, null);
    }

    public final <T extends StoredObject> ObjectIterator<T> listMasters(Transaction transaction, Class<T> objectClass,
                                                                        String condition) {
        return listMasters(transaction, objectClass, condition, null);
    }

    public final <T extends StoredObject> ObjectIterator<T> listMasters(Class<T> objectClass, String condition, String order) {
        return listMasters((Transaction) null, objectClass, condition, order);
    }

    public final <T extends StoredObject> ObjectIterator<T> listMasters(Transaction transaction, Class<T> objectClass,
                                                                        String condition, String order) {
        return listMasters(transaction, null, objectClass, condition, order);
    }

    public final <T extends StoredObject> ObjectIterator<T> listMasters(int linkType, Class<T> objectClass) {
        return listMasters(null, linkType, objectClass, null, null);
    }

    public final <T extends StoredObject> ObjectIterator<T> listMasters(Transaction transaction, int linkType, Class<T> objectClass) {
        return listMasters(transaction, linkType, objectClass, null, null);
    }

    public final <T extends StoredObject> ObjectIterator<T> listMasters(int linkType, Class<T> objectClass, String condition) {
        return listMasters(null, linkType, objectClass, condition, null);
    }

    public final <T extends StoredObject> ObjectIterator<T> listMasters(Transaction transaction, int linkType,
                                                                        Class<T> objectClass, String condition) {
        return listMasters(transaction, linkType, objectClass, condition, null);
    }

    public final <T extends StoredObject> ObjectIterator<T> listMasters(int linkType, Class<T> objectClass, String condition,
                                                                        String order) {
        return listMasters(null, linkType, objectClass, condition, order);
    }

    public final <T extends StoredObject> ObjectIterator<T> listMasters(Transaction transaction, int linkType,
                                                                        Class<T> objectClass, String condition, String order) {
        return listMasters(transaction, "" + linkType, objectClass, condition, order);
    }

    public final <T extends StoredObject> ObjectIterator<T> listMasters(String linkType, Class<T> objectClass) {
        return listMasters(null, linkType, objectClass, null, null);
    }

    public final <T extends StoredObject> ObjectIterator<T> listMasters(Transaction transaction, String linkType,
                                                                        Class<T> objectClass) {
        return listMasters(transaction, linkType, objectClass, null, null);
    }

    public final <T extends StoredObject> ObjectIterator<T> listMasters(String linkType, Class<T> objectClass, String condition) {
        return listMasters(null, linkType, objectClass, condition, null);
    }

    public final <T extends StoredObject> ObjectIterator<T> listMasters(Transaction transaction, String linkType,
                                                                        Class<T> objectClass, String condition) {
        return listMasters(transaction, linkType, objectClass, condition, null);
    }

    public final <T extends StoredObject> ObjectIterator<T> listMasters(String linkType, Class<T> objectClass, String condition,
                                                                        String order) {
        return listMasters(null, linkType, objectClass, condition, order);
    }

    public final <T extends StoredObject> ObjectIterator<T> listMasters(Transaction transaction, String linkType,
                                                                        Class<T> objectClass, String condition, String order) {
        return ObjectIterator.create();
    }

    public final <T extends StoredObject> ObjectIterator<T> listMasters(Class<T> objectClass, boolean any) {
        return listMasters((Transaction) null, objectClass, null, null, any);
    }

    public final <T extends StoredObject> ObjectIterator<T> listMasters(Transaction transaction, Class<T> objectClass, boolean any) {
        return listMasters(transaction, objectClass, null, null, any);
    }

    public final <T extends StoredObject> ObjectIterator<T> listMasters(Class<T> objectClass, String condition, boolean any) {
        return listMasters((Transaction) null, objectClass, condition, null, any);
    }

    public final <T extends StoredObject> ObjectIterator<T> listMasters(Transaction transaction, Class<T> objectClass,
                                                                        String condition, boolean any) {
        return listMasters(transaction, objectClass, condition, null, any);
    }

    public final <T extends StoredObject> ObjectIterator<T> listMasters(Class<T> objectClass, String condition, String order,
                                                                        boolean any) {
        return listMasters((Transaction) null, objectClass, condition, order, any);
    }

    public final <T extends StoredObject> ObjectIterator<T> listMasters(Transaction transaction, Class<T> objectClass,
                                                                        String condition, String order, boolean any) {
        return listMasters(transaction, null, objectClass, condition, order, any);
    }

    public final <T extends StoredObject> ObjectIterator<T> listMasters(int linkType, Class<T> objectClass, boolean any) {
        return listMasters(null, linkType, objectClass, null, null, any);
    }

    public final <T extends StoredObject> ObjectIterator<T> listMasters(Transaction transaction, int linkType,
                                                                        Class<T> objectClass, boolean any) {
        return listMasters(transaction, linkType, objectClass, null, null, any);
    }

    public final <T extends StoredObject> ObjectIterator<T> listMasters(int linkType, Class<T> objectClass, String condition,
                                                                        boolean any) {
        return listMasters(null, linkType, objectClass, condition, null, any);
    }

    public final <T extends StoredObject> ObjectIterator<T> listMasters(Transaction transaction, int linkType,
                                                                        Class<T> objectClass, String condition, boolean any) {
        return listMasters(transaction, linkType, objectClass, condition, null, any);
    }

    public final <T extends StoredObject> ObjectIterator<T> listMasters(int linkType, Class<T> objectClass, String condition,
                                                                        String order, boolean any) {
        return listMasters(null, linkType, objectClass, condition, order, any);
    }

    public final <T extends StoredObject> ObjectIterator<T> listMasters(Transaction transaction, int linkType,
                                                                        Class<T> objectClass, String condition, String order, boolean any) {
        return listMasters(transaction, "" + linkType, objectClass, condition, order, any);
    }

    public final <T extends StoredObject> ObjectIterator<T> listMasters(String linkType, Class<T> objectClass, boolean any) {
        return listMasters(null, linkType, objectClass, null, null, any);
    }

    public final <T extends StoredObject> ObjectIterator<T> listMasters(Transaction transaction, String linkType,
                                                                        Class<T> objectClass, boolean any) {
        return listMasters(transaction, linkType, objectClass, null, null, any);
    }

    public final <T extends StoredObject> ObjectIterator<T> listMasters(String linkType, Class<T> objectClass, String condition,
                                                                        boolean any) {
        return listMasters(null, linkType, objectClass, condition, null, any);
    }

    public final <T extends StoredObject> ObjectIterator<T> listMasters(Transaction transaction, String linkType,
                                                                        Class<T> objectClass, String condition, boolean any) {
        return listMasters(transaction, linkType, objectClass, condition, null, any);
    }

    public final <T extends StoredObject> ObjectIterator<T> listMasters(String linkType, Class<T> objectClass,
                                                                        String condition, String order, boolean any) {
        return listMasters(null, linkType, objectClass, condition, order, any);
    }

    public final <T extends StoredObject> ObjectIterator<T> listMasters(Transaction transaction, String linkType,
                                                                        Class<T> objectClass, String condition,
                                                                        String order, boolean any) {
        return ObjectIterator.create();
    }

    public final <T extends StoredObject> ObjectIterator<T> listMasters(Transaction transaction, String linkType,
                                                                        Class<T> objectClass, String condition,
                                                                        String order, boolean any,
                                                                        int skip, int limit, int[] distinct) {
        return ObjectIterator.create();
    }

    public void setMaster(StoredObject master, int linkType) throws Exception {
    }

    public final <T extends StoredObject> T getMaster(Class<T> objectClass) {
        return getMaster((Transaction) null, objectClass, null);
    }

    public final <T extends StoredObject> T getMaster(Transaction transaction, Class<T> objectClass) {
        return getMaster(transaction, objectClass, null);
    }

    public final <T extends StoredObject> T getMaster(Class<T> objectClass, String condition) {
        return getMaster((Transaction) null, objectClass, condition);
    }

    public final <T extends StoredObject> T getMaster(Transaction transaction, Class<T> objectClass, String condition) {
        return r == 0 ? null : (T) new Person();
    }

    public final <T extends StoredObject> T getMaster(int linkType, Class<T> objectClass) {
        return getMaster(null, linkType, objectClass, null);
    }

    public final <T extends StoredObject> T getMaster(Transaction transaction, int linkType, Class<T> objectClass) {
        return getMaster(transaction, linkType, objectClass, null);
    }

    public final <T extends StoredObject> T getMaster(int linkType, Class<T> objectClass, String condition) {
        return getMaster(null, linkType, objectClass, condition);
    }

    public final <T extends StoredObject> T getMaster(Transaction transaction, int linkType, Class<T> objectClass, String condition) {
        return r == 0 ? null : (T) new Person();
    }

    public final <T extends StoredObject> T getMaster(String linkType, Class<T> objectClass) {
        return getMaster(null, linkType, objectClass, null);
    }

    public final <T extends StoredObject> T getMaster(Transaction transaction, String linkType, Class<T> objectClass) {
        return getMaster(transaction, linkType, objectClass, null);
    }

    public final <T extends StoredObject> T getMaster(String linkType, Class<T> objectClass, String condition) {
        return getMaster(null, linkType, objectClass, condition);
    }

    public final <T extends StoredObject> T getMaster(Transaction transaction, String linkType, Class<T> objectClass,
                                                      String condition) {
        return r == 0 ? null : (T) new Person();
    }

    public final <T extends StoredObject> T getMaster(Class<T> objectClass, boolean any) {
        return getMaster((Transaction) null, objectClass, null, any);
    }

    public final <T extends StoredObject> T getMaster(Transaction transaction, Class<T> objectClass, boolean any) {
        return getMaster(transaction, objectClass, null, any);
    }

    public final <T extends StoredObject> T getMaster(Class<T> objectClass, String condition, boolean any) {
        return getMaster((Transaction) null, objectClass, condition, any);
    }

    public final <T extends StoredObject> T getMaster(Transaction transaction, Class<T> objectClass, String condition, boolean any) {
        return r == 0 ? null : (T) new Person();
    }

    public final <T extends StoredObject> T getMaster(int linkType, Class<T> objectClass, boolean any) {
        return getMaster(null, linkType, objectClass, null, any);
    }

    public final <T extends StoredObject> T getMaster(Transaction transaction, int linkType, Class<T> objectClass, boolean any) {
        return getMaster(transaction, linkType, objectClass, null, any);
    }

    public final <T extends StoredObject> T getMaster(int linkType, Class<T> objectClass, String condition, boolean any) {
        return getMaster(null, linkType, objectClass, condition, any);
    }

    public final <T extends StoredObject> T getMaster(Transaction transaction, int linkType, Class<T> objectClass,
                                                      String condition, boolean any) {
        return r == 0 ? null : (T) new Person();
    }

    public final <T extends StoredObject> T getMaster(String linkType, Class<T> objectClass, boolean any) {
        return getMaster(null, linkType, objectClass, null, any);
    }

    public final <T extends StoredObject> T getMaster(Transaction transaction, String linkType, Class<T> objectClass, boolean any) {
        return getMaster(transaction, linkType, objectClass, null, any);
    }

    public final <T extends StoredObject> T getMaster(String linkType, Class<T> objectClass, String condition, boolean any) {
        return getMaster(null, linkType, objectClass, condition, any);
    }

    public final <T extends StoredObject> T getMaster(Transaction transaction, String linkType, Class<T> objectClass,
                                                      String condition, boolean any) {
        return r == 0 ? null : (T) new Person();
    }

    public boolean existsMaster(StoredObject master) {
        return existsMaster(master.getId());
    }

    public boolean existsMaster(Id masterId) {
        return id != null && id.existsMaster(masterId);
    }

    public boolean existsMasters() {
        return id != null && id.existsMasters();
    }

    public boolean existsMasters(Class<? extends StoredObject> objectClass) {
        return existsMasters(objectClass, null);
    }

    public boolean existsMasters(Class<? extends StoredObject> objectClass, boolean any) {
        return existsMasters(objectClass, null, any);
    }

    public boolean existsMasters(Class<? extends StoredObject> objectClass, String condition) {
        return exists(queryMasters(objectClass, "Id", condition));
    }

    public boolean existsMasters(Class<? extends StoredObject> objectClass, String condition, boolean any) {
        return exists(queryMasters(objectClass, "Id", condition, any));
    }

    public boolean existsMasters(String linkType, Class<? extends StoredObject> objectClass) {
        return existsMasters(linkType, objectClass, null);
    }

    public boolean existsMasters(String linkType, Class<? extends StoredObject> objectClass, boolean any) {
        return existsMasters(linkType, objectClass, null, any);
    }

    public boolean existsMasters(String linkType, Class<? extends StoredObject> objectClass, String condition) {
        return exists(queryMasters(linkType, objectClass, "Id", condition));
    }

    public boolean existsMasters(String linkType, Class<? extends StoredObject> objectClass, String condition, boolean any) {
        return exists(queryMasters(linkType, objectClass, "Id", condition, any));
    }

    public boolean existsMasters(int linkType, Class<? extends StoredObject> objectClass) {
        return existsMasters(linkType, objectClass, null);
    }

    public boolean existsMasters(int linkType, Class<? extends StoredObject> objectClass, boolean any) {
        return existsMasters(linkType, objectClass, null, any);
    }

    public boolean existsMasters(int linkType, Class<? extends StoredObject> objectClass, String condition) {
        return exists(queryMasters(linkType, objectClass, "Id", condition));
    }

    public boolean existsMasters(int linkType, Class<? extends StoredObject> objectClass, String condition, boolean any) {
        return exists(queryMasters(linkType, objectClass, "Id", condition, any));
    }

    public final Query queryMasters(Class<? extends StoredObject> objectClass, String columns) {
        return queryMasters((Transaction) null, objectClass, columns, null, null);
    }

    public final Query queryMasters(Transaction transaction, Class<? extends StoredObject> objectClass, String columns) {
        return queryMasters(transaction, objectClass, columns, null, null);
    }

    public final Query queryMasters(Class<? extends StoredObject> objectClass, String columns, String condition) {
        return queryMasters((Transaction) null, objectClass, columns, condition, null);
    }

    public final Query queryMasters(Transaction transaction, Class<? extends StoredObject> objectClass, String columns,
                                    String condition) {
        return queryMasters(transaction, objectClass, columns, condition, null);
    }

    public final Query queryMasters(Class<? extends StoredObject> objectClass, String columns, String condition, String order) {
        return queryMasters((Transaction) null, objectClass, columns, condition, order);
    }

    public final Query queryMasters(Transaction transaction, Class<? extends StoredObject> objectClass, String columns,
                                    String condition, String order) {
        return queryMasters(transaction, null, objectClass, columns, condition, order);
    }

    public final Query queryMasters(int linkType, Class<? extends StoredObject> objectClass, String columns) {
        return queryMasters(null, linkType, objectClass, columns, null, null);
    }

    public final Query queryMasters(Transaction transaction, int linkType, Class<? extends StoredObject> objectClass, String columns) {
        return queryMasters(transaction, linkType, objectClass, columns, null, null);
    }

    public final Query queryMasters(int linkType, Class<? extends StoredObject> objectClass, String columns, String condition) {
        return queryMasters(null, linkType, objectClass, columns, condition, null);
    }

    public final Query queryMasters(Transaction transaction, int linkType, Class<? extends StoredObject> objectClass,
                                    String columns, String condition) {
        return queryMasters(transaction, linkType, objectClass, columns, condition, null);
    }

    public final Query queryMasters(int linkType, Class<? extends StoredObject> objectClass, String columns, String condition,
                                    String order) {
        return queryMasters(null, linkType, objectClass, columns, condition, order);
    }

    public final Query queryMasters(Transaction transaction, int linkType, Class<? extends StoredObject> objectClass,
                                    String columns, String condition, String order) {
        return queryMasters(transaction, "" + linkType, objectClass, columns, condition, order);
    }

    public final Query queryMasters(String linkType, Class<? extends StoredObject> objectClass, String columns) {
        return queryMasters(null, linkType, objectClass, columns, null, null);
    }

    public final Query queryMasters(Transaction transaction, String linkType, Class<? extends StoredObject> objectClass,
                                    String columns) {
        return queryMasters(transaction, linkType, objectClass, columns, null, null);
    }

    public final Query queryMasters(String linkType, Class<? extends StoredObject> objectClass, String columns, String condition) {
        return queryMasters(null, linkType, objectClass, columns, condition, null);
    }

    public final Query queryMasters(Transaction transaction, String linkType, Class<? extends StoredObject> objectClass,
                                    String columns, String condition) {
        return queryMasters(transaction, linkType, objectClass, columns, condition, null);
    }

    public final Query queryMasters(String linkType, Class<? extends StoredObject> objectClass, String columns, String condition,
                                    String order) {
        return queryMasters(null, linkType, objectClass, columns, condition, order);
    }

    public final Query queryMasters(Transaction transaction, String linkType, Class<? extends StoredObject> objectClass,
                                    String columns, String condition, String order) {
        return new Query();
    }

    public final Query queryMasters(Class<? extends StoredObject> objectClass, String columns, boolean any) {
        return queryMasters((Transaction) null, objectClass, columns, null, null, any);
    }

    public final Query queryMasters(Transaction transaction, Class<? extends StoredObject> objectClass, String columns, boolean any) {
        return queryMasters(transaction, objectClass, columns, null, null, any);
    }

    public final Query queryMasters(Class<? extends StoredObject> objectClass, String columns, String condition, boolean any) {
        return queryMasters((Transaction) null, objectClass, columns, condition, null, any);
    }

    public final Query queryMasters(Transaction transaction, Class<? extends StoredObject> objectClass, String columns,
                                    String condition, boolean any) {
        return queryMasters(transaction, objectClass, columns, condition, null, any);
    }

    public final Query queryMasters(Class<? extends StoredObject> objectClass, String columns, String condition, String order, boolean any) {
        return queryMasters((Transaction) null, objectClass, columns, condition, order, any);
    }

    public final Query queryMasters(Transaction transaction, Class<? extends StoredObject> objectClass, String columns, String condition, String order, boolean any) {
        return queryMasters(transaction, null, objectClass, columns, condition, order, any);
    }

    public final Query queryMasters(int linkType, Class<? extends StoredObject> objectClass, String columns, boolean any) {
        return queryMasters(null, linkType, objectClass, columns, null, null, any);
    }

    public final Query queryMasters(Transaction transaction, int linkType, Class<? extends StoredObject> objectClass, String columns, boolean any) {
        return queryMasters(transaction, linkType, objectClass, columns, null, null, any);
    }

    public final Query queryMasters(int linkType, Class<? extends StoredObject> objectClass, String columns, String condition, boolean any) {
        return queryMasters(null, linkType, objectClass, columns, condition, null, any);
    }

    public final Query queryMasters(Transaction transaction, int linkType, Class<? extends StoredObject> objectClass, String columns, String condition, boolean any) {
        return queryMasters(transaction, linkType, objectClass, columns, condition, null, any);
    }

    public final Query queryMasters(int linkType, Class<? extends StoredObject> objectClass, String columns, String condition, String order, boolean any) {
        return queryMasters(null, linkType, objectClass, columns, condition, order, any);
    }

    public final Query queryMasters(Transaction transaction, int linkType, Class<? extends StoredObject> objectClass,
                                    String columns, String condition, String order, boolean any) {
        return queryMasters(transaction, "" + linkType, objectClass, columns, condition, order, any);
    }

    public final Query queryMasters(String linkType, Class<? extends StoredObject> objectClass, String columns, boolean any) {
        return queryMasters(null, linkType, objectClass, columns, null, null, any);
    }

    public final Query queryMasters(Transaction transaction, String linkType, Class<? extends StoredObject> objectClass, String columns, boolean any) {
        return queryMasters(transaction, linkType, objectClass, columns, null, null, any);
    }

    public final Query queryMasters(String linkType, Class<? extends StoredObject> objectClass, String columns, String condition, boolean any) {
        return queryMasters(null, linkType, objectClass, columns, condition, null, any);
    }

    public final Query queryMasters(Transaction transaction, String linkType, Class<? extends StoredObject> objectClass, String columns, String condition, boolean any) {
        return queryMasters(transaction, linkType, objectClass, columns, condition, null, any);
    }

    public final Query queryMasters(String linkType, Class<? extends StoredObject> objectClass, String columns, String condition, String order, boolean any) {
        return queryMasters(null, linkType, objectClass, columns, condition, order, any);
    }

    public final Query queryMasters(Transaction transaction, String linkType, Class<? extends StoredObject> objectClass,
                                    String columns, String condition, String order, boolean any) {
        return queryLinksOrMasters(transaction, linkType, objectClass, columns, condition, order, any, false);
    }

    public final Query queryMasters(Transaction transaction, String linkType, Class<? extends StoredObject> objectClass,
                                    String columns, String condition, String order, boolean any,
                                    int skip, int limit, int[] distinct) {
        return queryLinksOrMasters(transaction, linkType, objectClass, columns, condition, order, any, false);
    }

    public final int countMasters(Class<? extends StoredObject> objectClass) {
        return countMasters((Transaction) null, objectClass, null);
    }

    public final int countMasters(Transaction transaction, Class<? extends StoredObject> objectClass) {
        return countMasters(transaction, objectClass, null);
    }

    public final int countMasters(Class<? extends StoredObject> objectClass, String condition) {
        return countMasters((Transaction) null, objectClass, condition);
    }

    public final int countMasters(Transaction transaction, Class<? extends StoredObject> objectClass, String condition) {
        return r;
    }

    public final int countMasters(int linkType, Class<? extends StoredObject> objectClass) {
        return countMasters(null, linkType, objectClass, null);
    }

    public final int countMasters(Transaction transaction, int linkType, Class<? extends StoredObject> objectClass) {
        return countMasters(transaction, linkType, objectClass, null);
    }

    public final int countMasters(int linkType, Class<? extends StoredObject> objectClass, String condition) {
        return countMasters(null, linkType, objectClass, condition);
    }

    public final int countMasters(Transaction transaction, int linkType, Class<? extends StoredObject> objectClass, String condition) {
        return r;
    }

    public final int countMasters(String linkType, Class<? extends StoredObject> objectClass) {
        return countMasters(null, linkType, objectClass, null);
    }

    public final int countMasters(Transaction transaction, String linkType, Class<? extends StoredObject> objectClass) {
        return countMasters(transaction, linkType, objectClass, null);
    }

    public final int countMasters(String linkType, Class<? extends StoredObject> objectClass, String condition) {
        return countMasters(null, linkType, objectClass, condition);
    }

    public final int countMasters(Transaction transaction, String linkType, Class<? extends StoredObject> objectClass,
                                  String condition) {
        return r;
    }

    public final int countMasters(Class<? extends StoredObject> objectClass, boolean any) {
        return countMasters((Transaction) null, objectClass, null, any);
    }

    public final int countMasters(Transaction transaction, Class<? extends StoredObject> objectClass, boolean any) {
        return countMasters(transaction, objectClass, null, any);
    }

    public final int countMasters(Class<? extends StoredObject> objectClass, String condition, boolean any) {
        return countMasters((Transaction) null, objectClass, condition, any);
    }

    public final int countMasters(Transaction transaction, Class<? extends StoredObject> objectClass, String condition, boolean any) {
        return r;
    }

    public final int countMasters(int linkType, Class<? extends StoredObject> objectClass, boolean any) {
        return countMasters(null, linkType, objectClass, null, any);
    }

    public final int countMasters(Transaction transaction, int linkType, Class<? extends StoredObject> objectClass, boolean any) {
        return countMasters(transaction, linkType, objectClass, null, any);
    }

    public final int countMasters(int linkType, Class<? extends StoredObject> objectClass, String condition, boolean any) {
        return countMasters(null, linkType, objectClass, condition, any);
    }

    public final int countMasters(Transaction transaction, int linkType, Class<? extends StoredObject> objectClass, String condition, boolean any) {
        return r;
    }

    public final int countMasters(String linkType, Class<? extends StoredObject> objectClass, boolean any) {
        return countMasters(null, linkType, objectClass, null, any);
    }

    public final int countMasters(Transaction transaction, String linkType, Class<? extends StoredObject> objectClass, boolean any) {
        return countMasters(transaction, linkType, objectClass, null, any);
    }

    public final int countMasters(String linkType, Class<? extends StoredObject> objectClass, String condition, boolean any) {
        return countMasters(null, linkType, objectClass, condition, any);
    }

    public final int countMasters(Transaction transaction, String linkType, Class<? extends StoredObject> objectClass, String condition, boolean any) {
        return r;
    }

    public final Id getAttachmentId(String name) {
        return id == null ? null : id.getAttachmentId(name);
    }

    @SuppressWarnings("UnusedReturnValue")
    public final StreamData getAttachment(String name) {
        return id == null ? null : id.getAttachment(name);
    }

    public final <F extends FileData> F getFileData(String name) {
        return getFileData(name, null);
    }

    public final <F extends FileData> F getFileData(String name, Transaction transaction) {
        return (F) new FileData();
    }

    public final ObjectIterator<? extends FileData> listFileData() {
        return listFileData(null);
    }

    public final ObjectIterator<? extends FileData> listFileData(Transaction transaction) {
        return ObjectIterator.create();
    }

    public final boolean existsFileData() {
        return existsLinks(9, FileData.class, true);
    }

    public final List<StoredObjectLink<?>> objectLinks(boolean create) {
        return links;
    }

    public final List<StoredObjectLink<?>> objectLinks() {
        return objectLinks(false);
    }

    public final StoredObjectLink<?> objectLink(String name) {
        return objectLink(name, false);
    }

    public final StoredObjectLink<?> objectLink(String name, boolean create) {
        List<StoredObjectLink<?>> links = objectLinks(create);
        if (links == null) {
            return null;
        }
        StoredObjectLink<?> link = links.stream().filter(l -> l.getName().equals(name)).findAny().orElse(null);
        if (link != null || !create) {
            return link;
        }
        return null;
    }

    public final StoredObjectLink<?> objectLink(StoredObjectUtility.Link<?> sLink) {
        return objectLink(sLink.getName(), true);
    }

    public final void clearObjectLinks() {
        links = null;
    }

    public static String toCode(String code) {
        return code;
    }

    static String createSQL(ClassAttribute<?> ca, String columns, String condition, String order, boolean only,
                            boolean header, int skip, int limit, int[] distinct) {
        return "";
    }
}
