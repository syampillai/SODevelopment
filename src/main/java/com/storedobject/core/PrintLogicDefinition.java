package com.storedobject.core;

import com.storedobject.common.Executable;
import com.storedobject.core.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Define "print logic" instances to be associated with a {@link StoredObject} class.
 *
 * @author Syam
 */
public class PrintLogicDefinition extends StoredObject {

    private String dataClassName;
    private String label = "Print";
    private String iconName;
    private String printLogicClassName;
    private Id formatId;
    private boolean special;

    public PrintLogicDefinition() {
    }

    public static void columns(Columns columns) {
        columns.add("DataClassName", "text");
        columns.add("Label", "text");
        columns.add("IconName", "text");
        columns.add("PrintLogicClassName", "text");
        columns.add("ODTFormat", "id");
        columns.add("Special", "boolean");
    }

    public static void indices(Indices indices) {
        indices.add("DataClassName,Label", true);
    }

    public static String[] links() {
        return new String[]{
                "Applicable Data Logic|com.storedobject.core.ApplicableDataLogic|||0",
        };
    }

    public void setDataClassName(String dataClassName) {
        this.dataClassName = dataClassName;
    }

    @Column(order = 100)
    public String getDataClassName() {
        return dataClassName;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Column(order = 200)
    public String getLabel() {
        return label;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }

    @Column(required = false, order = 300)
    public String getIconName() {
        return iconName;
    }

    public void setPrintLogicClassName(String printLogicClassName) {
        this.printLogicClassName = printLogicClassName;
    }

    @Column(order = 400, caption = "Logic(Device, Object) OR Logic(Device, Source, Object) OR ObjectLogicButton")
    public String getPrintLogicClassName() {
        return printLogicClassName;
    }

    public void setODTFormat(Id oDTFormatId) {
        this.formatId = oDTFormatId;
    }

    public void setODTFormat(BigDecimal idValue) {
        setODTFormat(new Id(idValue));
    }

    public void setODTFormat(StreamData format) {
        setODTFormat(format == null ? null : format.getId());
    }

    @Column(style = "(FILE)", required = false, order = 500)
    public Id getODTFormatId() {
        return formatId;
    }

    public StreamData getODTFormat() {
        return get(StreamData.class, formatId);
    }

    public void setSpecial(boolean special) {
        this.special = special;
    }

    @Column(order = 600)
    public boolean getSpecial() {
        return special;
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        dataClassName = StringUtility.pack(dataClassName);
        if (StringUtility.isWhite(dataClassName)) {
            throw new Invalid_Value("Data Class Name");
        }
        if (StringUtility.isWhite(label)) {
            throw new Invalid_Value("Label");
        }
        printLogicClassName = StringUtility.pack(printLogicClassName);
        if (StringUtility.isWhite(printLogicClassName)) {
            throw new Invalid_Value("Logic Class Name");
        }
        checkForDuplicate("DataClassName", "Label");
        formatId = tm.checkType(this, formatId, StreamData.class, true);
        super.validateData(tm);
    }

    public final Class<? extends StoredObject> getDataClass() {
        try {
            //noinspection unchecked
            return (Class<? extends StoredObject>) JavaClassLoader.getLogic(dataClassName);
        } catch(Throwable ignored) {
        }
        return null;
    }

    public final Class<? extends Executable> getLogicClass() {
        try {
            //noinspection unchecked
            return (Class<Executable>) JavaClassLoader.getLogic(printLogicClassName);
        } catch(Throwable ignored) {
        }
        return null;
    }

    public static PrintLogicDefinition getFor(Class<? extends StoredObject> dataCass, String label) {
        return get(PrintLogicDefinition.class,
                "DataClassName='" + dataCass.getName() + "' AND Label='" +
                        label.replace("'", "''") + "'");
    }

    public static ObjectIterator<PrintLogicDefinition> listFor(Class<? extends StoredObject> dataCass,
                                                               String applicableDataLogicName) {
        ObjectIterator<PrintLogicDefinition> list = listFor(dataCass, false);
        if(applicableDataLogicName != null && !applicableDataLogicName.isBlank()) {
            list = list.filter(p -> p.applicable(applicableDataLogicName));
        }
        return list;
    }

    private boolean applicable(String dataLogicName) {
        List<ApplicableDataLogic> applicable = listLinks(ApplicableDataLogic.class).toList();
        if(applicable.isEmpty()) {
            return true;
        }
        return applicable.stream().anyMatch(adc -> dataLogicName.equals(adc.getDataLogic()));
    }

    public static ObjectIterator<PrintLogicDefinition> listFor(Class<? extends StoredObject> dataCass,
                                                               boolean includeSpecial) {
        String condition = "DataClassName='" + dataCass.getName() + "'";
        if(!includeSpecial) {
            condition += " AND NOT Special";
        }
        return list(PrintLogicDefinition.class, condition);
    }
}
