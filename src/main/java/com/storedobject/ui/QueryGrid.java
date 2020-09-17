package com.storedobject.ui;

import com.storedobject.common.StringList;
import com.storedobject.core.Query;
import com.storedobject.vaadin.ListGrid;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class QueryGrid extends ListGrid<QueryGrid.SQLResultSet> {

    private final Map<Integer, String> columnNames = new HashMap<>();

    public QueryGrid(Query query) {
        this(query.getResultSet());
    }

    public QueryGrid(ResultSet resultSet) {
        super(SQLResultSet.class, StringList.EMPTY);
    }

    public boolean includeColumn(int columnIndex) {
        return true;
    }

    public Object convertValue(Object value, int columnIndex) {
        return value;
    }

    static class SQLResultSet extends HashMap<Integer, Object> {

        SQLResultSet(ResultSet rs, Set<Integer> columnIndices) {
        }

        Object getValue(int columnIndex) {
            return get(columnIndex);
        }
    }
}
