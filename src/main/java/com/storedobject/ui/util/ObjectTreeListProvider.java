package com.storedobject.ui.util;

import com.storedobject.core.StoredObject;
import com.storedobject.ui.ObjectTreeBuilder;
import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.data.provider.hierarchy.AbstractBackEndHierarchicalDataProvider;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalQuery;
import com.vaadin.flow.shared.Registration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ObjectTreeListProvider<T extends StoredObject> extends AbstractBackEndHierarchicalDataProvider<T, String> {

    private static final String[] EMPTY = new String[] {};
    private final List<T> roots;
    private ItemLabelGenerator<T> itemLabelGenerator = String::valueOf;
    private T currentParent;
    private List<T> cache = new ArrayList<>();
    private List<T> matched;
    private String[] matches = EMPTY;
    private final ObjectTreeBuilder objectTreeBuilder;
    private final List<ObjectDataLoadedListener> dataLoadedListeners = new ArrayList<>();

    public ObjectTreeListProvider(List<T> roots, ObjectTreeBuilder objectTreeBuilder) {
        this.roots = roots;
        matched = roots;
        this.objectTreeBuilder = objectTreeBuilder;
    }

    public ObjectTreeListProvider(Stream<T> roots, ObjectTreeBuilder objectTreeBuilder) {
        this(roots.collect(Collectors.toList()), objectTreeBuilder);
    }

    public Registration addObjectDataLoadedListener(ObjectDataLoadedListener listener) {
        dataLoadedListeners.add(listener);
        return () -> dataLoadedListeners.remove(listener);
    }

    public void setItemLabelGenerator(ItemLabelGenerator<T> itemLabelGenerator) {
        this.itemLabelGenerator = itemLabelGenerator;
        refreshAll();
    }

    public ObjectTreeBuilder getTreeBuilder() {
        return objectTreeBuilder;
    }

    public List<T> listRoots() {
        return Collections.unmodifiableList(roots);
    }

    @Override
    protected Stream<T> fetchChildrenFromBackEnd(HierarchicalQuery<T, String> query) {
        applyMatch(query.getFilter().orElse(null), query.getParent());
        int end = query.getLimit();
        if(end < (Integer.MAX_VALUE - query.getOffset())) {
            end += query.getOffset();
        }
        if(end > matched.size()) {
            end = matched.size();
        }
        if(query.getOffset() == 0 && end == matched.size()) {
            return matched.stream();
        }
        return matched.subList(query.getOffset(), end).stream();
    }

    @Override
    public int getChildCount(HierarchicalQuery<T, String> query) {
        applyMatch(query.getFilter().orElse(null), query.getParent());
        if(query.getOffset() >= matched.size()) {
            return 0;
        }
        int end = query.getLimit();
        if(end < (Integer.MAX_VALUE - query.getOffset())) {
            end += query.getOffset();
        }
        if(end > matched.size()) {
            end = matched.size();
        }
        return end - query.getOffset();
    }

    @Override
    public boolean hasChildren(T parent) {
        if(parent == null) {
            return roots.size() > 0;
        }
        if(!parent.equals(currentParent)) {
            load(parent);
        }
        return cache.size() > 0;
    }

    private void load(T parent) {
        if(parent == null) {
            currentParent = null;
            cache = roots;
            reapplyMatch(null);
            dataLoadedListeners.forEach(ObjectDataLoadedListener::dataLoaded);
            return;
        }
        if(cache == roots) {
            cache = new ArrayList<>();
        } else {
            cache.clear();
        }
        objectTreeBuilder.listChildren(parent).collectAll(cache);
        currentParent = parent;
        reapplyMatch(parent);
    }

    private void reapplyMatch(T parent) {
        if(skipMatching(parent)) {
            matched = cache;
            return;
        }
        if(matched == cache) {
            matched = new ArrayList<>();
        } else {
            matched.clear();
        }
        cache.stream().filter(this::match).forEach(matched::add);
    }

    private void applyMatch(String match, T parent) {
        if(match != null) {
            match = match.toUpperCase();
        }
        String[] matches = match == null ? EMPTY : match.split("\\s+");
        if(!Objects.equals(parent, currentParent)) {
            this.matches = matches;
            load(parent);
            return;
        }
        if(same(matches) && matched != null) {
            return;
        }
        this.matches = matches;
        reapplyMatch(parent);
    }

    private boolean match(T o) {
        String item = itemLabelGenerator.apply(o);
        if(item == null) {
            return false;
        }
        item = item.toUpperCase();
        for(String m: matches) {
            if(!m.isEmpty() && !item.contains(m)) {
                return false;
            }
        }
        return true;
    }

    private boolean same(String[] matches) {
        for(String m: matches) {
            if(m.isEmpty()) {
                continue;
            }
            if(notContain(m, this.matches)) {
                return false;
            }
        }
        for(String m: this.matches) {
            if(m.isEmpty()) {
                continue;
            }
            if(notContain(m, matches)) {
                return false;
            }
        }
        return true;
    }

    private boolean notContain(String m, String[] ms) {
        for(String s: ms) {
            if(s.equals(m)) {
                return false;
            }
        }
        return true;
    }

    private boolean skipMatching(T parent) {
        if(!Objects.equals(parent, currentParent)) {
            return false;
        }
        for(String m: matches) {
            if(!m.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public T getItem(int index) {
        return roots == null || index >= roots.size() || index < 0 ? null : roots.get(index);
    }

    public int getObjectCount() {
        return roots == null ? 0 : roots.size();
    }
}
