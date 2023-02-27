package com.storedobject.core;

import com.storedobject.core.annotation.Column;
import java.math.BigDecimal;

public final class ModuleLogic extends StoredObject implements Detail, DisplayOrder {

    private Id menuImageId;
    private Id logicId = Id.ZERO;
    private int displayOrder = 0;
    private String name;

    public ModuleLogic() {
    }

    public static void columns(Columns columns) {
        columns.add("Name", "text");
        columns.add("MenuImage", "id");
        columns.add("Logic", "id");
        columns.add("DisplayOrder", "int");
    }

    public static String[] links() {
        return new String[] { "Modules|ModuleLogic|DisplayOrder||0" };
    }

    public static String[] browseColumns() {
        return new String[] { "Name", "Logic", "MenuImage.Name as Image Name", "DisplayOrder" };
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(order = 100)
    public String getName() {
        return name;
    }

    public void setMenuImage(Id menuImageId) {
        this.menuImageId = menuImageId;
    }

    public void setMenuImage(BigDecimal idValue) {
        setMenuImage(new Id(idValue));
    }

    public void setMenuImage(MediaFile menuImage) {
        setMenuImage(menuImage == null ? null : menuImage.getId());
    }

    @Column(order = 200)
    public Id getMenuImageId() {
        return menuImageId;
    }

    public MediaFile getMenuImage() {
        return get(MediaFile.class, menuImageId);
    }

    public void setLogic(Id logicId) {
        this.logicId = logicId;
    }

    public void setLogic(BigDecimal idValue) {
        setLogic(new Id(idValue));
    }

    public void setLogic(Logic logic) {
        setLogic(logic == null ? null : logic.getId());
    }

    @Column(required = false, order = 300)
    public Id getLogicId() {
        return logicId;
    }

    public Logic getLogic() {
        return get(Logic.class, logicId);
    }

    @Override
    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    @Override
    @Column(required = false, order = 400)
    public int getDisplayOrder() {
        return displayOrder;
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        if(StringUtility.isWhite(name)) {
            throw new Invalid_Value("Name");
        }
        menuImageId = tm.checkType(this, menuImageId, MediaFile.class, false);
        logicId = tm.checkType(this, logicId, Logic.class, true);
        super.validateData(tm);
    }

    @Override
    public Id getUniqueId() {
        return getId();
    }

    @Override
    public boolean isDetailOf(Class <? extends StoredObject > masterClass) {
        return masterClass == ApplicationModule.class || masterClass == ModuleLogic.class;
    }

    @Override
    public String getTitle() {
        return name;
    }

    @Override
    public void setTitle(String title) {
        setName(title);
    }
}
