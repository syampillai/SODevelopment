package com.storedobject.ui.common;

import com.storedobject.common.SORuntimeException;
import com.storedobject.core.*;
import com.storedobject.ui.Application;
import com.storedobject.ui.*;
import com.storedobject.vaadin.*;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.grid.contextmenu.GridMenuItem;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class MemoSystem extends ObjectGrid<MemoComment> implements CloseableView, Transactional, AlertHandler {

    private final MemoType memoType;
    private MemoEditor<?> memoEditor;
    private CommentEditor commentEditor;
    private NewMemoType newMemoType;
    private SystemUser who;
    private Button whoButton;
    private SelectWho selectWho;
    private SelectAssistant selectAssistant;
    private String comment;
    private ELabel rightClick;
    private boolean history = false;
    private Id filterMemoId;
    private MemoComment prevCache;
    private ReasonForm reasonForm;

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
        super(MemoComment.class);
        checkMemoType(memoType);
        this.memoType = memoType;
        who = getTransactionManager().getUser();
        if(who.getStatus() != 0) {
            throw new SORuntimeException("You are not a regular user");
        }
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
                contextMenu.addItem(getApproveLabel(), e -> e.getItem().ifPresent(mc -> memoAction(mc, 3)));
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
            if(mc == null || mc.getMemo().getStatus() == 6) {
                editItem.setVisible(false);
                editSubject.setVisible(false);
                closeMemo.setVisible(false);
                editComment.setVisible(false);
                recallMemo.setVisible(mc != null && canRecall(mc));
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
            select(mc);
            editItem.setVisible(canEditMemo(mc));
            editSubject.setVisible(!editItem.isVisible() && canEditSubject(mc));
            boolean canComment = canComment(mc);
            editComment.setVisible(canComment);
            closeMemo.setVisible(mine() && canCloseMemo(mc));
            recallMemo.setVisible(mine() && canRecall(mc));
            boolean canForward = mine() && canForward(mc);
            forwardMemo.setVisible(canForward);
            boolean canReturn = mine() && canReturn(mc);
            returnMemo.setVisible(canReturn);
            if(!canForward && !canReturn && mine()) {
                String why = mc.getMemo().whyNoTakers();
                if(why != null) {
                    clearAlerts();
                    error(why);
                }
            }
            returnMemoToInitiator.setVisible(mine() && !canReturn && mc.canReturnToInitiator(who));
            approveMemo.setVisible(mine() && canApprove(mc));
            reopenMemo.setVisible(mine() && canReopen(mc));
            escalateMemo.setVisible(mine() && canEscalate(mc));
            rejectMemo.setVisible(canReject() && mine() && canApprove(mc));
            assignAssistant.setVisible(canAssignAssistant() && mine() && mc.getMemo().getStatus() < 4);
            createNew.setVisible(memoType == null && !mc.getMemo().getType().getSpecial());
            assignAssistant.setVisible(false);
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
    }

    private static MemoType memoType(String shortName) {
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

    protected void checkMemoType(MemoType type) {
    }

    protected String getMemoLabel() {
        return "Memo";
    }

    protected String getCreateLabel() {
        return "Create";
    }

    protected String getApproveLabel() {
        return "Approve";
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

    private void loadMemos() {
        prevCache = null;
        String filter = "CommentedBy=" + who.getId();
        if(!history) {
            filter += " AND T.Status<3";
        }
        if(memoType != null) {
            filter += " AND Memo.Type=" + memoType.getId();
        }
        if(filterMemoId != null) {
            filter += " AND Memo=" + filterMemoId;
        }
        ObjectIterator<MemoComment> mcs = StoredObject.list(MemoComment.class, filter, "Memo DESC,CommentedAt DESC")
                .filter(this::ownerFilter).convert(this::convertFilter).filter(Objects::nonNull);
        load(mcs);
        deselectAll();
    }

    private MemoComment convertFilter(MemoComment mc) {
        Memo m = mc.getMemo();
        MemoComment lastComment = m.getLatestComment();
        if(history) {
            return lastComment;
        }
        if(lastComment.equals(mc)) {
            return mc;
        }
        return m.isMine(who) ? lastComment : null;
    }

    private boolean ownerFilter(MemoComment mc) {
        if(memoType == null) {
            if(mc.getMemo().getType().getSpecial()) {
                return false;
            }
        }
        if(prevCache != null && prevCache.getMemoId().equals(mc.getMemoId())) {
            return false;
        }
        if(mc.getMemo() == null) {
            return false;
        }
        prevCache = mc;
        return true;
    }

    @Override
    public Component createHeader() {
        Checkbox h = new Checkbox("Include History");
        h.addValueChangeListener(e -> {
            history = e.getValue();
            loadMemos();
        });
        return new ButtonLayout(
                getConfigureButton(),
                canCreateNew() ? new Button(getCreateLabel() + " New " + getMemoLabel(), "add",
                        e -> newMemo((MemoComment) null)) : null,
                new Button("Load", e -> loadMemos()),
                h,
                new Button("Exit", e -> close())
        );
    }

    protected String whoName(SystemUser who) {
        return who.getName();
    }

    @Override
    public void createHeaders() {
        whoButton = new Button(whoName(who), (String) null, e -> selectWho()).asSmall();
        rightClick = new ELabel("Please right-click on the entry to see all options", Application.COLOR_SUCCESS);
        rightClick.setVisible(!isEmpty());
        ButtonLayout b = new ButtonLayout(whoButton, rightClick);
        prependHeader().join().setComponent(b);
    }

    @Override
    public void loaded() {
        super.loaded();
        if(rightClick != null) {
            rightClick.setVisible(!isEmpty());
        }
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
            Memo memo = mt.getMemoClass().getDeclaredConstructor().newInstance();
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

    private void editMemo(Memo memo) {
        memoEditor(memo).editObject(memo, getView());
    }

    private <M extends Memo> void viewMemo(MemoComment mc) {
        comment = mc.getContent();
        @SuppressWarnings("unchecked") M memo = (M)mc.getMemo();
        MemoEditor<M> me = memoEditor(memo);
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
    private <M extends Memo> MemoEditor<M> memoEditor(M memo) {
        clearAlerts();
        if(memoEditor == null || memoEditor.getObjectClass() != memo.getClass()) {
            memoEditor = createMemoEditor(memo.getClass());
            if(memoEditor == null) {
                memoEditor = new MemoEditor<>(memo.getClass());
            }
            memoEditor.setMemoSystem(this);
        }
        return (MemoEditor<M>) memoEditor;
    }

    private void selectWho() {
        if(canAssist()) {
            if (selectWho == null) {
                selectWho = new SelectWho();
                selectWho.suField.setValue(who);
            }
            selectWho.execute(getView());
        }
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


    private abstract class SelectSU extends DataForm {

        final UserField suField = new UserField(SystemUser.class);

        public SelectSU(String label) {
            super("Select");
            suField.setLabel(label);
            suField.setFilter("Status=0", false);
            suField.setLoadFilter(MemoSystem.this::whoFilter, false);
            addField(suField);
            setRequired(suField);
        }

        @Override
        protected final boolean process() {
            SystemUser su = suField.getObject();
            Id eid = getTransactionManager().getEntity().getId();
            if(su.listEntities().stream().noneMatch(e -> e.getId().equals(eid))) {
                warning(su.getName() + " doesn't belong to " + getTransactionManager().getEntity().getName());
                return false;
            }
            setSU(su);
            return true;
        }

        protected abstract void setSU(SystemUser su);
    }

    private boolean whoFilter(SystemUser su) {
        return !su.getId().equals(who.getId()) && filterWho(su);
    }

    protected boolean filterWho(SystemUser who) {
        return true;
    }

    private class SelectWho extends SelectSU {

        public SelectWho() {
            super("Assist");
        }

        @Override
        protected void setSU(SystemUser su) {
            who = su;
            memoEditor = null;
            whoButton.setText(whoName(who));
            loadMemos();
        }
    }

    private class SelectAssistant extends SelectSU {

        private MemoComment memoComment;

        public SelectAssistant() {
            super("Assistant");
        }

        @Override
        protected void setSU(SystemUser su) {
            if(transact(t -> memoComment.assignAssistant(t, su))) {
                loadMemos();
            }
        }
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
            return 60;
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
        protected void saveObject(Transaction t, M object) throws Exception {
            object.save(t, memoSystem.comment, memoSystem.who);
        }

        @Override
        public void inserted(M object) {
            memoSystem.memoCreated(object);
        }

        @Override
        public void setObject(M object, boolean load) {
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

    protected boolean canAssist() {
        return true;
    }

    protected boolean canCreateNew() {
        return true;
    }

    protected boolean canReject() {
        return true;
    }

    private void recallMemo(MemoComment mc) {
        if(transact(mc::recallMemo)) {
            loadMemos();
        }
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
        commentEditor.action = action;
        commentEditor.editObject(mc, getView());
    }

    private void selectAssistant(MemoComment mc) {
        if(selectAssistant == null) {
            selectAssistant = new SelectAssistant();
        }
        selectAssistant.memoComment = mc;
        selectAssistant.execute(getView());
    }

    private class CommentEditor extends ObjectEditor<MemoComment> {

        int action;
        private final UserField suField = new UserField("Forward to", SystemUser.class, ObjectField.Type.CHOICE);
        private SystemUser su;
        private final Comments comments = new Comments(this);
        private Focusable<?> commentField;

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
            addField("MemoStatus");
            addField("Comments");
            addConstructedListener(f -> {
                setColumnSpan((Component) getField("Comment"), 2);
                setColumnSpan((Component) getField("Subject"), 2);
                setRequired(suField, false);
            });
        }

        private Id suid() {
            return su == null ? null : su.getId();
        }

        @Override
        protected HasValue<?, ?> createField(String fieldName, Class<?> fieldType, String label) {
            if("Subject".equals(fieldName)) {
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
            return switch(fieldName) {
                case "MemoStatus" -> 110;
                case "ForwardTo" -> 750;
                case "Subject" -> 760;
                case "Comments" -> 770;
                default -> super.getFieldOrder(fieldName);
            };
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
            if(object.getCommentCount() == 0) return;
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
                case 3 -> getApproveLabel();
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

        void set(MemoComment mc) {
            removeAll();
            comments.clear();
            add(buttons);
            StoredObject.list(MemoComment.class, "Memo=" + mc.getMemoId(), "CommentCount")
                    .forEach(this::addComment);
            orderButton.setIcon(ascending ? VaadinIcon.ANGLE_DOUBLE_UP : VaadinIcon.ANGLE_DOUBLE_DOWN);
            if(!ascending) {
                for(int n = comments.size(); --n >= 0;) {
                    add(comments.get(n));
                }
            }
            orderButton.setVisible(comments.size() > 1);
        }

        private void toggleOrder() {
            ascending = !ascending;
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
            buttons.setVisible(true);
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
                action = mc.getMemo().renameActionVerb(action);
                add(new Badge(action + " by " + mc.getCommentedBy().getName() + " at " + getCommentedAt(mc)));
                Paragraph p = new Paragraph();
                p.getStyle().set("font-style", "italic").set("line-height", "normal");
                c.lines().forEach(line -> {
                    p.add(new Span(line));
                    p.add(new HtmlComponent("br"));
                });
                add(p);
                ButtonLayout attachments = new ButtonLayout();
                AtomicBoolean any = new AtomicBoolean(false);
                mc.listLinks(MemoAttachment.class).forEach(a -> {
                    attachments.add(new Button(a.getName(), VaadinIcon.PAPERCLIP, e -> Application.get().view(a)).asSmall());
                    any.set(true);
                });
                if(any.get()) {
                    add(attachments);
                }
                Box box = new Box(this);
                box.setStyle("width", "100%");
            }
        }
    }

    private class ReasonForm extends DataForm {

        private final ELabelField status = new ELabelField("Status");
        private final TextArea reason = new TextArea("Reason");
        private MemoComment mc;
        private boolean escalate;

        public ReasonForm() {
            super("Reason");
            reason.setMaxHeight(20, Unit.CH);
            addField(status, reason);
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
                tran(t -> mc.escalateMemo(t, r));
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
            execute();
        }

        @Override
        public int getMinimumContentWidth() {
            return 60;
        }
    }
}
