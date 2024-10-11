package com.storedobject.iot;

import com.storedobject.core.*;
import com.storedobject.core.annotation.Column;
import com.storedobject.core.annotation.SetNotAllowed;

import java.math.BigDecimal;

public abstract class Statistics extends StoredObject implements DBTransaction.NoHistory {

    private String name;
    private Id unitId;
    private int year;
    private int count = 0;
    double min = Double.MAX_VALUE, max = Double.MIN_VALUE, mean, sD;

    public Statistics() {
    }

    public static void columns(Columns columns) {
        columns.add("Name", "text");
        columns.add("Unit", "id");
        columns.add("Year", "int");
        columns.add("Count", "int");
        columns.add("Min", "double precision");
        columns.add("Max", "double precision");
        columns.add("Mean", "double precision");
        columns.add("SD", "double precision");
    }

    public void setUnit(Id unitId) {
        if (!loading() && !Id.equals(this.getUnitId(), unitId)) {
            throw new Set_Not_Allowed("Unit");
        }
        this.unitId = unitId;
    }

    public void setUnit(BigDecimal idValue) {
        setUnit(new Id(idValue));
    }

    public void setUnit(Unit unit) {
        setUnit(unit == null ? null : unit.getId());
    }

    @SetNotAllowed
    @Column(style = "(any)", order = 50)
    public Id getUnitId() {
        return unitId;
    }

    public Unit getUnit() {
        return getRelated(Unit.class, unitId, true);
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(order = 100)
    public String getName() {
        return name;
    }

    public void setYear(int year) {
        this.year = year;
    }

    @Column(order = 200)
    public int getYear() {
        return year;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Column(order = 3000)
    public int getCount() {
        return count;
    }

    public void setMin(double min) {
        this.min = min;
    }

    @Column(order = 3100)
    public double getMin() {
        return min;
    }

    public void setMax(double max) {
        this.max = max;
    }

    @Column(order = 3200)
    public double getMax() {
        return max;
    }

    public void setMean(double mean) {
        this.mean = mean;
    }

    @Column(order = 3300)
    public double getMean() {
        return mean;
    }

    public void setSD(double sd) {
        this.sD = sd;
    }

    @Column(order = 3400, caption = "SD")
    public double getSD() {
        return sD;
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        if(StringUtility.isWhite(name)) {
            throw new Invalid_Value("Name");
        }
        unitId = tm.checkTypeAny(this, unitId, AbstractUnit.class, false);
        super.validateData(tm);
    }

    public String getPeriodName() {
        if(this instanceof DailyStatistics) {
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
        return StringUtility.makeLabel(name) + " (" + getUnit().toDisplay() + ") Count = " + count
                + ", Min = " + min + ", Max = " + max + ", Mean = " + mean + ", SD = " + sD
                + " (Year: " + year
                + (this instanceof YearlyStatistics ? "" : (", " + getPeriodName() + ": " + getPeriod())) + ")";
    }

    private double ss() {
        return switch (count) {
            case 0 -> 0;
            case 1 -> mean;
            default -> sD * sD * count;
        };
    }

    public void add(double value) {
        if(count == 0) {
            min = value;
            max = value;
            mean = value;
            sD = 0;
            ++count;
            return;
        }
        if(value < min) {
            min = value;
        }
        if(value > max) {
            max = value;
        }
        double ss = ss() + ((value - mean) * (value - mean));
        mean = ((mean * count) + value) / (count + 1);
        ++count;
        sD = Math.sqrt((ss / count));
    }

    public void add(Statistics another) {
        if(another == null || another.count == 0) {
            return;
        }
        if(count == 0) {
            count = another.count;
            min = another.min;
            max = another.max;
            mean = another.mean;
            sD = another.sD;
            name = another.name;
            unitId = another.unitId;
            return;
        }
        if(another.count == 1) {
            add(another.min);
            name = another.name;
            unitId = another.unitId;
            return;
        }
        if(another.min < min) {
            min = another.min;
        }
        if(another.max > max) {
            max = another.max;
        }
        double ss = ss() + another.ss();
        ss  += ((another.sD * another.sD) + (another.mean * another.mean)) * another.count;
        mean = ((mean * count) + (another.mean * another.count));
        count += another.count;
        mean /= count;
        sD = Math.sqrt((ss / count));
    }
}