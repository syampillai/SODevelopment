package com.storedobject.core;

import com.storedobject.common.Address;
import com.storedobject.common.ComputedValue;
import com.storedobject.common.Fault;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.*;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Stream;

public class JSONMap implements Map<String, Object>, Serializable {

    private final HashMap<String, Object> map = new HashMap<>();

    public JSONMap() {
        this(false);
    }

    public JSONMap(boolean withErrorCode) {
        if(withErrorCode) {
            put("errorCode", Integer.MAX_VALUE);
        }
    }

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
    public Object get(Object key) {
        return map.get(key);
    }

    @Override
    public Object put(String key, Object object) {
        return map.put(key, value(key, object, null));
    }

    /**
     * Creates a new sub-map associated with a given key, stores it in the current map, and returns the newly created sub-map.
     *
     * @param key The key under which the sub-map will be stored.
     * @return A new instance of JSONMap associated with the specified key.
     */
    public JSONMap map(String key) {
        JSONMap map = new JSONMap(false);
        put(key, map);
        return map;
    }

    /**
     * Converts the current JSONMap instance into a JSON object representation.
     *
     * @return A JSON object constructed from the current JSONMap instance.
     */
    public JSON toJSON() {
        normalize();
        return new JSON(this);
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
        map.clear();
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
        error(Integer.MIN_VALUE, error);
    }

    /**
     * Put an error message in the result with an error code.
     *
     * @param errorCode Error code.
     * @param error Error message to be added.
     */
    public void error(int errorCode, String error) {
        if(errorCode > 0) {
            put("errorCode", errorCode);
        }
        put("status", "ERROR");
        put("message", error);
    }

    /**
     * Put an error message in the result with an error code.
     *
     * @param error Error (could be null).
     */
    public void error(Fault error) {
        if(error != null) {
            error(error.getCode(), error.getMessage());
        }
    }

    private static Object value(String key, Object value, BiFunction<String, ContentProducer, String> contentToString) {
        if(value instanceof ComputedValue<?> cv) {
            Map<String, Object> m = new HashMap<>();
            m.put("available", cv.consider());
            m.put("value", value(key, cv.getValueObject(), contentToString));
            return m;
        }
        if (value instanceof HasStreamData hsd) {
            value = new StreamDataContent(hsd.getStreamData());
        }
        if(value instanceof Timestamp ts) {
            return JSON.DateTimeConverter.format(ts);
        } else if(value instanceof Time t) {
            return JSON.DateTimeConverter.format(t);
        } else if(value instanceof java.util.Date d) {
            return JSON.DateTimeConverter.format(d);
        } else if(value instanceof Address a) {
            Map<String, Object> m = new HashMap<>();
            m.put("country", a.getCountry().getShortName());
            m.put("encoded", a.encode());
            m.put("text", a.toString());
            return m;
        } else  if(value instanceof ContentProducer cp) {
            Map<String , Object> m = new HashMap<>();
            m.put("contentType", cp.getContentType());
            if(contentToString == null) {
                m.put("content", "");
            } else {
                m.put("content", contentToString.apply(key, cp));
            }
            return m;
        } else if(value instanceof StoredObject so) {
            return so.toDisplay();
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
        normalize(this, null);
    }

    /**
     * @param contentToString Function to convert ContentProducer to String.
     * Normalize the map so that it will contain only valid JSON values.
     */
    public void normalize(BiFunction<String, ContentProducer, String> contentToString) {
        normalize(this, contentToString);
    }

    /**
     * Normalize the map so that it will contain only valid JSON values.
     *
     * @param result The result map to normalize.
     */
    @SuppressWarnings("unchecked")
    static void normalize(Map<String, Object> result, BiFunction<String, ContentProducer, String> contentToString) {
        Object value;
        Set<String> keys = new HashSet<>(result.keySet());
        for(String key: keys) {
            value = result.get(key);
            if(value instanceof Map<?, ?> m) {
                normalize((Map<String, Object>) m, contentToString);
            } else if(value instanceof Iterable<?> list) {
                result.put(key, normalize(key, list, contentToString));
            } else {
                result.put(key, value(key, value, contentToString));
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static List<Object> normalize(String key, Iterable<?> list, BiFunction<String, ContentProducer, String> contentToString) {
        List<Object> objects = new ArrayList<>();
        for(Object value: list) {
            if(value instanceof Map<?, ?> m) {
                normalize((Map<String, Object>) m, contentToString);
                objects.add(m);
            } else if(value instanceof List<?> l) {
                objects.add(normalize(key, l, contentToString));
            } else {
                objects.add(value(key, value, contentToString));
            }
        }
        return objects;
    }

    public Array array(String key) {
        Array a = new Array();
        put(key, a.list);
        return a;
    }

    public static class Array {

        private final List<Object> list = new ArrayList<>();

        private Array() {
        }

        public Array array() {
            Array a = new Array();
            list.add(a);
            return a;

        }

        public JSONMap map() {
            JSONMap m = new JSONMap(false);
            list.add(m);
            return m;
        }

        public void add(Object o) {
            list.add(o);
        }

        public JSONMap getMap(int index) {
            Object o = list.get(index);
            return o instanceof JSONMap ? (JSONMap) o : null;
        }

        public Array getArray(int index) {
            Object o = list.get(index);
            return o instanceof Array ? (Array) o : null;
        }

        public void remove(int index) {
            list.remove(index);
        }

        public void remove(Object o) {
            list.remove(o);
        }

        public Stream<JSONMap> maps() {
            return list.stream().filter(o -> o instanceof JSONMap).map(o -> (JSONMap) o);
        }

        public Stream<Array> arrays() {
            return list.stream().filter(o -> o instanceof Array).map(o -> (Array) o);
        }

        public Stream<Object> objects() {
            return list.stream();
        }

        public int size() {
            return list.size();
        }
    }
}
