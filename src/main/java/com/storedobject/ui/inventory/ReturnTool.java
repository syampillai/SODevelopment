package com.storedobject.ui.inventory;

import com.storedobject.common.SORuntimeException;
import com.storedobject.core.*;

public final class ReturnTool extends AbstractReturnMaterial {

    public ReturnTool() {
        this(RequestTool.per());
    }

    public ReturnTool(String from) {
        this(Person.get(from), from);
    }

    public ReturnTool(Person from) {
        this(from, null);
    }

    private ReturnTool(Person from, String name) {
        super(RequestTool.loc(from, name));
    }

    @Override
    void created() {
        super.created();
        setCaption("Return Tools");
    }

    @Override
    protected Class<? extends InventoryItem> itemClass() {
        if(itemClass != null && itemClass != InventoryItem.class) {
            return itemClass;
        }
        try {
            //noinspection unchecked
            itemClass = (Class<? extends InventoryItem>) JavaClassLoader.getLogic(GlobalProperty.get("TOOLS-CLASS"));
            return itemClass;
        } catch(Throwable ignored) {
        }
        throw new SORuntimeException("Unable to determine data class for tools!");
    }
}
