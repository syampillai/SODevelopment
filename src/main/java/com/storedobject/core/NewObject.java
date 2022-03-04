package com.storedobject.core;

import java.lang.reflect.Method;
import java.sql.Timestamp;

/**
 * Interface to generate new object of a specific type.
 *
 * @param <T> Object type.
 */
@FunctionalInterface
public interface NewObject<T> {

	/**
	 * Create a new instance of the object.
	 *
	 * @return New object created.
	 * @throws Exception If an exception occurs while creating a new instance.
	 */
	T newObject() throws Exception;

	/**
	 * Create a new instance of the object.
	 *
	 * @param tm Transaction manager.
	 * @return New object created.
	 * @throws Exception If an exception occurs while creating a new instance.
	 */
	default T newObject(TransactionManager tm) throws Exception {
		T object = newObject();
		if(tm != null && object instanceof StoredObject so) {
			if(so instanceof OfEntity) {
				setSystemEntity(tm, so);
			}
			setLocalTime(tm, so);
		}
		return object;
	}

	/**
	 * Set local time values corresponding to the {@link TransactionManager} to the object passed if it's just created
	 * now.
	 *
	 * @param tm Transaction manager.
	 * @param object Object to which local time values to be set.
	 */
	static void setLocalTime(TransactionManager tm, StoredObject object) {
		if(object == null) {
			return;
		}
		Method m;
		ClassAttribute<?> ca = ClassAttribute.get(object);
		for(String attribute: ca.getAttributes()) {
			m = ca.getMethod(attribute);
			if(m != null && m.getReturnType() == Timestamp.class) {
				Timestamp now = DateUtility.now();
				try {
					if(Math.abs(now.getTime() - ((Timestamp) m.invoke(object)).getTime()) <= 120000L) {
						m = ca.setMethod(attribute);
						if(m != null) {
							m.invoke(object, tm.date(now));
						}
					}
				} catch(Throwable ignored) {
				}
			}
		}
	}

	/**
	 * Set {@link SystemEntity} from the {@link TransactionManager} to the object passed if it's of type
	 * {@link OfEntity} and its system entity is not yet set.
	 *
	 * @param tm Transaction manager.
	 * @param object Object to which {@link SystemEntity} to be set.
	 */
	static void setSystemEntity(TransactionManager tm, StoredObject object) {
		if(object instanceof OfEntity o) {
			Id seId = o.getSystemEntityId();
			if(Id.isNull(seId)) {
				SystemEntity se = tm.getEntity();
				if(se != null) {
					o.setSystemEntity(se.getId());
				}
			}
		}
	}
}
