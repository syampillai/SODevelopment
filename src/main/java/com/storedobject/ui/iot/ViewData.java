package com.storedobject.ui.iot;

import com.storedobject.iot.Block;

import java.util.function.Consumer;

public class ViewData extends SelectData {

    public ViewData() {
        this(null);
    }

    public ViewData(Block block) {
        super("View Data", block);
    }

    @Override
    protected Consumer<Data4Unit> getProcessor() {
        return ud -> new DataView<>(ud).execute();
    }
}
