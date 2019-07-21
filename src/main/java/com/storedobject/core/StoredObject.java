package com.storedobject.core;

import com.storedobject.common.StringList;

import java.util.Map;

public abstract class StoredObject {

    public static java.util.logging.Logger logger;

    public StoredObject() {
    }

    protected static final < T extends StoredObject > T get(Transaction p1, Class < T > p2, java.sql.ResultSet p3) {
        return null;
    }

    public static final StoredObject get(Id p1) {
        return null;
    }

    public static final StoredObject get(Transaction p1, Id p2) {
        return null;
    }

    public static final < T extends StoredObject > T get(Class < T > p1, Id p2) {
        return null;
    }

    public static final < T extends StoredObject > T get(Transaction p1, Class < T > p2, Id p3) {
        return null;
    }

    public static final < T extends StoredObject > T get(Class < T > p1) {
        return null;
    }

    public static final < T extends StoredObject > T get(Class < T > p1, String p2) {
        return null;
    }

    public static final < T extends StoredObject > T get(Transaction p1, Class < T > p2, String p3) {
        return null;
    }

    public static final < T extends StoredObject > T get(Class < T > p1, String p2, String p3) {
        return null;
    }

    public static final < T extends StoredObject > T get(Transaction p1, Class < T > p2, String p3, String p4) {
        return null;
    }

	public final static <T extends StoredObject> T get(ObjectIterator<T> iterator) {
		return null;
	}

	public final static <T extends StoredObject> T get(ObjectIterator<T> list, boolean validateOne) {
		return null;
	}

    protected static final < T extends StoredObject > T get(Transaction p1, ClassAttribute < T > p2, String p3, String p4) {
        return null;
    }

    public static final < T extends StoredObject, C extends T > C get(Class < T > p1, Id p2, boolean p3) {
        return null;
    }

    public static final < T extends StoredObject, C extends T > C get(Transaction p1, Class < T > p2, Id p3, boolean p4) {
        return null;
    }

    public static final < T extends StoredObject, C extends T > C get(Class < T > p1, boolean p2) {
        return null;
    }

    public static final < T extends StoredObject, C extends T > C get(Class < T > p1, String p2, boolean p3) {
        return null;
    }

    public static final < T extends StoredObject, C extends T > C get(Transaction p1, Class < T > p2, String p3, boolean p4) {
        return null;
    }

    public static final < T extends StoredObject, C extends T > C get(Class < T > p1, String p2, String p3, boolean p4) {
        return null;
    }

    public static final < T extends StoredObject, C extends T > C get(Transaction p1, Class < T > p2, String p3, String p4, boolean p5) {
        return null;
    }

    @Override
	public boolean equals(Object p1) {
        return false;
    }

    @Override
	public String toString() {
        return null;
    }

    @Override
	public int hashCode() {
        return 0;
    }

    public static final int count(Class <? extends StoredObject > p1) {
        return 0;
    }

    public static final int count(Class <? extends StoredObject > p1, String p2) {
        return 0;
    }

    public static final int count(Transaction p1, Class <? extends StoredObject > p2, String p3) {
        return 0;
    }

    public static final int count(Class <? extends StoredObject > p1, boolean p2) {
        return 0;
    }

    public static final int count(Class <? extends StoredObject > p1, String p2, boolean p3) {
        return 0;
    }

    public static final int count(Transaction p1, Class <? extends StoredObject > p2, String p3, boolean p4) {
        return 0;
    }

    public final < T extends StoredObject > T getParent() {
        return null;
    }

    public final < T extends StoredObject > T getParent(Class < T > p1) {
        return null;
    }

    public final < T extends StoredObject > T getParent(Class < T > p1, int p2) {
        return null;
    }

    public void setRawValue(String attributeName, String rawValue) throws Exception {
    }

    public final boolean checkForDuplicate() {
    	return false;
    }
    
    public void load(java.io.BufferedReader p1) throws Exception {
    }

    public static int load(TransactionManager p1, java.io.InputStream p2, java.util.Comparator < CharSequence > p3) throws Exception {
        return 0;
    }

    public static int load(TransactionManager p1, java.io.Reader p2, java.util.Comparator < CharSequence > p3) throws Exception {
        return 0;
    }

    public final java.sql.Timestamp timestamp() {
        return null;
    }

    public final Id getId() {
        return null;
    }

    public static final < T extends StoredObject > ObjectIterator < T > list(Class < T > p1) {
        return null;
    }

    public static final < T extends StoredObject > ObjectIterator < T > list(Class < T > p1, String p2) {
        return null;
    }

    public static final < T extends StoredObject > ObjectIterator < T > list(Transaction p1, Class < T > p2, String p3) {
        return null;
    }

    public static final < T extends StoredObject > ObjectIterator < T > list(Class < T > p1, String p2, String p3) {
        return null;
    }

    public static final < T extends StoredObject > ObjectIterator < T > list(Transaction p1, Class < T > p2, String p3, String p4) {
        return null;
    }

    public static final < T extends StoredObject > ObjectIterator < T > list(Class < T > p1, boolean p2) {
        return null;
    }

    public static final < T extends StoredObject > ObjectIterator < T > list(Class < T > p1, String p2, boolean p3) {
        return null;
    }

    public static final < T extends StoredObject > ObjectIterator < T > list(Transaction p1, Class < T > p2, String p3, boolean p4) {
        return null;
    }

    public static final < T extends StoredObject > ObjectIterator < T > list(Class < T > p1, String p2, String p3, boolean p4) {
        return null;
    }

    public static final < T extends StoredObject > ObjectIterator < T > list(Transaction p1, Class < T > p2, String p3, String p4, boolean p5) {
        return null;
    }

    public final void save(Transaction p1) throws Exception {
    }

    public final void save() throws Exception {
    }

    public final boolean save(TransactionControl p1) {
        return false;
    }

    public final void save(java.io.Writer p1) throws Exception {
    }

    public final void save(Map<String, Object> map) throws Throwable {
    }

    public final void save(Map<String, Object> map, boolean includeReferences) throws Throwable {
    }

    public final void save(Map<String, Object> map, boolean includeReferences, boolean includeClassInfo) throws Throwable {
    }

    public final void save(Map<String, Object> map, StringList attributes, boolean includeReferences, boolean includeClassInfo) throws Throwable {
    }

    public final void save(Map<String, Object> map, String valueName, boolean includeReferences, boolean includeClassInfo) throws Throwable {
    }

    public final void save(Map<String, Object> map, String valueName, boolean includeReferences) throws Throwable {
    }

    public final void save(Map<String, Object> map, StringList attributes, String valueName) throws Throwable {
    }

    public final void save(Map<String, Object> map, StringList attributes) throws Throwable {
    }

    public final void save(Map<String, Object> map, String valueName) throws Throwable {
    }

    public final void save(Map<String, Object> map, StringList attributes, String valueName, boolean includeReferences, boolean includeClassInfo) throws Throwable {
    }

    public boolean copy(StoredObject p1) throws Exception {
        return false;
    }

    public StoredObject copy() throws Exception {
        return null;
    }

    public final void delete(Transaction p1) throws Exception {
    }

    public final void delete() throws Exception {
    }

    public final boolean delete(TransactionControl p1) {
        return false;
    }

    public static StoredObject create(java.io.BufferedReader p1) throws Exception {
        return null;
    }
    
	public final static <T extends StoredObject> ObjectIterator<T> list(Transaction transaction, ClassAttribute<T> ca, String condition, String order) {
		return null;
	}

    public static final boolean exists(Query p1) {
        return false;
    }

    public static final boolean exists(Class <? extends StoredObject > p1, String p2) {
        return false;
    }

    public static final boolean exists(Class <? extends StoredObject > p1, String p2, boolean p3) {
        return false;
    }

    public static final boolean exists(Transaction p1, Class <? extends StoredObject > p2, String p3) {
        return false;
    }

    public static final boolean exists(Transaction p1, Class <? extends StoredObject > p2, String p3, boolean p4) {
        return false;
    }

    public static final Query query(Class <? extends StoredObject > p1, String p2) {
        return null;
    }

    public static final Query query(Class <? extends StoredObject > p1, String p2, String p3) {
        return null;
    }

    public static final Query query(Transaction p1, Class <? extends StoredObject > p2, String p3, String p4) {
        return null;
    }

    public static final Query query(Class <? extends StoredObject > p1, String p2, String p3, String p4) {
        return null;
    }

    public static final Query query(Transaction p1, Class <? extends StoredObject > p2, String p3, String p4, String p5) {
        return null;
    }

    public static final Query query(Class <? extends StoredObject > p1, String p2, boolean p3) {
        return null;
    }

    public static final Query query(Class <? extends StoredObject > p1, String p2, String p3, boolean p4) {
        return null;
    }

    public static final Query query(Transaction p1, Class <? extends StoredObject > p2, String p3, String p4, boolean p5) {
        return null;
    }

    public static final Query query(Class <? extends StoredObject > p1, String p2, String p3, String p4, boolean p5) {
        return null;
    }

    public static final Query query(Transaction p1, Class <? extends StoredObject > p2, String p3, String p4, String p5, boolean p6) {
        return null;
    }

    public final int family() {
        return 0;
    }

    public Transaction getTransaction() {
        return null;
    }

    public final boolean loading() {
        return false;
    }

    public final void checkTransaction() throws Exception {
    }

    public void validateData() throws Exception {
    }

    public final boolean inserted() {
        return false;
    }

    public final void checkType(Id p1, Class <? extends StoredObject > p2) throws Invalid_Value, Invalid_State {
    }

    public final void checkType(Id p1, Class <? extends StoredObject > p2, boolean p3) throws Invalid_Value, Invalid_State {
    }

    public final void debit(Account p1, Money p2, int p3) throws Exception {
    }

    public final void debit(Account p1, java.math.BigDecimal p2, int p3) throws Exception {
    }

    public final void debit(Account p1, Money p2, Money p3, int p4) throws Exception {
    }

    public final void debit(Account p1, java.math.BigDecimal p2, java.math.BigDecimal p3, int p4) throws Exception {
    }

    public final void credit(Account p1, Money p2, int p3) throws Exception {
    }

    public final void credit(Account p1, java.math.BigDecimal p2, int p3) throws Exception {
    }

    public final void credit(Account p1, Money p2, Money p3, int p4) throws Exception {
    }

    public final void credit(Account p1, java.math.BigDecimal p2, java.math.BigDecimal p3, int p4) throws Exception {
    }

    public final boolean saving() {
        return false;
    }

    public final Id getTransactionId() {
        return null;
    }

    public String getTransactionIP() {
        return null;
    }

    public void setTransaction(Transaction p1) throws Exception {
    }
    
    public final boolean old() {
    	return false;
    }

    public final boolean created() {
        return false;
    }

    public final boolean updated() {
        return false;
    }

    public final boolean deleted() {
        return false;
    }

    public final boolean undeleted() {
        return false;
    }

    public final boolean transacting() {
        return false;
    }

    public final String moduleName() {
        return null;
    }

    public final String tableName() {
        return null;
    }

    public void validateInsert() throws Exception {
    }

    public void validateUpdate() throws Exception {
    }

    public void validateChildUpdate(StoredObject p1, int p2) throws Exception {
    }

    public void validateParentUpdate(StoredObject p1, int p2) throws Exception {
    }

    public void validateDelete() throws Exception {
    }

    public void validateUndelete() throws Exception {
    }

    public void validate() throws Exception {
    }

    public void validateChildAttach(StoredObject p1, int p2) throws Exception {
    }

    public void validateChildDetach(StoredObject p1, int p2) throws Exception {
    }

    public void validateParentAttach(StoredObject p1, int p2) throws Exception {
    }

    public void validateParentDetach(StoredObject p1, int p2) throws Exception {
    }

    public final void removeAllLinks() throws Exception {
    }

    public final void removeAllLinks(Transaction p1) throws Exception {
    }

    public final void removeAllLinks(int p1) throws Exception {
    }

    public final void removeAllLinks(Transaction p1, int p2) throws Exception {
    }

    public final void removeAllLinks(String p1) throws Exception {
    }

    public final void removeAllLinks(Transaction p1, String p2) throws Exception {
    }

    public final void removeAllLinks(Class <? extends StoredObject > p1) throws Exception {
    }

    public final void removeAllLinks(Transaction p1, Class <? extends StoredObject > p2) throws Exception {
    }

    public final void removeAllLinks(Class <? extends StoredObject > p1, int p2) throws Exception {
    }

    public final void removeAllLinks(Transaction p1, Class <? extends StoredObject > p2, int p3) throws Exception {
    }

    public final void removeAllLinks(Class <? extends StoredObject > p1, String p2) throws Exception {
    }

    public final void removeAllLinks(Transaction p1, Class <? extends StoredObject > p2, String p3) throws Exception {
    }

    public final void removeReverseLinks() throws Exception {
    }

    public final void removeReverseLinks(Transaction p1) throws Exception {
    }

    public final void undelete(Transaction p1) throws Exception {
    }

    public final void undelete() throws Exception {
    }

    public void generateTransactions() throws Exception {
    }

    public void saved() throws Exception {
    }

    public final String stringify() throws Exception {
        return null;
    }

    public final StoredObject getUnique() {
        return null;
    }

    public String getUniqueCondition() {
        return null;
    }

    public String transactionNarration(int p1) {
        return null;
    }

    protected final String historyString() throws Exception {
        return null;
    }

    public void loaded() {
    }

    public final void addLink(StoredObject p1) throws Exception {
    }

    public final void addLink(StoredObject p1, int p2) throws Exception {
    }

    public final void addLink(Transaction p1, StoredObject p2) throws Exception {
    }

    public final void addLink(Transaction p1, StoredObject p2, int p3) throws Exception {
    }

    public final void addLink(Id p1) throws Exception {
    }

    public final void addLink(Id p1, int p2) throws Exception {
    }

    public final void addLink(Transaction p1, Id p2) throws Exception {
    }

    public final void addLink(Transaction p1, Id p2, int p3) throws Exception {
    }

    public final void removeLink(StoredObject p1) throws Exception {
    }

    public final void removeLink(Transaction p1, StoredObject p2) throws Exception {
    }

    public final void removeLink(Id p1) throws Exception {
    }

    public final void removeLink(Transaction p1, Id p2) throws Exception {
    }

    public final void removeLink(StoredObject p1, int p2) throws Exception {
    }

    public final void removeLink(Transaction p1, StoredObject p2, int p3) throws Exception {
    }

    public final void removeLink(Id p1, int p2) throws Exception {
    }

    public final void removeLink(Transaction p1, Id p2, int p3) throws Exception {
    }

    public final void removeLink(StoredObject p1, String p2) throws Exception {
    }

    public final void removeLink(Transaction p1, StoredObject p2, String p3) throws Exception {
    }

    public final void removeLink(Id p1, String p2) throws Exception {
    }

    public final void removeLink(Transaction p1, Id p2, String p3) throws Exception {
    }

    public final String checkCurrency(String p1) throws Invalid_Value {
        return null;
    }

    public final void checkTypeAny(Id p1, Class <? extends StoredObject > p2) throws Invalid_Value, Invalid_State {
    }

    public final void checkTypeAny(Id p1, Class <? extends StoredObject > p2, boolean p3) throws Invalid_Value, Invalid_State {
    }

    public final Id getParentId() {
        return null;
    }

    public final Id getParentId(Class <? extends StoredObject > p1) {
        return null;
    }

    public final Id getParentId(Class <? extends StoredObject > p1, int p2) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > getChildren() {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listLinks(Class < T > p1) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listLinks(Transaction p1, Class < T > p2) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listLinks(Class < T > p1, String p2) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listLinks(Transaction p1, Class < T > p2, String p3) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listLinks(Class < T > p1, String p2, String p3) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listLinks(Transaction p1, Class < T > p2, String p3, String p4) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listLinks(int p1, Class < T > p2) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listLinks(Transaction p1, int p2, Class < T > p3) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listLinks(int p1, Class < T > p2, String p3) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listLinks(Transaction p1, int p2, Class < T > p3, String p4) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listLinks(int p1, Class < T > p2, String p3, String p4) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listLinks(Transaction p1, int p2, Class < T > p3, String p4, String p5) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listLinks(String p1, Class < T > p2) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listLinks(Transaction p1, String p2, Class < T > p3) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listLinks(String p1, Class < T > p2, String p3) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listLinks(Transaction p1, String p2, Class < T > p3, String p4) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listLinks(String p1, Class < T > p2, String p3, String p4) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listLinks(Transaction p1, String p2, Class < T > p3, String p4, String p5) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listLinks(Class < T > p1, boolean p2) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listLinks(Transaction p1, Class < T > p2, boolean p3) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listLinks(Class < T > p1, String p2, boolean p3) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listLinks(Transaction p1, Class < T > p2, String p3, boolean p4) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listLinks(Class < T > p1, String p2, String p3, boolean p4) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listLinks(Transaction p1, Class < T > p2, String p3, String p4, boolean p5) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listLinks(int p1, Class < T > p2, boolean p3) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listLinks(Transaction p1, int p2, Class < T > p3, boolean p4) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listLinks(int p1, Class < T > p2, String p3, boolean p4) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listLinks(Transaction p1, int p2, Class < T > p3, String p4, boolean p5) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listLinks(int p1, Class < T > p2, String p3, String p4, boolean p5) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listLinks(Transaction p1, int p2, Class < T > p3, String p4, String p5, boolean p6) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listLinks(String p1, Class < T > p2, boolean p3) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listLinks(Transaction p1, String p2, Class < T > p3, boolean p4) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listLinks(String p1, Class < T > p2, String p3, boolean p4) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listLinks(Transaction p1, String p2, Class < T > p3, String p4, boolean p5) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listLinks(String p1, Class < T > p2, String p3, String p4, boolean p5) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listLinks(Transaction p1, String p2, Class < T > p3, String p4, String p5, boolean p6) {
        return null;
    }

	public final StoredObject reload(Transaction fromTransaction) {
		return null;
	}
	
    public final StoredObject reload() {
        return null;
    }

    public void reloaded() {
    }
    
	public String toDisplay() {
		return null;
	}

    public final Id userId() {
        return null;
    }
    
	public StoredObject previousVersion() {
		return null;
	}
	
	public StoredObject previousVersion(StoredObject parent) {
		return null;
	}
    
	public static <H extends StoredObject> H getHistorical(Class<H> objectClass, Id id) {
		return null;
	}

    public SystemUser user() {
        return null;
    }

    public Person person() {
        return null;
    }

    public ObjectIterator <? extends StoredObject > listHistory() {
        return null;
    }

    public static final < T extends StoredObject > ObjectIterator < T > listViaQuery(Class < T > p1, Query p2) {
        return null;
    }

    public static final < T extends StoredObject > ObjectIterator < T > listViaQuery(Transaction p1, Class < T > p2, Query p3) {
        return null;
    }

	public boolean existsLink(StoredObject link) {
        return false;
	}
	
	public boolean existsLink(int linkType, StoredObject link) {
        return false;
	}
	
	public boolean existsLink(Transaction transaction, StoredObject link) {
        return false;
	}
	
	public boolean existsLink(Transaction transaction, int linkType, StoredObject link) {
        return false;
	}
	
	public boolean existsLink(Id linkId) {
        return false;
	}

	public boolean existsLink(int linkType, Id linkId) {
        return false;
	}

	public boolean existsLink(Transaction transaction, Id linkId) {
        return false;
	}
	
	public boolean existsLink(Transaction transaction, int linkType, Id linkId) {
        return false;
	}
	
	public boolean existsLinks() {
        return false;
	}

	public boolean existsLinks(Transaction transaction) {
        return false;
	}

    public boolean existsLinks(Class <? extends StoredObject > p1) {
        return false;
    }

    public boolean existsLinks(Class <? extends StoredObject > p1, boolean p2) {
        return false;
    }

    public boolean existsLinks(Class <? extends StoredObject > p1, String p2) {
        return false;
    }

    public boolean existsLinks(Class <? extends StoredObject > p1, String p2, boolean p3) {
        return false;
    }

    public boolean existsLinks(String p1, Class <? extends StoredObject > p2) {
        return false;
    }

    public boolean existsLinks(String p1, Class <? extends StoredObject > p2, boolean p3) {
        return false;
    }

    public boolean existsLinks(String p1, Class <? extends StoredObject > p2, String p3) {
        return false;
    }

    public boolean existsLinks(String p1, Class <? extends StoredObject > p2, String p3, boolean p4) {
        return false;
    }

    public boolean existsLinks(int p1, Class <? extends StoredObject > p2) {
        return false;
    }

    public boolean existsLinks(int p1, Class <? extends StoredObject > p2, boolean p3) {
        return false;
    }

    public boolean existsLinks(int p1, Class <? extends StoredObject > p2, String p3) {
        return false;
    }

    public boolean existsLinks(int p1, Class <? extends StoredObject > p2, String p3, boolean p4) {
        return false;
    }
    
	public boolean existsLinks(Transaction tran, Class<? extends StoredObject> objectClass) {
		return false;
	}

	public boolean existsLinks(Transaction tran, Class<? extends StoredObject> objectClass, boolean any) {
		return false;
	}

	public boolean existsLinks(Transaction tran, Class<? extends StoredObject> objectClass, String condition) {
		return false;
	}

	public boolean existsLinks(Transaction tran, Class<? extends StoredObject> objectClass, String condition, boolean any) {
		return false;
	}

	public boolean existsLinks(Transaction tran, String linkType, Class<? extends StoredObject> objectClass) {
		return false;
	}

	public boolean existsLinks(Transaction tran, String linkType, Class<? extends StoredObject> objectClass, boolean any) {
		return false;
	}

	public boolean existsLinks(Transaction tran, String linkType, Class<? extends StoredObject> objectClass, String condition) {
		return false;
	}

	public boolean existsLinks(Transaction tran, String linkType, Class<? extends StoredObject> objectClass, String condition, boolean any) {
		return false;
	}

	public boolean existsLinks(Transaction tran, int linkType, Class<? extends StoredObject> objectClass) {
		return false;
	}

	public boolean existsLinks(Transaction tran, int linkType, Class<? extends StoredObject> objectClass, boolean any) {
		return false;
	}

	public boolean existsLinks(Transaction tran, int linkType, Class<? extends StoredObject> objectClass, String condition) {
		return false;
	}

	public boolean existsLinks(Transaction tran, int linkType, Class<? extends StoredObject> objectClass, String condition, boolean any) {
		return false;
	}

    public final Query queryLinks(Class <? extends StoredObject > p1, String p2) {
        return null;
    }

    public final Query queryLinks(Transaction p1, Class <? extends StoredObject > p2, String p3) {
        return null;
    }

    public final Query queryLinks(Class <? extends StoredObject > p1, String p2, String p3) {
        return null;
    }

    public final Query queryLinks(Transaction p1, Class <? extends StoredObject > p2, String p3, String p4) {
        return null;
    }

    public final Query queryLinks(Class <? extends StoredObject > p1, String p2, String p3, String p4) {
        return null;
    }

    public final Query queryLinks(Transaction p1, Class <? extends StoredObject > p2, String p3, String p4, String p5) {
        return null;
    }

    public final Query queryLinks(int p1, Class <? extends StoredObject > p2, String p3) {
        return null;
    }

    public final Query queryLinks(Transaction p1, int p2, Class <? extends StoredObject > p3, String p4) {
        return null;
    }

    public final Query queryLinks(int p1, Class <? extends StoredObject > p2, String p3, String p4) {
        return null;
    }

    public final Query queryLinks(Transaction p1, int p2, Class <? extends StoredObject > p3, String p4, String p5) {
        return null;
    }

    public final Query queryLinks(int p1, Class <? extends StoredObject > p2, String p3, String p4, String p5) {
        return null;
    }

    public final Query queryLinks(Transaction p1, int p2, Class <? extends StoredObject > p3, String p4, String p5, String p6) {
        return null;
    }

    public final Query queryLinks(String p1, Class <? extends StoredObject > p2, String p3) {
        return null;
    }

    public final Query queryLinks(Transaction p1, String p2, Class <? extends StoredObject > p3, String p4) {
        return null;
    }

    public final Query queryLinks(String p1, Class <? extends StoredObject > p2, String p3, String p4) {
        return null;
    }

    public final Query queryLinks(Transaction p1, String p2, Class <? extends StoredObject > p3, String p4, String p5) {
        return null;
    }

    public final Query queryLinks(String p1, Class <? extends StoredObject > p2, String p3, String p4, String p5) {
        return null;
    }

    public final Query queryLinks(Transaction p1, String p2, Class <? extends StoredObject > p3, String p4, String p5, String p6) {
        return null;
    }

    public final Query queryLinks(Class <? extends StoredObject > p1, String p2, boolean p3) {
        return null;
    }

    public final Query queryLinks(Transaction p1, Class <? extends StoredObject > p2, String p3, boolean p4) {
        return null;
    }

    public final Query queryLinks(Class <? extends StoredObject > p1, String p2, String p3, boolean p4) {
        return null;
    }

    public final Query queryLinks(Transaction p1, Class <? extends StoredObject > p2, String p3, String p4, boolean p5) {
        return null;
    }

    public final Query queryLinks(Class <? extends StoredObject > p1, String p2, String p3, String p4, boolean p5) {
        return null;
    }

    public final Query queryLinks(Transaction p1, Class <? extends StoredObject > p2, String p3, String p4, String p5, boolean p6) {
        return null;
    }

    public final Query queryLinks(int p1, Class <? extends StoredObject > p2, String p3, boolean p4) {
        return null;
    }

    public final Query queryLinks(Transaction p1, int p2, Class <? extends StoredObject > p3, String p4, boolean p5) {
        return null;
    }

    public final Query queryLinks(int p1, Class <? extends StoredObject > p2, String p3, String p4, boolean p5) {
        return null;
    }

    public final Query queryLinks(Transaction p1, int p2, Class <? extends StoredObject > p3, String p4, String p5, boolean p6) {
        return null;
    }

    public final Query queryLinks(int p1, Class <? extends StoredObject > p2, String p3, String p4, String p5, boolean p6) {
        return null;
    }

    public final Query queryLinks(Transaction p1, int p2, Class <? extends StoredObject > p3, String p4, String p5, String p6, boolean p7) {
        return null;
    }

    public final Query queryLinks(String p1, Class <? extends StoredObject > p2, String p3, boolean p4) {
        return null;
    }

    public final Query queryLinks(Transaction p1, String p2, Class <? extends StoredObject > p3, String p4, boolean p5) {
        return null;
    }

    public final Query queryLinks(String p1, Class <? extends StoredObject > p2, String p3, String p4, boolean p5) {
        return null;
    }

    public final Query queryLinks(Transaction p1, String p2, Class <? extends StoredObject > p3, String p4, String p5, boolean p6) {
        return null;
    }

    public final Query queryLinks(String p1, Class <? extends StoredObject > p2, String p3, String p4, String p5, boolean p6) {
        return null;
    }

    public final Query queryLinks(Transaction p1, String p2, Class <? extends StoredObject > p3, String p4, String p5, String p6, boolean p7) {
        return null;
    }

    public final int countLinks(Class <? extends StoredObject > p1) {
        return 0;
    }

    public final int countLinks(Transaction p1, Class <? extends StoredObject > p2) {
        return 0;
    }

    public final int countLinks(Class <? extends StoredObject > p1, String p2) {
        return 0;
    }

    public final int countLinks(Transaction p1, Class <? extends StoredObject > p2, String p3) {
        return 0;
    }

    public final int countLinks(int p1, Class <? extends StoredObject > p2) {
        return 0;
    }

    public final int countLinks(Transaction p1, int p2, Class <? extends StoredObject > p3) {
        return 0;
    }

    public final int countLinks(int p1, Class <? extends StoredObject > p2, String p3) {
        return 0;
    }

    public final int countLinks(Transaction p1, int p2, Class <? extends StoredObject > p3, String p4) {
        return 0;
    }

    public final int countLinks(String p1, Class <? extends StoredObject > p2) {
        return 0;
    }

    public final int countLinks(Transaction p1, String p2, Class <? extends StoredObject > p3) {
        return 0;
    }

    public final int countLinks(String p1, Class <? extends StoredObject > p2, String p3) {
        return 0;
    }

    public final int countLinks(Transaction p1, String p2, Class <? extends StoredObject > p3, String p4) {
        return 0;
    }

    public final int countLinks(Class <? extends StoredObject > p1, boolean p2) {
        return 0;
    }

    public final int countLinks(Transaction p1, Class <? extends StoredObject > p2, boolean p3) {
        return 0;
    }

    public final int countLinks(Class <? extends StoredObject > p1, String p2, boolean p3) {
        return 0;
    }

    public final int countLinks(Transaction p1, Class <? extends StoredObject > p2, String p3, boolean p4) {
        return 0;
    }

    public final int countLinks(int p1, Class <? extends StoredObject > p2, boolean p3) {
        return 0;
    }

    public final int countLinks(Transaction p1, int p2, Class <? extends StoredObject > p3, boolean p4) {
        return 0;
    }

    public final int countLinks(int p1, Class <? extends StoredObject > p2, String p3, boolean p4) {
        return 0;
    }

    public final int countLinks(Transaction p1, int p2, Class <? extends StoredObject > p3, String p4, boolean p5) {
        return 0;
    }

    public final int countLinks(String p1, Class <? extends StoredObject > p2, boolean p3) {
        return 0;
    }

    public final int countLinks(Transaction p1, String p2, Class <? extends StoredObject > p3, boolean p4) {
        return 0;
    }

    public final int countLinks(String p1, Class <? extends StoredObject > p2, String p3, boolean p4) {
        return 0;
    }

    public final int countLinks(Transaction p1, String p2, Class <? extends StoredObject > p3, String p4, boolean p5) {
        return 0;
    }

    public final < T extends StoredObject > ObjectIterator < T > listMasters(Class < T > p1) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listMasters(Transaction p1, Class < T > p2) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listMasters(Class < T > p1, String p2) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listMasters(Transaction p1, Class < T > p2, String p3) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listMasters(Class < T > p1, String p2, String p3) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listMasters(Transaction p1, Class < T > p2, String p3, String p4) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listMasters(int p1, Class < T > p2) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listMasters(Transaction p1, int p2, Class < T > p3) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listMasters(int p1, Class < T > p2, String p3) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listMasters(Transaction p1, int p2, Class < T > p3, String p4) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listMasters(int p1, Class < T > p2, String p3, String p4) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listMasters(Transaction p1, int p2, Class < T > p3, String p4, String p5) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listMasters(String p1, Class < T > p2) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listMasters(Transaction p1, String p2, Class < T > p3) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listMasters(String p1, Class < T > p2, String p3) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listMasters(Transaction p1, String p2, Class < T > p3, String p4) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listMasters(String p1, Class < T > p2, String p3, String p4) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listMasters(Transaction p1, String p2, Class < T > p3, String p4, String p5) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listMasters(Class < T > p1, boolean p2) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listMasters(Transaction p1, Class < T > p2, boolean p3) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listMasters(Class < T > p1, String p2, boolean p3) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listMasters(Transaction p1, Class < T > p2, String p3, boolean p4) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listMasters(Class < T > p1, String p2, String p3, boolean p4) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listMasters(Transaction p1, Class < T > p2, String p3, String p4, boolean p5) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listMasters(int p1, Class < T > p2, boolean p3) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listMasters(Transaction p1, int p2, Class < T > p3, boolean p4) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listMasters(int p1, Class < T > p2, String p3, boolean p4) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listMasters(Transaction p1, int p2, Class < T > p3, String p4, boolean p5) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listMasters(int p1, Class < T > p2, String p3, String p4, boolean p5) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listMasters(Transaction p1, int p2, Class < T > p3, String p4, String p5, boolean p6) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listMasters(String p1, Class < T > p2, boolean p3) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listMasters(Transaction p1, String p2, Class < T > p3, boolean p4) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listMasters(String p1, Class < T > p2, String p3, boolean p4) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listMasters(Transaction p1, String p2, Class < T > p3, String p4, boolean p5) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listMasters(String p1, Class < T > p2, String p3, String p4, boolean p5) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listMasters(Transaction p1, String p2, Class < T > p3, String p4, String p5, boolean p6) {
        return null;
    }

    public void setMaster(StoredObject master, int linkType) throws Exception {
    }

    public final < T extends StoredObject > T getMaster(Class < T > p1) {
        return null;
    }

    public final < T extends StoredObject > T getMaster(Transaction p1, Class < T > p2) {
        return null;
    }

    public final < T extends StoredObject > T getMaster(Class < T > p1, String p2) {
        return null;
    }

    public final < T extends StoredObject > T getMaster(Transaction p1, Class < T > p2, String p3) {
        return null;
    }

    public final < T extends StoredObject > T getMaster(Class < T > p1, String p2, String p3) {
        return null;
    }

    public final < T extends StoredObject > T getMaster(Transaction p1, Class < T > p2, String p3, String p4) {
        return null;
    }

    public final < T extends StoredObject > T getMaster(int p1, Class < T > p2) {
        return null;
    }

    public final < T extends StoredObject > T getMaster(Transaction p1, int p2, Class < T > p3) {
        return null;
    }

    public final < T extends StoredObject > T getMaster(int p1, Class < T > p2, String p3) {
        return null;
    }

    public final < T extends StoredObject > T getMaster(Transaction p1, int p2, Class < T > p3, String p4) {
        return null;
    }

    public final < T extends StoredObject > T getMaster(int p1, Class < T > p2, String p3, String p4) {
        return null;
    }

    public final < T extends StoredObject > T getMaster(Transaction p1, int p2, Class < T > p3, String p4, String p5) {
        return null;
    }

    public final < T extends StoredObject > T getMaster(String p1, Class < T > p2) {
        return null;
    }

    public final < T extends StoredObject > T getMaster(Transaction p1, String p2, Class < T > p3) {
        return null;
    }

    public final < T extends StoredObject > T getMaster(String p1, Class < T > p2, String p3) {
        return null;
    }

    public final < T extends StoredObject > T getMaster(Transaction p1, String p2, Class < T > p3, String p4) {
        return null;
    }

    public final < T extends StoredObject > T getMaster(String p1, Class < T > p2, String p3, String p4) {
        return null;
    }

    public final < T extends StoredObject > T getMaster(Transaction p1, String p2, Class < T > p3, String p4, String p5) {
        return null;
    }

    public final < T extends StoredObject > T getMaster(Class < T > p1, boolean p2) {
        return null;
    }

    public final < T extends StoredObject > T getMaster(Transaction p1, Class < T > p2, boolean p3) {
        return null;
    }

    public final < T extends StoredObject > T getMaster(Class < T > p1, String p2, boolean p3) {
        return null;
    }

    public final < T extends StoredObject > T getMaster(Transaction p1, Class < T > p2, String p3, boolean p4) {
        return null;
    }

    public final < T extends StoredObject > T getMaster(Class < T > p1, String p2, String p3, boolean p4) {
        return null;
    }

    public final < T extends StoredObject > T getMaster(Transaction p1, Class < T > p2, String p3, String p4, boolean p5) {
        return null;
    }

    public final < T extends StoredObject > T getMaster(int p1, Class < T > p2, boolean p3) {
        return null;
    }

    public final < T extends StoredObject > T getMaster(Transaction p1, int p2, Class < T > p3, boolean p4) {
        return null;
    }

    public final < T extends StoredObject > T getMaster(int p1, Class < T > p2, String p3, boolean p4) {
        return null;
    }

    public final < T extends StoredObject > T getMaster(Transaction p1, int p2, Class < T > p3, String p4, boolean p5) {
        return null;
    }

    public final < T extends StoredObject > T getMaster(int p1, Class < T > p2, String p3, String p4, boolean p5) {
        return null;
    }

    public final < T extends StoredObject > T getMaster(Transaction p1, int p2, Class < T > p3, String p4, String p5, boolean p6) {
        return null;
    }

    public final < T extends StoredObject > T getMaster(String p1, Class < T > p2, boolean p3) {
        return null;
    }

    public final < T extends StoredObject > T getMaster(Transaction p1, String p2, Class < T > p3, boolean p4) {
        return null;
    }

    public final < T extends StoredObject > T getMaster(String p1, Class < T > p2, String p3, boolean p4) {
        return null;
    }

    public final < T extends StoredObject > T getMaster(Transaction p1, String p2, Class < T > p3, String p4, boolean p5) {
        return null;
    }

    public final < T extends StoredObject > T getMaster(String p1, Class < T > p2, String p3, String p4, boolean p5) {
        return null;
    }

    public final < T extends StoredObject > T getMaster(Transaction p1, String p2, Class < T > p3, String p4, String p5, boolean p6) {
        return null;
    }

    public boolean existsMaster(StoredObject p1) {
        return false;
    }

    public boolean existsMaster(Id p1) {
        return false;
    }

    public boolean existsMasters() {
        return false;
    }

    public boolean existsMasters(Class <? extends StoredObject > p1) {
        return false;
    }

    public boolean existsMasters(Class <? extends StoredObject > p1, boolean p2) {
        return false;
    }

    public boolean existsMasters(Class <? extends StoredObject > p1, String p2) {
        return false;
    }

    public boolean existsMasters(Class <? extends StoredObject > p1, String p2, boolean p3) {
        return false;
    }

    public boolean existsMasters(String p1, Class <? extends StoredObject > p2) {
        return false;
    }

    public boolean existsMasters(String p1, Class <? extends StoredObject > p2, boolean p3) {
        return false;
    }

    public boolean existsMasters(String p1, Class <? extends StoredObject > p2, String p3) {
        return false;
    }

    public boolean existsMasters(String p1, Class <? extends StoredObject > p2, String p3, boolean p4) {
        return false;
    }

    public boolean existsMasters(int p1, Class <? extends StoredObject > p2) {
        return false;
    }

    public boolean existsMasters(int p1, Class <? extends StoredObject > p2, boolean p3) {
        return false;
    }

    public boolean existsMasters(int p1, Class <? extends StoredObject > p2, String p3) {
        return false;
    }

    public boolean existsMasters(int p1, Class <? extends StoredObject > p2, String p3, boolean p4) {
        return false;
    }

    public final Query queryMasters(Class <? extends StoredObject > p1, String p2) {
        return null;
    }

    public final Query queryMasters(Transaction p1, Class <? extends StoredObject > p2, String p3) {
        return null;
    }

    public final Query queryMasters(Class <? extends StoredObject > p1, String p2, String p3) {
        return null;
    }

    public final Query queryMasters(Transaction p1, Class <? extends StoredObject > p2, String p3, String p4) {
        return null;
    }

    public final Query queryMasters(Class <? extends StoredObject > p1, String p2, String p3, String p4) {
        return null;
    }

    public final Query queryMasters(Transaction p1, Class <? extends StoredObject > p2, String p3, String p4, String p5) {
        return null;
    }

    public final Query queryMasters(int p1, Class <? extends StoredObject > p2, String p3) {
        return null;
    }

    public final Query queryMasters(Transaction p1, int p2, Class <? extends StoredObject > p3, String p4) {
        return null;
    }

    public final Query queryMasters(int p1, Class <? extends StoredObject > p2, String p3, String p4) {
        return null;
    }

    public final Query queryMasters(Transaction p1, int p2, Class <? extends StoredObject > p3, String p4, String p5) {
        return null;
    }

    public final Query queryMasters(int p1, Class <? extends StoredObject > p2, String p3, String p4, String p5) {
        return null;
    }

    public final Query queryMasters(Transaction p1, int p2, Class <? extends StoredObject > p3, String p4, String p5, String p6) {
        return null;
    }

    public final Query queryMasters(String p1, Class <? extends StoredObject > p2, String p3) {
        return null;
    }

    public final Query queryMasters(Transaction p1, String p2, Class <? extends StoredObject > p3, String p4) {
        return null;
    }

    public final Query queryMasters(String p1, Class <? extends StoredObject > p2, String p3, String p4) {
        return null;
    }

    public final Query queryMasters(Transaction p1, String p2, Class <? extends StoredObject > p3, String p4, String p5) {
        return null;
    }

    public final Query queryMasters(String p1, Class <? extends StoredObject > p2, String p3, String p4, String p5) {
        return null;
    }

    public final Query queryMasters(Transaction p1, String p2, Class <? extends StoredObject > p3, String p4, String p5, String p6) {
        return null;
    }

    public final Query queryMasters(Class <? extends StoredObject > p1, String p2, boolean p3) {
        return null;
    }

    public final Query queryMasters(Transaction p1, Class <? extends StoredObject > p2, String p3, boolean p4) {
        return null;
    }

    public final Query queryMasters(Class <? extends StoredObject > p1, String p2, String p3, boolean p4) {
        return null;
    }

    public final Query queryMasters(Transaction p1, Class <? extends StoredObject > p2, String p3, String p4, boolean p5) {
        return null;
    }

    public final Query queryMasters(Class <? extends StoredObject > p1, String p2, String p3, String p4, boolean p5) {
        return null;
    }

    public final Query queryMasters(Transaction p1, Class <? extends StoredObject > p2, String p3, String p4, String p5, boolean p6) {
        return null;
    }

    public final Query queryMasters(int p1, Class <? extends StoredObject > p2, String p3, boolean p4) {
        return null;
    }

    public final Query queryMasters(Transaction p1, int p2, Class <? extends StoredObject > p3, String p4, boolean p5) {
        return null;
    }

    public final Query queryMasters(int p1, Class <? extends StoredObject > p2, String p3, String p4, boolean p5) {
        return null;
    }

    public final Query queryMasters(Transaction p1, int p2, Class <? extends StoredObject > p3, String p4, String p5, boolean p6) {
        return null;
    }

    public final Query queryMasters(int p1, Class <? extends StoredObject > p2, String p3, String p4, String p5, boolean p6) {
        return null;
    }

    public final Query queryMasters(Transaction p1, int p2, Class <? extends StoredObject > p3, String p4, String p5, String p6, boolean p7) {
        return null;
    }

    public final Query queryMasters(String p1, Class <? extends StoredObject > p2, String p3, boolean p4) {
        return null;
    }

    public final Query queryMasters(Transaction p1, String p2, Class <? extends StoredObject > p3, String p4, boolean p5) {
        return null;
    }

    public final Query queryMasters(String p1, Class <? extends StoredObject > p2, String p3, String p4, boolean p5) {
        return null;
    }

    public final Query queryMasters(Transaction p1, String p2, Class <? extends StoredObject > p3, String p4, String p5, boolean p6) {
        return null;
    }

    public final Query queryMasters(String p1, Class <? extends StoredObject > p2, String p3, String p4, String p5, boolean p6) {
        return null;
    }

    public final Query queryMasters(Transaction p1, String p2, Class <? extends StoredObject > p3, String p4, String p5, String p6, boolean p7) {
        return null;
    }

    public final int countMasters(Class <? extends StoredObject > p1) {
        return 0;
    }

    public final int countMasters(Transaction p1, Class <? extends StoredObject > p2) {
        return 0;
    }

    public final int countMasters(Class <? extends StoredObject > p1, String p2) {
        return 0;
    }

    public final int countMasters(Transaction p1, Class <? extends StoredObject > p2, String p3) {
        return 0;
    }

    public final int countMasters(int p1, Class <? extends StoredObject > p2) {
        return 0;
    }

    public final int countMasters(Transaction p1, int p2, Class <? extends StoredObject > p3) {
        return 0;
    }

    public final int countMasters(int p1, Class <? extends StoredObject > p2, String p3) {
        return 0;
    }

    public final int countMasters(Transaction p1, int p2, Class <? extends StoredObject > p3, String p4) {
        return 0;
    }

    public final int countMasters(String p1, Class <? extends StoredObject > p2) {
        return 0;
    }

    public final int countMasters(Transaction p1, String p2, Class <? extends StoredObject > p3) {
        return 0;
    }

    public final int countMasters(String p1, Class <? extends StoredObject > p2, String p3) {
        return 0;
    }

    public final int countMasters(Transaction p1, String p2, Class <? extends StoredObject > p3, String p4) {
        return 0;
    }

    public final int countMasters(Class <? extends StoredObject > p1, boolean p2) {
        return 0;
    }

    public final int countMasters(Transaction p1, Class <? extends StoredObject > p2, boolean p3) {
        return 0;
    }

    public final int countMasters(Class <? extends StoredObject > p1, String p2, boolean p3) {
        return 0;
    }

    public final int countMasters(Transaction p1, Class <? extends StoredObject > p2, String p3, boolean p4) {
        return 0;
    }

    public final int countMasters(int p1, Class <? extends StoredObject > p2, boolean p3) {
        return 0;
    }

    public final int countMasters(Transaction p1, int p2, Class <? extends StoredObject > p3, boolean p4) {
        return 0;
    }

    public final int countMasters(int p1, Class <? extends StoredObject > p2, String p3, boolean p4) {
        return 0;
    }

    public final int countMasters(Transaction p1, int p2, Class <? extends StoredObject > p3, String p4, boolean p5) {
        return 0;
    }

    public final int countMasters(String p1, Class <? extends StoredObject > p2, boolean p3) {
        return 0;
    }

    public final int countMasters(Transaction p1, String p2, Class <? extends StoredObject > p3, boolean p4) {
        return 0;
    }

    public final int countMasters(String p1, Class <? extends StoredObject > p2, String p3, boolean p4) {
        return 0;
    }

    public final int countMasters(Transaction p1, String p2, Class <? extends StoredObject > p3, String p4, boolean p5) {
        return 0;
    }

    public final Id getAttachmentId(String name) {
        return null;
    }

    public final StreamData getAttachment(String name) {
        return null;
    }

    public final FileData getFileData(String name) {
        return null;
    }
    
	public final FileData getFileData(String name, Transaction transaction) {
		return null;
	}

    public final ObjectIterator < FileData > listFileData() {
        return null;
    }
    
	public final ObjectIterator<FileData> listFileData(Transaction transaction) {
		return null;
	}
}
