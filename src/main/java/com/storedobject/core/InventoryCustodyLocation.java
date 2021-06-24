package com.storedobject.core;

import java.math.BigDecimal;
import java.util.Objects;

public final class InventoryCustodyLocation extends InventoryLocation {

    public InventoryCustodyLocation() {
    }

    public static void columns(Columns columns) {
        columns.add("Person", "id");
    }

    public void setPerson(Id personId) {
    }

    public void setPerson(BigDecimal idValue) {
        setPerson(new Id(idValue));
    }

    public void setPerson(Person person) {
        setPerson(person == null ? null : person.getId());
    }

    public Id getPersonId() {
        return new Id();
    }

    public Person getPerson() {
        return new Person();
    }

    @Override
    public Id getEntityId() {
        return Id.ZERO;
    }

    @Override
    public int getType() {
        return 18;
    }


    public static InventoryCustodyLocation getForPerson(Id personId) {
        return Id.isNull(personId) ? null : get(InventoryCustodyLocation.class, "Person=" + personId);
    }

    public static InventoryCustodyLocation getForPerson(Person person) {
        return person == null ? null : getForPerson(person.getId());
    }

    public static InventoryCustodyLocation get(String personName) {
        return getForPerson(Person.get(personName));
    }

    public static ObjectIterator<InventoryCustodyLocation> list(String personName) {
        return Person.list(personName).convert(InventoryCustodyLocation::getForPerson).filter(Objects::nonNull);
    }
}
