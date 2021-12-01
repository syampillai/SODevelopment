package com.storedobject.core;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * API for an "editable list".
 *
 * @param <T> Type of items in the list.
 * @author Syam
 */
public interface EditableList<T> {

    /**
     * Check whether the given item is in this list or not. (Note: The parameter is generic {@link Object} type to make
     * it compatible with Java's {@link java.util.List} interface).
     *
     * @param item Item to check.
     * @return True/false.
     */
    boolean contains(Object item);

    /**
     * Check if the given item was added to the list or not.
     *
     * @param item Item to check.
     * @return True/false.
     */
    boolean isAdded(T item);

    /**
     * Check if the given item was deleted from the list or not.
     *
     * @param item Item to check.
     * @return True/false.
     */
    boolean isDeleted(T item);

    /**
     * Check if the given item was edited or not.
     *
     * @param item Item to check.
     * @return True/false.
     */
    boolean isEdited(T item);

    /**
     * Get a stream of all items (including original, added, deleted and edited items).
     *
     * @return Stream of items.
     */
    Stream<T> streamAll();

    /**
     * Get a stream of all items remaining (after removing deleted items).
     *
     * @return Stream of items.
     */
    default Stream<T> stream() {
        return streamAll().filter(item -> !isDeleted(item));
    }

    /**
     * Get a stream of all newly added items.
     *
     * @return Stream of items.
     */
    default Stream<T> streamAdded() {
        return streamAll().filter(this::isAdded);
    }

    /**
     * Get a stream of all modified items.
     *
     * @return Stream of items.
     */
    default Stream<T> streamEdited() {
        return streamAll().filter(this::isEdited);
    }

    /**
     * Get a stream of all deleted items.
     *
     * @return Stream of items.
     */
    default Stream<T> streamDeleted() {
        return streamAll().filter(this::isDeleted);
    }

    /**
     * Size of the list (including all items).
     *
     * @return Size.
     */
    int size();

    /**
     * Append an item to the list. This is not considered as a new item.
     *
     * @param item Item to append.
     * @return True if appended, otherwise, false.
     */
    boolean append(T item);

    /**
     * Append items to the list. This is not considered as new items.
     *
     * @param items Items to append.
     */
    default void append(Iterable<T> items) {
        items.forEach(this::append);
    }

    /**
     * Aad a new item to the list. (Note: This doesn't return a result to make it compatible with Java's
     * {@link java.util.List} interface).
     *
     * @param index Index at which the item needs to be inserted.
     * @param item Item to add.
     */
    default void add(int index, T item) {
        add(item);
    }

    /**
     * Aad a new item to the list.
     *
     * @param item Item to add.
     * @return True if added, otherwise, false.
     */
    boolean add(T item);

    /**
     * Delete an item from the list. (If the item was newly added, it will be removed).
     *
     * @param item Item to delete.
     * @return True if deleted, otherwise, false.
     */
    boolean delete(T item);

    /**
     * Undelete an item that was deleted earlier.
     *
     * @param item Item to undelete.
     * @return True if undeleted, otherwise, false.
     */
    boolean undelete(T item);

    /**
     * Update an item. Item will be marked as modified.
     *
     * @param item Updated item.
     * @return True if updated, otherwise, false.
     */
    boolean update(T item);

    /**
     * Is save pending? (Some entry has "edited" or "deleted" status).
     *
     * @return True/false.
     */
    default boolean isSavePending() {
        return streamAll().anyMatch(o -> isAdded(o) || isEdited(o) || isDeleted(o));
    }

    /**
     * Get a duplicate entry. A function to extract the duplicate value must be passed as the parameter.
     * <p>E.g., from a list of persons, we want to find out a an entry with duplicate "First Name" (to be compared
     * ignoring case):</p>
     * <p>
     * <code>
     *     getDuplicate(p -> p.getFirstName().toUpperCase());
     * </code>
     * </p><p>Note: <code>null</code> values are ignored.</p>
     *
     * @param value Function to extract the duplicate value.
     * @param <R> Type of the duplicate value.
     * @return Duplicate entry if found, otherwise <code>null</code>.
     */
    default <R> T getDuplicate(Function<T, R> value) {
        if(value == null) {
            return null;
        }
        Set<R> valuesSet = new HashSet<>();
        return stream().filter(item -> {
            R v = value.apply(item);
            return v != null && !valuesSet.add(v);
        }).findAny().orElse(null);
    }
}