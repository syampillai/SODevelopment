package com.storedobject.ui.util;

import com.storedobject.common.LogicalOperator;
import com.storedobject.core.ClassAttribute;
import com.storedobject.core.ObjectToString;
import com.storedobject.core.StoredObject;

import java.util.function.BiFunction;

public class ViewFilter<T extends StoredObject> {

    private static final String[] EMPTY = new String[] {};
    private AbstractObjectDataProvider<T, ?, ?> dataProvider;
    private String[] matches = EMPTY;
    private BiFunction<T, String[], Boolean> matcher;
    private ObjectToString<T> objectConverter;
    private final ObjectToString<T> defaultObjectConverter;
    private LogicalOperator filterLogic = LogicalOperator.OR;

    public ViewFilter(AbstractObjectDataProvider<T, ?, ?> dataProvider) {
        this.dataProvider = dataProvider;
        this.dataProvider.setViewFilter(this);
        defaultObjectConverter = ObjectToString.create(dataProvider.getObjectClass(), ClassAttribute.get(dataProvider.getObjectClass()).getAttributes());
    }

    public void setDataProvider(AbstractObjectDataProvider<T, ?, ?> dataProvider) {
        this.dataProvider = dataProvider;
        matches = EMPTY;
        this.dataProvider.setViewFilter(this);
    }

    public boolean setObjectConverter(ObjectToString<T> objectConverter) {
        if(this.objectConverter == objectConverter) {
            return false;
        }
        this.matches = EMPTY;
        this.matcher = null;
        if(this.objectConverter != null && objectConverter == null) {
            for(int i = 0; i < matches.length; i++) {
                matches[i] = matches[i].toUpperCase();
            }
        }
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

    boolean skipMatching() {
        for(String m: matches) {
            if(!m.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    boolean match(T o) {
        if(matcher != null) {
            return matcher.apply(o, matches);
        }
        String item = ((objectConverter == null) ? defaultObjectConverter : objectConverter).toString(o);
        if(item == null) {
            return false;
        }
        item = item.toUpperCase();
        boolean contains, truth;
        switch (filterLogic) {
            case OR:
            case NOT_AND:
                truth = false;
                break;
            default:
                truth = true;
        }
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

    boolean setMatchTokens(String tokens) { // Returns !changed
        String[] m = matchTokens(tokens);
        if(same(m)) {
            return true;
        }
        matches = m;
        return false;
    }
}