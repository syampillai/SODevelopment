package com.storedobject.office.od.ods;

import com.storedobject.office.PDFProperties;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.formula.EvaluationWorkbook;
import org.apache.poi.ss.formula.udf.UDFFinder;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;

public class Workbook implements org.apache.poi.ss.usermodel.Workbook {

    public Workbook() throws Exception {
        this(null);
    }

    public Workbook(InputStream in) throws Exception {
    }

    public final void setPDFProperties(PDFProperties pdfProperties) {
    }

    public final PDFProperties getPDFProperties() {
        return null;
    }

    @Override
    public Sheet getSheet(String name) {
        return null;
    }

    @Override
    public Sheet getSheetAt(int index) {
        return null;
    }

    @Override
    public void removeSheetAt(int index) {
    }

    @Override
    public Font createFont() {
        return null;
    }

    @Override
    public Font findFont(boolean b, short i, short i1, String s, boolean b1, boolean b2, short i2, byte b3) {
        return null;
    }

    @Override
    public int getNumberOfFonts() {
        return 0;
    }

    @Override
    public int getNumberOfFontsAsInt() {
        return getNumberOfFonts();
    }

    @Override
    public Font getFontAt(int index) {
        return getFontAt((short)index);
    }

    @Override
    public CellStyle createCellStyle() {
        return null;
    }

    @Override
    public int getNumCellStyles() {
        return 0;
    }

    @Override
    public CellStyle getCellStyleAt(int index) {
        return null;
    }

    @Override
    public void close() {
    }

    @Override
    public int getNumberOfNames() {
        return 0;
    }

    @Override
    public Name getName(String s) {
        return null;
    }

    @Override
    public List<? extends Name> getNames(String s) {
        return null;
    }

    @Override
    public List<? extends Name> getAllNames() {
        return null;
    }

    @Override
    public Name createName() {
        return null;
    }

    @Override
    public void removeName(Name name) {

    }

    @Override
    public int linkExternalWorkbook(String s, org.apache.poi.ss.usermodel.Workbook workbook) {
        return 0;
    }

    @Override
    public void setPrintArea(int index, String area) {

    }

    @Override
    public void setPrintArea(int i, int i1, int i2, int i3, int i4) {

    }

    @Override
    public String getPrintArea(int i) {
        return null;
    }

    @Override
    public void removePrintArea(int i) {

    }

    @Override
    public Row.MissingCellPolicy getMissingCellPolicy() {
        return null;
    }

    @Override
    public void setMissingCellPolicy(Row.MissingCellPolicy missingCellPolicy) {
    }

    @Override
    public DataFormat createDataFormat() {
        return null;
    }

    @Override
    public int addPicture(byte[] bytes, int i) {
        return 0;
    }

    @Override
    public List<? extends PictureData> getAllPictures() {
        return null;
    }

    @Override
    public CreationHelper getCreationHelper() {
        return null;
    }

    @Override
    public boolean isHidden() {
        return false;
    }

    @Override
    public void setHidden(boolean b) {

    }

    @Override
    public boolean isSheetHidden(int i) {
        return false;
    }

    @Override
    public boolean isSheetVeryHidden(int i) {
        return false;
    }

    @Override
    public void setSheetHidden(int i, boolean b) {

    }

    @Override
    public SheetVisibility getSheetVisibility(int index) {
        return null;
    }

    @Override
    public void setSheetVisibility(int index, SheetVisibility sheetVisibility) {

    }

    @Override
    public void addToolPack(UDFFinder udfFinder) {

    }

    @Override
    public void setForceFormulaRecalculation(boolean b) {

    }

    @Override
    public boolean getForceFormulaRecalculation() {
        return false;
    }

    @Override
    public SpreadsheetVersion getSpreadsheetVersion() {
        return null;
    }

    @Override
    public int addOlePackage(byte[] bytes, String s, String s1, String s2) {
        return 0;
    }

    @Override
    public EvaluationWorkbook createEvaluationWorkbook() {
        return null;
    }

    @Override
    public int getActiveSheetIndex() {
        return 0;
    }

    @Override
    public void setActiveSheet(int i) {

    }

    @Override
    public int getFirstVisibleTab() {
        return 0;
    }

    @Override
    public void setFirstVisibleTab(int i) {

    }

    @Override
    public void setSheetOrder(String s, int i) {

    }

    @Override
    public void setSelectedTab(int i) {

    }

    @Override
    public void setSheetName(int i, String s) {

    }

    @Override
    public String getSheetName(int index) {
        return null;
    }

    @Override
    public int getSheetIndex(String name) {
        return -1;
    }

    @Override
    public int getSheetIndex(Sheet sheet) {
        return getSheetIndex(sheet.getSheetName());
    }

    @Override
    public Sheet cloneSheet(int index) {
        return null;
    }

    @Override
    public Iterator<Sheet> sheetIterator() {
        return iterator();
    }

    @Override
    public int getNumberOfSheets() {
        return 0;
    }

    @Override
    public Sheet createSheet() {
        return null;
    }

    @Override
    public Sheet createSheet(String name) {
        return null;
    }

    @Override
    public void write(OutputStream out) throws IOException {
    }

    @Override
    public Iterator<Sheet> iterator() {
        return null;
    }

    public void reload() {
    }
}