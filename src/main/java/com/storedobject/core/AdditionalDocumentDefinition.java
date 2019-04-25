package com.storedobject.core;

import java.math.BigDecimal;

import com.storedobject.report.PrintAdditionalDocument;

public class AdditionalDocumentDefinition extends StoredObject {

    public AdditionalDocumentDefinition() {
    }

    public static void columns(Columns columns) {
    }

    public String getUniqueCondition() {
        return null;
    }

    public static AdditionalDocumentDefinition get(String name) {
        return null;
    }

    public static ObjectIterator < AdditionalDocumentDefinition > list(String name) {
        return null;
    }

    public static String[] displayColumns() {
        return null;
    }

    public void setCode(int code) {
    }

    public int getCode() {
        return 0;
    }

    public void setName(String name) {
    }

    public String getName() {
        return null;
    }

    public void setFormat(Id formatId) {
    }

    public void setFormat(BigDecimal idValue) {
    }

    public void setFormat(StreamData format) {
    }

    public Id getFormatId() {
        return null;
    }

    public StreamData getFormat() {
        return null;
    }

    public void setDocumentClassName(String documentClassName) {
    }

    public String getDocumentClassName() {
        return null;
    }

    public void setDocumentName(String documentName) {
    }

    public String getDocumentName() {
        return null;
    }

    public void setIncludeChildDocumentClasses(boolean includeChildDocumentClasses) {
    }

    public boolean getIncludeChildDocumentClasses() {
        return false;
    }

    public void setAllowMultipleDocuments(boolean allowMultipleDocuments) {
    }

    public boolean getAllowMultipleDocuments() {
        return false;
    }

    public void setUniquePerDocument(boolean uniquePerDocument) {
    }

    public boolean getUniquePerDocument() {
        return false;
    }

    public void setNumberBasedDocument(boolean numberBasedDocument) {
    }

    public boolean getNumberBasedDocument() {
        return false;
    }

    public void setEntityFieldName(String entityFieldName) {
    }

    public String getEntityFieldName() {
        return null;
    }

    public void setEntityName(String entityName) {
    }

    public String getEntityName() {
        return null;
    }

    public void setLogicClassName(String logicClassName) {
    }

    public String getLogicClassName() {
        return null;
    }
    
	public PrintAdditionalDocument getPrint(Device device) throws Exception {
        return null;
    }

	public static PrintAdditionalDocument getPrint(Device device, String definitionName) throws Exception {
		return null;
	}
}