package com.storedobject.accounts;

import com.storedobject.core.*;
import com.storedobject.core.annotation.*;
import com.storedobject.common.Address;
import com.storedobject.common.PhoneNumber;

public abstract class AccountEntity<T extends StoredObject> extends StoredObject {

    private T party;
    private String shortName;
    private String address;
    private String phone;
    private String taxCode;

    public AccountEntity() {
    }

    public static void columns(Columns columns) {
        columns.add("ShortName", "text");
        columns.add("Address", "address");
        columns.add("Phone", "phone");
        columns.add("TaxCode", "text");
    }

    public static void indices(Indices indices) {
        indices.add("lower(ShortName)", "ShortName<>''", true);
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

    public void setAddress(String address) {
        this.address = address;
    }

    @Column(style = "(address)", required = false, order = 300)
    public String getAddress() {
        return address;
    }

    public Address getAddressValue() {
        return Address.create(address);
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Column(style = "(phone)", required = false, order = 400)
    public String getPhone() {
        return phone;
    }

    public void setTaxCode(String taxCode) {
        this.taxCode = taxCode;
    }

    @Column(style = "(code)", required = false, order = 500)
    public String getTaxCode() {
        return taxCode;
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        setPartyId(tm.checkType(this, getPartyId(), getPartyClass(), false));
        shortName = toCode(shortName);
        if (address == null) {
            address = "";
        } else {
            address = address.trim();
        }
        if (!address.isEmpty()) {
            address = Address.check(address);
        }
        if (phone == null) {
            phone = "";
        } else {
            phone = phone.trim();
        }
        if (!phone.isEmpty()) {
            phone = PhoneNumber.check(phone);
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
}
