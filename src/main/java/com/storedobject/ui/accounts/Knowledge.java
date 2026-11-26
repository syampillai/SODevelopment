package com.storedobject.ui.accounts;

import com.storedobject.core.*;

import java.util.function.Predicate;

public class Knowledge extends com.storedobject.ui.ai.Knowledge {

    public Knowledge() {
        addDataClass(Account.class, "Chart", "Balance", "LocalCurrencyBalance");
        addDataClass(AccountChart.class);
    }

    @Override
    public <T extends StoredObject> void customize(QueryBuilder<T> queryBuilder) {
        if(queryBuilder.getObjectClass() == Account.class) {
            queryBuilder.any(true);
            queryBuilder.where("SystemEntity=" + getSystemEntity().getId());
        }
        super.customize(queryBuilder);
    }

    @Override
    public <T extends StoredObject> Predicate<T> getFilter(QueryBuilder<T> queryBuilder) {
        if(queryBuilder.getObjectClass() == Account.class) {
            return a -> !(a instanceof OffsetAccount || a instanceof AccountTitle);
        }
        return null;
    }

    @Override
    public String getTopic() {
        return "Accounts";
    }

    @Override
    public JSONMap getExtraKnowledge() {
        JSONMap m = new JSONMap();
        m.put("representation", "Debit-normal accounts carry negative balances by design. It is not an error.");
        return m;
    }
}
