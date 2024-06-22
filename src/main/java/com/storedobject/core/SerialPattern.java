package com.storedobject.core;

import com.storedobject.core.annotation.Column;

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

    static String getPatternFor(String name, String defaultPattern, Transaction transaction) {
        String p = getPatternFor(name, defaultPattern, transaction == null);
        if(p != null) {
            return p;
        }
        p = getPatternFor(name, defaultPattern, true);
        if(transaction != null) {
            SerialPattern sp = new SerialPattern();
            sp.name = name;
            sp.description = "Auto-generated";
            sp.pattern = p;
            try {
                transaction.getManager().transact(sp::save);
            } catch(Exception ignored) {
            }
        }
        return p;
    }

    private static String getPatternFor(String name, String defaultPattern, boolean recursive) {
        SerialPattern p = get(SerialPattern.class, "lower(Name)='" + name.toLowerCase() + "'");
        if(p != null) {
            return p.pattern;
        }
        if(!recursive) {
            return null;
        }
        int pos = name.lastIndexOf('-');
        if(pos > 0) {
            return getPatternFor(name.substring(0, pos), defaultPattern, true);
        }
        return defaultPattern;
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

    @Override
    void savedCore() throws Exception {
        ReferencePattern.clearPatterns();
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
     * @param systemEntity System entity.
     * @param serial Number to stuff in.
     * @param date Date to stuff in.
     * @return Stuffed number that can be generally used as a unique reference.
     */
    public String getNumber(SystemEntity systemEntity, long serial, Date date) {
        return getNumber(systemEntity, serial, date, pattern);
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
        return getNumber(tm == null ? null : tm.getEntity(), serial, date, pattern);
    }

    /**
     * Get the number string appropriately stuffed as per the pattern.
     *
     * @param systemEntity System entity.
     * @param serial Number to stuff in.
     * @param date Date to stuff in.
     * @param pattern Pattern.
     * @return Stuffed number that can be generally used as a unique reference.
     */
    public static String getNumber(SystemEntity systemEntity, long serial, Date date, String pattern) {
        if(pattern == null || pattern.isBlank()) {
            return String.valueOf(serial);
        }
        if(pattern.contains("fy")) {
            int y1 = DateUtility.getYear(systemEntity.getStartOfFinancialYear(date)),
                    y2 = DateUtility.getYear(systemEntity.getEndOfFinancialYear(date));
            String s;
            if(y1 == y2) {
                s = (String.valueOf(y2)).substring(2);
                pattern = stuff(pattern, "fy4", s);
                pattern = stuff(pattern, "fy6", String.valueOf(y2));
                pattern = stuff(pattern, "fy8", String.valueOf(y2));
                pattern = stuff(pattern, "fyfy-fyfy", String.valueOf(y2));
                pattern = stuff(pattern, "fyfy-fy", String.valueOf(y2));
                pattern = stuff(pattern, "fyfy", String.valueOf(y2));
            } else {
                s = (String.valueOf(y1)).substring(2) + (String.valueOf(y2)).substring(2);
                pattern = stuff(pattern, "fy4", s);
                s = y1 + (String.valueOf(y2)).substring(2);
                pattern = stuff(pattern, "fy6", s);
                s = (String.valueOf(y1)) + y2;
                pattern = stuff(pattern, "fy8", s);
                pattern = stuff(pattern, "fyfy-fyfy", y1 + "-" + y2);
                pattern = stuff(pattern, "fyfy-fy", y1 + "-" + (String.valueOf(y2)).substring(2));
                pattern = stuff(pattern, "fyfy", y1 + "-" + y2);
                s = (String.valueOf(y1)).substring(2) + "-" + (String.valueOf(y2)).substring(2);
            }
            pattern = stuff(pattern, "fy-fy", s);
            pattern = stuff(pattern, "fy", s);
        }
        pattern = stuff(pattern, "yyyy", String.valueOf(DateUtility.getYear(date)));
        pattern = stuff(pattern, "yy", (String.valueOf(DateUtility.getYear(date))).substring(2));
        pattern = stuff(pattern, "mm",
                StringUtility.padLeft(String.valueOf(DateUtility.getMonth(date)), 2, '0'));
        pattern = stuff(pattern, "m", String.valueOf(DateUtility.getMonth(date)));
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
            return String.valueOf(serial);
        }
        return getNumber(tm, serial, date, sp.pattern);
    }

    /**
     * Get the number string appropriately stuffed as per the pattern.
     *
     * @param patternName Name of the pattern.
     * @param systemEntity System entity.
     * @param serial Number to stuff in.
     * @param date Date to stuff in.
     * @return Stuffed number that can be generally used as a unique reference.
     */
    public static String getNumber(String patternName, SystemEntity systemEntity, long serial, Date date) {
        SerialPattern sp = get(patternName);
        if(sp == null) {
            return String.valueOf(serial);
        }
        return getNumber(systemEntity, serial, date, sp.pattern);
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
        String one = String.valueOf(c), two = one + one;
        if(!pattern.contains(one)) {
            return pattern;
        }
        if(pattern.indexOf(c) == pattern.lastIndexOf(c)) { // Only one c
            pattern = pattern.replace(one, String.valueOf(serial));
        } else {
            int count = StringUtility.getCharCount(pattern, c);
            while(pattern.contains(two)) {
                pattern = pattern.replace(two, one);
            }
            if(pattern.indexOf(c) == pattern.lastIndexOf(c)) { // Occurrence of c is not scattered
                pattern = pattern.replace(one, StringUtility.padLeft(String.valueOf(serial), count, '0'));
            }
        }
        return pattern;
    }
}
