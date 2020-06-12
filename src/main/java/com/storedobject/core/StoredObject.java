package com.storedobject.core;

import com.storedobject.common.StringList;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public abstract class StoredObject {

    public static java.util.logging.Logger logger;

    public StoredObject() {
    }

    protected static < T extends StoredObject > T get(Transaction transaction, Class < T > p2, java.sql.ResultSet p3) {
        return null;
    }

    public static StoredObject get(Id p1) {
        return null;
    }

    public static StoredObject get(Transaction transaction, Id p2) {
        return null;
    }

    public static < T extends StoredObject > T get(Class < T > p1, Id p2) {
        return null;
    }

    public static < T extends StoredObject > T get(Transaction transaction, Class < T > p2, Id p3) {
        return null;
    }

    public static < T extends StoredObject > T get(Class < T > p1) {
        return null;
    }

    public static < T extends StoredObject > T get(Class < T > p1, String p2) {
        return null;
    }

    public static < T extends StoredObject > T get(Transaction transaction, Class < T > p2, String p3) {
        return null;
    }

    public static < T extends StoredObject > T get(Class < T > p1, String p2, String p3) {
        return null;
    }

    public static < T extends StoredObject > T get(Transaction transaction, Class < T > p2, String p3, String p4) {
        return null;
    }

	public static <T extends StoredObject> T get(ObjectIterator<T> iterator) {
		return null;
	}

	public static <T extends StoredObject> T get(ObjectIterator<T> list, boolean validateOne) {
		return null;
	}

    protected static < T extends StoredObject > T get(Transaction transaction, ClassAttribute < T > p2, String p3, String p4) {
        return null;
    }

    public static < T extends StoredObject, C extends T > C get(Class < T > p1, Id p2, boolean p3) {
        return null;
    }

    public static < T extends StoredObject, C extends T > C get(Transaction transaction, Class < T > p2, Id p3, boolean p4) {
        return null;
    }

    public static < T extends StoredObject, C extends T > C get(Class < T > p1, boolean p2) {
        return null;
    }

    public static < T extends StoredObject, C extends T > C get(Class < T > p1, String p2, boolean p3) {
        return null;
    }

    public static < T extends StoredObject, C extends T > C get(Transaction transaction, Class < T > p2, String p3, boolean p4) {
        return null;
    }

    public static < T extends StoredObject, C extends T > C get(Class < T > p1, String p2, String p3, boolean p4) {
        return null;
    }

    public static < T extends StoredObject, C extends T > C get(Transaction transaction, Class < T > p2, String p3, String p4, boolean p5) {
        return null;
    }

    @Override
	public final boolean equals(Object another) {
        return false;
    }

    @Override
    public final int hashCode() {
        return getId().hashCode();
    }

    public static int count(Class<? extends StoredObject> soClass) {
        return 0;
    }

    public static int count(Class<? extends StoredObject> soClass, String condition) {
        return 0;
    }

    public static int count(Transaction transaction, Class<? extends StoredObject> soClass, String condition) {
        return 0;
    }

    public static int count(Class<? extends StoredObject> soClass, boolean p2) {
        return 0;
    }

    public static int count(Class<? extends StoredObject> soClass, String condition, boolean p3) {
        return 0;
    }

    public static int count(Transaction transaction, Class<? extends StoredObject> soClass, String condition, boolean p4) {
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

    public final void checkForDuplicate(String... attributes) throws Invalid_State {
    }

    public void load(BufferedReader reader) throws Exception {
    }

    public static int load(TransactionManager tm, InputStream in, Comparator< CharSequence > comparator) throws Exception {
        return 0;
    }

    public static int load(TransactionManager tm, Reader reader, Comparator < CharSequence > comparator) throws Exception {
        return 0;
    }

    public final java.sql.Timestamp timestamp() {
        return null;
    }

    public final Id getId() {
        return Id.ZERO;
    }

    public static < T extends StoredObject > ObjectIterator < T > list(Class < T > p1) {
        return null;
    }

    public static < T extends StoredObject > ObjectIterator < T > list(Class < T > p1, String p2) {
        return null;
    }

    public static < T extends StoredObject > ObjectIterator < T > list(Transaction transaction, Class < T > p2, String p3) {
        return null;
    }

    public static < T extends StoredObject > ObjectIterator < T > list(Class < T > p1, String p2, String p3) {
        return null;
    }

    public static < T extends StoredObject > ObjectIterator < T > list(Transaction transaction, Class < T > p2, String p3, String p4) {
        return null;
    }

    public static < T extends StoredObject > ObjectIterator < T > list(Class < T > p1, boolean p2) {
        return null;
    }

    public static < T extends StoredObject > ObjectIterator < T > list(Class < T > p1, String p2, boolean p3) {
        return null;
    }

    public static < T extends StoredObject > ObjectIterator < T > list(Transaction transaction, Class < T > p2, String p3, boolean p4) {
        return null;
    }

    public static < T extends StoredObject > ObjectIterator < T > list(Class < T > p1, String p2, String p3, boolean p4) {
        return null;
    }

    public static < T extends StoredObject > ObjectIterator < T > list(Transaction transaction, Class < T > p2, String p3, String p4, boolean p5) {
        return null;
    }

    public final void save(Transaction transaction) throws Exception {
    }

    public final void save() throws Exception {
    }

    public final boolean save(TransactionControl transactionControl) {
        return false;
    }

    public final void save(Writer writer) throws Exception {
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

    public final void delete(Transaction transaction) throws Exception {
    }

    public final void delete() throws Exception {
    }

    public final boolean delete(TransactionControl p1) {
        return false;
    }

    public static StoredObject create(BufferedReader p1) throws Exception {
        return null;
    }
    
	public static <T extends StoredObject> ObjectIterator<T> list(Transaction transaction, ClassAttribute<T> ca, String condition, String order) {
		return null;
	}

    public static boolean exists(Query p1) {
        return false;
    }

    public static boolean exists(Class<? extends StoredObject> soClass, String condition) {
        return false;
    }

    public static boolean exists(Class<? extends StoredObject> soClass, String condition, boolean p3) {
        return false;
    }

    public static boolean exists(Transaction transaction, Class<? extends StoredObject> soClass, String condition) {
        return false;
    }

    public static boolean exists(Transaction transaction, Class<? extends StoredObject> soClass, String condition, boolean p4) {
        return false;
    }

    public static Query query(Class<? extends StoredObject> soClass, String condition) {
        return null;
    }

    public static Query query(Class<? extends StoredObject> soClass, String condition, String p3) {
        return null;
    }

    public static Query query(Transaction transaction, Class<? extends StoredObject> soClass, String condition, String p4) {
        return null;
    }

    public static Query query(Class<? extends StoredObject> soClass, String condition, String p3, String p4) {
        return null;
    }

    public static Query query(Transaction transaction, Class<? extends StoredObject> soClass, String condition, String p4, String p5) {
        return null;
    }

    public static Query query(Class<? extends StoredObject> soClass, String condition, boolean p3) {
        return null;
    }

    public static Query query(Class<? extends StoredObject> soClass, String condition, String p3, boolean p4) {
        return null;
    }

    public static Query query(Transaction transaction, Class<? extends StoredObject> soClass, String condition, String p4, boolean p5) {
        return null;
    }

    public static Query query(Class<? extends StoredObject> soClass, String condition, String p3, String p4, boolean p5) {
        return null;
    }

    public static Query query(Transaction transaction, Class<? extends StoredObject> soClass, String condition, String p4, String p5, boolean p6) {
        return null;
    }

    public final int family() {
        return 0;
    }

    public static <O extends StoredObject> int family(Class<O> objectClass) {
        return -1;
    }

    public static int family(Id id) {
        return -1;
    }

    public Transaction getTransaction() {
        return null;
    }

    public final boolean loading() {
        return false;
    }

    public final void checkTransaction() throws Exception {
    }

    public void validateData(TransactionManager tm) throws Exception {
    }

    public final boolean inserted() {
        return false;
    }

    public final boolean saving() {
        return false;
    }

    public final Id getTransactionId() {
        return null;
    }

    public final String getTransactionIP() {
        return null;
    }

    public final boolean isVirtual() {
        return false;
    }

    public final boolean makeVirtual() {
        return false;
    }

    public void setTransaction(Transaction transaction) throws Exception {
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

    public final void removeAllLinks(Transaction transaction) throws Exception {
    }

    public final void removeAllLinks(int p1) throws Exception {
    }

    public final void removeAllLinks(Transaction transaction, int p2) throws Exception {
    }

    public final void removeAllLinks(String p1) throws Exception {
    }

    public final void removeAllLinks(Transaction transaction, String p2) throws Exception {
    }

    public final void removeAllLinks(Class<? extends StoredObject> soClass) throws Exception {
    }

    public final void removeAllLinks(Transaction transaction, Class<? extends StoredObject> soClass) throws Exception {
    }

    public final void removeAllLinks(Class<? extends StoredObject> soClass, int p2) throws Exception {
    }

    public final void removeAllLinks(Transaction transaction, Class<? extends StoredObject> soClass, int p3) throws Exception {
    }

    public final void removeAllLinks(Class<? extends StoredObject> soClass, String condition) throws Exception {
    }

    public final void removeAllLinks(Transaction transaction, Class<? extends StoredObject> soClass, String condition) throws Exception {
    }

    public final void removeReverseLinks() throws Exception {
    }

    public final void removeReverseLinks(Transaction transaction) throws Exception {
    }

    public final void undelete(Transaction transaction) throws Exception {
    }

    public final void undelete() throws Exception {
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

    protected final String historyString() throws Exception {
        return null;
    }

    public void loaded() {
    }

    public final void addLink(StoredObject p1) throws Exception {
    }

    public final void addLink(StoredObject p1, int p2) throws Exception {
    }

    public final void addLink(Transaction transaction, StoredObject p2) throws Exception {
    }

    public final void addLink(Transaction transaction, StoredObject p2, int p3) throws Exception {
    }

    public final void addLink(Id p1) throws Exception {
    }

    public final void addLink(Id p1, int p2) throws Exception {
    }

    public final void addLink(Transaction transaction, Id p2) throws Exception {
    }

    public final void addLink(Transaction transaction, Id p2, int p3) throws Exception {
    }

    public final void removeLink(StoredObject p1) throws Exception {
    }

    public final void removeLink(Transaction transaction, StoredObject p2) throws Exception {
    }

    public final void removeLink(Id p1) throws Exception {
    }

    public final void removeLink(Transaction transaction, Id p2) throws Exception {
    }

    public final void removeLink(StoredObject p1, int p2) throws Exception {
    }

    public final void removeLink(Transaction transaction, StoredObject p2, int p3) throws Exception {
    }

    public final void removeLink(Id p1, int p2) throws Exception {
    }

    public final void removeLink(Transaction transaction, Id p2, int p3) throws Exception {
    }

    public final void removeLink(StoredObject p1, String p2) throws Exception {
    }

    public final void removeLink(Transaction transaction, StoredObject p2, String p3) throws Exception {
    }

    public final void removeLink(Id p1, String p2) throws Exception {
    }

    public final void removeLink(Transaction transaction, Id p2, String p3) throws Exception {
    }

    public static String checkCurrency(String p1) throws Invalid_Value {
        return null;
    }

    public final Id getParentId() {
        return null;
    }

    public final Id getParentId(Class<? extends StoredObject> soClass) {
        return null;
    }

    public final Id getParentId(Class<? extends StoredObject> soClass, int p2) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > getChildren() {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listLinks(Class < T > p1) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listLinks(Transaction transaction, Class < T > p2) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listLinks(Class < T > p1, String p2) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listLinks(Transaction transaction, Class < T > p2, String p3) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listLinks(Class < T > p1, String p2, String p3) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listLinks(Transaction transaction, Class < T > p2, String p3, String p4) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listLinks(int p1, Class < T > p2) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listLinks(Transaction transaction, int p2, Class < T > p3) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listLinks(int p1, Class < T > p2, String p3) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listLinks(Transaction transaction, int p2, Class < T > p3, String p4) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listLinks(int p1, Class < T > p2, String p3, String p4) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listLinks(Transaction transaction, int p2, Class < T > p3, String p4, String p5) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listLinks(String p1, Class < T > p2) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listLinks(Transaction transaction, String p2, Class < T > p3) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listLinks(String p1, Class < T > p2, String p3) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listLinks(Transaction transaction, String p2, Class < T > p3, String p4) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listLinks(String p1, Class < T > p2, String p3, String p4) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listLinks(Transaction transaction, String p2, Class < T > p3, String p4, String p5) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listLinks(Class < T > p1, boolean p2) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listLinks(Transaction transaction, Class < T > p2, boolean p3) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listLinks(Class < T > p1, String p2, boolean p3) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listLinks(Transaction transaction, Class < T > p2, String p3, boolean p4) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listLinks(Class < T > p1, String p2, String p3, boolean p4) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listLinks(Transaction transaction, Class < T > p2, String p3, String p4, boolean p5) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listLinks(int p1, Class < T > p2, boolean p3) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listLinks(Transaction transaction, int p2, Class < T > p3, boolean p4) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listLinks(int p1, Class < T > p2, String p3, boolean p4) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listLinks(Transaction transaction, int p2, Class < T > p3, String p4, boolean p5) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listLinks(int p1, Class < T > p2, String p3, String p4, boolean p5) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listLinks(Transaction transaction, int p2, Class < T > p3, String p4, String p5, boolean p6) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listLinks(String p1, Class < T > p2, boolean p3) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listLinks(Transaction transaction, String p2, Class < T > p3, boolean p4) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listLinks(String p1, Class < T > p2, String p3, boolean p4) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listLinks(Transaction transaction, String p2, Class < T > p3, String p4, boolean p5) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listLinks(String p1, Class < T > p2, String p3, String p4, boolean p5) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listLinks(Transaction transaction, String p2, Class < T > p3, String p4, String p5, boolean p6) {
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

    public static < T extends StoredObject > ObjectIterator < T > listViaQuery(Class < T > p1, Query p2) {
        return null;
    }

    public static < T extends StoredObject > ObjectIterator < T > listViaQuery(Transaction transaction, Class < T > p2, Query p3) {
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

    public boolean existsLinks(Class<? extends StoredObject> soClass) {
        return false;
    }

    public boolean existsLinks(Class<? extends StoredObject> soClass, boolean p2) {
        return false;
    }

    public boolean existsLinks(Class<? extends StoredObject> soClass, String condition) {
        return false;
    }

    public boolean existsLinks(Class<? extends StoredObject> soClass, String condition, boolean p3) {
        return false;
    }

    public boolean existsLinks(String p1, Class<? extends StoredObject> soClass) {
        return false;
    }

    public boolean existsLinks(String p1, Class<? extends StoredObject> soClass, boolean p3) {
        return false;
    }

    public boolean existsLinks(String p1, Class<? extends StoredObject> soClass, String condition) {
        return false;
    }

    public boolean existsLinks(String p1, Class<? extends StoredObject> soClass, String condition, boolean p4) {
        return false;
    }

    public boolean existsLinks(int p1, Class<? extends StoredObject> soClass) {
        return false;
    }

    public boolean existsLinks(int p1, Class<? extends StoredObject> soClass, boolean p3) {
        return false;
    }

    public boolean existsLinks(int p1, Class<? extends StoredObject> soClass, String condition) {
        return false;
    }

    public boolean existsLinks(int p1, Class<? extends StoredObject> soClass, String condition, boolean p4) {
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

    public final Query queryLinks(Class<? extends StoredObject> soClass, String condition) {
        return null;
    }

    public final Query queryLinks(Transaction transaction, Class<? extends StoredObject> soClass, String condition) {
        return null;
    }

    public final Query queryLinks(Class<? extends StoredObject> soClass, String condition, String p3) {
        return null;
    }

    public final Query queryLinks(Transaction transaction, Class<? extends StoredObject> soClass, String condition, String p4) {
        return null;
    }

    public final Query queryLinks(Class<? extends StoredObject> soClass, String condition, String p3, String p4) {
        return null;
    }

    public final Query queryLinks(Transaction transaction, Class<? extends StoredObject> soClass, String condition, String p4, String p5) {
        return null;
    }

    public final Query queryLinks(int p1, Class<? extends StoredObject> soClass, String condition) {
        return null;
    }

    public final Query queryLinks(Transaction transaction, int p2, Class<? extends StoredObject> soClass, String condition) {
        return null;
    }

    public final Query queryLinks(int p1, Class<? extends StoredObject> soClass, String condition, String p4) {
        return null;
    }

    public final Query queryLinks(Transaction transaction, int p2, Class<? extends StoredObject> soClass, String condition, String p5) {
        return null;
    }

    public final Query queryLinks(int p1, Class<? extends StoredObject> soClass, String condition, String p4, String p5) {
        return null;
    }

    public final Query queryLinks(Transaction transaction, int p2, Class<? extends StoredObject> soClass, String condition, String p5, String p6) {
        return null;
    }

    public final Query queryLinks(String p1, Class<? extends StoredObject> soClass, String condition) {
        return null;
    }

    public final Query queryLinks(Transaction transaction, String p2, Class<? extends StoredObject> soClass, String condition) {
        return null;
    }

    public final Query queryLinks(String p1, Class<? extends StoredObject> soClass, String condition, String p4) {
        return null;
    }

    public final Query queryLinks(Transaction transaction, String p2, Class<? extends StoredObject> soClass, String condition, String p5) {
        return null;
    }

    public final Query queryLinks(String p1, Class<? extends StoredObject> soClass, String condition, String p4, String p5) {
        return null;
    }

    public final Query queryLinks(Transaction transaction, String p2, Class<? extends StoredObject> soClass, String condition, String p5, String p6) {
        return null;
    }

    public final Query queryLinks(Class<? extends StoredObject> soClass, String condition, boolean p3) {
        return null;
    }

    public final Query queryLinks(Transaction transaction, Class<? extends StoredObject> soClass, String condition, boolean p4) {
        return null;
    }

    public final Query queryLinks(Class<? extends StoredObject> soClass, String condition, String p3, boolean p4) {
        return null;
    }

    public final Query queryLinks(Transaction transaction, Class<? extends StoredObject> soClass, String condition, String p4, boolean p5) {
        return null;
    }

    public final Query queryLinks(Class<? extends StoredObject> soClass, String condition, String p3, String p4, boolean p5) {
        return null;
    }

    public final Query queryLinks(Transaction transaction, Class<? extends StoredObject> soClass, String condition, String p4, String p5, boolean p6) {
        return null;
    }

    public final Query queryLinks(int p1, Class<? extends StoredObject> soClass, String condition, boolean p4) {
        return null;
    }

    public final Query queryLinks(Transaction transaction, int p2, Class<? extends StoredObject> soClass, String condition, boolean p5) {
        return null;
    }

    public final Query queryLinks(int p1, Class<? extends StoredObject> soClass, String condition, String p4, boolean p5) {
        return null;
    }

    public final Query queryLinks(Transaction transaction, int p2, Class<? extends StoredObject> soClass, String condition, String p5, boolean p6) {
        return null;
    }

    public final Query queryLinks(int p1, Class<? extends StoredObject> soClass, String condition, String p4, String p5, boolean p6) {
        return null;
    }

    public final Query queryLinks(Transaction transaction, int p2, Class<? extends StoredObject> soClass, String condition, String p5, String p6, boolean p7) {
        return null;
    }

    public final Query queryLinks(String p1, Class<? extends StoredObject> soClass, String condition, boolean p4) {
        return null;
    }

    public final Query queryLinks(Transaction transaction, String p2, Class<? extends StoredObject> soClass, String condition, boolean p5) {
        return null;
    }

    public final Query queryLinks(String p1, Class<? extends StoredObject> soClass, String condition, String p4, boolean p5) {
        return null;
    }

    public final Query queryLinks(Transaction transaction, String p2, Class<? extends StoredObject> soClass, String condition, String p5, boolean p6) {
        return null;
    }

    public final Query queryLinks(String p1, Class<? extends StoredObject> soClass, String condition, String p4, String p5, boolean p6) {
        return null;
    }

    public final Query queryLinks(Transaction transaction, String p2, Class<? extends StoredObject> soClass, String condition, String p5, String p6, boolean p7) {
        return null;
    }

    public final int countLinks(Class<? extends StoredObject> soClass) {
        return 0;
    }

    public final int countLinks(Transaction transaction, Class<? extends StoredObject> soClass) {
        return 0;
    }

    public final int countLinks(Class<? extends StoredObject> soClass, String condition) {
        return 0;
    }

    public final int countLinks(Transaction transaction, Class<? extends StoredObject> soClass, String condition) {
        return 0;
    }

    public final int countLinks(int p1, Class<? extends StoredObject> soClass) {
        return 0;
    }

    public final int countLinks(Transaction transaction, int p2, Class<? extends StoredObject> soClass) {
        return 0;
    }

    public final int countLinks(int p1, Class<? extends StoredObject> soClass, String condition) {
        return 0;
    }

    public final int countLinks(Transaction transaction, int p2, Class<? extends StoredObject> soClass, String condition) {
        return 0;
    }

    public final int countLinks(String p1, Class<? extends StoredObject> soClass) {
        return 0;
    }

    public final int countLinks(Transaction transaction, String p2, Class<? extends StoredObject> soClass) {
        return 0;
    }

    public final int countLinks(String p1, Class<? extends StoredObject> soClass, String condition) {
        return 0;
    }

    public final int countLinks(Transaction transaction, String p2, Class<? extends StoredObject> soClass, String condition) {
        return 0;
    }

    public final int countLinks(Class<? extends StoredObject> soClass, boolean p2) {
        return 0;
    }

    public final int countLinks(Transaction transaction, Class<? extends StoredObject> soClass, boolean p3) {
        return 0;
    }

    public final int countLinks(Class<? extends StoredObject> soClass, String condition, boolean p3) {
        return 0;
    }

    public final int countLinks(Transaction transaction, Class<? extends StoredObject> soClass, String condition, boolean p4) {
        return 0;
    }

    public final int countLinks(int p1, Class<? extends StoredObject> soClass, boolean p3) {
        return 0;
    }

    public final int countLinks(Transaction transaction, int p2, Class<? extends StoredObject> soClass, boolean p4) {
        return 0;
    }

    public final int countLinks(int p1, Class<? extends StoredObject> soClass, String condition, boolean p4) {
        return 0;
    }

    public final int countLinks(Transaction transaction, int p2, Class<? extends StoredObject> soClass, String condition, boolean p5) {
        return 0;
    }

    public final int countLinks(String p1, Class<? extends StoredObject> soClass, boolean p3) {
        return 0;
    }

    public final int countLinks(Transaction transaction, String p2, Class<? extends StoredObject> soClass, boolean p4) {
        return 0;
    }

    public final int countLinks(String p1, Class<? extends StoredObject> soClass, String condition, boolean p4) {
        return 0;
    }

    public final int countLinks(Transaction transaction, String p2, Class<? extends StoredObject> soClass, String condition, boolean p5) {
        return 0;
    }

    public final < T extends StoredObject > ObjectIterator < T > listMasters(Class < T > p1) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listMasters(Transaction transaction, Class < T > p2) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listMasters(Class < T > p1, String p2) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listMasters(Transaction transaction, Class < T > p2, String p3) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listMasters(Class < T > p1, String p2, String p3) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listMasters(Transaction transaction, Class < T > p2, String p3, String p4) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listMasters(int p1, Class < T > p2) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listMasters(Transaction transaction, int p2, Class < T > p3) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listMasters(int p1, Class < T > p2, String p3) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listMasters(Transaction transaction, int p2, Class < T > p3, String p4) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listMasters(int p1, Class < T > p2, String p3, String p4) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listMasters(Transaction transaction, int p2, Class < T > p3, String p4, String p5) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listMasters(String p1, Class < T > p2) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listMasters(Transaction transaction, String p2, Class < T > p3) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listMasters(String p1, Class < T > p2, String p3) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listMasters(Transaction transaction, String p2, Class < T > p3, String p4) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listMasters(String p1, Class < T > p2, String p3, String p4) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listMasters(Transaction transaction, String p2, Class < T > p3, String p4, String p5) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listMasters(Class < T > p1, boolean p2) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listMasters(Transaction transaction, Class < T > p2, boolean p3) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listMasters(Class < T > p1, String p2, boolean p3) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listMasters(Transaction transaction, Class < T > p2, String p3, boolean p4) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listMasters(Class < T > p1, String p2, String p3, boolean p4) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listMasters(Transaction transaction, Class < T > p2, String p3, String p4, boolean p5) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listMasters(int p1, Class < T > p2, boolean p3) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listMasters(Transaction transaction, int p2, Class < T > p3, boolean p4) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listMasters(int p1, Class < T > p2, String p3, boolean p4) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listMasters(Transaction transaction, int p2, Class < T > p3, String p4, boolean p5) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listMasters(int p1, Class < T > p2, String p3, String p4, boolean p5) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listMasters(Transaction transaction, int p2, Class < T > p3, String p4, String p5, boolean p6) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listMasters(String p1, Class < T > p2, boolean p3) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listMasters(Transaction transaction, String p2, Class < T > p3, boolean p4) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listMasters(String p1, Class < T > p2, String p3, boolean p4) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listMasters(Transaction transaction, String p2, Class < T > p3, String p4, boolean p5) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listMasters(String p1, Class < T > p2, String p3, String p4, boolean p5) {
        return null;
    }

    public final < T extends StoredObject > ObjectIterator < T > listMasters(Transaction transaction, String p2, Class < T > p3, String p4, String p5, boolean p6) {
        return null;
    }

    public void setMaster(StoredObject master, int linkType) throws Exception {
    }

    public final < T extends StoredObject > T getMaster(Class < T > p1) {
        return null;
    }

    public final < T extends StoredObject > T getMaster(Transaction transaction, Class < T > p2) {
        return null;
    }

    public final < T extends StoredObject > T getMaster(Class < T > p1, String p2) {
        return null;
    }

    public final < T extends StoredObject > T getMaster(Transaction transaction, Class < T > p2, String p3) {
        return null;
    }

    public final < T extends StoredObject > T getMaster(Class < T > p1, String p2, String p3) {
        return null;
    }

    public final < T extends StoredObject > T getMaster(Transaction transaction, Class < T > p2, String p3, String p4) {
        return null;
    }

    public final < T extends StoredObject > T getMaster(int p1, Class < T > p2) {
        return null;
    }

    public final < T extends StoredObject > T getMaster(Transaction transaction, int p2, Class < T > p3) {
        return null;
    }

    public final < T extends StoredObject > T getMaster(int p1, Class < T > p2, String p3) {
        return null;
    }

    public final < T extends StoredObject > T getMaster(Transaction transaction, int p2, Class < T > p3, String p4) {
        return null;
    }

    public final < T extends StoredObject > T getMaster(int p1, Class < T > p2, String p3, String p4) {
        return null;
    }

    public final < T extends StoredObject > T getMaster(Transaction transaction, int p2, Class < T > p3, String p4, String p5) {
        return null;
    }

    public final < T extends StoredObject > T getMaster(String p1, Class < T > p2) {
        return null;
    }

    public final < T extends StoredObject > T getMaster(Transaction transaction, String p2, Class < T > p3) {
        return null;
    }

    public final < T extends StoredObject > T getMaster(String p1, Class < T > p2, String p3) {
        return null;
    }

    public final < T extends StoredObject > T getMaster(Transaction transaction, String p2, Class < T > p3, String p4) {
        return null;
    }

    public final < T extends StoredObject > T getMaster(String p1, Class < T > p2, String p3, String p4) {
        return null;
    }

    public final < T extends StoredObject > T getMaster(Transaction transaction, String p2, Class < T > p3, String p4, String p5) {
        return null;
    }

    public final < T extends StoredObject > T getMaster(Class < T > p1, boolean p2) {
        return null;
    }

    public final < T extends StoredObject > T getMaster(Transaction transaction, Class < T > p2, boolean p3) {
        return null;
    }

    public final < T extends StoredObject > T getMaster(Class < T > p1, String p2, boolean p3) {
        return null;
    }

    public final < T extends StoredObject > T getMaster(Transaction transaction, Class < T > p2, String p3, boolean p4) {
        return null;
    }

    public final < T extends StoredObject > T getMaster(Class < T > p1, String p2, String p3, boolean p4) {
        return null;
    }

    public final < T extends StoredObject > T getMaster(Transaction transaction, Class < T > p2, String p3, String p4, boolean p5) {
        return null;
    }

    public final < T extends StoredObject > T getMaster(int p1, Class < T > p2, boolean p3) {
        return null;
    }

    public final < T extends StoredObject > T getMaster(Transaction transaction, int p2, Class < T > p3, boolean p4) {
        return null;
    }

    public final < T extends StoredObject > T getMaster(int p1, Class < T > p2, String p3, boolean p4) {
        return null;
    }

    public final < T extends StoredObject > T getMaster(Transaction transaction, int p2, Class < T > p3, String p4, boolean p5) {
        return null;
    }

    public final < T extends StoredObject > T getMaster(int p1, Class < T > p2, String p3, String p4, boolean p5) {
        return null;
    }

    public final < T extends StoredObject > T getMaster(Transaction transaction, int p2, Class < T > p3, String p4, String p5, boolean p6) {
        return null;
    }

    public final < T extends StoredObject > T getMaster(String p1, Class < T > p2, boolean p3) {
        return null;
    }

    public final < T extends StoredObject > T getMaster(Transaction transaction, String p2, Class < T > p3, boolean p4) {
        return null;
    }

    public final < T extends StoredObject > T getMaster(String p1, Class < T > p2, String p3, boolean p4) {
        return null;
    }

    public final < T extends StoredObject > T getMaster(Transaction transaction, String p2, Class < T > p3, String p4, boolean p5) {
        return null;
    }

    public final < T extends StoredObject > T getMaster(String p1, Class < T > p2, String p3, String p4, boolean p5) {
        return null;
    }

    public final < T extends StoredObject > T getMaster(Transaction transaction, String p2, Class < T > p3, String p4, String p5, boolean p6) {
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

    public boolean existsMasters(Class<? extends StoredObject> soClass) {
        return false;
    }

    public boolean existsMasters(Class<? extends StoredObject> soClass, boolean p2) {
        return false;
    }

    public boolean existsMasters(Class<? extends StoredObject> soClass, String condition) {
        return false;
    }

    public boolean existsMasters(Class<? extends StoredObject> soClass, String condition, boolean p3) {
        return false;
    }

    public boolean existsMasters(String p1, Class<? extends StoredObject> soClass) {
        return false;
    }

    public boolean existsMasters(String p1, Class<? extends StoredObject> soClass, boolean p3) {
        return false;
    }

    public boolean existsMasters(String p1, Class<? extends StoredObject> soClass, String condition) {
        return false;
    }

    public boolean existsMasters(String p1, Class<? extends StoredObject> soClass, String condition, boolean p4) {
        return false;
    }

    public boolean existsMasters(int p1, Class<? extends StoredObject> soClass) {
        return false;
    }

    public boolean existsMasters(int p1, Class<? extends StoredObject> soClass, boolean p3) {
        return false;
    }

    public boolean existsMasters(int p1, Class<? extends StoredObject> soClass, String condition) {
        return false;
    }

    public boolean existsMasters(int p1, Class<? extends StoredObject> soClass, String condition, boolean p4) {
        return false;
    }

    public final Query queryMasters(Class<? extends StoredObject> soClass, String condition) {
        return null;
    }

    public final Query queryMasters(Transaction transaction, Class<? extends StoredObject> soClass, String condition) {
        return null;
    }

    public final Query queryMasters(Class<? extends StoredObject> soClass, String condition, String p3) {
        return null;
    }

    public final Query queryMasters(Transaction transaction, Class<? extends StoredObject> soClass, String condition, String p4) {
        return null;
    }

    public final Query queryMasters(Class<? extends StoredObject> soClass, String condition, String p3, String p4) {
        return null;
    }

    public final Query queryMasters(Transaction transaction, Class<? extends StoredObject> soClass, String condition, String p4, String p5) {
        return null;
    }

    public final Query queryMasters(int p1, Class<? extends StoredObject> soClass, String condition) {
        return null;
    }

    public final Query queryMasters(Transaction transaction, int p2, Class<? extends StoredObject> soClass, String condition) {
        return null;
    }

    public final Query queryMasters(int p1, Class<? extends StoredObject> soClass, String condition, String p4) {
        return null;
    }

    public final Query queryMasters(Transaction transaction, int p2, Class<? extends StoredObject> soClass, String condition, String p5) {
        return null;
    }

    public final Query queryMasters(int p1, Class<? extends StoredObject> soClass, String condition, String p4, String p5) {
        return null;
    }

    public final Query queryMasters(Transaction transaction, int p2, Class<? extends StoredObject> soClass, String condition, String p5, String p6) {
        return null;
    }

    public final Query queryMasters(String p1, Class<? extends StoredObject> soClass, String condition) {
        return null;
    }

    public final Query queryMasters(Transaction transaction, String p2, Class<? extends StoredObject> soClass, String condition) {
        return null;
    }

    public final Query queryMasters(String p1, Class<? extends StoredObject> soClass, String condition, String p4) {
        return null;
    }

    public final Query queryMasters(Transaction transaction, String p2, Class<? extends StoredObject> soClass, String condition, String p5) {
        return null;
    }

    public final Query queryMasters(String p1, Class<? extends StoredObject> soClass, String condition, String p4, String p5) {
        return null;
    }

    public final Query queryMasters(Transaction transaction, String p2, Class<? extends StoredObject> soClass, String condition, String p5, String p6) {
        return null;
    }

    public final Query queryMasters(Class<? extends StoredObject> soClass, String condition, boolean p3) {
        return null;
    }

    public final Query queryMasters(Transaction transaction, Class<? extends StoredObject> soClass, String condition, boolean p4) {
        return null;
    }

    public final Query queryMasters(Class<? extends StoredObject> soClass, String condition, String p3, boolean p4) {
        return null;
    }

    public final Query queryMasters(Transaction transaction, Class<? extends StoredObject> soClass, String condition, String p4, boolean p5) {
        return null;
    }

    public final Query queryMasters(Class<? extends StoredObject> soClass, String condition, String p3, String p4, boolean p5) {
        return null;
    }

    public final Query queryMasters(Transaction transaction, Class<? extends StoredObject> soClass, String condition, String p4, String p5, boolean p6) {
        return null;
    }

    public final Query queryMasters(int p1, Class<? extends StoredObject> soClass, String condition, boolean p4) {
        return null;
    }

    public final Query queryMasters(Transaction transaction, int p2, Class<? extends StoredObject> soClass, String condition, boolean p5) {
        return null;
    }

    public final Query queryMasters(int p1, Class<? extends StoredObject> soClass, String condition, String p4, boolean p5) {
        return null;
    }

    public final Query queryMasters(Transaction transaction, int p2, Class<? extends StoredObject> soClass, String condition, String p5, boolean p6) {
        return null;
    }

    public final Query queryMasters(int p1, Class<? extends StoredObject> soClass, String condition, String p4, String p5, boolean p6) {
        return null;
    }

    public final Query queryMasters(Transaction transaction, int p2, Class<? extends StoredObject> soClass, String condition, String p5, String p6, boolean p7) {
        return null;
    }

    public final Query queryMasters(String p1, Class<? extends StoredObject> soClass, String condition, boolean p4) {
        return null;
    }

    public final Query queryMasters(Transaction transaction, String p2, Class<? extends StoredObject> soClass, String condition, boolean p5) {
        return null;
    }

    public final Query queryMasters(String p1, Class<? extends StoredObject> soClass, String condition, String p4, boolean p5) {
        return null;
    }

    public final Query queryMasters(Transaction transaction, String p2, Class<? extends StoredObject> soClass, String condition, String p5, boolean p6) {
        return null;
    }

    public final Query queryMasters(String p1, Class<? extends StoredObject> soClass, String condition, String p4, String p5, boolean p6) {
        return null;
    }

    public final Query queryMasters(Transaction transaction, String p2, Class<? extends StoredObject> soClass, String condition, String p5, String p6, boolean p7) {
        return null;
    }

    public final int countMasters(Class<? extends StoredObject> soClass) {
        return 0;
    }

    public final int countMasters(Transaction transaction, Class<? extends StoredObject> soClass) {
        return 0;
    }

    public final int countMasters(Class<? extends StoredObject> soClass, String condition) {
        return 0;
    }

    public final int countMasters(Transaction transaction, Class<? extends StoredObject> soClass, String condition) {
        return 0;
    }

    public final int countMasters(int p1, Class<? extends StoredObject> soClass) {
        return 0;
    }

    public final int countMasters(Transaction transaction, int p2, Class<? extends StoredObject> soClass) {
        return 0;
    }

    public final int countMasters(int p1, Class<? extends StoredObject> soClass, String condition) {
        return 0;
    }

    public final int countMasters(Transaction transaction, int p2, Class<? extends StoredObject> soClass, String condition) {
        return 0;
    }

    public final int countMasters(String p1, Class<? extends StoredObject> soClass) {
        return 0;
    }

    public final int countMasters(Transaction transaction, String p2, Class<? extends StoredObject> soClass) {
        return 0;
    }

    public final int countMasters(String p1, Class<? extends StoredObject> soClass, String condition) {
        return 0;
    }

    public final int countMasters(Transaction transaction, String p2, Class<? extends StoredObject> soClass, String condition) {
        return 0;
    }

    public final int countMasters(Class<? extends StoredObject> soClass, boolean p2) {
        return 0;
    }

    public final int countMasters(Transaction transaction, Class<? extends StoredObject> soClass, boolean p3) {
        return 0;
    }

    public final int countMasters(Class<? extends StoredObject> soClass, String condition, boolean p3) {
        return 0;
    }

    public final int countMasters(Transaction transaction, Class<? extends StoredObject> soClass, String condition, boolean p4) {
        return 0;
    }

    public final int countMasters(int p1, Class<? extends StoredObject> soClass, boolean p3) {
        return 0;
    }

    public final int countMasters(Transaction transaction, int p2, Class<? extends StoredObject> soClass, boolean p4) {
        return 0;
    }

    public final int countMasters(int p1, Class<? extends StoredObject> soClass, String condition, boolean p4) {
        return 0;
    }

    public final int countMasters(Transaction transaction, int p2, Class<? extends StoredObject> soClass, String condition, boolean p5) {
        return 0;
    }

    public final int countMasters(String p1, Class<? extends StoredObject> soClass, boolean p3) {
        return 0;
    }

    public final int countMasters(Transaction transaction, String p2, Class<? extends StoredObject> soClass, boolean p4) {
        return 0;
    }

    public final int countMasters(String p1, Class<? extends StoredObject> soClass, String condition, boolean p4) {
        return 0;
    }

    public final int countMasters(Transaction transaction, String p2, Class<? extends StoredObject> soClass, String condition, boolean p5) {
        return 0;
    }

    public final Id getAttachmentId(String name) {
        return null;
    }

    public final StreamData getAttachment(String name) {
        return null;
    }

    public final <F extends FileData> F getFileData(String name) {
        return null;
    }
    
	public final <F extends FileData> F getFileData(String name, Transaction transaction) {
		return null;
	}

    public final ObjectIterator <? extends FileData> listFileData() {
        return null;
    }
    
	public final ObjectIterator<? extends FileData> listFileData(Transaction transaction) {
		return null;
	}

    public final boolean existsFileData() {
        return true;
    }

    public final List<StoredObjectLink<?>> objectLinks() {
        return null;
    }

    public final List<StoredObjectLink<?>> objectLinks(boolean create) {
        return null;
    }

    public final StoredObjectLink<?> objectLink(String name) {
        return null;
    }

    public final StoredObjectLink<?> objectLink(String name, boolean create) {
        return null;
    }

    public void clearObjectLinks() {
    }
}
