package com.storedobject.core;

import java.math.BigDecimal;

import com.storedobject.core.Columns;
import com.storedobject.core.Entity;
import com.storedobject.core.Id;
import com.storedobject.core.Indices;
import com.storedobject.core.StoredObject;
import com.storedobject.core.SystemEntity;
import com.storedobject.core.TransactionManager;
import com.storedobject.core.annotation.Column;

public class ReportFormat extends StoredObject {

    public ReportFormat() {
    }

    public static void columns(Columns columns) {
    }

    public static void indices(Indices indices) {
    }

    public String getUniqueCondition() {
    	return null;
    }

    public void setEntity(Id entityId) {
    }

    public void setEntity(BigDecimal idValue) {
    }

    public void setEntity(SystemEntity entity) {
    }

    @Column(required = false, order = 1)
    public Id getEntityId() {
        return null;
    }

    public SystemEntity getEntity() {
        return null;
    }

    public void setDefaultPaperSize(int defaultPaperSize) {
    }

    @Column(order = 2)
    public int getDefaultPaperSize() {
        return 0;
    }

    public static String[] getDefaultPaperSizeValues() {
        return null;
    }

    public static String getDefaultPaperSizeValue(int value) {
        return null;
    }

    public String getDefaultPaperSizeValue() {
        return null;
    }

    public void setLogoPosition(int logoPosition) {
    }

    @Column(order = 3)
    public int getLogoPosition() {
        return 0;
    }

    public static String[] getLogoPositionValues() {
        return null;
    }

    public static String getLogoPositionValue(int value) {
        return null;
    }

    public String getLogoPositionValue() {
        return null;
    }

    public void validateData() throws Exception {
    }
    
    public static ReportFormat get(Entity entity) {
    	return null;
    }
    
    public static ReportFormat get(SystemEntity entity) {
    	return null;
    }
    
    public static ReportFormat get(TransactionManager tm) {
    	return null;
    }
}
