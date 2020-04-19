package com.storedobject.core;

/**
 * Interface to define whether a class has a related {@link Logic} or not. This should be implemented by the executable class
 * defined in the {@link Logic} so that approvals can be carried out when the logic is executed and transactions are generated.
 *
 * @author Syam
 */
public interface HasLogic {

    /**
     * Set the logic. This will be invoked by the {@link ApplicationServer} when the {@link Logic} is executed.
     *
     * @param logic Logic to set
     */
    void setLogic(Logic logic);

    /**
     * Get the logic.
     *
     * @return Logic
     */
    Logic getLogic();
}
