package com.storedobject.ui.iot;

import com.storedobject.core.StoredObject;
import com.storedobject.core.TimestampPeriod;
import com.storedobject.iot.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public record Data4Unit(Class<? extends Data> dataClass, Unit unit, TimestampPeriod period, Collection<String> attributes) {

    public static void process(Unit unit, @SuppressWarnings("rawtypes") Collection<ValueDefinition> valueDefinitions,
                               TimestampPeriod period, Consumer<Data4Unit> processor) {
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
                processor.accept(new Data4Unit(ud.getDataClass(), unit, p, attributes));
            }
        });
    }

    String condition() {
        return "Unit=" + unit.getId() + " AND CollectedAt BETWEEN " + period.getFrom().getTime() + " AND "
                + period.getTo().getTime();
    }

    String orderBy() {
        return "CollectedAt DESC";
    }
}
