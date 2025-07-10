package com.storedobject.ui.util;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * The ChildVisitor interface allows for traversing and processing child elements
 * rooted in a hierarchical structure. It defines methods for obtaining root elements
 * and visiting child elements with specified operations.
 *
 * @param <P> Type representing the parent objects in the structure.
 * @param <T> Type representing all elements (both parents and children) in the structure.
 */
public interface ChildVisitor<P extends T, T> {

    /**
     * Streams the root elements of the hierarchical structure.
     *
     * @return a Stream of elements of type P representing the root elements in the structure.
     */
    default Stream<P> streamRoots() {
        return listRoots().stream();
    }

    /**
     * Produces a stream of child elements for the given parent element.
     * This allows traversal or processing of direct child elements in a hierarchical structure.
     *
     * @param parent the parent element whose children are to be streamed
     * @return a stream of child elements directly associated with the specified parent
     */
    default Stream<T> streamChildren(T parent) {
        return listChildren(parent).stream();
    }

    /**
     * Retrieves a list of root elements in the hierarchical structure.
     * This method provides a collection of elements that serve as entry points
     * or origins in the structure.
     *
     * @return a List of elements of type P representing the root elements
     */
    default List<P> listRoots() {
        return streamRoots().toList();
    }

    /**
     * Retrieves a list of child elements directly associated with the specified parent
     * in a hierarchical structure.
     *
     * @param parent the parent element whose children are to be listed
     * @return a list of child elements of type T associated with the given parent
     */
    default List<T> listChildren(T parent) {
        return streamChildren(parent).toList();
    }

    /**
     * Visits all elements in the structure starting from the root elements, applying the specified action to each
     * element encountered. This method traverses the entire hierarchy of elements by visiting each parent node
     * and recursively visiting its children.
     *
     * @param consumer the action to be performed on each element encountered during the traversal
     */
    default void visitAll(Consumer<T> consumer) {
        visitAll(consumer, p -> true);
    }

    /**
     * Visits all elements in the hierarchical structure starting from the root elements,
     * applying the specified action to each element encountered. The traversal continues
     * only if the specified {@code canContinue} predicate evaluates to {@code true}.
     *
     * @param consumer     a {@link Consumer} that specifies the operation to be performed
     *                     on each element encountered during the traversal
     * @param canContinue  a {@link Predicate} that determines whether the traversal should
     *                     continue. If the predicate evaluates to {@code false}, the traversal stops
     */
    default void visitAll(Consumer<T> consumer, Predicate<Void> canContinue) {
        if(canContinue.test(null)) {
            Stream<T> list = streamRoots().map(p -> p);
            list.forEach(t -> {
                if(canContinue.test(null)) {
                    consumer.accept(t);
                    visitChildren(t, consumer, true);
                }
            });
        }
    }

    /**
     * Traverses through the children of the given parent element, applying the specified
     * operation to each child. Optionally, it also processes the grandchildren and
     * later descendants recursively, if specified.
     *
     * @param parent The parent element whose children are to be visited and processed.
     * @param consumer A {@link Consumer} that specifies the operation
     *                 to be performed on each child element.
     * @param includeGrandChildren A boolean flag indicating whether the operation should
     *                             also be applied to grandchildren and subsequent descendants.
     */
    default void visitChildren(T parent, Consumer<T> consumer, boolean includeGrandChildren) {
        visitChildren(parent, consumer, includeGrandChildren, p -> true);
    }

    /**
     * Traverses through the children of the given parent element, applying the specified operation
     * to each child. Optionally, it also processes the grandchildren and later descendants recursively,
     * if specified. The traversal continues only if the specified {@code canContinue} predicate evaluates to {@code true}.
     *
     * @param parent The parent element whose children are to be visited and processed.
     * @param consumer A {@link Consumer} that specifies the operation to be performed on each child element.
     * @param includeGrandChildren A boolean flag indicating whether the operation should also
     *                             be applied to grandchildren and subsequent descendants.
     * @param canContinue A {@link Predicate} that determines whether the traversal should continue.
     *                    If the predicate evaluates to {@code false}, the traversal stops.
     */
    default void visitChildren(T parent, Consumer<T> consumer, boolean includeGrandChildren, Predicate<Void> canContinue) {
        if(canContinue.test(null)) {
            streamChildren(parent).forEach(c -> {
                if(canContinue.test(null)) {
                    consumer.accept(c);
                    if (includeGrandChildren) {
                        visitChildren(c, consumer, true);
                    }
                }
            });
        }
    }

    /**
     * Visits all elements in a hierarchical structure starting from the root elements,
     * applying the specified action to each element encountered. The method provides
     * the element and its path in the structure (represented as an array of indices)
     * to the specified {@link BiConsumer}.
     *
     * @param consumer a {@link BiConsumer} that specifies the action to be performed on
     *                 each element, taking the element of type {@code T} and its path
     *                 (an array of integers) in the hierarchy as arguments
     */
    default void visitAll(BiConsumer<T, int[]> consumer) {
        visitAll(consumer, p -> true);
    }

    /**
     * Traverses all elements in a hierarchical structure, starting from the root elements,
     * and applies the specified action to each element encountered. The method provides
     * each element and its path (represented as an array of indices) to the given {@link BiConsumer}.
     * The traversal continues only if the specified {@code canContinue} predicate evaluates to {@code true}.
     *
     * @param consumer a {@link BiConsumer} that defines the action to be performed on each element,
     *                 taking the element of type {@code T} and its corresponding path in the hierarchy (as an array of integers) as arguments
     * @param canContinue a {@link Predicate} that determines whether the traversal should continue.
     *                    If the predicate evaluates to {@code false}, the traversal stops
     */
    default void visitAll(BiConsumer<T, int[]> consumer, Predicate<Void> canContinue) {
        if(canContinue.test(null)) {
            Stream<T> list = streamRoots().map(p -> p);
            AtomicInteger index = new AtomicInteger(0);
            list.forEach(t -> {
                if(canContinue.test(null)) {
                    int[] path = new int[]{index.getAndIncrement()};
                    consumer.accept(t, path);
                    visitChildren(t, path, consumer, true);
                }
            });
        }
    }

    /**
     * Traverses through the children of the given parent element, applying the specified operation
     * to each child along with the corresponding path in the hierarchy. Optionally, it also processes
     * the grandchildren and later descendants recursively, if specified.
     *
     * @param parent The parent element whose children are to be visited and processed.
     * @param parentPath An array representing the path of indices leading to the current parent
     *                   within the hierarchical structure.
     * @param consumer A {@link BiConsumer} that specifies the operation to be performed on
     *                 each child element and their corresponding path.
     * @param includeGrandChildren A boolean flag indicating whether the operation should also
     *                             be applied to grandchildren and subsequent descendants.
     */
    default void visitChildren(T parent, int[] parentPath, BiConsumer<T, int[]> consumer, boolean includeGrandChildren) {
        visitChildren(parent, parentPath, consumer, includeGrandChildren, p -> true);
    }

    /**
     * Traverses the children of the given parent element, applying the specified operation
     * to each child along with its corresponding path in the hierarchy. Optionally, it also
     * processes the grandchildren and later descendants recursively, if specified. The traversal
     * continues only if the specified {@code canContinue} predicate evaluates to {@code true}.
     *
     * @param parent The parent element whose children are to be visited and processed.
     * @param parentPath An array representing the path of indices leading to the current parent
     *                   within the hierarchical structure.
     * @param consumer A {@link BiConsumer} that specifies the operation to be performed on
     *                 each child element and their corresponding path.
     * @param includeGrandChildren A boolean flag indicating whether the operation should also
     *                             be applied to grandchildren and subsequent descendants.
     * @param canContinue A {@link Predicate} that determines whether the traversal should
     *                    continue. If the predicate evaluates to {@code false}, the traversal stops.
     */
    default void visitChildren(T parent, int[] parentPath, BiConsumer<T, int[]> consumer, boolean includeGrandChildren,
                               Predicate<Void> canContinue) {
        if(canContinue.test(null)) {
            AtomicInteger index = new AtomicInteger(0);
            streamChildren(parent).forEach(c -> {
                if(canContinue.test(null)) {
                    int[] path = path(parentPath, index.getAndIncrement());
                    consumer.accept(c, path);
                    if (includeGrandChildren) {
                        visitChildren(c, path, consumer, true);
                    }
                }
            });
        }
    }

    private static int[] path(int[] path, int index) {
        int[] newPath = new int[path.length + 1];
        System.arraycopy(path, 0, newPath, 0, path.length);
        newPath[path.length] = index;
        return newPath;
    }
}
