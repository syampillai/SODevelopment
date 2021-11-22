package com.storedobject.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class IdIterable implements Iterable<Id> {

    private final List<Iterable<?>> lists = new ArrayList<>();

    public IdIterable() {
    }

    public <T extends StoredObject> void addObjects(Iterable<T> objects) {
        lists.add(objects);
    }

    public void add(Iterable<Id> ids) {
        lists.add(ids);
    }

    @Override
    public Iterator<Id> iterator() {
        return new IdIterator();
    }

    private class IdIterator implements Iterator<Id> {

        private int i = 0;
        private Iterator<?> current;

        @Override
        public boolean hasNext() {
            if(current == null) {
                if(i < lists.size()) {
                    current = lists.get(i).iterator();
                    ++i;
                    if(current.hasNext()) {
                        return true;
                    }
                    return hasNext();
                }
                return false;
            }
            if(current.hasNext()) {
                return true;
            }
            ++i;
            return hasNext();
        }

        @Override
        public Id next() {
            Object o = current.next();
            return (o instanceof StoredObject so) ? so.getId() : (Id)o;
        }
    }
}
