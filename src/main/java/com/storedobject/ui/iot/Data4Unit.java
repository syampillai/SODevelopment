package com.storedobject.ui.iot;

import com.storedobject.core.StoredObject;
import com.storedobject.core.TimestampPeriod;
import com.storedobject.iot.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public record Data4Unit(Class<? extends Data> dataClass, Unit unit, TimestampPeriod period, long timeSlice,
                        Collection<String> attributes) {

    public static void process(Unit unit, @SuppressWarnings("rawtypes") Collection<ValueDefinition> valueDefinitions,
                               TimestampPeriod period, long timeSlice, Consumer<Data4Unit> processor) {
        Site site = unit.getSite();
        final TimestampPeriod p = new TimestampPeriod(site.dateGMT(period.getFrom()), site.dateGMT(period.getTo()));
        StoredObject.list(UnitDefinition.class, "UnitType=" + unit.getType().getId()).forEach(ud -> {
            List<String> attributes = new ArrayList<>();
            ud.listLinks(ValueLimit.class).filter(valueDefinitions::contains).forEach(v -> attributes.add(v.getName()
                    + " AS " + v.getCaption()));
            ud.listLinks(AlarmSwitch.class).filter(valueDefinitions::contains).forEach(v -> attributes.add(v.getName()
                    + " AS " + v.getCaption()));
            if(!attributes.isEmpty()) {
                attributes.addFirst("CollectedAt AS Timestamp");
                processor.accept(new Data4Unit(ud.getDataClass(), unit, p, timeSlice, attributes));
            }
        });
    }

    String condition() {
        return "Unit=" + unit.getId() + " AND CollectedAt BETWEEN " + period.getFrom().getTime() + " AND "
                + period.getTo().getTime();
    }

    SliceFilter sliceFilter() {
        return new SliceFilter(timeSlice);
    }

    private static class SliceFilter implements Predicate<Data> {

        private final long slice;
        private long last = 0;

        SliceFilter(long slice) {
            this.slice = slice;
        }

        @Override
        public boolean test(Data data) {
            if(slice <= 0) {
                return true;
            }
            if(last == 0) {
                last = data.getCollectedAt() / slice;
                last *= slice;
                return true;
            }
            long current = data.getCollectedAt();
            if(Math.abs(current - last) < slice) {
                return false;
            }
            if(current < last) {
                last -= slice;
            } else {
                last += slice;
            }
            return true;
        }
    }
}
