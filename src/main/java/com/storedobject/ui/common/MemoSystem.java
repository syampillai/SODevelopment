package com.storedobject.ui.common;

import com.storedobject.common.SORuntimeException;
import com.storedobject.core.*;
import com.storedobject.ui.Application;
import com.storedobject.ui.*;
import com.storedobject.vaadin.*;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.grid.contextmenu.GridMenuItem;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MemoSystem extends ObjectGrid<MemoComment> implements CloseableView, Transactional, AlertHandler {

    protected final MemoType memoType;
    protected final ButtonLayout buttonPanel = new ButtonLayout();
    private MemoEditor<?> memoEditor;
    private CommentEditor commentEditor;
    private NewMemoType newMemoType;
    private SystemUser who;
    private Button whoButton;
    private SelectBoss selectBoss;
    private SelectAssistant selectAssistant;
    private String comment;
    private final ELabel entryCount = new ELabel();
    private final ELabel rightClick = new ELabel("|", Application.COLOR_INFO)
            .append(" Right-click on the entry to see options", Application.COLOR_SUCCESS)
            .update();
    protected final HistoryCheckbox history = new HistoryCheckbox(c -> loadMemos());
    private Id filterMemoId;
    private ReasonForm reasonForm;
    private boolean assisting = false;
    private final Button newButton = new Button(getCreateLabel() + " New " + getMemoLabel(), "add",
            e -> createNewMemo());

    public MemoSystem() {
        this(true);
    }

    public MemoSystem(MemoType memoType) {
        this(memoType, true);
    }

    public MemoSystem(String memoTypeShortName) {
        this(memoType(memoTypeShortName));
    }

    public MemoSystem(boolean load) {
        this(null, load);
    }

    public MemoSystem(MemoType memoType, boolean load) {
        this(memoType, null, load);
    }

    protected MemoSystem(MemoType memoType, Iterable<String> columns, boolean load) {
        super(MemoComment.class, columns);
        checkMemoType(memoType);
        this.memoType = memoType;
        who = getTransactionManager().getUser();
        checkUser(who);
        setCaption(memoType == null ? Application.getLogicCaption("Memo System") : memoType.getName());
        GridContextMenu<MemoComment> contextMenu = new GridContextMenu<>(this);
        contextMenu.addItem("View", e -> e.getItem().ifPresent(this::viewMemo));
        GridMenuItem<MemoComment> editItem =
                contextMenu.addItem("Edit", e -> e.getItem().ifPresent(this::editMemo));
        GridMenuItem<MemoComment> editSubject =
                contextMenu.addItem("Edit Subject", e -> e.getItem().ifPresent(this::editSubject));
        GridMenuItem<MemoComment> editComment =
                contextMenu.addItem("Add details", e -> e.getItem().ifPresent(mc -> memoAction(mc, 0)));
        GridMenuItem<MemoComment> closeMemo =
                contextMenu.addItem("Close", e -> e.getItem().ifPresent(this::closeMemo));
        GridMenuItem<MemoComment> recallMemo =
                contextMenu.addItem("Recall", e -> e.getItem().ifPresent(this::recallMemo));
        GridMenuItem<MemoComment> returnMemo =
                contextMenu.addItem("Return", e -> e.getItem().ifPresent(mc -> memoAction(mc, 1)));
        GridMenuItem<MemoComment> returnMemoToInitiator =
                contextMenu.addItem("Return to Initiator", e -> e.getItem().ifPresent(mc -> memoAction(mc, 5)));
        GridMenuItem<MemoComment> forwardMemo =
                contextMenu.addItem("Forward", e -> e.getItem().ifPresent(mc -> memoAction(mc, 2)));
        GridMenuItem<MemoComment> approveMemo =
                contextMenu.addItem("*", e -> e.getItem().ifPresent(mc -> memoAction(mc, 3)));
        GridMenuItem<MemoComment> rejectMemo =
                contextMenu.addItem("Reject", e -> e.getItem().ifPresent(mc -> memoAction(mc, 4)));
        GridMenuItem<MemoComment> reopenMemo =
                contextMenu.addItem("Reopen", e -> e.getItem().ifPresent(mc -> reason(mc, false)));
        GridMenuItem<MemoComment> escalateMemo =
                contextMenu.addItem("Escalate", e -> e.getItem().ifPresent(mc -> reason(mc, true)));
        GridMenuItem<MemoComment> assignAssistant =
                contextMenu.addItem("Assign Assistant", e -> e.getItem().ifPresent(this::selectAssistant));
        GridMenuItem<MemoComment> createNew =
                contextMenu.addItem("Create New " + getMemoLabel(), e -> newMemo(e.getItem().orElse(null)));
        contextMenu.setDynamicContentHandler(mc -> {
            deselectAll();
            select(mc);
            if(isViewMode() || mc == null || mc.getMemo().getStatus() == 6) {
                editItem.setVisible(false);
                editSubject.setVisible(false);
                closeMemo.setVisible(false);
                editComment.setVisible(false);
                recallMemo.setVisible(!isViewMode() && mc != null && canRecall(mc));
                forwardMemo.setVisible(false);
                returnMemo.setVisible(false);
                returnMemoToInitiator.setVisible(false);
                rejectMemo.setVisible(false);
                approveMemo.setVisible(false);
                assignAssistant.setVisible(false);
                createNew.setVisible(false);
                reopenMemo.setVisible(false);
                escalateMemo.setVisible(false);
                return true;
            }
            editItem.setVisible(canEditMemo(mc));
            editSubject.setVisible(!editItem.isVisible() && canEditSubject(mc));
            boolean canComment = canComment(mc);
            editComment.setVisible(canComment);
            closeMemo.setVisible(canCloseMemo(mc));
            recallMemo.setVisible(canRecall(mc));
            boolean canForward = canForward(mc);
            forwardMemo.setVisible(canForward);
            boolean canReturn = canReturn(mc);
            returnMemo.setVisible(canReturn);
            if(!canForward && !canReturn && mine()) {
                String why = mc.getMemo().whyNoTakers();
                if(why != null) {
                    clearAlerts();
                    error(why);
                }
            }
            returnMemoToInitiator.setVisible(!canReturn && mc.canReturnToInitiator(who));
            boolean canApprove = mine() && canApprove(mc);
            approveMemo.setVisible(canApprove);
            if(canApprove) {
                approveMemo.setText(mc.getMemo().getApproveLabel());
            }
            reopenMemo.setVisible(canReopen(mc));
            escalateMemo.setVisible(canEscalate(mc));
            rejectMemo.setVisible(canReject() && mine() && canApprove(mc));
            assignAssistant.setVisible(canAssignAssistant() && mc.getMemo().getStatus() < 4);
            createNew.setVisible(memoType != null && !mc.getMemo().getType().getSpecial());
            return true;
        });
        if(load) {
            loadMemos();
        }
        addItemDoubleClickListener(e -> {
            MemoComment mc = e.getItem();
            if(mc != null) {
                viewMemo(mc);
            }
        });
        addConstructedListener(e -> {
            Grid.Column<MemoComment> c = getColumn("Memo.Subject");
            if(c != null) {
                c.setFlexGrow(0).setWidth("25vw").setAutoWidth(false);
            }
        });
    }

    protected static MemoType memoType(String shortName) {
        int p;
        if(shortName != null && (p = shortName.indexOf('|')) > 0) {
            shortName = shortName.substring(0, p);
        }
        shortName = StoredObject.toCode(shortName);
        MemoType t = StoredObject.get(MemoType.class, "ShortPrefix='" + shortName + "'");
        if(t == null) {
            throw new SORuntimeException(shortName + " doesn't exist");
        }
        return t;
    }

    protected void checkUser(SystemUser who) {
        if(who.getStatus() != 0) {
            throw new SORuntimeException("You are not a regular user");
        }
    }

    protected boolean isViewMode() {
        return false;
    }

    @Override
    public void execute(View lock) {
        super.execute(lock);
        Application.get().closeMenu();
    }

    protected void checkMemoType(MemoType type) {
    }

    protected String getMemoLabel() {
        return "Memo";
    }

    protected String getCreateLabel() {
        return "Create";
    }

    @Override
    public boolean includeColumn(String columnName) {
        if(memoType != null && "Memo.Type.Name".equals(columnName)) {
            return false;
        }
        return super.includeColumn(columnName);
    }

    private boolean mine() {
        return getTransactionManager().getUser().getId().equals(who.getId());
    }

    protected String memoLoadFilter() {
        return "EXISTS (SELECT * FROM core.MemoComment WHERE Memo=T.Id AND CommentedBy=" + who.getId() + ")";
    }

    protected <M extends Memo> void loadMemos() {
        String filter = "SystemEntity";
        List<SystemEntity> entityList = getTransactionManager().getUser().listEntities();
        if(entityList.size() == 1) {
            filter += "=" + entityList.getFirst().getId();
        } else {
            filter += " IN (" + entityList.stream().map(SystemEntity::getId).map(Id::toString)
                    .collect(Collectors.joining(",")) + ")";
        }
        filter += " AND ";
        boolean h = history.getValue();
        if(h) {
            filter += history.getFilter("Date");
        } else {
            filter += "Status<10";
        }
        if(memoType != null) {
            filter += " AND Type=" + memoType.getId();
        }
        if(filterMemoId != null) {
            filter += " AND Memo=" + filterMemoId;
        }
        if(assisting) {
            filter += " AND AssistedBy=" + getTransactionManager().getUser().getId();
        }
        String extraFilter = memoLoadFilter();
        if(extraFilter != null && !extraFilter.isEmpty()) filter += " AND " + extraFilter;
        QueryBuilder<M> q = QueryBuilder.from(memoClass());
        q.where(filter).orderBy("SystemEntity,No DESC");
        ObjectIterator<MemoComment> mcs = q.list().filter(this::filter)
                .map(Memo::getLatestComment).filter(Objects::nonNull)
                .filter(mc -> {
                    if(h) return true;
                    Memo m = mc.getMemo();
                    if(m.getStatus() > 3) { // Approved, Rejected
                        return m.getInitiatedById().equals(who.getId()) || m.getAssistedById().equals(who.getId());
                    }
                    return true;
                })
                .filter(this::filter);
        load(mcs);
        deselectAll();
    }

    @SuppressWarnings("unchecked")
    protected final <M extends Memo> Class<M> memoClass() {
        return (Class<M>)(memoType == null ? Memo.class : memoType.getMemoClass());
    }

    protected boolean filter(Memo memo) {
        return true;
    }

    protected boolean filter(MemoComment memoComment) {
        return true;
    }

    @Override
    public final Component createHeader() {
        newButton.setVisible(canCreateNew());
        GridSearchField<MemoComment> searchField = new GridSearchField<>(this);
        searchField.configure(mc -> {
            Memo m = mc.getMemo();
            return m.getSubject() + " " + mc.getPendingWith() + " " + mc.getMemoStatus() + " " + m.getInitiatedBy().getName();
        });
        buttonPanel.add(getConfigureButton(), newButton, searchField, new Button("Load", e -> loadMemos()));
        addExtraButtons();
        buttonPanel.add(history, new Button("Exit", e -> close()));
        return buttonPanel;
    }

    protected void addExtraButtons() {
    }

    protected String whoName(SystemUser who) {
        return who.getName();
    }

    @Override
    public void createHeaders() {
        whoButton = new Button(whoName(who), (String) null, e -> selectWho()).asSmall();
        whoButton();
        rightClick.setVisible(!isEmpty());
        ButtonLayout b = new ButtonLayout(whoButton, new ELabel("|", Application.COLOR_INFO),
                new ELabel("Count"), entryCount, rightClick);
        addToHeader(b);
        prependHeader().join().setComponent(b);
    }

    protected final void whoButton() {
        String t = whoName(who);
        if(assisting) {
            t += " - Assisting";
        }
        whoButton.setText(t);
        whoButton.getElement().setAttribute("title", assisting ? "Click to change" : assistantMessage(who));
    }

    protected String assistantMessage(SystemUser who) {
        return "Click here if you want to assist someone else";
    }

    protected void addToHeader(ButtonLayout buttonLayout) {
    }

    @Override
    public void loaded() {
        super.loaded();
        entryCount.clearContent().append("#" + size(), Application.COLOR_SUCCESS).update();
        rightClick.setVisible(!isEmpty());
        filterMemoId = null;
    }

    @Override
    public void clean() {
        super.clean();
        clear();
        filterMemoId = null;
        if(memoEditor != null) {
            memoEditor.close();
        }
        if(commentEditor != null) {
            commentEditor.close();
        }
    }

    public void executeAndLoad() {
        execute();
        loadMemos();
    }

    public String getCommentedAt(MemoComment mc) {
        return DateUtility.formatWithTimeHHMM(getTransactionManager().date(mc.getCommentedAt()));
    }

    protected final void createNewMemo() {
        newMemo((MemoComment) null);
    }

    private void newMemo(MemoComment mc) {
        if(memoType != null) {
            newMemo(memoType);
            return;
        }
        clearAlerts();
        if(newMemoType == null) {
            newMemoType = new NewMemoType();
            newMemoType.creatingFor.setValue(getTransactionManager().getUser());
        }
        if(mc != null) {
            newMemoType.type.setValue(mc.getMemo().getType());
        }
        newMemoType.execute(this.getView());
    }

    private void newMemo(MemoType mt) {
        clearAlerts();
        if(mt.getInactive()) {
            warning("Not active - " + mt.toDisplay());
            return;
        }
        try {
            MemoEditor<?> me = memoEditor(mt.getMemoClass());
            Memo memo = me.createObjectInstance();
            if(memoType != null && !memoType.getSpecial()) {
                memo.setSubject(memoType.getName());
            }
            memo.setSystemEntity(getTransactionManager().getEntity());
            comment = mt.getContentTemplate();
            memo.setType(mt);
            editMemo(memo);
        } catch(Throwable e) {
            log(e);
            error("Unable to create - " + mt.toDisplay());
        }
    }

    private void editMemo(MemoComment mc) {
        clearAlerts();
        if(mc == null) {
            mc = selected();
            if(mc == null) {
                return;
            }
        }
        if(!canEditMemo(mc)) {
            warning("Can't edit!");
            return;
        }
        comment = mc.getContent();
        editMemo(mc.getMemo());
    }

    private void closeMemo(MemoComment mc) {
        clearAlerts();
        new ActionForm("Status is '" + mc.getMemoStatus()
                + "'.\nDo you really want to " + (mc.getMemo().getStatus() == 0 ? "abandon" : "close") + " it?",
                () -> tran(mc::closeMemo)).execute();
    }

    private void tran(TransactionManager.Transact transact) {
        try {
            getTransactionManager().transact(transact);
        } catch(Exception e) {
            warning(e);
        }
        loadMemos();
    }

    private boolean canCloseMemo(MemoComment mc) {
        return mc.canClose(who);
    }

    private boolean canEditMemo(MemoComment mc) {
        return mc.canEdit(who);
    }

    private boolean canEditSubject(MemoComment mc) {
        return mc.canEditSubject(who);
    }

    private <M extends Memo> void editMemo(M memo) {
        @SuppressWarnings("unchecked") MemoEditor<M> me = (MemoEditor<M>) memoEditor(memo.getClass());
        me.editObject(memo, getView());
    }

    public void viewMemo() {
        MemoComment mc = selected();
        if(mc != null) {
            viewMemo(mc);
        }
    }

    private <M extends Memo> void viewMemo(MemoComment mc) {
        comment = mc.getContent();
        @SuppressWarnings("unchecked") M memo = (M)mc.getMemo();
        @SuppressWarnings("unchecked") MemoEditor<M> me = (MemoEditor<M>) memoEditor(memo.getClass());
        me.viewObject(memo, getView());
        me.comments.set(mc);
    }

    protected SystemUser getUser() {
        return who;
    }

    protected <M extends Memo> MemoEditor<M> createMemoEditor(Class<M> memoClass) {
        return null;
    }

    @SuppressWarnings("unchecked")
    private <M extends Memo> MemoEditor<M> memoEditor(Class<M> memoClass) {
        clearAlerts();
        if(memoEditor == null || memoEditor.getObjectClass() != memoClass) {
            memoEditor = createMemoEditor(memoClass);
            if(memoEditor == null) {
                memoEditor = new MemoEditor<>(memoClass);
            }
            memoEditor.setMemoSystem(this);
        }
        return (MemoEditor<M>) memoEditor;
    }

    protected void selectWho() {
        if (selectBoss == null) {
            selectBoss = new SelectBoss();
            selectBoss.suField.setValue(who);
        }
        selectBoss.execute(getView());
    }

    @Override
    public void handleAlert(StoredObject memo) {
        filterMemoId = memo instanceof Memo ? null : memo.getId();
        loadMemos();
        execute();
    }

    @Override
    public void handleAlert(Id id) {
        handleAlert(StoredObject.get(Memo.class, id, true));
    }


    private boolean assistantFilter(SystemUser su) {
        return !su.getId().equals(who.getId()) && filterAssistant(su);
    }

    protected boolean filterAssistant(SystemUser who) {
        return true;
    }

    private class SelectAssistant extends DataForm {

        final UserField suField = UserField.forLinks("Select Assistant", who, MemoSystem.this::assistantFilter);
        MemoComment memoComment;

        public SelectAssistant() {
            super("Select");
            addField(suField);
            setRequired(suField);
        }

        @Override
        protected void execute(View parent, boolean doNotLock) {
            //noinspection SizeReplaceableByIsEmpty
            if(suField.getField() instanceof ObjectComboField<SystemUser> f && f.size() == 0) {
                warning("No assistants available!");
                return;
            }
            suField.clear();
            super.execute(parent, doNotLock);
        }

        @Override
        protected final boolean process() {
            clearAlerts();
            SystemUser su = suField.getObject();
            if(su.getId().equals(getTransactionManager().getUser().getId())) {
                return false;
            }
            close();
            if(su.getId().equals(memoComment.getMemo().getAssistedById())) {
                message("Already assigned to " + su.getName());
                return true;
            }
            if(transact(t -> memoComment.assignAssistant(t, su))) {
                message("Assigned to " + su.getName());
            }
            return true;
        }

        @Override
        public int getMinimumContentHeight() {
            return 40;
        }
    }

    protected String assistantName(SystemUser assistant) {
        return assistant.getName();
    }

    protected List<SystemUser> assistants() {
        return getTransactionManager().getUser().listAssistants().toList();
    }

    private class SelectBoss extends DataForm {

        final UserField suField = new UserField("Assist", assistants());

        public SelectBoss() {
            super("Select");
            addField(suField);
            if(suField.getField() instanceof ObjectComboField<SystemUser> f) {
                f.setItemLabelGenerator(MemoSystem.this::assistantName);
                f.setClearButtonVisible(true);
            }
        }

        @Override
        protected void execute(View parent, boolean doNotLock) {
            //noinspection SizeReplaceableByIsEmpty
            if(suField.getField() instanceof ObjectComboField<SystemUser> f && f.size() == 0) {
                warning("You can't assist anyone!");
                return;
            }
            suField.clear();
            super.execute(parent, doNotLock);
        }

        @Override
        protected final boolean process() {
            clearAlerts();
            close();
            who = suField.getObject();
            if(who == null || who.getId().equals(getTransactionManager().getUser().getId())) {
                who = getTransactionManager().getUser();
                assisting = false;
            } else {
                assisting = true;
            }
            newButton.setVisible(canCreateNew());
            memoEditor = null;
            whoButton();
            loadMemos();
            return true;
        }

        @Override
        public int getMinimumContentWidth() {
            return 40;
        }
    }

    protected final SystemUser who() {
        return who;
    }

    protected boolean isAssisting() {
        return assisting;
    }

    private void editSubject(MemoComment mc) {
        EditSubject es = new EditSubject();
        es.memo = mc.getMemo();
        es.execute();
    }

    private class EditSubject extends DataForm {

        private final TextField subject = new TextField("Subject");
        Memo memo;

        public EditSubject() {
            super("Subject");
            addField(subject);
            setRequired(subject);
        }

        @Override
        protected void execute(View parent, boolean doNotLock) {
            subject.setValue(memo.getSubject());
            super.execute(parent, doNotLock);
        }

        @Override
        protected boolean process() {
            String s = subject.getValue().trim();
            close();
            if(memo.getSubject().equals(s)) {
                MemoSystem.this.message("No changes made");
            } else {
                if(transact(t -> memo.updateSubject(t, s))) {
                    MemoSystem.this.message("Subject updated");
                    loadMemos();
                }
            }
            return true;
        }

        @Override
        public int getMinimumContentWidth() {
            return 40;
        }
    }

    private class NewMemoType extends DataForm {

        final ObjectField<MemoType> type = new ObjectField<>("Select", MemoType.class, true);
        private final ELabelField creatingFor = new ELabelField("Creating for");

        public NewMemoType() {
            super(getCreateLabel() + " New");
            addField(type, creatingFor);
            setRequired(type);
            type.setFilter("NOT Special AND NOT Inactive");
        }

        @Override
        protected boolean process() {
            close();
            newMemo(type.getObject());
            return true;
        }

        @Override
        protected void execute(View parent, boolean doNotLock) {
            creatingFor.clearContent().append(who.getName()).update();
            super.execute(parent, doNotLock);
        }
    }

    protected static class MemoEditor<M extends Memo> extends ObjectEditor<M> {

        private MemoSystem memoSystem;
        private Comments comments;
        private CompoundField commentsField;

        protected MemoEditor(Class<M> objectClass) {
            super(objectClass);
            addField("Reference");
            addField("InitiatedBy", this::initBy);
            addField("LastCommentBy", this::lastCommentBy);
            addField("Comments");
            setIncludeFieldChecker(n -> !n.equals("No"));
            addConstructedListener(f -> {
                setFieldReadOnly("Type", "Date", "Status");
                setColumnSpan((Component) getField("Subject"), 2);
                setColumnSpan(commentsField, 2);
                setFieldVisible(TransactionManager.isMultiTenant(), getField("SystemEntity"));
                setFieldReadOnly("SystemEntity");
            });
            addObjectChangedListener(new ObjectChangedListener<>() {
                @Override
                public void saved(M object) {
                    memoSystem.loadMemos();
                }

                @Override
                public void inserted(M object) {
                    memoSystem.memoCreated(object);
                    memoSystem.loadMemos();
                }
            });
        }

        private void setMemoSystem(MemoSystem memoSystem) {
            this.memoSystem = memoSystem;
            comments = memoSystem.createComments(this);
            commentsField = new CompoundField(comments);
        }

        @Override
        public boolean isFieldEditable(String fieldName) {
            return !"Type".equals(fieldName) && super.isFieldEditable(fieldName);
        }

        @Override
        protected HasValue<?, ?> createField(String fieldName, String label) {
            return switch(fieldName) {
                case "InitiatedBy", "LastCommentBy" -> new TextField(label);
                case "Comments" -> commentsField;
                default -> super.createField(fieldName, label);
            };
        }

        @Override
        protected void customizeField(String fieldName, HasValue<?, ?> field) {
            if("Subject".equals(fieldName) && field instanceof TextField tf) {
                tf.setMaxLength(200);
            }
            super.customizeField(fieldName, field);
        }

        private String initBy(Memo memo) {
            SystemUser u = memo.getInitiatedBy();
            return u == null ? memoSystem.who.getName() : u.getName();
        }

        private String lastCommentBy(Memo memo) {
            SystemUser u = memo.getLastCommentBy();
            return u == null ? "None" : u.getName();
        }

        @Override
        protected int getFieldOrder(String fieldName) {
            return switch(fieldName) {
                case "Reference" -> 300;
                case "InitiatedBy" -> 380;
                case "LastCommentBy" -> 390;
                case "Comments" -> 20000;
                default -> super.getFieldOrder(fieldName);
            };
        }

        @Override
        protected M createObjectInstance() {
            M instance = super.createObjectInstance();
            if(memoSystem.assisting) instance.setAssistedBy(getTransactionManager().getUser());
            return instance;
        }

        @Override
        protected void saveObject(Transaction t, M object) throws Exception {
            object.save(t, memoSystem.comment, memoSystem.who);
        }

        @Override
        public void inserted(M object) {
            memoSystem.memoCreated(object);
        }

        @Override
        public void setObject(M object, boolean load) {
            comments.removeAll();
            super.setObject(object, load);
            setCaption(object == null ? "Memo" : object.getReference());
        }
    }

    protected void memoCreated(Memo memo) {
    }

    private boolean canComment(MemoComment mc) {
        return mc.canComment(who);
    }

    private List<SystemUser> approvers(MemoComment mc) {
        return mc.getMemo().listApprovers();
    }

    private List<SystemUser> commenters(MemoComment mc) {
        return mc.getMemo().listCommenters();
    }

    private boolean canApprove(MemoComment mc) {
        return mc.canApprove(who);
    }

    private boolean canReopen(MemoComment mc) {
        return mc.canReopen(who);
    }

    private boolean canEscalate(MemoComment mc) {
        return mc.canEscalate(who);
    }

    private boolean canForward(MemoComment mc) {
        return mc.canForward(who);
    }

    private boolean canReturn(MemoComment mc) {
        return mc.canReturn(who);
    }

    private boolean canRecall(MemoComment mc) {
        return mc.canRecall(who);
    }

    protected boolean canAssignAssistant() {
        return true;
    }

    protected boolean canCreateNew() {
        return true;
    }

    protected boolean canReject() {
        return true;
    }

    protected void recallMemo(MemoComment mc) {
        if(transact(mc::recallMemo)) {
            loadMemos();
        }
    }

    protected final void forwardMemo(MemoComment mc, SystemUser to) {
        memoAction(mc, 2);
        commentEditor.suField.setValue(to);
        commentEditor.suField.setReadOnly(true);
    }

    private void reason(MemoComment mc, boolean escalate) {
        if(reasonForm == null) {
            reasonForm = new ReasonForm();
        }
        reasonForm.setComment(mc, escalate);
    }

    private void memoAction(MemoComment mc, int action) {
        if(commentEditor == null) {
            commentEditor = new CommentEditor();
        }
        commentEditor.suField.setReadOnly(false);
        commentEditor.action = action;
        if(action == 2 && mc.getComment().isBlank() && mc.getCommentCount() > 1) {
            MemoComment pc = mc.getPrevious();
            if(pc.getStatus() == 6) { // It was reopened, copy the reason as the comment
                mc.setComment(pc.getComment());
            }
        }
        commentEditor.editObject(mc, getView());
    }

    private void selectAssistant(MemoComment mc) {
        if(!canAssignAssistant()) {
            return;
        }
        if(selectAssistant == null) {
            selectAssistant = new SelectAssistant();
        }
        selectAssistant.memoComment = mc;
        selectAssistant.execute(getView());
    }

    private class CommentEditor extends ObjectEditor<MemoComment> {

        int action;
        private final UserField suField = new UserField("Forward to", ObjectField.Type.CHOICE);
        private SystemUser su;
        private final Comments comments = new Comments(this);
        private Focusable<?> commentField;
        private final List<ExtraMemoField> extraMemoFields = new ArrayList<>();

        CommentEditor() {
            super(MemoComment.class);
            setIncludeFieldChecker(n -> switch(n) {
                case "EnteredBy", "CommentedBy" -> false;
                default -> true;
            });
            addObjectChangedListener(new ObjectChangedListener<>() {
                @Override
                public void saved(MemoComment object) {
                    loadMemos();
                }
            });
            addField("ForwardTo", o -> suid(), (o, v) -> su = suField.getObject());
            addField("Subject", o -> o.getMemo().getSubject());
            List<ExtraMemoField> emfs = extraMemoFields();
            if(emfs != null) {
                for(ExtraMemoField field: emfs) {
                    if(field != null) {
                        addField(field.name(), o -> field.getter().apply(o.getMemo()));
                        extraMemoFields.add(field);
                    }
                }
            }
            addField("MemoStatus");
            addField("Comments");
            addConstructedListener(f -> {
                setColumnSpan((Component) getField("Comments"), 2);
                setColumnSpan((Component) getField("Subject"), 2);
                setRequired(suField, false);
            });
        }

        private Id suid() {
            return su == null ? null : su.getId();
        }

        @Override
        protected HasValue<?, ?> createField(String fieldName, Class<?> fieldType, String label) {
            if("Subject".equals(fieldName) || extraMemoFields.stream().anyMatch(f -> f.name().equals(fieldName))) {
                return new TextField(label);
            }
            if("ForwardTo".equals(fieldName)) {
                return suField;
            }
            if("Comments".equals(fieldName)) {
                return new CompoundField(comments);
            }
            return super.createField(fieldName, fieldType, label);
        }

        @Override
        protected String getLabel(String fieldName) {
            return switch(fieldName) {
                case "Memo" -> "No.";
                case "MemoStatus" -> "Status";
                default -> super.getLabel(fieldName);
            };
        }

        @Override
        protected int getFieldOrder(String fieldName) {
            int o = extraFieldOrder(fieldName);
            if(o >= 0) {
                return 110 + o;
            }
            return switch(fieldName) {
                case "MemoStatus" -> 110;
                case "ForwardTo" -> 750;
                case "Subject" -> 760;
                case "Comment" -> 761;
                case "Comments" -> 770;
                default -> super.getFieldOrder(fieldName);
            };
        }

        private int extraFieldOrder(String fieldName) {
            for(int i = 0; i < extraMemoFields.size(); i++) {
                if(extraMemoFields.get(i).name().equals(fieldName)) {
                    return i;
                }
            }
            return -1;
        }

        @Override
        public boolean isFieldEditable(String fieldName) {
            return switch(fieldName) {
                case "Attachments.l", "Comment", "ForwardTo" -> true;
                default -> false;
            };
        }

        @Override
        protected void customizeField(String fieldName, HasValue<?, ?> field) {
            if("Comment".equals(fieldName) && (field instanceof Focusable<?> f)) {
                commentField = f;
            }
            super.customizeField(fieldName, field);
        }

        @Override
        public void validateData() throws Exception {
            clearAlerts();
            super.validateData();
            if(suField.isVisible() && su == null) {
                throw new SOException("Please select 'Forward to'");
            }
        }

        @Override
        protected void saveObject(Transaction t, MemoComment object) throws Exception {
            switch(action) {
                case 1 -> object.returnMemo(t, object.getComment());
                case 2 -> object.forwardMemo(t, object.getComment(), su);
                case 3 -> object.approveMemo(t, object.getComment(), suField.isVisible() ? su : null);
                case 4 -> object.rejectMemo(t, object.getComment());
                case 5 -> object.returnMemoToInitiator(t, object.getComment());
                default -> object.commentMemo(t, object.getComment());
            }
        }

        @Override
        public void setObject(MemoComment object, boolean load) {
            super.setObject(object, load);
            if(object != null) {
                ((TextArea)getField("Comment")).setLabel(object.getCommentCount() == 0 ?
                        "Detailed Description" : "Comment");
                setCaption(object.getMemo().getReference());
                comments.set(object);
            }
        }

        @Override
        protected void execute(View parent, boolean doNotLock) {
            MemoComment mc = getObject();
            if(action == 3 && mc.getComment().isBlank()) {
                mc.setComment(mc.getMemo().renameActionVerb("Approved"));
            }
            if(action == 2) {
                setFieldVisible(suField);
                suField.load(ObjectIterator.create(commenters(mc)).filter(u -> !u.getId().equals(who.getId())));
            } else if(action == 3) {
                int a = mc.getMemo().getApprovalsRequired();
                if (a > 1) {
                    int approvals = StoredObject.count(MemoComment.class,
                            "Memo=" + mc.getMemoId() + " AND Status=3") + 1;
                    if (approvals >= a) {
                        a = 1;
                    }
                }
                setFieldVisible(a > 1, suField);
                if (a > 1) {
                    suField.load(ObjectIterator.create(approvers(mc)).filter(u -> !u.getId().equals(who.getId())));
                }
            } else if(action == 5) { // Return to initiator
                su = mc.getFirst().getCommentedBy();
                suField.load(ObjectIterator.create(su));
                suField.setValue(su);
            } else {
                setFieldHidden(suField);
            }
            super.execute(parent, doNotLock);
            save.setText(switch(action) {
                case 1, 5 -> "Return";
                case 2 -> "Forward";
                case 3 -> mc.getMemo().getApproveLabel();
                case 4 -> "Reject";
                default -> "Save";
            });
            setFirstFocus(action == 5 ? commentField : null);
        }
    }

    private Comments createComments(View view) {
        return new Comments(view);
    }

    private class Comments extends VerticalLayout {

        private static final String BACKGROUND = "#E6F2F2";
        private MemoComment memoComment;
        private final View view;
        private final Button showButton, orderButton;
        private boolean showing = true;
        private boolean ascending = true;
        private final ButtonLayout buttons = new ButtonLayout();
        private final List<CommentCard> comments = new ArrayList<>();

        Comments(View view) {
            this.view = view;
            showButton = new Button("Hide", VaadinIcon.EYE_SLASH, e -> toggleVisibility()).asSmall();
            orderButton = new Button("Reverse Order", VaadinIcon.ANGLE_DOUBLE_UP, e -> toggleOrder()).asSmall();
            buttons.add(new ELabel("Comments/Details:"), showButton, orderButton);
        }

        @Override
        public void removeAll() {
            super.removeAll();
            comments.clear();
            showing = true;
            showButton.setIcon(VaadinIcon.EYE_SLASH);
            showButton.setVisible(true);
            showButton.setText("Hide");
            orderButton.setVisible(false);
        }

        void set(MemoComment mc) {
            removeAll();
            VerticalLayout v = new VerticalLayout();
            v.add(buttons);
            v.add(new Badge("Pending with " + mc.getMemo().getPendingWith()));
            Box box = new Box(v);
            box.setStyle("background-color", BACKGROUND);
            add(v);
            StoredObject.list(MemoComment.class, "Memo=" + mc.getMemoId(), "CommentCount")
                    .forEach(this::addComment);
            orderButton.setIcon(ascending ? VaadinIcon.ANGLE_DOUBLE_DOWN : VaadinIcon.ANGLE_DOUBLE_UP);
            if(!ascending) {
                for(int n = comments.size(); --n >= 0;) {
                    add(comments.get(n));
                }
            }
            orderButton.setVisible(comments.size() > 1);
        }

        private void toggleOrder() {
            ascending = !ascending;
            orderButton.setIcon(ascending ? VaadinIcon.ANGLE_DOUBLE_DOWN : VaadinIcon.ANGLE_DOUBLE_UP);
            comments.forEach(this::remove);
            if(ascending) {
                comments.forEach(this::add);
            } else {
                for(int n = comments.size(); --n >= 0;) {
                    add(comments.get(n));
                }
            }
            if(view instanceof CommentEditor ce) {
                ce.commentField.focus();
            }
        }

        private void toggleVisibility() {
            showing = !showing;
            getChildren().forEach(c -> c.setVisible(showing));
            //noinspection OptionalGetWithoutIsPresent
            buttons.getParent().get().setVisible(true);
            if(view instanceof CommentEditor ce) {
                ce.commentField.focus();
            }
            showButton.setIcon(showing ? VaadinIcon.EYE_SLASH : VaadinIcon.EYE);
            showButton.setVisible(true);
            showButton.setText(showing ?  "Hide" : "Show");
            orderButton.setVisible(showing && comments.size() > 1);
        }

        private void addComment(MemoComment mc) {
            if(mc == memoComment) {
                if(!(view instanceof ObjectEditor<?>)) {
                    view.execute();
                }
                return;
            }
            memoComment = mc;
            String c = mc.getComment();
            if(c.isBlank()) {
                return;
            }
            CommentCard cc = new CommentCard(mc, c);
            comments.add(cc);
            if(ascending) {
                add(cc);
            }
        }

        private class CommentCard extends VerticalLayout {

            private CommentCard(MemoComment mc, String c) {
                String action;
                if(mc.getCommentCount() == 0) {
                    action = "Initiated";
                } else {
                    action = switch(mc.getStatus()) {
                        case 2 -> "Returned";
                        case 3 -> "Approved";
                        case 4 -> "Rejected";
                        case 5 -> "Closed";
                        case 6 -> "Reopened";
                        case 7 -> "Escalated";
                        default -> "Commented";
                    };
                }
                action = (mc.getCommentCount() + 1) + ". " + mc.getMemo().renameActionVerb(action);
                add(new Badge(action + " by " + assistantName(mc.getCommentedBy()) + " at " + getCommentedAt(mc)));
                Paragraph p = new Paragraph();
                p.getStyle().set("font-style", "italic").set("font-weight", "bold").set("line-height", "normal");
                c.lines().forEach(line -> {
                    p.add(new Span(line));
                    p.add(new HtmlComponent("br"));
                });
                add(p);
                ButtonLayout attachments = new ButtonLayout();
                AtomicBoolean any = new AtomicBoolean(false);
                mc.listLinks(MemoAttachment.class).forEach(a -> {
                    attachments.add(new Button(a.getName(), VaadinIcon.PAPERCLIP,
                            e -> Application.get().view(a, true)).asSmall());
                    any.set(true);
                });
                if(any.get()) {
                    add(attachments);
                }
                Box box = new Box(this);
                box.setStyle("width", "100%");
                box.setStyle("background-color", BACKGROUND);
            }
        }
    }

    private class ReasonForm extends DataForm {

        private final ELabelField status = new ELabelField("Status");
        private final TextArea reason = new TextArea("Reason");
        private final ObjectComboField<SystemUser> escalateTo = new ObjectComboField<>("Escalate To", SystemUser.class, List.of());
        private MemoComment mc;
        private boolean escalate;

        public ReasonForm() {
            super("Reason");
            new SpeechRecognition(reason);
            reason.setMaxHeight(20, Unit.CH);
            escalateTo.setClearButtonVisible(true);
            addField(status, escalateTo, reason);
            setRequired(reason);
            setFirstFocus(reason);
        }

        @Override
        protected boolean process() {
            String r = reason.getValue().trim();
            if(r.length() < 10) {
                MemoSystem.this.message("Reason is too short!");
                return false;
            }
            clearAlerts();
            close();
            if(escalate) {
                tran(t -> mc.escalateMemo(t, r, escalateTo.getObject()));
            } else {
                tran(t -> mc.reopenMemo(t, r));
            }
            return true;
        }

        void setComment(MemoComment mc, boolean escalate) {
            clearAlerts();
            this.mc = mc;
            this.escalate = escalate;
            status.clearContent().append("Current status is \"" + mc.getStatusValue() + "\"").update();
            setCaption("Reason for " + (escalate ? "Escalation" : "Reopening"));
            setFieldVisible(escalate, escalateTo);
            if(escalate) {
                List<SystemUser> approvers = mc.getMemo().listNextLevelApprovers();
                if(approvers.isEmpty()) {
                    warning("Can not escalate to anyone!");
                    return;
                }
                escalateTo.load(approvers);
            }
            execute();
        }

        @Override
        public int getMinimumContentWidth() {
            return 60;
        }
    }

    protected List<ExtraMemoField> extraMemoFields() {
        return null;
    }

    public record ExtraMemoField(String name, Function<Memo, String> getter) {
    }
}
