package com.storedobject.iot;

import com.storedobject.core.*;

public final class ValueLimit extends StoredObject implements Detail {

    public ValueLimit() {
    }

    public static void columns(Columns columns) {
    }

    public void setName(String name) {
    }

    public String getName() {
        return "";
    }

    public void setCaption(String caption) {
    }

    public String getCaption() {
        return "";
    }

    public void setSignificance(int significance) {
    }

    public int getSignificance() {
        return 0;
    }

    public void setUnitOfMeasurement(MeasurementUnit unitOfMeasurement) {
    }

    public void setUnitOfMeasurement(Quantity unitOfMeasurement) {
    }

    public void setUnitOfMeasurement(Object value) {
        setUnitOfMeasurement(Quantity.create(value));
    }

    public Quantity getUnitOfMeasurement() {
        return Count.ONE;
    }

    public void setAlert(boolean alert) {
    }

    public boolean getAlert() {
        return true;
    }

    public void setLowest(double lowest) {
    }

    public double getLowest() {
        return 0;
    }

    public void setLower(double lower) {
    }

    public double getLower() {
        return 0;
    }

    public void setLow(double low) {
    }

    public double getLow() {
        return 0;
    }

    public void setHigh(double high) {
    }

    public double getHigh() {
        return 0;
    }

    public void setHigher(double higher) {
    }

    public double getHigher() {
        return 0;
    }

    public void setHighest(double highest) {
    }

    public double getHighest() {
        return 0;
    }

    public void setActive(boolean active) {
    }

    public boolean getActive() {
        return true;
    }

    @Override
    public boolean isDetailOf(Class<? extends StoredObject> masterClass) {
        return masterClass == UnitDefinition.class;
    }
}
