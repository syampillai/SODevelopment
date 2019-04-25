package com.storedobject.core;

public class Query implements java.util.Iterator < java.sql.ResultSet >, java.lang.Iterable < java.sql.ResultSet >, java.io.Closeable {

    protected Query() {
    }

    protected Query(com.storedobject.core.RawSQL p1) {
        this();
    }

    protected Query(com.storedobject.core.Query p1) {
        this();
    }

    @Override
	protected void finalize() {
    }

    @Override
	public boolean hasNext() {
        return false;
    }

    @Override
	public java.util.Iterator < java.sql.ResultSet > iterator() {
        return null;
    }

    @Override
	public java.sql.ResultSet next() {
        return null;
    }

    @Override
	public void remove() {
    }

    @Override
	public void close() {
    }

    public java.sql.ResultSet getResultSet() {
        return null;
    }
	
	public Iterable<String> stringIterator() {
		return null;
	}
	
	public Iterable<String> stringIterator(int column) {
		return null;
	}
	
	public Iterable<Integer> integerIterator() {
		return null;
	}
	
	public Iterable<Integer> integerIterator(int column) {
		return null;
	}
	
	public Iterable<Id> idIterator() {
		return null;
	}
	
	public Iterable<Id> idIterator(int column) {
		return null;
	}
}
