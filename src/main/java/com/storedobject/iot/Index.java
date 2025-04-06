package com.storedobject.iot;

import com.storedobject.common.TriFunction;
import com.storedobject.core.ClassAttribute;
import com.storedobject.core.DateUtility;
import com.storedobject.core.Id;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public abstract class Index implements HasValue {

    protected final Unit unit;
    protected final int year;
    protected final int hour;

    protected Index(Unit unit, int year, int hour) {
        this.unit = unit;
        this.year = year;
        this.hour = hour;
    }

    public static <I extends Index> I create(Class<I> indexClass, Data data, TriFunction<Unit, Integer, Integer, I> creator) {
        Cache<I> cache = Cache.create(indexClass);
        Unit unit;
        synchronized (cache.units) {
            unit = cache.units.get(data.getUnitId());
            if (unit == null) {
                unit = data.getUnit();
                cache.units.put(unit.getId(), unit);
            }
        }
        Date siteDate = unit.getSite().date(new Date(data.getCollectedAt() - 1000 * 60 * 60));
        int hour = DateUtility.getHourOfYear(siteDate);
        int year = DateUtility.getYear(siteDate);
        String key = hour + "-" + year + "-" + unit.getId();
        I index;
        synchronized (cache) {
            index = cache.cache.get(key);
            if (index == null) {
                index = creator.accept(unit, year, hour);
                cache.cache.put(key, index);
            }
            index.compute();
        }
        return index;
    }

    protected abstract void compute();

    @Override
    public final boolean equals(Object obj) {
        if(obj != null && obj.getClass() == getClass()) {
            Index index = (Index) obj;
            return index.unit.getId().equals(unit.getId()) && index.year == year && index.hour == hour;
        }
        return false;
    }

    @Override
    public final int hashCode() {
        return (unit.getId().hashCode() + ClassAttribute.get(unit).getFamily()) * 100_000_000 + (year * 10_000 + hour);
    }

    private static class Cache<I extends Index> {

        private static final Map<Class<? extends Index>, Cache<?>> caches = new HashMap<>();
        final Map<String, I> cache = new HashMap<>();
        final Map<Id, Unit> units = new HashMap<>();

        private Cache(Class<I> indexClass) {
            caches.put(indexClass, this);
        }

        synchronized static <O extends Index> Cache<O> create(Class<O> indexClass) {
            @SuppressWarnings("unchecked") Cache<O> cache = (Cache<O>) caches.get(indexClass);
            if(cache == null) {
                cache = new Cache<>(indexClass);
                caches.put(indexClass, cache);
            }
            return cache;
        }
    }
}
