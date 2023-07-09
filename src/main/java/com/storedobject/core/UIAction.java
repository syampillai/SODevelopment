package com.storedobject.core;

import com.storedobject.core.annotation.*;

public class UIAction extends StoredObject {

    private String action = "";
    private String description;

    public UIAction() {
    }

    public static void columns(Columns columns) {
        columns.add("Action", "text");
        columns.add("Description", "text");
    }

    public static void indices(Indices indices) {
        indices.add("Action", true);
    }

    public String getUniqueCondition() {
        return "Action='" + action.replace("'", "''") + "'";
    }

    public static int hints() {
        return ObjectHint.SMALL | ObjectHint.SMALL_LIST;
    }

    public void setAction(String action) {
        if (!loading()) {
            throw new Set_Not_Allowed("Action");
        }
        this.action = action;
    }

    @SetNotAllowed
    @Column(style = "(code)", order = 100)
    public String getAction() {
        return action;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(required = false, order = 200)
    public String getDescription() {
        return description;
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        if (StringUtility.isWhite(action)) {
            throw new Invalid_Value("Action");
        }
        action = toCode(action);
        checkForDuplicate("Action");
        super.validateData(tm);
    }

    @Override
    public String toString() {
        return action;
    }

    @Override
    public String toDisplay() {
        return action + " " + description;
    }

    public static UIAction get(String action) {
        action = toCode(action);
        UIAction a = get(UIAction.class, "Action='" + action + "'");
        if(a != null) {
            return a;
        }
        return list(UIAction.class, "Action LIKE '" + action + "%'").single(false);
    }

    public static ObjectIterator<UIAction> list(String action) {
        action = toCode(action);
        return list(UIAction.class, "Action LIKE '" + action + "%'");
    }
}
