package com.storedobject.ui.util;

import java.util.List;
import java.util.function.Consumer;

public interface ChildVisitor<P extends T, T> {

    List<P> listRoots();

    void visitChildren(T parent, Consumer<T> consumer, boolean includeGrandChildren);
}
