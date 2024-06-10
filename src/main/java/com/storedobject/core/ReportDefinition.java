package com.storedobject.core;

import com.storedobject.common.Executable;
import com.storedobject.common.SORuntimeException;
import com.storedobject.common.StringList;
import com.storedobject.core.annotation.Column;
import com.storedobject.core.annotation.Table;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Table(tab = "Report Definition")
public class ReportDefinition extends Name {

    private static final String[] baseFontSizeValues =
            new String[] {
                    "10", "8", "6",
            };
    private static final String[] orientationValues =
            new String[] {
                    "Portrait", "Landscape",
            };
    private String title;
    private String description = "";
    private boolean printDescription;
    private String dataClass;
    private boolean includeSubclasses;
    private String condition, filter = "";
    private String orderBy;
    private String logicClass;
    private int baseFontSize = 0;
    private int orientation = 0;
    private List<ReportColumnDefinition> columns;
    private Class<? extends StoredObject> dClass;
    private Class<? extends Executable> exClass;
    private Executable executable;
    private Supplier<StringList> customColumnSupplier;

    public ReportDefinition() {
    }

    public static void columns(Columns columns) {
        columns.add("Title", "text");
        columns.add("Description", "text");
        columns.add("PrintDescription", "boolean");
        columns.add("DataClass", "text");
        columns.add("IncludeSubclasses", "boolean");
        columns.add("Condition", "text");
        columns.add("Filter", "text");
        columns.add("OrderBy", "text");
        columns.add("LogicClass", "text");
        columns.add("BaseFontSize", "int");
        columns.add("Orientation", "int");
    }

    public static void indices(Indices indices) {
        indices.add("DataClass", false);
    }

    public static ReportDefinition get(String name) {
        return StoredObjectUtility.get(ReportDefinition.class, "Name", name, true);
    }

    public static ObjectIterator<ReportDefinition> list(String name) {
        return StoredObjectUtility.list(ReportDefinition.class, "Name", name, true);
    }

    public static int hints() {
        return ObjectHint.SMALL_LIST;
    }

    public static String[] links() {
        return new String[] {
                "Report Columns|com.storedobject.core.ReportColumnDefinition|DisplayOrder||0",
        };
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Column(order = 200)
    public String getTitle() {
        return title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(style = "(large)", order = 300)
    public String getDescription() {
        return description;
    }

    public void setPrintDescription(boolean printDescription) {
        this.printDescription = printDescription;
    }

    @Column(order = 400)
    public boolean getPrintDescription() {
        return printDescription;
    }

    public void setDataClass(String dataClass) {
        this.dataClass = dataClass;
    }

    @Column(order = 500)
    public String getDataClass() {
        return dataClass;
    }

    public void setIncludeSubclasses(boolean includeSubclasses) {
        this.includeSubclasses = includeSubclasses;
    }

    @Column(order = 600)
    public boolean getIncludeSubclasses() {
        return includeSubclasses;
    }

    /**
     * Set the condition to be applied while retrieving the object instances from the database.
     *
     * @param condition SQL-style condition.
     */
    public void setCondition(String condition) {
        this.condition = condition;
    }

    /**
     * Set the condition to be applied while retrieving the object instances from the database.
     *
     * @return SQL-style condition.
     */
    @Column(style = "(large)", required = false, order = 700, caption = "SQL-style Condition")
    public String getCondition() {
        return condition;
    }

    /**
     * Set the filter variables with filter conditions.
     * <p>The filter variables are defined in separate lines. Each filter variable can be
     * specified as just the attribute name or the attribute name followed by its comparison indicators.</p>
     * <p>Comparison indicators specify how the comparison is done. It is indicated by 2 characters - "EQ" for "equal
     * to", "LT" for "less than", "GT" for "greater than", "LE" for "less than or equal to", "NE" for "not equal to"
     * etc. If an invalid comparison indicator is specified or if no indicator is specified, "EQ" is assumed. The
     * comparison indicator must be separated from the field attribute by a slash "/" character. A star "*" (asterisk
     * character) may be added to indicate that the field is mandatory when value is being accepted from the user.</p>
     * <pre>
     * Example: (Assuming Person as the data class)
     * Age/LT*
     * Example: (Assuming Person as the data class)
     * Gender/EQ
     * Age/GT
     * </pre>
     * <p>The captions of the filter fields may be customized by adding " AS " followed by the customized caption.</p>
     * <pre>
     * Example: (Assuming Person as the data class)
     * Gender/EQ AS Gender equal to
     * Age/GT AS Age greater than
     * </pre>
     * <p>Sub-fields may be accessed using the platform-specific "dot" notation.</p>
     * <pre>
     * Example: (Assuming SystemUser as the data class)
     * Person.Gender/EQ AS Gender equal to
     * </pre>
     *
     * @param filter Filter variable details.
     */
    public void setFilter(String filter) {
        this.filter = filter;
    }

    /**
     * Get the filter variables with comparison indicators. (See {@link #setFilter(String)}).
     *
     * @return Filter variable details.
     */
    @Column(style = "(large)", required = false, order = 800, caption = "Attributes with Comparison Indicators")
    public String getFilter() {
        return filter;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    @Column(required = false, order = 900)
    public String getOrderBy() {
        return orderBy;
    }

    public void setLogicClass(String logicClass) {
        this.logicClass = logicClass;
    }

    @Column(required = false, order = 1000)
    public String getLogicClass() {
        return logicClass;
    }

    public void setBaseFontSize(int baseFontSize) {
        this.baseFontSize = baseFontSize;
    }

    @Column(order = 1100)
    public int getBaseFontSize() {
        return baseFontSize;
    }

    public static String[] getBaseFontSizeValues() {
        return baseFontSizeValues;
    }

    public static String getBaseFontSizeValue(int value) {
        String[] s = getBaseFontSizeValues();
        return s[value % s.length];
    }

    public String getBaseFontSizeValue() {
        return getBaseFontSizeValue(baseFontSize);
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    @Column(order = 1200)
    public int getOrientation() {
        return orientation;
    }

    public static String[] getOrientationValues() {
        return orientationValues;
    }

    public static String getOrientationValue(int value) {
        String[] s = getOrientationValues();
        return s[value % s.length];
    }

    public String getOrientationValue() {
        return getOrientationValue(orientation);
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        if (StringUtility.isWhite(title)) {
            throw new Invalid_Value("Title");
        }
        if (StringUtility.isWhite(description)) {
            throw new Invalid_Value("Description");
        }
        if (StringUtility.isWhite(dataClass)) {
            throw new Invalid_Value("Data Class");
        }
        super.validateData(tm);
        setName(toCode(getName()));
    }

    public Class<? extends StoredObject> getClassForData() {
        if(dClass == null) {
            try {
                //noinspection unchecked
                dClass = (Class<? extends StoredObject>) JavaClassLoader.getLogic(dataClass);
            } catch(ClassNotFoundException e) {
                throw new SORuntimeException("No data class found: " + dataClass);
            }
        }
        return dClass;
    }

    public Class<? extends Executable> getClassForLogic(boolean excel) {
        return classForLogic("com.storedobject.report.ObjectList" + (excel ? "Excel" : ""));
    }

    private Class<? extends Executable> classForLogic(String name) {
        if(executable != null) {
            return executable.getClass();
        }
        if(exClass == null) {
            if(logicClass == null || logicClass.isBlank()) {
                logicClass = name;
            }
            try {
                //noinspection unchecked
                exClass = (Class<? extends Executable>) JavaClassLoader.getLogic(logicClass);
            } catch(ClassNotFoundException e) {
                throw new SORuntimeException("No logic class found: " + logicClass);
            }
        }
        return exClass;
    }

    public final List<ReportColumnDefinition> getColumns() {
        if(columns != null) {
            return columns;
        }
        getClassForData();
        StringList custom = customColumnSupplier == null ? null : customColumnSupplier.get();
        if(custom != null && custom.isEmpty()) {
            custom = null;
        }
        if(custom != null || isVirtual()) {
            columns = new ArrayList<>();
        } else {
            columns = listLinks(ReportColumnDefinition.class, null, "DisplayOrder").toList();
        }
        if(columns.isEmpty() || (columns.size() == 1 && "RowCount".equals(columns.get(0).getAttribute()))) {
            if(custom == null) {
                custom = columns();
            }
            custom.forEach(c -> {
                ReportColumnDefinition rcd = new ReportColumnDefinition();
                rcd.setAttribute(c);
                rcd.makeVirtual();
                columns.add(rcd);
            });
        }
        sanitizeCols();
        return columns;
    }

    StringList columns() {
        return ClassAttribute.get(dClass).getAttributes();
    }

    private void sanitizeCols() {
        if(columns.isEmpty()) {
            ReportColumnDefinition rcd = new ReportColumnDefinition();
            rcd.makeVirtual();
            rcd.setCaption("");
            rcd.value = StoredObject::toDisplay;
            columns.add(rcd);
            return;
        }
        String s;
        int p;
        for(ReportColumnDefinition c: columns) {
            if(c.isVirtual()) {
                s = c.getAttribute();
                p = s.toLowerCase().indexOf(" as ");
                if(p > 0) {
                    c.setCaption(s.substring(p + 4));
                } else {
                    c.setCaption(StringUtility.makeLabel(s));
                }
            } else {
                s = c.getCaption();
                if(s == null || s.isBlank()) {
                    c.setCaption(StringUtility.makeLabel(c.getAttribute()));
                }
            }
            Method method = Utility.getMethod(getClassForLogic(false), "get" + c.getAttribute(), dClass);
            if(method != null) {
                c.value = o -> {
                    try {
                        return method.invoke(executable, o);
                    } catch(Throwable ignored) {
                    }
                    return "?";
                };
            }
            if(c.value == null) {
                StoredObjectUtility.MethodList m;
                try {
                    m = StoredObjectUtility.createMethodList(dClass, c.getAttribute());
                    m.stringifyTail();
                    c.value = o -> m.invoke(o, false);
                } catch(Throwable e) {
                    c.value = o -> c.getAttribute() + "?";
                }
            }
        }
    }

    public static ReportDefinition create(Class<? extends StoredObject> dataClass, String... columns) {
        return create(dataClass, StringList.create(columns));
    }

    public static ReportDefinition create(Class<? extends StoredObject> dataClass, Iterable<String> columns) {
        StringList cols = columns == null ? StringList.EMPTY : StringList.create(columns);
        ReportDefinition rd = new ReportDefinition() {
            @Override
            StringList columns() {
                return cols.isEmpty() ? super.columns() : cols;
            }
        };
        rd.makeVirtual();
        rd.dataClass = dataClass.getName();
        rd.dClass = dataClass;
        rd.title = StringUtility.makeLabel(dataClass) + " List";
        return rd;
    }

    public void setExecutable(Executable executable) {
        this.executable = executable;
    }

    public void setCustomColumnSupplier(Supplier<StringList> customColumnSupplier) {
        this.customColumnSupplier = customColumnSupplier;
    }

    public <T extends StoredObject> ObjectIterator<T> listObjects() {
        return listObjects(null, getOrderBy());
    }

    public <T extends StoredObject> ObjectIterator<T> listObjects(String extraCondition) {
        return listObjects(extraCondition, getOrderBy());
    }

    public <T extends StoredObject> ObjectIterator<T> listObjects(String extraCondition, String orderBy) {
        if(extraCondition == null || extraCondition.isBlank()) {
            extraCondition = getCondition();
        } else {
            String c = getCondition();
            if(c != null && !c.isBlank()) {
                extraCondition = "(" + extraCondition + ") AND " + c;
            }
        }
        //noinspection unchecked
        return StoredObject.list((Class<T>)getClassForData(), extraCondition, orderBy, getIncludeSubclasses());
    }
}
