package com.storedobject.account;

import com.storedobject.core.Account;
import com.storedobject.core.Id;
import com.storedobject.core.Person;
import com.storedobject.core.annotation.SetNotAllowed;

/**
 * Represents a personal account.
 *
 * @author Syam
 */
public class PersonalAccount extends Account {

    private final Id personId;

    /**
     * Constructs a Personal Account.
     *
     * @param personId Id of the person owning this account.
     * @param systemEntityId Id of the System Entity where this account is opened.
     * @param currencyId Id of the account currency.
     */
    public PersonalAccount(Id personId, Id systemEntityId, Id currencyId) {
        super(systemEntityId, null, currencyId);
        this.personId = personId;
    }

    /**
     * Constructs a Personal Account.
     *
     * @param personId of the person owning this account.
     * @param currencyId Id of the account currency.
     */
    public PersonalAccount(Id personId, Id currencyId) {
        this(personId, null, currencyId);
    }

    /**
     * Constructs a Personal Account.
     *
     * @param personId of the person owning this account.
     */
    public PersonalAccount(Id personId) {
        this(personId, null, null);
    }

    /**
     * Constructs a local currency Account.
     */
    public PersonalAccount() {
        this(null, null, null);
    }

    /**
     * Gets name of the Person as account's title.
     *
     * @return The title
     */
    @Override
    public String getTitle() {
        return getPerson().getName();
    }

    /**
     * Gets the Id of the person.
     *
     * @return The Id of the person
     */
    @SetNotAllowed
    public Id getPersonId() {
        return personId;
    }

    /**
     * Gets the person.
     *
     * @return The person.
     */
    public Person getPerson() {
        return get(getTransaction(), Person.class, personId);
    }
}