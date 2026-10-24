package com.storedobject.office;

import com.storedobject.core.Formula;
import com.storedobject.core.StoredObject;

/**
 * ObjectFiller is a specialized implementation of the Filler class
 * that is designed to fill or populate values from a StoredObject instance.
 * <p></p>
 * The class extends the generic Filler class, providing functionality
 * tailored to objects of type StoredObject.
 *
 * @author Syam
 */
public class ObjectFiller extends Filler<StoredObject> {

    /**
     * Default constructor for the ObjectFiller class.
     * This constructor initializes an instance of ObjectFiller with no predefined formula.
     * It is typically used when no specific formula configuration is required.
     */
    public ObjectFiller() {
    }

    /**
     * Constructs an ObjectFiller instance with a specified formula.
     * The formula is used to set up a Formula object that determines how attributes are derived.
     *
     * @param formula the formula expression used to initialize the Formula object
     */
    public ObjectFiller(String formula) {
        setFormula(new Formula(formula));
    }
}
