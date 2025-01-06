package com.storedobject.core;

import javax.annotation.Nonnull;
import java.io.Closeable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Function;

/**
 * The Query class provides a mechanism to iterate over rows in a SQL result set.
 * It implements {@code Iterator<ResultSet>}, {@code Iterable<ResultSet>}, and {@code Closeable}
 * interfaces to facilitate traversing and managing the lifecycle of database query results using for/for each/while
 * loops.
 * <p>
 * Query objects allow navigation through the result set, provide means to map results
 * into other data types, and support optional transformation functions to manipulate individual
 * rows.
 * </p>
 *
 * @author Syam
 */
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

    /**
     * Closes the resources associated with this query.
     * This method ensures that any open database resources, represented
     * by the {@code sql} object, are properly cleaned up. After the
     * cleanup, the {@code sql} field is set to {@code null} to indicate
     * that the associated resources have been released. Repeated calls
     * to this method are safe and effectively have no additional effect
     * once the resources have already been closed and {@code sql} is null.
     */
    @Override
    public void close() {
        if(sql != null) {
            sql.close();
            sql = null;
        }
    }

    /**
     * Returns an iterator for traversing the results of the query.
     *
     * @return an iterator over the {@link ResultSet} objects produced by the query
     */
    @Override
    @Nonnull
    public Iterator<ResultSet> iterator() {
        return this;
    }

    /**
     * Retrieves the current result set of the query.
     *
     * @return the {@code ResultSet} object obtained from the underlying SQL query execution.
     */
    public ResultSet getResultSet() {
        return sql.getResult();
    }

    /**
     * Retrieves the next result set from the query. If no result set is available or
     * the query is invalid, this method will throw a {@link NoSuchElementException}.
     * It skips over the current result set if it has already been read before
     * retrieving the next result.
     *
     * @return the next {@link ResultSet} retrieved from the query
     * @throws NoSuchElementException if there are no more results available or
     *         the query is invalid
     */
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

    /**
     * Checks if there are additional results available in the query.
     * This method determines whether there are more elements to process in the query.
     * It manages conditions such as end-of-query state, ensures the query is properly
     * closed when no further results are available, and skips the current result if necessary.
     *
     * @return {@code true} if additional results are available; {@code false} otherwise.
     */
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

    /**
     * Removes the current element from the underlying data structure.
     * This method is unsupported for the current implementation and will always
     * throw an {@link UnsupportedOperationException}. It is included to fulfill
     * the {@link Iterator} interface contract.
     *
     * @throws UnsupportedOperationException always, as this operation is not supported.
     */
    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    /**
     * Provides an iterable sequence of string values retrieved from the query's result set,
     * defaulting to the first column (column index 1).
     *
     * @return an {@code Iterable<String>} representing the sequence of string values from the result set
     *         in the specified column.
     */
    public Iterable<String> stringIterator() {
        return stringIterator(1);
    }

    /**
     * Provides an iterable over a specific column in the result set, extracting its values as strings.
     *
     * @param column the column index (1-based) to retrieve string values from
     * @return an iterable of strings corresponding to the specified column in the result set
     */
    public Iterable<String> stringIterator(int column) {
        return new MappedIterator<>(rs -> {
            try {
                return rs.getString(column);
            } catch(SQLException e) {
                return null;
            }
        });
    }

    /**
     * Creates an iterable that iterates over the first column of the result set,
     * mapping each row to an integer value.
     *
     * @return an {@code Iterable} that provides integer values from the first column of the result set
     */
    public Iterable<Integer> integerIterator() {
        return integerIterator(1);
    }

    /**
     * Creates an iterable over integers retrieved from a specified column of a database result set.
     * The method maps the values obtained from the given column index of the result set
     * into Integer objects. If an SQL exception occurs during the retrieval of a value,
     * it will return {@code null} for that entry.
     *
     * @param column the column index of the result set from which integers are to be retrieved
     * @return an Iterable of integers corresponding to the specified column's values in the result set
     */
    public Iterable<Integer> integerIterator(int column) {
        return new MappedIterator<>(rs -> {
            try {
                return rs.getInt(column);
            } catch(SQLException e) {
                return null;
            }
        });
    }

    /**
     * Provides an iterable over the query results, using the default column index.
     *
     * @return an {@link Iterable} of {@link Id} objects from the query results.
     */
    public Iterable<Id> idIterator() {
        return idIterator(1);
    }

    /**
     * Provides an iterable of {@code Id} objects by mapping the result set from a specific
     * column index of a database query to instances of {@code Id}.
     *
     * @param column the column index to extract {@code Id} values from the result set
     * @return an {@code Iterable<Id>} that iterates over the {@code Id} objects from the specified column
     */
    public Iterable<Id> idIterator(int column) {
        return new MappedIterator<>(rs -> {
            try {
                return new Id(rs.getBigDecimal(column));
            } catch(SQLException e) {
                return null;
            }
        });
    }

    /**
     * Creates an {@code Iterable} that maps each {@code ResultSet} entry from the query
     * into an object of type {@code T} using the provided {@code objectMapper} function.
     *
     * @param <T> the type of the object to be created from each row of the {@code ResultSet}
     * @param objectMapper a {@code Function} that maps a {@code ResultSet} to an object of type {@code T}
     * @return an {@code Iterable} over objects of type {@code T}, representing the mapped entries from the query
     */
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

    /**
     * Determines if the end of the query has been reached.
     * This method is a utility to check if there are no more results available
     * in the query being processed. It internally calls the {@code hasNext()}
     * method to verify if additional elements exist and negates its result.
     *
     * @return {@code true} if the end of the query has been reached (no more results);
     *         {@code false} otherwise.
     */
    public boolean eoq() {
        return !hasNext();
    }

    /**
     * Skips a specified number of rows in the result set.
     *
     * @param skip the number of rows to skip
     * @return the current Query instance, for method chaining
     */
    public Query skip(long skip) {
        sql.skip(skip);
        return this;
    }

    /**
     * Limits the number of results returned by the query.
     *
     * @param limit the maximum number of results to be returned
     * @return a new Query instance with the specified limit applied
     */
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
