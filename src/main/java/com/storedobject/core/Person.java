package com.storedobject.core;

import com.storedobject.common.DateUtility;
import com.storedobject.core.annotation.Column;

import java.math.BigInteger;
import java.sql.Date;
import java.sql.ResultSet;
import java.util.*;
import java.util.function.Function;

public final class Person extends StoredObject implements HasContacts, Comparable<Person>, HasName, HasShortName,
        Notifye {

    private final static String[] genderValues = {
            "Male", "Female", "Transgender"
    };
    private static String[] titleValues = {
            "Mr.",      // 0
            "Mrs.",     // 1
            "Miss",     // 2
            "Ms.",      // 3
            "Dr.",      // 4
            "Er.",      // 5
            "Mx.",     // 6
            "Sir",      // 7
    };
    private static final int[][] titleIndex = {
            { 0, 4, 5, 7 }, // Male
            { 1, 2, 3, 4, 5 }, // Female
            { 4, 5, 6, 7 }  // Transgender
    };
    private static String[] suffixValues = new String[3];
    static {
        initSalutation();
        initSuffix();
    }
    private final static String[] maritalStatusValues = {
            "Unknown", "Unmarried", "Married", "Divorced", "Widowed", "Living Together", "Common Law Partner"
    };
    private String firstName = "", middleName, lastName, shortName;
    private Date dateOfBirth = DateUtility.today();
    private int maritalStatus = 0, gender = 0, title = 0, suffix = 0;

    /**
     * The constructor to create a person
     *
     * @param name The name of the person (first name, middle name, last name)
     */
    public Person(String name) {
        splitName(name);
    }

    /**
     * The constructor to create a person
     *
     * @param firstName The first name of the person
     * @param middleName The middle name of the person
     * @param lastName The last name of the person
     */
    public Person(String firstName, String middleName, String lastName) {
        splitName(firstName, middleName, lastName);
    }

    /**
     * For internal use only
     */
    public Person() {
    }

    private void splitName(String fName, String mName, String lName) {
        firstName = fName == null ? "" : fName.trim();
        middleName = mName == null ? "" : mName.trim();
        lastName = lName == null ? "" : lName.trim();
    }

    private void splitName(String name) {
        String[] s = name.split(",", -1);
        if(s.length == 1) {
            splitName(name, null, null);
        } else if(s.length == 2) {
            splitName(s[0], null, s[1]);
        } else {
            splitName(s[0], s[1], s[2]);
        }
    }

    public static void columns(Columns columns) {
        columns.add("Title", "int");
        columns.add("FirstName", "text");
        columns.add("MiddleName", "text");
        columns.add("LastName", "text");
        columns.add("Suffix", "int");
        columns.add("ShortName", "text");
        columns.add("Gender", "int");
        columns.add("DateOfBirth", "date");
        columns.add("MaritalStatus", "int");
    }

    public static void indices(Indices indices) {
        indices.add("lower(LastName)", false);
        indices.add("lower(FirstName)", false);
        indices.add("lower(ShortName)", false);
    }

    public static String[] displayColumns() {
        return new String[] { "FirstName", "LastName" };
    }

    public static String[] browseColumns() {
        return new String[] { "Title", "FirstName", "MiddleName", "LastName" };
    }

    /**
     * @return Returns the title values in the local language
     */
    public static String[] getTitleValues() {
        return titleValues;
    }

    /**
     * Get the title values for a particular gender in the local language
     *
     * @param gender The gender value for which title values to be returned
     * @return Returns the title values in the local language. Null will be returned if the gender value passed is invalid.
     */
    public static String[] getTitleValues(int gender) {
        try {
            String[] s = new String[titleIndex[gender].length];
            for(int i = 0; i < titleIndex[gender].length; i++) {
                s[i] = titleValues[titleIndex[gender][i]];
            }
            return s;
        } catch(Exception ignored) {
        }
        return null;
    }

    /**
     * @return Returns the title value of this person in the local language
     */
    public String getTitleValue() {
        return titleValues[title];
    }

    /**
     * @return Returns the title of this person
     */
    public int getTitle() {
        return title;
    }

    /**
     * Sets the title value of this person. Side effect: Setting certain title values may change the gender of the person!
     *
     * @param title The title value to set
     * @exception Invalid_Value If the title value is out-of-range
     */
    public void setTitle(int title) throws Invalid_Value {
        try {
            for(int g = 0; g < 3; g++) {
                for(int i = 0; i < titleIndex[gender].length; i++) {
                    if(title == titleIndex[gender][i]) {
                        this.title = title;
                        return;
                    }
                }
                ++gender;
                if(gender == 3) {
                    gender = 0;
                }
            }
        } catch(Exception ignored) {
        }
        throw new Invalid_Value("Title = " + title);
    }

    /**
     * Get the name of the person
     *
     * @return The name of the person as First Name + Middle Name + Last Name
     */
    @Override
    public String getName() {
        String s = firstName;
        if(s == null) {
            s = "";
        }
        if(middleName != null && !middleName.isBlank()) {
            s += " " + middleName;
        }
        if(lastName != null && !lastName.isBlank()) {
            s += " " + lastName;
        }
        return s;
    }

    /**
     * Set the name of the person
     *
     * @param name The name (first name, middle name, last name)
     */
    public void setName(String name) {
        splitName(name);
    }

    /**
     * Set the first name of the person
     *
     * @param name The first name
     */
    public void setFirstName(String name) {
        firstName = name == null ? "" : name.trim();
    }

    /**
     * Get the first name of the person
     *
     * @return The first name
     */
    @Column(style = "(camel)")
    public String getFirstName() {
        return firstName;
    }

    /**
     * Set the middle name of the person
     *
     * @param name The middle name
     */
    public void setMiddleName(String name) {
        middleName = name == null ? "" : name.trim();
    }

    /**
     * Get the middle name of the person
     *
     * @return The middle name
     */
    @Column(required = false, style = "(camel)")
    public String getMiddleName() {
        return middleName;
    }

    /**
     * Set the last name of the person
     *
     * @param name The last name
     */
    public void setLastName(String name) {
        lastName = name == null ? "" : name.trim();
    }

    /**
     * Get the last name of the person
     *
     * @return The last name
     */
    @Column(required = false, style = "(camel)")
    public String getLastName() {
        return lastName;
    }

    /**
     * Set the short name of the person
     *
     * @param name The short name
     */
    public void setShortName(String name) {
        shortName = name == null ? "" : name.trim();
    }

    /**
     * Get the short name of the person
     *
     * @return The short name
     */
    @Override
    @Column(required = false, style = "(camel)")
    public String getShortName() {
        return StringUtility.isWhite(shortName) ? (StringUtility.isWhite(lastName) ? firstName : lastName) : shortName;
    }

    /**
     * Gets the list of suffix values.
     *
     * @return Array of suffix values.
     */
    public static String[] getSuffixValues() {
        return suffixValues;
    }

    /**
     * Get the suffix value for the index passed.
     *
     * @param suffix Suffix index
     * @return The suffix value for the index passed.
     */
    public static String getSuffixValue(int suffix) {
        return suffixValues[suffix % suffixValues.length];
    }

    /**
     * Gets the suffix value of this person.
     *
     * @return Suffix value of this person.
     */
    public String getSuffixValue() {
        return getSuffixValue(suffix);
    }

    /**
     * Gets the suffix of this person.
     *
     * @return Suffix of this person.
     */
    public int getSuffix() {
        return suffix;
    }

    /**
     * Sets the suffix.
     *
     * @param suffix Suffix to set.
     */
    public void setSuffix(int suffix) {
        this.suffix = suffix % suffixValues.length;
    }

    /**
     * Sets the title value of this person. A gender-specific index is passed as the parameter. This method is useful
     * to pass an index value selected from the array returned by getTitleValues(int gender) method.
     *
     * @param index Index to the gender-specific title values
     * @exception Invalid_Value If the index value is out-of-range
     * @see #getTitleValues(int gender)
     */
    public void setGenderSpecificTitle(int index) throws Invalid_Value {
        try {
            title = titleIndex[gender][index];
            return;
        } catch(Exception ignored) {
        }
        throw new Invalid_Value();
    }

    /**
     * @return Returns the gender values in the local language
     */
    public static String[] getGenderValues() {
        return genderValues;
    }

    /**
     * Set the gender of the person. Side effect: Title value may change as per the gender value.
     *
     * @param gender 0 for Male, 1 for Female, 2 for Transgender
     * @exception Invalid_Value If the value is out-of-range
     */
    public void setGender(int gender) throws Invalid_Value {
        if(gender < 0 || gender >= genderValues.length) {
            throw new Invalid_Value();
        }
        this.gender = gender;
        for(int i = 0; i < titleIndex[gender].length; i++) {
            if(title == titleIndex[gender][i]) {
                return;
            }
        }
        title = gender == 0 ? 0 : (gender == 1 ? 3 : 6);
    }

    /**
     * Get the gender value
     *
     * @return The gender value. 0 for Male, 1 for Female
     */
    public int getGender() {
        return gender;
    }

    /**
     * Get the gender value as a String in the local language
     *
     * @return The gender value as a String
     */
    public String getGenderValue() {
        return genderValues[gender];
    }

    /**
     * Set the date of birth
     *
     * @param dateOfBirth Date of birth to be set
     */
    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = new Date(dateOfBirth.getTime());
    }

    /**
     * Get the date of birth
     *
     * @return Date of birth
     */
    public Date getDateOfBirth() {
        return new Date(dateOfBirth.getTime());
    }

    /**
     * See if this person is a minor
     *
     * @return true if the person is a minor
     */
    public boolean isMinor() {
        return isMinor(DateUtility.today());
    }

    /**
     * See if this person is a minor as of a given day
     *
     * @param date As of this date.
     * @return true if the person is a minor
     */
    public boolean isMinor(java.util.Date date) {
        if(this.dateOfBirth == null) {
            return true;
        }
        GregorianCalendar dateOfBirth = new GregorianCalendar(), asOf = new GregorianCalendar();
        dateOfBirth.setTime(this.dateOfBirth);
        asOf.setTime(date);
        int t = asOf.get(Calendar.YEAR) - dateOfBirth.get(Calendar.YEAR);
        if(t < 18) {
            return true;
        }
        if(t > 18) {
            return false;
        }
        if(asOf.get(Calendar.MONTH) > dateOfBirth.get(Calendar.MONTH)) {
            return false;
        }
        if(asOf.get(Calendar.MONTH) < dateOfBirth.get(Calendar.MONTH)) {
            return true;
        }
        return asOf.get(Calendar.DATE) <= dateOfBirth.get(Calendar.DATE);
    }

    /**
     * Gets the age of this person as of now.
     *
     * @return Age as of now
     */
    public int getAge() {
        return getAge(new Date(System.currentTimeMillis()));
    }

    /**
     * Gets the age of this person.
     *
     * @param date Date as of age is computed.
     * @return The age as of the day passed.
     */
    public int getAge(java.util.Date date) {
        if(dateOfBirth == null) {
            return -1;
        }
        GregorianCalendar dateOfBirth = new GregorianCalendar(), asOf = new GregorianCalendar();
        dateOfBirth.setTime(this.dateOfBirth);
        asOf.setTime(date);
        return asOf.get(Calendar.YEAR) - dateOfBirth.get(Calendar.YEAR);
    }

    /**
     * @return Returns the marital status values in the local language
     */
    public static String[] getMaritalStatusValues() {
        return maritalStatusValues;
    }

    /**
     * Set the marital status of the person
     *
     * @param maritalStatus Marital status value
     * @exception Invalid_Value If the value is out-of-range
     */
    public void setMaritalStatus(int maritalStatus) throws Invalid_Value {
        if(maritalStatus < 0 || maritalStatus >= maritalStatusValues.length) {
            throw new Invalid_Value();
        }
        this.maritalStatus = maritalStatus;
    }

    /**
     * Get the marital status value
     *
     * @return The marital status value.
     */
    public int getMaritalStatus() {
        return maritalStatus;
    }

    /**
     * Get the marital status value as a String in the local language
     *
     * @return The marital status value as a String
     */
    public String getMaritalStatusValue() {
        return maritalStatusValues[maritalStatus];
    }

    @Override
    public String toString() {
        String s = getTitleValue() + " " + getName();
        if(suffix > 0) {
            return s + " " + getSuffixValue();
        }
        return s;
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        if(StringUtility.isBlank(firstName) && StringUtility.isBlank(lastName)) {
            if(middleName.isBlank()) {
                throw new Invalid_Value("Name");
            } else {
                firstName = middleName;
                middleName = "";
            }
        }
        if(dateOfBirth == null || dateOfBirth.after(DateUtility.now())) {
            throw new Invalid_Value("Date of Birth");
        }
        if((updated() || deleted()) && getId().get().equals(BigInteger.ONE)) {
            throw new Invalid_State("Not allowed");
        }
        super.validateData(tm);
    }

    public static Person get(String name) {
        name = StringUtility.smoothen(name);
        if(name.isEmpty()) {
            return null;
        }
        name = name.toLowerCase().replace("'", "''").replace('_', ' ');
        Person p;
        String[] names = name.split("\\s");
        switch(names.length) {
            case 0 -> {
                return null;
            }
            case 1 -> {
                p = list(Person.class, "lower(FirstName)='" + name + "' OR lower(LastName)='" + name +
                        "' OR lower(ShortName)='" + name + "'").single(false);
                if(p != null) {
                    return p;
                }
                return list(Person.class, "lower(FirstName) LIKE '" + name + "%' OR lower(LastName) LIKE '"
                        + name + "%' OR lower(ShortName) LIKE '" + name + "%'").single(false);
            }
            case 2 -> {
                p = get(names[0], "Last", names[1]);
                if(p != null) {
                    return p;
                }
                return get(names[0], "Middle", names[1]);
            }
            default -> {
                p = list(Person.class, "lower(FirstName)='" + names[0] + "' AND lower(MiddleName)='"
                        + names[1] + "' AND lower(LastName)='" + names[2] + "'").single(false);
                if(p != null) {
                    return p;
                }
                p = list(Person.class, "lower(FirstName) LIKE '" + names[0]
                        + "%' AND lower(MiddleName) LIKE '" + names[1]
                        + "%' AND lower(LastName) LIKE '" + names[2] + "%'").single(false);
                if(p != null) {
                    return p;
                }
                p = get(names[0] + " " + names[1], "Last", names[2]);
                if(p != null) {
                    return p;
                }
                p = get(names[0] + " " + names[1], "Middle", names[2]);
                if(p != null) {
                    return p;
                }
                p = get(names[0], "Last", names[1] + " " + names[2]);
                if(p != null) {
                    return p;
                }
                return get(names[0], "Middle", names[1] + " " + names[2]);
            }
        }
    }

    private static Person get(String one, String prefix2, String two) {
        Person p;
        String name = one + " " + two;
        p = list(Person.class, "lower(" + "First" + "Name)='" + name + "' OR lower(" + prefix2 + "Name)='"
                + name + "'").single(false);
        if(p != null) {
            return p;
        }
        p = list(Person.class, "lower(" + "First" + "Name) LIKE '" + name + "%' OR lower(" + prefix2
                + "Name) LIKE '" + name + "%'").single(false);
        if(p != null) {
            return p;
        }
        p = list(Person.class, "lower(" + "First" + "Name)='" + one + "' AND lower(" + prefix2 + "Name)='"
                + two + "'").single(false);
        if(p != null) {
            return p;
        }
        return list(Person.class, "lower(" + "First" + "Name) LIKE '" + one + "%' AND lower(" + prefix2
                + "Name) LIKE '" + two + "%'").single(false);
    }

    public static ObjectIterator<Person> list(String name) {
        name = StringUtility.smoothen(name);
        if(name.isEmpty()) {
            return ObjectIterator.create();
        }
        name = name.toLowerCase().replace("'", "''").replace('_', ' ');
        String[] names = name.split("\\s");
        switch(names.length) {
            case 1, 2 -> {
                ObjectIterator<Person> p = list(Person.class, "lower(FirstName) LIKE '" + name
                        + "%' OR lower(LastName) LIKE '" + name + "%' OR lower(ShortName) LIKE '" + name + "%'");
                if(names.length == 1) {
                    return p;
                }
                return p.add(list(Person.class, "lower(FirstName) LIKE '" + names[0]
                        + "%' AND lower(LastName) LIKE '" + names[1] + "%'"));
            }
            case 3 -> {
                return list(Person.class, "lower(FirstName) LIKE '" + names[0]
                        + "%' AND lower(MiddleName) LIKE '" + names[1] + "%' AND lower(LastName) LIKE '"
                        + names[2] + "%'");
            }
        }
        return ObjectIterator.create();
    }

    @Override
    public int compareTo(Person person) {
        if(firstName.equalsIgnoreCase(person.firstName)) {
            if(middleName.equalsIgnoreCase(person.middleName)) {
                return lastName.compareToIgnoreCase(person.lastName);
            }
            return middleName.compareToIgnoreCase(person.middleName);
        }
        return firstName.compareToIgnoreCase(person.firstName);
    }

    private static void reinit() {
        String[] titles = new String[8];
        System.arraycopy(titleValues, 0, titles, 0, titles.length);
        titleValues = titles;
        int[] i;
        i = new int[4];
        System.arraycopy(titleIndex[0], 0, i, 0, i.length);
        titleIndex[0] = i;
        i = new int[5];
        System.arraycopy(titleIndex[1], 0, i, 0, i.length);
        titleIndex[1] = i;
        i = new int[4];
        System.arraycopy(titleIndex[2], 0, i, 0, i.length);
        titleIndex[2] = i;
        initSalutation();
        suffixValues = new String[] { "", "Jr", "Sr." };
        initSuffix();
    }

    private static void checkSalutation(TransactionManager tm, String salutation, boolean male, boolean female,
                              boolean transgender) throws Exception {
        SystemUser su = tm == null ? null : tm.getUser();
        if(tm == null || !(su.isAdmin() || su.isAppAdmin())) {
            throw new SOException("No authority");
        }
        if(salutation.isEmpty() || (!male && !female && !transgender)) {
            throw new SOException("Invalid salutation");
        }
    }

    public static void addSalutation(TransactionManager tm, String salutation, boolean male, boolean female,
                                     boolean transgender) throws Exception {
        salutation = salutation == null ? "" : salutation.strip();
        checkSalutation(tm, salutation, male, female, transgender);
        for(String s: titleValues) {
            if(salutation.equalsIgnoreCase(s)) {
                throw new SOException("Duplicate salutation");
            }
        }
        RawSQL sql = new RawSQL("SELECT Count(*) FROM core.Salutation");
        try {
            sql.execute();
            ResultSet rs = sql.getResult();
            int code = rs.getInt(1) + 8;
            sql.executeUpdate("INSERT INTO core.Salutation(Salutation,Male,Female,Transgender,Code) VALUES('"
                    + salutation.replace("'", "''") + "'," + male + "," + female + ","
                    + transgender + "," + code + ")");
            reinit();
        } finally {
            sql.close();
        }
    }

    public static void updateSalutation(TransactionManager tm, int code, String salutation, boolean male, boolean female,
                                        boolean transgender) throws Exception {
        salutation = salutation == null ? "" : salutation.strip();
        checkSalutation(tm, salutation, male, female, transgender);
        if(code <= 7 || code >= titleValues.length) {
            throw new SOException("Invalid salutation code");
        }
        String s;
        for(int i = 0; i < titleValues.length; i++) {
            s = titleValues[i];
            if(i == code) {
                continue;
            }
            if(salutation.equalsIgnoreCase(s)) {
                throw new SOException("Duplicate salutation");
            }
        }
        RawSQL sql = new RawSQL();
        try {
            sql.executeUpdate("UPDATE core.Salutation SET Salutation='"
                    + salutation.replace("'", "''") + "',Male=" + male + ",Female=" + female
                    + ",Transgender=" + transgender + " WHERE Code=" + code);
            reinit();
        } finally {
            sql.close();
        }
    }

    public static void addSuffix(TransactionManager tm, String suffix) throws Exception {
        suffix = checkAuthority(tm, suffix);
        for(String s: suffixValues) {
            if(suffix.equalsIgnoreCase(s)) {
                throw new SOException("Duplicate suffix");
            }
        }
        RawSQL sql = new RawSQL("SELECT Count(*) FROM core.Suffix");
        try {
            sql.execute();
            ResultSet rs = sql.getResult();
            int code = rs.getInt(1) + 3;
            sql.executeUpdate("INSERT INTO core.Suffix(Suffix,Code) VALUES('"
                    + suffix.replace("'", "''") + "'," + code + ")");
            reinit();
        } finally {
            sql.close();
        }
    }

    private static String checkAuthority(TransactionManager tm, String suffix) throws SOException {
        SystemUser su = tm == null ? null : tm.getUser();
        if(tm == null || !(su.isAdmin() || su.isAppAdmin())) {
            throw new SOException("No authority");
        }
        suffix = suffix == null ? "" : suffix.strip();
        return suffix;
    }

    public static void updateSuffix(TransactionManager tm, int code, String suffix) throws Exception {
        suffix = checkAuthority(tm, suffix);
        if(code <= 2 || code >= suffixValues.length) {
            throw new SOException("Invalid suffix code");
        }
        String s;
        for(int i = 0; i < suffixValues.length; i++) {
            s = suffixValues[i];
            if(i == code) {
                continue;
            }
            if(suffix.equalsIgnoreCase(s)) {
                throw new SOException("Duplicate suffix");
            }
        }
        RawSQL sql = new RawSQL();
        try {
            sql.executeUpdate("UPDATE core.Suffix SET Suffix='"
                    + suffix.replace("'", "''") + "' WHERE Code=" + code);
            reinit();
        } finally {
            sql.close();
        }
    }

    private static void initSalutation() {
        RawSQL sql = new RawSQL("SELECT Salutation,Male,Female,Transgender FROM core.Salutation ORDER BY Code");
        try {
            sql.execute();
            if(sql.eoq()) {
                return;
            }
            List<Salutation> salutations = new ArrayList<>();
            ResultSet rs = sql.getResult();
            int code = 8;
            while(!sql.eoq()) {
                salutations.add(new Salutation(code, rs.getString(1), rs.getBoolean(2),
                        rs.getBoolean(3), rs.getBoolean(4)));
                ++code;
                sql.skip();
            }
            if(salutations.isEmpty()) {
                return;
            }
            String[] titles = new String[titleValues.length + salutations.size()];
            System.arraycopy(titleValues, 0, titles, 0, titleValues.length);
            code = 8;
            for(Salutation s: salutations) {
                titles[code++] = s.salutation;
            }
            titleValues = titles;
            titleIndex[0] = add(salutations, s -> s.male, titleIndex[0]);
            titleIndex[1] = add(salutations, s -> s.female, titleIndex[1]);
            titleIndex[2] = add(salutations, s -> s.transgender, titleIndex[2]);
        } catch(Throwable ignored) {
        } finally {
            sql.close();
        }
    }

    private static void initSuffix() {
        RawSQL sql = new RawSQL("SELECT Suffix FROM core.Suffix ORDER BY Code");
        try {
            sql.execute();
            if(sql.eoq()) {
                return;
            }
            List<String> suffixes = new ArrayList<>();
            ResultSet rs = sql.getResult();
            while(!sql.eoq()) {
                suffixes.add(rs.getString(1));
                sql.skip();
            }
            if(suffixes.isEmpty()) {
                return;
            }
            suffixValues = new String[3 + suffixes.size()];
            int i = 3;
            for(String s: suffixes) {
                suffixValues[i++] = s;
            }
        } catch(Throwable ignored) {
        } finally {
            sql.close();
            defaultSuffixes();
        }
    }

    private static void defaultSuffixes() {
        suffixValues[0] = "";
        suffixValues[1] = "Jr.";
        suffixValues[2] = "Sr.";
    }

    private static int[] add(List<Salutation> salutations, Function<Salutation, Boolean> func, int[] list) {
        int size = (int) salutations.stream().filter(func::apply).count();
        int[] newList = new int[list.length + size];
        System.arraycopy(list, 0, newList, 0, list.length);
        int code = list.length;
        for(Salutation s: salutations) {
            if(func.apply(s)) {
                newList[code++] = s.code;
            }
        }
        return newList;
    }

    public static boolean isMaleAllowed(int title) {
        return allowed(title, titleIndex[0]);
    }

    public static boolean isFemaleAllowed(int title) {
        return allowed(title, titleIndex[1]);
    }

    public static boolean isTransgenderAllowed(int title) {
        return allowed(title, titleIndex[2]);
    }

    private static boolean allowed(int title, int[] titleIndex) {
        for(int i: titleIndex) {
            if(title == i) {
                return true;
            }
        }
        return false;
    }

    private record Salutation(int code, String salutation, boolean male, boolean female, boolean transgender) {
    }

    /**
     * Create and send a message to this person.
     * <p>Note: If the template doesn't exist, the default template is used.</p>
     * @param templateName Name of the template to create the message.
     * @param tm Transaction manager.
     * @param messageParameters Parameters for creating message from the associated template.
     * @return True the message is successfully created for delivery.
     */
    @Override
    public boolean notify(String templateName, TransactionManager tm, Object... messageParameters) {
        return MessageTemplate.notify(templateName, tm, ObjectIterator.create(this), messageParameters);
    }

    @Override
    public Contact getContactObject(Id contactTypeId) {
        Contact c = listLinks(Contact.class, "Type=" + contactTypeId).single(false);
        if(c == null) {
            try(ObjectIterator<PersonRole> roles
                        = list(PersonRole.class, "Person=" + getId(), true)) {
                for(PersonRole role: roles) {
                    c = role.listLinks(Contact.class, "Type=" + contactTypeId).single(false);
                    if(c != null) {
                        break;
                    }
                }
            }
        }
        return c;
    }
}