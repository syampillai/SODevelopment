package com.storedobject.ui.util;

import com.storedobject.common.LogicalOperator;
import com.storedobject.core.ClassAttribute;
import com.storedobject.core.ObjectToString;
import com.storedobject.core.StoredObject;
import com.storedobject.core.StringUtility;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public class ViewFilter<T> {

    private static final String[] EMPTY = new String[] {};
    private String[] matches = EMPTY;
    private BiFunction<T, String[], Boolean> matcher;
    private Function<T, String> objectConverter;
    private final Function<T, String> defaultObjectConverter;
    private LogicalOperator filterLogic = LogicalOperator.OR;

    public <O extends StoredObject> ViewFilter(Class<T> forClass) {
        if(StoredObject.class.isAssignableFrom(forClass)) {
            @SuppressWarnings("unchecked") Class<O> objectClass = (Class<O>) forClass;
            //noinspection unchecked
            defaultObjectConverter = (Function<T, String>)
                    ObjectToString.create(objectClass, ClassAttribute.get(objectClass).getAttributes());
        } else {
            defaultObjectConverter = StringUtility::toString;
        }
    }

    public boolean setObjectConverter(Function<T, String> objectConverter) {
        if(this.objectConverter == objectConverter) {
            return false;
        }
        this.matches = EMPTY;
        this.matcher = null;
        this.objectConverter = objectConverter;
        return true;
    }

    public void setFilterLogic(LogicalOperator filterLogic) {
        this.filterLogic = filterLogic;
    }

    public boolean setMatcher(BiFunction<T, String[], Boolean> matcher) {
        if(this.matcher == matcher) {
            return false;
        }
        this.matches = EMPTY;
        this.objectConverter = null;
        this.matcher = matcher;
        return true;
    }

    private String[] matchTokens(String m) {
        if(m == null || m.isEmpty()) {
            return EMPTY;
        }
        if(matcher == null) {
            m = m.toUpperCase();
        }
        return m.split("\\s+");
    }

    private boolean exactlySame(String[] matches) {
        if(matches.length != this.matches.length) {
            return false;
        }
        for(int i = 0; i < matches.length; i++) {
            if(!matches[i].equals(this.matches[i])) {
                return false;
            }
        }
        return true;
    }

    private boolean same(String[] matches) {
        if(matches == this.matches || exactlySame(matches)) {
            return true;
        }
        if(matcher != null) {
            return false;
        }
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

    private boolean skipMatching() {
        for(String m: matches) {
            if(!m.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private boolean match(T o) {
        if(matcher != null) {
            return matcher.apply(o, matches);
        }
        String item = (objectConverter == null ? defaultObjectConverter : objectConverter).apply(o);
        if(item == null) {
            return false;
        }
        item = item.toUpperCase();
        boolean contains, truth;
        truth = switch(filterLogic) {
            case OR, NOT_AND -> false;
            default -> true;
        };
        for(String m: matches) {
            contains = item.contains(m);
            switch (filterLogic) {
                case OR:
                    if(contains) {
                        return true;
                    }
                    continue;
                case AND:
                    if(!contains) {
                        return false;
                    }
                    continue;
                case NOT_OR:
                    if(contains) {
                        return false;
                    }
                    continue;
                case NOT_AND:
                    if(!contains) {
                        return true;
                    }
            }
        }
        return truth;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean setMatchTokens(String tokens) { // Returns !changed
        String[] m = matchTokens(tokens);
        if(same(m)) {
            return true;
        }
        matches = m;
        return false;
    }

    public Predicate<T> getPredicate(String tokens, Predicate<T> extraFilter) {
        if(setMatchTokens(tokens) || skipMatching()) {
            return extraFilter;
        }
        if(extraFilter == null) {
            return this::match;
        }
        return item -> match(item) && extraFilter.test(item);
    }
}