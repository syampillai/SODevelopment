package  com.storedobject.core;

public interface ObjectSearcher<T extends StoredObject> {
    void setLoadFilter(ObjectLoadFilter<T> filter);
    ObjectLoadFilter<T> getLoadFilter();
    void search(SystemEntity systemEntity, ObjectSetter<T> setter);
    void search(SystemEntity systemEntity, ObjectSetter<T> setter, String extraFilter);
    void resetSearch();
    void populate(SystemEntity systemEntity);
    void populate(SystemEntity systemEntity, String extraFilter);
    void populate(SystemEntity systemEntity, ObjectIterator<T> objects);
    ObjectSearchBuilder<T> getSearchBuilder();
    int getObjectCount();
}
