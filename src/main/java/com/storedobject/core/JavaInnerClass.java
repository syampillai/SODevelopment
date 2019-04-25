package com.storedobject.core;

public class JavaInnerClass extends com.storedobject.core.StoredObject {

    protected java.lang.String name;

    protected JavaInnerClass(java.lang.String p1) {
        this();
    }

    public JavaInnerClass() {
    }

    public java.lang.String getName() {
        return null;
    }

    public static boolean checkName(java.lang.String p1) {
        return false;
    }

    public void setName(java.lang.String p1) {
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

    protected Filer getFiler(java.lang.String p1) {
        return null;
    }

    public java.io.InputStream getClassStream() {
        return null;
    }

    public com.storedobject.core.Id getClassDataId() {
        return null;
    }

    public void setClassData(java.math.BigDecimal p1) {
    }

    public com.storedobject.core.StreamData getClassData() {
        return null;
    }

    class Filer implements com.storedobject.core.StreamDataProvider {

        protected Filer(com.storedobject.core.JavaInnerClass p1, java.lang.String p2) {
            this();
        }

        private Filer() {
        }

        public java.io.InputStream getStream(com.storedobject.core.StreamData p1) throws com.storedobject.core.Data_Not_Changed, java.lang.Exception {
            return null;
        }

        public void writeStream(com.storedobject.core.StreamData p1, java.io.OutputStream p2) throws java.lang.Exception {
        }

        public java.io.BufferedWriter getFileWriter() throws java.lang.Exception {
            return null;
        }
    }
}
