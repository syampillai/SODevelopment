package com.storedobject.ui.account;

import com.storedobject.core.Account;
import com.storedobject.core.AccountTitle;
import com.storedobject.core.Id;
import com.storedobject.ui.ObjectField;

public class AccountField<T extends Account> extends ObjectField<T> {

    /**
     * Constructor.
     */
    public AccountField() {
        this((String) null);
    }


    /**
     * Constructor.
     *
     * @param label Label for the field.
     */
    public AccountField(String label) {
        //noinspection unchecked
        this(label, (Class<T>) Account.class, true);
    }

    /**
     * Constructor.
     *
     * @param accountClass Class of the accounts that are valid.
     */
    public AccountField(Class<T> accountClass) {
        this(null, accountClass);
    }

    /**
     * Constructor.
     *
     * @param accountClass Class of the accounts that are valid.
     * @param type Desired type of the field.
     */
    public AccountField(Class<T> accountClass, Type type) {
        this(null, accountClass, type);
    }

    /**
     * Constructor.
     *
     * @param label Label for the field.
     * @param accountClass Class of the accounts that are valid.
     */
    public AccountField(String label, Class<T> accountClass) {
        this(label, accountClass, accountClass == Account.class);
    }

    /**
     * Constructor.
     *
     * @param label Label for the field.
     * @param accountClass Class of the accounts that are valid.
     * @param type Desired type of the field.
     */
    public AccountField(String label, Class<T> accountClass, Type type) {
        this(label, accountClass, accountClass == Account.class, type);
    }

    /**
     * Constructor.
     *
     * @param accountClass Class of the accounts that are valid.
     * @param any Whether subclasses should be allowed or not.
     */
    public AccountField(Class<T> accountClass, boolean any) {
        this(null, accountClass, any, Type.AUTO);
    }

    /**
     * Constructor.
     *
     * @param label Label for the field.
     * @param accountClass Class of the accounts that are valid.
     * @param any Whether subclasses should be allowed or not.
     */
    public AccountField(String label, Class<T> accountClass, boolean any) {
        this(label, accountClass, any, Type.AUTO);
    }

    /**
     * Constructor.
     *
     * @param accountClass Class of the accounts that are valid.
     * @param any Whether subclasses should be allowed or not.
     * @param type Desired type of the field.
     */
    public AccountField(Class<T> accountClass, boolean any, Type type) {
        this(null, accountClass, any, type);
    }

    /**
     * Constructor.
     *
     * @param label Label for the field.
     * @param accountClass Class of the accounts that are valid.
     * @param any Whether subclasses should be allowed or not.
     * @param type Desired type of the field.
     */
    public AccountField(String label, Class<T> accountClass, boolean any, Type type) {
        super(label, accountClass, any, type);
    }

    public T getAccount() {
        T a = super.getObject();
        if(a instanceof AccountTitle t) {
            Account oa = t.getAccount();
            if(getObjectClass().isAssignableFrom(oa.getClass())) {
                //noinspection unchecked
                return (T)oa;
            }
            return null;
        }
        return a;
    }

    public Id getAccountId() {
        T a = getAccount();
        return a == null ? null : a.getId();
    }
}
