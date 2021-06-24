package com.storedobject.ui;

import com.storedobject.pdf.PDF;
import com.storedobject.vaadin.HTMLGenerator;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.Query;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

public class DataGrid<T> extends com.storedobject.vaadin.DataGrid<T> implements Transactional {

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private final Map<String, Function<T, ?>[]> renderers = new HashMap<>();
    private boolean printing = false;

    public DataGrid(Class<T> objectClass) {
        this(objectClass, null);
    }

    public DataGrid(Class<T> objectClass, Iterable<String> columns) {
        super(objectClass, columns);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setRendererFunctions(String columnName, boolean html, Function<T, ?>... functions) {
        if (html) {
            functions[0] = new HTMLFunction((Function<T, HTMLGenerator>) functions[0]);
        }
        renderers.put(columnName, functions);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Application getApplication() {
        return super.getApplication();
    }

    class HTMLFunction implements Function<T, HTMLGenerator> {

        private final Function<T, HTMLGenerator> generator;

        private HTMLFunction(Function<T, HTMLGenerator> generator) {
            this.generator = generator;
        }

        @Override
        public HTMLGenerator apply(T object) {
            return generator.apply(object);
        }
    }

    public void print() {
        printing = true;
        getApplication().view(new Report());
    }

    protected GridCellText getGridCellText() {
        return printing ? new StyledString() : new HTMLText();
    }

    private class Report extends PDF {

        @SuppressWarnings("unchecked")
        @Override
        public void generateContent() {
            Query<T, String> q = new Query<>(0, Integer.MAX_VALUE, Collections.emptyList(), null, null);
            Stream<T> stream = ((DataProvider<T, String>) getDataProvider()).fetch(q);
            stream.forEach(System.err::println);
            printing = false;
        }
    }
}