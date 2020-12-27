package com.storedobject.ui;

import com.storedobject.common.XML;
import org.w3c.dom.Node;
import java.util.ArrayList;

public class XMLGrid extends XGrid<XMLNodeData> {

    private String nodeDataPath;
    private boolean columns;

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
        this.columns = columns != null;
        this.nodeDataPath = nodeDataPath;
        if(xml != null) {
            setXML(xml);
        }
    }

    public void setNodeDataPath(String nodeDataPath) {
        this.nodeDataPath = nodeDataPath;
    }

    public void setXML(XML xml) {
        ArrayList<XMLNodeData> items = new ArrayList<>();
        ArrayList<Node> nodes;
        try {
            nodes = xml.listNodes(nodeDataPath);
            if(!columns && !nodes.isEmpty() && getDefinedColumnCount() == 0) {
                columns = true;
                new XMLNodeData(xml, nodes.get(0)).listTags().forEach(this::createColumn);
            }
            acceptNodes(nodes);
        } catch (Throwable e) {
            setItems(items);
            return;
        }
        nodes.stream().map(node -> new XMLNodeData(xml, node)).filter(this::acceptNodeData).forEach(items::add);
        acceptNodeData(items);
        setItems(items);
    }

    public void acceptNodes(@SuppressWarnings("unused") ArrayList<Node> nodes) {
    }

    public boolean acceptNodeData(@SuppressWarnings("unused") XMLNodeData xmlNodeData) {
        return true;
    }

    public void acceptNodeData(@SuppressWarnings("unused") ArrayList<XMLNodeData> nodeData) {
    }
}