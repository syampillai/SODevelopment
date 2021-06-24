package com.storedobject.core;

import com.storedobject.core.annotation.Column;

import java.math.BigDecimal;

public final class ModuleLogic extends Name implements Detail, DisplayOrder {

    public ModuleLogic() {
    }

    public static void columns(Columns columns) {
    }

    public void setMenuImage(Id menuImageId) {
    }

    public void setMenuImage(BigDecimal idValue) {
    }

    public void setMenuImage(MediaFile menuImage) {
    }

    @Column(order = 200)
    public Id getMenuImageId() {
        return null;
    }

    public MediaFile getMenuImage() {
        return null;
    }

    public void setLogic(Id logicId) {
    }

    public void setLogic(BigDecimal idValue) {
    }

    public void setLogic(Logic logic) {
    }

    public Id getLogicId() {
        return null;
    }

    public Logic getLogic() {
        return null;
    }

    @Override
    public void setDisplayOrder(int displayOrder) {
    }

    @Override
    public int getDisplayOrder() {
        return 0;
    }

    @Override
    public boolean isDetailOf(Class <? extends StoredObject > masterClass) {
        return false;
    }
}
