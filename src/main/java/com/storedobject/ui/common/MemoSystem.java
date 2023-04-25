package com.storedobject.ui.common;

import com.storedobject.common.SORuntimeException;
import com.storedobject.core.*;
import com.storedobject.ui.Application;
import com.storedobject.ui.*;
import com.storedobject.vaadin.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.grid.contextmenu.GridMenuItem;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
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
    private final Map<Id, Set<SystemUser>> commenters = new HashMap<>(), approvers = new HashMap<>();
    private ViewComments viewComments;
    private Id filterMemoId;
    private MemoComment prevCache;

    public MemoSystem() {
        this(true);
    }

    public MemoSystem(MemoType memoType) {
        this(memoType, true);
    }

    public MemoSystem(String memoTypeShortName) {
        this(mt(memoTypeShortName));
    }

    public MemoSystem(boolean load) {
        this(null, load);
    }

    public MemoSystem(MemoType memoType, boolean load) {
        super(MemoComment.class);
        this.memoType = memoType;
        who = getTransactionManager().getUser();
        if(who.getStatus() != 0) {
            throw new SORuntimeException("You are not a regular user");
        }
        setCaption(memoType == null ? Application.getLogicCaption("Memo System") : memoType.getName());
        GridContextMenu<MemoComment> contextMenu = new GridContextMenu<>(this);
        contextMenu.addItem("View Content", e -> e.getItem().ifPresent(this::viewMemo));
        GridMenuItem<MemoComment> viewComments =
                contextMenu.addItem("View Comments", e -> e.getItem().ifPresent(this::viewComments));
        GridMenuItem<MemoComment> editItem =
                contextMenu.addItem("Edit", e -> e.getItem().ifPresent(this::editMemo));
        GridMenuItem<MemoComment> editSubject =
                contextMenu.addItem("Edit Subject", e -> e.getItem().ifPresent(this::editSubject));
        GridMenuItem<MemoComment> attachDocs =
                contextMenu.addItem("Attach Documents", e -> e.getItem().ifPresent(mc -> memoAction(mc, 0)));
        GridMenuItem<MemoComment> editComment =
                contextMenu.addItem("Edit Comment", e -> e.getItem().ifPresent(mc -> memoAction(mc, 0)));
        GridMenuItem<MemoComment> closeMemo =
                contextMenu.addItem("Close", e -> e.getItem().ifPresent(this::closeMemo));
        GridMenuItem<MemoComment> recallMemo =
                contextMenu.addItem("Recall", e -> e.getItem().ifPresent(this::recallMemo));
        GridMenuItem<MemoComment> returnMemo =
                contextMenu.addItem("Return", e -> e.getItem().ifPresent(mc -> memoAction(mc, 1)));
        GridMenuItem<MemoComment> forwardMemo =
                contextMenu.addItem("Forward", e -> e.getItem().ifPresent(mc -> memoAction(mc, 2)));
        GridMenuItem<MemoComment> approveMemo =
                contextMenu.addItem("Approve", e -> e.getItem().ifPresent(mc -> memoAction(mc, 3)));
        GridMenuItem<MemoComment> rejectMemo =
                contextMenu.addItem("Reject", e -> e.getItem().ifPresent(mc -> memoAction(mc, 4)));
        GridMenuItem<MemoComment> assignAssistant =
                contextMenu.addItem("Assign Assistant", e -> e.getItem().ifPresent(this::selectAssistant));
        GridMenuItem<MemoComment> createNew =
                contextMenu.addItem("Create New", e -> newMemo(e.getItem().orElse(null)));
        contextMenu.setDynamicContentHandler(mc -> {
            deselectAll();
            if(mc != null && mc.getCommentCount() != mc.getMemo().getLastComment()) {
                viewComments.setVisible(true);
            } else {
                viewComments.setVisible(mc != null && mc.getMemo().getStatus() > 0);
            }
            if(mc == null || mc.getMemo().getStatus() == 6) {
                editItem.setVisible(false);
                editSubject.setVisible(false);
                closeMemo.setVisible(false);
                attachDocs.setVisible(false);
                editComment.setVisible(false);
                recallMemo.setVisible(mc != null && canRecall(mc));
                forwardMemo.setVisible(false);
                returnMemo.setVisible(false);
                rejectMemo.setVisible(false);
                approveMemo.setVisible(false);
                assignAssistant.setVisible(false);
                createNew.setVisible(false);
                return true;
            }
            select(mc);
            editItem.setVisible(canEditMemo(mc));
            editSubject.setVisible(!editItem.isVisible() && canEditSubject(mc));
            if(mc.getCommentCount() == 0) {
                attachDocs.setVisible(canComment(mc));
                editComment.setVisible(false);
            } else {
                attachDocs.setVisible(false);
                editComment.setVisible(canComment(mc));
            }
            closeMemo.setVisible(mine() && canCloseMemo(mc));
            recallMemo.setVisible(mine() && canRecall(mc));
            forwardMemo.setVisible(mine() && canForward(mc));
            returnMemo.setVisible(mine() && canReturn(mc));
            approveMemo.setVisible(mine() && canApprove(mc));
            rejectMemo.setVisible(mine() && canApprove(mc));
            assignAssistant.setVisible(mine() && mc.getMemo().getStatus() < 4);
            createNew.setVisible(memoType == null && !mc.getMemo().getType().getSpecial());
            return true;
        });
        setOrderBy("Memo DESC,CommentedAt DESC", false);
        setLoadFilter(this::ownerFilter, false);
        if(load) {
            loadMemos();
        }
        addItemDoubleClickListener(e -> {
            MemoComment mc = e.getItem();
            if(mc != null) {
                viewComments(mc);
            }
        });
    }

    private static MemoType mt(String shortName) {
        shortName = StoredObject.toCode(shortName);
        MemoType t = StoredObject.get(MemoType.class, "ShortPrefix='" + shortName + "'");
        if(t == null) {
            throw new SORuntimeException("Memo type " + shortName + " doesn't exist");
        }
        return t;
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
        if(viewComments != null) {
            viewComments.memoComment = null;
        }
        String filter = "CommentedBy=" + who.getId();
        if(!history) {
            filter += " AND T.Status<3";
        }
        if(!mine()) {
            filter += " AND EnteredBy=" + getTransactionManager().getUser().getId();
        }
        if(memoType != null) {
            filter += " AND Memo.Type=" + memoType.getId();
        }
        if(filterMemoId != null) {
            filter += " AND Memo=" + filterMemoId;
        }
        load(filter);
        deselectAll();
    }

    private boolean ownerFilter(MemoComment mc) {
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
                new Button("Create New", "add", e -> newMemo((MemoComment) null)),
                new Button("Load", e -> loadMemos()),
                h,
                new Button("Exit", e -> close())
        );
    }

    @Override
    public void createHeaders() {
        whoButton = new Button(who.getName(), (String) null, e -> selectWho()).asSmall();
        rightClick = new ELabel("Please right-click on the entry to see all options", Application.COLOR_SUCCESS);
        rightClick.setVisible(!isEmpty());
        ButtonLayout b = new ButtonLayout(new ELabel("Of"), whoButton, rightClick);
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
        if(viewComments != null) {
            viewComments.close();
        }
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
        try {
            Memo memo = mt.getMemoClass().getDeclaredConstructor().newInstance();
            if(memoType != null) {
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
            loadMemos();
        } catch(Exception e) {
            warning(e);
        }
    }

    private boolean canCloseMemo(MemoComment mc) {
        if(mc.getCommentCount() != 0 || mc.getStatus() == 5) {
            return false;
        }
        Memo m = mc.getMemo();
        return switch(m.getStatus()) {
            case 0, 4, 5, 6 -> true;
            default -> false;
        };
    }

    private boolean canEditMemo(MemoComment mc) {
        if(mc.getCommentCount() != 0) {
            return false;
        }
        Memo memo = mc.getMemo();
        return memo.getLastComment() == 0 && memo.getStatus() <= 1
                && StoredObject.get(MemoComment.class, "Memo=" + memo.getId() + " AND CommentCount=0")
                .getCommentedById().equals(who.getId());
    }

    private boolean canEditSubject(MemoComment mc) {
        Memo memo = mc.getMemo();
        if(memo.getStatus() >= 3) {
            return false;
        }
        if(mc.getCommentCount() == 0) {
            return true;
        }
        return memo.getInitiatedById().equals(who.getId());
    }

    private void editMemo(Memo memo) {
        memoEditor(memo).editObject(memo, getView());
    }

    private void viewMemo(MemoComment mc) {
        comment = mc.getContent();
        Memo memo = mc.getMemo();
        memoEditor(memo).viewObject(memo, getView());
    }

    @SuppressWarnings("unchecked")
    private <M extends Memo> MemoEditor<M> memoEditor(Memo memo) {
        clearAlerts();
        M m = (M)memo;
        if(memoEditor == null || memoEditor.getObjectClass() != m.getClass()) {
            memoEditor = new MemoEditor<>(m.getClass());
        }
        return (MemoEditor<M>) memoEditor;
    }

    private void selectWho() {
        if(selectWho == null) {
            selectWho = new SelectWho();
            selectWho.suField.setValue(getTransactionManager().getUser());
        }
        selectWho.execute(getView());
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

        final ObjectField<SystemUser> suField = new ObjectField<>(SystemUser.class);

        public SelectSU(String label) {
            super("Select");
            suField.setLabel(label);
            suField.setFilter("Status=0", false);
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

    private class SelectWho extends SelectSU {

        public SelectWho() {
            super("Assist");
        }

        @Override
        protected void setSU(SystemUser su) {
            who = su;
            whoButton.setText(who.getName());
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

        final ObjectField<MemoType> type = new ObjectField<>("Select", MemoType.class);
        private final ELabelField creatingFor = new ELabelField("Creating for");

        public NewMemoType() {
            super("Create New");
            addField(type, creatingFor);
            setRequired(type);
            type.setFilter("NOT Special");
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

    private class MemoEditor<M extends Memo> extends ObjectEditor<M> {

        private final TextArea content;

        MemoEditor(Class<M> objectClass) {
            super(objectClass);
            content =  new TextArea(objectClass == Memo.class ? "Content" : null);
            addField("Reference");
            addField("Content", m -> comment, (m, v) -> comment = (String)v);
            addField("InitiatedBy", this::initBy);
            addField("LastCommentBy", this::lastCommentBy);
            setIncludeFieldChecker(n -> !n.equals("No"));
            addConstructedListener(f -> {
                setFieldReadOnly("Type", "Date", "Status");
                setColumnSpan((Component) getField("Subject"), 2);
                setColumnSpan(content, 2);
                setFieldVisible(TransactionManager.isMultiTenant(), getField("SystemEntity"));
                setFieldReadOnly("SystemEntity");
            });
            addObjectChangedListener(new ObjectChangedListener<>() {
                @Override
                public void saved(M object) {
                    loadMemos();
                }
            });
        }

        @Override
        protected HasValue<?, ?> createField(String fieldName, String label) {
            return switch(fieldName) {
                case "Content" -> content;
                case "InitiatedBy", "LastCommentBy" -> new TextField(label);
                default -> super.createField(fieldName, label);
            };
        }

        private String initBy(Memo memo) {
            SystemUser u = memo.getInitiatedBy();
            return u == null ? who.getName() : u.getName();
        }

        private String lastCommentBy(Memo memo) {
            SystemUser u = memo.getLastCommentBy();
            return u == null ? "None" : u.getName();
        }

        @Override
        protected String getTabName(String fieldName, HasValue<?, ?> field) {
            if(getObjectClass() == Memo.class) {
                return null;
            }
            if(field == content) {
                return "Content";
            }
            return "Details";
        }

        @Override
        protected int getFieldOrder(String fieldName) {
            return switch(fieldName) {
                case "Reference" -> 300;
                case "InitiatedBy" -> 380;
                case "LastCommentBy" -> 390;
                case "Content" -> 10000;
                default -> super.getFieldOrder(fieldName);
            };
        }

        @Override
        protected void saveObject(Transaction t, M object) throws Exception {
            object.save(t, comment, who);
        }

        @Override
        public void setObject(M object, boolean load) {
            super.setObject(object, load);
            setCaption(object == null ? "Memo" : object.getReference());
        }
    }

    private boolean canComment(MemoComment mc) {
        if(mc.getCommentCount() != mc.getMemo().getLastComment()) {
            return false;
        }
        return switch(mc.getStatus()) {
            case 0, 1, 2 -> true;
            default -> false;
        };
    }

    private Set<SystemUser> approvers(MemoComment mc) {
        Set<SystemUser> set = approvers.get(mc.getMemo().getTypeId());
        if(set == null) {
            set = mc.getMemo().getType().listApprovers(getTransactionManager().getEntity());
            approvers.put(mc.getMemo().getTypeId(), set);
        }
        return set;
    }

    private Set<SystemUser> commenters(MemoComment mc) {
        Set<SystemUser> set = commenters.get(mc.getMemo().getTypeId());
        if(set == null) {
            set = mc.getMemo().getType().listCommenters(getTransactionManager().getEntity());
            set.add(getTransactionManager().getUser());
            set.add(mc.getFirst().getCommentedBy());
            commenters.put(mc.getMemo().getTypeId(), set);
        }
        return set;
    }

    private boolean canApprove(MemoComment mc) {
        if(mc.getCommentCount() == 0 || mc.getCommentCount() != mc.getMemo().getLastComment()
                || mc.getMemo().getStatus() >= 4 || mc.getFirst().getCommentedById().equals(who.getId())) {
            return false;
        }
        Set<SystemUser> set = approvers(mc);
        return !set.isEmpty() && set.contains(getTransactionManager().getUser());
    }

    private boolean canForward(MemoComment mc) {
        if(mc.getCommentCount() != mc.getMemo().getLastComment() || mc.getStatus() >= 3) {
            return false;
        }
        return commenters(mc).size() > 1;
    }

    private boolean canReturn(MemoComment mc) {
        return mc.getCommentCount() > 0 && switch(mc.getStatus()) {
            case 0, 1 -> true;
            default -> false;
        };
    }

    private boolean canRecall(MemoComment mc) {
        int status = mc.getStatus();
        if(status == 3 || status == 4 || mc.getMemo().getStatus() >= 4) {
            return false;
        }
        MemoComment next = mc.getNext();
        return next != null && next.getStatus() == 0;
    }

    private void recallMemo(MemoComment mc) {
        if(transact(mc::recallMemo)) {
            loadMemos();
        }
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
        private final ObjectField<SystemUser> suField = new ObjectField<>("Forward to", SystemUser.class,
                ObjectField.Type.CHOICE);
        private SystemUser su;
        private final Button comments =
                new Button("View Comments", VaadinIcon.COMMENTS_O, e -> viewComments(getObject()));

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
        public void validateData() throws Exception {
            clearAlerts();
            super.validateData();
            if(suField.isVisible() && su == null) {
                throw new SOException("Please select 'Forward to'");
            }
        }

        @Override
        protected void addExtraEditingButtons() {
            buttonPanel.add(comments);
        }

        @Override
        protected void saveObject(Transaction t, MemoComment object) throws Exception {
            switch(action) {
                case 1 -> object.returnMemo(t, object.getComment());
                case 2 -> object.forwardMemo(t, object.getComment(), su);
                case 3 -> object.approveMemo(t, object.getComment(), suField.isVisible() ? su : null);
                case 4 -> object.rejectMemo(t, object.getComment());
                default -> object.commentMemo(t, object.getComment());
            }
        }

        @Override
        public void setObject(MemoComment object, boolean load) {
            super.setObject(object, load);
            if(object != null) {
                ((TextArea)getField("Comment")).setLabel(object.getCommentCount() == 0 ? "Content" : "Comment");
                setCaption(object.getMemo().getReference());
            }
        }

        @Override
        protected void execute(View parent, boolean doNotLock) {
            MemoComment mc = getObject();
            if(action == 3 && mc.getComment().isBlank()) {
                mc.setComment("Approved");
            }
            if(action == 2) {
                setFieldVisible(suField);
                Id me = getTransactionManager().getUser().getId();
                suField.load(ObjectIterator.create(commenters(mc)).filter(u -> !u.getId().equals(me)));
            } else if(action == 3) {
                int a = mc.getMemo().getApprovalsRequired();
                if(a > 1) {
                    int approvals = StoredObject.count(MemoComment.class,
                            "Memo=" + mc.getMemoId() + " AND Status=3") + 1;
                    if(approvals >= a) {
                        a = 1;
                    }
                }
                setFieldVisible(a > 1, suField);
                if(a > 1) {
                    Id me = getTransactionManager().getUser().getId();
                    suField.load(ObjectIterator.create(approvers(mc)).filter(u -> !u.getId().equals(me)));
                }
            } else {
                setFieldHidden(suField);
            }
            super.execute(parent, doNotLock);
            save.setText(switch(action) {
                case 1 -> "Return";
                case 2 -> "Forward";
                case 3 -> "Approve";
                case 4 -> "Reject";
                default -> "Save";
            });
        }
    }

    private void viewComments(MemoComment mc) {
        if(viewComments == null) {
            viewComments = new ViewComments();
        }
        viewComments.view(mc);
    }

    private class ViewComments extends View implements  CloseableView {

        private final VerticalLayout layout = new VerticalLayout();
        private final WindowDecorator header;
        private MemoComment memoComment;

        private ViewComments() {
            header = new WindowDecorator(this);
            setComponent(new ContentWithHeader(header, layout));
        }

        void view(MemoComment mc) {
            setCaption("Comments - " + mc.getReference());
            layout.removeAll();
            layout.add(new Box(new ELabel("Subject: " + mc.getMemo().getSubject(), Application.COLOR_SUCCESS)));
            layout.add(new Hr());
            StoredObject.list(MemoComment.class, "Memo=" + mc.getMemoId(), "CommentCount DESC")
                    .forEach(this::addComment);
            execute();
        }

        @Override
        public void setCaption(String caption) {
            super.setCaption(caption);
            if(header != null) {
                header.setCaption(caption);
            }
        }

        private void addComment(MemoComment mc) {
            if(mc == memoComment) {
                execute();
                return;
            }
            memoComment = mc;
            String c = mc.getComment();
            if(c.isBlank()) {
                return;
            }
            String action;
            if(mc.getCommentCount() == 0) {
                action = "initiated";
            } else {
                action = switch(mc.getStatus()) {
                    case 3 -> "approved";
                    case 4 -> "rejected";
                    default -> "commented";
                };
            }
            layout.add(new Badge(mc.getCommentedBy().getName() + " " + action + " at " + getCommentedAt(mc)));
            layout.add(new ELabel(c));
            ButtonLayout attachments = new ButtonLayout();
            AtomicBoolean any = new AtomicBoolean(false);
            mc.listLinks(MemoAttachment.class).forEach(a -> {
                attachments.add(new Button(a.getName(), VaadinIcon.PAPERCLIP, e -> Application.get().view(a)).asSmall());
                any.set(true);
            });
            if(any.get()) {
                layout.add(attachments);
            }
            layout.add(new Hr());
        }
    }
}
