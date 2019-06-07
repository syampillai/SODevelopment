package com.storedobject.ui;

import com.storedobject.common.JSON;

import java.util.ArrayList;

public class JSONGrid extends XGrid<JSONData> {

    private boolean columns;

    public JSONGrid() {
        this(null, null);
    }

    public JSONGrid(Iterable<String> columns) {
        this(null, columns);
    }

    public JSONGrid(JSON json, Iterable<String> columns) {
        super(JSONData.class, columns);
    }

    public void setJSON(JSON json) {
    }

    public boolean acceptData(@SuppressWarnings("unused") JSONData jsonData) {
        return true;
    }

    public void acceptData(@SuppressWarnings("unused") ArrayList<JSONData> data) {
    }
}