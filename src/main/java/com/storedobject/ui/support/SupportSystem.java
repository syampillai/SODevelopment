package com.storedobject.ui.support;

import com.storedobject.common.SORuntimeException;
import com.storedobject.common.StringList;
import com.storedobject.core.*;
import com.storedobject.ui.Application;
import com.storedobject.ui.ELabel;
import com.storedobject.ui.ObjectComboField;
import com.storedobject.ui.ObjectField;
import com.storedobject.ui.common.MemoSystem;
import com.storedobject.vaadin.*;
import com.storedobjects.support.*;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.icon.VaadinIcon;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class SupportSystem extends MemoSystem {

    private final Map<Id, Organization> supportOrg = new HashMap<>();
    private List<Organization> organizations;
    private boolean user = false; // Determines whether the current user is a support user
    private ObjectField<Product> productField;
    private final ObjectField<ProductModule> moduleField =
            new ObjectField<>("Module", ProductModule.class, ObjectField.Type.CHOICE);
    private ObjectComboField<Product> productFilterField;
    private Product productFilter;
    private ObjectComboField<Organization> organizationFilterField;
    private Organization organizationFilter;
    private IssueEditor<? extends Issue> issueEditor;

    public SupportSystem() {
        this("SS");
    }

    public SupportSystem(MemoType type) {
        this(type, true);
    }

    public SupportSystem(String typeShortName) {
        this(typeShortName, true);
    }

    public SupportSystem(String typeShortName, boolean load) {
        this(memoType(typeShortName), load);
    }

    public SupportSystem(MemoType type, boolean load) {
        super(type, StringList.concat(StoredObjectUtility.browseColumns(MemoComment.class),
                StringList.create("From", "Product AS Product/Service")), load);
        addConstructedListener(e -> setColumnVisible("From", !user));
    }

    public String getFrom(MemoComment mc) {
        return ((Issue)mc.getMemo()).getEntity().toDisplay();
    }

    public String getProduct(MemoComment mc) {
        return ((Issue)mc.getMemo()).getProduct().toDisplay();
    }

    @Override
    protected void checkMemoType(MemoType type) {
        if(type == null || !type.getSpecial() || !Issue.class.isAssignableFrom(type.getMemoClass())) {
            throw new SORuntimeException("Not a support type");
        }
    }

    @Override
    protected String whoName(SystemUser who) {
        return user ? (who.getName() + " (" + organizations.getFirst().getName() + ")") : assistantName(who);
    }

    @Override
    protected String assistantName(SystemUser assistant) {
        String s = super.assistantName(assistant);
        Organization organization = getOrganization(assistant);
        if(organization.isVirtual()) return s;
        return organization.getName() + " - " + s;
    }

    private Organization getOrganization(SystemUser systemUser) {
        Organization organization = supportOrg.get(systemUser.getId());
        if(organization == null) {
            SupportUser supportUser = StoredObject.get(SupportUser.class, "SupportUser=" + systemUser.getId());
            if(supportUser == null) {
                organization = new Organization();
                organization.makeVirtual();
            } else {
                organization = supportUser.getOrganization();
            }
            supportOrg.put(systemUser.getId(), organization);
        }
        return organization;
    }

    @Override
    protected List<SystemUser> assistants() {
        if(user) return super.assistants();
        String orgIDs = organizations.stream().map(o -> "" + o.getId())
                .collect(Collectors.joining(","));
        List<SystemUser> list = StoredObject.list(SupportUser.class, "Organization IN (" + orgIDs + ") AND Id<>"
                        + getTransactionManager().getUser().getId())
                .map(SupportUser::getSupportUser).filter(SystemUser::userLoadFilter).toList();
        list.sort(Comparator.comparing(this::assistantName, String.CASE_INSENSITIVE_ORDER));
        return list;
    }

    @Override
    protected boolean filterAssistant(SystemUser who) {
        if(!user) {
            return false;
        }
        return StoredObject.exists(SupportUser.class, "SupportUser=" + who.getId()) ||
                StoredObject.exists(SupportPerson.class, "Person=" + who.getId());
    }

    @Override
    protected boolean canAssignAssistant() {
        return user;
    }

    @Override
    protected boolean canCreateNew() {
        if(productField != null) {
            if(user || isAssisting()) {
                Organization organization = getOrganization(who());
                productField.getField().load(organization.listProducts());
            } else {
                productField.getField().load(StoredObject.list(Product.class));
            }
        }
        return user || isAssisting();
    }

    @Override
    protected void addExtraButtons() {
        if(user) return;
        buttonPanel.add(new Button("Create Internal Task", VaadinIcon.TASKS, e -> createInternalTask()));
    }

    private void createInternalTask() {
        Memo m;
        MemoComment mc = selected();
        String s;
        if(mc == null) {
            m = null;
            s = "You haven't selected any ticket.\nSo, an independent internal task";
        } else {
            m = mc.getMemo();
            s = "A sub-task of " + m.getReference();
        }
        s += " will be created.\nAre you sure?";
        ActionForm.execute(s, () -> createNewMemo(m));
    }

    @Override
    protected boolean canReject() {
        return false;
    }

    @Override
    public void execute(View lock) {
        if(organizations != null) {
            super.execute(lock);
            return;
        }
        clearAlerts();
        organizations = new ArrayList<>();
        SystemUser su = getTransactionManager().getUser();
        SupportUser supportUser = StoredObject.get(SupportUser.class, "SupportUser=" + su.getId());
        if(supportUser != null) {
            user = true;
            setColumnVisible("From", false);
            organizations.add(supportUser.getOrganization());
        }
        SupportPerson supportPerson = StoredObject.get(SupportPerson.class, "Person=" + su.getId());
        if (supportPerson == null) {
            if(user) {
                executeAsSupportUser(lock);
                return;
            }
            warning("You are not part of the support system");
            return;
        }
        supportPerson.listOrganizations().collectAll(organizations);
        if (organizations.isEmpty()) {
            warning("You are not assigned to any organization to provide support");
            return;
        }
        AtomicBoolean hasInternalSkill = new AtomicBoolean(false);
        Set<Id> skills = new HashSet<>();
        supportPerson.listLinks(ProductSkill.class).forEach(s -> {
            if(s.getProduct().getInternal()) hasInternalSkill.set(true);
            skills.add(s.getProductId());
        });
        boolean anySkill = hasInternalSkill.get();
        if(!anySkill) {
            for (Organization o : organizations) {
                anySkill = o.listLinks(Product.class).anyMatch(p -> skills.contains(p.getId()));
                if (anySkill) break;
            }
        }
        if(!anySkill) {
            warning("You are not assigned to any product to provide support");
            return;
        }
        setColumnVisible("From", true);
        if(user && organizations.size() == 1) {
            executeAsSupportUser(lock);
            return;
        }
        if(user) {
            new ChooseUserType(this, lock).execute();
        } else {
            executeAsSupportPerson(lock);
        }
    }

    private void executeAsSupportUser(View lock) {
        Organization userOrganization = organizations.getFirst();
        organizations.clear();
        organizations.add(userOrganization);
        user = true;
        setProductField(productField);
        super.execute(lock);
    }

    private void executeAsSupportPerson(View lock) {
        if(user) {
            organizations.removeFirst();
            user = false;
        }
        productFilterField = new ObjectComboField<>(Product.class);
        productFilterField.setMinWidth("300px");
        productFilterField.setClearButtonVisible(true);
        productFilterField.addValueChangeListener(e -> productChanged());
        organizationFilterField = new ObjectComboField<>(Organization.class, organizations);
        organizationFilterField.setMinWidth("300px");
        organizationFilterField.setClearButtonVisible(true);
        organizationFilterField.addValueChangeListener(e -> organizationChanged());
        super.execute(lock);
    }

    private void setProductField(ObjectField<Product> field) {
        if(field == null) {
            return;
        }
        this.productField = field;
        canCreateNew(); // Reload products
        this.productField.addValueChangeListener(e -> {
            Id pid = e.getValue();
            if(!Id.isNull(pid)) {
                moduleField.load(pid.listLinks(ProductModule.class));
            }
        });
    }

    @Override
    protected String getMemoLabel() {
        return "Issue";
    }

    @Override
    protected String getCreateLabel() {
        return "Log";
    }

    @Override
    protected <M extends Memo> MemoEditor<M> createMemoEditor(Class<M> memoClass) {
        if(Issue.class.isAssignableFrom(memoClass)) {
            return createIssueEditor(memoClass);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private <M extends Memo, I extends Issue> MemoEditor<M> createIssueEditor(Class<M> memoClass) {
        if(issueEditor == null) {
            Class<I> issueClass = (Class<I>) memoClass;
            issueEditor = new IssueEditor<>(issueClass);
        }
        return (MemoEditor<M>) issueEditor;
    }

    private static boolean checkAlertHandler = true;

    private class IssueEditor<I extends Issue> extends MemoEditor<I> {

        protected IssueEditor(Class<I> objectClass) {
            super(objectClass);
            if(checkAlertHandler) {
                checkAlertHandler = false;
                if (!StoredObject.exists(ApplicationAlertHandler.class, "DataClassName='com.storedobjects.support.Issue'")) {
                    ApplicationAlertHandler aah = new ApplicationAlertHandler();
                    aah.setDataClassName(Issue.class.getName());
                    aah.setLogicClassName(SupportSystem.class.getName());
                    try {
                        getTransactionManager().transact(aah::save);
                    } catch (Exception ignored) {
                    }
                }
            }
        }

        @Override
        public boolean isFieldVisible(String fieldName) {
            return switch (fieldName) {
                case "SystemEntity", "Type" -> false;
                default -> super.isFieldVisible(fieldName);
            };
        }

        @Override
        protected HasValue<?, ?> createField(String fieldName, String label) {
            if(fieldName.equals("ProductModule")) {
                return moduleField;
            }
            return super.createField(fieldName, label);
        }

        @Override
        protected void customizeField(String fieldName, HasValue<?, ?> field) {
            if("Product".equals(fieldName) && field instanceof ObjectField<?>) {
                //noinspection unchecked
                setProductField((ObjectField<Product>) field);
            }
            super.customizeField(fieldName, field);
        }
    }

    @Override
    protected void addToHeader(ButtonLayout buttonLayout) {
        if(productFilterField == null) return;
        buttonLayout.add(new ELabel("|", Application.COLOR_INFO), new ELabel("Filters - Organization"),
                organizationFilterField, new ELabel("Product"), productFilterField);
    }

    @Override
    protected boolean filter(Memo memo) {
        if(user) return true;
        Issue issue = (Issue) memo;
        if(productFilter != null && !issue.getProductId().equals(productFilter.getId())) {
            return false;
        }
        return organizationFilter == null || issue.getOrganizationId().equals(organizationFilter.getId());
    }

    private void productChanged() {
        productFilter = productFilterField.getValue();
        organizationFilter = organizationFilterField.getValue();
        loadMemos();
    }

    private void organizationChanged() {
        Product p = productFilterField.getValue();
        organizationFilter = organizationFilterField.getValue();
        if(organizationFilter == null) {
            if(p == null) {
                productFilter = null;
                loadMemos();
                return;
            }
            productFilterField.clear();
            return;
        }
        productFilterField.setLoadFilter(product -> organizationFilter.existsLinks(Product.class,
                "Id=" + product.getId()));
        productFilter = productFilterField.getValue();
        if(Objects.equals(p, productFilter)) {
            loadMemos();
        }
    }

    private static class ChooseUserType extends DataForm {

        private final SupportSystem system;
        private final View lock;
        private final RadioChoiceField choiceField = new RadioChoiceField("Choose", new String[] { "Support User", "Support Person"} );

        public ChooseUserType(SupportSystem system, View lock) {
            super("Choose Your Role");
            this.system = system;
            this.lock = lock;
            addField(choiceField);
        }

        @Override
        protected boolean process() {
            close();
            if(choiceField.getValue() == 0) {
                system.executeAsSupportUser(lock);
            } else {
                system.executeAsSupportPerson(lock);
            }
            return true;
        }
    }

    @Override
    protected List<ExtraMemoField> extraMemoFields() {
        List<ExtraMemoField> extraMemoFields = new ArrayList<>();
        extraMemoFields.add(new ExtraMemoField("Product", this::product));
        extraMemoFields.add(new ExtraMemoField("Product Module", this::productModule));
        return extraMemoFields;
    }

    private String product(Memo memo) {
        return ((Issue)memo).getProduct().toDisplay();
    }

    private String productModule(Memo memo) {
        ProductModule pm = ((Issue)memo).getProductModule();
        return pm == null ? "" : pm.toDisplay();
    }
}
