package com.storedobject.ui.accounts;

import com.storedobject.core.Account;
import com.storedobject.core.AccountTitle;
import com.storedobject.core.Id;
import com.storedobject.ui.ObjectField;

/**
 * A specialized field for handling account-related objects, extending the functionality
 * of the {@link ObjectField}. This field is designed to work with instances of the
 * {@code Account} class or its subclasses.
 *
 * @param <T> The type of the account object, which must extend the {@code Account} class.
 *
 * @author Syam
 */
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

    /**
     * Retrieves the account associated with this field.
     * If the underlying object is of type {@code AccountTitle} and the account's class is
     * assignable to the expected object class, the associated account is returned.
     * Otherwise, the object itself is returned or null if the conditions are not met.
     *
     * @return The account of type {@code T}, the original object, or null if no valid account is found.
     */
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

    /**
     * Retrieves the ID of the account associated with this field.
     *
     * @return The ID of the account if one is set, or {@code null} if no account is associated.
     */
    public Id getAccountId() {
        T a = getAccount();
        return a == null ? null : a.getId();
    }

    /**
     * Sets the value for this instance. If the provided value is an instance of
     * AccountTitle, it sets the value using the associated account's ID. Otherwise,
     * it falls back to the default implementation.
     *
     * @param a the value to be set. Can be of generic type T. If the value is an
     *          instance of AccountTitle, it extracts and sets the associated
     *          account's ID.
     */
    @Override
    public void setValue(T a) {
        if(a instanceof AccountTitle at) {
            setValue(at.getAccount().getId());
        } else {
            super.setValue(a);
        }
    }

    /**
     * Sets the account for this field.
     *
     * @param a The account to set, which is an instance of the generic type T.
     */
    public void setAccount(T a) {
        setValue(a);
    }
}
