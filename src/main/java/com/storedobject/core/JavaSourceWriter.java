package com.storedobject.core;

import java.io.FilterWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

public class JavaSourceWriter extends FilterWriter {

    public JavaSourceWriter(Writer out) {
        this(out, null);
    }

    public JavaSourceWriter(Writer out, String className) {
        super(new StringWriter());
    }

    public void format() throws IOException {
    }
}