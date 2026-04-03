package com.storedobject.ui.report;

import com.storedobject.common.StringList;
import com.storedobject.core.StringUtility;
import com.storedobject.office.ODT;
import com.storedobject.vaadin.DataForm;
import com.storedobject.vaadin.DataTreeGrid;
import com.vaadin.flow.data.provider.hierarchy.TreeData;
import com.vaadin.flow.data.selection.MultiSelectionEvent;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class SectionSelector extends DataForm {

    private final ODT<?> odt;
    private final DataTreeGrid<ODT.SectionName> tree;
    private final Set<ODT.SectionName> fixedSelections = new HashSet<>();

    public SectionSelector(ODT<?> odt) {
        this("Select Sections", odt, null);
    }

    public SectionSelector(String caption, ODT<?> odt, Function<ODT.SectionName, String> nameFunction) {
        super(caption);
        this.odt = odt;
        List<ODT.SectionName> roots = odt.getSectionNames();
        if(roots.isEmpty()) {
            tree = null;
        } else {
            if(nameFunction == null) {
                nameFunction = sn -> StringUtility.makeLabel(sn.getName());
            }
            odt.setSectionCustomizer((f, s) -> check(s));
            tree = new DataTreeGrid<>(ODT.SectionName.class, StringList.create(new String[]{"SectionName"}));
            Function<ODT.SectionName, String> finalNameFunction = nameFunction;
            tree.createHierarchyColumn("SectionName", finalNameFunction::apply);
            tree.setTreeData(new SectionData(roots));
            tree.setSelectionMode(DataTreeGrid.SelectionMode.MULTI);
            tree.setWidthFull();
            tree.expandRecursively(roots.stream(), 5);
            tree.setMinHeight("35vh");
            add(tree);
            tree.addSelectionListener(e -> {
                if (e.isFromClient() && e instanceof MultiSelectionEvent<?, ?>) {
                    MultiSelectionEvent<?, ODT.SectionName> event = (MultiSelectionEvent<?, ODT.SectionName>) e;
                    Set<ODT.SectionName> oldSelection = event.getOldSelection();
                    Set<ODT.SectionName> newSelection = event.getAllSelectedItems();
                    Set<ODT.SectionName> removedItems = new HashSet<>(oldSelection);
                    removedItems.removeAll(newSelection);
                    for (ODT.SectionName s : removedItems) {
                        if (fixedSelections.contains(s)) {
                            select(s);
                        }
                    }
                }
            });
            selectFixed(roots.getFirst());
        }
    }

    @Override
    public int getMinimumContentWidth() {
        return 40;
    }

    public final ODT<?> getODT() {
        return odt;
    }

    @Override
    protected boolean process() {
        close();
        odt.execute();
        return true;
    }

    private void check(ODT.Section section) {
        String n = section.getName();
        if(tree.getSelectedItems().stream().noneMatch(sn -> sn.getName().equals(n))) {
            section.remove();
        }
    }

    public void selectFixed(ODT.SectionName sectionName) {
        tree.select(sectionName);
        fixedSelections.add(sectionName);
    }

    public void select(ODT.SectionName sectionName) {
        tree.select(sectionName);
    }

    public void deselect(ODT.SectionName sectionName) {
        tree.deselect(sectionName);
    }

    private static class SectionData extends TreeData<ODT.SectionName> {

        public SectionData(List<ODT.SectionName> roots) {
            addItems(roots, ODT.SectionName::getChildren);
        }
    }
}
