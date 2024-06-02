package com.storedobject.accounts;

import com.storedobject.core.*;
import com.storedobject.core.annotation.Column;
import com.storedobject.core.annotation.SetNotAllowed;

import java.math.BigDecimal;

/**
 * Personal entity - Used by {@link EntityAccount}.
 *
 * <p>Note: Contact grouping code ({@link #getContactGroupingCode()}) for this class is 10191.</p>
 *
 * @author Syam
 */
public class PersonalEntity extends AccountEntity<Person> {

    private Id personId;

    public PersonalEntity() {
    }

    public static void columns(Columns columns) {
        columns.add("Person", "id");
    }

    @Override
    protected void setPartyId(Id id) {
        personId = id;
    }

    @Override
    protected Id getPartyId() {
        return personId;
    }

    public void setPerson(BigDecimal idValue) {
        setParty(new Id(idValue));
    }

    public void setPerson(Id personId) {
        setParty(personId);
    }

    public void setPerson(Person person) {
        setParty(person);
    }

    public Person getPerson() {
        return getParty();
    }

    @SetNotAllowed
    @Column(order = 100, caption = "Person")
    public Id getPersonId() {
        return personId;
    }

    @Override
    protected final Class<Person> getPartyClass() {
        return Person.class;
    }

    @Override
    public final String getName() {
        return getPerson().getName();
    }

    @Override
    public int getContactGroupingCode() {
        return 10191;
    }
}
