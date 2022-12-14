package com.storedobject.core;

import com.storedobject.common.Executable;
import com.storedobject.common.StringList;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ReportDefinition extends Name {

    public ReportDefinition() {
    }

    public static void columns(Columns columns) {
    }

    public static ReportDefinition get(String name) {
        return StoredObjectUtility.get(ReportDefinition.class, "Name", name, false);
    }

    public static ObjectIterator<ReportDefinition> list(String name) {
        return StoredObjectUtility.list(ReportDefinition.class, "Name", name, false);
    }

    public void setTitle(String title) {
    }

    public String getTitle() {
        return "";
    }

    public void setDescription(String description) {
    }

    public String getDescription() {
        return "";
    }

    public void setPrintDescription(boolean printDescription) {
    }

    public boolean getPrintDescription() {
        return false;
    }

    public void setDataClass(String dataClass) {
    }

    public String getDataClass() {
        return "";
    }

    public void setIncludeSubclasses(boolean includeSubclasses) {
    }

    public boolean getIncludeSubclasses() {
        return false;
    }

    public void setCondition(String condition) {
    }

    public String getCondition() {
        return "";
    }

    public void setFilter(String filter) {
    }

    public String getFilter() {
        return "";
    }

    public void setOrderBy(String orderBy) {
    }

    public String getOrderBy() {
        return "";
    }

    public void setLogicClass(String logicClass) {
    }

    public String getLogicClass() {
        return "";
    }

    public void setBaseFontSize(int baseFontSize) {
    }

    public int getBaseFontSize() {
        return 0;
    }

    public static String[] getBaseFontSizeValues() {
        return new String[] {};
    }

    public static String getBaseFontSizeValue(int value) {
        return "";
    }

    public String getBaseFontSizeValue() {
        return "";
    }

    public void setOrientation(int orientation) {
    }

    public int getOrientation() {
        return 0;
    }

    public static String[] getOrientationValues() {
        return new String[] {};
    }

    public static String getOrientationValue(int value) {
        return "";
    }

    public String getOrientationValue() {
        return "";
    }

    public Class<? extends StoredObject> getClassForData() {
        return Person.class;
    }

    public Class<? extends Executable> getClassForLogic(boolean excel) {
        return null;
    }

    public final List<ReportColumnDefinition> getColumns() {
        return new ArrayList<>();
    }

    public static ReportDefinition create(Class<? extends StoredObject> dataClass, String... columns) {
        return new ReportDefinition();
    }

    public static ReportDefinition create(Class<? extends StoredObject> dataClass, Iterable<String> columns) {
        return new ReportDefinition();
    }

    public void setExecutable(Executable executable) {
    }

    public void setCustomColumnSupplier(Supplier<StringList> customColumnSupplier) {
    }
}
