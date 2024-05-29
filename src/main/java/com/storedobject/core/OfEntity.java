package com.storedobject.core;

public interface OfEntity {

	Id getSystemEntityId();

	SystemEntity getSystemEntity();

	default void setSystemEntity(Id systemEntityId) {
	}

	default Id check(TransactionManager tm, Id systemEntityId) throws Exception {
		if(!(this instanceof StoredObject object)) {
			return systemEntityId;
		}
		Transaction t = object.getTransaction();
		if(t != null) {
			if(Id.isNull(systemEntityId)) {
				SystemEntity se = t.getManager().getEntity();
				if(se != null) {
					systemEntityId = se.getId();
				}
			}
			return tm.checkType(object, systemEntityId, SystemEntity.class,false);
		} else {
			return tm.checkType(object, systemEntityId, SystemEntity.class,true);
		}
	}

	default Id findSystemEntityId() {
		Id sid = getSystemEntityId();
		if(!Id.isNull(sid)) {
			if(this instanceof StoredObject so) {
				Transaction t = so.getTransaction();
				if(t != null) {
                    //noinspection DataFlowIssue
                    sid = t.getManager().getEntity().getId();
				}
			}
		}
		return sid;
	}
}
