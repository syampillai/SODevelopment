package com.storedobject.tools;

import java.io.InputStream;
import java.io.Writer;
import java.util.ArrayList;

import com.storedobject.common.StyledBuilder;
import com.storedobject.core.Columns;
import com.storedobject.core.JavaClass;
import com.storedobject.core.ObjectIterator;
import com.storedobject.core.StoredObject;
import com.storedobject.core.TransactionManager;

public class TableDefinition extends StoredObject {

	public TableDefinition() {
	}

	public static void columns(Columns columns) {
	}

    public static void compareDefinitions(TransactionManager transactionManager, InputStream data, StyledBuilder view) {
    }

	public static void loadDefinitions(TransactionManager transactionManager, InputStream data, StyledBuilder view) {
	}

	public boolean isCorrectionNeeded() {
		return false;
	}

	public boolean generateInterfaceCode() {
		return true;
	}

	public void setClassName(String className) {
	}

	public String getClassName() {
		return null;
	}

	public void setParentClassName(String parentClassName) {
	}

	public String getParentClassName() {
		return null;
	}

	public void setAbstractClass(boolean abstractClass) {
	}

	public boolean getAbstractClass() {
		return false;
	}

	public String getParentTableName() {
		return null;
	}

	public String getTableName(TransactionManager tm) {
		return null;
	}

	public String getSchemaName(TransactionManager tm) {
		return null;
	}

	public void setFormTitle(String formTitle) {
	}

	public String getFormTitle() {
		return null;
	}

	public void setFormStyle(int formStyle) {
	}

	public int getFormStyle() {
		return 0;
	}

	public static String[] getFormStyleValues() {
		return null;
	}

	public static String getFormStyleValue(int formStyle) {
		return null;
	}

	public String getFormStyleValue() {
		return null;
	}

	public boolean compile() {
		return true;
	}

	public void generateJavaCode(Writer writer) throws Exception {
	}

	public void generateJavaCode(Writer writer, boolean comment) throws Exception {
	}
	
	public boolean isCoreType(String type) {
		return true;
	}
	
	public void generateJavaCode(Writer writer, boolean comment, boolean close) throws Exception {
	}

	public void setExtraImports(String extraImports) {
	}

	public String getExtraImports() {
		return null;
	}

	public void setExtraConstructors(String extraConstructors) {
	}

	public String getExtraConstructors() {
		return null;
	}

	public void setExtraMethods(String extraMethods) {
	}

	public String getExtraMethods() {
		return null;
	}

	public void setConstructorBlock(String constructorBlock) {
	}

	public String getConstructorBlock() {
		return null;
	}

	public void setValidateBlock(String validateBlock) {
	}

	public String getValidateBlock() {
		return null;
	}

	public void setInterfaces(String interfaces) {
	}

	public String getInterfaces() {
		return null;
	}

	public void setVariablesBlock(String variablesBlock) {
	}

	public String getVariablesBlock() {
		return null;
	}

	public void setDisplayColumns(String displayColumns) {
	}

	public String getDisplayColumns() {
		return null;
	}

	public void setSearchColumns(String searchColumns) {
	}

	public String getSearchColumns() {
		return null;
	}

	public void setBrowseColumns(String browseColumns) {
	}

	public String getBrowseColumns() {
		return null;
	}

	public void setProtectedColumns(String protectedColumns) {
	}

	public String getProtectedColumns() {
		return null;
	}
    
	public void setAnchorColumns(String anchorColumns) {
	}

	public String getAnchorColumns() {
		return null;
	}

	public void setSmall(boolean small) {
	}

	public boolean getSmall() {
		return false;
	}

	public void setSmallList(boolean smallList) {
	}

	public boolean getSmallList() {
		return false;
	}

	public String getNotes() {
		return null;
	}

	public void setNotes(String notes) {
	}

	public static TableDefinition get(String className) {
		return null;
	}
	
	public static ObjectIterator<TableDefinition> list(String className) {
		return null;
	}

	public ArrayList<String> alterTable(TransactionManager tm) throws Exception {
		return null;
	}

	public boolean classChanged() throws Exception {
		return false;
	}

	public boolean classChanged(JavaClass jc) throws Exception {
		return false;
	}

	public boolean deploy(TransactionManager tm, String securityPassword) throws Exception {
		return false;
	}

	public boolean deploy(TransactionManager tm, String securityPassword, boolean checkOldLoaded) throws Exception {
		return false;
	}

	public boolean deployLogic(TransactionManager tm) throws Exception {
		return false;
	}
	
	public boolean deployLogic(TransactionManager tm, boolean checkOldLoaded) throws Exception {
		return false;
	}

	public boolean deployTable(TransactionManager tm, String securityPassword) throws Exception {
		return false;
	}

	public boolean hasDetailInterface() {
		return false;
	}

	public boolean isMasterObject() {
		return false;
	}
}
