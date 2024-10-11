package com.storedobject.iot;

import com.storedobject.core.*;
import com.storedobject.core.annotation.*;
import java.math.BigDecimal;

public abstract class Consumption extends StoredObject implements DBTransaction.NoHistory {

    private Id resourceId, itemId;
    private double consumption = 0;
    private int year;

    public Consumption() {
    }

    public static void columns(Columns columns) {
        columns.add("Item", "id");
        columns.add("Resource", "id");
        columns.add("Year", "int");
        columns.add("Consumption", "double precision");
    }

    public void setItem(Id itemId) {
        if (!loading() && !Id.equals(this.getItemId(), itemId)) {
            throw new Set_Not_Allowed("Item");
        }
        this.itemId = itemId;
    }

    public void setItem(BigDecimal idValue) {
        setItem(new Id(idValue));
    }

    public void setItem(AbstractUnit item) {
        setItem(item == null ? null : item.getId());
    }

    @SetNotAllowed
    @Column(style = "(any)", order = 50)
    public Id getItemId() {
        return itemId;
    }

    public AbstractUnit getItem() {
        return getRelated(AbstractUnit.class, itemId, true);
    }

    public void setResource(Id resourceId) {
        if (!loading() && !Id.equals(this.getResourceId(), resourceId)) {
            throw new Set_Not_Allowed("Resource");
        }
        this.resourceId = resourceId;
    }

    public void setResource(BigDecimal idValue) {
        setResource(new Id(idValue));
    }

    public void setResource(Resource resource) {
        setResource(resource == null ? null : resource.getId());
    }

    @SetNotAllowed
    @Column(order = 100)
    public Id getResourceId() {
        return resourceId;
    }

    public Resource getResource() {
        return getRelated(com.storedobject.iot.Resource.class, resourceId);
    }

    public void setYear(int year) {
        this.year = year;
    }

    @Column(order = 200)
    public int getYear() {
        return year;
    }

    public void setConsumption(double consumption) {
        this.consumption = consumption;
    }

    @Column(required = false, order = 2000)
    public double getConsumption() {
        return consumption;
    }

    void addConsumption(double consumption) {
        this.consumption += consumption;
        if (this.consumption < 0.000000001) {
            this.consumption = 0;
        }
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        itemId = tm.checkTypeAny(this, itemId, AbstractUnit.class, false);
        resourceId = tm.checkType(this, resourceId, Resource.class, false);
        super.validateData(tm);
    }

    public String getPeriodName() {
        if(this instanceof DailyConsumption) {
            return "Day";
        }
        String s = getClass().getName();
        s = s.substring(s.lastIndexOf('.') + 1);
        return s.substring(0, s.indexOf("ly"));
    }

    public abstract int getPeriod();

    public abstract String getPeriodDetail();

    @Override
    public String toDisplay() {
        return getResource().toDisplay() + " (" + getItem().toDisplay() + ") = " + consumption + " (Year: " + year
                + (this instanceof YearlyConsumption ? "" : (", " + getPeriodName() + ": " + getPeriod())) + ")";
    }
}