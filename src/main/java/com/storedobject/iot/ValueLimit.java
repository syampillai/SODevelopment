package com.storedobject.iot;

import com.storedobject.core.*;
import com.storedobject.core.annotation.*;

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

    @Column(required = false, order = 500)
    public double getLowest() {
        return lowest;
    }

    public void setLower(double lower) {
        this.lower = lower;
    }

    @Column(required = false, order = 600)
    public double getLower() {
        return lower;
    }

    public void setLow(double low) {
        this.low = low;
    }

    @Column(required = false, order = 700)
    public double getLow() {
        return low;
    }

    public void setHigh(double high) {
        this.high = high;
    }

    @Column(required = false, order = 800)
    public double getHigh() {
        return high;
    }

    public void setHigher(double higher) {
        this.higher = higher;
    }

    @Column(required = false, order = 900)
    public double getHigher() {
        return higher;
    }

    public void setHighest(double highest) {
        this.highest = highest;
    }

    @Column(required = false, order = 1000)
    public double getHighest() {
        return highest;
    }

    public void setMinimum(double minimum) {
        this.minimum = minimum;
    }

    @Column(order = 1200, caption = "Min. Possible Value", required = false)
    public double getMinimum() {
        return minimum;
    }

    public void setMaximum(double maximum) {
        this.maximum = maximum;
    }

    @Column(order = 1300, caption = "Max. Possible Value", required = false)
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
}
