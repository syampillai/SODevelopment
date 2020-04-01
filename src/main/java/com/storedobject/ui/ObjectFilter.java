package com.storedobject.ui;

import com.storedobject.common.MathUtility;
import com.storedobject.common.SORuntimeException;
import com.storedobject.common.Storable;
import com.storedobject.common.StringList;
import com.storedobject.core.*;
import com.storedobject.vaadin.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

@SuppressWarnings("serial")
public class ObjectFilter<T extends StoredObject> extends Form implements ObjectSearchBuilder<T> {

    private final static String[] selectText = new String[] {
            "Starting with", "Not starting with", "Equal to", "Not equal to", "Contains*"
    };
    private final static String[] selectNumber = new String[] {
            "Equal to", "Not equal to", "Greater than", "Greater than or equal to", "Less than", "Less than or equal to"
    };
    private final static String[] selectDate = new String[] {
            "Equal to", "Not equal to", "After", "On or after", "Before", "On or before"
    };
    private final static String[] selectIndex = new String[] {
            "Equal to", "Not equal to"
    };
    private final static String[] selectChoices = new String[] {
            "Equal to", "Include all", "Include any"
    };
    private final static String[] selectBoolean = new String[] {
            "No", "Yes"
    };
    private Class<T> objectClass;
    private String[] columns;
    private Function<T, ?>[] functions;
    private Caption[] captions;
    private Checkbox[] checks;
    private ChoiceField[] combos;
    private HasValue<?, ?>[] fields;
    private boolean autoChecked = false;

    public ObjectFilter(Class<T> objectClass) {
        this(objectClass, null);
    }

    public ObjectFilter(Class<T> objectClass, StringList columns) {
        this.objectClass = objectClass;
        if(columns == null || columns.size() == 0) {
            columns = StoredObjectUtility.searchColumns(objectClass);
        }
        this.columns = removeContacts(columns);
        buildFields();
    }

    @Override
    protected HasComponents createContainer() {
        return new ButtonLayout();
    }

    @SuppressWarnings("unchecked")
    public static <O extends StoredObject> ObjectFilter<O> create(Class<O> objectClass, StringList columns) {
        try {
            Class<?> logic = JavaClassLoader.getLogic(ApplicationServer.createLogicName(Application.getPackageTag(), objectClass, "Filter"));
            Constructor<?> c = logic.getConstructor(StringList.class);
            if(c != null) {
                return (ObjectFilter<O>) c.newInstance(new Object[] { columns });
            }
            c = logic.getConstructor((Class<?>[])null);
            if(c != null) {
                return (ObjectFilter<O>) c.newInstance((Object[])null);
            }
        } catch (Throwable ignored) {
        }
        return new ObjectFilter<>(objectClass, columns);
    }

    @Override
    public int getSearchFieldCount() {
        return fields.length;
    }

    private static String[] removeContacts(StringList cols) {
        String contact = "Contact.";
        int count = 0;
        for(String s: cols) {
            if(s.contains(contact)) {
                ++count;
            }
        }
        if(count == 0) {
            return cols.array();
        }
        String[] c = new String[cols.size() - count];
        int i = 0;
        for(String s: cols) {
            if(s.contains(contact)) {
                continue;
            }
            c[i++] = s;
        }
        return c;
    }

    @Override
    protected void attachField(String fieldName, HasValue<?, ?> field) {
    }

    @SuppressWarnings("unchecked")
    private void buildFields() {
        int i, n;
        String[] labels = new String[columns.length];
        for(i = 0; i < labels.length; i++) {
            labels[i] = StringUtility.makeLabel(columns[i]);
        }
        ArrayList<Function<T, ?>> functions = new ArrayList<>();
        StoredObjectUtility.MethodList[] columnMethods = StoredObjectUtility.createMethodLists(objectClass, StringList.create(columns));
        n = 0;
        Class<?> c;
        for(i = 0; i < columnMethods.length; i++) {
            if(columnMethods[i] == null || (c = columnMethods[i].getReturnType()).getComponentType() != null) {
                continue;
            }
            if(Money.class == c || Date.class == c || String.class == c || byte.class == c || int.class == c || long.class == c ||
                    short.class == c || double.class == c || float.class == c || boolean.class == c || Boolean.class == c ||
                    (Number.class).isAssignableFrom(c) || (Quantity.class).isAssignableFrom(c)) {
                ++n;
            } else {
                columnMethods[i] = null;
            }
        }
        captions = new Caption[n];
        checks = new Checkbox[n];
        combos = new ChoiceField[n];
        fields = new HasValue[n];
        n = 0;
        HorizontalLayout span;
        for(i = 0; i < columnMethods.length; i++) {
            if(columnMethods[i] == null) {
                labels[i] = columns[i] = null;
                continue;
            }
            span = new HorizontalLayout();
            add(span);
            span.add(checks[n] = new Checkbox(""));
            span.setVerticalComponentAlignment(FlexComponent.Alignment.CENTER, checks[n]);
            span.add(captions[n] = new Caption(labels[i]));
            c = columnMethods[i].getReturnType();
            if(java.sql.Date.class == c) {
                fields[n] = new DateField();
                combos[n] = new ChoiceField(selectDate);
            } else if(ComputedDate.class == c) {
                fields[n] = new CDateField();
                combos[n] = new ChoiceField(selectDate);
            } else if(ComputedMinute.class == c) {
                fields[n] = new CMinutesField();
                combos[n] = new ChoiceField(selectNumber);
            } else if(ComputedInteger.class == c) {
                fields[n] = new CIntegerField();
                combos[n] = new ChoiceField(selectNumber);
            } else if(ComputedLong.class == c) {
                fields[n] = new CLongField();
                combos[n] = new ChoiceField(selectNumber);
            } else if(ComputedDouble.class == c) {
                fields[n] = new CDoubleField();
                combos[n] = new ChoiceField(selectNumber);
            } else if(String.class == c) {
                fields[n] = new TextField();
                combos[n] = new ChoiceField(selectText);
            } else if(Money.class == c) {
                fields[n] = new MField();
                combos[n] = new ChoiceField(selectNumber);
            } else if(boolean.class == c || Boolean.class == c) {
                fields[n] = new Caption("");
                combos[n] = new ChoiceField(selectBoolean);
            } else if(Rate.class == c || float.class == c || double.class == c || Float.class == c || Double.class == c) {
                fields[n] = new DoubleField(18, 6);
                combos[n] = new ChoiceField(selectNumber);
            } else if((Quantity.class).isAssignableFrom(c)) {
                fields[n] = new QField();
                combos[n] = new ChoiceField(selectNumber);
            } else if(int.class == c || long.class == c || short.class == c || (Number.class).isAssignableFrom(c)) {
                if(int.class == c) {
                    try {
                        Method listM = objectClass.getMethod(columnMethods[i].getName() + "Values");
                        if(Modifier.isStatic(listM.getModifiers()) && listM.getReturnType() == String[].class) {
                            List<String> list = new StringUtility.List((String[])listM.invoke(null, (Object[])null));
                            fields[n] = new ChoiceField(null, list);
                            combos[n] = new ChoiceField(selectIndex);
                        }
                    } catch(Exception ignore) {
                    }
                }
                if(fields[n] == null && (int.class == c || long.class == c)) {
                    try {
                        Method listM = objectClass.getMethod(columnMethods[i].getName() + "BitValues");
                        if(Modifier.isStatic(listM.getModifiers()) && listM.getReturnType() == String[].class) {
                            List<String> list = new StringUtility.List((String[])listM.invoke(null, (Object[])null));
                            fields[n] = new ChoicesField(null, list);
                            combos[n] = new ChoiceField(selectChoices);
                        }
                    } catch(Exception ignore) {
                    }
                }
                if(fields[n] == null) {
                    fields[n] = new LongField();
                    combos[n] = new ChoiceField(selectNumber);
                }
            }
            span.add(combos[n]);
            span.add((Component)fields[n]);
            addField(combos[n]);
            addField(fields[n]);
            checks[n].setTabIndex(-1);
            combos[n].setTabIndex(-1);
            ++n;
            functions.add(function(columnMethods[i]));
        }
        columns = StringUtility.removeNulls(columns);
        for(i = 0; i < columns.length; i++) {
            n = columns[i].toUpperCase().indexOf((" AS "));
            if(n > 0) {
                columns[i] = columns[i].substring(0, n);
            }
            if(columns[i].indexOf('.') < 0) {
                columns[i] = "T." + columns[i];
            }
        }
        this.functions = new Function[functions.size()];
        for(i = 0; i < this.functions.length; i++) {
            this.functions[i] = functions.get(i);
        }
        adjustCaptionHeight();
    }

    private Function<T, ?> function(StoredObjectUtility.MethodList m) {
        Class<?> type = m.getReturnType();
        if(type == String.class) {
            return (Function<T, String>) t -> ((String)m.invoke(t)).toLowerCase();
        }
        if(type == Money.class) {
            return (Function<T, BigDecimal>) t -> ((Money)m.invoke(t)).getValue();
        }
        if(Quantity.class.isAssignableFrom(type)) {
            return (Function<T, BigDecimal>) t -> ((Quantity)m.invoke(t)).getValue();
        }
        if(type == boolean.class || type == Boolean.class) {
            return (Function<T, Boolean>) t -> (Boolean)m.invoke(t);
        }
        if(type == double.class || type == Double.class || type == float.class || type == Float.class) {
            return (Function<T, BigDecimal>) t -> BigDecimal.valueOf((Double)m.invoke(t));
        }
        if(type == ComputedInteger.class) {
            return (Function<T, Integer>) t -> ((ComputedInteger)m.invoke(t)).getValue();
        }
        if(type == ComputedLong.class) {
            return (Function<T, BigDecimal>) t -> new BigDecimal(((ComputedLong)m.invoke(t)).getValue());
        }
        if(type == ComputedDouble.class) {
            return (Function<T, BigDecimal>) t -> BigDecimal.valueOf(((ComputedDouble)m.invoke(t)).getValue());
        }
        return (Function<T, Object>) m::invoke;
    }

    @Override
    public boolean addSearchField(String fieldName) {
        return false;
    }

    @Override
    public boolean removeSearchField(String fieldName) {
        if(fieldName.indexOf('.') < 0) {
            fieldName = "T." + fieldName;
        }
        for(int i = 0; i < columns.length; i++) {
            if(columns[i].equals(fieldName)) {
                removeSearchField(i);
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    private void removeSearchField(int index) {
        int k, i;
        String[] newColumns = new String[columns.length - 1];
        i = 0;
        for(k = 0; k < columns.length; k++) {
            if(k == index) {
                continue;
            }
            newColumns[i++] = columns[k];
        }
        columns = newColumns;
        Checkbox[] newChecks = new Checkbox[checks.length - 1];
        i = 0;
        for(k = 0; k < checks.length; k++) {
            if(k == index) {
                continue;
            }
            newChecks[i++] = checks[k];
        }
        checks = newChecks;
        ChoiceField[] newCombos = new ChoiceField[combos.length - 1];
        i = 0;
        for(k = 0; k < combos.length; k++) {
            if(k == index) {
                continue;
            }
            newCombos[i++] = combos[k];
        }
        combos = newCombos;
        HasValue[] newFields = new HasValue[fields.length - 1];
        i = 0;
        for(k = 0; k < fields.length; k++) {
            if(k == index) {
                continue;
            }
            newFields[i++] = fields[k];
        }
        fields = newFields;
        Caption[] newCaptions = new Caption[captions.length - 1];
        i = 0;
        for(k = 0; k < captions.length; k++) {
            if(k == index) {
                continue;
            }
            newCaptions[i++] = captions[k];
        }
        captions = newCaptions;
        Function<T, ?>[] newFunctions = new Function[functions.length - 1];
        i = 0;
        for(k = 0; k < functions.length; k++) {
            if(k == index) {
                continue;
            }
            newFunctions[i++] = functions[k];
        }
        functions = newFunctions;
        removeAll();
        for(i = 0; i < columns.length; i++) {
            add(checks[i]);
            add(captions[i]);
            add(combos[i]);
            add((Component)fields[i]);
        }
        adjustCaptionHeight();
    }

    private void adjustCaptionHeight() {
        int m = 0, w;
        for(Caption caption: captions) {
            if((w = caption.getValue().length()) > m) {
                m = w;
            }
        }
        for(Caption caption: captions) {
            caption.setWidth(m + "ch");
        }
    }

    private boolean autoCheck() {
        if(autoChecked) {
            return false;
        }
        for(Checkbox cb: checks) {
            if(cb.getValue()) {
                return false;
            }
        }
        boolean checked = false;
        String s;
        Object value;
        int i, p;
        for(i = 0; i < checks.length; i++) {
            p = combos[i].getValue();
            if(p > 0) {
                checks[i].setValue(true);
                checked = true;
                continue;
            }
            if(fields[i].getClass() == TextField.class) {
                s = fields[i].getValue().toString().trim().toLowerCase();
                if(s.length() > 0) {
                    checks[i].setValue(true);
                    checked = true;
                }
            } else if(fields[i] instanceof ChoicesField) {
                if(((ChoicesField)fields[i]).getValue() > 0) {
                    checks[i].setValue(true);
                    checked = true;
                }
            } else {
                if(fields[i] instanceof ChoiceField) {
                    if(((ChoiceField)fields[i]).getValue() > 0) {
                        checks[i].setValue(true);
                        checked = true;
                    }
                } else {
                    value = fields[i].getValue();
                    if(!isEmptyCheat(value)) {
                        checks[i].setValue(true);
                        checked = true;
                    }
                }
            }
        }
        return checked;
    }

    private StringBuilder buildCriteria() {
        String s = null;
        Object value;
        int i, p;
        StringBuilder b, sb = new StringBuilder();
        for(i = 0; i < checks.length; i++) {
            if(!checks[i].getValue()) {
                continue;
            }
            p = combos[i].getValue();
            b = new StringBuilder();
            if(fields[i].getClass() == Caption.class) {
                if(p == 0) {
                    b.append("NOT ");
                }
                b.append(columns[i]);
            } else if(fields[i].getClass() == TextField.class) {
                s = fields[i].getValue().toString().trim().toLowerCase();
                if(s.indexOf('\'') >= 1) {
                    s = s.replace("'", "''");
                }
                b.append("lower(").append(columns[i]).append(")");
                switch(p) {
                    case 0:
                        b.append(" LIKE '").append(s).append("%'");
                        break;
                    case 1:
                        b.append(" NOT LIKE '").append(s).append("%'");
                        break;
                    case 2:
                        b.append("='").append(s).append("'");
                        break;
                    case 3:
                        b.append("<>'").append(s).append("'");
                        break;
                    case 4:
                        b.append(" LIKE '%").append(s).append("%'");
                        break;
                }
            } else if(fields[i] instanceof ChoicesField) {
                s = "" + ((ChoicesField) fields[i]).getValue();
                switch (p) {
                    case 0:
                        b.append(columns[i]).append('=').append(s);
                        break;
                    case 1:
                        b.append('(').append(columns[i]).append(" & ").append(s).append(") =").append(s);
                        break;
                    case 2:
                        b.append('(').append(columns[i]).append(" & ").append(s).append(") > 0");
                        break;
                }
            } else {
                if(fields[i] instanceof ChoiceField) {
                    s = "" + ((ChoiceField)fields[i]).getValue();
                } else {
                    value = fields[i].getValue();
                    if(value instanceof Storable) {
                        s = ((Storable)value).getStorableValue();
                    } else if(fields[i] instanceof DateField) {
                        s = "'" + DateUtility.formatDate(((DateField)fields[i]).getValue()) + "'";
                    } else if(value instanceof Number) {
                        BigDecimal bd = MathUtility.toBigDecimal(value);
                        s = bd == null ? "0" : bd.toPlainString();
                    }
                }
                if(fields[i] instanceof ObjectFilter.MField) {
                    b.append('(').append(columns[i]).append(").Amount");
                } else if(fields[i] instanceof ObjectFilter.QField) {
                    b.append('(').append(columns[i]).append(").Quantity");
                } else if(fields[i] instanceof ObjectFilter.CVField) {
                    b.append('(').append(columns[i]).append(").Value");
                } else {
                    b.append(columns[i]);
                }
                switch(p) {
                    case 0:
                        b.append('=');
                        break;
                    case 1:
                        b.append("<>");
                        break;
                    case 2:
                        b.append('>');
                        break;
                    case 3:
                        b.append(">=");
                        break;
                    case 4:
                        b.append('<');
                        break;
                    case 5:
                        b.append("<=");
                        break;
                }
                b.append(s);
            }
            if(sb.length() > 0) {
                sb.append(" AND ");
            }
            sb.append(b);
        }
        return sb;
    }

    private boolean isEmptyCheat(Object value) {
        if(value == null) {
            return true;
        }
        if(value instanceof BigDecimal) {
            return ((BigDecimal)value).signum() == 0;
        }
        return true;
    }

    private Predicate<T> equals(int functionIndex, String value) {
        return t -> functions[functionIndex].apply(t).equals(value);
    }

    private Predicate<T> notEquals(int functionIndex, String value) {
        return t -> !functions[functionIndex].apply(t).equals(value);
    }

    private Predicate<T> contains(int functionIndex, String value) {
        return t -> ((String)functions[functionIndex].apply(t)).contains(value);
    }

    private Predicate<T> startingWith(int functionIndex, String value) {
        return t -> ((String)functions[functionIndex].apply(t)).startsWith(value);
    }

    private Predicate<T> notStartingWith(int functionIndex, String value) {
        return t -> !((String)functions[functionIndex].apply(t)).startsWith(value);
    }

    private Predicate<T> equals(int functionIndex, boolean value) {
        return t -> functions[functionIndex].apply(t).equals(value);
    }

    private Predicate<T> equals(int functionIndex, int value) {
        return t -> ((Integer)functions[functionIndex].apply(t)) == value;
    }

    private Predicate<T> notEquals(int functionIndex, int value) {
        return t -> ((Integer)functions[functionIndex].apply(t)) != value;
    }

    private Predicate<T> lessThan(int functionIndex, int value) {
        return t -> ((Integer)functions[functionIndex].apply(t)) < value;
    }

    private Predicate<T> lessThanOrEquals(int functionIndex, int value) {
        return t -> ((Integer)functions[functionIndex].apply(t)) <= value;
    }

    private Predicate<T> greaterThan(int functionIndex, int value) {
        return t -> ((Integer)functions[functionIndex].apply(t)) > value;
    }

    private Predicate<T> greaterThanOrEquals(int functionIndex, int value) {
        return t -> ((Integer)functions[functionIndex].apply(t)) >= value;
    }

    private Predicate<T> includeAny(int functionIndex, int value) {
        return greaterThan(functionIndex, value);
    }

    private Predicate<T> includeAll(int functionIndex, int value) {
        return equals(functionIndex, value);
    }

    private Predicate<T> equals(int functionIndex, BigDecimal value) {
        return t -> ((BigDecimal)functions[functionIndex].apply(t)).compareTo(value) == 0;
    }

    private Predicate<T> notEquals(int functionIndex, BigDecimal value) {
        return t -> ((BigDecimal)functions[functionIndex].apply(t)).compareTo(value) != 0;
    }

    private Predicate<T> lessThan(int functionIndex, BigDecimal value) {
        return t -> ((BigDecimal)functions[functionIndex].apply(t)).compareTo(value) < 0;
    }

    private Predicate<T> lessThanOrEquals(int functionIndex, BigDecimal value) {
        return t -> ((BigDecimal)functions[functionIndex].apply(t)).compareTo(value) <= 0;
    }

    private Predicate<T> greaterThan(int functionIndex, BigDecimal value) {
        return t -> ((BigDecimal)functions[functionIndex].apply(t)).compareTo(value) > 0;
    }

    private Predicate<T> greaterThanOrEquals(int functionIndex, BigDecimal value) {
        return t -> ((BigDecimal)functions[functionIndex].apply(t)).compareTo(value) >= 0;
    }

    private Predicate<T> equals(int functionIndex, Date value) {
        return t -> DateUtility.equals(((Date)functions[functionIndex].apply(t)), value);
    }

    private Predicate<T> notEquals(int functionIndex, Date value) {
        return t -> !DateUtility.equals(((Date)functions[functionIndex].apply(t)), value);
    }

    private Predicate<T> after(int functionIndex, Date value) {
        return t -> ((Date)functions[functionIndex].apply(t)).after(value);
    }

    private Predicate<T> onOrAfter(int functionIndex, Date value) {
        return t -> !((Date)functions[functionIndex].apply(t)).before(value);
    }

    private Predicate<T> before(int functionIndex, Date value) {
        return t -> ((Date)functions[functionIndex].apply(t)).before(value);
    }

    private Predicate<T> onOrBefore(int functionIndex, Date value) {
        return t -> !((Date)functions[functionIndex].apply(t)).after(value);
    }

    private Predicate<T> add(Predicate<T> last, Predicate<T> next) {
        if(last == null) {
            return next;
        }
        return last.and(next);
    }

    @Override
    public Predicate<T> getFilterPredicate() {
        Predicate<T> filter = null;
        String textValue;
        Date dateValue;
        BigDecimal bdValue;
        int intValue, i, p;
        for(i = 0; i < checks.length; i++) {
            if(!checks[i].getValue()) {
                continue;
            }
            p = combos[i].getValue();
            if(fields[i].getClass() == Caption.class) {
                filter = add(filter, equals(i, p == 1));
            } else if(fields[i].getClass() == TextField.class) {
                textValue = fields[i].getValue().toString().trim().toLowerCase();
                switch(p) {
                    case 0:
                        filter = add(filter, startingWith(i, textValue));
                        break;
                    case 1:
                        filter = add(filter, notStartingWith(i, textValue));
                        break;
                    case 2:
                        filter = add(filter, equals(i, textValue));
                        break;
                    case 3:
                        filter = add(filter, notEquals(i, textValue));
                        break;
                    case 4:
                        filter = add(filter, contains(i, textValue));
                        break;
                }
            } else if(fields[i] instanceof ChoicesField) {
                intValue = ((ChoicesField)fields[i]).getValue();
                switch(p) {
                    case 0:
                        filter = add(filter, equals(i, intValue));
                        break;
                    case 1:
                        filter = add(filter, includeAll(i, intValue));
                        break;
                    case 2:
                        filter = add(filter, includeAny(i, intValue));
                        break;
                }
            } else if(fields[i] instanceof ChoiceField) {
                filter = add(filter, equals(i, ((ChoiceField)fields[i]).getValue()));
            } else if(fields[i] instanceof DateField) {
                dateValue = ((DateField)fields[i]).getValue();
                switch (p) {
                    case 0:
                        filter = add(filter, equals(i, dateValue));
                        break;
                    case 1:
                        filter = add(filter, notEquals(i, dateValue));
                        break;
                    case 2:
                        filter = add(filter, after(i, dateValue));
                        break;
                    case 3:
                        filter = add(filter, onOrAfter(i, dateValue));
                        break;
                    case 4:
                        filter = add(filter, before(i, dateValue));
                        break;
                    case 5:
                        filter = add(filter, onOrBefore(i, dateValue));
                        break;
                }
            } else if(fields[i] instanceof IntegerField) {
                intValue = ((IntegerField)fields[i]).getValue();
                switch (p) {
                    case 0:
                        filter = add(filter, equals(i, intValue));
                        break;
                    case 1:
                        filter = add(filter, notEquals(i, intValue));
                        break;
                    case 2:
                        filter = add(filter, greaterThan(i, intValue));
                        break;
                    case 3:
                        filter = add(filter, greaterThanOrEquals(i, intValue));
                        break;
                    case 4:
                        filter = add(filter, lessThan(i, intValue));
                        break;
                    case 5:
                        filter = add(filter, lessThanOrEquals(i, intValue));
                        break;
                }
            } else {
                if(fields[i] instanceof LongField) {
                    bdValue = new BigDecimal(((LongField)fields[i]).getValue());
                } else if(fields[i] instanceof DoubleField) {
                    bdValue = new BigDecimal(((DoubleField)fields[i]).getValue().toString());
                } else {
                    throw new SORuntimeException("Unexpected: " + fields[i].getClass());
                }
                switch (p) {
                    case 0:
                        filter = add(filter, equals(i, bdValue));
                        break;
                    case 1:
                        filter = add(filter, notEquals(i, bdValue));
                        break;
                    case 2:
                        filter = add(filter, greaterThan(i, bdValue));
                        break;
                    case 3:
                        filter = add(filter, greaterThanOrEquals(i, bdValue));
                        break;
                    case 4:
                        filter = add(filter, lessThan(i, bdValue));
                        break;
                    case 5:
                        filter = add(filter, lessThanOrEquals(i, bdValue));
                        break;
                }
            }
        }
        return filter;
    }

    @Override
    public String getFilterText() {
        StringBuilder sb = buildCriteria();
        if((sb == null || sb.length() == 0) && autoCheck()) {
            autoChecked = true;
            sb = buildCriteria();
        }
        return sb == null || sb.length() == 0 ? null : sb.toString();
    }

    @Override
    public Class<T> getObjectClass() {
        return objectClass;
    }

    interface CVField {
    }

    private static class MField extends DoubleField {
        private MField() {
            super(18, 2);
        }
    }

    private static class QField extends DoubleField {
        private QField() {
            super(18, 5);
        }
    }

    private static class CDateField extends DateField implements CVField {
        private CDateField() {
        }
    }

    private static class CMinutesField extends IntegerField implements CVField {
        private CMinutesField() {
        }
    }

    private static class CDoubleField extends DoubleField implements CVField {
        private CDoubleField() {
        }
    }

    private static class CIntegerField extends IntegerField implements CVField {
        private CIntegerField() {
        }
    }

    private static class CLongField extends LongField implements CVField {
        private CLongField() {
        }
    }

    private static class Caption extends TextField {

        private Caption(String text) {
            setValue(text);
            getStyle().set("min-width", "150px");
            setReadOnly(true);
        }
    }
}