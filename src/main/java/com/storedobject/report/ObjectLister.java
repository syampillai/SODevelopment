package com.storedobject.report;

import com.storedobject.core.*;
import com.storedobject.pdf.TableHeader;

import java.util.function.Predicate;

public interface ObjectLister<T extends StoredObject> {

    ReportDefinition getReportDefinition();

    String getExtraCondition();

    void setExtraCondition(String extraCondition);

    void setErrorMessage(String errorMessage);

    default String getOrderBy() {
        return getReportDefinition().getOrderBy();
    }

    default Predicate<T> getLoadFilter() {
        return null;
    }

    default String getColumnCaption(String columnName, int columnIndex) {
        return getReportDefinition().getColumns().get(columnIndex).getCaption();
    }

    default QueryBuilder<T> customizeQueryBuilder(QueryBuilder<T> queryBuilder) {
        return queryBuilder;
    }

    default ObjectIterator<T> customizeList(ObjectIterator<T> objectList) {
        return objectList;
    }

    default void customizeTableHeader(TableHeader tableHeader) {
    }

    default int getCharCount(Object object) {
        return 10;
    }

    default ReportDefinition getReportDefinition(JSON json) {
        String definition = json.getString("definition");
        ReportDefinition reportDefinition = null;
        if(definition != null) {
            reportDefinition = rd(definition);
            if(reportDefinition == null) {
                setErrorMessage("Definition not found: " + definition);
            }
            return reportDefinition;
        }
        try {
            @SuppressWarnings("unchecked") Class<T> dataClass = (Class<T>) json.getDataClass("className");
            reportDefinition = ReportDefinition.create(dataClass, json.getStringList("attributes"));
            Boolean any = json.getBoolean("any");
            if(any != null && any) {
                reportDefinition.setIncludeSubclasses(true);
            }
            setExtraCondition(json.getString("extraCondition"));
        } catch (Exception e) {
            setErrorMessage(e.getMessage());
        }
        return reportDefinition;
    }

    static ReportDefinition rd(String rd) {
        if(rd.startsWith("Id:")) {
            return StoredObject.get(ReportDefinition.class, "Id=" + rd.substring(3), true);
        }
        if(rd.contains(".")) {
            boolean any = rd.toLowerCase().endsWith("/any");
            if(any) {
                rd = rd.substring(0, rd.length() - 4);
            }
            if(JavaClassLoader.exists(rd)) {
                try {
                    Class<?> dClass = JavaClassLoader.getLogic(rd);
                    if(StoredObject.class.isAssignableFrom(dClass)) {
                        @SuppressWarnings("unchecked")
                        ReportDefinition rDef = ReportDefinition.create((Class<? extends StoredObject>) dClass);
                        if(any) {
                            rDef.setIncludeSubclasses(true);
                        }
                        return rDef;
                    }
                } catch(ClassNotFoundException ignored) {
                }
            }
        }
        return StoredObject.get(ReportDefinition.class, "lower(Name)='" + rd.toLowerCase().trim() + "'",
                true);
    }
}
