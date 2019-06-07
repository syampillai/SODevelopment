package com.storedobject.ui;

import com.storedobject.common.XML;
import org.w3c.dom.Node;

import java.util.ArrayList;

public class XMLGrid extends XGrid<XMLNodeData> {

    public XMLGrid() {
        this(null, null, null);
    }

    public XMLGrid(XML xml, String nodeDataPath) {
        this(xml, nodeDataPath, null);
    }

    public XMLGrid(Iterable<String> columns) {
        this(null, null, columns);
    }

    public XMLGrid(String nodeDataPath) {
        this(null, nodeDataPath, null);
    }

    public XMLGrid(String nodeDataPath, Iterable<String> columns) {
        this(null, nodeDataPath, columns);
    }

    public XMLGrid(XML xml, String nodeDataPath, Iterable<String> columns) {
        super(XMLNodeData.class, columns);
    }

    public void setNodeDataPath(String nodeDataPath) {
    }

    public void setXML(XML xml) {
    }

    public void acceptNodes(@SuppressWarnings("unused") ArrayList<Node> nodes) {
    }

    public boolean acceptNodeData(@SuppressWarnings("unused") XMLNodeData xmlNodeData) {
        return true;
    }

    public void acceptNodeData(@SuppressWarnings("unused") ArrayList<XMLNodeData> nodeData) {
    }
}