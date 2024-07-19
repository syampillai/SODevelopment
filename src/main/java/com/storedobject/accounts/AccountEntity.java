package com.storedobject.accounts;

import com.storedobject.common.Email;
import com.storedobject.core.*;
import com.storedobject.core.annotation.*;
import com.storedobject.common.Address;
import com.storedobject.common.PhoneNumber;

import java.util.Objects;

/**
 * Entity class used by the accounting system. The underlying party could be an {@link Entity} or {@link Person}.
 *
 * @param <T> Underlying party type.
 * @author Syam
 */
public abstract class AccountEntity<T extends StoredObject> extends StoredObject
        implements HasContacts, HasName, HasShortName {

    private T party;
    private String shortName;
    private String primaryAddress;
    private String primaryEmail;
    private String primaryPhone;
    private String taxCode;

    /**
     * Represents an account entity.
     *
     * <p>
     * The AccountEntity class is a base class for PersonalEntity and BusinessEntity classes used in the EntityAccount class.
     * It provides common functionality and fields related to an account entity.
     * </p>
     *
     * <p>
     * The AccountEntity class is an abstract class and cannot be instantiated directly.
     * </p>
     *
     * <p>
     * This class extends the StoredObject class and implements the HasContacts, HasName, and HasShortName interfaces.
     * </p>
     */
    public AccountEntity() {
    }

    /**
     * Adds columns to the provided Columns interface for an entity.
     * The columns represent the following fields in the entity:
     * - ShortName (text):
     *   The short name of the entity.
     * - PrimaryAddress (address):
     *   The primary address of the entity.
     * - PrimaryEmail (email):
     *   The primary email address of the entity.
     * - PrimaryPhone (phone):
     *   The primary phone number of the entity.
     * - TaxCode (text):
     *   The tax code of the entity.
     *
     * @param columns The Columns interface to add the columns to.
     */
    public static void columns(Columns columns) {
        columns.add("ShortName", "text");
        columns.add("PrimaryAddress", "address");
        columns.add("PrimaryEmail", "email");
        columns.add("PrimaryPhone", "phone");
        columns.add("TaxCode", "text");
    }

    /**
     * Adds columns to the provided Indices interface for an entity.
     * The columns represent the following fields in the entity:
     * - lower(ShortName) (text):
     *   Adds an index for the ShortName field in the entity, ignoring case.
     *
     * @param indices The Indices interface to add the columns to.
     */
    public static void indices(Indices indices) {
        indices.add("lower(ShortName)", "ShortName<>''", true);
    }

    /**
     * Returns the columns to be displayed in the browser for the AccountEntity class.
     *
     * @return An array of strings representing the columns.
     */
    public static String[] browseColumns() {
        return new String[] {
                "ShortName as Code",
                "Name",
                "PrimaryAddress",
                "PrimaryEmail",
                "PrimaryPhone",
        };
    }

    /**
     * Sets the party associated with this object.
     *
     * @param partyId the ID of the party to be set
     * @throws Set_Not_Allowed if the method is called when not loading and the provided party ID is different from the current party ID
     */
    public final void setParty(Id partyId) {
        if (!loading() && !Id.equals(this.getPartyId(), partyId)) {
            throw new Set_Not_Allowed("Party");
        }
        this.party = null;
        setPartyId(partyId);
    }

    /**
     * Sets the party ID for the given object.
     *
     * @param id The party ID to be set.
     */
    protected abstract void setPartyId(Id id);

    /**
     * Sets the party for this object.
     *
     * @param party The party to set. (null is allowed)
     */
    public void setParty(T party) {
        setParty(party == null ? null : party.getId());
    }

    /**
     * Retrieves the ID of the party associated with this object.
     *
     * @return The ID of the party
     */
    protected abstract Id getPartyId();

    /**
     * Retrieves the party associated with this account entity.
     * If the party is not already initialized, it retrieves the related instance from the database using the party class and party id.
     * If the instance is old, it might return an old instance.
     *
     * @return The party associated with this account entity, or null if it is not available for the given parameters.
     */
    public final T getParty() {
        if (this.party == null) {
            this.party = getRelated(getPartyClass(), getPartyId());
        }
        return this.party;
    }

    /**
     * Sets the short name of the Entity.
     *
     * @param shortName The new short name.
     */
    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    /**
     * Retrieves the short name of the Entity.
     *
     * @return The short name.
     */
    @Column(style = "(code)", required = false, order = 200)
    public String getShortName() {
        return shortName;
    }

    /**
     * Sets the primary address of the AccountEntity.
     *
     * @param primaryAddress The new primary address.
     */
    public void setPrimaryAddress(String primaryAddress) {
        this.primaryAddress = primaryAddress;
    }

    /**
     * Retrieves the primary address of the AccountEntity.
     *
     * @return The primary address of the AccountEntity.
     */
    @Column(style = "(address)", required = false, order = 300)
    public String getPrimaryAddress() {
        return primaryAddress;
    }

    /**
     * Returns the primary address value of the AccountEntity.
     *
     * @return The primary address value as an instance of the Address class.
     */
    public Address getPrimaryAddressValue() {
        return Address.create(primaryAddress);
    }

    /**
     * Sets the primary email address for the account entity.
     *
     * @param primaryEmail The new primary email address.
     */
    public void setPrimaryEmail(String primaryEmail) {
        this.primaryEmail = primaryEmail;
    }

    /**
     * Retrieves the primary email address of the account entity.
     *
     * @return The primary email address as a string.
     */
    @Column(style = "(email)", required = false, order = 400)
    public String getPrimaryEmail() {
        return primaryEmail;
    }

    /**
     * Sets the primary phone number for the account entity.
     *
     * @param primaryPhone The primary phone number to set.
     */
    public void setPrimaryPhone(String primaryPhone) {
        this.primaryPhone = primaryPhone;
    }

    /**
     * Retrieves the primary phone number of the account entity.
     *
     * @return The primary phone number as a string.
     */
    @Column(style = "(phone)", required = false, order = 500)
    public String getPrimaryPhone() {
        return primaryPhone;
    }

    /**
     * Sets the tax code for the entity.
     *
     * @param taxCode The tax code to be set for the entity.
     */
    public void setTaxCode(String taxCode) {
        this.taxCode = taxCode;
    }

    /**
     * Returns the tax code of the AccountEntity.
     *
     * @return The tax code.
     */
    @Column(style = "(code)", required = false, order = 600)
    public String getTaxCode() {
        return taxCode;
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        setPartyId(tm.checkType(this, getPartyId(), getPartyClass(), false));
        shortName = toCode(shortName);
        if (primaryAddress == null) {
            primaryAddress = "";
        } else {
            primaryAddress = primaryAddress.trim();
        }
        if (!primaryAddress.isEmpty()) {
            primaryAddress = Address.check(primaryAddress);
        }
        if (primaryPhone == null) {
            primaryPhone = "";
        } else {
            primaryPhone = primaryPhone.trim();
        }
        if (!primaryPhone.isEmpty()) {
            primaryPhone = PhoneNumber.check(primaryPhone);
        }
        primaryEmail = StringUtility.pack(primaryEmail);
        if (!primaryEmail.isEmpty()) {
            primaryEmail = Email.check(primaryEmail);
        }
        taxCode = toCode(taxCode);
        super.validateData(tm);
    }

    /**
     * Retrieves the party class associated with this object.
     *
     * @return The party class.
     */
    protected abstract Class<T> getPartyClass();

    /**
     * Retrieves the name of the entity associated with this account.
     *
     * @return The name of the entity as a string.
     */
    public abstract String getName();

    /**
     * Determines whether the account entity is associated with a personal party.
     *
     * @return true if the account entity is associated with a personal party, false otherwise
     */
    public final boolean isPersonal() {
        return getPartyClass() == Person.class;
    }

    /**
     * Determines whether the account entity is a business entity.
     *
     * @return {@code true} if the account entity is a business entity, {@code false} otherwise.
     */
    public final boolean isBusiness() {
        return getPartyClass() == Entity.class;
    }

    @Override
    public String toString() {
        String s = shortName + " " + getName();
        if(!primaryAddress.isEmpty()) {
            s += ", " + getPrimaryAddressValue().toString().replace("\n", ", ");
        }
        return s;
    }

    @Override
    public Address getAddress() {
        Address a = HasContacts.super.getAddress();
        return a == null ?getPrimaryAddressValue() : a;
    }

    @Override
    public String getEmail() {
        String s = HasContacts.super.getEmail();
        return s != null && !s.isEmpty() ? s : (primaryEmail != null && !primaryEmail.isEmpty() ? primaryEmail : null);
    }

    @Override
    public long getPhone() {
        long s = HasContacts.super.getPhone();
        return s == 0 ? s : HasContacts.phoneToNumber(primaryPhone);
    }

    public static AccountEntity<?> get(String name) {
        ObjectIterator<AccountEntity<?>> oi = listInt(name);
        return oi == null ? null : oi.single(false);
    }

    public static ObjectIterator<AccountEntity<?>> list(String name) {
        ObjectIterator<AccountEntity<?>> oi = listInt(name);
        return oi == null ? ObjectIterator.create() : oi;
    }

    private static ObjectIterator<AccountEntity<?>> listInt(String name) {
        String sn = toCode(name);
        if(sn.isEmpty()) {
            return null;
        }
        ObjectIterator<AccountEntity<?>> oi =
                list(AccountEntity.class, "lower(ShortName) LIKE '" + sn.toLowerCase() + "%'", true)
                        .map(e -> (AccountEntity<?>) e);
        oi = oi.add(Entity.list(name).map(e -> BusinessEntity.getFor(e.getId())).filter(Objects::nonNull).map(ae -> ae));
        oi = oi.add(Person.list(name).map(p -> PersonalEntity.getFor(p.getId())).filter(Objects::nonNull).map(ae -> ae));
        return oi;
    }
}
