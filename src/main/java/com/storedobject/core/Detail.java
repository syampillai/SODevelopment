package com.storedobject.core;

public interface Detail {

    public com.storedobject.core.Id getId();

    public boolean isDetailOf(java.lang.Class <? extends com.storedobject.core.StoredObject > p1);

    public void copyValuesFrom(com.storedobject.core.Detail p1);

    public com.storedobject.core.Id getUniqueId();
}
