package com.storedobject.iot;

import com.storedobject.common.SORuntimeException;
import com.storedobject.core.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class SuperUnit extends Unit implements HasChildren {

    private Map<String, Id> statistics;

    public SuperUnit() {
    }

    public static void columns(Columns columns) {
    }

    public static String[] links() {
        return new String[] {
                "Units|com.storedobject.iot.Unit/Any",
        };
    }

    public static SuperUnit get(String name) {
        return StoredObjectUtility.get(SuperUnit.class, "Name", name, true);
    }

    public static ObjectIterator<? extends SuperUnit> list(String name) {
        return StoredObjectUtility.list(SuperUnit.class, "Name", name, true);
    }

    @Override
    final Double consumption(int resource, long from, long to) {
        throw new SORuntimeException();
    }

    @Override
    public void validateChildAttach(StoredObject child, int linkType) throws Exception {
        super.validateChildAttach(child, linkType);
        if(child instanceof Unit u) {
            if(u.getId().equals(getId())) {
                throw new Invalid_State("Can not add self as a sub-unit");
            }
            if(checkCircular(getId(), u)) {
                throw new Invalid_State("Circular dependency detected - " + u.toDisplay());
            }
            if(!u.getBlockId().equals(getBlockId())) {
                throw new Invalid_State("Sub-unit Does not belong to the same block - " + u.toDisplay());
            }
        }
    }

    private static boolean checkCircular(Id myId, Unit child) {
        if(myId.equals(child.getId())) {
            return true;
        }
        if(!(child instanceof SuperUnit su)) {
            return false;
        }
        List<Unit> children = su.listImmediateChildren();
        for(Unit c: children) {
            if(checkCircular(myId, c)) {
                return true;
            }
        }
        return false;
    }

    List<Unit> childrenAll() throws SOException {
        List<Unit> children = listImmediateChildren();
        for (Unit c : children) {
            if (checkCircular(getId(), c)) {
                throw new Invalid_State("Circular dependency detected - " + c.toDisplay());
            }
            if (!c.getBlockId().equals(getBlockId())) {
                throw new Invalid_State("Sub-unit Does not belong to the same block - " + c.toDisplay());
            }
        }
        return listAllChildren();
    }

    public List<Unit> listAllChildren() {
        List<Unit> children = listImmediateChildren();
        List<Unit> allChildren = new ArrayList<>();
        for(Unit c: children) {
            if(c instanceof SuperUnit su) {
                allChildren.addAll(su.listAllChildren());
            } else {
                allChildren.add(c);
            }
        }
        return allChildren;
    }

    public int getTotalChildCount() {
        return listImmediateChildren().size();
    }

    public List<Unit> listImmediateChildren() {
        return listLinks(Unit.class, true).toList();
    }

    public int getImmediateChildCount() {
        return listImmediateChildren().size();
    }

    @Override
    protected final Double computeConsumption(int resource, long from, long to) {
        throw new SORuntimeException();
    }

    @Override
    public final void recomputeStatistics(TransactionManager tm) {
    }

    @Override
    public final void computeStatistics(TransactionManager tm) {
    }

    @Override
    Id unitId4Statistics(String name) {
        return unitId4Statistics(name, 0);
    }

    private Id unitId4Statistics(String name, int depth) {
        Id id = statistics == null ? null : statistics.get(name);
        if(id == null) {
            Unit unit = getUnitForStatistics(name);
            if(unit == null) {
                id = Id.ZERO;
            } else {
                if(unit instanceof SuperUnit su) {
                    id = depth > 20 ? Id.ZERO : su.unitId4Statistics(name, ++ depth);
                } else {
                    id = unit.getId();
                }
            }
        }
        if(statistics == null) {
            statistics = new HashMap<>();
        }
        statistics.put(name, id);
        return id;
    }

    @Override
    public void saved() {
        statistics = null;
    }

    protected Unit getUnitForStatistics(String name) {
        return null;
    }
}
