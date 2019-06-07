package com.storedobject.ui;

import com.storedobject.common.XML;
import org.w3c.dom.Node;

import java.util.stream.Stream;

public class XMLNodeData implements XGrid.XData {

    public XMLNodeData(XML xml, Node node) {
    }

    @Override
    public Object getDataValue(String columnName) {
        return null;
    }

    public Stream<String> listTags() {
        return null;
    }
}
