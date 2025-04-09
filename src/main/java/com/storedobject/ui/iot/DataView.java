package com.storedobject.ui.iot;

import com.storedobject.iot.Data;
import com.storedobject.iot.Site;
import com.storedobject.iot.Unit;
import com.storedobject.ui.ELabel;
import com.storedobject.ui.ObjectGrid;
import com.storedobject.vaadin.Button;
import com.storedobject.vaadin.ButtonLayout;
import com.vaadin.flow.component.Component;

import java.sql.Timestamp;
import java.util.function.Predicate;

public class DataView<D extends Data> extends ObjectGrid<D> {

    private final Data4Unit data4Unit;
    private final Site site;
    private final boolean canDownload;

    public DataView(Data4Unit data4Unit, boolean canDownload) {
        //noinspection unchecked
        super((Class<D>) data4Unit.dataClass(), data4Unit.attributes());
        this.canDownload = canDownload;
        this.data4Unit = data4Unit;
        Unit unit = data4Unit.unit();
        this.site = unit.getSite();
        setFilter(data4Unit.condition(), false);
        setOrderBy("CollectedAt DESC", false);
        //noinspection unchecked
        setLoadFilter((Predicate<D>) data4Unit.sliceFilter(), false);
        load();
    }

    @SuppressWarnings("unused")
    public Timestamp getCollectedAt(D data) {
        return site.date(data.getTimestamp());
    }

    @Override
    public Component createHeader() {
        //noinspection resource
        return new ButtonLayout(new ELabel("Unit: " + data4Unit.unit().getName()),
                canDownload ? new Button("Download",
                        e -> new DataDownload(data4Unit).execute()) : null,
                new Button("Exit", e -> close()));
    }
}
