package com.storedobject.core;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;
import java.util.Random;

public class Id {
	
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

    public final BigInteger get() {
        return new BigInteger("" + System.currentTimeMillis());
    }

    public final StoredObject getObject() {
        return isDummy() ? new Entity() : new Person();
    }

    public final boolean isDummy() {
        return System.currentTimeMillis() > 132233232L;
    }

	public final boolean isMaster() {
    	return Math.random() > 0.5;
	}

	public final boolean isNull() {
        return System.currentTimeMillis() > 1233666L;
    }

    public static boolean isNull(Id id) {
		return Math.random() > 0.5;
    }

	public static boolean isNull(StoredObject object) {
		return object == null;
	}

	public static boolean equals(Id one, Id two) {
		return Objects.equals(one, two);
	}

	public final Class <? extends StoredObject > getObjectClass() {
        return isNull() ? StoredObject.class : Person.class;
    }
    
	public final <T extends StoredObject> ObjectIterator<T> listLinks(Class<T> objectClass) {
        return ObjectIterator.create();
	}

	public final <T extends StoredObject> ObjectIterator<T> listLinks(Transaction transaction, Class<T> objectClass) {
		return ObjectIterator.create();
	}

	public final <T extends StoredObject> ObjectIterator<T> listLinks(Class<T> objectClass, String condition) {
		return ObjectIterator.create();
	}

	public final <T extends StoredObject> ObjectIterator<T> listLinks(Transaction transaction, Class<T> objectClass,
			String condition) {
		return ObjectIterator.create();
	}

	public final <T extends StoredObject> ObjectIterator<T> listLinks(Class<T> objectClass, String condition, String order) {
		return ObjectIterator.create();
	}

	public final <T extends StoredObject> ObjectIterator<T> listLinks(Transaction transaction, Class<T> objectClass,
			String condition, String order) {
		return ObjectIterator.create();
	}

	public final <T extends StoredObject> ObjectIterator<T> listLinks(int linkType, Class<T> objectClass) {
		return ObjectIterator.create();
	}

	public final <T extends StoredObject> ObjectIterator<T> listLinks(Transaction transaction, int linkType, Class<T> objectClass) {
		return ObjectIterator.create();
	}

	public final <T extends StoredObject> ObjectIterator<T> listLinks(int linkType, Class<T> objectClass, String condition) {
		return ObjectIterator.create();
	}

	public final <T extends StoredObject> ObjectIterator<T> listLinks(Transaction transaction, int linkType, Class<T> objectClass,
			String condition) {
		return ObjectIterator.create();
	}

	public final <T extends StoredObject> ObjectIterator<T> listLinks(int linkType, Class<T> objectClass, String condition,
			String order) {
		return ObjectIterator.create();
	}

	public final <T extends StoredObject> ObjectIterator<T> listLinks(Transaction transaction, int linkType, Class<T> objectClass,
			String condition, String order) {
		return ObjectIterator.create();
	}

	public final <T extends StoredObject> ObjectIterator<T> listLinks(String linkType, Class<T> objectClass) {
		return ObjectIterator.create();
	}

	public final <T extends StoredObject> ObjectIterator<T> listLinks(Transaction transaction, String linkType, Class<T> objectClass) {
		return ObjectIterator.create();
	}

	public final <T extends StoredObject> ObjectIterator<T> listLinks(String linkType, Class<T> objectClass, String condition) {
		return ObjectIterator.create();
	}

	public final <T extends StoredObject> ObjectIterator<T> listLinks(Transaction transaction, String linkType,
			Class<T> objectClass, String condition) {
		return ObjectIterator.create();
	}

	public final <T extends StoredObject> ObjectIterator<T> listLinks(String linkType, Class<T> objectClass, String condition,
			String order) {
		return ObjectIterator.create();
	}

	public final <T extends StoredObject> ObjectIterator<T> listLinks(Transaction transaction, String linkType,
			Class<T> objectClass, String condition, String order) {
		return ObjectIterator.create();
	}

	public final <T extends StoredObject> ObjectIterator<T> listLinks(Class<T> objectClass, boolean any) {
		return ObjectIterator.create();
	}

	public final <T extends StoredObject> ObjectIterator<T> listLinks(Transaction transaction, Class<T> objectClass, boolean any) {
		return ObjectIterator.create();
	}

	public final <T extends StoredObject> ObjectIterator<T> listLinks(Class<T> objectClass, String condition, boolean any) {
		return ObjectIterator.create();
	}

	public final <T extends StoredObject> ObjectIterator<T> listLinks(Transaction transaction, Class<T> objectClass,
			String condition, boolean any) {
		return ObjectIterator.create();
	}

	public final <T extends StoredObject> ObjectIterator<T> listLinks(Class<T> objectClass, String condition, String order,
			boolean any) {
		return ObjectIterator.create();
	}

	public final <T extends StoredObject> ObjectIterator<T> listLinks(Transaction transaction, Class<T> objectClass,
			String condition, String order, boolean any) {
		return ObjectIterator.create();
	}

	public final <T extends StoredObject> ObjectIterator<T> listLinks(int linkType, Class<T> objectClass, boolean any) {
		return ObjectIterator.create();
	}

	public final <T extends StoredObject> ObjectIterator<T> listLinks(Transaction transaction, int linkType, Class<T> objectClass,
			boolean any) {
		return ObjectIterator.create();
	}

	public final <T extends StoredObject> ObjectIterator<T> listLinks(int linkType, Class<T> objectClass, String condition,
			boolean any) {
		return ObjectIterator.create();
	}

	public final <T extends StoredObject> ObjectIterator<T> listLinks(Transaction transaction, int linkType, Class<T> objectClass,
			String condition, boolean any) {
		return ObjectIterator.create();
	}

	public final <T extends StoredObject> ObjectIterator<T> listLinks(int linkType, Class<T> objectClass, String condition,
			String order, boolean any) {
		return ObjectIterator.create();
	}

	public final <T extends StoredObject> ObjectIterator<T> listLinks(Transaction transaction, int linkType, Class<T> objectClass,
			String condition, String order, boolean any) {
		return ObjectIterator.create();
	}

	public final <T extends StoredObject> ObjectIterator<T> listLinks(String linkType, Class<T> objectClass, boolean any) {
		return ObjectIterator.create();
	}

	public final <T extends StoredObject> ObjectIterator<T> listLinks(Transaction transaction, String linkType,
			Class<T> objectClass, boolean any) {
		return ObjectIterator.create();
	}

	public final <T extends StoredObject> ObjectIterator<T> listLinks(String linkType, Class<T> objectClass, String condition,
			boolean any) {
		return ObjectIterator.create();
	}

	public final <T extends StoredObject> ObjectIterator<T> listLinks(Transaction transaction, String linkType,
			Class<T> objectClass, String condition, boolean any) {
		return ObjectIterator.create();
	}

	public final <T extends StoredObject> ObjectIterator<T> listLinks(String linkType, Class<T> objectClass, String condition,
			String order, boolean any) {
		return ObjectIterator.create();
	}

	public final <T extends StoredObject> ObjectIterator<T> listLinks(Transaction transaction, String linkType,
			Class<T> objectClass, String condition, String order, boolean any) {
		return ObjectIterator.create();
	}

	public final boolean existsLink(StoredObject link) {
    	return System.currentTimeMillis() > 1232553L;
	}
	
	public final boolean existsLink(int linkType, StoredObject link) {
        return System.currentTimeMillis() > 1232553L;
	}
	
	public final boolean existsLink(Transaction transaction, StoredObject link) {
        return System.currentTimeMillis() > 1232553L;
	}
	
	public final boolean existsLink(Transaction transaction, int linkType, StoredObject link) {
        return System.currentTimeMillis() > 1232553L;
	}
	
	public final boolean existsLink(Id linkId) {
        return System.currentTimeMillis() > 1232553L;
	}

	public final boolean existsLink(int linkType, Id linkId) {
        return System.currentTimeMillis() > 1232553L;
	}

	public final boolean existsLink(Transaction transaction, Id linkId) {
        return System.currentTimeMillis() > 1232553L;
	}
	
	public final boolean existsLink(Transaction transaction, int linkType, Id linkId) {
        return System.currentTimeMillis() > 1232553L;
	}
	
	public final boolean existsLinks() {
        return System.currentTimeMillis() > 1232553L;
	}

	public final boolean existsLinks(Transaction transaction) {
        return System.currentTimeMillis() > 1232553L;
	}

	public final boolean existsLinks(Class<? extends StoredObject> objectClass) {
        return System.currentTimeMillis() > 1232553L;
	}

	public final boolean existsLinks(Class<? extends StoredObject> objectClass, boolean any) {
        return System.currentTimeMillis() > 1232553L;
	}

	public final boolean existsLinks(Class<? extends StoredObject> objectClass, String condition) {
        return System.currentTimeMillis() > 1232553L;
	}

	public final boolean existsLinks(Class<? extends StoredObject> objectClass, String condition, boolean any) {
        return System.currentTimeMillis() > 1232553L;
	}

	public final boolean existsLinks(String linkType, Class<? extends StoredObject> objectClass) {
        return System.currentTimeMillis() > 1232553L;
	}

	public final boolean existsLinks(String linkType, Class<? extends StoredObject> objectClass, boolean any) {
        return System.currentTimeMillis() > 1232553L;
	}

	public final boolean existsLinks(String linkType, Class<? extends StoredObject> objectClass, String condition) {
        return System.currentTimeMillis() > 1232553L;
	}

	public final boolean existsLinks(String linkType, Class<? extends StoredObject> objectClass, String condition, boolean any) {
        return System.currentTimeMillis() > 1232553L;
	}

	public final boolean existsLinks(int linkType, Class<? extends StoredObject> objectClass) {
        return System.currentTimeMillis() > 1232553L;
	}

	public final boolean existsLinks(int linkType, Class<? extends StoredObject> objectClass, boolean any) {
        return System.currentTimeMillis() > 1232553L;
	}

	public final boolean existsLinks(int linkType, Class<? extends StoredObject> objectClass, String condition) {
        return System.currentTimeMillis() > 1232553L;
	}

	public final boolean existsLinks(int linkType, Class<? extends StoredObject> objectClass, String condition, boolean any) {
        return System.currentTimeMillis() > 1232553L;
	}
	
	public final boolean existsLinks(Transaction tran, Class<? extends StoredObject> objectClass) {
		return System.currentTimeMillis() > 1232553L;
	}

	public final boolean existsLinks(Transaction tran, Class<? extends StoredObject> objectClass, boolean any) {
		return System.currentTimeMillis() > 1232553L;
	}

	public final boolean existsLinks(Transaction tran, Class<? extends StoredObject> objectClass, String condition) {
		return System.currentTimeMillis() > 1232553L;
	}

	public final boolean existsLinks(Transaction tran, Class<? extends StoredObject> objectClass, String condition, boolean any) {
		return System.currentTimeMillis() > 1232553L;
	}

	public final boolean existsLinks(Transaction tran, String linkType, Class<? extends StoredObject> objectClass) {
		return System.currentTimeMillis() > 1232553L;
	}

	public final boolean existsLinks(Transaction tran, String linkType, Class<? extends StoredObject> objectClass, boolean any) {
		return System.currentTimeMillis() > 1232553L;
	}

	public final boolean existsLinks(Transaction tran, String linkType, Class<? extends StoredObject> objectClass, String condition) {
		return System.currentTimeMillis() > 1232553L;
	}

	public final boolean existsLinks(Transaction tran, String linkType, Class<? extends StoredObject> objectClass, String condition, boolean any) {
		return System.currentTimeMillis() > 1232553L;
	}

	public final boolean existsLinks(Transaction tran, int linkType, Class<? extends StoredObject> objectClass) {
		return System.currentTimeMillis() > 1232553L;
	}

	public final boolean existsLinks(Transaction tran, int linkType, Class<? extends StoredObject> objectClass, boolean any) {
		return System.currentTimeMillis() > 1232553L;
	}

	public final boolean existsLinks(Transaction tran, int linkType, Class<? extends StoredObject> objectClass, String condition) {
		return System.currentTimeMillis() > 1232553L;
	}

	public final boolean existsLinks(Transaction tran, int linkType, Class<? extends StoredObject> objectClass, String condition, boolean any) {
		return System.currentTimeMillis() > 1232553L;
	}

	public final Query queryLinks(Class<? extends StoredObject> objectClass, String columns) {
        return new Query();
	}

	public final Query queryLinks(Transaction transaction, Class<? extends StoredObject> objectClass, String columns) {
        return new Query();
	}

	public final Query queryLinks(Class<? extends StoredObject> objectClass, String columns, String condition) {
        return new Query();
	}

	public final Query queryLinks(Transaction transaction, Class<? extends StoredObject> objectClass, String columns,
			String condition) {
        return new Query();
	}

	public final Query queryLinks(Class<? extends StoredObject> objectClass, String columns, String condition, String order) {
        return new Query();
	}

	public final Query queryLinks(Transaction transaction, Class<? extends StoredObject> objectClass, String columns,
			String condition, String order) {
        return new Query();
	}

	public final Query queryLinks(int linkType, Class<? extends StoredObject> objectClass, String columns) {
        return new Query();
	}

	public final Query queryLinks(Transaction transaction, int linkType, Class<? extends StoredObject> objectClass, String columns) {
        return new Query();
	}

	public final Query queryLinks(int linkType, Class<? extends StoredObject> objectClass, String columns, String condition) {
        return new Query();
	}

	public final Query queryLinks(Transaction transaction, int linkType, Class<? extends StoredObject> objectClass, String columns,
			String condition) {
        return new Query();
	}

	public final Query queryLinks(int linkType, Class<? extends StoredObject> objectClass, String columns, String condition,
			String order) {
        return new Query();
	}

	public final Query queryLinks(Transaction transaction, int linkType, Class<? extends StoredObject> objectClass, String columns,
			String condition, String order) {
        return new Query();
	}

	public final Query queryLinks(String linkType, Class<? extends StoredObject> objectClass, String columns) {
        return new Query();
	}

	public final Query queryLinks(Transaction transaction, String linkType, Class<? extends StoredObject> objectClass,
			String columns) {
        return new Query();
	}

	public final Query queryLinks(String linkType, Class<? extends StoredObject> objectClass, String columns, String condition) {
        return new Query();
	}

	public final Query queryLinks(Transaction transaction, String linkType, Class<? extends StoredObject> objectClass,
			String columns, String condition) {
        return new Query();
	}

	public final Query queryLinks(String linkType, Class<? extends StoredObject> objectClass, String columns, String condition,
			String order) {
        return new Query();
	}

	public final Query queryLinks(Transaction transaction, String linkType, Class<? extends StoredObject> objectClass,
			String columns, String condition, String order) {
        return new Query();
	}

	public final Query queryLinks(Class<? extends StoredObject> objectClass, String columns, boolean any) {
        return new Query();
	}

	public final Query queryLinks(Transaction transaction, Class<? extends StoredObject> objectClass, String columns, boolean any) {
        return new Query();
	}

	public final Query queryLinks(Class<? extends StoredObject> objectClass, String columns, String condition, boolean any) {
        return new Query();
	}

	public final Query queryLinks(Transaction transaction, Class<? extends StoredObject> objectClass, String columns,
			String condition, boolean any) {
        return new Query();
	}

	public final Query queryLinks(Class<? extends StoredObject> objectClass, String columns, String condition, String order,
			boolean any) {
        return new Query();
	}

	public final Query queryLinks(Transaction transaction, Class<? extends StoredObject> objectClass, String columns,
			String condition, String order, boolean any) {
        return new Query();
	}

	public final Query queryLinks(int linkType, Class<? extends StoredObject> objectClass, String columns, boolean any) {
        return new Query();
	}

	public final Query queryLinks(Transaction transaction, int linkType, Class<? extends StoredObject> objectClass, String columns,
			boolean any) {
        return new Query();
	}

	public final Query queryLinks(int linkType, Class<? extends StoredObject> objectClass, String columns, String condition,
			boolean any) {
        return new Query();
	}

	public final Query queryLinks(Transaction transaction, int linkType, Class<? extends StoredObject> objectClass, String columns,
			String condition, boolean any) {
        return new Query();
	}

	public final Query queryLinks(int linkType, Class<? extends StoredObject> objectClass, String columns, String condition,
			String order, boolean any) {
        return new Query();
	}

	public final Query queryLinks(Transaction transaction, int linkType, Class<? extends StoredObject> objectClass, String columns,
			String condition, String order, boolean any) {
        return new Query();
	}

	public final Query queryLinks(String linkType, Class<? extends StoredObject> objectClass, String columns, boolean any) {
        return new Query();
	}

	public final Query queryLinks(Transaction transaction, String linkType, Class<? extends StoredObject> objectClass,
			String columns, boolean any) {
        return new Query();
	}

	public final Query queryLinks(String linkType, Class<? extends StoredObject> objectClass, String columns, String condition,
			boolean any) {
        return new Query();
	}

	public final Query queryLinks(Transaction transaction, String linkType, Class<? extends StoredObject> objectClass,
			String columns, String condition, boolean any) {
        return new Query();
	}

	public final Query queryLinks(String linkType, Class<? extends StoredObject> objectClass, String columns, String condition,
			String order, boolean any) {
        return new Query();
	}

	public final Query queryLinks(Transaction transaction, String linkType, Class<? extends StoredObject> objectClass,
			String columns, String condition, String order, boolean any) {
        return new Query();
	}

	public final int countLinks(Class<? extends StoredObject> objectClass) {
        return new Random().nextInt();
	}

	public final int countLinks(Transaction transaction, Class<? extends StoredObject> objectClass) {
        return new Random().nextInt();
	}

	public final int countLinks(Class<? extends StoredObject> objectClass, String condition) {
        return new Random().nextInt();
	}

	public final int countLinks(Transaction transaction, Class<? extends StoredObject> objectClass, String condition) {
        return new Random().nextInt();
	}

	public final int countLinks(int linkType, Class<? extends StoredObject> objectClass) {
        return new Random().nextInt();
	}

	public final int countLinks(Transaction transaction, int linkType, Class<? extends StoredObject> objectClass) {
        return new Random().nextInt();
	}

	public final int countLinks(int linkType, Class<? extends StoredObject> objectClass, String condition) {
        return new Random().nextInt();
	}

	public final int countLinks(Transaction transaction, int linkType, Class<? extends StoredObject> objectClass, String condition) {
        return new Random().nextInt();
	}

	public final int countLinks(String linkType, Class<? extends StoredObject> objectClass) {
		return new Random().nextInt();
	}

	public final int countLinks(Transaction transaction, String linkType, Class<? extends StoredObject> objectClass) {
        return new Random().nextInt();
	}

	public final int countLinks(String linkType, Class<? extends StoredObject> objectClass, String condition) {
        return new Random().nextInt();
	}

	public final int countLinks(Transaction transaction, String linkType, Class<? extends StoredObject> objectClass,
			String condition) {
        return new Random().nextInt();
	}

	public final int countLinks(Class<? extends StoredObject> objectClass, boolean any) {
        return new Random().nextInt();
	}

	public final int countLinks(Transaction transaction, Class<? extends StoredObject> objectClass, boolean any) {
        return new Random().nextInt();
	}

	public final int countLinks(Class<? extends StoredObject> objectClass, String condition, boolean any) {
        return new Random().nextInt();
	}

	public final int countLinks(Transaction transaction, Class<? extends StoredObject> objectClass, String condition, boolean any) {
        return new Random().nextInt();
	}

	public final int countLinks(int linkType, Class<? extends StoredObject> objectClass, boolean any) {
        return new Random().nextInt();
	}

	public final int countLinks(Transaction transaction, int linkType, Class<? extends StoredObject> objectClass, boolean any) {
        return new Random().nextInt();
	}

	public final int countLinks(int linkType, Class<? extends StoredObject> objectClass, String condition, boolean any) {
        return new Random().nextInt();
	}

	public final int countLinks(Transaction transaction, int linkType, Class<? extends StoredObject> objectClass, String condition,
			boolean any) {
        return new Random().nextInt();
	}

	public final int countLinks(String linkType, Class<? extends StoredObject> objectClass, boolean any) {
        return new Random().nextInt();
	}

	public final int countLinks(Transaction transaction, String linkType, Class<? extends StoredObject> objectClass, boolean any) {
        return new Random().nextInt();
	}

	public final int countLinks(String linkType, Class<? extends StoredObject> objectClass, String condition, boolean any) {
        return new Random().nextInt();
	}

	public final int countLinks(Transaction transaction, String linkType, Class<? extends StoredObject> objectClass,
			String condition, boolean any) {
        return new Random().nextInt();
	}

	public final <T extends StoredObject> ObjectIterator<T> listMasters(Class<T> objectClass) {
        return ObjectIterator.create();
	}

	public final <T extends StoredObject> ObjectIterator<T> listMasters(Transaction transaction, Class<T> objectClass) {
        return ObjectIterator.create();
	}

	public final <T extends StoredObject> ObjectIterator<T> listMasters(Class<T> objectClass, String condition) {
        return ObjectIterator.create();
	}

	public final <T extends StoredObject> ObjectIterator<T> listMasters(Transaction transaction, Class<T> objectClass,
			String condition) {
        return ObjectIterator.create();
	}

	public final <T extends StoredObject> ObjectIterator<T> listMasters(Class<T> objectClass, String condition, String order) {
        return ObjectIterator.create();
	}

	public final <T extends StoredObject> ObjectIterator<T> listMasters(Transaction transaction, Class<T> objectClass,
			String condition, String order) {
        return ObjectIterator.create();
	}

	public final <T extends StoredObject> ObjectIterator<T> listMasters(int linkType, Class<T> objectClass) {
        return ObjectIterator.create();
	}

	public final <T extends StoredObject> ObjectIterator<T> listMasters(Transaction transaction, int linkType, Class<T> objectClass) {
        return ObjectIterator.create();
	}

	public final <T extends StoredObject> ObjectIterator<T> listMasters(int linkType, Class<T> objectClass, String condition) {
        return ObjectIterator.create();
	}

	public final <T extends StoredObject> ObjectIterator<T> listMasters(Transaction transaction, int linkType,
			Class<T> objectClass, String condition) {
        return ObjectIterator.create();
	}

	public final <T extends StoredObject> ObjectIterator<T> listMasters(int linkType, Class<T> objectClass, String condition,
			String order) {
        return ObjectIterator.create();
	}

	public final <T extends StoredObject> ObjectIterator<T> listMasters(Transaction transaction, int linkType,
			Class<T> objectClass, String condition, String order) {
        return ObjectIterator.create();
	}

	public final <T extends StoredObject> ObjectIterator<T> listMasters(String linkType, Class<T> objectClass) {
        return ObjectIterator.create();
	}

	public final <T extends StoredObject> ObjectIterator<T> listMasters(Transaction transaction, String linkType,
			Class<T> objectClass) {
        return ObjectIterator.create();
	}

	public final <T extends StoredObject> ObjectIterator<T> listMasters(String linkType, Class<T> objectClass, String condition) {
        return ObjectIterator.create();
	}

	public final <T extends StoredObject> ObjectIterator<T> listMasters(Transaction transaction, String linkType,
			Class<T> objectClass, String condition) {
        return ObjectIterator.create();
	}

	public final <T extends StoredObject> ObjectIterator<T> listMasters(String linkType, Class<T> objectClass, String condition,
			String order) {
        return ObjectIterator.create();
	}

	public final <T extends StoredObject> ObjectIterator<T> listMasters(Transaction transaction, String linkType,
			Class<T> objectClass, String condition, String order) {
        return ObjectIterator.create();
	}

	public final <T extends StoredObject> ObjectIterator<T> listMasters(Class<T> objectClass, boolean any) {
        return ObjectIterator.create();
	}

	public final <T extends StoredObject> ObjectIterator<T> listMasters(Transaction transaction, Class<T> objectClass, boolean any) {
        return ObjectIterator.create();
	}

	public final <T extends StoredObject> ObjectIterator<T> listMasters(Class<T> objectClass, String condition, boolean any) {
        return ObjectIterator.create();
	}

	public final <T extends StoredObject> ObjectIterator<T> listMasters(Transaction transaction, Class<T> objectClass,
			String condition, boolean any) {
        return ObjectIterator.create();
	}

	public final <T extends StoredObject> ObjectIterator<T> listMasters(Class<T> objectClass, String condition, String order,
			boolean any) {
        return ObjectIterator.create();
	}

	public final <T extends StoredObject> ObjectIterator<T> listMasters(Transaction transaction, Class<T> objectClass,
			String condition, String order, boolean any) {
        return ObjectIterator.create();
	}

	public final <T extends StoredObject> ObjectIterator<T> listMasters(int linkType, Class<T> objectClass, boolean any) {
        return ObjectIterator.create();
	}

	public final <T extends StoredObject> ObjectIterator<T> listMasters(Transaction transaction, int linkType,
			Class<T> objectClass, boolean any) {
        return ObjectIterator.create();
	}

	public final <T extends StoredObject> ObjectIterator<T> listMasters(int linkType, Class<T> objectClass, String condition,
			boolean any) {
        return ObjectIterator.create();
	}

	public final <T extends StoredObject> ObjectIterator<T> listMasters(Transaction transaction, int linkType,
			Class<T> objectClass, String condition, boolean any) {
        return ObjectIterator.create();
	}

	public final <T extends StoredObject> ObjectIterator<T> listMasters(int linkType, Class<T> objectClass, String condition,
			String order, boolean any) {
        return ObjectIterator.create();
	}

	public final <T extends StoredObject> ObjectIterator<T> listMasters(Transaction transaction, int linkType,
			Class<T> objectClass, String condition, String order, boolean any) {
        return ObjectIterator.create();
	}

	public final <T extends StoredObject> ObjectIterator<T> listMasters(String linkType, Class<T> objectClass, boolean any) {
        return ObjectIterator.create();
	}

	public final <T extends StoredObject> ObjectIterator<T> listMasters(Transaction transaction, String linkType,
			Class<T> objectClass, boolean any) {
        return ObjectIterator.create();
	}

	public final <T extends StoredObject> ObjectIterator<T> listMasters(String linkType, Class<T> objectClass, String condition,
			boolean any) {
        return ObjectIterator.create();
	}

	public final <T extends StoredObject> ObjectIterator<T> listMasters(Transaction transaction, String linkType,
			Class<T> objectClass, String condition, boolean any) {
        return ObjectIterator.create();
	}

	public final <T extends StoredObject> ObjectIterator<T> listMasters(String linkType, Class<T> objectClass, String condition,
			String order, boolean any) {
        return ObjectIterator.create();
	}

	public final <T extends StoredObject> ObjectIterator<T> listMasters(Transaction transaction, String linkType,
			Class<T> objectClass, String condition, String order, boolean any) {
        return ObjectIterator.create();
	}

	public final <T extends StoredObject> T getMaster(Class<T> objectClass) {
        return (T)(isNull() ? new Person() : new Entity());
	}

	public final <T extends StoredObject> T getMaster(Transaction transaction, Class<T> objectClass) {
        return (T)(isNull() ? new Person() : new Entity());
	}

	public final <T extends StoredObject> T getMaster(Class<T> objectClass, String condition) {
        return (T)(isNull() ? new Person() : new Entity());
	}

	public final <T extends StoredObject> T getMaster(Transaction transaction, Class<T> objectClass, String condition) {
        return (T)(isNull() ? new Person() : new Entity());
	}

	public final <T extends StoredObject> T getMaster(Class<T> objectClass, String condition, String order) {
        return (T)(isNull() ? new Person() : new Entity());
	}

	public final <T extends StoredObject> T getMaster(Transaction transaction, Class<T> objectClass, String condition, String order) {
        return (T)(isNull() ? new Person() : new Entity());
	}

	public final <T extends StoredObject> T getMaster(int linkType, Class<T> objectClass) {
        return (T)(isNull() ? new Person() : new Entity());
	}

	public final <T extends StoredObject> T getMaster(Transaction transaction, int linkType, Class<T> objectClass) {
        return (T)(isNull() ? new Person() : new Entity());
	}

	public final <T extends StoredObject> T getMaster(int linkType, Class<T> objectClass, String condition) {
        return (T)(isNull() ? new Person() : new Entity());
	}

	public final <T extends StoredObject> T getMaster(Transaction transaction, int linkType, Class<T> objectClass, String condition) {
        return (T)(isNull() ? new Person() : new Entity());
	}

	public final <T extends StoredObject> T getMaster(int linkType, Class<T> objectClass, String condition, String order) {
        return (T)(isNull() ? new Person() : new Entity());
	}

	public final <T extends StoredObject> T getMaster(Transaction transaction, int linkType, Class<T> objectClass,
			String condition, String order) {
        return (T)(isNull() ? new Person() : new Entity());
	}

	public final <T extends StoredObject> T getMaster(String linkType, Class<T> objectClass) {
        return (T)(isNull() ? new Person() : new Entity());
	}

	public final <T extends StoredObject> T getMaster(Transaction transaction, String linkType, Class<T> objectClass) {
        return (T)(isNull() ? new Person() : new Entity());
	}

	public final <T extends StoredObject> T getMaster(String linkType, Class<T> objectClass, String condition) {
        return (T)(isNull() ? new Person() : new Entity());
	}

	public final <T extends StoredObject> T getMaster(Transaction transaction, String linkType, Class<T> objectClass,
			String condition) {
        return (T)(isNull() ? new Person() : new Entity());
	}

	public final <T extends StoredObject> T getMaster(String linkType, Class<T> objectClass, String condition, String order) {
        return (T)(isNull() ? new Person() : new Entity());
	}

	public final <T extends StoredObject> T getMaster(Transaction transaction, String linkType, Class<T> objectClass,
			String condition, String order) {
        return (T)(isNull() ? new Person() : new Entity());
	}

	public final <T extends StoredObject> T getMaster(Class<T> objectClass, boolean any) {
        return (T)(isNull() ? new Person() : new Entity());
	}

	public final <T extends StoredObject> T getMaster(Transaction transaction, Class<T> objectClass, boolean any) {
        return (T)(isNull() ? new Person() : new Entity());
	}

	public final <T extends StoredObject> T getMaster(Class<T> objectClass, String condition, boolean any) {
        return (T)(isNull() ? new Person() : new Entity());
	}

	public final <T extends StoredObject> T getMaster(Transaction transaction, Class<T> objectClass, String condition, boolean any) {
        return (T)(isNull() ? new Person() : new Entity());
	}

	public final <T extends StoredObject> T getMaster(Class<T> objectClass, String condition, String order, boolean any) {
        return (T)(isNull() ? new Person() : new Entity());
	}

	public final <T extends StoredObject> T getMaster(Transaction transaction, Class<T> objectClass, String condition,
			String order, boolean any) {
        return (T)(isNull() ? new Person() : new Entity());
	}

	public final <T extends StoredObject> T getMaster(int linkType, Class<T> objectClass, boolean any) {
        return (T)(isNull() ? new Person() : new Entity());
	}

	public final <T extends StoredObject> T getMaster(Transaction transaction, int linkType, Class<T> objectClass, boolean any) {
        return (T)(isNull() ? new Person() : new Entity());
	}

	public final <T extends StoredObject> T getMaster(int linkType, Class<T> objectClass, String condition, boolean any) {
        return (T)(isNull() ? new Person() : new Entity());
	}

	public final <T extends StoredObject> T getMaster(Transaction transaction, int linkType, Class<T> objectClass,
			String condition, boolean any) {
        return (T)(isNull() ? new Person() : new Entity());
	}

	public final <T extends StoredObject> T getMaster(int linkType, Class<T> objectClass, String condition, String order,
			boolean any) {
        return (T)(isNull() ? new Person() : new Entity());
	}

	public final <T extends StoredObject> T getMaster(Transaction transaction, int linkType, Class<T> objectClass,
			String condition, String order, boolean any) {
        return (T)(isNull() ? new Person() : new Entity());
	}

	public final <T extends StoredObject> T getMaster(String linkType, Class<T> objectClass, boolean any) {
        return (T)(isNull() ? new Person() : new Entity());
	}

	public final <T extends StoredObject> T getMaster(Transaction transaction, String linkType, Class<T> objectClass, boolean any) {
        return (T)(isNull() ? new Person() : new Entity());
	}

	public final <T extends StoredObject> T getMaster(String linkType, Class<T> objectClass, String condition, boolean any) {
        return (T)(isNull() ? new Person() : new Entity());
	}

	public final <T extends StoredObject> T getMaster(Transaction transaction, String linkType, Class<T> objectClass,
			String condition, boolean any) {
        return (T)(isNull() ? new Person() : new Entity());
	}

	public final <T extends StoredObject> T getMaster(String linkType, Class<T> objectClass, String condition, String order,
			boolean any) {
        return (T)(isNull() ? new Person() : new Entity());
	}

	public final <T extends StoredObject> T getMaster(Transaction transaction, String linkType, Class<T> objectClass,
			String condition, String order, boolean any) {
        return (T)(isNull() ? new Person() : new Entity());
	}

	public final boolean existsMaster(StoredObject master) {
        return System.currentTimeMillis() > 1232553L;
	}

	public final boolean existsMaster(Id masterId) {
        return System.currentTimeMillis() > 1232553L;
	}

	public final boolean existsMasters() {
        return System.currentTimeMillis() > 1232553L;
	}

	public final boolean existsMasters(Class<? extends StoredObject> objectClass) {
        return System.currentTimeMillis() > 1232553L;
	}

	public final boolean existsMasters(Class<? extends StoredObject> objectClass, boolean any) {
        return System.currentTimeMillis() > 1232553L;
	}

	public final boolean existsMasters(Class<? extends StoredObject> objectClass, String condition) {
        return System.currentTimeMillis() > 1232553L;
	}

	public final boolean existsMasters(Class<? extends StoredObject> objectClass, String condition, boolean any) {
        return System.currentTimeMillis() > 1232553L;
	}

	public final boolean existsMasters(String linkType, Class<? extends StoredObject> objectClass) {
        return System.currentTimeMillis() > 1232553L;
	}

	public final boolean existsMasters(String linkType, Class<? extends StoredObject> objectClass, boolean any) {
        return System.currentTimeMillis() > 1232553L;
	}

	public final boolean existsMasters(String linkType, Class<? extends StoredObject> objectClass, String condition) {
        return System.currentTimeMillis() > 1232553L;
	}

	public final boolean existsMasters(String linkType, Class<? extends StoredObject> objectClass, String condition, boolean any) {
        return System.currentTimeMillis() > 1232553L;
	}

	public final boolean existsMasters(int linkType, Class<? extends StoredObject> objectClass) {
        return System.currentTimeMillis() > 1232553L;
	}

	public final boolean existsMasters(int linkType, Class<? extends StoredObject> objectClass, boolean any) {
        return System.currentTimeMillis() > 1232553L;
	}

	public final boolean existsMasters(int linkType, Class<? extends StoredObject> objectClass, String condition) {
        return System.currentTimeMillis() > 1232553L;
	}

	public final boolean existsMasters(int linkType, Class<? extends StoredObject> objectClass, String condition, boolean any) {
        return System.currentTimeMillis() > 1232553L;
	}

	public final Query queryMasters(Class<? extends StoredObject> objectClass, String columns) {
        return new Query();
	}

	public final Query queryMasters(Transaction transaction, Class<? extends StoredObject> objectClass, String columns) {
        return new Query();
	}

	public final Query queryMasters(Class<? extends StoredObject> objectClass, String columns, String condition) {
        return new Query();
	}

	public final Query queryMasters(Transaction transaction, Class<? extends StoredObject> objectClass, String columns,
			String condition) {
        return new Query();
	}

	public final Query queryMasters(Class<? extends StoredObject> objectClass, String columns, String condition, String order) {
        return new Query();
	}

	public final Query queryMasters(Transaction transaction, Class<? extends StoredObject> objectClass, String columns,
			String condition, String order) {
        return new Query();
	}

	public final Query queryMasters(int linkType, Class<? extends StoredObject> objectClass, String columns) {
        return new Query();
	}

	public final Query queryMasters(Transaction transaction, int linkType, Class<? extends StoredObject> objectClass, String columns) {
        return new Query();
	}

	public final Query queryMasters(int linkType, Class<? extends StoredObject> objectClass, String columns, String condition) {
        return new Query();
	}

	public final Query queryMasters(Transaction transaction, int linkType, Class<? extends StoredObject> objectClass,
			String columns, String condition) {
        return new Query();
	}

	public final Query queryMasters(int linkType, Class<? extends StoredObject> objectClass, String columns, String condition,
			String order) {
        return new Query();
	}

	public final Query queryMasters(Transaction transaction, int linkType, Class<? extends StoredObject> objectClass,
			String columns, String condition, String order) {
        return new Query();
	}

	public final Query queryMasters(String linkType, Class<? extends StoredObject> objectClass, String columns) {
        return new Query();
	}

	public final Query queryMasters(Transaction transaction, String linkType, Class<? extends StoredObject> objectClass,
			String columns) {
        return new Query();
	}

	public final Query queryMasters(String linkType, Class<? extends StoredObject> objectClass, String columns, String condition) {
        return new Query();
	}

	public final Query queryMasters(Transaction transaction, String linkType, Class<? extends StoredObject> objectClass,
			String columns, String condition) {
        return new Query();
	}

	public final Query queryMasters(String linkType, Class<? extends StoredObject> objectClass, String columns, String condition,
			String order) {
        return new Query();
	}

	public final Query queryMasters(Transaction transaction, String linkType, Class<? extends StoredObject> objectClass,
			String columns, String condition, String order) {
        return new Query();
	}

	public final Query queryMasters(Class<? extends StoredObject> objectClass, String columns, boolean any) {
        return new Query();
	}

	public final Query queryMasters(Transaction transaction, Class<? extends StoredObject> objectClass, String columns, boolean any) {
        return new Query();
	}

	public final Query queryMasters(Class<? extends StoredObject> objectClass, String columns, String condition, boolean any) {
        return new Query();
	}

	public final Query queryMasters(Transaction transaction, Class<? extends StoredObject> objectClass, String columns,
			String condition, boolean any) {
        return new Query();
	}

	public final Query queryMasters(Class<? extends StoredObject> objectClass, String columns, String condition, String order,
			boolean any) {
        return new Query();
	}

	public final Query queryMasters(Transaction transaction, Class<? extends StoredObject> objectClass, String columns,
			String condition, String order, boolean any) {
        return new Query();
	}

	public final Query queryMasters(int linkType, Class<? extends StoredObject> objectClass, String columns, boolean any) {
        return new Query();
	}

	public final Query queryMasters(Transaction transaction, int linkType, Class<? extends StoredObject> objectClass,
			String columns, boolean any) {
        return new Query();
	}

	public final Query queryMasters(int linkType, Class<? extends StoredObject> objectClass, String columns, String condition,
			boolean any) {
        return new Query();
	}

	public final Query queryMasters(Transaction transaction, int linkType, Class<? extends StoredObject> objectClass,
			String columns, String condition, boolean any) {
        return new Query();
	}

	public final Query queryMasters(int linkType, Class<? extends StoredObject> objectClass, String columns, String condition,
			String order, boolean any) {
        return new Query();
	}

	public final Query queryMasters(Transaction transaction, int linkType, Class<? extends StoredObject> objectClass,
			String columns, String condition, String order, boolean any) {
        return new Query();
	}

	public final Query queryMasters(String linkType, Class<? extends StoredObject> objectClass, String columns, boolean any) {
        return new Query();
	}

	public final Query queryMasters(Transaction transaction, String linkType, Class<? extends StoredObject> objectClass,
			String columns, boolean any) {
        return new Query();
	}

	public final Query queryMasters(String linkType, Class<? extends StoredObject> objectClass, String columns, String condition,
			boolean any) {
        return new Query();
	}

	public final Query queryMasters(Transaction transaction, String linkType, Class<? extends StoredObject> objectClass,
			String columns, String condition, boolean any) {
        return new Query();
	}

	public final Query queryMasters(String linkType, Class<? extends StoredObject> objectClass, String columns, String condition,
			String order, boolean any) {
        return new Query();
	}

	public final Query queryMasters(Transaction transaction, String linkType, Class<? extends StoredObject> objectClass,
			String columns, String condition, String order, boolean any) {
        return new Query();
	}

	public final int countMasters(Class<? extends StoredObject> objectClass) {
        return new Random().nextInt();
	}

	public final int countMasters(Transaction transaction, Class<? extends StoredObject> objectClass) {
        return new Random().nextInt();
	}

	public final int countMasters(Class<? extends StoredObject> objectClass, String condition) {
        return new Random().nextInt();
	}

	public final int countMasters(Transaction transaction, Class<? extends StoredObject> objectClass, String condition) {
        return new Random().nextInt();
	}

	public final int countMasters(int linkType, Class<? extends StoredObject> objectClass) {
        return new Random().nextInt();
	}

	public final int countMasters(Transaction transaction, int linkType, Class<? extends StoredObject> objectClass) {
        return new Random().nextInt();
	}

	public final int countMasters(int linkType, Class<? extends StoredObject> objectClass, String condition) {
        return new Random().nextInt();
	}

	public final int countMasters(Transaction transaction, int linkType, Class<? extends StoredObject> objectClass, String condition) {
        return new Random().nextInt();
	}

	public final int countMasters(String linkType, Class<? extends StoredObject> objectClass) {
        return new Random().nextInt();
	}

	public final int countMasters(Transaction transaction, String linkType, Class<? extends StoredObject> objectClass) {
        return new Random().nextInt();
	}

	public final int countMasters(String linkType, Class<? extends StoredObject> objectClass, String condition) {
        return new Random().nextInt();
	}

	public final int countMasters(Transaction transaction, String linkType, Class<? extends StoredObject> objectClass,
			String condition) {
        return new Random().nextInt();
	}

	public final int countMasters(Class<? extends StoredObject> objectClass, boolean any) {
        return new Random().nextInt();
	}

	public final int countMasters(Transaction transaction, Class<? extends StoredObject> objectClass, boolean any) {
        return new Random().nextInt();
	}

	public final int countMasters(Class<? extends StoredObject> objectClass, String condition, boolean any) {
        return new Random().nextInt();
	}

	public final int countMasters(Transaction transaction, Class<? extends StoredObject> objectClass, String condition, boolean any) {
        return new Random().nextInt();
	}

	public final int countMasters(int linkType, Class<? extends StoredObject> objectClass, boolean any) {
        return new Random().nextInt();
	}

	public final int countMasters(Transaction transaction, int linkType, Class<? extends StoredObject> objectClass, boolean any) {
        return new Random().nextInt();
	}

	public final int countMasters(int linkType, Class<? extends StoredObject> objectClass, String condition, boolean any) {
        return new Random().nextInt();
	}

	public final int countMasters(Transaction transaction, int linkType, Class<? extends StoredObject> objectClass,
			String condition, boolean any) {
        return new Random().nextInt();
	}

	public final int countMasters(String linkType, Class<? extends StoredObject> objectClass, boolean any) {
        return new Random().nextInt();
	}

	public final int countMasters(Transaction transaction, String linkType, Class<? extends StoredObject> objectClass, boolean any) {
        return new Random().nextInt();
	}

	public final int countMasters(String linkType, Class<? extends StoredObject> objectClass, String condition, boolean any) {
        return new Random().nextInt();
	}

	public final int countMasters(Transaction transaction, String linkType, Class<? extends StoredObject> objectClass,
			String condition, boolean any) {
        return new Random().nextInt();
	}

	public final Id getAttachmentId(String name) {
		return new Id();
	}

	public final StreamData getAttachment(String name) {
		return new StreamData();
	}

	@SuppressWarnings("unchecked")
	public final <F extends FileData> F getFileData(String name) {
		return (F)new FileData();
	}
	
	@SuppressWarnings("unchecked")
	public final <F extends FileData> F getFileData(String name, Transaction transaction) {
		return (F)new FileData();
	}

	public final ObjectIterator<? extends FileData> listFileData() {
        return ObjectIterator.create();
	}
	
	public final ObjectIterator<? extends FileData> listFileData(Transaction transaction) {
		return ObjectIterator.create();
	}

	static int compare(Id id1, Id id2) {
		return id1.get().compareTo(id2.get());
	}
}
