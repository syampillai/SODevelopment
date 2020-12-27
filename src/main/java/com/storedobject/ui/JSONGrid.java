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
        this.columns = columns != null;
        if(json != null) {
            setJSON(json);
        }
    }

    public void setJSON(JSON json) {
        if(json.getType() != JSON.Type.ARRAY) {
            return;
        }
        ArrayList<JSONData> items = new ArrayList<>();
        if(!columns && json.getArraySize() > 0 && getDefinedColumnCount() == 0) {
            columns = true;
            json.get(0).keys().forEach(this::createColumn);
        }
        JSONData jsonData;
        for(int i = 0; i < json.getArraySize(); i++) {
            jsonData = new JSONData(json.get(i));
            if(acceptData(jsonData)) {
                items.add(jsonData);
            }
        }
        acceptData(items);
        setItems(items);
    }

    public boolean acceptData(@SuppressWarnings("unused") JSONData jsonData) {
        return true;
    }

    public void acceptData(@SuppressWarnings("unused") ArrayList<JSONData> data) {
    }
}