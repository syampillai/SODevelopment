package com.storedobject.iot;

import com.storedobject.core.*;
import com.storedobject.core.annotation.*;

import java.util.HashMap;
import java.util.Map;

public final class ValueLimit extends ValueDefinition<Double> {

    private Quantity unitOfMeasurement = Count.ZERO;
    private double lowest;
    private double lower;
    private double low;
    private double high;
    private double higher;
    private double highest;
    private double minimum, maximum;
    private int decimals = 2;
    private boolean unlimited;
    private Map<Integer, String> alertMessages = null;

    public ValueLimit() {
    }

    public static void columns(Columns columns) {
        columns.add("UnitOfMeasurement", "quantity");
        columns.add("Decimals", "int");
        columns.add("Lowest", "double precision");
        columns.add("Lower", "double precision");
        columns.add("Low", "double precision");
        columns.add("High", "double precision");
        columns.add("Higher", "double precision");
        columns.add("Highest", "double precision");
        columns.add("Minimum", "double precision");
        columns.add("Maximum", "double precision");
        columns.add("Unlimited", "boolean");
    }

    public static ValueLimit get(String name) {
        return StoredObjectUtility.get(ValueLimit.class, "Name", name, true);
    }

    public static ObjectIterator<ValueLimit> list(String name) {
        return StoredObjectUtility.list(ValueLimit.class, "Name", name, true);
    }

    public void setUnitOfMeasurement(MeasurementUnit unitOfMeasurement) {
        setUnitOfMeasurement(Quantity.create(0, unitOfMeasurement));
    }

    public void setUnitOfMeasurement(Quantity unitOfMeasurement) {
        this.unitOfMeasurement = unitOfMeasurement.zero();
    }

    public void setUnitOfMeasurement(Object value) {
        setUnitOfMeasurement(Quantity.create(value));
    }

    @Column(required = false, order = 400)
    public Quantity getUnitOfMeasurement() {
        return unitOfMeasurement.zero();
    }

    public String getUnitSuffix() {
        return unitOfMeasurement instanceof FractionalCount ? "" : (" " + unitOfMeasurement.getUnit().getUnit());
    }

    public void setDecimals(int decimals) {
        this.decimals = decimals;
    }

    @Column(required = false, order = 450)
    public int getDecimals() {
        return decimals;
    }

    public void setLowest(double lowest) {
        this.lowest = lowest;
    }

    @Column(required = false, order = 500, style = "(-)")
    public double getLowest() {
        return lowest;
    }

    public void setLower(double lower) {
        this.lower = lower;
    }

    @Column(required = false, order = 600, style = "(-)")
    public double getLower() {
        return lower;
    }

    public void setLow(double low) {
        this.low = low;
    }

    @Column(required = false, order = 700, style = "(-)")
    public double getLow() {
        return low;
    }

    public void setHigh(double high) {
        this.high = high;
    }

    @Column(required = false, order = 800, style = "(-)")
    public double getHigh() {
        return high;
    }

    public void setHigher(double higher) {
        this.higher = higher;
    }

    @Column(required = false, order = 900, style = "(-)")
    public double getHigher() {
        return higher;
    }

    public void setHighest(double highest) {
        this.highest = highest;
    }

    @Column(required = false, order = 1000, style = "(-)")
    public double getHighest() {
        return highest;
    }

    public void setMinimum(double minimum) {
        this.minimum = minimum;
    }

    @Column(order = 1200, caption = "Min. Possible Value", required = false, style = "(-)")
    public double getMinimum() {
        return minimum;
    }

    public void setMaximum(double maximum) {
        this.maximum = maximum;
    }

    @Column(order = 1300, caption = "Max. Possible Value", required = false, style = "(-)")
    public double getMaximum() {
        return maximum;
    }

    public void setUnlimited(boolean unlimited) {
        this.unlimited = unlimited;
    }

    @Column(order = 1400)
    public boolean getUnlimited() {
        return unlimited;
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        if(!unlimited) {
            if (lowest > lower || lower > low || low > high || high > higher || higher > highest) {
                throw new Invalid_State("Check limit values");
            }
            minMax();
        }
        if(decimals > 0 && !getUnitOfMeasurement().getUnit().hasDecimals()) {
            decimals = 0;
        }
        super.validateData(tm);
    }

    private void minMax() {
        if(minimum > lowest) {
            minimum = lowest;
        }
        if(maximum < highest) {
            maximum = highest;
        }
    }

    @Override
    public void loaded() {
        super.loaded();
        minMax();
    }

    @Override
    protected Double convertValue(Object value) {
        if(value instanceof Double d) {
            return d;
        }
        if(value instanceof Number n) {
            return n.doubleValue();
        }
        if(value instanceof HasValue hv) {
            return hv.getValue();
        }
        return null;
    }

    @Override
    public String getAlertMessage(int alarmLevel) {
        if(alarmLevel == 0) {
            return "";
        }
        if(alarmLevel < -3) {
            alarmLevel = -3;
        }
        if(alarmLevel > 3) {
            alarmLevel = 3;
        }
        if(alarmLevel < 0) {
            alarmLevel += 3;
        } else {
            alarmLevel += 2;
        }
        if(alertMessages == null) {
            loadLimitMessages();
        }
        if(alertMessages.isEmpty()) {
            return alertMessage;
        }
        String m = alertMessages.get(alarmLevel);
        if (m != null) {
            return m;
        }
        return switch (alarmLevel) {
            case 0 -> am(0, 1, 2);
            case 1 -> am(1, 2, 0);
            case 2 -> am(2, 1, 0);
            case 3 -> am(3, 4, 5);
            case 4 -> am(4, 5, 3);
            case 5 -> am(5, 3, 4);
            default -> alertMessage;
        };
    }

    private String am(int... levels) {
        for(int level: levels) {
            String m = alertMessages.get(level);
            if(m != null) {
                return m;
            }
        }
        return alertMessage;
    }

    private void loadLimitMessages() {
        alertMessages = new HashMap<>();
        list(LimitMessage.class, "ValueName=" + getId(), "Condition")
                .forEach(m -> alertMessages.put(m.getCondition(), m.getMessage()));
    }
}
