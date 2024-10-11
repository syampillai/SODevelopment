package com.storedobject.iot;

import com.storedobject.core.Id;

@FunctionalInterface
public interface ConsumptionCalculator {

    Double compute(int resource, Id unitId, long from, long to);

    static ConsumptionCalculator create(Class<? extends Data> dataClass, String variable) {
        return (resource, unitId, from, to) -> Data.getValueDifference(dataClass, unitId, variable, from, to);
    }
}
