package com.storedobject.core;

public class JavaPackage extends com.storedobject.core.StoredObject implements com.storedobject.core.StreamDataProvider {

    public JavaPackage(java.lang.String p1) {
        this();
    }

    public JavaPackage() {
    }

    public static com.storedobject.core.JavaPackage get(java.lang.String p1) {
        return null;
    }

    public java.lang.String getName() {
        return null;
    }

    public static java.lang.Package getPackage(java.lang.String p1) {
        return null;
    }

    public static java.lang.Package getPackage(java.lang.Class <?> p1) {
        return null;
    }

    public void setName(java.lang.String p1) {
    }

    public long getSize() {
        return 0;
    }

    public void setSize(long p1) {
    }

    public static java.lang.String getJarPath() {
        return null;
    }

    public static void setJarPath(java.lang.String p1) {
    }

    public static void columns(com.storedobject.core.Columns p1) {
    }

    public static void indices(com.storedobject.core.Indices p1) {
    }

    public void validate() throws java.lang.Exception {
    }

    public java.lang.String getUniqueCondition() {
        return null;
    }

    public java.io.InputStream getStream(com.storedobject.core.StreamData p1) throws com.storedobject.core.Data_Not_Changed, java.lang.Exception {
        return null;
    }

    public void writeStream(com.storedobject.core.StreamData p1, java.io.OutputStream p2) throws java.lang.Exception {
    }

    public static boolean downloadAllPackages() throws java.lang.Exception {
        return false;
    }

    public static boolean checkAllPackages() throws java.lang.Exception {
        return false;
    }

    public java.lang.String getJarName() {
        return null;
    }

    public void setJarName(java.lang.String p1) {
    }

    public int getBuild() {
        return 0;
    }

    public void setBuild(int p1) {
    }

    public com.storedobject.core.Id getJarDataId() {
        return null;
    }

    public void setJarData(java.math.BigDecimal p1) {
    }

    public com.storedobject.core.StreamData getJarData() {
        return null;
    }

    public java.io.InputStream getJarStream() {
        return null;
    }

    public boolean downloadPackage() throws java.lang.Exception {
        return false;
    }

    public boolean checkPackage() throws java.lang.Exception {
        return false;
    }

    public boolean uploadPackage() throws java.lang.Exception {
        return false;
    }

    public static boolean uploadAllPackages(com.storedobject.core.Transaction p1) throws java.lang.Exception {
        return false;
    }
}
