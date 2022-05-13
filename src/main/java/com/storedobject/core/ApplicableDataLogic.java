package com.storedobject.core;

import com.storedobject.core.annotation.*;

public class ApplicableDataLogic extends StoredObject implements Detail {

    private String dataLogic;

    public ApplicableDataLogic() {
    }

    public static void columns(Columns columns) {
        columns.add("DataLogic", "text");
    }

    public void setDataLogic(String dataLogic) {
        this.dataLogic = dataLogic;
    }

    @Column(order = 100, caption = "Data Logic Class Name")
    public String getDataLogic() {
        return dataLogic;
    }

    @Override
    public boolean isDetailOf(Class<? extends StoredObject> masterClass) {
        return masterClass == PrintLogicDefinition.class;
    }
}
