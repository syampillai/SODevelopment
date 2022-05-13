package com.storedobject.core;

import com.storedobject.common.SORuntimeException;
import com.storedobject.core.annotation.*;

import java.sql.Date;

public class SerialPattern extends StoredObject {

    private String name, description;
    private String pattern;

    public SerialPattern() {
    }

    public static void columns(Columns columns) {
        columns.add("Name", "text");
        columns.add("Pattern", "text");
        columns.add("Description", "text");
    }

    public static void indices(Indices indices) {
        indices.add("lower(Name)", true);
    }

    public String getUniqueCondition() {
        return "lower(Name)='" + getName().trim().toLowerCase().replace("'", "''") + "'";
    }

    public static SerialPattern get(String name) {
        return StoredObjectUtility.get(SerialPattern.class, "Name", toCode(name), false);
    }

    public static ObjectIterator<SerialPattern> list(String name) {
        return StoredObjectUtility.list(SerialPattern.class, "Name", toCode(name), false);
    }

    @Override
    public String toString() {
        return getName();
    }

    public static int hints() {
        return ObjectHint.SMALL | ObjectHint.SMALL_LIST;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(order = 100)
    public String getName() {
        return name;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    @Column(required = false, order = 200)
    public String getPattern() {
        return pattern;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(required = false, order = 300)
    public String getDescription() {
        return description;
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        name = toCode(name);
        if(StringUtility.isWhite(name)) {
            throw new Invalid_Value("Name");
        }
        super.validateData(tm);
    }

    /**
     * Get the number string appropriately stuffed as per the pattern.
     *
     * @param tm Transaction manager.
     * @param serial Number to stuff in.
     * @param date Date to stuff in.
     * @return Stuffed number that can be generally used as a unique reference.
     */
    public String getNumber(TransactionManager tm, long serial, Date date) {
        return getNumber(tm, serial, date, pattern);
    }

    /**
     * Get the number string appropriately stuffed as per the pattern.
     *
     * @param tm Transaction manager.
     * @param serial Number to stuff in.
     * @param date Date to stuff in.
     * @param pattern Pattern.
     * @return Stuffed number that can be generally used as a unique reference.
     */
    public static String getNumber(TransactionManager tm, long serial, Date date, String pattern) {
        if(pattern == null || pattern.isBlank()) {
            return "" + serial;
        }
        if(pattern.contains("fy")) {
            SystemEntity se = tm.getEntity();
            if(se == null) {
                throw new SORuntimeException("Entity not set!");
            }
            int y1 = DateUtility.getYear(se.getStartOfFinancialYear(date)),
                    y2 = DateUtility.getYear(se.getEndOfFinancialYear(date));
            if(y1 == y2) {
                pattern = stuff(pattern, "fyfy-fyfy", "" + y2);
                pattern = stuff(pattern, "fyfy-fy", "" + y2);
                pattern = stuff(pattern, "fyfy", "" + y2);
                pattern = stuff(pattern, "fy-fy", ("" + y2).substring(2));
                pattern = stuff(pattern, "fy", ("" + y2).substring(2));
            } else {
                pattern = stuff(pattern, "fyfy-fyfy", y1 + "-" + y2);
                pattern = stuff(pattern, "fyfy-fy", y1 + "-" + ("" + y2).substring(2));
                pattern = stuff(pattern, "fyfy", y1 + "-" + y2);
                String s = ("" + y1).substring(2) + "-" + ("" + y2).substring(2);
                pattern = stuff(pattern, "fy-fy", s);
                pattern = stuff(pattern, "fy", s);
            }
        }
        pattern = stuff(pattern, "yyyy", "" + DateUtility.getYear(date));
        pattern = stuff(pattern, "yy", ("" + DateUtility.getYear(date)).substring(2));
        pattern = stuff(pattern, "mm",
                StringUtility.padLeft("" + DateUtility.getMonth(date), 2, '0'));
        pattern = stuff(pattern, "m", "" + DateUtility.getMonth(date));
        pattern = pattern.replace("serial", "n");
        pattern = pattern.replace("number", "n");
        pattern = pattern.replace("no", "n");
        return stuff(pattern, 'n', serial);
    }

    private static String stuff(String pattern, String lookFor, String replacement) {
        while(pattern.contains(lookFor)) {
            pattern = pattern.replace(lookFor, replacement);
        }
        return pattern;
    }

    /**
     * Get the number string appropriately stuffed as per the pattern.
     *
     * @param patternName Name of the pattern.
     * @param tm Transaction manager.
     * @param serial Number to stuff in.
     * @param date Date to stuff in.
     * @return Stuffed number that can be generally used as a unique reference.
     */
    public static String getNumber(String patternName, TransactionManager tm, long serial, Date date) {
        SerialPattern sp = get(patternName);
        if(sp == null) {
            return "" + serial;
        }
        return getNumber(tm, serial, date, sp.pattern);
    }

    /**
     * Replace the occurrence of a give character with the given serial number.
     *
     * @param pattern Pattern.
     * @param c       Character to look for.
     * @param serial  Serial number.
     * @return Resultant string.
     */
    public static String stuff(String pattern, char c, long serial) {
        String one = "" + c, two = one + one;
        if(!pattern.contains(one)) {
            return pattern;
        }
        if(pattern.indexOf(c) == pattern.lastIndexOf(c)) { // Only one c
            pattern = pattern.replace(one, "" + serial);
        } else {
            int count = StringUtility.getCharCount(pattern, c);
            while(pattern.contains(two)) {
                pattern = pattern.replace(two, one);
            }
            if(pattern.indexOf(c) == pattern.lastIndexOf(c)) { // Occurrence of c is not scattered
                pattern = pattern.replace(one, StringUtility.padLeft("" + serial, count, '0'));
            }
        }
        return pattern;
    }
}
