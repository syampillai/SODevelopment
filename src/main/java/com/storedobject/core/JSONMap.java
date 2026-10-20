package com.storedobject.core;

import com.storedobject.common.Address;
import com.storedobject.common.ComputedValue;
import com.storedobject.common.Fault;
import com.storedobject.common.Geolocation;
import org.jspecify.annotations.NonNull;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

public class JSONMap implements Map<String, Object>, Serializable {

    private final Map<String, Object> map = new TreeMap<>();
    private boolean rawMode = false;

    public JSONMap() {
        this(false);
    }

    public JSONMap(boolean withErrorCode) {
        if(withErrorCode) {
            put("errorCode", Integer.MAX_VALUE);
        }
    }

    public void setRawMode(boolean rawMode) {
        this.rawMode = rawMode;
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
    public Object put(@Nonnull String key, Object object) {
        return map.put(key, rawMode ? object : value(key, object, null));
    }

    /**
     * Creates a new sub-map associated with a given key, stores it in the current map, and returns the newly created sub-map.
     * <p>Note: If a map already exists, it will be returned instead of creating a new one.</p>
     *
     * @param key The key under which the sub-map will be stored.
     * @return A new instance of JSONMap associated with the specified key.
     */
    public JSONMap map(String key) {
        Object o = get(key);
        if(o instanceof JSONMap) {
            return (JSONMap)o;
        }
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
        try {
            return new JSON(this);
        } catch (Throwable e) {
            ApplicationServer.log("\n--- Invalid JSON ---\n" + map + "\n--- End of JSON ---", e);
            return new JSON("""
                    {"error": "JSON Serialization Error"}
                    """);
        }
    }

    @Override
    public Object remove(Object o) {
        return map.remove(o);
    }

    @Override
    public void putAll(@Nonnull Map<? extends String, ?> map) {
        map.forEach(this::put);
    }

    @Override
    public Object putIfAbsent(@Nonnull String key, Object value) {
        return map.putIfAbsent(key, rawMode ? value : value(key, value, null));
    }

    @Override
    public Object computeIfAbsent(@Nonnull String key, @NonNull Function<? super String, ?> mappingFunction) {
        return map.computeIfAbsent(key, k -> {
            Object computed = mappingFunction.apply(k);
            return rawMode ? computed : value(k, computed, null);
        });
    }

    @Override
    public Object computeIfPresent(@Nonnull String key, @NonNull BiFunction<? super String, ? super Object, ?> remappingFunction) {
        return map.computeIfPresent(key, (k, v) -> {
            Object computed = remappingFunction.apply(k, v);
            return rawMode ? computed : value(k, computed, null);
        });
    }

    @Override
    public Object compute(@Nonnull String key, @NonNull BiFunction<? super String, ? super Object, ?> remappingFunction) {
        return map.compute(key, (k, v) -> {
            Object computed = remappingFunction.apply(k, v);
            return rawMode ? computed : value(k, computed, null);
        });
    }

    @Override
    public Object replace(@Nonnull String key, Object value) {
        return map.replace(key, rawMode ? value : value(key, value, null));
    }

    @Override
    public void replaceAll(BiFunction<? super String, ? super Object, ?> function) {
        map.replaceAll((k, v) -> {
            Object computed = function.apply(k, v);
            return rawMode ? computed : value(k, computed, null);
        });
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
        if(errorCode == 0) {
            remove("errorCode");
        } else {
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
        return switch (value) {
            case ComputedValue<?> cv -> {
                Map<String, Object> m = new TreeMap<>();
                m.put("available", cv.consider());
                if (cv instanceof ComputedMinute cm) {
                    m.put("valueAsText", cm.toString());
                }
                m.put("value", value(key, cv.getValueObject(), contentToString));
                yield m;
            }
            case DecimalNumber dn -> dn.getValue();
            case HasStreamData hsd -> value(key, new StreamDataContent(hsd.getStreamData()), contentToString);
            case Timestamp ts -> Utility.isEmpty(ts) ? null : JSON.DateTimeConverter.format(ts);
            case Time t -> Utility.isEmpty(t) ? null : JSON.DateTimeConverter.format(t);
            case java.util.Date d -> Utility.isEmpty(d) ? null : JSON.DateTimeConverter.format(d);
            case DatePeriod p -> {
                Map<String, Object> m = new TreeMap<>();
                m.put("dateFrom", JSON.DateTimeConverter.format(p.getFrom()));
                m.put("dateTo", JSON.DateTimeConverter.format(p.getTo()));
                yield m;
            }
            case TimePeriod p -> {
                Map<String, Object> m = new TreeMap<>();
                m.put("timeFrom", JSON.DateTimeConverter.format(p.getFrom()));
                m.put("timeTo", JSON.DateTimeConverter.format(p.getTo()));
                yield m;
            }
            case TimestampPeriod p -> {
                Map<String, Object> m = new TreeMap<>();
                m.put("timeFrom", JSON.DateTimeConverter.format(p.getFrom()));
                m.put("timeTo", JSON.DateTimeConverter.format(p.getTo()));
                yield m;
            }
            case Address a -> {
                Map<String, Object> m = new TreeMap<>();
                m.put("country", a.getCountry().getShortName());
                m.put("encoded", a.encode());
                m.put("text", a.toString());
                yield m;
            }
            case ContentProducer cp -> {
                Map<String, Object> m = new TreeMap<>();
                m.put("contentType", cp.getContentType());
                if (contentToString == null) {
                    m.put("content", "");
                } else {
                    m.put("content", contentToString.apply(key, cp));
                }
                yield m;
            }
            case StoredObject so -> so.toDisplay();
            case Money money -> {
                Map<String, Object> m = new TreeMap<>();
                m.put("currency", money.getCurrency().getCurrencyCode());
                m.put("amount", money.getValue());
                yield m;
            }
            case Quantity q -> {
                Map<String, Object> m = new TreeMap<>();
                m.put("unit", q.getUnit().getUnit());
                m.put("quantity", q.getValue());
                yield m;
            }
            case Geolocation geo -> geo.toString();
            case Id id -> id.toString();
            case null, default -> value;
        };
    }

    /**
     * Normalize the map so that it will contain only valid JSON values.
     */
    public void normalize() {
        if(rawMode) {
            return;
        }
        normalize(this, null);
    }

    /**
     * @param contentToString Function to convert ContentProducer to String.
     * Normalize the map so that it will contain only valid JSON values.
     */
    public void normalize(BiFunction<String, ContentProducer, String> contentToString) {
        if(rawMode) {
            return;
        }
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
        for (Entry<String, Object> entry : result.entrySet()) {
            String key = entry.getKey();
            value = entry.getValue();
            switch (value) {
                case Map<?, ?> m -> normalize((Map<String, Object>) m, contentToString);
                case Array a -> entry.setValue(normalize(key, a.list, contentToString));
                case Iterable<?> list -> entry.setValue(normalize(key, list, contentToString));
                case null, default -> entry.setValue(value(key, value, contentToString));
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static List<Object> normalize(String key, Iterable<?> list, BiFunction<String, ContentProducer, String> contentToString) {
        List<Object> objects = new ArrayList<>();
        for(Object value: list) {
            switch (value) {
                case Map<?, ?> m -> {
                    normalize((Map<String, Object>) m, contentToString);
                    objects.add(m);
                }
                case List<?> l -> objects.add(normalize(key, l, contentToString));
                case null, default -> objects.add(value(key, value, contentToString));
            }
        }
        return objects;
    }

    public Array array(String key) {
        Object o = get(key);
        if(o instanceof Array) {
            return (Array)o;
        }
        Array a = new Array();
        put(key, a);
        return a;
    }

    @Override
    public String toString() {
        return toJSON().toPrettyString();
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
