package com.storedobject.accounts;

import com.storedobject.common.Email;
import com.storedobject.core.*;
import com.storedobject.core.annotation.*;
import com.storedobject.common.Address;
import com.storedobject.common.PhoneNumber;

public abstract class AccountEntity<T extends StoredObject> extends StoredObject
        implements HasContacts, HasName, HasShortName {

    private T party;
    private String shortName;
    private String primaryAddress;
    private String primaryEmail;
    private String primaryPhone;
    private String taxCode;

    public AccountEntity() {
    }

    public static void columns(Columns columns) {
        columns.add("ShortName", "text");
        columns.add("PrimaryAddress", "address");
        columns.add("PrimaryEmail", "email");
        columns.add("PrimaryPhone", "phone");
        columns.add("TaxCode", "text");
    }

    public static void indices(Indices indices) {
        indices.add("lower(ShortName)", "ShortName<>''", true);
    }

    public static String[] browseColumns() {
        return new String[] {
                "ShortName as Code",
                "Name",
                "PrimaryAddress",
                "PrimaryEmail",
                "PrimaryPhone",
        };
    }

    public final void setParty(Id partyId) {
        if (!loading() && !Id.equals(this.getPartyId(), partyId)) {
            throw new Set_Not_Allowed("Party");
        }
        this.party = null;
        setPartyId(partyId);
    }

    protected abstract void setPartyId(Id id);

    public void setParty(T party) {
        setParty(party == null ? null : party.getId());
    }

    protected abstract Id getPartyId();

    public final T getParty() {
        if (this.party == null) {
            this.party = getRelated(getPartyClass(), getPartyId());
        }
        return this.party;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    @Column(style = "(code)", required = false, order = 200)
    public String getShortName() {
        return shortName;
    }

    public void setPrimaryAddress(String primaryAddress) {
        this.primaryAddress = primaryAddress;
    }

    @Column(style = "(address)", required = false, order = 300)
    public String getPrimaryAddress() {
        return primaryAddress;
    }

    public Address getPrimaryAddressValue() {
        return Address.create(primaryAddress);
    }

    public void setPrimaryEmail(String primaryEmail) {
        this.primaryEmail = primaryEmail;
    }

    @Column(style = "(email)", required = false, order = 400)
    public String getPrimaryEmail() {
        return primaryEmail;
    }

    public void setPrimaryPhone(String primaryPhone) {
        this.primaryPhone = primaryPhone;
    }

    @Column(style = "(phone)", required = false, order = 500)
    public String getPrimaryPhone() {
        return primaryPhone;
    }

    public void setTaxCode(String taxCode) {
        this.taxCode = taxCode;
    }

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

    protected abstract Class<T> getPartyClass();

    public abstract String getName();

    public final boolean isPersonal() {
        return getPartyClass() == Person.class;
    }

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
}
