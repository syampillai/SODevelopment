package com.storedobject.ui;

import com.storedobject.common.SORuntimeException;
import com.storedobject.ui.util.ChildVisitor;
import com.storedobject.vaadin.*;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.icon.VaadinIcon;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class TreeSearchField<P extends T, T> extends TextField {

    private final ChildVisitor<P, T> tree;
    private int current = -1;
    private String searchText = "";
    private List<Match> matches;
    private final View view;
    private CountDownLatch searching;
    private boolean continueSearch;
    private final ApplicationEnvironment environment = ApplicationEnvironment.get();

    public TreeSearchField(ChildVisitor<P, T> tree) {
        this.tree = tree;
        if(tree instanceof DataTreeGrid<?> g) {
            view = g.getView(true);
        } else {
            throw new SORuntimeException();
        }
        setPlaceholder("Text to search");
        ImageButton ib = new ImageButton("Search backward", VaadinIcon.ANGLE_DOUBLE_UP, e -> show(true));
        ib.getElement().setAttribute("slot", "prefix");
        getElement().appendChild(ib.getElement());
        ib = new ImageButton("Search forward", VaadinIcon.ANGLE_DOUBLE_DOWN, e -> show(false));
        ib.getElement().setAttribute("slot", "prefix");
        getElement().appendChild(ib.getElement());
        addKeyPressListener(Key.ENTER, e -> doSearch());
        addValueChangeListener(e -> doSearch());
    }

    private void show(boolean backward) {
        view.clearAlerts();
        if(matches.isEmpty()) {
            view.message("No matches found");
            return;
        }
        if(backward) {
            --current;
        } else {
            ++current;
        }
        boolean next = true;
        while(current >= matches.size() && searching != null && searching.getCount() > 0) {
            if(next) {
                next = false;
                view.message("Next match...");
            }
            try {
                if(searching.await(1, TimeUnit.SECONDS)) {
                    break;
                }
            } catch (InterruptedException e) {
                break;
            }
        }
        if(current < 0 || current >= matches.size()) {
            view.message("No more matches");
            if(backward) {
                ++current;
            } else {
                --current;
            }
            return;
        }
        if(!(tree instanceof DataTreeGrid<?>)) {
            return;
        }
        @SuppressWarnings("unchecked") DataTreeGrid<T> grid = (DataTreeGrid<T>) tree;
        Match match = matches.get(current);
        T selected = match.item;
        List<P> roots = tree.listRoots();
        //noinspection SuspiciousMethodCalls
        if(!roots.contains(selected)) {
            List<T> parentTree = new ArrayList<>();
            //noinspection unchecked
            family(selected, parentTree, (List<T>) roots);
            for(T p: parentTree) {
                //noinspection unchecked
                grid.expand(p);
            }
        }
        grid.select(selected);
        ((DataTreeGrid<?>) tree).scrollToIndex(match.path);
        focus();
        view.message("Match: " + (current + 1) +
                (searching != null && searching.getCount() > 0 ? "" : ("/" + matches.size())));
    }

    private boolean contains(Object row) {
        try {
            return environment.toString(row).toLowerCase().contains(searchText);
        } catch(Throwable ignored) {
        }
        return false;
    }

    private List<T> children(T parent) {
        List<T> children = new ArrayList<>();
        tree.visitChildren(parent, children::add, false);
        return children;
    }

    private boolean family(T selected, List<T> parentTree, List<T> from) {
        List<T> children;
        int count;
        for(T p: from) {
            count = parentTree.size();
            parentTree.add(p);
            children = children(p);
            if(children.contains(selected)) {
                return true;
            }
            if(family(selected, parentTree, children)) {
                return true;
            }
            while(parentTree.size() > count) {
                parentTree.removeLast();
            }
        }
        return false;
    }

    private void doSearch() {
        Thread.startVirtualThread(this::doSearchInt);
    }

    private void doSearchInt() {
        if(searching != null && searching.getCount() > 0) {
            continueSearch = false;
            try {
                searching.await();
            } catch (InterruptedException e) {
                return;
            }
        }
        searchText = getValue().strip().toLowerCase();
        if(searchText.isEmpty()) {
            return;
        }
        view.getApplication().access(() -> view.message("Searching..."));
        searching = new CountDownLatch(1);
        matches = new ArrayList<>();
        current = -1;
        continueSearch = true;
        tree.visitAll((item, path) -> {
            if(contains(item)) {
                matches.add(new Match(item, path));
                if(current == -1 && continueSearch) {
                    view.getApplication().access(() -> show(false));
                }
            }
        }, p -> continueSearch);
        searching.countDown();
        if(matches.isEmpty()) {
            view.getApplication().access(() -> {
                view.clearAlerts();
                view.message("No matches found");
            });
        }
    }

    private class Match {

        private final T item;
        private final int[] path;

        private Match(T item, int[] path) {
            this.item = item;
            this.path = path;
        }
    }
}
