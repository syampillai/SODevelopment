package com.storedobject.ui;

import com.storedobject.common.StringList;
import com.storedobject.core.Query;
import com.storedobject.vaadin.ListGrid;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Set;

public class QueryGrid extends ListGrid<QueryGrid.QueryResult> {

    public QueryGrid(Query query) {
        this(query.getResultSet());
    }

    public QueryGrid(ResultSet resultSet) {
        super(QueryResult.class, StringList.EMPTY);
    }

    public boolean includeColumn(int columnIndex) {
        return true;
    }

    public Object convertValue(Object value, int columnIndex) {
        return value;
    }

    public static class QueryResult extends HashMap<Integer, Object> {

        private QueryResult(ResultSet rs, Set<Integer> columnIndices) {
        }

        public Object getValue(int columnIndex) {
            return get(columnIndex);
        }
    }
}