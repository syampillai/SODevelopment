package com.storedobject.ui;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class RightClickButton<T> implements Consumer<T>, Predicate<T> {

    private String label;
    private Consumer<T> consumer;
    private Predicate<T> predicate;

    public RightClickButton(String label) {
        this(label, null, null);
    }

    public RightClickButton(String label, Consumer<T> consumer, Predicate<T> predicate) {
        this.label = label;
        this.consumer = consumer;
        this.predicate = predicate;
    }

    public void setConsumer(Consumer<T> consumer) {
        this.consumer = consumer;
    }

    public void setPredicate(Predicate<T> predicate) {
        this.predicate = predicate;
    }

    @Override
    public void accept(T t) {
        if(consumer != null) {
            consumer.accept(t);
        }
    }

    @Override
    public boolean test(T t) {
        return predicate == null || predicate.test(t);
    }

    public final String getLabel() {
        return label == null ? "" : label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
