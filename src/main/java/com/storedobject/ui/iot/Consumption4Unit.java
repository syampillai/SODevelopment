package com.storedobject.ui.iot;

import com.storedobject.core.JavaClassLoader;
import com.storedobject.core.ObjectIterator;
import com.storedobject.core.StoredObject;
import com.storedobject.iot.*;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public record Consumption4Unit<C extends Consumption<?>>(Class<C> consumptionClass, Resource resource, AbstractUnit unit,
                               int yearFrom, int yearTo) {

    public static void process(Resource resource, Block block, int periodicity, int yearFrom, int yearTo,
                               Consumer<Consumption4Unit<?>> processor) {
        Class<? extends Consumption<?>> cClass;
        try {
            //noinspection unchecked
            cClass = (Class<? extends Consumption<?>>)
                    JavaClassLoader.getLogic("com.storedobject.iot."
                            + ConsumptionDashboard.periodicity.get(periodicity) + "Consumption");
        } catch (ClassNotFoundException e) {
            return;
        }
        AtomicInteger count = new AtomicInteger(0);
        StoredObject.list(Unit.class, "Block=" + block.getId() + " AND Active", true)
                .filter(u -> u.consumes(resource.getCode()))
                .forEach(u -> {
            processor.accept(new Consumption4Unit<>(cClass, resource, u, yearFrom, yearTo));
            count.incrementAndGet();
            StoredObject.list(UnitItem.class, "Unit=" + u.getId() + " AND Active", true)
                    .forEach(ui -> {
                        processor.accept(new Consumption4Unit<>(cClass, resource, ui, yearFrom, yearTo));
                        count.incrementAndGet();
                    });
        });
        if(count.get() > 1) {
            processor.accept(new Consumption4Unit<>(cClass, resource, block, yearFrom, yearTo));
        }
    }

    String condition() {
        return "Year BETWEEN " + yearFrom + " AND " + yearTo + " AND Resource="
                + resource.getId() + " AND Item=" + unit.getId();
    }

    String orderBy() {
        return "Year DESC" + ((consumptionClass == YearlyConsumption.class) ? "" : ("," + periodName() + " DESC"));
    }

    String periodName() {
        try {
            return consumptionClass.getDeclaredConstructor().newInstance().getPeriodName();
        } catch (Exception ignored) {
        }
        return "Period";
    }

    ObjectIterator<C> load() {
        return StoredObject.list(consumptionClass, condition(), orderBy());
    }

    ObjectIterator<C> load(int limit) {
        return limit > 0 ? load().limit(limit) : load();
    }
}
