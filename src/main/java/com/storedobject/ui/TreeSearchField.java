package com.storedobject.ui;

import com.storedobject.common.SORuntimeException;
import com.storedobject.ui.util.ChildVisitor;
import com.storedobject.vaadin.*;
import com.storedobject.vaadin.DataTreeGrid;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.icon.VaadinIcon;

import java.util.ArrayList;
import java.util.List;

public class TreeSearchField<P extends T, T> extends TextField {

    private final ChildVisitor<P, T> tree;
    private int current = -1;
    private String text = "";
    private final List<T> list = new ArrayList<>();
    private final View view;

    public TreeSearchField(ChildVisitor<P, T> tree) {
        this.tree = tree;
        if(tree instanceof DataTreeGrid<?> g) {
            view = g.getView(true);
        } else {
            throw new SORuntimeException();
        }
        setPlaceholder("Text to search");
        addToPrefix(new ImageButton("Search backward", VaadinIcon.ANGLE_DOUBLE_UP, e -> search(true)));
        addToSuffix(new ImageButton("Search forward", VaadinIcon.ANGLE_DOUBLE_DOWN, e -> search(false)));
        addKeyPressListener(Key.ENTER, e -> search(false));
        addValueChangeListener(e -> search(false));
    }

    private void search(boolean backward) {
        String s = getValue().trim().toLowerCase();
        if(s.isEmpty()) {
            return;
        }
        if(s.equals(text)) {
            continueSearch(backward);
            return;
        }
        text = s;
        current = -1;
        list.clear();
        tree.listRoots().forEach(r -> {
            if(contains(r)) {
                list.add(r);
            }
            tree.visitChildren(r, t -> {
                if(contains(t)) {
                    list.add(t);
                }
            }, true);
        });
        continueSearch(false);
    }

    private void continueSearch(boolean backward) {
        if(list.isEmpty()) {
            view.clearAlerts();
            view.message("No matches found");
            return;
        }
        if(backward) {
            --current;
        } else {
            ++current;
        }
        if(current < 0 || current >= list.size()) {
            view.clearAlerts();
            view.message("No more matches");
            if(backward) {
                ++current;
            } else {
                --current;
            }
            return;
        }
        T selected = list.get(current);
        /*
        List<P> roots = tree.listRoots();
        //noinspection SuspiciousMethodCalls
        if(!roots.contains(selected)) {
            List<T> parents = new ArrayList<>();
            List<T> children;
            for(P root: roots) {
                children = children(root);
                if(children.contains(selected)) {
                    parents.add(root);
                    break;
                }

            }
        }
         */
        //noinspection unchecked
        ((DataTreeGrid<T>)tree).select(selected);
        focus();
    }

    private boolean contains(Object row) {
        try {
            return ApplicationEnvironment.get().toString(row).toLowerCase().contains(text);
        } catch(Throwable ignored) {
        }
        return false;
    }

    private List<T> children(T parent) {
        List<T> children = new ArrayList<>();
        tree.visitChildren(parent, children::add, false);
        return children;
    }
}
