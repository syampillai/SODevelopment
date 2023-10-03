package com.storedobject.core;

import com.storedobject.common.ComputedValue;
import com.storedobject.common.JSON;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@FunctionalInterface
public interface JSONService {

	String DATE_FORMAT = "yyyy-MM-dd", DATE_TIME_FORMAT = "yyyy-MM-dd'T'hh:mm:ss.SSS", TIME_FORMAT = "hh:mm:ss.SSS";

	void execute(Device device, JSON json, Map<String, Object> result);

	static Date getDate(JSON json, String attribute) {
		try {
			return new Date(new SimpleDateFormat(DATE_FORMAT).parse(json.getString(attribute)).getTime());
		} catch(Throwable e) {
			return null;
		}
	}

	static Timestamp getTimestamp(JSON json, String attribute) {
		try {
			return new Timestamp(new SimpleDateFormat(DATE_TIME_FORMAT)
					.parse(json.getString(attribute)).getTime());
		} catch(Throwable e) {
			return null;
		}
	}

	static Time getTime(JSON json, String attribute) {
		try {
			return new Time(new SimpleDateFormat(TIME_FORMAT)
					.parse(json.getString(attribute)).getTime());
		} catch(Throwable e) {
			return null;
		}
	}

	static void put(String attribute, Object value, Map<String, Object> result) {
		result.put(attribute, value(value));
	}

	private static Object value(Object value) {
		if(value instanceof ComputedValue<?> cv) {
			Map<String, Object> m = new HashMap<>();
			m.put("available", cv.consider());
			m.put("value", value(cv.getValueObject()));
			return m;
		}
		if(value instanceof Timestamp ts) {
			return new SimpleDateFormat(DATE_TIME_FORMAT).format(ts);
		} else if(value instanceof Time t) {
			return new SimpleDateFormat(TIME_FORMAT).format(t);
		} else if(value instanceof java.util.Date d) {
			return new SimpleDateFormat(DATE_FORMAT).format(d);
		} else if(value instanceof StoredObject so) {
			if(so instanceof FileData || so instanceof StreamData) { // Note: JSON service will handle it!
				return so;
			}
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
			return id.get();
		}
		return value;
	}

	static void error(String error, Map<String, Object> result) {
		result.put("status", "ERROR");
		result.put("message", error);
	}

	@SuppressWarnings("unchecked")
	static void normalize(Map<String, Object> map) {
		Object value;
		for(String key: map.keySet()) {
			value = map.get(key);
			if(value instanceof Map<?, ?> m) {
				normalize((Map<String, Object>) m);
			} else if(value instanceof List<?> list) {
				map.put(key, normalize(list));
			} else {
				map.put(key, value(value));
			}
		}
	}

	@SuppressWarnings("unchecked")
	private static List<Object> normalize(List<?> list) {
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
