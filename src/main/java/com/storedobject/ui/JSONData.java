package com.storedobject.ui;

import com.storedobject.common.JSON;

public class JSONData implements XMLGrid.XData {

    private final JSON json;

    public JSONData(JSON json) {
        this.json = json;
    }

    @Override
    public Object getDataValue(String columnName) {
        return null;
    }

    public JSON getJSON() {
        return json;
    }
}
