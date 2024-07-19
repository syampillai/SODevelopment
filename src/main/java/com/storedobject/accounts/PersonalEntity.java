package com.storedobject.accounts;

import com.storedobject.core.*;
import com.storedobject.core.annotation.Column;
import com.storedobject.core.annotation.SetNotAllowed;

import java.math.BigDecimal;

/**
 * Personal entity - Used by {@link EntityAccount}.
 *
 * <p>Note (1): Contact grouping code ({@link #getContactGroupingCode()}) for this class is 10191.</p>
 * <p>Note (2): You typically extend the {@link EntityAccount} to customize party-related attributes.</p>
 *
 * @author Syam
 */
public final class PersonalEntity extends AccountEntity<Person> {

    private Id personId;

    /**
     * Personal entity - Used by {@link EntityAccount}.
     *
     * <p>Note: Contact grouping code ({@link #getContactGroupingCode()}) for this class is 10191.</p>
     */
    public PersonalEntity() {
    }

    /**
     * Adds the name and type of the column to the provided Columns object.
     *
     * @param columns the Columns object to which the name and type of the column will be added
     */
    public static void columns(Columns columns) {
        columns.add("Person", "id");
    }

    /**
     * Adds the specified column list to the indices.
     *
     * @param indices The Indices object to add the column list to.
     */
    public static void indices(Indices indices) {
        indices.add("Person", true);
    }

    /**
     * Sets the party ID of the person.
     *
     * @param id The party ID to be set.
     */
    @Override
    protected void setPartyId(Id id) {
        personId = id;
    }

    /**
     * Retrieves the Party ID.
     *
     * @return The Party ID associated with this method.
     */
    @Override
    protected Id getPartyId() {
        return personId;
    }

    /**
     * Sets the person identified by the specified ID value.
     *
     * @param idValue the ID value of the person
     */
    public void setPerson(BigDecimal idValue) {
        setParty(new Id(idValue));
    }

    /**
     * Set the person with the given personId.
     *
     * @param personId The ID of the person to be set.
     */
    public void setPerson(Id personId) {
        setParty(personId);
    }

    /**
     * Sets the person associated with this PersonalEntity.
     *
     * @param person The person object to be set. Cannot be null.
     */
    public void setPerson(Person person) {
        setParty(person);
    }

    /**
     * Retrieves the person associated with this PersonalEntity.
     *
     * @return The person associated with this PersonalEntity.
     */
    public Person getPerson() {
        return getParty();
    }

    /**
     * Retrieves the unique identifier of a person.
     *
     * @return The unique identifier of the person.
     */
    @SetNotAllowed
    @Column(order = 100, caption = "Person")
    public Id getPersonId() {
        return personId;
    }

    /**
     * Returns the class of the party associated with this PersonalEntity.
     * The party class is Person.
     *
     * @return the class of the party (Person)
     */
    @Override
    protected Class<Person> getPartyClass() {
        return Person.class;
    }

    /**
     * Returns the name of the person associated with the account.
     *
     * @return The name of the person as a string.
     */
    @Override
    public String getName() {
        return getPerson().getName();
    }

    /**
     * Retrieves the contact grouping code for this entity.
     *
     * @return The contact grouping code. This value is always 10191.
     */
    @Override
    public int getContactGroupingCode() {
        return 10191;
    }

    public static PersonalEntity getFor(Id personId) {
        return get(PersonalEntity.class, "Person=" + personId);
    }
}
