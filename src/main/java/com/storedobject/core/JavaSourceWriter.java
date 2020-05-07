package com.storedobject.core;

import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;

public class JavaSourceWriter extends FilterWriter {

    public JavaSourceWriter(Writer out) {
        super(out);
    }

    public void format() throws IOException {
    }
}