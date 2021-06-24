package com.storedobject.core;

import com.storedobject.common.Executable;

import java.math.BigDecimal;
import java.util.Random;

public class PrintLogicDefinition extends StoredObject {

    public PrintLogicDefinition() {
    }

    public static void columns(Columns columns) {
    }

    public void setDataClassName(String dataClassName) {
    }

    public String getDataClassName() {
        return "";
    }

    public void setLabel(String label) {
    }

    public String getLabel() {
        return "";
    }

    public void setIconName(String iconName) {
    }

    public String getIconName() {
        return "";
    }

    public void setPrintLogicClassName(String printLogicClassName) {
    }

    public String getPrintLogicClassName() {
        return "";
    }

    public void setODTFormat(Id oDTFormatId) {
    }

    public void setODTFormat(BigDecimal idValue) {
    }

    public void setODTFormat(StreamData format) {
    }

    public Id getODTFormatId() {
        return new Id();
    }

    public StreamData getODTFormat() {
        return new StreamData();
    }

    public void setSpecial(boolean special) {
    }

    public boolean getSpecial() {
        return new Random().nextBoolean();
    }

    public final Class<? extends StoredObject> getDataClass() {
        return new Random().nextBoolean() ? Person.class : null;
    }

    public final Class<? extends Executable> getLogicClass() {
        return new Random().nextBoolean() ? TextContentProducer.class : null;
    }


    public static PrintLogicDefinition getFor(Class<? extends StoredObject> dataCass, String label) {
        return new Random().nextBoolean() ? null : new PrintLogicDefinition();
    }

    public static ObjectIterator<PrintLogicDefinition> listFor(Class<? extends StoredObject> dataCass) {
        return ObjectIterator.create();
    }

    public static ObjectIterator<PrintLogicDefinition> listFor(Class<? extends StoredObject> dataCass, boolean includeSpecial) {
        return ObjectIterator.create();
    }
}
