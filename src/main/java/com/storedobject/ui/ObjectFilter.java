package com.storedobject.ui;

import com.storedobject.common.MathUtility;
import com.storedobject.common.SORuntimeException;
import com.storedobject.common.Storable;
import com.storedobject.common.StringList;
import com.storedobject.core.*;
import com.storedobject.core.annotation.Column;
import com.storedobject.ui.util.LogicParser;
import com.storedobject.vaadin.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasSize;
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
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

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
    private final static String[] selectChoice = new String[] {
            "Equal to", "Not equal to"
    };
    private final static String[] selectChoices = new String[] {
            "Equal to", "Include all", "Include any"
    };
    private final static String[] selectBoolean = new String[] {
            "No", "Yes"
    };
    private final static String[] selectId = selectChoice;
    private final Class<T> objectClass;
    private String[] columns;
    private Function<T, ?>[] functions;
    private Caption[] captions;
    private Checkbox[] checks;
    private ChoiceField[] combos;
    private HasValue<?, ?>[] fields;
    private final Consumer<ObjectSearchBuilder<T>> changeConsumer;

    public ObjectFilter(Class<T> objectClass) {
        this(objectClass, null);
    }

    public ObjectFilter(Class<T> objectClass, StringList columns) {
        this(objectClass, columns, null);
    }

    public ObjectFilter(Class<T> objectClass, StringList columns, Consumer<ObjectSearchBuilder<T>> changeConsumer) {
        this.changeConsumer = changeConsumer;
        this.objectClass = objectClass;
        if(columns == null || columns.isEmpty()) {
            columns = StoredObjectUtility.searchColumns(objectClass);
        }
        this.columns = removeContacts(columns);
        buildFields();
        getContent().getElement().getStyle().set("max-width", "90vw");
    }

    @Override
    protected HasComponents createContainer() {
        return new ButtonLayout();
    }

    @SuppressWarnings("unchecked")
    public static <O extends StoredObject> ObjectFilter<O> create(Class<O> objectClass, StringList columns) {
        try {
            Class<?> logic = JavaClassLoader.getLogic(LogicParser.createLogicName(objectClass, "Filter"));
            Constructor<?> c;
            try {
                c = logic.getConstructor(StringList.class);
                return (ObjectFilter<O>) c.newInstance(new Object[]{columns});
            } catch(Throwable ignored) {
            }
            c = logic.getConstructor((Class<?>[])null);
            return (ObjectFilter<O>) c.newInstance((Object[])null);
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
        StoredObjectUtility.MethodList[] columnMethods = StoredObjectUtility
                .createMethodLists(objectClass, StringList.create(columns));
        n = 0;
        Class<?> c;
        for(i = 0; i < columnMethods.length; i++) {
            if(columnMethods[i] == null || (c = columnMethods[i].getReturnType()).getComponentType() != null) {
                continue;
            }
            if(StoredObject.class.isAssignableFrom(c)) {
                if(StreamData.class.isAssignableFrom(c)) {
                    columnMethods[i] = null;
                    continue;
                }
                try {
                    StoredObjectUtility.createMethodList(objectClass, columns[i] + "Id");
                } catch(Throwable e) {
                    columnMethods[i] = null;
                    continue;
                }
            }
            if(Money.class == c || Date.class == c || String.class == c || byte.class == c || int.class == c
                    || long.class == c || short.class == c || double.class == c || float.class == c
                    || boolean.class == c || Boolean.class == c || Number.class.isAssignableFrom(c)
                    || Quantity.class.isAssignableFrom(c) || StoredObject.class.isAssignableFrom(c)
                    || c == ComputedDate.class || c == ComputedMinute.class || c == ComputedInteger.class
                    || c == ComputedLong.class || c == ComputedDouble.class) {
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
            c = columnMethods[i].getReturnType();
            span = new HorizontalLayout();
            add(span);
            span.add(checks[n] = new Checkbox(""));
            span.setVerticalComponentAlignment(FlexComponent.Alignment.CENTER, checks[n]);
            span.add(captions[n] = new Caption(labels[i]));
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
            } else if(Quantity.class.isAssignableFrom(c)) {
                fields[n] = new QField();
                combos[n] = new ChoiceField(selectNumber);
            } else if(StoredObject.class.isAssignableFrom(c)) {
                boolean any = false;
                try {
                    Method m = objectClass.getMethod(columnMethods[i].getName() + "Id");
                    if(m.getReturnType() == Id.class) {
                        Column cc = m.getAnnotation(Column.class);
                        any = cc != null && cc.style().contains("(any)");
                    }
                } catch (NoSuchMethodException ignored) {
                }
                //noinspection rawtypes
                fields[n] = new ObField(c, any);
                combos[n] = new ChoiceField(selectId);
                columnMethods[n] = StoredObjectUtility.createMethodList(objectClass, columns[i] + "Id");
            } else if(int.class == c || long.class == c || short.class == c || (Number.class).isAssignableFrom(c)) {
                if(int.class == c) {
                    try {
                        Method listM = objectClass.getMethod(columnMethods[i].getName() + "Values");
                        if(Modifier.isStatic(listM.getModifiers()) && listM.getReturnType() == String[].class) {
                            List<String> list = new StringUtility.List((String[])listM.invoke(null, (Object[])null));
                            fields[n] = new ChoiceField(null, list);
                            combos[n] = new ChoiceField(selectChoice);
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
                    if(int.class == c) {
                        fields[n] = new IntegerField();
                    } else if(long.class == c) {
                        fields[n] = new LongField();
                    } else {
                        fields[n] = new BigDecimalField();
                    }
                    combos[n] = new ChoiceField(selectNumber);
                }
            }
            span.add(combos[n]);
            span.add((Component)fields[n]);
            addField(combos[n]);
            addField(fields[n]);
            Checkbox cb = checks[n];
            fields[n].addValueChangeListener(e -> {
                if(!cb.getValue()) {
                    cb.setValue(true);
                } else if(changeConsumer != null) {
                    changeConsumer.accept(this);
                }
            });
            if(changeConsumer != null) {
                cb.addValueChangeListener(e -> changeConsumer.accept(this));
            }
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
        equalizeWidths();
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
        HasValue<?, ?>[] newFields = new HasValue[fields.length - 1];
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
        equalizeWidths();
    }

    private void equalizeWidths() {
        int m = 0, w;
        for(Caption caption: captions) {
            if((w = caption.getValue().length()) > m) {
                m = w;
            }
        }
        for(Caption caption: captions) {
            caption.setWidth(m + "ch");
        }
        for(HasValue<?, ?> field: fields) {
            if(field instanceof HasSize hs) {
                hs.setMinWidth("250px");
                hs.setMaxWidth("250px");
            }
        }
    }

    private StringBuilder buildCriteria() {
        String s;
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
                    case 0 -> b.append(" LIKE '").append(s).append("%'");
                    case 1 -> b.append(" NOT LIKE '").append(s).append("%'");
                    case 2 -> b.append("='").append(s).append("'");
                    case 3 -> b.append("<>'").append(s).append("'");
                    case 4 -> b.append(" LIKE '%").append(s).append("%'");
                }
            } else if(fields[i] instanceof ChoicesField) {
                s = "" + ((ChoicesField) fields[i]).getValue();
                switch(p) {
                    case 0 -> b.append(columns[i]).append('=').append(s);
                    case 1 -> b.append('(').append(columns[i]).append(" & ").append(s).append(") =").append(s);
                    case 2 -> b.append('(').append(columns[i]).append(" & ").append(s).append(") > 0");
                }
            } else {
                value = fields[i].getValue();
                if(value instanceof Storable) {
                    s = ((Storable)value).getStorableValue();
                } else if(fields[i] instanceof DateField) {
                    s = "'" + DateUtility.formatDate(((DateField)fields[i]).getValue()) + "'";
                } else if(value instanceof Number) {
                    BigDecimal bd = MathUtility.toBigDecimal(value);
                    s = bd == null ? "0" : bd.toPlainString();
                } else {
                    if(value == null) {
                        if(fields[i] instanceof ObjectField) {
                            value = 0;
                        }
                    }
                    s = "" + value;
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
                    case 0 -> b.append('=');
                    case 1 -> b.append("<>");
                    case 2 -> b.append('>');
                    case 3 -> b.append(">=");
                    case 4 -> b.append('<');
                    case 5 -> b.append("<=");
                }
                b.append(s);
            }
            if(!sb.isEmpty()) {
                sb.append(" AND ");
            }
            sb.append(b);
        }
        return sb;
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

    private Predicate<T> equals(int functionIndex, long value) {
        return t -> ((Long)functions[functionIndex].apply(t)) == value;
    }

    private Predicate<T> notEquals(int functionIndex, long value) {
        return t -> ((Long)functions[functionIndex].apply(t)) != value;
    }

    private Predicate<T> lessThan(int functionIndex, long value) {
        return t -> ((Long)functions[functionIndex].apply(t)) < value;
    }

    private Predicate<T> lessThanOrEquals(int functionIndex, long value) {
        return t -> ((Long)functions[functionIndex].apply(t)) <= value;
    }

    private Predicate<T> greaterThan(int functionIndex, long value) {
        return t -> ((Long)functions[functionIndex].apply(t)) > value;
    }

    private Predicate<T> greaterThanOrEquals(int functionIndex, long value) {
        return t -> ((Long)functions[functionIndex].apply(t)) >= value;
    }

    private Predicate<T> equals(int functionIndex, Id value) {
        return t -> (functions[functionIndex].apply(t)).equals(value);
    }

    private Predicate<T> notEquals(int functionIndex, Id value) {
        return t -> (functions[functionIndex].apply(t)).equals(value);
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
        Id idValue;
        int intValue, i, p;
        long longValue;
        for(i = 0; i < checks.length; i++) {
            if(!checks[i].getValue()) {
                continue;
            }
            p = combos[i].getValue();
            if(fields[i].getClass() == Caption.class) {
                filter = add(filter, equals(i, p == 1));
            } else if(fields[i].getClass() == TextField.class) {
                textValue = fields[i].getValue().toString().trim().toLowerCase();
                filter = switch(p) {
                    case 0 -> add(filter, startingWith(i, textValue));
                    case 1 -> add(filter, notStartingWith(i, textValue));
                    case 2 -> add(filter, equals(i, textValue));
                    case 3 -> add(filter, notEquals(i, textValue));
                    case 4 -> add(filter, contains(i, textValue));
                    default -> filter;
                };
            } else if(fields[i] instanceof ChoicesField) {
                intValue = ((ChoicesField)fields[i]).getValue();
                filter = switch(p) {
                    case 0 -> add(filter, equals(i, intValue));
                    case 1 -> add(filter, includeAll(i, intValue));
                    case 2 -> add(filter, includeAny(i, intValue));
                    default -> filter;
                };
            } else if(fields[i] instanceof ChoiceField) {
                filter = add(filter, equals(i, ((ChoiceField) fields[i]).getValue()));
            } else if(fields[i] instanceof ObjectField) {
                idValue = ((ObjectField<?>)fields[i]).getValue();
                if(p == 0) {
                    filter = add(filter, equals(i, idValue));
                } else {
                    filter = add(filter, notEquals(i, idValue));
                }
            } else if(fields[i] instanceof DateField) {
                dateValue = ((DateField)fields[i]).getValue();
                filter = switch(p) {
                    case 0 -> add(filter, equals(i, dateValue));
                    case 1 -> add(filter, notEquals(i, dateValue));
                    case 2 -> add(filter, after(i, dateValue));
                    case 3 -> add(filter, onOrAfter(i, dateValue));
                    case 4 -> add(filter, before(i, dateValue));
                    case 5 -> add(filter, onOrBefore(i, dateValue));
                    default -> filter;
                };
            } else if(fields[i] instanceof IntegerField) {
                intValue = ((IntegerField)fields[i]).getValue();
                filter = switch(p) {
                    case 0 -> add(filter, equals(i, intValue));
                    case 1 -> add(filter, notEquals(i, intValue));
                    case 2 -> add(filter, greaterThan(i, intValue));
                    case 3 -> add(filter, greaterThanOrEquals(i, intValue));
                    case 4 -> add(filter, lessThan(i, intValue));
                    case 5 -> add(filter, lessThanOrEquals(i, intValue));
                    default -> filter;
                };
            } else if(fields[i] instanceof LongField) {
                longValue = ((LongField)fields[i]).getValue();
                filter = switch(p) {
                    case 0 -> add(filter, equals(i, longValue));
                    case 1 -> add(filter, notEquals(i, longValue));
                    case 2 -> add(filter, greaterThan(i, longValue));
                    case 3 -> add(filter, greaterThanOrEquals(i, longValue));
                    case 4 -> add(filter, lessThan(i, longValue));
                    case 5 -> add(filter, lessThanOrEquals(i, longValue));
                    default -> filter;
                };
            } else {
                if(fields[i] instanceof BigDecimalField) {
                    bdValue = ((BigDecimalField)fields[i]).getValue();
                } else if(fields[i] instanceof DoubleField) {
                    bdValue = BigDecimal.valueOf(((DoubleField)fields[i]).getValue());
                } else {
                    throw new SORuntimeException("Unexpected: " + fields[i].getClass());
                }
                filter = switch(p) {
                    case 0 -> add(filter, equals(i, bdValue));
                    case 1 -> add(filter, notEquals(i, bdValue));
                    case 2 -> add(filter, greaterThan(i, bdValue));
                    case 3 -> add(filter, greaterThanOrEquals(i, bdValue));
                    case 4 -> add(filter, lessThan(i, bdValue));
                    case 5 -> add(filter, lessThanOrEquals(i, bdValue));
                    default -> filter;
                };
            }
        }
        return filter;
    }

    @Override
    public String getFilterText() {
        StringBuilder sb = buildCriteria();
        return sb.isEmpty() ? null : sb.toString();
    }

    @Override
    public Class<T> getObjectClass() {
        return objectClass;
    }

    interface CVField {
    }

    private static class ObField<O extends StoredObject> extends ObjectField<O> {
        private ObField(Class<O> objectClass, boolean any) {
            super(objectClass, any);
            ObjectInput<O> field = getField();
            if(field instanceof ObjectGetField) {
                TextField searchField = ((ObjectGetField<?>)field).getSearchField();
                searchField.getStyle().set("flexGrow", "1");
                setDisplayDetail(d -> {
                    searchField.setPlaceholder(d == null ? null : d.toDisplay());
                    searchField.setTitle(d == null ? null : d.toDisplay());
                });
            } else if(field instanceof ObjectSearchField) {
                TextField dc = new TextField();
                dc.getStyle().set("flexGrow", "1");
                dc.setReadOnly(true);
                setDetailComponent(dc);
                setDisplayDetail(d -> {
                    dc.setValue(d == null ? "" : d.toDisplay());
                    dc.setTitle(d == null ? null : d.toDisplay());
                });
                ((ObjectSearchField<O>) field).getContent().add(dc);
            }
        }
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
            getStyle().set("min-width", "150px").set("max-width", "150px");
            setReadOnly(true);
        }
    }
}