package com.storedobject.core;

import javax.annotation.Nonnull;
import java.io.Closeable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Function;

public class Query implements Iterator<ResultSet>, Iterable<ResultSet>, Closeable {

    private RawSQL sql;
    private boolean read = false;

    Query() {
        this(new RawSQL());
    }

    Query(RawSQL sql) {
        this.sql = sql;
    }

    @SuppressWarnings("CopyConstructorMissesField")
    Query(Query query) {
        this(query.sql);
        query.sql = null;
    }

    @Override
    public void close() {
        if(sql != null) {
            sql.close();
            sql = null;
        }
    }

    @Override
    @Nonnull
    public Iterator<ResultSet> iterator() {
        return this;
    }

    public ResultSet getResultSet() {
        return sql.getResult();
    }

    @Override
    public ResultSet next() {
        if(sql == null) {
            throw new NoSuchElementException();
        }
        try {
            if(!sql.eoq() && read) {
                sql.skip();
            }
            if(sql.eoq()) {
                throw new NoSuchElementException();
            }
        } catch(Exception e) {
            throw new NoSuchElementException();
        }
        read = true;
        return sql.getResult();
    }

    @Override
    public boolean hasNext() {
        if(sql == null) {
            return false;
        }
        if(sql.eoq()) {
            sql.close();
            return false;
        }
        if(!read) {
            return true;
        }
        read = false;
        try {
            sql.skip();
            if(sql.eoq()) {
                sql.close();
                return false;
            }
        } catch(Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    public Iterable<String> stringIterator() {
        return stringIterator(1);
    }

    public Iterable<String> stringIterator(int column) {
        return new MappedIterator<>(rs -> {
            try {
                return rs.getString(column);
            } catch(SQLException e) {
                return null;
            }
        });
    }

    public Iterable<Integer> integerIterator() {
        return integerIterator(1);
    }

    public Iterable<Integer> integerIterator(int column) {
        return new MappedIterator<>(rs -> {
            try {
                return rs.getInt(column);
            } catch(SQLException e) {
                return null;
            }
        });
    }

    public Iterable<Id> idIterator() {
        return idIterator(1);
    }

    public Iterable<Id> idIterator(int column) {
        return new MappedIterator<>(rs -> {
            try {
                return new Id(rs.getBigDecimal(column));
            } catch(SQLException e) {
                return null;
            }
        });
    }

    public <T> Iterable<T> objectIterator(Function<ResultSet, T> objectMapper) {
        return new MappedIterator<>(objectMapper);
    }

    private class MappedIterator<T> implements Iterable<T>, Iterator<T> {

        private final Function<ResultSet, T> mapper;

        private MappedIterator(Function<ResultSet, T> mapper) {
            this.mapper = mapper;
        }

        @Override
        public boolean hasNext() {
            return Query.this.hasNext();
        }

        @Override
        public void remove() {
            Query.this.remove();
        }

        @Override
        @Nonnull
        public Iterator<T> iterator() {
            return this;
        }

        @Override
        public T next() {
            try {
                return mapper.apply(Query.this.next());
            } catch(Throwable error) {
                Query.this.close();
            }
            return null;
        }
    }

    public boolean eoq() {
        return !hasNext();
    }

    public Query skip(long skip) {
        sql.skip(skip);
        return this;
    }

    public Query limit(long limit) {
        return new LimitQuery(sql, limit);
    }

    private static class LimitQuery extends Query {

        private long limit;

        LimitQuery(RawSQL sql, long limit) {
            super(sql);
            this.limit = limit;
        }

        @Override
        public boolean hasNext() {
            if(limit <= 0) {
                close();
                return false;
            }
            return super.hasNext();
        }

        @Override
        public ResultSet next() {
            if(limit <= 0) {
                close();
                throw new NoSuchElementException();
            }
            --limit;
            return super.next();
        }
    }
}
