package com.storedobject.ui.accounts;

import com.storedobject.accounts.BusinessEntity;
import com.storedobject.accounts.EntityAccount;
import com.storedobject.core.*;
import com.storedobject.ui.*;
import com.storedobject.ui.common.EntityRoleEditor;
import com.storedobject.vaadin.Button;
import com.vaadin.flow.component.icon.VaadinIcon;

import java.util.function.BiFunction;

/**
 * An editor for {@link EntityRole} with tax details in which the fields of the {@link Entity} get merged seamlessly
 * with the fields of the {@link EntityRole}.
 * <p></p>
 *
 * @param <T> Type of Entity Role.
 * @author Syam
 */
public class TaxEntityRoleEditor<T extends EntityRole> extends EntityRoleEditor<T> {

    private final Button taxSetup = new Button("Tax Setup", VaadinIcon.BOOK_DOLLAR, e -> taxSetup());
    private ObjectEditor<BusinessEntity> taxEditor;
    private BiFunction<EntityRole, TransactionManager, EntityAccount> accountFinder;

    /**
     * Constructor.
     *
     * @param objectClass Entity role class.
     */
    public TaxEntityRoleEditor(Class<T> objectClass) {
        this(objectClass, 0, null);
    }

    /**
     * Constructor.
     *
     * @param objectClass Entity role class.
     * @param actions Actions allowed (ORed values of {@link com.storedobject.core.EditorAction}).
     */
    public TaxEntityRoleEditor(Class<T> objectClass, int actions) {
        this(objectClass, actions, null);
    }

    /**
     * Constructor.
     *
     * @param objectClass Entity role class.
     * @param actions Actions allowed (ORed values of {@link com.storedobject.core.EditorAction}).
     * @param caption Caption.
     */
    public TaxEntityRoleEditor(Class<T> objectClass, int actions, String caption) {
        super(objectClass, actions, caption);
    }

    /**
     * Constructor.
     *
     * @param className Name of the Entity role class.
     */
    public TaxEntityRoleEditor(String className) throws Exception {
        super(className);
    }

    @Override
    protected void addExtraButtons() {
        if (getObject() != null) buttonPanel.add(taxSetup);
    }

    protected void setAccountFinder(BiFunction<EntityRole, TransactionManager, EntityAccount> accountFinder) {
        this.accountFinder = accountFinder;
    }

    private void taxSetup() {
        if(accountFinder == null) {
            warning("No account finder defined!");
            return;
        }
        EntityRole role = getObject();
        if (role == null) return;
        EntityAccount account;
        try {
            account = accountFinder.apply(role, getTransactionManager());
            if(account == null) {
                warning("No account found for " + role.toDisplay());
                return;
            }
        } catch (Exception e) {
            error(e);
            return;
        }
        if (taxEditor == null) taxEditor = ObjectEditor.create(BusinessEntity.class);
        taxEditor.editObject((BusinessEntity) account.getEntity(), this);
    }
}
