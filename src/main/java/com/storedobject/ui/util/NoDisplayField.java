package com.storedobject.ui.util;

/**
 * To indicate whether a field non-displayable ore not. Some fields may be embedded in unusual
 * places within the layout and may not behave like normal data fields.
 *
 * @author Syam
 */
@FunctionalInterface
public interface NoDisplayField {

    /**
     * Can this field display information like normal fields?
     *
     * @return True/false.
     */
    boolean canDisplay();
}
