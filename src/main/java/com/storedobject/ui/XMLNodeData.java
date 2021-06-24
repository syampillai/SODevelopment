package com.storedobject.ui;

import com.storedobject.common.XML;
import org.w3c.dom.Node;

import java.util.stream.Stream;

public class XMLNodeData implements XGrid.XData {

    private final XML xml;
    private final Node node;

    public XMLNodeData(XML xml, Node node) {
        this.xml = xml;
        this.node = node;
    }

    @Override
    public Object getDataValue(String columnName) {
        try {
            return xml.get(node, columnName);
        } catch (Exception e) {
            return "Error";
        }
    }

    @Override
    public String toString() {
        return xml.toString(node);
    }

    public Stream<String> listTags() {
        try {
            return xml.listNodes(node, "*").stream().map(Node::getNodeName);
        } catch (Exception e) {
            return Stream.of();
        }
    }
}
