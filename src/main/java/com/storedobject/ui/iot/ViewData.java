package com.storedobject.ui.iot;

import com.storedobject.iot.Block;

import java.util.function.Consumer;

public class ViewData extends SelectData {

    private final boolean canDownload;

    public ViewData() {
        this(null, true);
    }

    public ViewData(Block block) {
        this(block, true);
    }

    public ViewData(Block block, boolean canDownload) {
        super("View Data", block);
        this.canDownload = canDownload;
    }

    @Override
    protected Consumer<Data4Unit> getProcessor() {
        return ud -> new DataView<>(ud, canDownload).execute();
    }
}
