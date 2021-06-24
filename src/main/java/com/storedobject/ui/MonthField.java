package com.storedobject.ui;

import com.storedobject.core.DatePeriod;
import com.storedobject.core.DateUtility;
import com.storedobject.common.StringList;
import com.storedobject.vaadin.CustomField;
import com.storedobject.vaadin.ListField;
import com.vaadin.flow.component.html.Div;

import java.sql.Date;
import java.util.ArrayList;

public class MonthField extends CustomField<Date> {

    private static final StringList months = StringList.create(DateUtility.getMonthNames());
    protected ListField<String> monthPart;
    protected ListField<Integer> yearPart;

    public MonthField() {
        this(null);
    }

    public MonthField(String label) {
        super(DateUtility.startOfMonth());
        monthPart = new ListField<>(months);
        yearPart = new ListField<>(years(0, 10));
        monthPart.setWidth("5em");
        yearPart.setWidth("5em");
        monthPart.getElement().getStyle().set("vertical-align", "top");
        yearPart.getElement().getStyle().set("vertical-align", "top");
        Div div = new Div();
        add(div);
        addField(div, monthPart, yearPart);
        setPresentationValue(getEmptyValue());
        setLabel(label);
    }

    @Override
    public void setValue(Date value) {
        super.setValue(DateUtility.startOfMonth(value));
    }

    public void setYearRange(int yearOffset) {
        setYearRange(0, yearOffset);
    }

    public int getMonth() {
        return DateUtility.getMonth(getValue());
    }

    public int getYear() {
        return DateUtility.getYear(getValue());
    }

    public DatePeriod getPeriod() {
        Date v = getValue();
        return new DatePeriod(v, DateUtility.endOfMonth(v));
    }

    public String getDBCondition() {
        return getPeriod().getDBCondition();
    }

    @Override
    protected Date generateModelValue() {
        return DateUtility.create(this.yearPart.getValue(), DateUtility.getMonth(this.monthPart.getValue()), 1);
    }

    @Override
    protected void setPresentationValue(Date value) {
        this.monthPart.setValue(DateUtility.getMonthName(value));
        this.yearPart.setValue(DateUtility.getYear(value));
    }

    public void setYearRange(int yearFrom, int yearTo) {
        this.yearPart.setItems(years(yearFrom, yearTo));
    }

    private static ArrayList<Integer> years(int yearFrom, int yearTo) {
        if (yearFrom <= 100) {
            yearFrom += DateUtility.getYear();
            if (yearTo < 100) {
                yearTo += DateUtility.getYear();
            }
        }
        if (yearTo == 0) {
            yearTo = yearFrom + 10;
        }
        if (yearTo < 100) {
            yearTo += yearFrom;
        }
        ArrayList<Integer> years = new ArrayList<>();
        int n = Math.abs(yearTo - yearFrom);
        if (n > 100) {
            yearFrom = yearTo = DateUtility.getYear();
        }
        if (yearFrom <= yearTo) {
            while(yearFrom <= yearTo) {
                years.add(yearFrom);
                ++yearFrom;
            }
        } else {
            while(yearFrom >= yearTo) {
                years.add(yearFrom);
                --yearFrom;
            }
        }
        return years;
    }
}
