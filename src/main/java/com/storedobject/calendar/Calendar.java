package com.storedobject.calendar;

import com.storedobject.core.Columns;
import com.storedobject.core.Id;
import com.storedobject.core.Person;
import com.storedobject.core.StoredObject;

import java.math.BigDecimal;

public class Calendar extends StoredObject {

    public Calendar() {
    }

    public static void columns(Columns columns) {
    }

    public void setOwner(Id ownerId) {
    }

    public void setOwner(BigDecimal idValue) {
    }

    public void setOwner(Person owner) {
    }

    public Id getOwnerId() {
        return new Id();
    }

    public Person getOwner() {
        return new Person();
    }

    public void setName(String name) {
    }

    public String getName() {
        return "";
    }

    public static String[] getStartOfWeekValues() {
        return new String[0];
    }

    public void setStartOfWeek(int startOfWeek) {
    }

    public int getStartOfWeek() {
        return 1;
    }

    public String getStartOfWeekValue() {
        return "";
    }
}
