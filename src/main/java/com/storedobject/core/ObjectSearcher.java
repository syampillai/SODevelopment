package com.storedobject.core;

public interface ObjectSearcher < T extends com.storedobject.core.StoredObject > {

    public void search(com.storedobject.core.SystemEntity p1, com.storedobject.core.ObjectSetter p2);

    public void search(com.storedobject.core.SystemEntity p1, com.storedobject.core.ObjectSetter p2, java.lang.String p3);

    public void setFilter(com.storedobject.core.ObjectSearchFilter p1);

    public com.storedobject.core.ObjectSearchFilter getFilter();

    public void resetSearch();

    public void populate(com.storedobject.core.SystemEntity p1);

    public void populate(com.storedobject.core.SystemEntity p1, java.lang.String p2);

    public void populate(com.storedobject.core.SystemEntity p1, com.storedobject.core.ObjectIterator < T > p2);

    public com.storedobject.core.ObjectSearchBuilder < T > getSearchBuilder();

    public int getObjectCount();
}
