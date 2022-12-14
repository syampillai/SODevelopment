package com.storedobject.ui;

import com.storedobject.common.Executable;
import com.storedobject.common.SORuntimeException;
import com.storedobject.common.StringList;
import com.storedobject.core.*;
import com.storedobject.ui.util.SOFieldCreator;
import com.storedobject.vaadin.ChoiceField;
import com.storedobject.vaadin.DataForm;
import com.vaadin.flow.component.HasValue;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A list report that lists {@link StoredObject}s. The object class to be listed can be defined using
 * a {@link ReportDefinition} or directly specified as the data class name. Attribute filters defined in the
 * {@link ReportDefinition} instance is processed and filter variable values are accepted and applied while
 * generating the output.
 *
 * @author Syam
 */
public class ObjectList extends DataForm {

    private static final double DIFF = 0.000000001;
    private final ReportDefinition definition;
    private final ChoiceField format = new ChoiceField("Format", StringList.create("PDF", "Excel"));
    private final List<Field> fields = new ArrayList<>();
    private final List<Field> filters = new ArrayList<>();

    /**
     * Constructor.
     * <p>The parameter should contain the name of the report (as specified in the {@link ReportDefinition}.
     * However, you may use the fully qualified name of the data class
     * instead of the report definition name and in that case, a default report definition will be generated.</p>
     *
     * @param report Report name.
     */
    public ObjectList(String report) {
        this(definition(report));
    }

    /**
     * Constructor.
     *
     * @param definition Report definition.
     * @param <T> Type of objects to list.
     */
    public <T extends StoredObject> ObjectList(ReportDefinition definition) {
        super(definition.getTitle());
        this.definition = definition;
        addField(format);
        StringList fList = StringList.create(definition.getFilter().split("\n"));
        if(fList.isEmpty()) {
            return;
        }
        @SuppressWarnings("unchecked") Class<T> dClass = (Class<T>) definition.getClassForData();
        SOFieldCreator<T> fc = fc(dClass);
        if(fc == null) {
            return;
        }
        int p;
        StoredObjectUtility.MethodList m;
        String caption, compare;
        HasValue<?, ?> field;
        for(String fieldName: fList) {
            if(fieldName.isBlank()) {
                continue;
            }
            p = fieldName.toLowerCase().indexOf(" as ");
            if(p > 0) {
                caption = StringUtility.makeLabel(fieldName);
                fieldName = fieldName.substring(0, p).strip();
            } else {
                caption = null;
            }
            p = fieldName.indexOf('/');
            if(p < 0) {
                compare = "";
            } else {
                compare = fieldName.substring(p + 1);
                fieldName = fieldName.substring(0, p).strip();
            }
            try {
                m = StoredObjectUtility.createMethodList(dClass, fieldName);
            } catch(Throwable e) {
                continue;
            }
            if(caption == null) {
                caption = StringUtility.makeLabel(fieldName);
            }
            String fn = fieldName;
            p = fn.lastIndexOf('.');
            fn = fn.substring(p + 1);
            if(fc.getObjectClass() == m.getTail().getDeclaringClass()) {
                if(Date.class.isAssignableFrom(m.getReturnType()) && compare(compare) == 'P') {
                    field = new DatePeriodField(caption);
                } else {
                    field = fc.createField(fn, m.getReturnType(), caption);
                }
            } else {
                SOFieldCreator<?> ofc = fc(m.getTail().getDeclaringClass());
                if(ofc == null) {
                    continue;
                }
                field = ofc.createField(fn, m.getReturnType(), caption);
            }
            addField(field);
            this.fields.add(new Field(fieldName, compare(compare), field, m.isAttribute() ? null : m));
            if(compare.indexOf('*') >= 0) {
                setRequired(field);
            }
        }
    }

    private static <O extends StoredObject> SOFieldCreator<O> fc(Class<?> oClass) {
        if(StoredObject.class.isAssignableFrom(oClass)) {
            @SuppressWarnings("unchecked") Class<O> dClass = (Class<O>) oClass;
            SOFieldCreator<O> fc = new SOFieldCreator<>();
            return fc.create(dClass);
        }
        return null;
    }

    private static char compare(String compare) {
        return switch(compare.replace("*", "").toUpperCase()) {
            case "LT", "L", "<" -> 'L';
            case "NG", "LE", "<=", "=<" -> 'l';
            case "GT", "G", ">" -> 'G';
            case "NL", "GE", ">=", "=>" -> 'g';
            case "NE", "!=", "<>" -> 'N';
            case "P" -> 'P';
            default -> 'E';
        };
    }

    private static ReportDefinition definition(String report) {
        ReportDefinition definition;
        if(report.indexOf('.') > 0) {
            report = ApplicationServer.guessClass(report);
            definition = ReportDefinition.get(report);
            if(definition != null && definition.getDataClass().equals(report)) {
                return definition;
            }
            try {
                Class<?> dc = JavaClassLoader.getLogic(report);
                if(StoredObject.class.isAssignableFrom(dc)) {
                    //noinspection unchecked
                    return ReportDefinition.create((Class<? extends StoredObject>) dc);
                }
            } catch(ClassNotFoundException ignored) {
            }
            throw new SORuntimeException("Not a data class: " + report);
        }
        definition = ReportDefinition.get(report);
        if(definition == null) {
            throw new SORuntimeException("No definition found for: " + report);
        }
        return definition;
    }

    @Override
    protected boolean process() {
        close();
        report();
        return true;
    }

    private <T extends StoredObject> void report() {
        filters.clear();
        fields.stream().filter(f -> f.method != null && f.field.getValue() != null).forEach(filters::add);
        Class<? extends Executable> logicClass = definition.getClassForLogic(format.getValue() == 1);
        Executable logic;
        try {
            logic = Utility.construct(logicClass,
                    new Class<?>[]{ Device.class, ReportDefinition.class },
                    new Object[]{ Application.get(), definition });
        } catch(LogicRedirected ld) {
            logic = ld.getExecutable();
        }
        if(logic == null) {
            error("Unable to create logic: " + logicClass.getName());
            return;
        }
        if(logic instanceof com.storedobject.report.ObjectList) {
            @SuppressWarnings("unchecked")
            com.storedobject.report.ObjectList<T> report = (com.storedobject.report.ObjectList<T>) logic;
            report.setExtraCondition(extraCondition());
            if(!filters.isEmpty()) {
                report.setLoadFilter(this::filter);
            }
        } else if(logic instanceof com.storedobject.report.ObjectListExcel) {
            @SuppressWarnings("unchecked")
            com.storedobject.report.ObjectListExcel<T> report = (com.storedobject.report.ObjectListExcel<T>) logic;
            report.setExtraCondition(extraCondition());
            if(!filters.isEmpty()) {
                report.setLoadFilter(this::filter);
            }
        }
        logic.execute();
    }

    /**
     * Specify extra condition to be applied while retrieving the data. This condition is in addition to the condition
     * generated for the filters and defined in the report definition itself.
     *
     * @return Extra condition.
     */
    public String getExtraCondition() {
        return null;
    }

    /**
     * Get the definition associated with this list.
     *
     * @return Definition.
     */
    public ReportDefinition getDefinition() {
        return definition;
    }

    private <T extends StoredObject> boolean filter(T o) {
        Object v, f;
        for(Field field: filters) {
            f = field.field.getValue();
            if(f == null) {
                continue;
            }
            try {
                v = field.method.invoke(o);
            } catch(Throwable ignored) {
                continue;
            }
            switch(field.compare) {
                case 'E' -> {
                    if(compare(v, f) == 0) {
                        continue;
                    }
                    return false;
                }
                case 'L' -> {
                    if(compare(v, f) < 0) {
                        continue;
                    }
                    return false;
                }
                case 'l' -> {
                    if(compare(v, f) <= 0) {
                        continue;
                    }
                    return false;
                }
                case 'G' -> {
                    if(compare(v, f) > 0) {
                        continue;
                    }
                    return false;
                }
                case 'g' -> {
                    if(compare(v, f) >= 0) {
                        continue;
                    }
                    return false;
                }
                case 'N' -> {
                    if(compare(v, f) != 0) {
                        continue;
                    }
                    return false;
                }
                case 'P' -> {
                    if(f instanceof DatePeriod dp && v instanceof Date d) {
                        if(dp.inside(d)) {
                            continue;
                        }
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private String extraCondition() {
        if(fields.isEmpty()) {
            return getExtraCondition();
        }
        Object value;
        StringBuilder sb = new StringBuilder();
        for(Field field: fields) {
            if(field.method != null) {
                continue;
            }
            value = field.field.getValue();
            if(value == null) {
                continue;
            }
            if(!sb.isEmpty()) {
                sb.append(" AND ");
            }
            if(value instanceof String) {
                sb.append("lower(").append(field.name).append(')');
            } else {
                sb.append(field.name);
            }
            if(value instanceof String s) {
                value = "'" + s.toLowerCase().replace("'", "''") + "'";
            }
            if(value instanceof DatePeriod dp) {
                value = dp.getDBCondition();
            }
            if(value instanceof Date d) {
                value = "'" + Database.format(d) + "'";
            }
            switch(field.compare) {
                case 'E' -> sb.append('=');
                case 'L' -> sb.append('<');
                case 'l' -> sb.append("<=");
                case 'G' -> sb.append('>');
                case 'g' -> sb.append(">=");
                case 'N' -> sb.append("<>");
            }
            sb.append(value);
        }
        if(sb.isEmpty()) {
            return getExtraCondition();
        }
        String c = getExtraCondition();
        if(c == null) {
            return sb.toString();
        }
        return c + " AND " + sb;
    }

    private static int compare(Object v1, Object v2) {
        if(v1 instanceof String v) {
            return v.compareTo((String) v2);
        }
        if(v1 instanceof Date d) {
            return (int) (d.getTime() - ((Date)v2).getTime());
        }
        if(v1 instanceof Boolean v) {
            Boolean o = (Boolean) v2;
            if(v && o) {
                return 0;
            }
            if(!v && o) {
                return -1;
            }
            return 1;
        }
        if(v1 instanceof DecimalNumber v) {
            v1 = v.getValue();
            v2 = ((DecimalNumber)v2).getValue();
        }
        if(v1 instanceof Quantity v) {
            v1 = v.getValue();
            v2 = ((Quantity)v2).getValue();
        }
        if(v1 instanceof Money v) {
            v1 = v.getValue();
            v2 = ((Money)v2).getValue();
        }
        if(v1 instanceof BigDecimal v) {
            return v.compareTo((BigDecimal) v2);
        }
        if(v1 instanceof BigInteger v) {
            return v.compareTo((BigInteger) v2);
        }
        if(v1 instanceof Number n) {
            if(v1 instanceof Double v) {
                double diff = v - (Double) v2;
                if(Math.abs(diff) <= DIFF) {
                    return 0;
                }
                return diff < 0 ? -1 : 1;
            }
            long diff = n.longValue() - ((Number)v2).longValue();
            return diff < 0 ? -1 : (diff == 0 ? 0 : 1);
        }
        return v1.toString().compareTo(v2.toString());
    }

    private record Field(String name, char compare, HasValue<?, ?> field, StoredObjectUtility.MethodList method) {
    }
}
