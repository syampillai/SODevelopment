package com.storedobject.core;

import com.storedobject.common.ComputedValue;
import com.storedobject.common.JSON;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Interface defining the JSON Service interface. (SO Connector logic should implement this interface).
 *
 * @author Syam
 */
@FunctionalInterface
public interface JSONService {

	/**
	 * ISO 8601 date format string.
	 */
	String DATE_FORMAT = "yyyy-MM-dd";
	/**
	 * ISO 8601 date-time (timestamp) format string.
	 */
	String DATE_TIME_FORMAT = "yyyy-MM-dd'T'hh:mm:ss.SSS";
	/**
	 * ISO 8601 time format string.
	 */
	String TIME_FORMAT = "hh:mm:ss.SSS";

	/**
	 * Method to be implemented to serve the connector API call.
	 *
	 * @param device Device on which the call is made.
	 * @param json JSON request.
	 * @param result Response should be added to this map. To indicate an error invoke
	 * {@link JSONService#error(String, Map)}.
	 */
	void execute(Device device, JSON json, Map<String, Object> result);

	/**
	 * Helper method to retrieve a date value from the JSON request.
	 * @param json Request received.
	 * @param attribute Attribute name.
	 * @return Date if found, otherwise null.
	 */
	static Date getDate(JSON json, String attribute) {
		try {
			return new Date(new SimpleDateFormat(DATE_FORMAT).parse(json.getString(attribute)).getTime());
		} catch(Throwable e) {
			return null;
		}
	}

	/**
	 * Helper method to retrieve a timestamp value from the JSON request.
	 * @param json Request received.
	 * @param attribute Attribute name.
	 * @return Timestamp if found, otherwise null.
	 */
	static Timestamp getTimestamp(JSON json, String attribute) {
		try {
			return new Timestamp(new SimpleDateFormat(DATE_TIME_FORMAT)
					.parse(json.getString(attribute)).getTime());
		} catch(Throwable e) {
			return null;
		}
	}

	/**
	 * Helper method to retrieve a time value from the JSON request.
	 * @param json Request received.
	 * @param attribute Attribute name.
	 * @return Time if found, otherwise null.
	 */
	static Time getTime(JSON json, String attribute) {
		try {
			return new Time(new SimpleDateFormat(TIME_FORMAT)
					.parse(json.getString(attribute)).getTime());
		} catch(Throwable e) {
			return null;
		}
	}

	/**
	 * Helper method to retrieve a {@link Id} from the JSON request.
	 * @param json Request received.
	 * @param attribute Attribute name.
	 * @return {@link Id} if found, otherwise null.
	 */
	static Id getId(JSON json, String attribute) {
		Number n = json.getNumber(attribute);
		if(n == null) {
			return null;
		}
		if(n instanceof BigDecimal bd) {
			return new Id(bd);
		}
		if(n instanceof BigInteger bi) {
			return new Id(bi);
		}
		return new Id(BigInteger.valueOf(n.longValue()));
	}

	/**
	 * Put some value in to the result. This method converts the value to a valid JSON value.
	 *
	 * @param attribute Attribute name.
	 * @param value Value to put.
	 * @param result The result map to which the value to be put.
	 */
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

	/**
	 * Put an error message in the result.
	 *
	 * @param error Error message to be added.
	 * @param result The result map to which the value to be put.
	 */
	static void error(String error, Map<String, Object> result) {
		result.put("status", "ERROR");
		result.put("message", error);
	}

	/**
	 * Normalize the result map so that it will contain only valid JSON values.
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
