package com.storedobject.core;

import java.math.BigDecimal;
import java.sql.Date;

import com.storedobject.report.PrintAdditionalDocument;

public class AdditionalDocument extends StoredObject {

    public AdditionalDocument() {
    }

    public static void columns(Columns columns) {
    }

    public String getUniqueCondition() {
        return null;
    }

    public void setDefinition(Id definitionId) {
    }

    public void setDefinition(BigDecimal idValue) {
    }

    public void setDefinition(AdditionalDocumentDefinition definition) {
    }

    public Id getDefinitionId() {
        return null;
    }

    public AdditionalDocumentDefinition getDefinition() {
        return null;
    }

    public void setReferenceNumber(String referenceNumber) {
    }

    public String getReferenceNumber() {
        return null;
    }

    public void setDate(Date date) {
    }

    public Date getDate() {
        return null;
    }

    public void setEntity(Id entityId) {
    }

    public void setEntity(BigDecimal idValue) {
    }

    public void setEntity(StoredObject entity) {
    }

    public Id getEntityId() {
        return null;
    }

    public StoredObject getEntity() {
        return null;
    }

	public void print(Device device) throws Exception {
	}
	
	public PrintAdditionalDocument getPrint(Device device) throws Exception {
		return null;
	}
	
	public void setPrintValues(PrintAdditionalDocument logic) throws Exception {
	}
}