package com.storedobject.core;

import java.math.BigDecimal;
import java.math.BigInteger;

public final class Id {
	
	public final static Id ZERO = new Id(BigInteger.ZERO);

    public Id(BigInteger value) {
        this();
    }

    public Id(BigDecimal value) {
        this();
    }

    public Id(String value) {
        this();
    }

    public Id() {
    }

    public final java.math.BigInteger get() {
        return null;
    }

    public final com.storedobject.core.StoredObject getObject() {
        return null;
    }

    public final boolean isDummy() {
        return false;
    }

    public final boolean isNull() {
        return false;
    }

    public static boolean isNull(Id id) {
        return false;
    }

	public static boolean isNull(StoredObject object) {
		return false;
	}

	public final java.lang.Class <? extends com.storedobject.core.StoredObject > getObjectClass() {
        return null;
    }
    
	public final <T extends StoredObject> ObjectIterator<T> listLinks(Class<T> objectClass) {
        return null;
	}

	public final <T extends StoredObject> ObjectIterator<T> listLinks(Transaction transaction, Class<T> objectClass) {
        return null;
	}

	public final <T extends StoredObject> ObjectIterator<T> listLinks(Class<T> objectClass, String condition) {
        return null;
	}

	public final <T extends StoredObject> ObjectIterator<T> listLinks(Transaction transaction, Class<T> objectClass,
			String condition) {
        return null;
	}

	public final <T extends StoredObject> ObjectIterator<T> listLinks(Class<T> objectClass, String condition, String order) {
        return null;
	}

	public final <T extends StoredObject> ObjectIterator<T> listLinks(Transaction transaction, Class<T> objectClass,
			String condition, String order) {
        return null;
	}

	public final <T extends StoredObject> ObjectIterator<T> listLinks(int linkType, Class<T> objectClass) {
        return null;
	}

	public final <T extends StoredObject> ObjectIterator<T> listLinks(Transaction transaction, int linkType, Class<T> objectClass) {
        return null;
	}

	public final <T extends StoredObject> ObjectIterator<T> listLinks(int linkType, Class<T> objectClass, String condition) {
        return null;
	}

	public final <T extends StoredObject> ObjectIterator<T> listLinks(Transaction transaction, int linkType, Class<T> objectClass,
			String condition) {
        return null;
	}

	public final <T extends StoredObject> ObjectIterator<T> listLinks(int linkType, Class<T> objectClass, String condition,
			String order) {
        return null;
	}

	public final <T extends StoredObject> ObjectIterator<T> listLinks(Transaction transaction, int linkType, Class<T> objectClass,
			String condition, String order) {
        return null;
	}

	public final <T extends StoredObject> ObjectIterator<T> listLinks(String linkType, Class<T> objectClass) {
        return null;
	}

	public final <T extends StoredObject> ObjectIterator<T> listLinks(Transaction transaction, String linkType, Class<T> objectClass) {
        return null;
	}

	public final <T extends StoredObject> ObjectIterator<T> listLinks(String linkType, Class<T> objectClass, String condition) {
        return null;
	}

	public final <T extends StoredObject> ObjectIterator<T> listLinks(Transaction transaction, String linkType,
			Class<T> objectClass, String condition) {
        return null;
	}

	public final <T extends StoredObject> ObjectIterator<T> listLinks(String linkType, Class<T> objectClass, String condition,
			String order) {
        return null;
	}

	public final <T extends StoredObject> ObjectIterator<T> listLinks(Transaction transaction, String linkType,
			Class<T> objectClass, String condition, String order) {
        return null;
	}

	public final <T extends StoredObject> ObjectIterator<T> listLinks(Class<T> objectClass, boolean any) {
        return null;
	}

	public final <T extends StoredObject> ObjectIterator<T> listLinks(Transaction transaction, Class<T> objectClass, boolean any) {
        return null;
	}

	public final <T extends StoredObject> ObjectIterator<T> listLinks(Class<T> objectClass, String condition, boolean any) {
        return null;
	}

	public final <T extends StoredObject> ObjectIterator<T> listLinks(Transaction transaction, Class<T> objectClass,
			String condition, boolean any) {
        return null;
	}

	public final <T extends StoredObject> ObjectIterator<T> listLinks(Class<T> objectClass, String condition, String order,
			boolean any) {
        return null;
	}

	public final <T extends StoredObject> ObjectIterator<T> listLinks(Transaction transaction, Class<T> objectClass,
			String condition, String order, boolean any) {
        return null;
	}

	public final <T extends StoredObject> ObjectIterator<T> listLinks(int linkType, Class<T> objectClass, boolean any) {
        return null;
	}

	public final <T extends StoredObject> ObjectIterator<T> listLinks(Transaction transaction, int linkType, Class<T> objectClass,
			boolean any) {
        return null;
	}

	public final <T extends StoredObject> ObjectIterator<T> listLinks(int linkType, Class<T> objectClass, String condition,
			boolean any) {
        return null;
	}

	public final <T extends StoredObject> ObjectIterator<T> listLinks(Transaction transaction, int linkType, Class<T> objectClass,
			String condition, boolean any) {
        return null;
	}

	public final <T extends StoredObject> ObjectIterator<T> listLinks(int linkType, Class<T> objectClass, String condition,
			String order, boolean any) {
        return null;
	}

	public final <T extends StoredObject> ObjectIterator<T> listLinks(Transaction transaction, int linkType, Class<T> objectClass,
			String condition, String order, boolean any) {
        return null;
	}

	public final <T extends StoredObject> ObjectIterator<T> listLinks(String linkType, Class<T> objectClass, boolean any) {
        return null;
	}

	public final <T extends StoredObject> ObjectIterator<T> listLinks(Transaction transaction, String linkType,
			Class<T> objectClass, boolean any) {
        return null;
	}

	public final <T extends StoredObject> ObjectIterator<T> listLinks(String linkType, Class<T> objectClass, String condition,
			boolean any) {
        return null;
	}

	public final <T extends StoredObject> ObjectIterator<T> listLinks(Transaction transaction, String linkType,
			Class<T> objectClass, String condition, boolean any) {
        return null;
	}

	public final <T extends StoredObject> ObjectIterator<T> listLinks(String linkType, Class<T> objectClass, String condition,
			String order, boolean any) {
        return null;
	}

	public final <T extends StoredObject> ObjectIterator<T> listLinks(Transaction transaction, String linkType,
			Class<T> objectClass, String condition, String order, boolean any) {
        return null;
	}

	public final boolean existsLink(StoredObject link) {
        return false;
	}
	
	public final boolean existsLink(int linkType, StoredObject link) {
        return false;
	}
	
	public final boolean existsLink(Transaction transaction, StoredObject link) {
        return false;
	}
	
	public final boolean existsLink(Transaction transaction, int linkType, StoredObject link) {
        return false;
	}
	
	public final boolean existsLink(Id linkId) {
        return false;
	}

	public final boolean existsLink(int linkType, Id linkId) {
        return false;
	}

	public final boolean existsLink(Transaction transaction, Id linkId) {
        return false;
	}
	
	public final boolean existsLink(Transaction transaction, int linkType, Id linkId) {
        return false;
	}
	
	public final boolean existsLinks() {
        return false;
	}

	public final boolean existsLinks(Transaction transaction) {
        return false;
	}

	public final boolean existsLinks(Class<? extends StoredObject> objectClass) {
        return false;
	}

	public final boolean existsLinks(Class<? extends StoredObject> objectClass, boolean any) {
        return false;
	}

	public final boolean existsLinks(Class<? extends StoredObject> objectClass, String condition) {
        return false;
	}

	public final boolean existsLinks(Class<? extends StoredObject> objectClass, String condition, boolean any) {
        return false;
	}

	public final boolean existsLinks(String linkType, Class<? extends StoredObject> objectClass) {
        return false;
	}

	public final boolean existsLinks(String linkType, Class<? extends StoredObject> objectClass, boolean any) {
        return false;
	}

	public final boolean existsLinks(String linkType, Class<? extends StoredObject> objectClass, String condition) {
        return false;
	}

	public final boolean existsLinks(String linkType, Class<? extends StoredObject> objectClass, String condition, boolean any) {
        return false;
	}

	public final boolean existsLinks(int linkType, Class<? extends StoredObject> objectClass) {
        return false;
	}

	public final boolean existsLinks(int linkType, Class<? extends StoredObject> objectClass, boolean any) {
        return false;
	}

	public final boolean existsLinks(int linkType, Class<? extends StoredObject> objectClass, String condition) {
        return false;
	}

	public final boolean existsLinks(int linkType, Class<? extends StoredObject> objectClass, String condition, boolean any) {
        return false;
	}
	
	public final boolean existsLinks(Transaction tran, Class<? extends StoredObject> objectClass) {
		return false;
	}

	public final boolean existsLinks(Transaction tran, Class<? extends StoredObject> objectClass, boolean any) {
		return false;
	}

	public final boolean existsLinks(Transaction tran, Class<? extends StoredObject> objectClass, String condition) {
		return false;
	}

	public final boolean existsLinks(Transaction tran, Class<? extends StoredObject> objectClass, String condition, boolean any) {
		return false;
	}

	public final boolean existsLinks(Transaction tran, String linkType, Class<? extends StoredObject> objectClass) {
		return false;
	}

	public final boolean existsLinks(Transaction tran, String linkType, Class<? extends StoredObject> objectClass, boolean any) {
		return false;
	}

	public final boolean existsLinks(Transaction tran, String linkType, Class<? extends StoredObject> objectClass, String condition) {
		return false;
	}

	public final boolean existsLinks(Transaction tran, String linkType, Class<? extends StoredObject> objectClass, String condition, boolean any) {
		return false;
	}

	public final boolean existsLinks(Transaction tran, int linkType, Class<? extends StoredObject> objectClass) {
		return false;
	}

	public final boolean existsLinks(Transaction tran, int linkType, Class<? extends StoredObject> objectClass, boolean any) {
		return false;
	}

	public final boolean existsLinks(Transaction tran, int linkType, Class<? extends StoredObject> objectClass, String condition) {
		return false;
	}

	public final boolean existsLinks(Transaction tran, int linkType, Class<? extends StoredObject> objectClass, String condition, boolean any) {
		return false;
	}

	public final Query queryLinks(Class<? extends StoredObject> objectClass, String columns) {
        return null;
	}

	public final Query queryLinks(Transaction transaction, Class<? extends StoredObject> objectClass, String columns) {
        return null;
	}

	public final Query queryLinks(Class<? extends StoredObject> objectClass, String columns, String condition) {
        return null;
	}

	public final Query queryLinks(Transaction transaction, Class<? extends StoredObject> objectClass, String columns,
			String condition) {
        return null;
	}

	public final Query queryLinks(Class<? extends StoredObject> objectClass, String columns, String condition, String order) {
        return null;
	}

	public final Query queryLinks(Transaction transaction, Class<? extends StoredObject> objectClass, String columns,
			String condition, String order) {
        return null;
	}

	public final Query queryLinks(int linkType, Class<? extends StoredObject> objectClass, String columns) {
        return null;
	}

	public final Query queryLinks(Transaction transaction, int linkType, Class<? extends StoredObject> objectClass, String columns) {
        return null;
	}

	public final Query queryLinks(int linkType, Class<? extends StoredObject> objectClass, String columns, String condition) {
        return null;
	}

	public final Query queryLinks(Transaction transaction, int linkType, Class<? extends StoredObject> objectClass, String columns,
			String condition) {
        return null;
	}

	public final Query queryLinks(int linkType, Class<? extends StoredObject> objectClass, String columns, String condition,
			String order) {
        return null;
	}

	public final Query queryLinks(Transaction transaction, int linkType, Class<? extends StoredObject> objectClass, String columns,
			String condition, String order) {
        return null;
	}

	public final Query queryLinks(String linkType, Class<? extends StoredObject> objectClass, String columns) {
        return null;
	}

	public final Query queryLinks(Transaction transaction, String linkType, Class<? extends StoredObject> objectClass,
			String columns) {
        return null;
	}

	public final Query queryLinks(String linkType, Class<? extends StoredObject> objectClass, String columns, String condition) {
        return null;
	}

	public final Query queryLinks(Transaction transaction, String linkType, Class<? extends StoredObject> objectClass,
			String columns, String condition) {
        return null;
	}

	public final Query queryLinks(String linkType, Class<? extends StoredObject> objectClass, String columns, String condition,
			String order) {
        return null;
	}

	public final Query queryLinks(Transaction transaction, String linkType, Class<? extends StoredObject> objectClass,
			String columns, String condition, String order) {
        return null;
	}

	public final Query queryLinks(Class<? extends StoredObject> objectClass, String columns, boolean any) {
        return null;
	}

	public final Query queryLinks(Transaction transaction, Class<? extends StoredObject> objectClass, String columns, boolean any) {
        return null;
	}

	public final Query queryLinks(Class<? extends StoredObject> objectClass, String columns, String condition, boolean any) {
        return null;
	}

	public final Query queryLinks(Transaction transaction, Class<? extends StoredObject> objectClass, String columns,
			String condition, boolean any) {
        return null;
	}

	public final Query queryLinks(Class<? extends StoredObject> objectClass, String columns, String condition, String order,
			boolean any) {
        return null;
	}

	public final Query queryLinks(Transaction transaction, Class<? extends StoredObject> objectClass, String columns,
			String condition, String order, boolean any) {
        return null;
	}

	public final Query queryLinks(int linkType, Class<? extends StoredObject> objectClass, String columns, boolean any) {
        return null;
	}

	public final Query queryLinks(Transaction transaction, int linkType, Class<? extends StoredObject> objectClass, String columns,
			boolean any) {
        return null;
	}

	public final Query queryLinks(int linkType, Class<? extends StoredObject> objectClass, String columns, String condition,
			boolean any) {
        return null;
	}

	public final Query queryLinks(Transaction transaction, int linkType, Class<? extends StoredObject> objectClass, String columns,
			String condition, boolean any) {
        return null;
	}

	public final Query queryLinks(int linkType, Class<? extends StoredObject> objectClass, String columns, String condition,
			String order, boolean any) {
        return null;
	}

	public final Query queryLinks(Transaction transaction, int linkType, Class<? extends StoredObject> objectClass, String columns,
			String condition, String order, boolean any) {
        return null;
	}

	public final Query queryLinks(String linkType, Class<? extends StoredObject> objectClass, String columns, boolean any) {
        return null;
	}

	public final Query queryLinks(Transaction transaction, String linkType, Class<? extends StoredObject> objectClass,
			String columns, boolean any) {
        return null;
	}

	public final Query queryLinks(String linkType, Class<? extends StoredObject> objectClass, String columns, String condition,
			boolean any) {
        return null;
	}

	public final Query queryLinks(Transaction transaction, String linkType, Class<? extends StoredObject> objectClass,
			String columns, String condition, boolean any) {
        return null;
	}

	public final Query queryLinks(String linkType, Class<? extends StoredObject> objectClass, String columns, String condition,
			String order, boolean any) {
        return null;
	}

	public final Query queryLinks(Transaction transaction, String linkType, Class<? extends StoredObject> objectClass,
			String columns, String condition, String order, boolean any) {
        return null;
	}

	public final int countLinks(Class<? extends StoredObject> objectClass) {
        return 0;
	}

	public final int countLinks(Transaction transaction, Class<? extends StoredObject> objectClass) {
        return 0;
	}

	public final int countLinks(Class<? extends StoredObject> objectClass, String condition) {
        return 0;
	}

	public final int countLinks(Transaction transaction, Class<? extends StoredObject> objectClass, String condition) {
        return 0;
	}

	public final int countLinks(int linkType, Class<? extends StoredObject> objectClass) {
        return 0;
	}

	public final int countLinks(Transaction transaction, int linkType, Class<? extends StoredObject> objectClass) {
        return 0;
	}

	public final int countLinks(int linkType, Class<? extends StoredObject> objectClass, String condition) {
        return 0;
	}

	public final int countLinks(Transaction transaction, int linkType, Class<? extends StoredObject> objectClass, String condition) {
        return 0;
	}

	public final int countLinks(String linkType, Class<? extends StoredObject> objectClass) {
		return 0;
	}

	public final int countLinks(Transaction transaction, String linkType, Class<? extends StoredObject> objectClass) {
        return 0;
	}

	public final int countLinks(String linkType, Class<? extends StoredObject> objectClass, String condition) {
        return 0;
	}

	public final int countLinks(Transaction transaction, String linkType, Class<? extends StoredObject> objectClass,
			String condition) {
        return 0;
	}

	public final int countLinks(Class<? extends StoredObject> objectClass, boolean any) {
        return 0;
	}

	public final int countLinks(Transaction transaction, Class<? extends StoredObject> objectClass, boolean any) {
        return 0;
	}

	public final int countLinks(Class<? extends StoredObject> objectClass, String condition, boolean any) {
        return 0;
	}

	public final int countLinks(Transaction transaction, Class<? extends StoredObject> objectClass, String condition, boolean any) {
        return 0;
	}

	public final int countLinks(int linkType, Class<? extends StoredObject> objectClass, boolean any) {
        return 0;
	}

	public final int countLinks(Transaction transaction, int linkType, Class<? extends StoredObject> objectClass, boolean any) {
        return 0;
	}

	public final int countLinks(int linkType, Class<? extends StoredObject> objectClass, String condition, boolean any) {
        return 0;
	}

	public final int countLinks(Transaction transaction, int linkType, Class<? extends StoredObject> objectClass, String condition,
			boolean any) {
        return 0;
	}

	public final int countLinks(String linkType, Class<? extends StoredObject> objectClass, boolean any) {
        return 0;
	}

	public final int countLinks(Transaction transaction, String linkType, Class<? extends StoredObject> objectClass, boolean any) {
        return 0;
	}

	public final int countLinks(String linkType, Class<? extends StoredObject> objectClass, String condition, boolean any) {
        return 0;
	}

	public final int countLinks(Transaction transaction, String linkType, Class<? extends StoredObject> objectClass,
			String condition, boolean any) {
        return 0;
	}

	public final <T extends StoredObject> ObjectIterator<T> listMasters(Class<T> objectClass) {
        return null;
	}

	public final <T extends StoredObject> ObjectIterator<T> listMasters(Transaction transaction, Class<T> objectClass) {
        return null;
	}

	public final <T extends StoredObject> ObjectIterator<T> listMasters(Class<T> objectClass, String condition) {
        return null;
	}

	public final <T extends StoredObject> ObjectIterator<T> listMasters(Transaction transaction, Class<T> objectClass,
			String condition) {
        return null;
	}

	public final <T extends StoredObject> ObjectIterator<T> listMasters(Class<T> objectClass, String condition, String order) {
        return null;
	}

	public final <T extends StoredObject> ObjectIterator<T> listMasters(Transaction transaction, Class<T> objectClass,
			String condition, String order) {
        return null;
	}

	public final <T extends StoredObject> ObjectIterator<T> listMasters(int linkType, Class<T> objectClass) {
        return null;
	}

	public final <T extends StoredObject> ObjectIterator<T> listMasters(Transaction transaction, int linkType, Class<T> objectClass) {
        return null;
	}

	public final <T extends StoredObject> ObjectIterator<T> listMasters(int linkType, Class<T> objectClass, String condition) {
        return null;
	}

	public final <T extends StoredObject> ObjectIterator<T> listMasters(Transaction transaction, int linkType,
			Class<T> objectClass, String condition) {
        return null;
	}

	public final <T extends StoredObject> ObjectIterator<T> listMasters(int linkType, Class<T> objectClass, String condition,
			String order) {
        return null;
	}

	public final <T extends StoredObject> ObjectIterator<T> listMasters(Transaction transaction, int linkType,
			Class<T> objectClass, String condition, String order) {
        return null;
	}

	public final <T extends StoredObject> ObjectIterator<T> listMasters(String linkType, Class<T> objectClass) {
        return null;
	}

	public final <T extends StoredObject> ObjectIterator<T> listMasters(Transaction transaction, String linkType,
			Class<T> objectClass) {
        return null;
	}

	public final <T extends StoredObject> ObjectIterator<T> listMasters(String linkType, Class<T> objectClass, String condition) {
        return null;
	}

	public final <T extends StoredObject> ObjectIterator<T> listMasters(Transaction transaction, String linkType,
			Class<T> objectClass, String condition) {
        return null;
	}

	public final <T extends StoredObject> ObjectIterator<T> listMasters(String linkType, Class<T> objectClass, String condition,
			String order) {
        return null;
	}

	public final <T extends StoredObject> ObjectIterator<T> listMasters(Transaction transaction, String linkType,
			Class<T> objectClass, String condition, String order) {
        return null;
	}

	public final <T extends StoredObject> ObjectIterator<T> listMasters(Class<T> objectClass, boolean any) {
        return null;
	}

	public final <T extends StoredObject> ObjectIterator<T> listMasters(Transaction transaction, Class<T> objectClass, boolean any) {
        return null;
	}

	public final <T extends StoredObject> ObjectIterator<T> listMasters(Class<T> objectClass, String condition, boolean any) {
        return null;
	}

	public final <T extends StoredObject> ObjectIterator<T> listMasters(Transaction transaction, Class<T> objectClass,
			String condition, boolean any) {
        return null;
	}

	public final <T extends StoredObject> ObjectIterator<T> listMasters(Class<T> objectClass, String condition, String order,
			boolean any) {
        return null;
	}

	public final <T extends StoredObject> ObjectIterator<T> listMasters(Transaction transaction, Class<T> objectClass,
			String condition, String order, boolean any) {
        return null;
	}

	public final <T extends StoredObject> ObjectIterator<T> listMasters(int linkType, Class<T> objectClass, boolean any) {
        return null;
	}

	public final <T extends StoredObject> ObjectIterator<T> listMasters(Transaction transaction, int linkType,
			Class<T> objectClass, boolean any) {
        return null;
	}

	public final <T extends StoredObject> ObjectIterator<T> listMasters(int linkType, Class<T> objectClass, String condition,
			boolean any) {
        return null;
	}

	public final <T extends StoredObject> ObjectIterator<T> listMasters(Transaction transaction, int linkType,
			Class<T> objectClass, String condition, boolean any) {
        return null;
	}

	public final <T extends StoredObject> ObjectIterator<T> listMasters(int linkType, Class<T> objectClass, String condition,
			String order, boolean any) {
        return null;
	}

	public final <T extends StoredObject> ObjectIterator<T> listMasters(Transaction transaction, int linkType,
			Class<T> objectClass, String condition, String order, boolean any) {
        return null;
	}

	public final <T extends StoredObject> ObjectIterator<T> listMasters(String linkType, Class<T> objectClass, boolean any) {
        return null;
	}

	public final <T extends StoredObject> ObjectIterator<T> listMasters(Transaction transaction, String linkType,
			Class<T> objectClass, boolean any) {
        return null;
	}

	public final <T extends StoredObject> ObjectIterator<T> listMasters(String linkType, Class<T> objectClass, String condition,
			boolean any) {
        return null;
	}

	public final <T extends StoredObject> ObjectIterator<T> listMasters(Transaction transaction, String linkType,
			Class<T> objectClass, String condition, boolean any) {
        return null;
	}

	public final <T extends StoredObject> ObjectIterator<T> listMasters(String linkType, Class<T> objectClass, String condition,
			String order, boolean any) {
        return null;
	}

	public final <T extends StoredObject> ObjectIterator<T> listMasters(Transaction transaction, String linkType,
			Class<T> objectClass, String condition, String order, boolean any) {
        return null;
	}

	public final <T extends StoredObject> T getMaster(Class<T> objectClass) {
        return null;
	}

	public final <T extends StoredObject> T getMaster(Transaction transaction, Class<T> objectClass) {
        return null;
	}

	public final <T extends StoredObject> T getMaster(Class<T> objectClass, String condition) {
        return null;
	}

	public final <T extends StoredObject> T getMaster(Transaction transaction, Class<T> objectClass, String condition) {
        return null;
	}

	public final <T extends StoredObject> T getMaster(Class<T> objectClass, String condition, String order) {
        return null;
	}

	public final <T extends StoredObject> T getMaster(Transaction transaction, Class<T> objectClass, String condition, String order) {
        return null;
	}

	public final <T extends StoredObject> T getMaster(int linkType, Class<T> objectClass) {
        return null;
	}

	public final <T extends StoredObject> T getMaster(Transaction transaction, int linkType, Class<T> objectClass) {
        return null;
	}

	public final <T extends StoredObject> T getMaster(int linkType, Class<T> objectClass, String condition) {
        return null;
	}

	public final <T extends StoredObject> T getMaster(Transaction transaction, int linkType, Class<T> objectClass, String condition) {
        return null;
	}

	public final <T extends StoredObject> T getMaster(int linkType, Class<T> objectClass, String condition, String order) {
        return null;
	}

	public final <T extends StoredObject> T getMaster(Transaction transaction, int linkType, Class<T> objectClass,
			String condition, String order) {
        return null;
	}

	public final <T extends StoredObject> T getMaster(String linkType, Class<T> objectClass) {
        return null;
	}

	public final <T extends StoredObject> T getMaster(Transaction transaction, String linkType, Class<T> objectClass) {
        return null;
	}

	public final <T extends StoredObject> T getMaster(String linkType, Class<T> objectClass, String condition) {
        return null;
	}

	public final <T extends StoredObject> T getMaster(Transaction transaction, String linkType, Class<T> objectClass,
			String condition) {
        return null;
	}

	public final <T extends StoredObject> T getMaster(String linkType, Class<T> objectClass, String condition, String order) {
        return null;
	}

	public final <T extends StoredObject> T getMaster(Transaction transaction, String linkType, Class<T> objectClass,
			String condition, String order) {
        return null;
	}

	public final <T extends StoredObject> T getMaster(Class<T> objectClass, boolean any) {
        return null;
	}

	public final <T extends StoredObject> T getMaster(Transaction transaction, Class<T> objectClass, boolean any) {
        return null;
	}

	public final <T extends StoredObject> T getMaster(Class<T> objectClass, String condition, boolean any) {
        return null;
	}

	public final <T extends StoredObject> T getMaster(Transaction transaction, Class<T> objectClass, String condition, boolean any) {
        return null;
	}

	public final <T extends StoredObject> T getMaster(Class<T> objectClass, String condition, String order, boolean any) {
        return null;
	}

	public final <T extends StoredObject> T getMaster(Transaction transaction, Class<T> objectClass, String condition,
			String order, boolean any) {
        return null;
	}

	public final <T extends StoredObject> T getMaster(int linkType, Class<T> objectClass, boolean any) {
        return null;
	}

	public final <T extends StoredObject> T getMaster(Transaction transaction, int linkType, Class<T> objectClass, boolean any) {
        return null;
	}

	public final <T extends StoredObject> T getMaster(int linkType, Class<T> objectClass, String condition, boolean any) {
        return null;
	}

	public final <T extends StoredObject> T getMaster(Transaction transaction, int linkType, Class<T> objectClass,
			String condition, boolean any) {
        return null;
	}

	public final <T extends StoredObject> T getMaster(int linkType, Class<T> objectClass, String condition, String order,
			boolean any) {
        return null;
	}

	public final <T extends StoredObject> T getMaster(Transaction transaction, int linkType, Class<T> objectClass,
			String condition, String order, boolean any) {
        return null;
	}

	public final <T extends StoredObject> T getMaster(String linkType, Class<T> objectClass, boolean any) {
        return null;
	}

	public final <T extends StoredObject> T getMaster(Transaction transaction, String linkType, Class<T> objectClass, boolean any) {
        return null;
	}

	public final <T extends StoredObject> T getMaster(String linkType, Class<T> objectClass, String condition, boolean any) {
        return null;
	}

	public final <T extends StoredObject> T getMaster(Transaction transaction, String linkType, Class<T> objectClass,
			String condition, boolean any) {
        return null;
	}

	public final <T extends StoredObject> T getMaster(String linkType, Class<T> objectClass, String condition, String order,
			boolean any) {
        return null;
	}

	public final <T extends StoredObject> T getMaster(Transaction transaction, String linkType, Class<T> objectClass,
			String condition, String order, boolean any) {
        return null;
	}

	public final boolean existsMaster(StoredObject master) {
        return false;
	}

	public final boolean existsMaster(Id masterId) {
        return false;
	}

	public final boolean existsMasters() {
        return false;
	}

	public final boolean existsMasters(Class<? extends StoredObject> objectClass) {
        return false;
	}

	public final boolean existsMasters(Class<? extends StoredObject> objectClass, boolean any) {
        return false;
	}

	public final boolean existsMasters(Class<? extends StoredObject> objectClass, String condition) {
        return false;
	}

	public final boolean existsMasters(Class<? extends StoredObject> objectClass, String condition, boolean any) {
        return false;
	}

	public final boolean existsMasters(String linkType, Class<? extends StoredObject> objectClass) {
        return false;
	}

	public final boolean existsMasters(String linkType, Class<? extends StoredObject> objectClass, boolean any) {
        return false;
	}

	public final boolean existsMasters(String linkType, Class<? extends StoredObject> objectClass, String condition) {
        return false;
	}

	public final boolean existsMasters(String linkType, Class<? extends StoredObject> objectClass, String condition, boolean any) {
        return false;
	}

	public final boolean existsMasters(int linkType, Class<? extends StoredObject> objectClass) {
        return false;
	}

	public final boolean existsMasters(int linkType, Class<? extends StoredObject> objectClass, boolean any) {
        return false;
	}

	public final boolean existsMasters(int linkType, Class<? extends StoredObject> objectClass, String condition) {
        return false;
	}

	public final boolean existsMasters(int linkType, Class<? extends StoredObject> objectClass, String condition, boolean any) {
        return false;
	}

	public final Query queryMasters(Class<? extends StoredObject> objectClass, String columns) {
        return null;
	}

	public final Query queryMasters(Transaction transaction, Class<? extends StoredObject> objectClass, String columns) {
        return null;
	}

	public final Query queryMasters(Class<? extends StoredObject> objectClass, String columns, String condition) {
        return null;
	}

	public final Query queryMasters(Transaction transaction, Class<? extends StoredObject> objectClass, String columns,
			String condition) {
        return null;
	}

	public final Query queryMasters(Class<? extends StoredObject> objectClass, String columns, String condition, String order) {
        return null;
	}

	public final Query queryMasters(Transaction transaction, Class<? extends StoredObject> objectClass, String columns,
			String condition, String order) {
        return null;
	}

	public final Query queryMasters(int linkType, Class<? extends StoredObject> objectClass, String columns) {
        return null;
	}

	public final Query queryMasters(Transaction transaction, int linkType, Class<? extends StoredObject> objectClass, String columns) {
        return null;
	}

	public final Query queryMasters(int linkType, Class<? extends StoredObject> objectClass, String columns, String condition) {
        return null;
	}

	public final Query queryMasters(Transaction transaction, int linkType, Class<? extends StoredObject> objectClass,
			String columns, String condition) {
        return null;
	}

	public final Query queryMasters(int linkType, Class<? extends StoredObject> objectClass, String columns, String condition,
			String order) {
        return null;
	}

	public final Query queryMasters(Transaction transaction, int linkType, Class<? extends StoredObject> objectClass,
			String columns, String condition, String order) {
        return null;
	}

	public final Query queryMasters(String linkType, Class<? extends StoredObject> objectClass, String columns) {
        return null;
	}

	public final Query queryMasters(Transaction transaction, String linkType, Class<? extends StoredObject> objectClass,
			String columns) {
        return null;
	}

	public final Query queryMasters(String linkType, Class<? extends StoredObject> objectClass, String columns, String condition) {
        return null;
	}

	public final Query queryMasters(Transaction transaction, String linkType, Class<? extends StoredObject> objectClass,
			String columns, String condition) {
        return null;
	}

	public final Query queryMasters(String linkType, Class<? extends StoredObject> objectClass, String columns, String condition,
			String order) {
        return null;
	}

	public final Query queryMasters(Transaction transaction, String linkType, Class<? extends StoredObject> objectClass,
			String columns, String condition, String order) {
        return null;
	}

	public final Query queryMasters(Class<? extends StoredObject> objectClass, String columns, boolean any) {
        return null;
	}

	public final Query queryMasters(Transaction transaction, Class<? extends StoredObject> objectClass, String columns, boolean any) {
        return null;
	}

	public final Query queryMasters(Class<? extends StoredObject> objectClass, String columns, String condition, boolean any) {
        return null;
	}

	public final Query queryMasters(Transaction transaction, Class<? extends StoredObject> objectClass, String columns,
			String condition, boolean any) {
        return null;
	}

	public final Query queryMasters(Class<? extends StoredObject> objectClass, String columns, String condition, String order,
			boolean any) {
        return null;
	}

	public final Query queryMasters(Transaction transaction, Class<? extends StoredObject> objectClass, String columns,
			String condition, String order, boolean any) {
        return null;
	}

	public final Query queryMasters(int linkType, Class<? extends StoredObject> objectClass, String columns, boolean any) {
        return null;
	}

	public final Query queryMasters(Transaction transaction, int linkType, Class<? extends StoredObject> objectClass,
			String columns, boolean any) {
        return null;
	}

	public final Query queryMasters(int linkType, Class<? extends StoredObject> objectClass, String columns, String condition,
			boolean any) {
        return null;
	}

	public final Query queryMasters(Transaction transaction, int linkType, Class<? extends StoredObject> objectClass,
			String columns, String condition, boolean any) {
        return null;
	}

	public final Query queryMasters(int linkType, Class<? extends StoredObject> objectClass, String columns, String condition,
			String order, boolean any) {
        return null;
	}

	public final Query queryMasters(Transaction transaction, int linkType, Class<? extends StoredObject> objectClass,
			String columns, String condition, String order, boolean any) {
        return null;
	}

	public final Query queryMasters(String linkType, Class<? extends StoredObject> objectClass, String columns, boolean any) {
        return null;
	}

	public final Query queryMasters(Transaction transaction, String linkType, Class<? extends StoredObject> objectClass,
			String columns, boolean any) {
        return null;
	}

	public final Query queryMasters(String linkType, Class<? extends StoredObject> objectClass, String columns, String condition,
			boolean any) {
        return null;
	}

	public final Query queryMasters(Transaction transaction, String linkType, Class<? extends StoredObject> objectClass,
			String columns, String condition, boolean any) {
        return null;
	}

	public final Query queryMasters(String linkType, Class<? extends StoredObject> objectClass, String columns, String condition,
			String order, boolean any) {
        return null;
	}

	public final Query queryMasters(Transaction transaction, String linkType, Class<? extends StoredObject> objectClass,
			String columns, String condition, String order, boolean any) {
        return null;
	}

	public final int countMasters(Class<? extends StoredObject> objectClass) {
        return 0;
	}

	public final int countMasters(Transaction transaction, Class<? extends StoredObject> objectClass) {
        return 0;
	}

	public final int countMasters(Class<? extends StoredObject> objectClass, String condition) {
        return 0;
	}

	public final int countMasters(Transaction transaction, Class<? extends StoredObject> objectClass, String condition) {
        return 0;
	}

	public final int countMasters(int linkType, Class<? extends StoredObject> objectClass) {
        return 0;
	}

	public final int countMasters(Transaction transaction, int linkType, Class<? extends StoredObject> objectClass) {
        return 0;
	}

	public final int countMasters(int linkType, Class<? extends StoredObject> objectClass, String condition) {
        return 0;
	}

	public final int countMasters(Transaction transaction, int linkType, Class<? extends StoredObject> objectClass, String condition) {
        return 0;
	}

	public final int countMasters(String linkType, Class<? extends StoredObject> objectClass) {
        return 0;
	}

	public final int countMasters(Transaction transaction, String linkType, Class<? extends StoredObject> objectClass) {
        return 0;
	}

	public final int countMasters(String linkType, Class<? extends StoredObject> objectClass, String condition) {
        return 0;
	}

	public final int countMasters(Transaction transaction, String linkType, Class<? extends StoredObject> objectClass,
			String condition) {
        return 0;
	}

	public final int countMasters(Class<? extends StoredObject> objectClass, boolean any) {
        return 0;
	}

	public final int countMasters(Transaction transaction, Class<? extends StoredObject> objectClass, boolean any) {
        return 0;
	}

	public final int countMasters(Class<? extends StoredObject> objectClass, String condition, boolean any) {
        return 0;
	}

	public final int countMasters(Transaction transaction, Class<? extends StoredObject> objectClass, String condition, boolean any) {
        return 0;
	}

	public final int countMasters(int linkType, Class<? extends StoredObject> objectClass, boolean any) {
        return 0;
	}

	public final int countMasters(Transaction transaction, int linkType, Class<? extends StoredObject> objectClass, boolean any) {
        return 0;
	}

	public final int countMasters(int linkType, Class<? extends StoredObject> objectClass, String condition, boolean any) {
        return 0;
	}

	public final int countMasters(Transaction transaction, int linkType, Class<? extends StoredObject> objectClass,
			String condition, boolean any) {
        return 0;
	}

	public final int countMasters(String linkType, Class<? extends StoredObject> objectClass, boolean any) {
        return 0;
	}

	public final int countMasters(Transaction transaction, String linkType, Class<? extends StoredObject> objectClass, boolean any) {
        return 0;
	}

	public final int countMasters(String linkType, Class<? extends StoredObject> objectClass, String condition, boolean any) {
        return 0;
	}

	public final int countMasters(Transaction transaction, String linkType, Class<? extends StoredObject> objectClass,
			String condition, boolean any) {
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

	public final ObjectIterator<? extends FileData> listFileData() {
        return null;
	}
	
	public final ObjectIterator<? extends FileData> listFileData(Transaction transaction) {
		return null;
	}
}
