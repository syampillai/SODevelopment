package com.storedobject.ui.iot;

import com.storedobject.iot.Block;
import com.storedobject.iot.Unit;
import com.storedobject.iot.ValueDefinition;
import com.storedobject.vaadin.ComboField;
import com.storedobject.vaadin.DataForm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public class DownloadData extends SelectData {

    private final List<DataDownload> list = new ArrayList<>();

    public DownloadData() {
        this(null);
    }

    public DownloadData(Block block) {
        super("Download Data", block);
    }

    @Override
    protected Consumer<Data4Unit> getProcessor() {
        //noinspection
        return ud -> list.add(new DataDownload(ud));
    }

    @Override
    protected void process(Unit unit, @SuppressWarnings("rawtypes") Collection<ValueDefinition> valueDefinitions) {
        super.process(unit, valueDefinitions);
        if(!list.isEmpty()) {
            new DD(list).execute();
        }
    }

    private static class DD extends DataForm {

        private final ComboField<DataDownload> ddField;
        private final List<DataDownload> list;

        public DD(List<DataDownload> list) {
            super("Download Data");
            this.list = list;
            ddField = new ComboField<>("Select", list);
            ddField.setItemLabelGenerator(DataDownload::getFileName);
            addField(ddField);
            ddField.setValue(list.get(0));
        }

        @Override
        protected boolean process() {
            close();
            DataDownload dd = ddField.getValue();
            list.remove(dd);
            dd.execute();
            if (!list.isEmpty()) {
                new DD(list).execute();
            }
            return true;
        }

        @Override
        public int getMinimumContentWidth() {
            return 40;
        }
    }
}
