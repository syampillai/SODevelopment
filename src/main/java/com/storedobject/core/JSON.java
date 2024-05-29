package com.storedobject.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.storedobject.common.ComputedValue;
import com.storedobject.common.MathUtility;
import com.storedobject.common.StringList;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.BiFunction;

/**
 * Extended JSON class with more extract methods for the core-specific attribute types.
 *
 * @author Syam
 */
public class JSON extends com.storedobject.common.JSON {

    /**
     * ISO 8601 date format string.
     */
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    /**
     * ISO 8601 date-time (timestamp) format string.
     */
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'hh:mm:ss.SSS";
    /**
     * ISO 8601 time format string.
     */
    public static final String TIME_FORMAT = "hh:mm:ss.SSS";

    /**
     * Construct an empty JSON.
     */
    public JSON() {
    }

    /**
     * Construct a JSON instance from an Object that could hopefully parse into a JSON compatible String.
     * Typically, it could be a {@link Map} or some sort of array or collection. It could also be a standalone object
     * that can be converted to a valid JSON string. Otherwise, the following types are handled as special cases: (1) A
     * {@link JsonNode} instance - directly set internally, (2) Another {@link com.storedobject.common.JSON} instance
     * - directly set internally, (3) An {@link InputStream} instance - expects a string value from the stream,
     * (4) An instance of a {@link Reader} - expects a string value from it, (5) A {@link URL} instance - content
     * from the {@link URL} is read and processed.
     *
     * @param object JSON to construct from this Object.
     */
    public JSON(Object object) {
        super(object);
    }

    @Override
    public JSON get(int n) {
        com.storedobject.common.JSON json = super.get(n);
        return json == null ? null : new JSON(json);
    }

    @Override
    public JSON get(String key) {
        com.storedobject.common.JSON json = super.get(key);
        return json == null ? null : new JSON(json);
    }

    @Override
    public JSON get(String key, int n) {
        com.storedobject.common.JSON json = super.get(key, n);
        return json == null ? null : new JSON(json);
    }

    /**
     * Helper method to retrieve a date value from the JSON request.
     * @param attribute Attribute name.
     * @return Date if found, otherwise null.
     */
    public Date getDate(String attribute) {
        return getDate(this, attribute);
    }

    private static Date getDate(com.storedobject.common.JSON json, String attribute) {
        try {
            return new Date(new SimpleDateFormat(DATE_FORMAT).parse(json.getString(attribute)).getTime());
        } catch(Throwable e) {
            return null;
        }
    }

    /**
     * Helper method to retrieve a date value from the JSON request assuming that the attribute name is "date".
     * @return Date if found, otherwise null.
     */
    public Date getDate() {
        return getDate("date");
    }

    /**
     * Helper method to retrieve a timestamp value from the JSON request.
     * @param attribute Attribute name.
     * @return Timestamp if found, otherwise null.
     */
    public Timestamp getTimestamp(String attribute) {
        try {
            return new Timestamp(new SimpleDateFormat(DATE_TIME_FORMAT)
                    .parse(getString(attribute)).getTime());
        } catch(Throwable e) {
            return null;
        }
    }

    /**
     * Helper method to retrieve a time value from the JSON request.
     * @param attribute Attribute name.
     * @return Time if found, otherwise null.
     */
    public Time getTime(String attribute) {
        try {
            return new Time(new SimpleDateFormat(TIME_FORMAT)
                    .parse(getString(attribute)).getTime());
        } catch(Throwable e) {
            return null;
        }
    }

    /**
     * Helper method to retrieve a {@link Id} from the JSON request.
     * @param attribute Attribute name.
     * @return {@link Id} if found, otherwise null.
     */
    public Id getId(String attribute) {
        BigDecimal n = getBigDecimal(attribute);
        if(n == null) {
            String s = getString(attribute);
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
     * @param attribute Attribute name.
     * @return Quantity if found, otherwise null.
     */
    public Quantity getQuantity(String attribute) {
        com.storedobject.common.JSON json = get(attribute);
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
     * @param attribute Attribute name.
     * @return Money if found, otherwise null.
     */
    public Money getMoney(String attribute) {
        com.storedobject.common.JSON json = get(attribute);
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
     * @param attribute Attribute name.
     * @return {@link ComputedDate} value if found, otherwise null.
     */
    public ComputedDate getComputedDate(String attribute) {
        com.storedobject.common.JSON json = get(attribute);
        if(json == null) {
            return null;
        }
        Boolean available = json.getBoolean("available");
        if(available == null) {
            available = Boolean.TRUE;
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
     * @param attribute Attribute name.
     * @return {@link ComputedMinute} value if found, otherwise null.
     */
    public ComputedMinute getComputedMinute(String attribute) {
        return (ComputedMinute) getComputed(attribute, (n, b) -> new ComputedMinute(n.intValue(), b));
    }

    /**
     * Helper method to retrieve a {@link ComputedDouble} value from the JSON request.
     * @param attribute Attribute name.
     * @return {@link ComputedDouble} value if found, otherwise null.
     */
    public ComputedDouble getComputedDouble(String attribute) {
        return (ComputedDouble) getComputed(attribute, (n, b) -> new ComputedDouble(n.doubleValue(), b));
    }

    /**
     * Helper method to retrieve a {@link ComputedInteger} value from the JSON request.
     * @param attribute Attribute name.
     * @return {@link ComputedInteger} value if found, otherwise null.
     */
    public ComputedInteger getComputedInteger(String attribute) {
        return (ComputedInteger) getComputed(attribute, (n, b) -> new ComputedInteger(n.intValue(), b));
    }

    /**
     * Helper method to retrieve a {@link ComputedLong} value from the JSON request.
     * @param attribute Attribute name.
     * @return {@link ComputedLong} value if found, otherwise null.
     */
    public ComputedLong getComputedLong(String attribute) {
        return (ComputedLong) getComputed(attribute, (n, b) -> new ComputedLong(n.longValue(), b));
    }

    private <T> ComputedValue<T> getComputed(String attribute,
                                             BiFunction<Number, Boolean, ComputedValue<T>> func) {
        com.storedobject.common.JSON json = get(attribute);
        if(json == null) {
            return null;
        }
        Boolean available = json.getBoolean("available");
        if(available == null) {
            available = Boolean.TRUE;
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
     * @param attribute Attribute name.
     * @return {@link Integer} value if found, otherwise null.
     */
    public Integer getInteger(String attribute) {
        Number n = getNumber(attribute);
        return n == null ? null : n.intValue();
    }

    /**
     * Helper method to retrieve a {@link Long} value from the JSON request.
     * @param attribute Attribute name.
     * @return {@link Long} value if found, otherwise null.
     */
    public Long getLong(String attribute) {
        Number n = getNumber(attribute);
        return n == null ? null : n.longValue();
    }

    /**
     * Helper method to retrieve a {@link Double} value from the JSON request.
     * @param attribute Attribute name.
     * @return {@link Double} value if found, otherwise null.
     */
    public Double getDouble(String attribute) {
        Number n = getNumber(attribute);
        return n == null ? null : n.doubleValue();
    }

    /**
     * Helper method to retrieve a {@link BigDecimal} value from the JSON request.
     * @param attribute Attribute name.
     * @return {@link BigDecimal} value if found, otherwise null.
     */
    public BigDecimal getBigDecimal(String attribute) {
        return MathUtility.toBigDecimal(getNumber(attribute));
    }

    /**
     * Helper method to retrieve a {@link StringList} from the JSON request.
     * @param attribute Attribute name.
     * @return {@link StringList} if found, otherwise null.
     */
    public StringList getStringList(String attribute) {
        com.storedobject.common.JSON json = get(attribute);
        if(json == null) {
            return null;
        }
        if(json.getType() == Type.ARRAY) {
            String[] as = new String[json.getArraySize()];
            for(int i = 0; i < as.length; i++) {
                as[i] = json.get(i).getString();
                if(as[i] == null) {
                    return null;
                }
            }
            return StringList.create(as);
        } else if(json.getType() == Type.STRING) {
            return StringList.create(json.getString());
        }
        return null;
    }

    /**
     * Helper method to retrieve a {@link DatePeriod} value from the JSON request assuming that "dateFrom" and
     * "dateTo" are the names of the attributes.
     * @return {@link BigDecimal} value if found, otherwise null.
     */
    public DatePeriod getDatePeriod() {
        Date d1 = getDate("dateFrom"), d2 = getDate("dateTo");
        if(d1 == null || d2 == null) {
            return null;
        }
        return DatePeriod.create(d1, d2);
    }

    /**
     * Helper method to retrieve a {@link StoredObject} class value from the JSON request.
     * @return {@link StoredObject} class value if found, otherwise an exception is raised.
     * @exception Exception is thrown if class name can't be extracted or the name is invalid.
     */
    public Class<? extends StoredObject> getDataClass(String attribute) throws Exception {
        String className = getString(attribute);
        try {
            if(className == null) {
                throw new Exception("Class not specified");
            }
            Class<?> dClass = JavaClassLoader.getLogic(ApplicationServer.guessClass(className));
            Class<? extends StoredObject> dataClass;
            if(StoredObject.class.isAssignableFrom(dClass) && dClass != StoredObject.class) {
                //noinspection unchecked
                dataClass = (Class<? extends StoredObject>) dClass;
            } else {
                throw new Exception("Not a data class - " + dClass.getName());
            }
            if(dataClass == Secret.class) {
                throw new Exception("No access - " + dClass.getName());
            }
            return dataClass;
        } catch (ClassNotFoundException e) {
            throw new Exception("Class not found - " + className);
        }
    }

    /**
     * Helper method to retrieve an {@link Account} instance from the JSON request. The parameter value must be
     * the "account number".
     * @param attribute Attribute name.
     * @return {@link Account} instance if found, otherwise null.
     */
    public Account getAccount(String attribute) {
        return Account.getFor(getString(attribute));
    }
}
