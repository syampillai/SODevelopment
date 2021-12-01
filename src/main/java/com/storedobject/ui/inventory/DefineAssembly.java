package com.storedobject.ui.inventory;

import com.storedobject.common.StringList;
import com.storedobject.core.*;
import com.storedobject.pdf.PDFCell;
import com.storedobject.pdf.PDFFont;
import com.storedobject.pdf.PDFReport;
import com.storedobject.pdf.PDFTable;
import com.storedobject.ui.Application;
import com.storedobject.ui.*;
import com.storedobject.vaadin.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.data.provider.hierarchy.AbstractHierarchicalDataProvider;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalQuery;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class DefineAssembly<T extends InventoryItemType, C extends InventoryItemType> extends DataTreeGrid<InventoryAssembly> implements Transactional {

    private static final StringList COLUMNS = StringList.create("ItemType", "Position", "Quantity", "Accessory", "Optional");
    private final ButtonLayout buttonLayout = new ButtonLayout();
    private final ObjectSearchBrowser<T> searcher;
    protected Button add = new Button("Add", this);
    protected Button edit = new Button("Edit", this);
    protected ConfirmButton copy = new ConfirmButton("Copy from Another", "copy", e -> doSearchToCopy());
    protected ConfirmButton delete = new ConfirmButton("Delete", this);
    protected Button selectRoot = new Button("Select Top-Level Assembly", VaadinIcon.SPLIT, e -> doSearchForTopLevel());
    private InventoryAssembly root, selected;
    private final Class<C> componentTypeClass;
    private final Map <Id, ArrayList<InventoryAssembly>> assemblies = new HashMap<>();
    private Editor editor;
    private final TreeData treeData;

    public DefineAssembly(Class<T> itemTypeClass) {
        this(itemTypeClass, null);
    }

    public DefineAssembly(Class<T> itemTypeClass, Class<C> componentTypeClass) {
        super(InventoryAssembly.class, COLUMNS);
        //noinspection unchecked
        this.componentTypeClass = componentTypeClass == null ? (Class<C>)itemTypeClass : componentTypeClass;
        searcher = ObjectSearchBrowser.create(itemTypeClass, "N", null);
        setDataProvider(treeData = new TreeData());
        setHeightFull();
        Application a = Application.get();
        if(a != null) {
            String caption = a.getLogicTitle(null);
            if(caption != null) {
                setCaption(caption);
            }
        }
        addConstructedListener(o -> con());
    }

    public DefineAssembly(T itemType) {
        //noinspection unchecked
        this((Class<T>) itemType.getClass(), null);
        selectRoot = null;
        setTopLevelItem(itemType);
    }

    @SuppressWarnings("unchecked")
    public DefineAssembly(String itemTypeClass) {
        this((Class<T>) ParameterParser.itemTypeClass(itemTypeClass),
                (Class<C>) ParameterParser.itemTypeClass(1, itemTypeClass));
    }

    private void con() {
        getColumn("Optional").setAutoWidth(false).setFlexGrow(5);
        getColumn("Accessory").setAutoWidth(false).setFlexGrow(5);
        getColumn("Quantity").setAutoWidth(false).setFlexGrow(13);
        getColumn("Position").setAutoWidth(false).setFlexGrow(13);
        getColumn("ItemType").setAutoWidth(false).setFlexGrow(64);
    }

    public String getAccessory(InventoryAssembly ia) {
        return ia == root || !ia.getAccessory() ? "" : "Yes";
    }

    public String getOptional(InventoryAssembly ia) {
        return ia == root || !ia.getOptional() ? "" : "Yes";
    }

    @Override
    public boolean isColumnSortable(String columnName) {
        return false;
    }

    @Override
    public String getColumnCaption(String columnName) {
        if("Position".equals(columnName)) {
            return "Position/Label";
        }
        return super.getColumnCaption(columnName);
    }

    @Override
    public Component createHeader() {
        if(delete != null) {
            delete.setPreconfirm(() -> {
                InventoryAssembly ia = getSelected();
                return ia != null && ia != root;
            });
        }
        copy.setVisible(false);
        Button exit = new Button("Exit", e -> close());
        addButtons(add, edit, delete, selectRoot, copy,
                new Button("Print", e -> new PrintAssembly()),
                exit);
        return buttonLayout;
    }

    private void addButtons(Button... buttons) {
        for(Button b: buttons) {
            if (b != null) {
                buttonLayout.add(b);
            }
        }
    }

    public void setTopLevelItem(T itemType) {
        assemblies.clear();
        if(itemType == null) {
            root = null;
            copy.setVisible(false);
        } else {
            root = new InventoryAssembly();
            root.setPosition("[Assembly]");
            root.setItemType(itemType);
            root.setQuantity(Count.ONE);
            root.makeVirtual();
        }
        refresh();
        copy.setVisible(root != null && !treeData.hasChildren(root));
    }

    private ArrayList<InventoryAssembly> subassemblies(InventoryAssembly assembly) {
        ArrayList<InventoryAssembly> subassemblies = assemblies.get(assembly.getItemTypeId());
        if(subassemblies == null) {
            subassemblies = new ArrayList<>();
            if(assembly.getItemType().isSerialized()) {
                assembly.listImmediateAssemblies().collectAll(subassemblies);
            }
            assemblies.put(assembly.getItemTypeId(), subassemblies);
        }
        return subassemblies;
    }

    @Override
    public void clicked(Component c) {
        if(root == null) {
            warning("Please set the top-level assembly first!");
            return;
        }
        if(c == copy) {
            return;
        }
        selected = getSelected();
        if(selected == null) {
            warning("Nothing selected!");
            return;
        }
        if(c == add) {
            if(!selected.getItemType().isSerialized()) {
                warning("Further items can not be added under '" + selected.getItemType() + "'");
                return;
            }
            editor().addObject(getView());
            return;
        }
        if(c == delete || c == edit) {
            if(selected == root) {
                warning("The top-level assembly item can not be " + (c == edit ? "edit" : "delet") + "ed!");
                return;
            }
            if(c == delete) {
                if(transact(t -> selected.delete(t))) {
                    refresh();
                }
            } else {
                editor().editObject(selected, getView());
            }
            return;
        }
        super.clicked(c);
    }

    private class TreeData extends AbstractHierarchicalDataProvider<InventoryAssembly, String> {

        @Override
        public int getChildCount(HierarchicalQuery<InventoryAssembly, String> query) {
            InventoryAssembly ia = query.getParent();
            if(ia == null) {
                return root == null ? 0 : 1;
            }
            return Utility.size(subassemblies(ia), query.getOffset(), query.getOffset() + query.getLimit());
        }

        @Override
        public Stream<InventoryAssembly> fetchChildren(HierarchicalQuery<InventoryAssembly, String> query) {
            InventoryAssembly ia = query.getParent();
            if(ia == null) {
                return Stream.ofNullable(root);
            }
            return Utility.stream(subassemblies(ia), query.getOffset(), query.getOffset() + query.getLimit());
        }

        @Override
        public boolean hasChildren(InventoryAssembly inventoryAssembly) {
            if(inventoryAssembly == null) {
                return root != null;
            }
            return !subassemblies(inventoryAssembly).isEmpty();
        }

        @Override
        public boolean isInMemory() {
            return true;
        }

        @Override
        public void refreshAll() {
            assemblies.clear();
            super.refreshAll();
        }
    }

    private Editor editor() {
        if(editor == null) {
            editor = new Editor();
            editor.addObjectChangedListener(new ObjectChangedListener<>() {

                @Override
                public void inserted(InventoryAssembly object) {
                    assemblies.remove(selected.getItemTypeId());
                    refresh();
                }

                @Override
                public void updated(InventoryAssembly object) {
                    Id parent = object.getParentItemTypeId();
                    if(parent.equals(root.getItemTypeId())) {
                        refresh(object, false);
                    } else {
                        refresh();
                    }
                }

                @Override
                public void deleted(InventoryAssembly object) {
                    refresh();
                }
            });
        }
        return editor;
    }

    private class Editor extends ObjectEditor<InventoryAssembly> {

        private HasValue<?, Quantity> quantityField;
        private ObjectField<?> itemTypeField;

        private Editor() {
            super(InventoryAssembly.class, EditorAction.NEW | EditorAction.EDIT, "Assembly");
            addConstructedListener(o -> fConstructed());
        }

        private void fConstructed() {
            setFieldReadOnly("ParentItemType");
            trackValueChange(itemTypeField);
        }

        @Override
        public void valueChanged(ChangedValues changedValues) {
            if(changedValues.getChanged() == itemTypeField) {
                InventoryItemType iit = (InventoryItemType)itemTypeField.getObject();
                if(iit != null) {
                    if(iit.isSerialized()) {
                        quantityField.setValue(Count.ONE);
                        quantityField.setReadOnly(true);
                        setFieldReadOnly(quantityField);
                    } else {
                        quantityField.setValue(iit.getUnitOfMeasurement());
                        quantityField.setReadOnly(false);
                        setFieldEditable(quantityField);
                    }
                }
            }
        }

        @Override
        protected HasValue<?, ?> createField(String fieldName, String label) {
            if("ItemType".equals(fieldName)) {
                itemTypeField = new ObjectField<>(label, componentTypeClass, true);
                return itemTypeField;
            }
            return super.createField(fieldName, label);
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void customizeField(String fieldName, HasValue<?, ?> field) {
            if("Quantity".equals(fieldName)) {
                quantityField = (HasValue<?, Quantity>) field;
                return;
            }
            super.customizeField(fieldName, field);
        }

        @Override
        protected void execute(View parent, boolean doNotLock) {
            super.execute(parent, false);
        }

        @Override
        protected InventoryAssembly createObjectInstance() {
            InventoryAssembly ia = new InventoryAssembly();
            ia.setParentItemType(selected.getItemTypeId());
            ia.setQuantity(Count.ONE);
            return ia;
        }
    }

    private void doSearchForTopLevel() {
        clearAlerts();
        searcher.setCaption("Top-Level Assembly");
        searcher.setObjectConsumer(this::setTopLevelItem);
        searcher.search();
    }

    private void doSearchToCopy() {
        clearAlerts();
        searcher.setCaption("Copy from");
        searcher.setObjectConsumer(this::copyFrom);
        searcher.search();
    }

    private void copyFrom(InventoryItemType itemType) {
        if(itemType.getId().equals(root.getItemTypeId())) {
            warning("You selected the same");
            return;
        }
        new Copy(itemType).execute(this.getView());
    }

    private class Copy extends DataForm {

        private final InventoryItemType itemType;

        public Copy(InventoryItemType itemType) {
            super("Copy Assembly Definitions");
            this.itemType = itemType;
            ELabelField e;
            e = new ELabelField("Warning");
            e.append("This operation is irreversible, please proceed with it only if you are sure about it!",
                    "red").update();
            addField(e);
            e = new ELabelField("Copy from");
            e.append(itemType.toDisplay()).update();
            addField(e);
            e = new ELabelField("Copy to");
            e.append(root.getItemType()).update();
            addField(e);
        }

        @Override
        protected void buildButtons() {
            super.buildButtons();
            ok.setText("Copy");
        }

        @Override
        protected boolean process() {
            Collection<InventoryAssembly> assemblies = itemType.listImmediateAssemblies().collectAll();
            if(!transact(t -> {
                InventoryAssembly ia;
                for(InventoryAssembly copy: assemblies) {
                    ia = new InventoryAssembly();
                    ia.setParentItemType(root.getItemTypeId());
                    ia.setItemType(copy.getItemTypeId());
                    ia.setQuantity(copy.getQuantity());
                    ia.setPosition(copy.getPosition());
                    ia.setAccessory(copy.getAccessory());
                    ia.setOptional(copy.getOptional());
                    ia.setDisplayOrder(copy.getDisplayOrder());
                    ia.save(t);
                }
            })) {
                return false;
            }
            //noinspection unchecked
            setTopLevelItem((T)root.getItemType());
            return true;
        }
    }

    private class PrintAssembly extends PDFReport {

        private PDFTable table;

        public PrintAssembly() {
            super((com.storedobject.ui.Application) com.storedobject.vaadin.Application.get());
            if(root == null) {
                return;
            }
            setTitleText(
                    new Text("Assembly Definition", 16, PDFFont.BOLD).
                            newLine().
                            append(root.getItemType().toDisplay(), 14, PDFFont.BOLD)
            );
            execute();
        }

        @Override
        public void generateContent() throws Exception {
            table = createTable(3, 3, 3, 3, 3, 3, 3, 3, 60, 20, 20, 20, 20);
            boolean first = true;
            PDFCell cell;
            for(String h: COLUMNS) {
                cell = createCenteredCell(createTitleText(StringUtility.makeLabel(h)));
                if(first) {
                    first = false;
                    cell.setColumnSpan(9);
                }
                table.addCell(cell);
            }
            table.setHeaderRows(1);
            print(0, root, null);
            add(table);
        }

        private void print(int level, InventoryAssembly ia, String pos) {
            for(InventoryAssembly p: subassemblies(ia)) {
                print(level + 1, p, print(p, level, pos));
            }
        }

        private String print(InventoryAssembly ia, int level, String parentPos) {
            PDFCell cell;
            for(int i = 0; i < Math.min(8, level); i++) {
                cell = createCenteredCell("-", c -> { c.setBorder(0); return c; });
                table.addCell(cell);
            }
            StringBuilder name = new StringBuilder(ia.getItemType().toDisplay());
            while(level > 8) {
                name.insert(0, "- ");
                --level;
            }
            cell = createCell(name);
            cell.setColumnSpan(9 - level);
            table.addCell(cell);
            String p = ia.getPosition();
            if(parentPos != null && "-".equals(p)) {
                p = parentPos;
            }
            cCell(p);
            tCell(ia.getQuantity());
            cCell(getAccessory(ia));
            cCell(getOptional(ia));
            return p;
        }

        private void tCell(Object o) {
            table.addCell(createCell(o));
        }

        private void cCell(Object o) {
            table.addCell(createCenteredCell(o));
        }

        @Override
        public int getPageOrientation() {
            return ORIENTATION_LANDSCAPE;
        }
    }
}