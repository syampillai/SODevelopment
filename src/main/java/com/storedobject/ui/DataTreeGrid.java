package com.storedobject.ui;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalDataCommunicator;
import com.vaadin.flow.data.provider.hierarchy.HierarchyMapper;
import com.vaadin.flow.function.SerializableConsumer;

import java.lang.reflect.Method;

public abstract class DataTreeGrid<T> extends com.storedobject.vaadin.DataTreeGrid<T> implements Transactional {

    public DataTreeGrid(Class<T> objectClass) {
        this(objectClass, null);
    }

    public DataTreeGrid(Class<T> objectClass, Iterable<String> columns) {
        super(objectClass, DataGrid.columns(objectClass, columns));
        initScrollWhenReady();
    }

    /**
     * Scroll to a given item.
     *
     * @param item Item to scroll to.
     */
    public void scrollTo(T item) {
        int index = getIndexForItem(item);
        if (index >= 0) {
            this.getElement().executeJs("this.scrollWhenReady($0, true);", index);
        }
    }

    private int getIndexForItem(T item) {
        HierarchicalDataCommunicator<T> dataCommunicator = super.getDataCommunicator();
        Method getHierarchyMapper;
        try {
            getHierarchyMapper = HierarchicalDataCommunicator.class.getDeclaredMethod("getHierarchyMapper");
            getHierarchyMapper.setAccessible(true);
            @SuppressWarnings("unchecked")
            HierarchyMapper<T, ?> mapper = (HierarchyMapper<T, ?>)getHierarchyMapper.invoke(dataCommunicator);
            return mapper.getIndex(item);
        } catch (Exception ignored) {
        }
        return -1;
    }

    private void initScrollWhenReady() {
        runBeforeClientResponse(ui -> getElement()
                .executeJs("this.scrollWhenReady = function(index, firstCall){"
                        + "if(this.loading || firstCall){var that = this;"
                        + "setTimeout(function(){that.scrollWhenReady(index, false);}, 200);}"
                        + "else {this.scrollToIndex(index);}};"
                )
        );
    }

    private void runBeforeClientResponse(SerializableConsumer<UI> command) {
        getElement().getNode().runWhenAttached(ui -> ui.beforeClientResponse(this, context -> command.accept(ui)));
    }
}
