package com.storedobject.ui.inventory;

import com.storedobject.common.SORuntimeException;
import com.storedobject.core.*;
import com.storedobject.ui.Application;

public class RequestTool extends RequestMaterial {

    public RequestTool() {
        this(per());
    }

    public RequestTool(String from) {
        this(Person.get(from), from);
    }

    public RequestTool(Person from) {
        this(from, null);
    }

    private RequestTool(Person from, String name) {
        super(loc(from, name));
    }

    static Person per() {
        Application a = Application.get();
        return a == null ? null : a.getTransactionManager().getUser().getPerson();
    }

    static InventoryCustodyLocation loc(Person from, String name) {
        InventoryCustodyLocation cl = InventoryCustodyLocation.getForPerson(from);
        if(cl == null) {
            if(from != null) {
                name = from.getName();
            }
            if(name == null || name.isBlank()) {
                name = "Unknown person";
            }
            throw new SORuntimeException("Unable to determine custody location for '" + name + "'");
        }
        return cl;
    }

    @Override
    void created() {
        super.created();
        setCaption("Request Tools");
    }

    @Override
    protected Class<? extends InventoryItemType> itemTypeClass() {
        if(itemTypeClass != null && itemTypeClass != InventoryItemType.class) {
            return itemTypeClass;
        }
        try {
            //noinspection unchecked
            itemTypeClass = (Class<? extends InventoryItemType>) JavaClassLoader.getLogic(GlobalProperty.get("TOOLS-TYPE-CLASS"));
            return itemTypeClass;
        } catch(Throwable ignored) {
        }
        throw new SORuntimeException("Unable to determine data class for tools type!");
    }
}
