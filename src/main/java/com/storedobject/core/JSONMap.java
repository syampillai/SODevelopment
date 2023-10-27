package com.storedobject.core;

import com.storedobject.common.ComputedValue;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

public class JSONMap implements Map<String, Object>, Serializable {

    private final HashMap<String, Object> map = new HashMap<>();

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object o) {
        return map.containsKey(o);
    }

    @Override
    public boolean containsValue(Object o) {
        return map.containsValue(o);
    }

    @Override
    public Object get(Object o) {
        return map.get(o);
    }

    @Override
    public Object put(String s, Object object) {
        return map.put(s, value(object));
    }

    @Override
    public Object remove(Object o) {
        return map.remove(o);
    }

    @Override
    public void putAll(@Nonnull Map<? extends String, ?> map) {
        this.map.putAll(map);
    }

    @Override
    public void clear() {

    }

    @Nonnull
    @Override
    public Set<String> keySet() {
        return map.keySet();
    }

    @Nonnull
    @Override
    public Collection<Object> values() {
        return map.values();
    }

    @Nonnull
    @Override
    public Set<Entry<String, Object>> entrySet() {
        return map.entrySet();
    }


    /**
     * Put an error message in the result.
     *
     * @param error Error message to be added.
     */
    public void error(String error) {
        put("status", "ERROR");
        put("message", error);
    }

    private static Object value(Object value) {
        if(value instanceof ComputedValue<?> cv) {
            Map<String, Object> m = new HashMap<>();
            m.put("available", cv.consider());
            m.put("value", value(cv.getValueObject()));
            return m;
        }
        if(value instanceof Timestamp ts) {
            return new SimpleDateFormat(JSON.DATE_TIME_FORMAT).format(ts);
        } else if(value instanceof Time t) {
            return new SimpleDateFormat(JSON.TIME_FORMAT).format(t);
        } else if(value instanceof java.util.Date d) {
            return new SimpleDateFormat(JSON.DATE_FORMAT).format(d);
        } else if(value instanceof StoredObject so) {
            if (so instanceof FileData || so instanceof StreamData) { // Note: JSON service will handle it!
                return so;
            }
            return so.toDisplay();
        } else if(value instanceof ContentProducer) {
            return value; // Note: JSON service will handle it!
        } else if(value instanceof Money money) {
            Map<String, Object> m = new HashMap<>();
            m.put("currency", money.getCurrency().getCurrencyCode());
            m.put("amount", money.getValue());
            return m;
        } else if(value instanceof Quantity q) {
            Map<String, Object> m = new HashMap<>();
            m.put("unit", q.getUnit().getUnit());
            m.put("quantity", q.getValue());
            return m;
        } else if(value instanceof Id id) {
            return id.toString();
        }
        return value;
    }

    /**
     * Normalize the map so that it will contain only valid JSON values.
     */
    public void normalize() {
        normalize(this);
    }

    /**
     * Normalize the map so that it will contain only valid JSON values.
     *
     * @param result The result map to normalize.
     */
    @SuppressWarnings("unchecked")
    static void normalize(Map<String, Object> result) {
        Object value;
        for(String key: result.keySet()) {
            value = result.get(key);
            if(value instanceof Map<?, ?> m) {
                normalize((Map<String, Object>) m);
            } else if(value instanceof Iterable<?> list) {
                result.put(key, normalize(list));
            } else {
                result.put(key, value(value));
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static List<Object> normalize(Iterable<?> list) {
        List<Object> objects = new ArrayList<>();
        for(Object value: list) {
            if(value instanceof Map<?, ?> m) {
                normalize((Map<String, Object>) m);
                objects.add(m);
            } else if(value instanceof List<?> l) {
                objects.add(normalize(l));
            } else {
                objects.add(value(value));
            }
        }
        return objects;
    }
}
