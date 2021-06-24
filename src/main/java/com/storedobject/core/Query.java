package com.storedobject.core;

import javax.annotation.Nonnull;
import java.io.Closeable;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Query implements Iterator < ResultSet >, Iterable < ResultSet >, Closeable {

    Query() {
    }

    Query(RawSQL sql) {
        this();
    }

    Query(Query another) {
        this();
    }

    @Override
	public boolean hasNext() {
        return false;
    }

    @Nonnull
    @Override
	public Iterator<ResultSet> iterator() {
        return this;
    }

    @Override
	public ResultSet next() {
        return new RawSQL().getResult();
    }

    public Stream<ResultSet> stream() {
        return StreamSupport.stream(this.spliterator(), false);
    }

    @Override
	public void remove() {
    }

    @Override
	public void close() {
    }

    public ResultSet getResultSet() {
        return next();
    }
	
	public Iterable<String> stringIterator() {
		return new ArrayList<>();
	}
	
	public Iterable<String> stringIterator(int column) {
		return new ArrayList<>();
	}
	
	public Iterable<Integer> integerIterator() {
		return new ArrayList<>();
	}
	
	public Iterable<Integer> integerIterator(int column) {
		return new ArrayList<>();
	}
	
	public Iterable<Id> idIterator() {
		return new ArrayList<>();
	}
	
	public Iterable<Id> idIterator(int column) {
		return new ArrayList<>();
	}
}
