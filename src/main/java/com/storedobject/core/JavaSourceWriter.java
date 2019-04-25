package com.storedobject.core;

public class JavaSourceWriter extends java.io.FilterWriter {

    private enum LineType {
        CODE,
        COMMENT,
        SLASHCOMMENT,
        STRING,
        CHAR,
        NEWLINE,
    }

    public JavaSourceWriter(java.io.Writer p1) {
        this();
    }

    private JavaSourceWriter() {
        super((java.io.Writer) null);
    }

    public static void main(java.lang.String[] p1) {
    }

    public void write(int p1) throws java.io.IOException {
    }

    public void write(char[] p1, int p2, int p3) throws java.io.IOException {
    }

    public void write(java.lang.String p1, int p2, int p3) throws java.io.IOException {
    }

    public void write(java.lang.String p1) throws java.io.IOException {
    }

    public void close() throws java.io.IOException {
    }

    public void flush() throws java.io.IOException {
    }

    class Line {

        public LineType type;
        public java.lang.StringBuilder line;

        public Line(com.storedobject.core.JavaSourceWriter p1, com.storedobject.core.JavaSourceWriter.LineType p2, java.lang.StringBuilder p3) {
            this();
        }

        private Line() {
        }
    }
}
