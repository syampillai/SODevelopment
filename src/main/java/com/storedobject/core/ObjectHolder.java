package com.storedobject.core;

import java.util.List;

import com.storedobject.core.StoredObjectUtility.Link;

public class ObjectHolder<T extends StoredObject> implements ObjectSetter, ObjectGetter {

	public enum Status {
		EMPTY,
		NONE,
		NEW,
		MODIFIED,
		DELETED,
		UNKNOWN
	}

	public ObjectHolder(Class<T> objectClass) {
	}

	public ObjectHolder(T value) {
	}

	public Class<T> getObjectClass() {
		return null;
	}

	public boolean getAllowAny() {
		return false;
	}
	
	public void setAllowAny(boolean allowAny) {
	}
	
	public final boolean isAllowAny() {
		return getAllowAny();
	}

	@Override
	public void setObject(StoredObject object) {
	}

	@Override
	public void setObject(Id objectId) {
	}
	
	@Override
	public T getObject() {
		return null;
	}

	@Override
	public Id getObjectId() {
		return null;
	}
	
	public ObjectConverter<T, T> getLoadFilter() {
		return null;
	}

	public ObjectSearcher<T> getSearcher() {
		return null;
	}

	public void setSearcher(ObjectSearcher<T> searcher) {
	}

	public void search(SystemEntity systemEntity) {
	}

	public Status getStatus() {
		return null;
	}

	public boolean isEmpty() {
		return false;
	}

	public boolean isDeleted() {
		return false;
	}

	public boolean isModified() {
		return false;
	}

	public boolean delete() {
		return false;
	}

	public boolean undelete() {
		return false;
	}

	public boolean modifiy() {
		return false;
	}

	protected void valueChanged() {
	}

	protected void statusChanged() {
	}

	public void reload() {
	}

	public void removeLink(Link<?> link) {
	}

	public void addLink(Link<? extends StoredObject> link) {
	}

	public void createAllLinks() {
	}

	public List<ObjectHolder<StoredObject>> getLinkData(Link<?> link) {
		return null;
	}

	public List<ObjectHolder<StoredObject>> getLinkData(String linkName) {
		return null;
	}

	public LinkHolder getLinkHolder(String linkName) {
		return null;
	}

	public boolean delete(TransactionControl tc) {
		return false;
	}

	public boolean save(TransactionControl tc) {
		return false;
	}

	public boolean saveLinks(TransactionControl tc) {
		return false;
	}

	protected boolean save(T object, TransactionControl tc) throws Exception {
		return false;
	}

	protected boolean delete(T object, TransactionControl tc) throws Exception {
		return false;
	}

	public void deleted(T object, TransactionControl transactionControl) {
	}

	public void saved(T object, TransactionControl transactionControl) {
	}

	public void committed(T object) {
	}

	public void rolledback(T object) {
	}

	public ObjectSearchFilter getSearchFilter() {
		return null;
	}

	public void setSearchFilter(ObjectSearchFilter searchFilter) {
	}

	public StoredObject getParentObject() {
		return null;
	}

	public void setParentObject(StoredObject parentObject) {
	}

	public int getParentLinkType() {
		return 0;
	}

	public void setParentLinkType(int parentLinkType) {
	}

	protected <L extends StoredObject> ObjectHolder<L> createLinkEntry(L object) {
		return null;
	}

	public class LinkHolder {

		private LinkHolder(Link<?> link) {
		}

		public List<ObjectHolder<StoredObject>> getData() {
			return null;
		}

		public void reload() {
		}

		public Link<?> getLink() {
			return null;
		}

		public boolean isDetail() {
			return false;
		}

		public Class<T> getMasterClass() {
			return null;
		}

		public ObjectHolder<T> getMaster() {
			return null;
		}

		public ObjectHolder<StoredObject> getHolder(Id id) {
			return null;
		}

		public StoredObject get(Id id) {
			return null;
		}

		public Id add(StoredObject object) {
			return null;
		}

		public boolean delete(Id id) {
			return true;
		}

		public boolean undelete(Id id) {
			return false;
		}

		public boolean modify(Id id) {
			return true;
		}

		public boolean isLoaded() {
			return false;
		}

		public boolean isChanged() {
			return false;
		}
	}
}