package com.storedobject.core;

import java.util.Comparator;
import java.util.function.Predicate;
import java.util.stream.Stream;

public interface Filtered<T> {

    void order(Comparator<? super T> comparator);

    void filter(Predicate<? super T> filter);

    void filter(Predicate<? super T> filter, Comparator<? super T> comparator);

    Predicate<? super T> getFilter();

    Comparator<? super T> getComparator();

    int size();

    int size(int startingIndex, int endingIndex);

    int sizeAll();

    Stream<T> stream(int startingIndex, int endingIndex);

    Stream<T> streamAll(int startingIndex, int endingIndex);
}
