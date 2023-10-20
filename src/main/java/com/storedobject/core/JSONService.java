package com.storedobject.core;

import com.storedobject.common.ComputedValue;
import com.storedobject.common.JSON;
import com.storedobject.common.MathUtility;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.BiFunction;

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
		BigDecimal n = getBigDecimal(json, attribute);
		if(n == null) {
			String s = json.getString(attribute);
			if(StringUtility.isNumber(s)) {
				return new Id(s);
			} else {
				return null;
			}
		}
		return new Id(n);
	}

	/**
	 * Helper method to retrieve a quantity value from the JSON request.
	 * @param json Request received.
	 * @param attribute Attribute name.
	 * @return Quantity if found, otherwise null.
	 */
	static Quantity getQuantity(JSON json, String attribute) {
		json = json.get(attribute);
		if(json == null) {
			return null;
		}
		MeasurementUnit unit = MeasurementUnit.get(json.getString("unit"));
		BigDecimal quantity = MathUtility.toBigDecimal(json.getNumber("quantity"));
		if(unit == null || quantity == null) {
			return null;
		}
		return Quantity.create(quantity, unit);
	}

	/**
	 * Helper method to retrieve a monetary value from the JSON request.
	 * @param json Request received.
	 * @param attribute Attribute name.
	 * @return Money if found, otherwise null.
	 */
	static Money getMoney(JSON json, String attribute) {
		json = json.get(attribute);
		if(json == null) {
			return null;
		}
		Currency currency = Money.getCurrency(json.getString("currency"));
		BigDecimal amount = MathUtility.toBigDecimal(json.getNumber("amount"));
		if(currency == null || amount == null) {
			return null;
		}
		return new Money(amount, currency);
	}

	/**
	 * Helper method to retrieve a {@link ComputedDate} value from the JSON request.
	 * @param json Request received.
	 * @param attribute Attribute name.
	 * @return {@link ComputedDate} value if found, otherwise null.
	 */
	static ComputedDate getComputedDate(JSON json, String attribute) {
		json = json.get(attribute);
		if(json == null) {
			return null;
		}
		Boolean available = json.getBoolean("available");
		if(available == null) {
			return null;
		}
		Date date = getDate(json, "value");
		if(date == null) {
			if(available) {
				return null;
			} else {
				date = DateUtility.today();
			}
		}
		return new ComputedDate(date, !available);
	}

	/**
	 * Helper method to retrieve a {@link ComputedMinute} value from the JSON request.
	 * @param json Request received.
	 * @param attribute Attribute name.
	 * @return {@link ComputedMinute} value if found, otherwise null.
	 */
	static ComputedMinute getComputedMinute(JSON json, String attribute) {
		return (ComputedMinute) getComputed(json, attribute, (n, b) -> new ComputedMinute(n.intValue(), b));
	}

	/**
	 * Helper method to retrieve a {@link ComputedDouble} value from the JSON request.
	 * @param json Request received.
	 * @param attribute Attribute name.
	 * @return {@link ComputedDouble} value if found, otherwise null.
	 */
	static ComputedDouble getComputedDouble(JSON json, String attribute) {
		return (ComputedDouble) getComputed(json, attribute, (n, b) -> new ComputedDouble(n.doubleValue(), b));
	}

	/**
	 * Helper method to retrieve a {@link ComputedInteger} value from the JSON request.
	 * @param json Request received.
	 * @param attribute Attribute name.
	 * @return {@link ComputedInteger} value if found, otherwise null.
	 */
	static ComputedInteger getComputedInteger(JSON json, String attribute) {
		return (ComputedInteger) getComputed(json, attribute, (n, b) -> new ComputedInteger(n.intValue(), b));
	}

	/**
	 * Helper method to retrieve a {@link ComputedLong} value from the JSON request.
	 * @param json Request received.
	 * @param attribute Attribute name.
	 * @return {@link ComputedLong} value if found, otherwise null.
	 */
	static ComputedLong getComputedLong(JSON json, String attribute) {
		return (ComputedLong) getComputed(json, attribute, (n, b) -> new ComputedLong(n.longValue(), b));
	}

	private static <T> ComputedValue<T> getComputed(JSON json, String attribute,
													BiFunction<Number, Boolean, ComputedValue<T>> func) {
		json = json.get(attribute);
		if(json == null) {
			return null;
		}
		Boolean available = json.getBoolean("available");
		if(available == null) {
			return null;
		}
		Number number = json.getNumber("value");
		if(number == null) {
			if(available) {
				return null;
			} else {
				number = 0;
			}
		}
		return func.apply(number, !available);
	}

	/**
	 * Helper method to retrieve a {@link Integer} value from the JSON request.
	 * @param json Request received.
	 * @param attribute Attribute name.
	 * @return {@link Integer} value if found, otherwise null.
	 */
	static Integer getInteger(JSON json, String attribute) {
		Number n = json.getNumber(attribute);
		return n == null ? null : n.intValue();
	}

	/**
	 * Helper method to retrieve a {@link Long} value from the JSON request.
	 * @param json Request received.
	 * @param attribute Attribute name.
	 * @return {@link Long} value if found, otherwise null.
	 */
	static Long getLong(JSON json, String attribute) {
		Number n = json.getNumber(attribute);
		return n == null ? null : n.longValue();
	}

	/**
	 * Helper method to retrieve a {@link Double} value from the JSON request.
	 * @param json Request received.
	 * @param attribute Attribute name.
	 * @return {@link Double} value if found, otherwise null.
	 */
	static Double getDouble(JSON json, String attribute) {
		Number n = json.getNumber(attribute);
		return n == null ? null : n.doubleValue();
	}

	/**
	 * Helper method to retrieve a {@link BigDecimal} value from the JSON request.
	 * @param json Request received.
	 * @param attribute Attribute name.
	 * @return {@link BigDecimal} value if found, otherwise null.
	 */
	static BigDecimal getBigDecimal(JSON json, String attribute) {
		return MathUtility.toBigDecimal(json.getNumber(attribute));
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
			return id.toString();
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

	/**
	 * Get the {@link StreamData} instance for the given name. The name could be the {@link Id} of the instance or the
	 * {@link Id} or name of a {@link FileData} instance as a string.
	 *
	 * @param name Name.
	 * @return {@link StreamData} instance if available.
	 */
	static StreamData getStreamData(String name) {
		StreamData sd = null;
		if(StringUtility.isDigit(name)) {
			sd = StreamData.get(StreamData.class, "Id=" + name);
			if(sd == null) {
				FileData file = FileData.get(FileData.class, "Id=" + name, true);
				if(file != null) {
					sd = file.getFile();
				}
			}
		} else {
			FileData file = FileData.get(name);
			if(file != null) {
				sd = file.getFile();
			}
		}
		return sd;
	}
}
